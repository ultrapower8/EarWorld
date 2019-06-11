package com.nickole.earworld.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout;

/**
 * @author shuzijie
 * @date 2019-05-16.
 */
public class PlusSuperSwipeRefreshLayout extends SuperSwipeRefreshLayout {

    public PlusSuperSwipeRefreshLayout(Context context) {
        super(context);
    }

    public PlusSuperSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isChildScrollToBottom() {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isRefreshing()) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }
}
