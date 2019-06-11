package com.nickole.earworld.clipboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.nickole.earworld.R;
import com.nickole.earworld.main.BaseActivity;
import com.nickole.earworld.view.VoicePlayer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.Window.FEATURE_NO_TITLE;

/**
 * @author shuzijie
 * @date 2019-05-24.
 */
public class ClipboardVoiceActivity extends BaseActivity {
    @BindView(R.id.go_back_button)
    Button goBackButton;
    @BindView(R.id.copy_text_view)
    TextView copyTextView;
    VoicePlayer mVoicePlayer;
    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(FEATURE_NO_TITLE);
        setContentView(R.layout.activity_clipboard_voice);
        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        String content = intent.getStringExtra("content");
        copyTextView.setText(content);
        mVoicePlayer = new VoicePlayer(this);
        mVoicePlayer.hideLoading();
        mVoicePlayer.initPlayer(intent.getStringExtra("voice_path"), content);
        //自动开始播放
        mVoicePlayer.playWav();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVoicePlayer != null) {
            mVoicePlayer.destroyPlayer();
        }
        unbinder.unbind();
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {

    }

    @OnClick(R.id.go_back_button)
    public void onViewClicked() {
        finish();
    }
}
