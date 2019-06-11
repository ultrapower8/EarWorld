package com.nickole.earworld.transform;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nickole.earworld.R;
import com.nickole.earworld.entity.TextInfo;
import com.nickole.earworld.history.HistoryVoiceActivity;
import com.nickole.earworld.main.MainApplication;
import com.nickole.earworld.view.VoicePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * @author shuzijie
 */
public class TransformFragment extends Fragment implements ISynthesisListener {

    private static final int MAX_TEXT_COUNT = 150;

    @BindView(R.id.text_input_edittext)
    EditText textInputEdittext;
    @BindView(R.id.voice_speed_button)
    LinearLayout voiceSpeedButton;
    @BindView(R.id.voice_slow_speed_button)
    RadioButton voiceSlowSpeedButton;
    @BindView(R.id.voice_normal_speed_button)
    RadioButton voiceNormalSpeedButton;
    @BindView(R.id.voice_fast_speed_button)
    RadioButton voiceFastSpeedButton;
    @BindView(R.id.voice_standstill_button)
    LinearLayout voiceStandstillButton;
    @BindView(R.id.voice_standstill_button_0)
    RadioButton voiceStandstillButton0;
    @BindView(R.id.voice_standstill_button_1)
    RadioButton voiceStandstillButton1;
    @BindView(R.id.voice_standstill_button_2)
    RadioButton voiceStandstillButton2;
    @BindView(R.id.voice_standstill_button_3)
    RadioButton voiceStandstillButton3;
    @BindView(R.id.voice_standstill_button_4)
    RadioButton voiceStandstillButton4;
    @BindView(R.id.voice_standstill_button_5)
    RadioButton voiceStandstillButton5;
    @BindView(R.id.voice_tone_button)
    LinearLayout vioceToneButton;
    @BindView(R.id.voice_low_tone_button)
    RadioButton voiceLowToneButton;
    @BindView(R.id.voice_normal_tone_button)
    RadioButton voiceNormalToneButton;
    @BindView(R.id.voice_high_tone_button)
    RadioButton voiceHighToneButton;
    @BindView(R.id.synthetic_wav_button)
    Button syntheticWavButton;

    @BindView(R.id.wav_play_layout)
    FrameLayout wavPlayLayout;
    Unbinder unbinder;
    @BindView(R.id.text_count)
    TextView textCount;
    @BindView(R.id.voice_speed_layout)
    RadioGroup voiceSpeedLayout;
    @BindView(R.id.voice_standstill_layout)
    HorizontalScrollView voiceStandstillLayout;
    @BindView(R.id.voice_tone_layout)
    RadioGroup voiceToneLayout;

    @BindView(R.id.voice_language_button)
    LinearLayout voiceLanguageButton;
    @BindView(R.id.voice_english_button)
    RadioButton voiceEnglishButton;
    @BindView(R.id.voice_chinese_button)
    RadioButton voiceChineseButton;
    @BindView(R.id.voice_language_layout)
    RadioGroup voiceLanguageLayout;
    @BindView(R.id.wav_document_button)
    Button wavDocumentButton;

    private VoiceSynthesisTask voiceSynthesisTask;
    private boolean isPlayerLayoutHidden = true;
    private int heightOfSettingLayout = 0;
    private String voiceLanguage = "EN";
    //单位: ms
    private int voiceStandstillTime = 1000;
    private int voiceSpeed = 1;
    private int voiceTone = 1;
    private VoicePlayer mVoicePlayer;
    private String saveWavContent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transform, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        mVoicePlayer = new VoicePlayer(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint() && !isHidden() && mVoicePlayer != null) {
            mVoicePlayer.pauseWav();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden && mVoicePlayer != null) {
            mVoicePlayer.pauseWav();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed() && !isHidden() && !isVisibleToUser && mVoicePlayer != null) {
            mVoicePlayer.pauseWav();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mVoicePlayer != null) {
            mVoicePlayer.destroyPlayer();
        }
        unbinder.unbind();
    }

    private void initView() {
        syntheticWavButton.setClickable(false);
        syntheticWavButton.setText("合成语音");

        voiceLanguageLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                heightOfSettingLayout = voiceLanguageLayout.getHeight();
                voiceLanguageLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    @OnClick(R.id.voice_speed_button)
    public void onVioceSpeedButtonClicked() {
        if (voiceSpeedLayout.getVisibility() == View.VISIBLE) {
            expandVoiceSettingLayout(voiceSpeedLayout, false);
        } else {
            hideAllVoiceSetting(voiceSpeedLayout);
            expandVoiceSettingLayout(voiceSpeedLayout, true);
        }
    }


    @OnClick(R.id.voice_standstill_button)
    public void onVoiceStandstillButtonClicked() {
        if (voiceStandstillLayout.getVisibility() == View.VISIBLE) {
            expandVoiceSettingLayout(voiceStandstillLayout, false);
        } else {
            hideAllVoiceSetting(voiceStandstillLayout);
            expandVoiceSettingLayout(voiceStandstillLayout, true);
        }
    }

    @OnClick(R.id.voice_tone_button)
    public void onVioceToneButtonClicked() {
        if (voiceToneLayout.getVisibility() == View.VISIBLE) {
            expandVoiceSettingLayout(voiceToneLayout, false);
        } else {
            hideAllVoiceSetting(voiceToneLayout);
            expandVoiceSettingLayout(voiceToneLayout, true);
        }
    }

    @OnClick(R.id.synthetic_wav_button)
    public void onSyntheticWavButtonClicked() {
        if (ContextCompat.checkSelfPermission(MainApplication.getAppContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        } else {
            syntheticWavButton.setClickable(false);
            syntheticWavButton.setText("合成中...");
            syntheticWavButton.setBackgroundResource(R.drawable.wav_output_button_disable);
            textInputEdittext.setFocusableInTouchMode(false);
            textInputEdittext.setFocusable(false);
            startVoiceSynthesisTask();
        }
    }

    private void startVoiceSynthesisTask() {
        String content = textInputEdittext.getText().toString();
        saveWavContent = content;
        content = content.replaceAll("[^a-zA-Z0-9\\u4E00-\\u9FA5“”‘’' ]+", "|");
        content = content.replaceAll("  +", "|");
        content = content.replaceAll(" *\\|+ *\\|* *", "|");
        List<String> textList = new ArrayList<>(Arrays.asList(content.split("\\|")));

        if (voiceSynthesisTask != null) {
            voiceSynthesisTask.cancel(true);
        }
        String taskId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
        voiceSynthesisTask = new VoiceSynthesisTask(this);
        voiceSynthesisTask.setTextInfo(new TextInfo(taskId, voiceLanguage, textList, voiceSpeed, voiceStandstillTime, voiceTone));
        voiceSynthesisTask.execute();
    }

    @OnTextChanged(value = R.id.text_input_edittext, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void editTextDetailChange(CharSequence s, int start, int before, int count) {
        int detailLength = s.toString().trim().length();

        if (detailLength > 0) {
            syntheticWavButton.setClickable(true);
            syntheticWavButton.setBackgroundResource(R.drawable.wav_output_button_selector);
            if (detailLength > MAX_TEXT_COUNT) {
                textInputEdittext.setText(s.toString().substring(0, MAX_TEXT_COUNT));
                textInputEdittext.setSelection(MAX_TEXT_COUNT);
                textCount.setText(String.format(Locale.CHINESE, "%d 字", MAX_TEXT_COUNT));
                Toast.makeText(getContext(), "最多只能输入150个字", Toast.LENGTH_SHORT).show();
            } else {
                textCount.setText(String.format(Locale.CHINESE, "%d 字", detailLength));
            }
        } else {
            syntheticWavButton.setClickable(false);
            syntheticWavButton.setBackgroundResource(R.drawable.wav_output_button_disable);
        }
    }

    @Override
    public void beforeSynthesis() {
        if (isPlayerLayoutHidden) {
            showVoicePlayerLayout();
        }
        if (mVoicePlayer != null) {
            mVoicePlayer.showLoading();
        }
    }

    @Override
    public void onSuccess(String voicePath) {
        textInputEdittext.setFocusableInTouchMode(true);
        textInputEdittext.setFocusable(true);

        //自动开始播放
        if (mVoicePlayer != null) {
            mVoicePlayer.hideLoading();
            mVoicePlayer.initPlayer(voicePath, saveWavContent);
            mVoicePlayer.playWav();
        }
        if (textInputEdittext.getText().toString().trim().length() > 0) {
            syntheticWavButton.setClickable(true);
            syntheticWavButton.setText("合成语音");
            syntheticWavButton.setBackgroundResource(R.drawable.wav_output_button_selector);
        }
    }

    @Override
    public void onSuccess(Queue<String> voicePathQueue, int stepNum) {

    }

    @Override
    public void onFailed() {
        textInputEdittext.setFocusableInTouchMode(true);
        textInputEdittext.setFocusable(true);
        if (mVoicePlayer != null) {
            mVoicePlayer.hideLoading();
        }
        if (!isPlayerLayoutHidden) {
            hideVoicePlayerLayout();
        }
        Toast.makeText(getContext(), "语音合成失败，请重试", Toast.LENGTH_SHORT).show();
        if (textInputEdittext.getText().toString().trim().length() > 0) {
            syntheticWavButton.setClickable(true);
            syntheticWavButton.setText("合成语音");
            syntheticWavButton.setBackgroundResource(R.drawable.wav_output_button_selector);
        }
    }

    @OnClick({R.id.voice_language_button, R.id.voice_english_button, R.id.voice_chinese_button})
    public void onVoiceLanguageClicked(View view) {
        switch (view.getId()) {
            case R.id.voice_language_button:
                if (voiceLanguageLayout.getVisibility() == View.VISIBLE) {
                    expandVoiceSettingLayout(voiceLanguageLayout, false);
                } else {
                    hideAllVoiceSetting(voiceLanguageLayout);
                    expandVoiceSettingLayout(voiceLanguageLayout, true);
                }
                break;
            case R.id.voice_english_button:
                voiceLanguage = "EN";
                break;
            case R.id.voice_chinese_button:
                voiceLanguage = "CN";
                break;
            default:
        }
    }

    @OnClick({R.id.voice_slow_speed_button, R.id.voice_normal_speed_button, R.id.voice_fast_speed_button})
    public void onVoiceSpeedClicked(View view) {
        switch (view.getId()) {
            case R.id.voice_slow_speed_button:
                voiceSpeed = 0;
                break;
            case R.id.voice_normal_speed_button:
                voiceSpeed = 1;
                break;
            case R.id.voice_fast_speed_button:
                voiceSpeed = 2;
                break;
            default:
        }
    }

    @OnClick({R.id.voice_standstill_button_0, R.id.voice_standstill_button_1, R.id.voice_standstill_button_2, R.id.voice_standstill_button_3, R.id.voice_standstill_button_4, R.id.voice_standstill_button_5})
    public void onVoiceStandstillClicked(View view) {
        switch (view.getId()) {
            case R.id.voice_standstill_button_0:
                voiceStandstillTime = 500;
                break;
            case R.id.voice_standstill_button_1:
                voiceStandstillTime = 1000;
                break;
            case R.id.voice_standstill_button_2:
                voiceStandstillTime = 2000;
                break;
            case R.id.voice_standstill_button_3:
                voiceStandstillTime = 3000;
                break;
            case R.id.voice_standstill_button_4:
                voiceStandstillTime = 4000;
                break;
            case R.id.voice_standstill_button_5:
                voiceStandstillTime = 5000;
                break;
            default:
        }
    }

    @OnClick({R.id.voice_low_tone_button, R.id.voice_normal_tone_button, R.id.voice_high_tone_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.voice_low_tone_button:
                voiceTone = 0;
                break;
            case R.id.voice_normal_tone_button:
                voiceTone = 1;
                break;
            case R.id.voice_high_tone_button:
                voiceTone = 2;
                break;
            default:
        }
    }

    private void hideAllVoiceSetting(ViewGroup needToExpandLayout) {
        if (voiceLanguageLayout != needToExpandLayout && voiceLanguageLayout.getVisibility() == View.VISIBLE) {
            expandVoiceSettingLayout(voiceLanguageLayout, false);
        } else if (voiceToneLayout != needToExpandLayout && voiceToneLayout.getVisibility() == View.VISIBLE) {
            expandVoiceSettingLayout(voiceToneLayout, false);
        } else if (voiceSpeedLayout != needToExpandLayout && voiceSpeedLayout.getVisibility() == View.VISIBLE) {
            expandVoiceSettingLayout(voiceSpeedLayout, false);
        } else if (voiceStandstillLayout != needToExpandLayout && voiceStandstillLayout.getVisibility() == View.VISIBLE) {
            expandVoiceSettingLayout(voiceStandstillLayout, false);
        }
        needToExpandLayout.setVisibility(View.VISIBLE);
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

    private void hideVoicePlayerLayout() {
        isPlayerLayoutHidden = true;
        final TranslateAnimation downAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
        downAnimation.setDuration(500);
        downAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                wavPlayLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        wavPlayLayout.startAnimation(downAnimation);
    }

    private void expandVoiceSettingLayout(final ViewGroup viewGroup, boolean isShow) {
        ValueAnimator va;
        if (isShow) {
//            viewGroup.setVisibility(View.VISIBLE);
            va = ValueAnimator.ofInt(0, heightOfSettingLayout);
        } else {
            va = ValueAnimator.ofInt(heightOfSettingLayout, 0);
            va.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    viewGroup.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h = (Integer) valueAnimator.getAnimatedValue();
                //动态更新view的高度
                ViewGroup.LayoutParams params = viewGroup.getLayoutParams();
                params.height = h;
                viewGroup.setLayoutParams(params);
            }
        });

        va.setDuration(300);
        va.start();
    }

    @OnClick(R.id.wav_document_button)
    public void onViewClicked() {
        Intent intent = new Intent(getContext(), HistoryVoiceActivity.class);
        startActivity(intent);
    }
}
