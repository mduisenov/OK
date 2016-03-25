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

public class RayLayout extends ViewGroup {
    private int mChildGap;
    private int mChildSize;
    private boolean mExpanded;
    private int mLeftHolderWidth;

    /* renamed from: ru.ok.android.ui.custom.arcmenu.RayLayout.1 */
    class C06431 implements AnimationListener {
        final /* synthetic */ boolean val$isLast;

        /* renamed from: ru.ok.android.ui.custom.arcmenu.RayLayout.1.1 */
        class C06421 implements Runnable {
            C06421() {
            }

            public void run() {
                RayLayout.this.onAllAnimationsEnd();
            }
        }

        C06431(boolean z) {
            this.val$isLast = z;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            if (this.val$isLast) {
                RayLayout.this.postDelayed(new C06421(), 0);
            }
        }
    }

    public RayLayout(Context context) {
        super(context);
        this.mExpanded = false;
    }

    public RayLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mExpanded = false;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, C0206R.styleable.ArcLayout, 0, 0);
            this.mChildSize = Math.max(a.getDimensionPixelSize(2, 0), 0);
            a.recycle();
            a.recycle();
        }
    }

    private static int computeChildGap(float width, int childCount, int childSize, int minGap) {
        return Math.max((int) ((width / ((float) childCount)) - ((float) childSize)), minGap);
    }

    private static Rect computeChildFrame(boolean expanded, int paddingLeft, int childIndex, int gap, int size) {
        int left = expanded ? (((gap + size) * childIndex) + paddingLeft) + gap : (paddingLeft - size) / 2;
        return new Rect(left, 0, left + size, size);
    }

    protected int getSuggestedMinimumHeight() {
        return this.mChildSize;
    }

    protected int getSuggestedMinimumWidth() {
        return this.mLeftHolderWidth + (this.mChildSize * getChildCount());
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(getSuggestedMinimumHeight(), 1073741824));
        int count = getChildCount();
        this.mChildGap = computeChildGap((float) (getMeasuredWidth() - this.mLeftHolderWidth), count, this.mChildSize, 0);
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(this.mChildSize, 1073741824), MeasureSpec.makeMeasureSpec(this.mChildSize, 1073741824));
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = this.mLeftHolderWidth;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            Rect frame = computeChildFrame(this.mExpanded, paddingLeft, i, this.mChildGap, this.mChildSize);
            getChildAt(i).layout(frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    private static long computeStartOffset(int childCount, boolean expanded, int index, float delayPercent, long duration, Interpolator interpolator) {
        float delay = delayPercent * ((float) duration);
        float totalDelay = delay * ((float) childCount);
        return (long) (interpolator.getInterpolation(((float) ((long) (((float) getTransformedIndex(expanded, childCount, index)) * delay))) / totalDelay) * totalDelay);
    }

    private static int getTransformedIndex(boolean expanded, int count, int index) {
        return (count - 1) - index;
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
        int childCount = getChildCount();
        Rect frame = computeChildFrame(!expanded, this.mLeftHolderWidth, index, this.mChildGap, this.mChildSize);
        int toXDelta = frame.left - child.getLeft();
        int toYDelta = frame.top - child.getTop();
        Interpolator interpolator = this.mExpanded ? new AccelerateInterpolator() : new OvershootInterpolator(1.5f);
        long startOffset = computeStartOffset(childCount, this.mExpanded, index, 0.1f, duration, interpolator);
        Animation animation = this.mExpanded ? createShrinkAnimation(0.0f, (float) toXDelta, 0.0f, (float) toYDelta, startOffset, duration, interpolator) : createExpandAnimation(0.0f, (float) toXDelta, 0.0f, (float) toYDelta, startOffset, duration, interpolator);
        animation.setAnimationListener(new C06431(getTransformedIndex(expanded, childCount, index) == childCount + -1));
        child.setAnimation(animation);
    }

    public boolean isExpanded() {
        return this.mExpanded;
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
