package ru.ok.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class DebugRelativeLayout extends RelativeLayout {
    public DebugRelativeLayout(Context context) {
        super(context);
    }

    public DebugRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DebugRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
