package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.util.AttributeSet;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;

public class ImageRoundPressedView extends ImageRoundView {
    private RoundedColorDrawable overlayDrawable;

    public ImageRoundPressedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.overlayDrawable = new RoundedColorDrawable(getResources().getColor(2131492919), 0.0f);
        setHierarchy(new GenericDraweeHierarchyBuilder(getResources()).setPressedStateOverlay(this.overlayDrawable).build());
    }

    public void setStroke(float stroke) {
        super.setStroke(stroke);
        this.overlayDrawable.setMargin(stroke);
    }

    public void setShadowStroke(float stroke) {
        super.setShadowStroke(stroke);
        this.overlayDrawable.setMargin(stroke);
    }
}
