package ru.ok.android.ui.custom.imageview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class CircleBitmapDrawable extends BitmapShaderDrawable {
    private float centerX;
    private float centerY;
    private final int mMargin;
    private Paint mPaintStroke;
    private float radius;

    public CircleBitmapDrawable(Bitmap bitmap, int margin) {
        super(bitmap);
        this.mMargin = margin;
    }

    protected void updateBounds(Rect drawableBounds, RectF outBitmapBounds) {
        outBitmapBounds.set((float) this.mMargin, (float) this.mMargin, (float) (drawableBounds.width() - this.mMargin), (float) (drawableBounds.height() - this.mMargin));
        this.centerX = outBitmapBounds.centerX();
        this.centerY = outBitmapBounds.centerY();
        this.radius = outBitmapBounds.width() / 2.0f;
    }

    protected void drawBitmapShader(Canvas canvas, Paint bitmapShaderPaint) {
        canvas.drawCircle(this.centerX, this.centerY, this.radius, bitmapShaderPaint);
        if (this.mPaintStroke != null) {
            canvas.drawCircle(this.centerX, this.centerY, this.radius - (this.mPaintStroke.getStrokeWidth() / 2.0f), this.mPaintStroke);
        }
    }
}
