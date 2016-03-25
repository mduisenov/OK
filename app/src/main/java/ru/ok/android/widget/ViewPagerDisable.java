package ru.ok.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import ru.ok.android.ui.tabbar.TabbarViewPager;

public class ViewPagerDisable extends TabbarViewPager {
    boolean enableScroll;

    public ViewPagerDisable(Context context) {
        super(context);
        this.enableScroll = true;
    }

    public ViewPagerDisable(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enableScroll = true;
    }

    public void setEnableScroll(boolean enableScroll) {
        this.enableScroll = enableScroll;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.enableScroll || ev.getAction() != 0) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    public boolean onTrackballEvent(MotionEvent event) {
        if (this.enableScroll) {
            return super.onTrackballEvent(event);
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (this.enableScroll || ev.getAction() != 0) {
            return super.onTouchEvent(ev);
        }
        return false;
    }
}
