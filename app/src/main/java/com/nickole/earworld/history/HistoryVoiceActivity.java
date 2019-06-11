package com.nickole.earworld.history;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;

import com.nickole.earworld.R;
import com.nickole.earworld.entity.VoiceFile;
import com.nickole.earworld.event.PlayHistoryVoiceEvent;
import com.nickole.earworld.main.BaseActivity;
import com.nickole.earworld.main.MainApplication;
import com.nickole.earworld.util.DateTimeUtil;
import com.nickole.earworld.util.FileUtil;
import com.nickole.earworld.util.SaveWavContentHelper;
import com.nickole.earworld.view.VoicePlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.Window.FEATURE_NO_TITLE;

/**
 * @author shuzijie
 * @date 2019-05-24.
 */
public class HistoryVoiceActivity extends BaseActivity {
    @BindView(R.id.go_back_button)
    Button goBackButton;
    Unbinder unbinder;
    @BindView(R.id.history_voice_recyclerview)
    RecyclerView historyVoiceRecyclerview;
    @BindView(R.id.wav_play_layout)
    FrameLayout wavPlayLayout;

    private HistoryVoiceAdapter historyVoiceAdapter;
    private boolean isPlayerLayoutHidden = true;
    private VoicePlayer mVoicePlayer;
    private VoiceFile mCurVoiceFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        supportRequestWindowFeature(FEATURE_NO_TITLE);
        setContentView(R.layout.activity_voice_history);
        unbinder = ButterKnife.bind(this);

        mVoicePlayer = new VoicePlayer(this);
        mVoicePlayer.hideLoading();
        mVoicePlayer.hideSaveButton();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        historyVoiceRecyclerview.setLayoutManager(layoutManager);
        historyVoiceAdapter = new HistoryVoiceAdapter(this);
        //获取列表
        historyVoiceAdapter.setVoiceFileList(getHistoryVoiceFiles());
        historyVoiceRecyclerview.setAdapter(historyVoiceAdapter);
        historyVoiceAdapter.notifyDataSetChanged();
    }

    private List<VoiceFile> getHistoryVoiceFiles() {
        String dirPath = MainApplication.getAppContext().getFilesDir().getAbsolutePath() + "/myvoice";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return null;
        }

        List<VoiceFile> voiceFileList = new ArrayList<>();
        //按文件创建时间后先顺序排序
        File[] fileList = dir.listFiles();
        Arrays.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File a, File b) {
                        if (a.lastModified() > b.lastModified()) {
                            return -1;
                        }
                        if (a.lastModified() == b.lastModified()) {
                            return 0;
                        }
                        return 1;
                    }
                });
        for (File f : fileList) {
            if (f.isFile() && f.getName().endsWith(".wav")) {
                String content = SaveWavContentHelper.getWavContent(f.getName());
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(f.getAbsolutePath());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int duration = mediaPlayer.getDuration();
                String fileCreateDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(f.lastModified()));
                VoiceFile voiceFile = new VoiceFile(f.getAbsolutePath(),
                        content,
                        fileCreateDate,
                        duration < 1000 ? duration + "毫秒" : DateTimeUtil.formatTime(duration),
                        FileUtil.ShowLongFileSize(f.length()));
                voiceFileList.add(voiceFile);
            }
        }
        return voiceFileList;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVoicePlayer != null) {
            mVoicePlayer.destroyPlayer();
        }
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.go_back_button)
    public void onViewClicked() {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayHistoryVoiceEvent event) {
        //开始播放语音
        if (mVoicePlayer != null) {
            if (isPlayerLayoutHidden) {
                showVoicePlayerLayout();
                isPlayerLayoutHidden = false;
            }
            mCurVoiceFile = event.getVoiceFile();
            mVoicePlayer.initPlayer(mCurVoiceFile.getFilePath(), null);
            mVoicePlayer.playWav();
        }
    }

    private void showVoicePlayerLayout() {
        isPlayerLayoutHidden = false;
        //设置动画，从自身位置的最下端向上滑动了自身的高度，持续时间为500ms
        final TranslateAnimation upAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        upAnimation.setDuration(500);
        wavPlayLayout.setVisibility(View.VISIBLE);
        wavPlayLayout.startAnimation(upAnimation);
    }
}
