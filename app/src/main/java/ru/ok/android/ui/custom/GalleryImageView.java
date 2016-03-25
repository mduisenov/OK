package ru.ok.android.ui.custom;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import ru.ok.android.proto.MessagesProto.Message;

public class GalleryImageView extends View {
    protected ValueAnimator mAlphaAnimation;
    protected Paint mBorderPaint;
    protected Path mBorderPath;
    protected Drawable mBrokenDrawable;
    protected Drawable mCheckBoxDrawable;
    protected Drawable mDefaultDrawable;
    protected boolean mDefaultDrawn;
    private boolean mDrawBrokenImage;
    protected Bitmap mImage;
    protected Rect mImageDrawingRect;
    protected Paint mImagePaint;
    protected OnImageSelectionListener mOnImageSelectionListener;
    protected boolean mSelected;
    protected boolean mTouched;
    protected Drawable mTouchedDrawable;

    /* renamed from: ru.ok.android.ui.custom.GalleryImageView.1 */
    class C06171 implements AnimatorUpdateListener {
        C06171() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            GalleryImageView.this.mImagePaint.setAlpha(((Integer) animation.getAnimatedValue()).intValue());
            GalleryImageView.this.invalidate();
        }
    }

    public interface OnImageSelectionListener {
        void onImageSelection(GalleryImageView galleryImageView, boolean z);
    }

    public GalleryImageView(Context context) {
        super(context);
        onCreate();
    }

    public GalleryImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreate();
    }

    public GalleryImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    private final void onCreate() {
        int padding = getResources().getDimensionPixelSize(2131231111);
        setPadding(padding, padding, padding, padding);
        this.mTouchedDrawable = getResources().getDrawable(2130838293);
        this.mDefaultDrawable = getResources().getDrawable(2130838526);
        this.mCheckBoxDrawable = getResources().getDrawable(2130837775);
        this.mBrokenDrawable = getResources().getDrawable(2130838524);
        this.mImagePaint = new Paint();
        this.mImageDrawingRect = new Rect();
        this.mBorderPaint = new Paint();
        this.mBorderPaint.setStyle(Style.STROKE);
        this.mBorderPaint.setColor(-1);
        this.mBorderPaint.setStrokeWidth((float) getResources().getDimensionPixelSize(2131231010));
        this.mBorderPath = new Path();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawBrokenImage) {
            this.mBrokenDrawable.draw(canvas);
            this.mDefaultDrawn = false;
        } else if (getImage() == null || getImage().isRecycled()) {
            this.mImage = null;
            this.mDefaultDrawable.draw(canvas);
            this.mDefaultDrawn = true;
        } else {
            if (this.mImagePaint.getAlpha() != MotionEventCompat.ACTION_MASK) {
                this.mDefaultDrawable.draw(canvas);
            }
            canvas.drawBitmap(getImage(), null, this.mImageDrawingRect, this.mImagePaint);
            this.mDefaultDrawn = false;
        }
        if (this.mSelected) {
            this.mCheckBoxDrawable.draw(canvas);
            canvas.drawPath(this.mBorderPath, this.mBorderPaint);
        }
        if (this.mTouched) {
            this.mTouchedDrawable.draw(canvas);
        }
    }

    public final void setImage(Bitmap image) {
        this.mImage = image;
        if (this.mAlphaAnimation != null) {
            this.mAlphaAnimation.cancel();
            this.mImagePaint.setAlpha(MotionEventCompat.ACTION_MASK);
        }
        if (getImage() != null && this.mDefaultDrawn) {
            this.mImagePaint.setAlpha(0);
            this.mAlphaAnimation = ObjectAnimator.ofInt(new int[]{0, MotionEventCompat.ACTION_MASK});
            this.mAlphaAnimation.addUpdateListener(new C06171());
            this.mAlphaAnimation.start();
        }
        invalidate();
    }

    public final void setImageSelected(boolean selected) {
        if (this.mSelected != selected) {
            this.mSelected = selected;
            invalidate();
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getMeasuredWidth() - getPaddingRight();
        int bottom = getMeasuredHeight() - getPaddingBottom();
        this.mDefaultDrawable.setBounds(left, top, right, bottom);
        this.mBrokenDrawable.setBounds(left, top, right, bottom);
        this.mCheckBoxDrawable.setBounds(right - this.mCheckBoxDrawable.getIntrinsicWidth(), top, right, this.mCheckBoxDrawable.getIntrinsicHeight());
        this.mBorderPath.moveTo((float) left, (float) top);
        this.mBorderPath.lineTo((float) right, (float) top);
        this.mBorderPath.lineTo((float) right, (float) bottom);
        this.mBorderPath.lineTo((float) left, (float) bottom);
        this.mBorderPath.lineTo((float) left, ((float) top) - (this.mBorderPaint.getStrokeWidth() / 2.0f));
        this.mImageDrawingRect.set(left, top, right, bottom);
        this.mTouchedDrawable.setBounds(left, top, right, bottom);
    }

    public final void setDrawBrokenImage(boolean drawBroken) {
        this.mDrawBrokenImage = drawBroken;
        if (drawBroken) {
            this.mImage = null;
        }
        invalidate();
    }

    private final Bitmap getImage() {
        return this.mImage;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean z = false;
        switch (event.getAction()) {
            case RECEIVED_VALUE:
                this.mTouched = true;
                invalidate();
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.mTouched = false;
                if (!this.mSelected) {
                    z = true;
                }
                this.mSelected = z;
                invalidate();
                if (this.mOnImageSelectionListener != null) {
                    this.mOnImageSelectionListener.onImageSelection(this, this.mSelected);
                    break;
                }
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                this.mTouched = false;
                invalidate();
                break;
        }
        return true;
    }

    public void setOnImageSelectionListener(OnImageSelectionListener onImageSelectionListener) {
        this.mOnImageSelectionListener = onImageSelectionListener;
    }
}
