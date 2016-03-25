package ru.ok.android.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class LinearLayoutObserveSize extends LinearLayout {
    public OnSizeChangedListener changedListener;

    @SuppressLint({"NewApi"})
    public LinearLayoutObserveSize(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.changedListener = null;
    }

    public LinearLayoutObserveSize(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.changedListener = null;
    }

    public LinearLayoutObserveSize(Context context) {
        super(context);
        this.changedListener = null;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.changedListener == null) {
            return;
        }
        if (w != oldw || h != oldh) {
            this.changedListener.onSizeChanged(w, h, oldw, oldh);
        }
    }
}
