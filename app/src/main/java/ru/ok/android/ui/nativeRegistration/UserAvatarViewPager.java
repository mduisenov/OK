package ru.ok.android.ui.nativeRegistration;

import android.content.Context;
import android.support.v4.view.ViewPagerExposed;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class UserAvatarViewPager extends ViewPagerExposed {
    private int prevItem;
    private boolean touchEnabled;

    public void setTouchEnabled(boolean touchEnabled) {
        this.touchEnabled = touchEnabled;
    }

    public UserAvatarViewPager(Context context) {
        super(context);
        this.prevItem = 0;
        this.touchEnabled = true;
    }

    public void selectCurrentView() {
        UserAvatar currentView = (UserAvatar) findViewWithTag(Integer.valueOf(getCurrentItem()));
        if (currentView != null) {
            currentView.selectView();
        }
    }

    protected void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
        super.setCurrentItemInternal(item, smoothScroll, always);
        selectCurrentItem(item);
    }

    protected void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, int velocity) {
        super.setCurrentItemInternal(item, smoothScroll, always, velocity);
        selectCurrentItem(item);
    }

    private void selectCurrentItem(int position) {
        UserAvatar prevView = (UserAvatar) findViewWithTag(Integer.valueOf(this.prevItem));
        if (prevView != null) {
            prevView.unselectView();
        }
        UserAvatar currentView = (UserAvatar) findViewWithTag(Integer.valueOf(position));
        if (currentView != null) {
            currentView.selectView();
        }
        this.prevItem = position;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return !this.touchEnabled || super.onTouchEvent(ev);
    }

    public UserAvatarViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.prevItem = 0;
        this.touchEnabled = true;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !this.touchEnabled || super.onInterceptTouchEvent(ev);
    }
}
