package ru.ok.android.fresco;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

public class FrescoMaxWidthView extends SimpleDraweeView {
    private int maximumWidth;

    public FrescoMaxWidthView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        this.maximumWidth = Integer.MAX_VALUE;
    }

    public FrescoMaxWidthView(Context context) {
        super(context);
        this.maximumWidth = Integer.MAX_VALUE;
    }

    public FrescoMaxWidthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.maximumWidth = Integer.MAX_VALUE;
    }

    public FrescoMaxWidthView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.maximumWidth = Integer.MAX_VALUE;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.maximumWidth != Integer.MAX_VALUE) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getMode(widthMeasureSpec) == 0 ? this.maximumWidth : Math.min(this.maximumWidth, MeasureSpec.getSize(widthMeasureSpec)), 1073741824);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMaximumWidth(int maximumWidth) {
        this.maximumWidth = maximumWidth;
    }
}
