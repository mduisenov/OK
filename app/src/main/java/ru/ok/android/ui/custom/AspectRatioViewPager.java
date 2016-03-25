package ru.ok.android.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import ru.ok.android.ui.custom.photo.StableViewPager;
import ru.ok.android.utils.Logger;

public class AspectRatioViewPager extends StableViewPager {
    final AspectRatioMeasureHelper armHelper;
    final float initialAspectRatio;

    public AspectRatioViewPager(Context context) {
        this(context, null);
    }

    public AspectRatioViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.armHelper = new AspectRatioMeasureHelper(this, attrs);
        this.initialAspectRatio = this.armHelper.getAspectRatio();
    }

    public AspectRatioViewPager(Context context, AttributeSet attrs, int defAttrId, int defStyleId) {
        super(context, attrs);
        this.armHelper = new AspectRatioMeasureHelper(this, attrs, defAttrId, defStyleId);
        this.initialAspectRatio = this.armHelper.getAspectRatio();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Logger.m172d("onMeasure >>> width=" + MeasureSpec.toString(widthMeasureSpec) + ", height=" + MeasureSpec.toString(heightMeasureSpec));
        this.armHelper.updateSpecs(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(this.armHelper.getWidthSpec(), this.armHelper.getHeightSpec());
        Logger.m172d("onMeasure <<< measured width=" + getMeasuredWidth() + ", height=" + getMeasuredHeight());
    }

    public float getInitialAspectRatio() {
        return this.initialAspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.armHelper.setAspectRatio(aspectRatio);
    }

    public float getAspectRatio() {
        return this.armHelper.getAspectRatio();
    }
}
