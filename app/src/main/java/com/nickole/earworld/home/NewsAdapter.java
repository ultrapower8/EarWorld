package com.nickole.earworld.home;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nickole.earworld.R;
import com.nickole.earworld.entity.News;
import com.nickole.earworld.event.PlayNewsVoiceEvent;
import com.nickole.earworld.main.MainApplication;
import com.nickole.earworld.util.DateTimeUtil;
import com.taishi.library.Indicator;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nickole on 2018/3/2.
 */

public class NewsAdapter extends RecyclerView.Adapter {
    public final static int STATUS_NONE = -1;
    public final static int STATUS_LOADING = 0;
    public final static int STATUS_EMPTY = 1;
    public final static int STATUS_REFRESH = 2;

    private List<News> mNewsList = new ArrayList<>();
    private static final int TYPE_FOOTER = -1;
    private LoadMoreViewHolder mLoadMoreViewHolder;
    private int currentNewsPosition = -1;
    private Activity mActivity;

    public NewsAdapter(Activity activity) {
        mActivity = activity;
    }

    public List<News> getNewsList() {
        return mNewsList;
    }

    public void setNewsList(List<News> newsList) {
        this.mNewsList = newsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_FOOTER == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer_view, parent, false);
            mLoadMoreViewHolder = new LoadMoreViewHolder(view);
            return mLoadMoreViewHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
            final NewsViewHolder baseHolder = new NewsViewHolder(view);

            baseHolder.listenButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int position = baseHolder.getAdapterPosition();
                    if (currentNewsPosition != position) {
                        if (ContextCompat.checkSelfPermission(MainApplication.getAppContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(mActivity, new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }, 1);
                        } else {
                            baseHolder.listenButtonIcon.setVisibility(View.GONE);
                            baseHolder.listenButtonText.setVisibility(View.GONE);
                            baseHolder.voiceIndicator.setVisibility(View.VISIBLE);
                            baseHolder.listenButton.setClickable(false);
                            //播放
                            News news = mNewsList.get(position);
                            PlayNewsVoiceEvent playNewsVoiceEvent = new PlayNewsVoiceEvent(news);
                            playNewsVoiceEvent.setUpdateCurNewsItemListener(new IUpdateCurNewsItemListener() {
                                @Override
                                public void updateCurNewsItemOnFailed() {
                                    baseHolder.listenButtonIcon.setVisibility(View.VISIBLE);
                                    baseHolder.listenButtonText.setVisibility(View.VISIBLE);
                                    baseHolder.voiceIndicator.setVisibility(View.GONE);
                                    baseHolder.listenButton.setClickable(true);
                                    currentNewsPosition = -1;
                                }

                                @Override
                                public void updateCurNewsItemOnSuccess() {

                                }
                            });
                            EventBus.getDefault().post(playNewsVoiceEvent);

                            //更新上一个播放的按钮样式
                            int lastPosition = currentNewsPosition;
                            currentNewsPosition = position;
                            if (lastPosition > -1) {
                                notifyItemChanged(lastPosition);
                            }
                        }
                    }
                }
            });

            baseHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int position = baseHolder.getAdapterPosition();
                    News news = mNewsList.get(position);
//                    Intent intent = new Intent(mContext , LectureContentActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                    intent.putExtra("lecture_item",lecture);
//                    intent.putExtra("alarm" , alarm );
//                    mContext.startActivity(intent);
                }
            });
            return baseHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_FOOTER) {
            ((LoadMoreViewHolder) holder).bind();
        } else {
            News news = mNewsList.get(position);
            NewsViewHolder baseHolder = (NewsViewHolder) holder;
            baseHolder.titleTextView.setText(news.getTitle());
            baseHolder.publisherTextView.setText(news.getPosterScreenName());
            baseHolder.contentTextView.setText(news.getContent().replaceAll("\\s+", ""));
            baseHolder.commentCountTextView.setText(String.valueOf(news.getCommentCount()));
            baseHolder.publishTimeTextView.setText(DateTimeUtil.DateTime2ShowStr(news.getPublishDate()));
            if (news.getImageUrls() != null && news.getImageUrls().size() > 0) {
                Uri url = Uri.parse(news.getImageUrls().get(0));
                baseHolder.newsImageView.setImageURI(url);
            }
            if (currentNewsPosition != position) {
                baseHolder.listenButtonIcon.setVisibility(View.VISIBLE);
                baseHolder.listenButtonText.setVisibility(View.VISIBLE);
                baseHolder.voiceIndicator.setVisibility(View.GONE);
                baseHolder.listenButton.setClickable(true);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mShowFooter && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return position;
    }

    public int getNewsItemSize() {
        return mNewsList.size();
    }

    @Override
    public int getItemCount() {
        return mNewsList.size() + (mShowFooter ? 1 : 0);
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        TextView publisherTextView;
        TextView publishTimeTextView;
        TextView commentCountTextView;
        SimpleDraweeView newsImageView;
        RelativeLayout listenButton;
        TextView listenButtonText;
        ImageView listenButtonIcon;
        Indicator voiceIndicator;

        public NewsViewHolder(View view) {
            super(view);

            titleTextView = view.findViewById(R.id.news_title);
            contentTextView = view.findViewById(R.id.news_content);
            publisherTextView = view.findViewById(R.id.publish_name);
            publishTimeTextView = view.findViewById(R.id.publish_time);
            commentCountTextView = view.findViewById(R.id.comment_count);
            newsImageView = view.findViewById(R.id.news_image);
            listenButton = view.findViewById(R.id.listen_button);
            listenButtonText = view.findViewById(R.id.listen_button_text);
            listenButtonIcon = view.findViewById(R.id.listen_button_icon);
            voiceIndicator = view.findViewById(R.id.voice_indicator);
        }
    }

    public void showLoadMoreLoading() {
        if (mLoadMoreViewHolder != null) {
            mLoadMoreViewHolder.showLoading();
        }
    }

    public void showLoadMoreEmpty() {
        if (mLoadMoreViewHolder != null) {
            mLoadMoreViewHolder.showEmpty();
        }
    }

    public void resetLoadMoreState() {
        if (mLoadMoreViewHolder != null) {
            mLoadMoreViewHolder.reset();
        }
    }

    private boolean mShowFooter = true;
    private int mStatus = STATUS_REFRESH;
    private ILoadMore mLoadMoreListener;
    private IRefresh mRefreshListener;

    public boolean isResetStatue() {
        return mStatus == STATUS_NONE;
    }

    public void setLoadMoreListener(ILoadMore loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public void setRefreshListener(IRefresh refreshListener) {
        this.mRefreshListener = refreshListener;
    }

    private class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        TextView noMoreTextView;
        AVLoadingIndicatorView loadingView;

        LoadMoreViewHolder(View itemView) {
            super(itemView);

            noMoreTextView = itemView.findViewById(R.id.no_more_textview);
            loadingView = itemView.findViewById(R.id.loading_view);
        }

        void bind() {
            if (mStatus == STATUS_NONE) {
                //处于普通状态, 尝试加载
                if (mLoadMoreListener != null) {
                    showLoading();
                    mLoadMoreListener.loadMore();
                }
            } else if (mStatus == STATUS_REFRESH) {
                if (mRefreshListener != null) {
                    showLoading();
                    mRefreshListener.refresh();
                }
            }
        }

        private void showLoading() {
            noMoreTextView.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
            loadingView.show();
            mStatus = STATUS_LOADING;
        }

        private void showEmpty() {
            noMoreTextView.setVisibility(View.VISIBLE);
            loadingView.hide();
            loadingView.setVisibility(View.GONE);
            mStatus = STATUS_EMPTY;
        }

        private void reset() {
            noMoreTextView.setVisibility(View.GONE);
            loadingView.hide();
            loadingView.setVisibility(View.GONE);
            mStatus = STATUS_NONE;
        }
    }

    public interface ILoadMore {
        void loadMore();
    }

    public interface IRefresh {
        void refresh();
    }
}
