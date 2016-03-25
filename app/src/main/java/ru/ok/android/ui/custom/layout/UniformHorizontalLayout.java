package ru.ok.android.ui.custom.layout;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

public final class UniformHorizontalLayout extends ViewGroup {
    private final int columns;

    public UniformHorizontalLayout(Context context, int columns) {
        super(context);
        this.columns = columns;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int childWidthSpec = MeasureSpec.makeMeasureSpec(((width - getPaddingLeft()) - getPaddingRight()) / this.columns, 1073741824);
        int maxMeasuredHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            childAt.measure(childWidthSpec, heightMeasureSpec);
            if (childAt.getMeasuredHeight() > maxMeasuredHeight) {
                maxMeasuredHeight = childAt.getMeasuredHeight();
            }
        }
        setMeasuredDimension(width, maxMeasuredHeight);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = (((r - l) - getPaddingLeft()) - getPaddingRight()) + 1;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            int x = getPaddingLeft() + ((i * width) / this.columns);
            childAt.layout(x, 0, childAt.getMeasuredWidth() + x, childAt.getMeasuredHeight());
        }
    }
}
