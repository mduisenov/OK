package ru.ok.android.ui.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import ru.ok.android.ui.custom.imageview.GifMarkerDrawableHelper;
import ru.ok.android.utils.DimenUtils;

public class PaddingBorderedBitmapView extends ImageView {
    private final GifMarkerDrawableHelper gifMarkerDrawableHelper;
    private Bitmap mBitmap;
    private int mBorderWidth;
    private boolean mShouldDrawBorder;
    private Paint mWhitePaint;
    private final Rect rect;

    public PaddingBorderedBitmapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
        this.mShouldDrawBorder = true;
        this.rect = new Rect();
        init(context);
    }

    public PaddingBorderedBitmapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
        this.mShouldDrawBorder = true;
        this.rect = new Rect();
        init(context);
    }

    public PaddingBorderedBitmapView(Context context) {
        super(context);
        this.gifMarkerDrawableHelper = new GifMarkerDrawableHelper();
        this.mShouldDrawBorder = true;
        this.rect = new Rect();
        init(context);
    }

    private void init(Context context) {
        this.mWhitePaint = new Paint();
        this.mWhitePaint.setColor(-1);
        this.mBorderWidth = DimenUtils.getRealDisplayPixels(1, context);
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        super.setImageBitmap(bitmap);
    }

    protected void onDraw(@NonNull Canvas canvas) {
        try {
            drawBorder(canvas);
        } catch (Exception e) {
        }
        try {
            super.onDraw(canvas);
            canvas.save();
            canvas.translate((float) this.rect.left, (float) (-this.rect.top));
            this.gifMarkerDrawableHelper.drawGifMarkerIfNecessary(this, canvas);
            canvas.restore();
        } catch (Exception e2) {
        }
    }

    private void drawBorder(Canvas canvas) {
        if (this.mBitmap != null && !this.mBitmap.isRecycled() && this.mShouldDrawBorder) {
            updateBitmapMetrics();
            canvas.drawRect((float) (this.rect.left - this.mBorderWidth), (float) (this.rect.top - this.mBorderWidth), (float) (this.rect.right + this.mBorderWidth), (float) (this.rect.bottom + this.mBorderWidth), this.mWhitePaint);
        }
    }

    public final Rect updateBitmapMetrics() {
        if (this.mBitmap != null) {
            float[] values = new float[9];
            getImageMatrix().getValues(values);
            float left = values[2] + ((float) getPaddingLeft());
            float top = values[5] + ((float) getPaddingTop());
            float scale = values[0];
            this.rect.left = (int) left;
            this.rect.top = (int) top;
            this.rect.right = ((int) left) + BigDecimal.valueOf((double) (((float) this.mBitmap.getWidth()) * scale)).setScale(0, RoundingMode.CEILING).intValue();
            this.rect.bottom = ((int) top) + BigDecimal.valueOf((double) (((float) this.mBitmap.getHeight()) * scale)).setScale(0, RoundingMode.CEILING).intValue();
        }
        return this.rect;
    }

    public void setDrawBorder(boolean shouldDrawBorder) {
        if (shouldDrawBorder != this.mShouldDrawBorder) {
            this.mShouldDrawBorder = shouldDrawBorder;
            invalidate();
        }
    }

    public void setShouldDrawGifMarker(boolean shouldDrawGifMarker) {
        this.gifMarkerDrawableHelper.setShouldDrawGifMarker(shouldDrawGifMarker);
    }

    public int getBorderWidth() {
        return this.mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.mBorderWidth = borderWidth;
    }
}
