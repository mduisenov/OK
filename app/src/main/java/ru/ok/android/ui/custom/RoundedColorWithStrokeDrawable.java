package ru.ok.android.ui.custom;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import ru.ok.android.ui.custom.imageview.RoundedBitmapDrawable;

public class RoundedColorWithStrokeDrawable extends Drawable {
    private int gradientLeftColor;
    private int gradientRightColor;
    private final boolean isGradient;
    private final Paint paint;
    private Path path;
    private int size;
    private final String text;
    private final Paint textPaint;

    public RoundedColorWithStrokeDrawable(int gradientLeftColor, int gradientRightColor, int strokeWidth, String text, int textColor, int textSize) {
        this.path = new Path();
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth((float) strokeWidth);
        this.gradientLeftColor = gradientLeftColor;
        this.gradientRightColor = gradientRightColor;
        this.isGradient = true;
        if (text == null) {
            text = "";
        }
        this.text = text;
        this.textPaint = new Paint();
        this.textPaint.setColor(textColor);
        this.textPaint.setTextSize((float) textSize);
    }

    public RoundedColorWithStrokeDrawable(int solidColor, String text, int textColor, int textSize) {
        this.path = new Path();
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Style.FILL);
        this.paint.setColor(solidColor);
        if (text == null) {
            text = "";
        }
        this.text = text;
        this.textPaint = new Paint();
        this.textPaint.setColor(textColor);
        this.textPaint.setTextSize((float) textSize);
        this.isGradient = false;
    }

    public void draw(Canvas canvas) {
        int width = canvas.getWidth();
        if (width <= this.size || this.size <= 0) {
            canvas.drawPath(this.path, this.paint);
        } else {
            canvas.save();
            canvas.translate(((float) (width - this.size)) / 2.0f, 0.0f);
            canvas.drawPath(this.path, this.paint);
            canvas.restore();
        }
        Canvas canvas2 = canvas;
        canvas2.drawText(this.text, 0, this.text.length(), (((float) width) / 2.0f) - (this.textPaint.measureText(this.text) / 2.0f), (((float) canvas.getHeight()) / 2.0f) - ((this.textPaint.descent() + this.textPaint.ascent()) / 2.0f), this.textPaint);
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return 0;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        this.path = new Path();
        this.size = Math.min(right - left, bottom - top);
        if (this.isGradient) {
            this.paint.setShader(new LinearGradient(0.0f, 0.0f, (float) this.size, 0.0f, this.gradientLeftColor, this.gradientRightColor, TileMode.MIRROR));
        }
        RoundedBitmapDrawable.fillPath(this.path, (float) this.size, (float) this.size, 10.0f);
    }
}
