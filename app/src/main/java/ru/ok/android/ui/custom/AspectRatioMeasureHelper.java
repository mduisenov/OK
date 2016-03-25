package ru.ok.android.ui.custom;

import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import ru.ok.android.C0206R;

public class AspectRatioMeasureHelper {
    private float aspectRatio;
    protected int heightSpec;
    private int maxHeight;
    private int maxWidth;
    final View targetView;
    protected int widthSpec;

    public AspectRatioMeasureHelper(View targetView, AttributeSet attrs) {
        this(targetView, attrs, 0, 0);
    }

    public AspectRatioMeasureHelper(View targetView, AttributeSet attrs, int defAttrId, int defStyleId) {
        TypedArray a = targetView.getContext().obtainStyledAttributes(attrs, C0206R.styleable.AspectRatioView, defAttrId, defStyleId);
        this.aspectRatio = a.getFloat(0, 2.0f);
        this.aspectRatio = Math.max(Math.abs(this.aspectRatio), 0.01f);
        this.maxHeight = a.getDimensionPixelSize(1, Integer.MAX_VALUE);
        this.maxWidth = a.getDimensionPixelSize(2, Integer.MAX_VALUE);
        a.recycle();
        this.targetView = targetView;
        this.widthSpec = MeasureSpec.makeMeasureSpec(0, 0);
        this.heightSpec = MeasureSpec.makeMeasureSpec(0, 0);
    }

    public int getWidthSpec() {
        return this.widthSpec;
    }

    public int getHeightSpec() {
        return this.heightSpec;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = Math.max(Math.abs(aspectRatio), 0.01f);
        this.targetView.requestLayout();
    }

    public float getAspectRatio() {
        return this.aspectRatio;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        this.targetView.requestLayout();
    }

    public void updateSpecs(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (heightMode == 1073741824) {
            this.widthSpec = widthMeasureSpec;
            this.heightSpec = heightMeasureSpec;
            return;
        }
        int widthSize;
        if (widthMode == 0) {
            widthSize = this.maxWidth;
        } else {
            widthSize = Math.min(this.maxWidth, MeasureSpec.getSize(widthMeasureSpec));
        }
        int maxHeight = heightMode == LinearLayoutManager.INVALID_OFFSET ? Math.min(this.maxHeight, MeasureSpec.getSize(heightMeasureSpec)) : this.maxHeight;
        int aspectHeight = Math.min(((int) (((float) Math.max(0, widthSize - getTotalHPadding())) / this.aspectRatio)) + getTotalVPadding(), maxHeight);
        this.widthSpec = MeasureSpec.makeMeasureSpec(widthSize, 1073741824);
        this.heightSpec = MeasureSpec.makeMeasureSpec(aspectHeight, 1073741824);
    }

    int getTotalVPadding() {
        return this.targetView.getPaddingTop() + this.targetView.getPaddingBottom();
    }

    int getTotalHPadding() {
        return this.targetView.getPaddingLeft() + this.targetView.getPaddingRight();
    }
}
