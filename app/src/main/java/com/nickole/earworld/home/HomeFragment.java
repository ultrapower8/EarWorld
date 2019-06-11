package com.nickole.earworld.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.lhh.apst.library.AdvancedPagerSlidingTabStrip;
import com.nickole.earworld.R;
import com.nickole.earworld.entity.News;
import com.nickole.earworld.entity.TextInfo;
import com.nickole.earworld.event.PlayNewsVoiceEvent;
import com.nickole.earworld.event.UpdatePlayerViewEvent;
import com.nickole.earworld.transform.ISynthesisListener;
import com.nickole.earworld.transform.VoiceSynthesisTask;
import com.nickole.earworld.util.TextUtil;
import com.nickole.earworld.view.NewsPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener {

    Unbinder unbinder;
    @BindView(R.id.wav_play_layout)
    FrameLayout wavPlayLayout;
    @BindView(R.id.open_news_detail_button)
    ImageView openNewsDetailButton;
    private View view;
    public AdvancedPagerSlidingTabStrip tabs;
    public ViewPager homeViewPager;

    private static final int VIEW_FIRST = 0;
    private static final int VIEW_SECOND = 1;
    private static final int VIEW_THIRD = 2;
    private static final int VIEW_FOUR = 3;
    private static final int VIEW_FIVE = 4;

    private static final int VIEW_SIZE = 5;

    private Context mContext;
    private boolean isPlayerLayoutHidden = true;
    private NewsPlayer mNewsPlayer;
    private VoiceSynthesisTask voiceSynthesisTask;
    private News mCurNews;

    private AFragment donationFragment = null;
    private NewsFragment assistanceFragment = null;
    private AFragment joinLowCarbonFragment = null;
    private AFragment aFragment = null;
    private AFragment bFragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //初始化播放器
        mNewsPlayer = new NewsPlayer(view);

        homeViewPager = view.findViewById(R.id.home_viewpager);
        tabs = view.findViewById(R.id.home_tabs);
        homeViewPager.setOffscreenPageLimit(VIEW_SIZE);
        FragmentAdapter adapter = new FragmentAdapter(getFragmentManager());
        homeViewPager.setAdapter(new FragmentAdapter(getFragmentManager()));
        adapter.notifyDataSetChanged();
        tabs.setViewPager(homeViewPager);
        tabs.setOnPageChangeListener(this);

        unbinder = ButterKnife.bind(this, view);
        openNewsDetailButton.setClickable(false);
        return view;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        ((MainActivity)getActivity()).updateMainDownBar();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        ((MainActivity)getActivity()).updateMainDownBar();
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((MainActivity)getActivity()).updateMainDownBar();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if (!hidden)
//            ((MainActivity)getActivity()).updateMainDownBar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNewsPlayer != null) {
            mNewsPlayer.destroyPlayer();
        }
        EventBus.getDefault().unregister(this);
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

    @OnClick(R.id.open_news_detail_button)
    public void onViewClicked() {
        if (mCurNews != null && mNewsPlayer != null) {
            Intent intent = new Intent(getContext(), NewsDetailActivity.class);
            intent.putExtra("news_data", mCurNews);
            intent.putExtra("voice_source_path", mNewsPlayer.getVoiceSourcePath());
            intent.putExtra("save_wav_content", mNewsPlayer.getSaveWavContent());
            startActivity(intent);
        }
    }

    public class FragmentAdapter extends FragmentStatePagerAdapter {
        protected LayoutInflater mInflater;

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public Fragment getItem(int position) {
            if (position >= 0 && position < VIEW_SIZE) {
                switch (position) {
                    case VIEW_FIRST:
                        if (null == assistanceFragment) {
                            assistanceFragment = new NewsFragment();
                        }
                        return assistanceFragment;
                    case VIEW_SECOND:
                        if (null == joinLowCarbonFragment) {
                            joinLowCarbonFragment = new AFragment();
                        }
                        return joinLowCarbonFragment;
                    case VIEW_THIRD:
                        if (null == donationFragment) {
                            donationFragment = new AFragment();
                        }
                        return donationFragment;
                    case VIEW_FOUR:
                        if (null == aFragment) {
                            aFragment = new AFragment();
                        }
                        return aFragment;
                    case VIEW_FIVE:
                        if (null == bFragment) {
                            bFragment = new AFragment();
                        }
                        return bFragment;
                    default:
                        break;
                }
            }
            return null;
        }


        @Override
        public int getCount() {
            return VIEW_SIZE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= 0 && position < VIEW_SIZE) {
                switch (position) {
                    case VIEW_FIRST:
                        return "头条";
                    case VIEW_SECOND:
                        return "推荐";
                    case VIEW_THIRD:
                        return "热点";
                    case VIEW_FOUR:
                        return "央视";
                    case VIEW_FIVE:
                        return "科技";
                    default:
                        break;
                }
            }
            return null;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayNewsVoiceEvent event) {
        //开始语音合成
        startVoiceSynthesisTask(event.getNews(), event.getUpdateCurNewsItemListener());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdatePlayerViewEvent event) {
        if (mNewsPlayer != null) {
            mNewsPlayer.updatePlayerState();
        }
    }

    private void startVoiceSynthesisTask(News news, final IUpdateCurNewsItemListener listener) {
        if (news == null || news.getContent() == null) {
            return;
        }
        mCurNews = news;
        String content = news.getTitle().replaceAll("[“”‘’']", "");
        content = content.replaceAll("[^a-zA-Z0-9\\u4E00-\\u9FA5 ]+", "|");
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
        voiceSynthesisTask = new VoiceSynthesisTask(new ISynthesisListener() {
            @Override
            public void beforeSynthesis() {
                if (mNewsPlayer != null) {
                    mNewsPlayer.showLoading();
                }
            }

            @Override
            public void onSuccess(String voicePath) {
                //自动开始播放
                if (mNewsPlayer != null && mCurNews != null) {
                    mNewsPlayer.hideLoading();
                    mNewsPlayer.initPlayer(voicePath, mCurNews);
                    mNewsPlayer.playWav();
                    openNewsDetailButton.setClickable(true);
                }
                if (listener != null) {
                    listener.updateCurNewsItemOnSuccess();
                }
            }

            @Override
            public void onSuccess(Queue<String> voicePathQueue, int stepNum) {

            }


            @Override
            public void onFailed() {
                if (mNewsPlayer != null) {
                    mNewsPlayer.hideLoading();
                }
                Toast.makeText(getContext(), "语音合成失败，请重试", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.updateCurNewsItemOnFailed();
                }
            }
        });
        voiceSynthesisTask.setTextInfo(new TextInfo(news.getId(), type, textList));
        voiceSynthesisTask.execute();
    }
}
