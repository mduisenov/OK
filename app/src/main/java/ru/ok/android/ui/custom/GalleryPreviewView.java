package ru.ok.android.ui.custom;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import ru.ok.android.proto.MessagesProto.Message;

public class GalleryPreviewView extends View {
    protected ValueAnimator mAlphaAnimation;
    protected Drawable mBlackGlowBottom;
    protected Drawable mDefaultDrawable;
    protected boolean mDefaultDrawn;
    private int mId;
    protected Bitmap mImage;
    protected Paint mImagePaint;
    protected OnClickListener mOncliClickListener;
    private Matrix mScaleMatrix;
    private String mText;
    private int mTextPadding;
    protected TextPaint mTextPaint;
    private int mTextSize;
    protected boolean mTouched;
    protected Drawable mTouchedDrawable;

    /* renamed from: ru.ok.android.ui.custom.GalleryPreviewView.1 */
    class C06181 implements AnimatorUpdateListener {
        C06181() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            GalleryPreviewView.this.mImagePaint.setAlpha(((Integer) animation.getAnimatedValue()).intValue());
            GalleryPreviewView.this.invalidate();
        }
    }

    public GalleryPreviewView(Context context) {
        super(context);
        this.mScaleMatrix = new Matrix();
        onCreate();
    }

    public GalleryPreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mScaleMatrix = new Matrix();
        onCreate();
    }

    public GalleryPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mScaleMatrix = new Matrix();
        onCreate();
    }

    private final void onCreate() {
        this.mDefaultDrawable = getResources().getDrawable(2130838525);
        this.mTouchedDrawable = getResources().getDrawable(2130838293);
        this.mBlackGlowBottom = getResources().getDrawable(2130837685);
        this.mImagePaint = new Paint();
        this.mTextPaint = new TextPaint();
        this.mTextPaint.setColor(-1);
        this.mTextSize = getResources().getDimensionPixelSize(2131231013);
        this.mTextPaint.setTextSize((float) this.mTextSize);
        this.mTextPaint.setShadowLayer((float) getResources().getDimensionPixelSize(2131231012), 0.0f, 0.0f, ViewCompat.MEASURED_STATE_MASK);
        this.mTextPadding = getResources().getDimensionPixelSize(2131231011);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mImage == null || this.mImage.isRecycled()) {
            this.mImage = null;
            this.mDefaultDrawable.draw(canvas);
            this.mDefaultDrawn = true;
        } else {
            if (this.mImagePaint.getAlpha() != MotionEventCompat.ACTION_MASK) {
                this.mDefaultDrawable.draw(canvas);
            }
            canvas.drawBitmap(this.mImage, this.mScaleMatrix, this.mImagePaint);
            this.mDefaultDrawn = false;
        }
        if (this.mTouched) {
            this.mTouchedDrawable.draw(canvas);
        } else {
            this.mBlackGlowBottom.draw(canvas);
        }
        if (this.mText != null) {
            canvas.drawText(this.mText, (float) this.mTextPadding, (float) (getHeight() - this.mTextPadding), this.mTextPaint);
        }
    }

    public final void setImage(Bitmap image) {
        this.mImage = image;
        if (this.mAlphaAnimation != null) {
            this.mAlphaAnimation.cancel();
            this.mImagePaint.setAlpha(MotionEventCompat.ACTION_MASK);
        }
        if (this.mImage != null) {
            calculateMatrix();
            if (this.mDefaultDrawn) {
                this.mImagePaint.setAlpha(0);
                this.mAlphaAnimation = ObjectAnimator.ofInt(new int[]{0, MotionEventCompat.ACTION_MASK});
                this.mAlphaAnimation.setDuration(250);
                this.mAlphaAnimation.addUpdateListener(new C06181());
                this.mAlphaAnimation.start();
            }
        }
        invalidate();
    }

    public final void setText(String text) {
        this.mText = text;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int x = (w - this.mDefaultDrawable.getIntrinsicWidth()) / 2;
        int y = (h - this.mDefaultDrawable.getIntrinsicHeight()) / 2;
        this.mDefaultDrawable.setBounds(x, y, this.mDefaultDrawable.getIntrinsicWidth() + x, this.mDefaultDrawable.getIntrinsicHeight() + y);
        this.mTouchedDrawable.setBounds(0, 0, w, h);
        this.mBlackGlowBottom.setBounds(0, 0, w, h);
        calculateMatrix();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case RECEIVED_VALUE:
                this.mTouched = true;
                invalidate();
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.mTouched = false;
                invalidate();
                if (this.mOncliClickListener != null) {
                    this.mOncliClickListener.onClick(this);
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

    private final void calculateMatrix() {
        if (this.mImage != null && !this.mImage.isRecycled()) {
            this.mScaleMatrix = new Matrix();
            int imageWidth = this.mImage.getWidth();
            int imageHeight = this.mImage.getHeight();
            int containerWidth = getMeasuredWidth();
            int containerHeight = getMeasuredHeight();
            if (imageWidth < containerWidth || imageHeight < containerHeight) {
                float scaleMax = Math.max(((float) containerWidth) / ((float) imageWidth), ((float) containerHeight) / ((float) imageHeight));
                float dy = ((((float) imageHeight) * scaleMax) - ((float) containerHeight)) / 2.0f;
                this.mScaleMatrix.preScale(scaleMax, scaleMax);
                this.mScaleMatrix.postTranslate(0.0f, -dy);
            }
        }
    }

    public final int getId() {
        return this.mId;
    }

    public final void setId(int id) {
        this.mId = id;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOncliClickListener = onClickListener;
    }
}
