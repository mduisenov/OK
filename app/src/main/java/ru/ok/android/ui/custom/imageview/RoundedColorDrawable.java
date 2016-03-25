package ru.ok.android.ui.custom.imageview;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;

public final class RoundedColorDrawable extends Drawable {
    private float margin;
    private final Paint paint;
    private final Path path;

    public RoundedColorDrawable(int color, float margin) {
        this.path = new Path();
        this.paint = new Paint();
        this.paint.setColor(color);
        this.paint.setAntiAlias(true);
        this.margin = margin;
    }

    public void setMargin(float margin) {
        this.margin = margin;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        RoundedBitmapDrawable.fillPath(this.path, (float) (right - left), (float) (bottom - top), this.margin);
    }

    public void draw(Canvas canvas) {
        canvas.drawPath(this.path, this.paint);
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return -3;
    }
}
