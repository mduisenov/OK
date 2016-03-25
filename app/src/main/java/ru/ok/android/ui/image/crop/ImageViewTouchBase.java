package ru.ok.android.ui.image.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

abstract class ImageViewTouchBase extends ImageView {
    protected Matrix mBaseMatrix;
    protected final RotateBitmap mBitmapDisplayed;
    private final Matrix mDisplayMatrix;
    protected Handler mHandler;
    private final float[] mMatrixValues;
    private Runnable mOnLayoutRunnable;
    private Recycler mRecycler;
    protected Matrix mSuppMatrix;
    int mThisHeight;
    int mThisWidth;

    /* renamed from: ru.ok.android.ui.image.crop.ImageViewTouchBase.1 */
    class C09841 implements Runnable {
        final /* synthetic */ RotateBitmap val$bitmap;
        final /* synthetic */ boolean val$resetSupp;

        C09841(RotateBitmap rotateBitmap, boolean z) {
            this.val$bitmap = rotateBitmap;
            this.val$resetSupp = z;
        }

        public void run() {
            ImageViewTouchBase.this.setImageRotateBitmapResetBase(this.val$bitmap, this.val$resetSupp);
        }
    }

    public interface Recycler {
        void recycle(Bitmap bitmap);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mThisWidth = right - left;
        this.mThisHeight = bottom - top;
        Runnable r = this.mOnLayoutRunnable;
        if (r != null) {
            this.mOnLayoutRunnable = null;
            r.run();
        }
        if (this.mBitmapDisplayed.getBitmap() != null) {
            getProperBaseMatrix(this.mBitmapDisplayed, this.mBaseMatrix);
            setImageMatrix(getImageViewMatrix());
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getRepeatCount() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        event.startTracking();
        return true;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != 4 || !event.isTracking() || event.isCanceled() || getScale() <= 1.0f) {
            return super.onKeyUp(keyCode, event);
        }
        return true;
    }

    public void setImageBitmap(Bitmap bitmap) {
        setImageBitmap(bitmap, 0);
    }

    private void setImageBitmap(Bitmap bitmap, int rotation) {
        super.setImageBitmap(bitmap);
        Drawable d = getDrawable();
        if (d != null) {
            d.setDither(true);
        }
        Bitmap old = this.mBitmapDisplayed.getBitmap();
        this.mBitmapDisplayed.setBitmap(bitmap);
        this.mBitmapDisplayed.setRotation(rotation);
        if (old != null && old != bitmap && this.mRecycler != null) {
            this.mRecycler.recycle(old);
        }
    }

    public void clear() {
        setImageBitmapResetBase(null, true);
    }

    public void setImageBitmapResetBase(Bitmap bitmap, boolean resetSupp) {
        setImageRotateBitmapResetBase(new RotateBitmap(bitmap), resetSupp);
    }

    public void setImageRotateBitmapResetBase(RotateBitmap bitmap, boolean resetSupp) {
        if (getWidth() <= 0) {
            this.mOnLayoutRunnable = new C09841(bitmap, resetSupp);
            return;
        }
        if (bitmap.getBitmap() != null) {
            getProperBaseMatrix(bitmap, this.mBaseMatrix);
            setImageBitmap(bitmap.getBitmap(), bitmap.getRotation());
        } else {
            this.mBaseMatrix.reset();
            setImageBitmap(null);
        }
        if (resetSupp) {
            this.mSuppMatrix.reset();
        }
        setImageMatrix(getImageViewMatrix());
    }

    protected void center(boolean horizontal, boolean vertical) {
        if (this.mBitmapDisplayed.getBitmap() != null) {
            Matrix m = getImageViewMatrix();
            RectF rect = new RectF(0.0f, 0.0f, (float) this.mBitmapDisplayed.getBitmap().getWidth(), (float) this.mBitmapDisplayed.getBitmap().getHeight());
            m.mapRect(rect);
            float height = rect.height();
            float width = rect.width();
            float deltaX = 0.0f;
            float deltaY = 0.0f;
            if (vertical) {
                int viewHeight = getHeight();
                if (height < ((float) viewHeight)) {
                    deltaY = ((((float) viewHeight) - height) / 2.0f) - rect.top;
                } else if (rect.top > 0.0f) {
                    deltaY = -rect.top;
                } else if (rect.bottom < ((float) viewHeight)) {
                    deltaY = ((float) getHeight()) - rect.bottom;
                }
            }
            if (horizontal) {
                int viewWidth = getWidth();
                if (width < ((float) viewWidth)) {
                    deltaX = ((((float) viewWidth) - width) / 2.0f) - rect.left;
                } else if (rect.left > 0.0f) {
                    deltaX = -rect.left;
                } else if (rect.right < ((float) viewWidth)) {
                    deltaX = ((float) viewWidth) - rect.right;
                }
            }
            postTranslate(deltaX, deltaY);
            setImageMatrix(getImageViewMatrix());
        }
    }

    public ImageViewTouchBase(Context context) {
        super(context);
        this.mBaseMatrix = new Matrix();
        this.mSuppMatrix = new Matrix();
        this.mDisplayMatrix = new Matrix();
        this.mMatrixValues = new float[9];
        this.mBitmapDisplayed = new RotateBitmap(null);
        this.mThisWidth = -1;
        this.mThisHeight = -1;
        this.mHandler = new Handler();
        this.mOnLayoutRunnable = null;
        init();
    }

    public ImageViewTouchBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBaseMatrix = new Matrix();
        this.mSuppMatrix = new Matrix();
        this.mDisplayMatrix = new Matrix();
        this.mMatrixValues = new float[9];
        this.mBitmapDisplayed = new RotateBitmap(null);
        this.mThisWidth = -1;
        this.mThisHeight = -1;
        this.mHandler = new Handler();
        this.mOnLayoutRunnable = null;
        init();
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[whichValue];
    }

    protected float getScale(Matrix matrix) {
        return getValue(matrix, 0);
    }

    protected float getScale() {
        return getScale(this.mSuppMatrix);
    }

    private void getProperBaseMatrix(RotateBitmap bitmap, Matrix matrix) {
        float viewWidth = (float) getWidth();
        float viewHeight = (float) getHeight();
        float w = (float) bitmap.getWidth();
        float h = (float) bitmap.getHeight();
        matrix.reset();
        float scale = Math.min(Math.min(viewWidth / w, 3.0f), Math.min(viewHeight / h, 3.0f));
        matrix.postConcat(bitmap.getRotateMatrix());
        matrix.postScale(scale, scale);
        matrix.postTranslate((viewWidth - (w * scale)) / 2.0f, (viewHeight - (h * scale)) / 2.0f);
    }

    protected Matrix getImageViewMatrix() {
        this.mDisplayMatrix.set(this.mBaseMatrix);
        this.mDisplayMatrix.postConcat(this.mSuppMatrix);
        return this.mDisplayMatrix;
    }

    protected void postTranslate(float dx, float dy) {
        this.mSuppMatrix.postTranslate(dx, dy);
    }

    protected void panBy(float dx, float dy) {
        postTranslate(dx, dy);
        setImageMatrix(getImageViewMatrix());
    }
}
