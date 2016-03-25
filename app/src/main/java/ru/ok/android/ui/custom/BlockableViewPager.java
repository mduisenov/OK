package ru.ok.android.ui.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class BlockableViewPager extends ViewPager {
    private boolean mBlocked;

    public BlockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlockableViewPager(Context context) {
        super(context);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.mBlocked) {
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public final void setBlocked(boolean blocked) {
        this.mBlocked = blocked;
    }
}
