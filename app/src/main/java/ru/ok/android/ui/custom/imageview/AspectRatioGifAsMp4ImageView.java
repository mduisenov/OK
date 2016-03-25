package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.util.AttributeSet;
import bo.pic.android.media.view.AnimatedMediaContentView;
import ru.ok.android.ui.custom.AspectRatioMeasureHelper;

public class AspectRatioGifAsMp4ImageView extends AnimatedMediaContentView {
    private final AspectRatioMeasureHelper mAspectRationMeasureHelper;

    public AspectRatioGifAsMp4ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAspectRationMeasureHelper = new AspectRatioMeasureHelper(this, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mAspectRationMeasureHelper.updateSpecs(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(this.mAspectRationMeasureHelper.getWidthSpec(), this.mAspectRationMeasureHelper.getHeightSpec());
    }

    public void setWidthHeightRatio(float aspectRatio) {
        this.mAspectRationMeasureHelper.setAspectRatio(aspectRatio);
    }

    public void setMaximumWidth(int maximumWidth) {
        this.mAspectRationMeasureHelper.setMaxWidth(maximumWidth);
    }
}
