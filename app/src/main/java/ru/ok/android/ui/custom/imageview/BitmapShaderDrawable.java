package ru.ok.android.ui.custom.imageview;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

public abstract class BitmapShaderDrawable extends Drawable {
    private final Bitmap bitmap;
    private final RectF bitmapBounds;
    private final BitmapShader bitmapShader;
    private final Paint bitmapShaderPaint;

    protected abstract void drawBitmapShader(Canvas canvas, Paint paint);

    public BitmapShaderDrawable(Bitmap bitmap) {
        this.bitmapBounds = new RectF();
        this.bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        this.bitmap = bitmap;
        this.bitmapShaderPaint = new Paint();
        this.bitmapShaderPaint.setAntiAlias(true);
        this.bitmapShaderPaint.setShader(this.bitmapShader);
    }

    public void setAlpha(int alpha) {
        this.bitmapShaderPaint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.bitmapShaderPaint.setColorFilter(colorFilter);
    }

    public int getOpacity() {
        return -3;
    }

    protected final void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateBounds(bounds, this.bitmapBounds);
        updateScale();
    }

    protected void updateBounds(Rect drawableBounds, RectF outBitmapBounds) {
        outBitmapBounds.set(drawableBounds);
    }

    private void updateScale() {
        int bitmapWidth = this.bitmap.getWidth();
        int bitmapHeight = this.bitmap.getHeight();
        if (bitmapWidth > 0 && bitmapHeight > 0) {
            float scale = Math.max(this.bitmapBounds.width() / ((float) bitmapWidth), this.bitmapBounds.height() / ((float) bitmapHeight));
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            matrix.postTranslate(((float) getBounds().centerX()) - ((((float) this.bitmap.getWidth()) * scale) / 2.0f), ((float) getBounds().centerY()) - ((((float) this.bitmap.getHeight()) * scale) / 2.0f));
            this.bitmapShader.setLocalMatrix(matrix);
        }
    }

    public final void draw(Canvas canvas) {
        drawBitmapShader(canvas, this.bitmapShaderPaint);
    }

    public int getIntrinsicWidth() {
        return this.bitmap.getWidth();
    }

    public int getIntrinsicHeight() {
        return this.bitmap.getHeight();
    }
}
