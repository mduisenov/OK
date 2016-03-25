package ru.ok.android.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AnimationImageView extends ImageView {
    private AnimationCompleteListener animationListener;

    interface AnimationCompleteListener {
        void onAnimationEnd();
    }

    public AnimationImageView(Context context) {
        super(context);
    }

    public AnimationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimationImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onAnimationEnd() {
        super.onAnimationEnd();
        if (this.animationListener != null) {
            this.animationListener.onAnimationEnd();
        }
    }

    public void setAnimationListener(AnimationCompleteListener animationListener) {
        this.animationListener = animationListener;
    }
}
