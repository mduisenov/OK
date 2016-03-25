package ru.ok.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

public final class FrameInterceptTouchListenerLayout extends FrameLayout {
    private OnTouchListener interceptTouchListener;

    public FrameInterceptTouchListenerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setInterceptTouchListener(OnTouchListener interceptTouchListener) {
        this.interceptTouchListener = interceptTouchListener;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.interceptTouchListener == null || !this.interceptTouchListener.onTouch(this, ev)) {
            return super.onInterceptTouchEvent(ev);
        }
        return true;
    }
}
