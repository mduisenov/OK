package ru.ok.android.ui.custom.imageview;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ViewParent;

public class HorizontalSlidingPanoramaHelper extends SimpleOnGestureListener implements AnimatorListener {
    private final ValueAnimator animator;
    private final GestureDetector gestureDetector;
    private boolean imageDimensionsSet;
    private int imageHeight;
    private int imageWidth;
    private PointF lastPoint;
    private float leftBorder;
    private float rightBorder;
    private Slidable slidable;
    private float translate;
    private boolean viewDimesionsSet;
    private int viewHeight;
    private int viewWidth;

    /* renamed from: ru.ok.android.ui.custom.imageview.HorizontalSlidingPanoramaHelper.1 */
    class C06571 implements AnimatorUpdateListener {
        final /* synthetic */ float val$size;

        C06571(float f) {
            this.val$size = f;
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            HorizontalSlidingPanoramaHelper.this.translate = ((Float) valueAnimator.getAnimatedValue()).floatValue() * this.val$size;
            if (HorizontalSlidingPanoramaHelper.this.rightBorder != 0.0f) {
                HorizontalSlidingPanoramaHelper.this.slidable.onTranslate(HorizontalSlidingPanoramaHelper.this.translate / Math.abs(HorizontalSlidingPanoramaHelper.this.rightBorder));
            }
        }
    }

    public interface Slidable {
        Context getContext();

        ViewParent getParent();

        void onTranslate(float f);

        boolean performClick();

        void setPressed(boolean z);
    }

    public HorizontalSlidingPanoramaHelper(Slidable slidable) {
        this.lastPoint = new PointF();
        this.slidable = slidable;
        this.gestureDetector = new GestureDetector(slidable.getContext(), this);
        this.animator = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        this.animator.addListener(this);
        this.translate = 0.0f;
    }

    public boolean onTouch(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        this.animator.cancel();
        this.animator.removeAllUpdateListeners();
        if (event.getAction() == 0) {
            this.lastPoint.x = event.getX();
            this.lastPoint.y = event.getY();
            return false;
        }
        if (event.getAction() == 2) {
            if (Math.abs(event.getX() - this.lastPoint.x) > Math.abs(event.getY() - this.lastPoint.y)) {
                this.translate += event.getX() - this.lastPoint.x;
                if (this.translate > this.rightBorder) {
                    this.translate = this.rightBorder;
                } else if (this.translate < this.leftBorder) {
                    this.translate = this.leftBorder;
                }
                if (this.rightBorder != 0.0f) {
                    this.slidable.onTranslate(this.translate / Math.abs(this.rightBorder));
                }
                this.lastPoint.x = event.getX();
                this.lastPoint.y = event.getY();
                this.slidable.getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            }
        } else if (event.getAction() == 1) {
            this.slidable.setPressed(false);
            return true;
        }
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        float size = this.translate;
        this.animator.cancel();
        this.animator.removeAllUpdateListeners();
        this.animator.addUpdateListener(new C06571(size));
        this.animator.setDuration((long) (300.0f * (Math.abs(this.translate) / this.rightBorder)));
        this.animator.start();
        return true;
    }

    public void onAnimationStart(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        this.slidable.performClick();
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void setViewDimensions(int viewHeight, int viewWidth) {
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;
        this.viewDimesionsSet = true;
        init();
    }

    private void init() {
        float f = 0.0f;
        if (this.viewDimesionsSet && this.imageDimensionsSet) {
            float f2;
            this.translate = 0.0f;
            float dx = ((((float) this.imageWidth) * (((float) this.viewHeight) / ((float) this.imageHeight))) - ((float) this.viewWidth)) / 2.0f;
            if (dx > 0.0f) {
                f2 = dx;
            } else {
                f2 = 0.0f;
            }
            this.rightBorder = f2;
            if (dx > 0.0f) {
                f = -dx;
            }
            this.leftBorder = f;
        }
    }

    public void setImageDimensions(int height, int width) {
        this.imageHeight = height;
        this.imageWidth = width;
        this.imageDimensionsSet = true;
        init();
    }
}
