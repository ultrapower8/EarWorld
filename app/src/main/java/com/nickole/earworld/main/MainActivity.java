package com.nickole.earworld.main;

import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nickole.earworld.R;
import com.nickole.earworld.api.ConnectEngineApi;
import com.nickole.earworld.home.HomeFragment;
import com.nickole.earworld.personcenter.PersonCenterFragment;
import com.nickole.earworld.transform.TransformFragment;

import net.robinx.lib.blurview.BlurDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.Window.FEATURE_NO_TITLE;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    // 转换fragment
    private TransformFragment transformFragment;
    // 主页fragment
    private HomeFragment homeFragment;
    // 我的fragment
    private PersonCenterFragment personCenterFragment;
    // fragment管理器
    private FragmentManager fragmentManager;

    private LinearLayout mBlurDrawableLayout;
    private BlurDrawable mBlurDrawable;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;

    @BindView(R.id.transform_button)
    ImageButton transformButton;
    @BindView(R.id.transform_text)
    TextView transformText;
    @BindView(R.id.transform_layout)
    LinearLayout transformLayout;
    @BindView(R.id.home_button)
    ImageButton homeButton;
    @BindView(R.id.home_text)
    TextView homeText;
    @BindView(R.id.home_layout)
    LinearLayout homeLayout;
    @BindView(R.id.my_button)
    ImageButton myButton;
    @BindView(R.id.my_text)
    TextView myText;
    @BindView(R.id.my_layout)
    LinearLayout myLayout;

    private ClipboardManager mClipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();

        fragmentManager = getSupportFragmentManager();
        setTabSelection(0);
        ConnectEngineApi.connectEngine(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBlurDrawable.onDestroy();
    }

    @Override
    protected void initViews() {
        homeLayout.setOnClickListener(this);
        transformLayout.setOnClickListener(this);
        myLayout.setOnClickListener(this);

//        initViewBlur();
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_layout:
                resetAllImage();
                homeButton.setImageResource(R.drawable.home_pressed);
                homeText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBright));
                setTabSelection(0);
                break;

            case R.id.transform_layout:
                resetAllImage();
                transformButton.setImageResource(R.drawable.transform_pressed);
                transformText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBright));
                setTabSelection(1);
                break;

            case R.id.my_layout:
                resetAllImage();
                myButton.setImageResource(R.drawable.my_pressed);
                myText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBright));
                setTabSelection(2);
                break;
            default:
        }
    }

    private void resetAllImage() {
        homeButton.setImageResource(R.drawable.home_normal);
        homeText.setTextColor(ContextCompat.getColor(this, R.color.gray_light));
        transformButton.setImageResource(R.drawable.transform_normal);
        transformText.setTextColor(ContextCompat.getColor(this, R.color.gray_light));
        myButton.setImageResource(R.drawable.my_normal);
        myText.setTextColor(ContextCompat.getColor(this, R.color.gray_light));
    }

    private void setTabSelection(int index) {
        // 开启一个fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 隐藏所有fragment
        hideFragments(transaction);

        switch (index) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.content, homeFragment);
                } else {
                    transaction.show(homeFragment);
                }
                break;

            case 1:
                if (transformFragment == null) {
                    transformFragment = new TransformFragment();
                    transaction.add(R.id.content, transformFragment);
                } else {
                    transaction.show(transformFragment);
                }
                break;

            case 2:
                if (personCenterFragment == null) {
                    personCenterFragment = new PersonCenterFragment();
                    transaction.add(R.id.content, personCenterFragment);
                } else {
                    transaction.show(personCenterFragment);
                }
                break;
            default:
        }

        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (transformFragment != null) {
            transaction.hide(transformFragment);
        }
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (personCenterFragment != null) {
            transaction.hide(personCenterFragment);
        }
    }

    private void initViewBlur(){
        //界面高斯模糊
        mBlurDrawableLayout = this.findViewById(R.id.act_main_down_bg);
        mBlurDrawableLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBlurDrawableLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mBlurDrawable = new BlurDrawable(MainActivity.this);
                mBlurDrawable.drawableContainerId(R.id.act_main_down_bg)
                        .cornerRadius(0)
                        .blurRadius(10)
                        .overlayColor(Color.parseColor("#ddffffff"))
                        .offset(mBlurDrawableLayout.getLeft(), mBlurDrawableLayout.getTop() );
                mBlurDrawableLayout.setBackgroundDrawable(mBlurDrawable);
            }
        });

//        this.findViewById(R.id.content).getViewTreeObserver().addOnGlobalLayoutListener(getOnGlobalLayoutListener());
    }

//    @Override
//    public void beforeSynthesis() {
//
//    }


//    @Override
//    public void onSuccess(String content, String voicePath) {
//        if (MonitorAppUtil.isBackground()) {
//            //存到SharedPreferences缓存中
//            ClipboardVoiceCacheHelper.saveClipboardVoice(content, voicePath, System.currentTimeMillis());
//        } else {
//            initPlayer(voicePath);
//            playWav();
//        }
//    }
//
//    @Override
//    public void onFailed() {
//        if (!MonitorAppUtil.isBackground()) {
//            Toast.makeText(this, "语音合成失败", Toast.LENGTH_SHORT).show();
//        }
//    }



//    public ViewTreeObserver.OnGlobalLayoutListener getOnGlobalLayoutListener() {
//        if (mOnGlobalLayoutListener == null) {
//            mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    //BlurDrawable
//                    ViewCompat.postInvalidateOnAnimation(mBlurDrawableLayout);
//                }
//            };
//        }
//        return mOnGlobalLayoutListener;
//    }

//    public void updateMainDownBar(){
//        ViewCompat.postInvalidateOnAnimation(mBlurDrawableLayout);
//    }
}
