package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

public class CircledBorderDrawable extends Drawable {
    private final Paint backgroundPaint;
    private Paint bitmapPaint;
    private final float circleRadiusPx;
    private float leftMargin;
    private final float paddingPx;
    private Path path;
    private float topMargin;

    public CircledBorderDrawable(Context context) {
        this.path = new Path();
        this.backgroundPaint = new Paint();
        this.backgroundPaint.setColor(-1);
        this.backgroundPaint.setAntiAlias(true);
        this.circleRadiusPx = 8.0f * context.getResources().getDisplayMetrics().density;
        this.paddingPx = 4.0f * context.getResources().getDisplayMetrics().density;
    }

    public void setBackground(@Nullable Bitmap bitmap) {
        if (bitmap != null) {
            this.bitmapPaint = new Paint();
            this.bitmapPaint.setShader(new BitmapShader(bitmap, TileMode.REPEAT, TileMode.REPEAT));
            return;
        }
        this.bitmapPaint = null;
    }

    public void draw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        canvas.save();
        canvas.scale((((float) width) + (this.leftMargin * 2.0f)) / ((float) width), (((float) height) + (this.topMargin * 2.0f)) / ((float) height));
        canvas.scale((((float) width) - (this.paddingPx * 2.0f)) / ((float) width), (((float) height) - (this.paddingPx * 2.0f)) / ((float) height), ((float) width) / 2.0f, ((float) height) / 2.0f);
        canvas.drawPath(this.path, this.backgroundPaint);
        if (this.bitmapPaint != null) {
            canvas.drawPath(this.path, this.bitmapPaint);
        }
        canvas.restore();
    }

    public void setAlpha(int alpha) {
        this.bitmapPaint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        this.bitmapPaint.setColorFilter(cf);
    }

    public int getOpacity() {
        return -3;
    }

    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.leftMargin = calculateMargin(bounds.width());
        this.topMargin = calculateMargin(bounds.height());
        calculatePath(bounds.width(), bounds.height());
    }

    private void calculatePath(int width, int height) {
        this.path.rewind();
        float maxWidthCenter = 0.0f;
        float maxHeightCenter = 0.0f;
        float i = this.circleRadiusPx;
        while (this.circleRadiusPx + i <= ((float) height)) {
            this.path.addCircle(this.circleRadiusPx, i, this.circleRadiusPx, Direction.CW);
            maxHeightCenter = i;
            i += this.circleRadiusPx * 1.5f;
        }
        i = this.circleRadiusPx;
        while (this.circleRadiusPx + i <= ((float) width)) {
            this.path.addCircle(i, this.circleRadiusPx, this.circleRadiusPx, Direction.CW);
            maxWidthCenter = i;
            i += this.circleRadiusPx * 1.5f;
        }
        i = this.circleRadiusPx;
        while (i <= maxHeightCenter) {
            this.path.addCircle(maxWidthCenter, i, this.circleRadiusPx, Direction.CW);
            i += this.circleRadiusPx * 1.5f;
        }
        i = this.circleRadiusPx;
        while (i <= maxWidthCenter) {
            this.path.addCircle(i, maxHeightCenter, this.circleRadiusPx, Direction.CW);
            i += this.circleRadiusPx * 1.5f;
        }
        this.path.addRect(this.circleRadiusPx, this.circleRadiusPx, maxWidthCenter, maxHeightCenter, Direction.CW);
    }

    private float calculateMargin(int size) {
        return (((float) size) - (((((float) ((int) ((((float) size) - (this.circleRadiusPx * 2.0f)) / (this.circleRadiusPx * 1.5f)))) * this.circleRadiusPx) * 1.5f) + (this.circleRadiusPx * 2.0f))) / 2.0f;
    }
}
