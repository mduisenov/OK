package ru.ok.android.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import ru.ok.android.utils.Logger;

public class ImageViewSafeDrawWithAlpha extends ImageView implements AnimatorListener, AnimatorUpdateListener {
    protected ValueAnimator alphaAnimator;
    protected Drawable alphaDrawable;
    private boolean animateNestedLayerDrawable;
    private int animationDuration;
    protected Drawable imageDrawable;
    private boolean isAlpha;

    public ImageViewSafeDrawWithAlpha(Context context) {
        super(context);
        this.isAlpha = false;
        this.animationDuration = 400;
    }

    public ImageViewSafeDrawWithAlpha(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isAlpha = false;
        this.animationDuration = 400;
    }

    public ImageViewSafeDrawWithAlpha(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.isAlpha = false;
        this.animationDuration = 400;
    }

    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }

    public void draw(Canvas canvas) {
        try {
            super.draw(canvas);
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }

    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    public void setIsAlpha(boolean isAlpha) {
        this.isAlpha = isAlpha;
    }

    private boolean imageHasChanged(Drawable image) {
        Drawable previousDrawable = getDrawable();
        if (previousDrawable == image) {
            return false;
        }
        if (previousDrawable != null && previousDrawable.equals(image)) {
            return false;
        }
        if ((previousDrawable instanceof BitmapDrawable) && (image instanceof BitmapDrawable) && ((BitmapDrawable) previousDrawable).getBitmap().equals(((BitmapDrawable) image).getBitmap())) {
            return false;
        }
        return true;
    }

    public void setImageDrawable(Drawable image) {
        setImage(image, this.isAlpha);
    }

    public void setImageDrawable(Drawable image, boolean animate) {
        setImage(image, animate);
    }

    protected void setImage(Drawable image, boolean animate) {
        if (imageHasChanged(image)) {
            if (this.alphaAnimator != null) {
                this.alphaAnimator.cancel();
                this.alphaAnimator = null;
            }
            this.imageDrawable = image;
            if (!animate || image == null) {
                super.setImageDrawable(image);
                onFinishedSetDrawable(image);
                return;
            }
            image.mutate();
            image.setAlpha(0);
            Drawable previousDrawable = getDrawable();
            this.animateNestedLayerDrawable = previousDrawable != null;
            if (previousDrawable == null) {
                this.alphaDrawable = image;
            } else {
                this.alphaDrawable = new LayerDrawable(new Drawable[]{previousDrawable, image});
            }
            this.alphaAnimator = ValueAnimator.ofInt(new int[]{0, MotionEventCompat.ACTION_MASK});
            this.alphaAnimator.setDuration((long) this.animationDuration);
            this.alphaAnimator.addUpdateListener(this);
            this.alphaAnimator.addListener(this);
            this.alphaAnimator.start();
            super.setImageDrawable(this.alphaDrawable);
        }
    }

    protected void onFinishedSetDrawable(Drawable drawable) {
    }

    public final void onAnimationUpdate(ValueAnimator animation) {
        Integer alpha = (Integer) animation.getAnimatedValue();
        Drawable animatable = this.alphaDrawable;
        if (this.animateNestedLayerDrawable && animatable != null) {
            animatable = ((LayerDrawable) this.alphaDrawable).getDrawable(1);
        }
        animatable.setAlpha(alpha.intValue());
        invalidate();
    }

    public final void onAnimationStart(Animator animation) {
    }

    public final void onAnimationRepeat(Animator animation) {
    }

    public final void onAnimationEnd(Animator animation) {
        if (this.alphaAnimator != null) {
            Drawable relevant = this.alphaDrawable;
            if (this.animateNestedLayerDrawable && relevant != null) {
                relevant = ((LayerDrawable) this.alphaDrawable).getDrawable(1);
            }
            if (relevant != null) {
                relevant.setAlpha(MotionEventCompat.ACTION_MASK);
            }
            this.alphaAnimator = null;
            this.alphaDrawable = null;
            super.setImageDrawable(relevant);
            onFinishedSetDrawable(relevant);
        }
    }

    public final void onAnimationCancel(Animator animation) {
    }

    public Drawable getImageDrawable() {
        return this.imageDrawable;
    }
}
