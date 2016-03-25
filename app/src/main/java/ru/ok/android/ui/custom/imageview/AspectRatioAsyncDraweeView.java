package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.util.AttributeSet;
import ru.ok.android.ui.custom.AspectRatioMeasureHelper;

@Deprecated
public class AspectRatioAsyncDraweeView extends AsyncDraweeView {
    private final AspectRatioMeasureHelper armHelper;

    public AspectRatioAsyncDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.armHelper = new AspectRatioMeasureHelper(this, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.armHelper.updateSpecs(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(this.armHelper.getWidthSpec(), this.armHelper.getHeightSpec());
    }

    public void setWidthHeightRatio(float widthHeightRatio) {
        this.armHelper.setAspectRatio(widthHeightRatio);
    }
}
