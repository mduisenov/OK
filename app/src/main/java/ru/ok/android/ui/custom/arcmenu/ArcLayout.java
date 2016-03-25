package ru.ok.android.ui.custom.arcmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import ru.ok.android.C0206R;

public class ArcLayout extends ViewGroup {
    private int mChildPadding;
    private int mChildSize;
    private boolean mExpanded;
    private float mFromDegrees;
    private int mLayoutPadding;
    private int mRadius;
    private float mToDegrees;

    /* renamed from: ru.ok.android.ui.custom.arcmenu.ArcLayout.1 */
    class C06401 implements AnimationListener {
        final /* synthetic */ boolean val$isLast;

        /* renamed from: ru.ok.android.ui.custom.arcmenu.ArcLayout.1.1 */
        class C06391 implements Runnable {
            C06391() {
            }

            public void run() {
                ArcLayout.this.onAllAnimationsEnd();
            }
        }

        C06401(boolean z) {
            this.val$isLast = z;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            if (this.val$isLast) {
                ArcLayout.this.postDelayed(new C06391(), 0);
            }
        }
    }

    public ArcLayout(Context context) {
        super(context);
        this.mChildPadding = 5;
        this.mLayoutPadding = 10;
        this.mFromDegrees = 270.0f;
        this.mToDegrees = 360.0f;
        this.mExpanded = false;
    }

    public ArcLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mChildPadding = 5;
        this.mLayoutPadding = 10;
        this.mFromDegrees = 270.0f;
        this.mToDegrees = 360.0f;
        this.mExpanded = false;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, C0206R.styleable.ArcLayout, 0, 0);
            this.mFromDegrees = a.getFloat(0, 270.0f);
            this.mToDegrees = a.getFloat(1, 360.0f);
            this.mChildSize = Math.max(a.getDimensionPixelSize(2, 0), 0);
            a.recycle();
        }
    }

    private static int computeRadius(float arcDegrees, int childCount, int childSize, int childPadding, int minRadius) {
        return childCount < 2 ? minRadius : Math.max((int) (((double) ((childSize + childPadding) / 2)) / Math.sin(Math.toRadians((double) ((arcDegrees / ((float) (childCount - 1))) / 2.0f)))), minRadius);
    }

    private static Rect computeChildFrame(int centerX, int centerY, int radius, float degrees, int size) {
        double childCenterX = ((double) centerX) + (((double) radius) * Math.cos(Math.toRadians((double) degrees)));
        double childCenterY = ((double) centerY) + (((double) radius) * Math.sin(Math.toRadians((double) degrees)));
        return new Rect((int) (childCenterX - ((double) (size / 2))), (int) (childCenterY - ((double) (size / 2))), (int) (((double) (size / 2)) + childCenterX), (int) (((double) (size / 2)) + childCenterY));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int radius = computeRadius(Math.abs(this.mToDegrees - this.mFromDegrees), getChildCount(), this.mChildSize, this.mChildPadding, 100);
        this.mRadius = radius;
        int size = (((radius * 2) + this.mChildSize) + this.mChildPadding) + (this.mLayoutPadding * 2);
        setMeasuredDimension(size, size);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(this.mChildSize, 1073741824), MeasureSpec.makeMeasureSpec(this.mChildSize, 1073741824));
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = this.mExpanded ? this.mRadius : 0;
        int childCount = getChildCount();
        float perDegrees = (this.mToDegrees - this.mFromDegrees) / ((float) (childCount - 1));
        float degrees = this.mFromDegrees;
        for (int i = 0; i < childCount; i++) {
            Rect frame = computeChildFrame(centerX, centerY, radius, degrees, this.mChildSize);
            degrees += perDegrees;
            getChildAt(i).layout(frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    private static long computeStartOffset(int childCount, boolean expanded, int index, float delayPercent, long duration, Interpolator interpolator) {
        float delay = delayPercent * ((float) duration);
        float totalDelay = delay * ((float) childCount);
        return (long) (interpolator.getInterpolation(((float) ((long) (((float) getTransformedIndex(expanded, childCount, index)) * delay))) / totalDelay) * totalDelay);
    }

    private static int getTransformedIndex(boolean expanded, int count, int index) {
        if (expanded) {
            return (count - 1) - index;
        }
        return index;
    }

    private static Animation createExpandAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, long startOffset, long duration, Interpolator interpolator) {
        Animation animation = new RotateAndTranslateAnimation(0.0f, toXDelta, 0.0f, toYDelta, 0.0f, 720.0f);
        animation.setStartOffset(startOffset);
        animation.setDuration(duration);
        animation.setInterpolator(interpolator);
        animation.setFillAfter(true);
        return animation;
    }

    private static Animation createShrinkAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, long startOffset, long duration, Interpolator interpolator) {
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.setFillAfter(true);
        long preDuration = duration / 2;
        Animation rotateAnimation = new RotateAnimation(0.0f, 360.0f, 1, 0.5f, 1, 0.5f);
        rotateAnimation.setStartOffset(startOffset);
        rotateAnimation.setDuration(preDuration);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setFillAfter(true);
        animationSet.addAnimation(rotateAnimation);
        Animation translateAnimation = new RotateAndTranslateAnimation(0.0f, toXDelta, 0.0f, toYDelta, 360.0f, 720.0f);
        translateAnimation.setStartOffset(startOffset + preDuration);
        translateAnimation.setDuration(duration - preDuration);
        translateAnimation.setInterpolator(interpolator);
        translateAnimation.setFillAfter(true);
        animationSet.addAnimation(translateAnimation);
        return animationSet;
    }

    private void bindChildAnimation(View child, int index, long duration) {
        boolean expanded = this.mExpanded;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = expanded ? 0 : this.mRadius;
        int childCount = getChildCount();
        Rect frame = computeChildFrame(centerX, centerY, radius, this.mFromDegrees + (((float) index) * ((this.mToDegrees - this.mFromDegrees) / ((float) (childCount - 1)))), this.mChildSize);
        int toXDelta = frame.left - child.getLeft();
        int toYDelta = frame.top - child.getTop();
        Interpolator interpolator = this.mExpanded ? new AccelerateInterpolator() : new OvershootInterpolator(1.5f);
        long startOffset = computeStartOffset(childCount, this.mExpanded, index, 0.1f, duration, interpolator);
        Animation animation = this.mExpanded ? createShrinkAnimation(0.0f, (float) toXDelta, 0.0f, (float) toYDelta, startOffset, duration, interpolator) : createExpandAnimation(0.0f, (float) toXDelta, 0.0f, (float) toYDelta, startOffset, duration, interpolator);
        animation.setAnimationListener(new C06401(getTransformedIndex(expanded, childCount, index) == childCount + -1));
        child.setAnimation(animation);
    }

    public boolean isExpanded() {
        return this.mExpanded;
    }

    public void setArc(float fromDegrees, float toDegrees) {
        if (this.mFromDegrees != fromDegrees || this.mToDegrees != toDegrees) {
            this.mFromDegrees = fromDegrees;
            this.mToDegrees = toDegrees;
            requestLayout();
        }
    }

    public void setChildSize(int size) {
        if (this.mChildSize != size && size >= 0) {
            this.mChildSize = size;
            requestLayout();
        }
    }

    public void switchState(boolean showAnimation) {
        if (showAnimation) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                bindChildAnimation(getChildAt(i), i, 300);
            }
        }
        this.mExpanded = !this.mExpanded;
        if (!showAnimation) {
            requestLayout();
        }
        invalidate();
    }

    private void onAllAnimationsEnd() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).clearAnimation();
        }
        requestLayout();
    }
}
