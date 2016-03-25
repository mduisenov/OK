package ru.ok.android.ui.custom.emptyview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class SmartProgressView extends ProgressBar {
    public SmartProgressView(Context context) {
        super(context);
    }

    public SmartProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
