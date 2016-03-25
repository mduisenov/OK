package ru.ok.android.ui.fragments.messages.view;

import android.view.View;
import android.view.View.MeasureSpec;

public final class ListEmptyFooterView extends View {
    private int computedHeight;

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), this.computedHeight);
    }
}
