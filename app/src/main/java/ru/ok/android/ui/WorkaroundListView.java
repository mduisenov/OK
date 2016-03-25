package ru.ok.android.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class WorkaroundListView extends ListView {
    private boolean isAttachedToWindow;

    public WorkaroundListView(Context context) {
        this(context, null);
    }

    public WorkaroundListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WorkaroundListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isAttachedToWindow = false;
        if (VERSION.SDK_INT >= 21) {
            setNestedScrollingEnabled(true);
        }
    }

    @TargetApi(21)
    public WorkaroundListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.isAttachedToWindow = false;
    }

    protected void onAttachedToWindow() {
        this.isAttachedToWindow = true;
        super.onAttachedToWindow();
    }

    protected void onDetachedFromWindow() {
        if (VERSION.SDK_INT >= 18 || this.isAttachedToWindow) {
            super.onDetachedFromWindow();
        } else {
            try {
                super.onDetachedFromWindow();
            } catch (Exception e) {
            }
        }
        this.isAttachedToWindow = false;
    }

    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
}
