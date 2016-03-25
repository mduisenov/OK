package ru.ok.android.ui.custom.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;

public final class FrameSquareLayout extends FrameLayout {
    public FrameSquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int spec = MeasureSpec.makeMeasureSpec(Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec)), 1073741824);
        super.onMeasure(spec, spec);
    }
}
