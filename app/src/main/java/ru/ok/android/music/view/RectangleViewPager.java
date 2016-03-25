package ru.ok.android.music.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class RectangleViewPager extends ViewPager {
    public RectangleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RectangleViewPager(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
