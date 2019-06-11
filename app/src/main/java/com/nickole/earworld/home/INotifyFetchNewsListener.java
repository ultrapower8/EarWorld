package com.nickole.earworld.home;

import com.nickole.earworld.entity.News;

import java.util.List;

/**
 * @author shuzijie
 * @date 2019-05-15.
 */
public interface INotifyFetchNewsListener {
    void onRefreshFailed(String msg);

    void onLoadMoreFailed(String msg);

    void onRefreshSuccess(List<News> news, boolean hasNext, String pageToken);

    void onLoadMoreSuccess(List<News> news, boolean hasNext, String pageToken);
}
