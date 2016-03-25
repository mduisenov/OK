package ru.ok.android.ui.custom.photo;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class RotateGridView extends TiltGridView implements AnimatorUpdateListener {
    private float alpha;
    private int rotation;

    public RotateGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RotateGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateGridView(Context context) {
        super(context);
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        setTilesTransformation(((Float) animation.getAnimatedValue("palpha")).floatValue(), ((Integer) animation.getAnimatedValue("protation")).intValue());
    }

    public void setTilesTransformation(float alpha, int rotation) {
        if (this.rotation != rotation || this.alpha != alpha) {
            this.rotation = rotation;
            this.alpha = alpha;
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                child.setPivotX((float) (child.getMeasuredWidth() / 2));
                child.setPivotY((float) (child.getMeasuredHeight() / 2));
                child.setRotationX((float) rotation);
                child.setAlpha(alpha);
            }
        }
    }
}
