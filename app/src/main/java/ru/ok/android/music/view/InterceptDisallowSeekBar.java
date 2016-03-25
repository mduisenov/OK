package ru.ok.android.music.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.SeekBar;

public class InterceptDisallowSeekBar extends SeekBar {
    public InterceptDisallowSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(MotionEvent event) {
        ViewParent parent = getParent();
        if (parent instanceof ViewGroup) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(event);
    }
}
