package com.nickole.earworld.view;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nickole.earworld.R;
import com.nickole.earworld.main.MainApplication;
import com.nickole.earworld.util.FileUtil;
import com.nickole.earworld.util.SaveWavContentHelper;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nickole.earworld.util.DateTimeUtil.formatTime;

/**
 * @author shuzijie
 * @date 2019-06-04.
 */
public class VoicePlayer {
    private static final String TIME_START = "00:00";

    private volatile MediaPlayer mediaPlayer;
    private volatile boolean isVoiceReady = false;
    private SeekBarUpdateThread seekBarUpdateThread = null;

    @BindView(R.id.wav_play_or_stop_button)
    Button wavPlayOrStopButton;
    @BindView(R.id.wav_current_time_text)
    TextView wavCurrentTimeText;
    @BindView(R.id.play_seek_bar)
    SeekBar playSeekBar;
    @BindView(R.id.wav_all_time_text)
    TextView wavAllTimeText;
    @BindView(R.id.save_wav_button)
    LinearLayout saveWavButton;
    @BindView(R.id.loading_view)
    AVLoadingIndicatorView loadingView;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;

    private String voiceSourcePath;
    private String saveWavContent;

    public VoicePlayer(Activity source) {
        ButterKnife.bind(this, source);
        initView();
    }

    public VoicePlayer(View view) {
        ButterKnife.bind(this, view);
        initView();
    }

    private void initView() {
        wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_play);
        wavCurrentTimeText.setText(TIME_START);
        wavAllTimeText.setText(TIME_START);
        playSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePlayerTime(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(playSeekBar.getProgress());
                }
            }
        });
    }

    public void initPlayer(String voicePath, String content) {
        voiceSourcePath = voicePath;
        saveWavContent = content;
        destroyPlayer();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(voicePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_play);
                }
            });
            isVoiceReady = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playWav() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && isVoiceReady) {
            wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_stop);

            mediaPlayer.start();
            int length = mediaPlayer.getDuration();
            wavAllTimeText.setText(formatTime(length));
            playSeekBar.setMax(length);
            if (seekBarUpdateThread == null || !seekBarUpdateThread.isAlive()) {
                seekBarUpdateThread = new SeekBarUpdateThread();
                seekBarUpdateThread.start();
            }
        }
    }

    public void pauseWav() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_play);
            mediaPlayer.pause();
        }
    }

    private void updatePlayerTime(int position) {
        wavCurrentTimeText.setText(formatTime(position));
    }

    public void hideLoading() {
        loadingView.hide();
        loadingLayout.setVisibility(View.GONE);
    }

    public void hideSaveButton() {
        saveWavButton.setVisibility(View.GONE);
    }

    public void showLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
        loadingView.show();
    }

    class SeekBarUpdateThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (mediaPlayer != null && isVoiceReady) {
                if (mediaPlayer.isPlaying() && !playSeekBar.isPressed()) {
                    playSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        }
    }

    public void destroyPlayer() {
        if (mediaPlayer != null) {
            isVoiceReady = false;
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @OnClick(R.id.wav_play_or_stop_button)
    public void onWavPlayOrStopButtonClicked() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            pauseWav();
        } else {
            playWav();
        }
    }

    @OnClick(R.id.save_wav_button)
    public void onSaveWavButtonClicked() {
        if (voiceSourcePath != null) {
            String fileName = voiceSourcePath.substring(voiceSourcePath.lastIndexOf("/") + 1);
            String dirPath = MainApplication.getAppContext().getFilesDir().getAbsolutePath();
            String newPathName = dirPath + "/myvoice/" + fileName;
            if ((new File(newPathName)).exists()) {
                Toast.makeText(MainApplication.getAppContext(), "语音已保存", Toast.LENGTH_SHORT).show();
            } else {
                boolean result = FileUtil.copyFile(voiceSourcePath, newPathName);
                if (saveWavContent != null) {
                    SaveWavContentHelper.saveWavMessage(fileName, saveWavContent);
                }
                Toast.makeText(MainApplication.getAppContext(), result ? "保存成功" : "保存失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
