package ru.ok.android.ui.custom.imageview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;

public class RoundCornersBitmapDrawable extends BitmapShaderDrawable {
    private final float[] cornerRad;
    private final Path path;

    public RoundCornersBitmapDrawable(Bitmap bitmap, float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius) {
        super(bitmap);
        this.path = new Path();
        this.cornerRad = new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius};
    }

    protected void updateBounds(Rect drawableBounds, RectF outBitmapBounds) {
        super.updateBounds(drawableBounds, outBitmapBounds);
        this.path.reset();
        this.path.addRoundRect(outBitmapBounds, this.cornerRad, Direction.CW);
    }

    protected void drawBitmapShader(Canvas canvas, Paint bitmapShaderPaint) {
        canvas.drawPath(this.path, bitmapShaderPaint);
    }
}
