package ru.ok.android.ui.custom.transform.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView.ScaleType;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import ru.ok.android.ui.custom.transform.BasicTransformView;
import ru.ok.android.utils.Logger;

public class TransformBitmapView extends BasicTransformView {
    private Bitmap animatedBitmap;
    private Paint bitmapPaint;
    private CloseableReference<CloseableImage> closeableReference;
    private final BitmapDrawStrategy matrixStrategy;
    private OnBitmapDrawListener onBitmapDrawListener;
    private ScaleType scaleType;

    public interface OnBitmapDrawListener {
        void onBitmapPostDraw(Canvas canvas, Rect rect);

        void onBitmapPreDraw(Canvas canvas, Rect rect);
    }

    public TransformBitmapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.scaleType = ScaleType.CENTER;
        this.bitmapPaint = new Paint();
        this.matrixStrategy = new MatrixDrawStrategy();
        onCreate();
    }

    public TransformBitmapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.scaleType = ScaleType.CENTER;
        this.bitmapPaint = new Paint();
        this.matrixStrategy = new MatrixDrawStrategy();
        onCreate();
    }

    public TransformBitmapView(Context context) {
        super(context);
        this.scaleType = ScaleType.CENTER;
        this.bitmapPaint = new Paint();
        this.matrixStrategy = new MatrixDrawStrategy();
        onCreate();
    }

    private void onCreate() {
        this.bitmapPaint.setFlags(6);
    }

    protected void draw(Canvas canvas, Rect drawRect) {
        boolean z = true;
        if (this.animatedBitmap == null || this.closeableReference == null || !this.closeableReference.isValid()) {
            String str = "Closeable reference is null(%s) or invalid!";
            Object[] objArr = new Object[1];
            if (this.closeableReference != null) {
                z = false;
            }
            objArr[0] = Boolean.valueOf(z);
            Logger.m185w(str, objArr);
            return;
        }
        if (this.onBitmapDrawListener != null) {
            this.onBitmapDrawListener.onBitmapPreDraw(canvas, drawRect);
        }
        this.bitmapPaint.setAlpha(this.contentAlpha);
        this.matrixStrategy.draw(canvas, this.animatedBitmap, drawRect, this.scaleType, this.bitmapPaint);
        if (this.onBitmapDrawListener != null) {
            this.onBitmapDrawListener.onBitmapPostDraw(canvas, drawRect);
        }
    }

    private float getRotationAngle() {
        CloseableImage closeableImage = (CloseableImage) this.closeableReference.get();
        if (closeableImage instanceof CloseableStaticBitmap) {
            return (float) ((CloseableStaticBitmap) closeableImage).getRotationAngle();
        }
        return 0.0f;
    }

    private Bitmap createBitmap() {
        Bitmap underlyingBitmap = ((CloseableBitmap) this.closeableReference.get()).getUnderlyingBitmap();
        float angle = getRotationAngle();
        if (angle != 90.0f && angle != 270.0f) {
            return underlyingBitmap;
        }
        Matrix m = new Matrix();
        m.postRotate(angle);
        return Bitmap.createBitmap(underlyingBitmap, 0, 0, underlyingBitmap.getWidth(), underlyingBitmap.getHeight(), m, false);
    }

    public final void setBitmapReference(@NonNull CloseableReference<CloseableImage> closeableReference) {
        if (this.closeableReference != null) {
            CloseableReference.closeSafely(this.closeableReference);
        }
        this.closeableReference = closeableReference;
        this.animatedBitmap = createBitmap();
        postInvalidate();
    }

    public final void closeBitmapRef() {
        Logger.m172d("Close bitmap ref");
        CloseableReference.closeSafely(this.closeableReference);
        this.animatedBitmap = null;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CloseableReference.closeSafely(this.closeableReference);
    }

    public final void setScaleType(ScaleType scaleType) {
        if (this.scaleType != scaleType) {
            this.scaleType = scaleType;
            postInvalidate();
        }
    }

    public void setIsTopCrop(boolean isTopCrop) {
        this.matrixStrategy.setIsTopCrop(isTopCrop);
    }

    public void setOnBitmapDrawListener(OnBitmapDrawListener onBitmapDrawListener) {
        this.onBitmapDrawListener = onBitmapDrawListener;
    }
}
