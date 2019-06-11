package com.nickole.earworld.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.nickole.earworld.R;
import com.nickole.earworld.entity.News;
import com.nickole.earworld.event.UpdatePlayerViewEvent;
import com.nickole.earworld.main.BaseActivity;
import com.nickole.earworld.main.MainApplication;
import com.nickole.earworld.util.DateTimeUtil;
import com.nickole.earworld.util.FileUtil;
import com.nickole.earworld.util.SaveWavContentHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.Window.FEATURE_NO_TITLE;
import static com.nickole.earworld.util.DateTimeUtil.formatTime;

/**
 * @author shuzijie
 * @date 2019-06-05.
 */
public class NewsDetailActivity extends BaseActivity implements IPlayerStateListener {

    @BindView(R.id.news_detail_close_btn)
    ImageView newsDetailCloseBtn;
    @BindView(R.id.news_title)
    TextView newsTitle;
    @BindView(R.id.publish_name)
    TextView publishName;
    @BindView(R.id.publish_time)
    TextView publishTime;
    @BindView(R.id.comment_count)
    TextView commentCount;
    @BindView(R.id.news_image)
    SimpleDraweeView newsImage;
    @BindView(R.id.news_content)
    TextView newsContent;
    @BindView(R.id.wav_current_time_text)
    TextView wavCurrentTimeText;
    @BindView(R.id.play_seek_bar)
    SeekBar playSeekBar;
    @BindView(R.id.wav_all_time_text)
    TextView wavAllTimeText;
    @BindView(R.id.wav_play_or_stop_button)
    Button wavPlayOrStopButton;
    @BindView(R.id.save_wav_button)
    Button saveWavButton;
    @BindView(R.id.news_detail_background)
    SimpleDraweeView newsDetailBackground;

    private News mCurNews;
    private SingleMediaPlayer mediaPlayer;
    private String voiceSourcePath;
    private String saveWavContent;
    private boolean isTerminateThread = false;
    private SeekBarUpdateThread seekBarUpdateThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurNews = (News) intent.getSerializableExtra("news_data");
        mediaPlayer = SingleMediaPlayer.getPlayer();
        voiceSourcePath = intent.getStringExtra("voice_source_path");
        saveWavContent = intent.getStringExtra("save_wav_content");
        initPlayerState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isTerminateThread = true;
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

    private void initPlayerState() {
        if (mCurNews != null) {
            newsTitle.setText(mCurNews.getTitle());
            newsContent.setText(mCurNews.getContent().trim());
            publishName.setText(mCurNews.getPosterScreenName());
            publishTime.setText(DateTimeUtil.DateTime2ShowStr(mCurNews.getPublishDate()));
            commentCount.setText(String.valueOf(mCurNews.getCommentCount()));
            if (mCurNews.getImageUrls() != null && mCurNews.getImageUrls().size() > 0) {
                Uri url = Uri.parse(mCurNews.getImageUrls().get(0));
                newsImage.setImageURI(url);
                setNewsBlurBackgroud(newsDetailBackground, url);
            }
        }
        if (mediaPlayer != null) {
            mediaPlayer.setPlayerStateListener(this);
            if (mediaPlayer.isPlaying()) {
                onPlayWav();
            } else {
                onPauseWav();
            }
        }
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

    private void setNewsBlurBackgroud(SimpleDraweeView newsDetailBackground, Uri url) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(url)
                .setPostprocessor(new IterativeBoxBlurPostProcessor(10, 1))
                .build();

        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(newsDetailBackground.getController())
                .build();

        newsDetailBackground.setController(controller);
    }

    @OnClick({R.id.news_detail_close_btn, R.id.wav_play_or_stop_button, R.id.save_wav_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.news_detail_close_btn:
                finish();
                break;
            case R.id.wav_play_or_stop_button:
                if (mediaPlayer == null) {
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    onPauseWav();
                } else {
                    mediaPlayer.start();
                    onPlayWav();
                }
                EventBus.getDefault().post(new UpdatePlayerViewEvent());
                break;
            case R.id.save_wav_button:
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
                break;
            default:
        }
    }

    private void updatePlayerTime(int position) {
        wavCurrentTimeText.setText(formatTime(position));
    }

    @Override
    public void onWavCompletion() {
        wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_big_play_selector);
    }

    @Override
    public void onPlayWav() {
        wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_big_pause_selector);
        if (mediaPlayer != null) {
            int length = mediaPlayer.getDuration();
            wavAllTimeText.setText(formatTime(length));
            playSeekBar.setMax(length);
            playSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            if (seekBarUpdateThread == null || !seekBarUpdateThread.isAlive()) {
                seekBarUpdateThread = new SeekBarUpdateThread();
                seekBarUpdateThread.start();
            }
        }
    }

    @Override
    public void onPauseWav() {
        wavPlayOrStopButton.setBackgroundResource(R.drawable.icon_big_play_selector);
    }

    class SeekBarUpdateThread extends Thread {
        @Override
        public void run() {
            super.run();

            while (!isTerminateThread && mediaPlayer != null) {
                if (mediaPlayer.isPlaying() && !playSeekBar.isPressed()) {
                    playSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        }
    }
}
