package com.nickole.earworld.clipboard;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nickole.earworld.R;
import com.nickole.earworld.entity.TextInfo;
import com.nickole.earworld.main.MainApplication;
import com.nickole.earworld.transform.ISynthesisListener;
import com.nickole.earworld.transform.VoiceSynthesisTask;
import com.nickole.earworld.util.TextUtil;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author shuzijie
 * @date 2019-05-24.
 */
public class ClipboardVoiceDialog extends Dialog implements ISynthesisListener {
    @BindView(R.id.copy_text_view)
    TextView copyTextView;
    @BindView(R.id.synthetic_wav_button)
    Button syntheticWavButton;
    @BindView(R.id.dialog_close)
    ImageView dialogClose;
    @BindView(R.id.loading_view)
    AVLoadingIndicatorView loadingView;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;

    private String mContent;
    private VoiceSynthesisTask voiceSynthesisTask;

    public ClipboardVoiceDialog(Context context) {
        super(context, R.style.LeaderBoardDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(true);

        setContentView(R.layout.dialog_clipboard);
        ButterKnife.bind(this);
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        //获取屏幕高度，不包括导航栏
        Point size = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getSize(size);
        layoutParams.height = size.y;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);

        syntheticWavButton.setClickable(true);
        syntheticWavButton.setText("朗读");
        syntheticWavButton.setBackgroundResource(R.drawable.wav_output_button_selector);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (voiceSynthesisTask != null) {
            voiceSynthesisTask.cancel(true);
        }
        dismiss();
    }

    @OnClick(R.id.synthetic_wav_button)
    public void onClicked() {
        if (ContextCompat.checkSelfPermission(MainApplication.getAppContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getOwnerActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        } else {
            syntheticWavButton.setClickable(false);
            syntheticWavButton.setText("合成中..");
            syntheticWavButton.setBackgroundResource(R.drawable.wav_output_button_disable);
            startVoiceSynthesisTask();
        }
    }

    @OnClick(R.id.dialog_close)
    public void onViewClicked() {
        if (voiceSynthesisTask != null) {
            voiceSynthesisTask.cancel(true);
        }
        dismiss();
    }

    public void setCopyContent(String content) {
        mContent = content;
        copyTextView.setText(content);
    }

    private void startVoiceSynthesisTask() {
        String content = mContent.replaceAll("[^a-zA-Z0-9\\u4E00-\\u9FA5“”‘’' ]+", "|");
        content = content.replaceAll("  +", "|");
        content = content.replaceAll(" *\\|+ *\\|* *", "|");
        List<String> textList = new ArrayList<>(Arrays.asList(content.split("\\|")));

        String type = "EN";
        for (String text : textList) {
            if (TextUtil.isChinese(text)) {
                type = "CN";
                break;
            }
        }

        if (voiceSynthesisTask != null) {
            voiceSynthesisTask.cancel(true);
        }
        String taskId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
        voiceSynthesisTask = new VoiceSynthesisTask(this);
        voiceSynthesisTask.setTextInfo(new TextInfo(taskId, type, textList));
        voiceSynthesisTask.execute();
    }

    @Override
    public void beforeSynthesis() {
        loadingLayout.setVisibility(View.VISIBLE);
        loadingView.show();
    }

    @Override
    public void onSuccess(String voicePath) {
        loadingView.hide();
        loadingLayout.setVisibility(View.GONE);
        Intent intent = new Intent(getContext(), ClipboardVoiceActivity.class);
        intent.putExtra("voice_path", voicePath);
        intent.putExtra("content", mContent);
        getContext().startActivity(intent);
        dismiss();
    }

    @Override
    public void onSuccess(Queue<String> voicePathQueue, int stepNum) {

    }

    @Override
    public void onFailed() {
        loadingView.hide();
        loadingLayout.setVisibility(View.GONE);
        Toast.makeText(getContext(), "语音合成失败，请重试", Toast.LENGTH_SHORT).show();
        syntheticWavButton.setClickable(true);
        syntheticWavButton.setText("朗读");
        syntheticWavButton.setBackgroundResource(R.drawable.wav_output_button_selector);
    }
}
