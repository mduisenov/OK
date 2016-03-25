package ru.ok.android.ui.custom;

import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class AnimatedScaleDrawable extends WrapperDrawable implements Animatable, AnimationListener {
    private static final Interpolator INTERPOLATOR;
    private final Animation animation;
    private float currentScale;
    private final View parent;
    private boolean running;

    /* renamed from: ru.ok.android.ui.custom.AnimatedScaleDrawable.1 */
    class C06011 extends Animation {
        C06011() {
        }

        protected void applyTransformation(float interpolatedTime, Transformation t) {
            AnimatedScaleDrawable.this.setScale(1.0f + (0.5f * interpolatedTime));
        }
    }

    static {
        INTERPOLATOR = new LinearInterpolator();
    }

    public AnimatedScaleDrawable(Drawable baseDrawable, View parent) {
        super(baseDrawable);
        this.currentScale = 1.0f;
        this.parent = parent;
        Animation a = new C06011();
        a.setRepeatCount(1);
        a.setRepeatMode(2);
        a.setInterpolator(INTERPOLATOR);
        a.setDuration(250);
        a.setAnimationListener(this);
        this.animation = a;
    }

    private void setScale(float scale) {
        this.currentScale = scale;
        invalidateSelf();
    }

    public void start() {
        this.animation.reset();
        this.parent.startAnimation(this.animation);
    }

    public void stop() {
        this.parent.clearAnimation();
        setScale(1.0f);
    }

    public boolean isRunning() {
        return this.running;
    }

    public void draw(Canvas c) {
        int saveCount = c.save();
        c.scale(this.currentScale, this.currentScale, getBounds().exactCenterX(), getBounds().exactCenterY());
        this.baseDrawable.draw(c);
        c.restoreToCount(saveCount);
    }

    public void onAnimationStart(Animation animation) {
        this.running = true;
    }

    public void onAnimationEnd(Animation animation) {
        this.running = false;
    }

    public void onAnimationRepeat(Animation animation) {
    }
}
