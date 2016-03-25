package ru.ok.android.spannable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.text.style.ReplacementSpan;

public class BorderSpan extends ReplacementSpan {
    private final int borderPadding;
    private Paint borderPaint;
    private final int borderRadius;

    public BorderSpan(@ColorInt int borderColor, int borderWidth, int borderRadius, int borderPadding) {
        this.borderRadius = borderRadius;
        this.borderPadding = borderPadding;
        this.borderPaint = new Paint(1);
        this.borderPaint.setColor(borderColor);
        this.borderPaint.setStyle(Style.STROKE);
        this.borderPaint.setStrokeWidth((float) borderWidth);
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
        return (((int) Math.ceil((double) paint.measureText(text, start, end))) + (this.borderPadding * 2)) + 1;
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Rect textBounds = new Rect();
        paint.getTextBounds(text.toString(), start, end, textBounds);
        canvas.drawRoundRect(new RectF(x, (float) ((textBounds.top + y) - this.borderPadding), (((float) (this.borderPadding * 2)) + x) + paint.measureText(text, start, end), (float) ((textBounds.bottom + y) + this.borderPadding)), (float) this.borderRadius, (float) this.borderRadius, this.borderPaint);
        canvas.drawText(text, start, end, x + ((float) this.borderPadding), (float) y, paint);
    }
}
