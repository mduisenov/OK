package ru.ok.android.ui.custom.highlight;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.animation.SimpleAnimatorListener;

public class HighlightOverlayView extends RelativeLayout implements AnimatorUpdateListener {
    private int animationAlpha;
    private Bitmap animationBitmap;
    private Canvas animationCanvas;
    private Path animationPath;
    private int animationRadius;
    private ValueAnimator animator;
    private Paint backgroundPaint;
    private Paint bitmapPaint;
    private boolean hidden;
    private Drawable highlightGradientDrawable;
    private int highlightPadding;
    private OnHighlightedClickListener onHighlightedClickListener;
    private int radius;
    private int f94x;
    private int f95y;

    /* renamed from: ru.ok.android.ui.custom.highlight.HighlightOverlayView.1 */
    class C06531 extends SimpleAnimatorListener {
        final /* synthetic */ int val$alphaTo;

        C06531(int i) {
            this.val$alphaTo = i;
        }

        public void onAnimationEnd(Animator animation) {
            HighlightOverlayView.this.animationAlpha = this.val$alphaTo;
            HighlightOverlayView.this.invalidate();
        }
    }

    public interface OnHighlightedClickListener {
        void onHighlightedClick();
    }

    public HighlightOverlayView(Context context) {
        super(context);
        this.bitmapPaint = new Paint(7);
        onCreate();
    }

    public HighlightOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.bitmapPaint = new Paint(7);
        onCreate();
    }

    public HighlightOverlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.bitmapPaint = new Paint(7);
        onCreate();
    }

    private void onCreate() {
        setWillNotDraw(false);
        this.highlightGradientDrawable = getResources().getDrawable(2130837945);
        this.backgroundPaint = new Paint();
        this.backgroundPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.backgroundPaint.setAlpha(215);
        this.animationPath = new Path();
        this.highlightPadding = DimenUtils.getRealDisplayPixels(16, getContext());
    }

    protected void dispatchDraw(Canvas canvas) {
        boolean running;
        if (this.animator == null || !this.animator.isRunning()) {
            running = false;
        } else {
            running = true;
        }
        this.animationCanvas.drawColor(0, Mode.CLEAR);
        this.bitmapPaint.setAlpha(MotionEventCompat.ACTION_MASK);
        if (running) {
            this.animationCanvas.save();
            this.animationPath.reset();
            this.animationPath.addCircle((float) this.f94x, (float) this.f95y, (float) this.animationRadius, Direction.CCW);
            this.animationCanvas.clipPath(this.animationPath, Op.REPLACE);
            doDispatchDraw(this.animationCanvas);
            this.animationCanvas.restore();
            this.bitmapPaint.setAlpha(this.animationAlpha);
            canvas.drawBitmap(this.animationBitmap, 0.0f, 0.0f, this.bitmapPaint);
        } else if (!this.hidden) {
            doDispatchDraw(this.animationCanvas);
            canvas.drawBitmap(this.animationBitmap, 0.0f, 0.0f, this.bitmapPaint);
        }
    }

    private void doDispatchDraw(Canvas canvas) {
        if (this.radius > 0) {
            canvas.save();
            canvas.clipRect((float) ((this.f94x - this.radius) - this.highlightPadding), (float) ((this.f95y - this.radius) - this.highlightPadding), (float) ((this.f94x + this.radius) + this.highlightPadding), (float) ((this.f95y + this.radius) + this.highlightPadding), Op.DIFFERENCE);
        }
        canvas.drawRect((float) getPaddingLeft(), (float) getPaddingTop(), (float) (getMeasuredWidth() - getPaddingRight()), (float) (getMeasuredHeight() - getPaddingBottom()), this.backgroundPaint);
        super.dispatchDraw(canvas);
        if (this.radius > 0) {
            canvas.restore();
            this.highlightGradientDrawable.setAlpha(215);
            this.highlightGradientDrawable.setBounds((this.f94x - this.radius) - this.highlightPadding, (this.f95y - this.radius) - this.highlightPadding, (this.f94x + this.radius) + this.highlightPadding, (this.f95y + this.radius) + this.highlightPadding);
            this.highlightGradientDrawable.draw(canvas);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 1 && touchesHighlightedArea(event) && this.onHighlightedClickListener != null) {
            this.onHighlightedClickListener.onHighlightedClick();
        }
        return super.onTouchEvent(event);
    }

    public boolean touchesHighlightedArea(MotionEvent event) {
        if (event.getX() <= ((float) (this.f94x - this.radius)) || event.getX() >= ((float) (this.f94x + this.radius)) || event.getY() <= ((float) (this.f95y - this.radius)) || event.getY() >= ((float) (this.f95y + this.radius))) {
            return false;
        }
        return true;
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        this.animationAlpha = ((Integer) this.animator.getAnimatedValue("lph")).intValue();
        this.animationRadius = ((Integer) this.animator.getAnimatedValue("rad")).intValue();
        invalidate();
    }

    public void animateShow(AnimatorListener listener) {
        int animationRadius = getLargestRadius();
        animateOverlay(0, MotionEventCompat.ACTION_MASK, Math.max(animationRadius / 3, this.radius), animationRadius, 400, listener);
        onShowAnimationStart(400);
        this.hidden = false;
    }

    public void animateHide(AnimatorListener listener) {
        animateOverlay(MotionEventCompat.ACTION_MASK, 0, this.animationRadius, this.radius, 300, listener);
        onHideAnimationStart(300);
        this.hidden = true;
    }

    private void animateOverlay(int alphaFrom, int alphaTo, int radiusFrom, int radiusTo, long duration, AnimatorListener listener) {
        Logger.m176e("WILL ANIMATE WITH VALUES " + alphaFrom + " " + alphaTo + " " + radiusFrom + " " + radiusTo + " " + duration);
        stopAnimation();
        PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("lph", new int[]{alphaFrom, alphaTo});
        PropertyValuesHolder radiusHolder = PropertyValuesHolder.ofInt("rad", new int[]{radiusFrom, radiusTo});
        this.animator = ValueAnimator.ofPropertyValuesHolder(new PropertyValuesHolder[]{alphaHolder, radiusHolder});
        this.animator.setDuration(duration);
        this.animator.setInterpolator(new DecelerateInterpolator());
        this.animator.addUpdateListener(this);
        if (listener != null) {
            this.animator.addListener(listener);
        }
        this.animator.addListener(new C06531(alphaTo));
        this.animator.start();
    }

    protected void onShowAnimationStart(long duration) {
    }

    protected void onHideAnimationStart(long duration) {
    }

    private int getLargestRadius() {
        return Math.max(Math.max(getDistanceFromHighlight(0, 0), getDistanceFromHighlight(getMeasuredWidth(), 0)), Math.max(getDistanceFromHighlight(0, getMeasuredHeight()), getDistanceFromHighlight(getMeasuredWidth(), getMeasuredHeight())));
    }

    private int getDistanceFromHighlight(int x, int y) {
        return (int) Math.sqrt(Math.pow((double) (x - this.f94x), 2.0d) + Math.pow((double) (y - this.f95y), 2.0d));
    }

    private void stopAnimation() {
        if (this.animator != null && this.animator.isRunning()) {
            this.animator.cancel();
        }
        this.animator = null;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            int width = r - l;
            int height = b - t;
            if (this.animationBitmap == null || this.animationBitmap.getWidth() != width || this.animationBitmap.getHeight() != height) {
                this.animationBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                this.animationCanvas = new Canvas(this.animationBitmap);
            }
        }
    }

    public void setHighlightArea(int x, int y, int radius) {
        this.f94x = x;
        this.f95y = y;
        this.radius = radius;
    }

    public void setOnHighlightedClickListener(OnHighlightedClickListener onHighlightedClickListener) {
        this.onHighlightedClickListener = onHighlightedClickListener;
    }
}
