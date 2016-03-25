package ru.ok.android.ui.custom;

import android.util.AttributeSet;
import android.view.View;

public class AspectRatioWithExtraPaddingsMeasureHelper extends AspectRatioMeasureHelper {
    protected int extraLeftPadding;
    protected int extraRightPadding;

    public AspectRatioWithExtraPaddingsMeasureHelper(View targetView, AttributeSet attrs, int defAttrId, int defStyleId) {
        super(targetView, attrs, defAttrId, defStyleId);
    }

    public void setExtraLeftPadding(int extraLeftPadding) {
        this.extraLeftPadding = extraLeftPadding;
    }

    public void setExtraRightPadding(int extraRightPadding) {
        this.extraRightPadding = extraRightPadding;
    }

    int getTotalHPadding() {
        return (super.getTotalHPadding() + this.extraLeftPadding) + this.extraRightPadding;
    }
}
