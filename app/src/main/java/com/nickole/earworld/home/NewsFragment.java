package com.nickole.earworld.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout;
import com.nickole.earworld.R;
import com.nickole.earworld.api.NewsApi;
import com.nickole.earworld.entity.News;
import com.nickole.earworld.view.PlusSuperSwipeRefreshLayout;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author shuzijie
 * @date 2019-05-13.
 */
public class NewsFragment extends Fragment implements INotifyFetchNewsListener {
    @BindView(R.id.news_recyclerview)
    RecyclerView newsRecyclerview;
    NewsAdapter newsAdapter;
    Unbinder unbinder;
    @BindView(R.id.swipe_refresh)
    PlusSuperSwipeRefreshLayout swipeRefresh;
    AVLoadingIndicatorView refreshLoadingView;
    String nextPageToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        unbinder = ButterKnife.bind(this, view);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        newsRecyclerview.setLayoutManager(layoutManager);
        newsAdapter = new NewsAdapter(getActivity());

        newsAdapter.setLoadMoreListener(new NewsAdapter.ILoadMore() {
            @Override
            public void loadMore() {
                NewsApi.fetchNewsList(NewsFragment.this, true, nextPageToken);
            }
        });

        newsAdapter.setRefreshListener(new NewsAdapter.IRefresh() {
            @Override
            public void refresh() {
                NewsApi.fetchNewsList(NewsFragment.this, false, null);
            }
        });

        newsRecyclerview.setAdapter(newsAdapter);

        View refreshView = inflater.inflate(R.layout.view_refresh_loading, swipeRefresh, true);
        View refreshLoadingLayout = refreshView.findViewById(R.id.refresh_loading_layout);
        refreshLoadingView = refreshView.findViewById(R.id.refresh_loading_view);
        ((ViewGroup)refreshLoadingLayout.getParent()).removeView(refreshLoadingLayout);
        swipeRefresh.setHeaderView(refreshLoadingLayout);
        swipeRefresh.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                if (refreshLoadingView != null) {
                    refreshLoadingView.show();
                }
                if (newsAdapter != null) {
                    newsAdapter.resetLoadMoreState();
                }
                refreshNewsList();
            }

            @Override
            public void onPullDistance(int i) {
                if (refreshLoadingView != null) {
                    refreshLoadingView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPullEnable(boolean b) {

            }
        });
        return view;
    }

    public void refreshNewsList() {
        NewsApi.fetchNewsList(this, false, null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefreshFailed(String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (refreshLoadingView != null) {
                    refreshLoadingView.hide();
                }
                swipeRefresh.setRefreshing(false);
                newsAdapter.resetLoadMoreState();
                if (newsAdapter != null && newsAdapter.getNewsItemSize() == 0) {
                    newsAdapter.showLoadMoreEmpty();
                }
            }
        });
    }

    @Override
    public void onLoadMoreFailed(String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newsAdapter.resetLoadMoreState();
                if (newsAdapter != null) {
                    newsAdapter.showLoadMoreEmpty();
                }
            }
        });
    }

    @Override
    public void onRefreshSuccess(final List<News> news, final boolean hasNext, String pageToken) {
        nextPageToken = pageToken;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (refreshLoadingView != null) {
                    refreshLoadingView.hide();
                }
                swipeRefresh.setRefreshing(false);
                if (newsAdapter != null) {
                    newsAdapter.setNewsList(news);
                    newsAdapter.notifyDataSetChanged();
                    newsAdapter.resetLoadMoreState();
                    if (!hasNext) {
                        newsAdapter.showLoadMoreEmpty();
                    }
                }
            }
        });
    }

    @Override
    public void onLoadMoreSuccess(final List<News> news, final boolean hasNext, String pageToken) {
        nextPageToken = pageToken;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (newsAdapter != null) {
                    int fromPosition = newsAdapter.getNewsItemSize();
                    newsAdapter.getNewsList().addAll(news);
                    newsAdapter.notifyItemRangeInserted(fromPosition, news.size());
                    newsAdapter.resetLoadMoreState();
                    if (!hasNext) {
                        newsAdapter.showLoadMoreEmpty();
                    }
                }
            }
        });

    }
}
