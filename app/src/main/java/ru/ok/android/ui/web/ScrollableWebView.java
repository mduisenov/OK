package ru.ok.android.ui.web;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.custom.scroll.ScrollTopView.OnClickScrollListener;
import ru.ok.android.ui.fragments.messages.view.SelectableWebView;
import ru.ok.android.ui.stream.view.StreamScrollTopView;
import ru.ok.android.ui.tabbar.HideTabbarListener;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.StackTraceUtils;
import ru.ok.android.utils.animation.WebViewUtils;

public class ScrollableWebView extends SelectableWebView implements OnLongClickListener, OnClickScrollListener {
    private HideTabbarListener hideTabbarListener;
    private int latestTop;
    protected StreamScrollTopView scrollTopView;

    /* renamed from: ru.ok.android.ui.web.ScrollableWebView.1 */
    class C14121 implements OnClickListener {
        C14121() {
        }

        public void onClick(View v) {
            ScrollableWebView.this.onScrollTopClick(ScrollableWebView.this.scrollTopView.getNewEventsCount());
        }
    }

    public ScrollableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.latestTop = -1;
        setOnLongClickListener(this);
    }

    public void setScrollTopView(StreamScrollTopView view) {
        this.scrollTopView = view;
        if (this.scrollTopView != null) {
            this.scrollTopView.setOnClickListener(new C14121());
        }
    }

    public void scrollTo(int x, int y) {
        if (!StackTraceUtils.stackTraceContainsMethod("scrollEditIntoView")) {
            super.scrollTo(x, y);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.hideTabbarListener.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    protected void onScrollChanged(int l, int top, int oldl, int oldTop) {
        super.onScrollChanged(l, top, oldl, oldTop);
        int diff = top - this.latestTop;
        if (this.hideTabbarListener != null) {
            this.hideTabbarListener.onScroll(-diff, top, computeVerticalScrollRange() - getHeight(), getHeight());
        }
        this.latestTop = top;
        if (isShowScrollView()) {
            boolean wantToShow;
            boolean wantToHide;
            boolean atTop = top == 0;
            if (atTop || oldTop <= 2 || diff >= -1) {
                wantToShow = false;
            } else {
                wantToShow = true;
            }
            if (atTop || (diff > 1 && oldTop > 10)) {
                wantToHide = true;
            } else {
                wantToHide = false;
            }
            this.scrollTopView.onScroll(wantToShow, wantToHide, true, false);
        }
    }

    private boolean isShowScrollView() {
        if (((double) (((float) getContentHeight()) / ((float) getHeight()))) > 1.6d) {
            return true;
        }
        return false;
    }

    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (Exception e) {
            Logger.m173d("WebView Detached :%s", "Stopped a scroll crash");
        }
    }

    public void onScrollTopClick(int count) {
        if (count == 0) {
            WebViewUtils.scrollPositionToTop(this);
            return;
        }
        StatisticManager.getInstance().addStatisticEvent("refresh_bubble", new Pair[0]);
        refreshEvents();
        this.scrollTopView.setNewEventCount(0);
    }

    protected void refreshEvents() {
    }

    public int publicComputeVerticalScrollRange() {
        return computeVerticalScrollRange();
    }

    public void setHideTabbarListener(HideTabbarListener hideTabbarListener) {
        this.hideTabbarListener = hideTabbarListener;
    }
}
