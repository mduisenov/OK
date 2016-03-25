package ru.ok.android.ui.custom.transform;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.animation.SimpleAnimatorListener;

public abstract class BasicTransformView extends View implements AnimatorUpdateListener {
    private ValueAnimator animator;
    protected int backgroundAlpha;
    protected Drawable backgroundDrawable;
    protected int contentAlpha;
    protected final Rect drawRect;
    private boolean redrawUnchangedRect;
    private final ArrayList<PropertyValuesHolder> valueHolders;

    /* renamed from: ru.ok.android.ui.custom.transform.BasicTransformView.1 */
    class C07601 extends SimpleAnimatorListener {
        final /* synthetic */ Transformation val$transformation;

        C07601(Transformation transformation) {
            this.val$transformation = transformation;
        }

        public void onAnimationStart(Animator animation) {
            if (this.val$transformation.listener != null) {
                this.val$transformation.listener.onAnimationStart(animation);
            }
        }

        public void onAnimationEnd(Animator animation) {
            if (this.val$transformation.listener != null) {
                this.val$transformation.listener.onAnimationEnd(animation);
                BasicTransformView.this.onTransformationEnded();
            }
            if (this.val$transformation.endRunnable != null) {
                this.val$transformation.endRunnable.run();
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (this.val$transformation.listener != null) {
                this.val$transformation.listener.onAnimationCancel(animation);
            }
        }

        public void onAnimationRepeat(Animator animation) {
            if (this.val$transformation.listener != null) {
                this.val$transformation.listener.onAnimationRepeat(animation);
            }
        }
    }

    protected abstract void draw(Canvas canvas, Rect rect);

    public BasicTransformView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.drawRect = new Rect();
        this.backgroundAlpha = MotionEventCompat.ACTION_MASK;
        this.contentAlpha = MotionEventCompat.ACTION_MASK;
        this.valueHolders = new ArrayList(5);
        onCreate();
    }

    public BasicTransformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.drawRect = new Rect();
        this.backgroundAlpha = MotionEventCompat.ACTION_MASK;
        this.contentAlpha = MotionEventCompat.ACTION_MASK;
        this.valueHolders = new ArrayList(5);
        onCreate();
    }

    public BasicTransformView(Context context) {
        super(context);
        this.drawRect = new Rect();
        this.backgroundAlpha = MotionEventCompat.ACTION_MASK;
        this.contentAlpha = MotionEventCompat.ACTION_MASK;
        this.valueHolders = new ArrayList(5);
        onCreate();
    }

    private void onCreate() {
        if (DeviceUtils.hasSdk(11)) {
            setLayerType(2, new Paint());
        }
    }

    protected final void onDraw(Canvas canvas) {
        drawBackground(canvas);
        canvas.save();
        canvas.clipRect(this.drawRect, Op.REPLACE);
        draw(canvas, this.drawRect);
        canvas.restore();
    }

    private void drawBackground(Canvas canvas) {
        if (this.backgroundDrawable != null) {
            this.backgroundDrawable.setAlpha(this.backgroundAlpha);
            this.backgroundDrawable.setBounds(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
            this.backgroundDrawable.draw(canvas);
        }
    }

    public Transformation transform() {
        return new Transformation(this);
    }

    Animator prepare(Transformation transformation) {
        if (this.animator != null) {
            this.animator.cancel();
        }
        this.valueHolders.clear();
        prepareValueHolders(transformation, this.valueHolders);
        if (this.valueHolders.isEmpty()) {
            return null;
        }
        this.animator = ValueAnimator.ofPropertyValuesHolder((PropertyValuesHolder[]) this.valueHolders.toArray(new PropertyValuesHolder[0]));
        this.animator.addUpdateListener(this);
        if (transformation.duration != LinearLayoutManager.INVALID_OFFSET) {
            this.animator.setDuration((long) transformation.duration);
        }
        if (transformation.interpolator != null) {
            this.animator.setInterpolator(transformation.interpolator);
        }
        this.animator.addListener(new C07601(transformation));
        return this.animator;
    }

    protected void prepareValueHolders(Transformation transformation, ArrayList<PropertyValuesHolder> valueHolders) {
        if (getDiff(this.drawRect.left, transformation.f100x) != 0) {
            Logger.m172d("WILL TRANSFORM X FROM " + this.drawRect.left + " TO " + transformation.f100x);
            valueHolders.add(PropertyValuesHolder.ofInt("hldr_x", new int[]{this.drawRect.left, transformation.f100x}));
        }
        if (getDiff(this.drawRect.top, transformation.f101y) != 0) {
            Logger.m172d("WILL TRANSFORM Y FROM " + this.drawRect.top + " TO " + transformation.f101y);
            valueHolders.add(PropertyValuesHolder.ofInt("hldr_y", new int[]{this.drawRect.top, transformation.f101y}));
        }
        if (getDiff(this.drawRect.width(), transformation.width) != 0) {
            Logger.m172d("WILL TRANSFORM WIDTH FROM " + this.drawRect.width() + " TO " + transformation.width);
            valueHolders.add(PropertyValuesHolder.ofInt("hldr_w", new int[]{this.drawRect.width(), transformation.width}));
        }
        if (getDiff(this.drawRect.height(), transformation.height) != 0) {
            Logger.m172d("WILL TRANSFORM HEIGHT FROM " + this.drawRect.height() + " TO " + transformation.height);
            valueHolders.add(PropertyValuesHolder.ofInt("hldr_h", new int[]{this.drawRect.height(), transformation.height}));
        }
        if (getDiff(this.backgroundAlpha, transformation.backgroundAlpha) != 0) {
            Logger.m172d("WILL TRANSFORM BACKGROUND ALPHA FROM " + this.backgroundAlpha + " TO " + transformation.backgroundAlpha);
            valueHolders.add(PropertyValuesHolder.ofInt("hldr_ba", new int[]{this.backgroundAlpha, transformation.backgroundAlpha}));
        }
        if (getDiff(this.contentAlpha, transformation.contentAlpha) != 0) {
            Logger.m172d("WILL TRANSFORM CONTENT ALPHA FROM " + this.contentAlpha + " TO " + transformation.contentAlpha);
            valueHolders.add(PropertyValuesHolder.ofInt("hldr_ca", new int[]{this.contentAlpha, transformation.contentAlpha}));
        }
    }

    public final void onAnimationUpdate(ValueAnimator animator) {
        if (onAnimationValuesUpdate(animator) || this.redrawUnchangedRect) {
            postInvalidate();
        } else {
            Logger.m172d("NO CHANGES AFTER ANIMATION UPDATE. SKIPPING REDRAW.");
        }
    }

    protected boolean onAnimationValuesUpdate(ValueAnimator animator) {
        int alpha;
        boolean hasChanges = false;
        Object xValue = animator.getAnimatedValue("hldr_x");
        if (!(xValue == null || this.drawRect.left == ((Integer) xValue).intValue())) {
            Logger.m172d("NEW X VALUE " + xValue);
            setX(((Integer) xValue).intValue());
            hasChanges = true;
        }
        Object yValue = animator.getAnimatedValue("hldr_y");
        if (!(yValue == null || this.drawRect.top == ((Integer) yValue).intValue())) {
            Logger.m172d("NEW Y VALUE " + yValue);
            setY(((Integer) yValue).intValue());
            hasChanges = true;
        }
        Object wValue = animator.getAnimatedValue("hldr_w");
        if (!(wValue == null || this.drawRect.right - this.drawRect.left == ((Integer) wValue).intValue())) {
            Logger.m172d("NEW WIDTH VALUE " + wValue);
            setWidth(((Integer) wValue).intValue());
            hasChanges = true;
        }
        Object hValue = animator.getAnimatedValue("hldr_h");
        if (!(hValue == null || this.drawRect.bottom - this.drawRect.top == ((Integer) hValue).intValue())) {
            Logger.m172d("NEW HEIGHT VALUE " + hValue);
            setHeight(((Integer) hValue).intValue());
            hasChanges = true;
        }
        Object baValue = animator.getAnimatedValue("hldr_ba");
        if (baValue != null) {
            Logger.m172d("NEW BACKGROUND ALPHA VALUE " + baValue);
            alpha = Math.max(Math.min(((Integer) baValue).intValue(), MotionEventCompat.ACTION_MASK), 0);
            if (alpha != this.backgroundAlpha) {
                setBackgroundAlpha(alpha);
                hasChanges = true;
            }
        }
        Object caValue = animator.getAnimatedValue("hldr_ca");
        if (caValue == null) {
            return hasChanges;
        }
        Logger.m172d("NEW CONTENT ALPHA VALUE " + caValue);
        alpha = Math.max(Math.min(((Integer) caValue).intValue(), MotionEventCompat.ACTION_MASK), 0);
        if (alpha == ((Integer) caValue).intValue()) {
            return hasChanges;
        }
        setContentAlpha(alpha);
        return true;
    }

    protected int getDiff(int actual, int transformer) {
        if (transformer != LinearLayoutManager.INVALID_OFFSET) {
            return actual - transformer;
        }
        return 0;
    }

    public final void setX(int x) {
        int width = this.drawRect.width();
        this.drawRect.left = x;
        this.drawRect.right = x + width;
        onDrawRectChanged();
    }

    public final void setY(int y) {
        int height = this.drawRect.height();
        this.drawRect.top = y;
        this.drawRect.bottom = y + height;
        onDrawRectChanged();
    }

    public final void setWidth(int width) {
        this.drawRect.right = this.drawRect.left + width;
        onDrawRectChanged();
    }

    public final void setHeight(int height) {
        this.drawRect.bottom = this.drawRect.top + height;
        onDrawRectChanged();
    }

    public void setBackground(Drawable background) {
        this.backgroundDrawable = background;
    }

    public void setBackgroundColor(int color) {
        this.backgroundDrawable = new ColorDrawable(color);
    }

    @Deprecated
    public void setBackgroundDrawable(Drawable background) {
        this.backgroundDrawable = background;
    }

    public void setBackgroundResource(int resid) {
        this.backgroundDrawable = getResources().getDrawable(resid);
    }

    public Drawable getBackground() {
        return this.backgroundDrawable;
    }

    public final void setBackgroundAlpha(int backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
    }

    public void setContentAlpha(int contentAlpha) {
        this.contentAlpha = contentAlpha;
    }

    public Rect getDrawRect() {
        return this.drawRect;
    }

    public Drawable getBackgroundDrawable() {
        return this.backgroundDrawable;
    }

    public int getBackgroundAlpha() {
        return this.backgroundAlpha;
    }

    public int getContentAlpha() {
        return this.contentAlpha;
    }

    public void setRedrawUnchangedRect(boolean redrawUnchangedRect) {
        this.redrawUnchangedRect = redrawUnchangedRect;
    }

    protected void onDrawRectChanged() {
    }

    protected void onTransformationEnded() {
    }

    public final void cancelAnimation() {
        if (isAnimating()) {
            this.animator.cancel();
        }
    }

    public final boolean isAnimating() {
        return this.animator != null && this.animator.isRunning();
    }
}
