package com.nickole.earworld.view;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.nickole.earworld.R;
import com.nickole.earworld.entity.News;
import com.nickole.earworld.home.SingleMediaPlayer;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author shuzijie
 * @date 2019-06-04.
 */
public class NewsPlayer {
    private volatile SingleMediaPlayer mediaPlayer;
    private boolean isVoiceReady = false;

    @BindView(R.id.wav_play_or_stop_button)
    Button wavPlayOrStopButton;
    @BindView(R.id.loading_view)
    AVLoadingIndicatorView loadingView;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.news_title_view)
    MarqueeView newsTitleView;
    private String voiceSourcePath;
    private Queue<String> voicePathQueue;
    private int stepNum;

    private String saveWavContent;

    public NewsPlayer(View view) {
        ButterKnife.bind(this, view);
        wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_play);
        newsTitleView.setText("以耳为伍，不期而遇");
    }

    public String getVoiceSourcePath() {
        return voiceSourcePath;
    }


    public void initPlayer(String voicePath, News news) {
        destroyPlayer();

        newsTitleView.setText(news.getTitle() + " - " + news.getPosterScreenName());
        mediaPlayer = SingleMediaPlayer.getInstance();
        voiceSourcePath = voicePath;
        saveWavContent = news.getTitle();
        try {
            mediaPlayer.setDataSource(voicePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                int index = 0;
                @Override
                public void onCompletion(MediaPlayer mp) {
                    index++;
                    if (voicePathQueue != null && index < stepNum) {

                    } else {
                        wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_play);
                        if (mediaPlayer.getPlayerStateListener() != null) {
                            mediaPlayer.getPlayerStateListener().onWavCompletion();
                        }
                    }
                }
            });
            isVoiceReady = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void initPlayer(final Queue<String> voicePathQueue, News news, final int stepNum) {
//        destroyPlayer();
//        newsTitleView.setText(news.getTitle() + " - " + news.getPosterScreenName());
//        String voicePath = voicePathQueue.poll();
//        mediaPlayer = SingleMediaPlayer.getInstance();
//        saveWavContent = news.getTitle();
//        try {
//            mediaPlayer.setDataSource(voicePath);
//            mediaPlayer.prepare();
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                int index = 0;
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    index++;
//                    if (index < stepNum) {
//                        String voicePath = voicePathQueue.poll();
//                        mediaPlayer = SingleMediaPlayer.getInstance();
//                    } else {
//                        wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_play);
//                        if (mediaPlayer.getPlayerStateListener() != null) {
//                            mediaPlayer.getPlayerStateListener().onWavCompletion();
//                        }
//                    }
//                }
//            });
//            isVoiceReady = true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void playWav() {
        if (mediaPlayer != null && isVoiceReady) {
            wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_stop);
            mediaPlayer.start();
            newsTitleView.startMarquee();
        }
    }

    public void pauseWav() {
        if (mediaPlayer != null) {
            wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_play);
            mediaPlayer.pause();
            newsTitleView.pauseMarquee();
        }
    }

    public void updatePlayerState() {
        if (mediaPlayer.isPlaying()) {
            wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_stop);
            newsTitleView.startMarquee();
        } else {
            wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_play);
            newsTitleView.pauseMarquee();
        }
    }

    public void hideLoading() {
        loadingView.hide();
        loadingLayout.setVisibility(View.GONE);
    }

    public void showLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
        loadingView.show();
    }

    public void destroyPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            isVoiceReady = false;
            mediaPlayer = null;
            SingleMediaPlayer.destoryPlayer();
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
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

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        } else {
            return false;
        }
    }

    public String getSaveWavContent() {
        return saveWavContent;
    }
}
