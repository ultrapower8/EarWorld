package com.nickole.earworld.event;

import com.nickole.earworld.entity.News;
import com.nickole.earworld.home.IUpdateCurNewsItemListener;

/**
 * @author shuzijie
 * @date 2019-06-04.
 */
public class PlayNewsVoiceEvent {
    private News mNews;

    public IUpdateCurNewsItemListener getUpdateCurNewsItemListener() {
        return iUpdateCurNewsItemListener;
    }

    public void setUpdateCurNewsItemListener(IUpdateCurNewsItemListener listener) {
        this.iUpdateCurNewsItemListener = listener;
    }

    private IUpdateCurNewsItemListener iUpdateCurNewsItemListener;

    public PlayNewsVoiceEvent(News news){
        mNews = news;
    }

    public News getNews() {
        return mNews;
    }
}
