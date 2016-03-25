package ru.ok.android.ui.custom.imageview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import ru.ok.android.app.OdnoklassnikiApplication;

public class RoundedBitmapDrawable extends BitmapShaderDrawable {
    private boolean isPressed;
    private final int mMargin;
    private Paint paintStroke;
    private final Path path;
    private Paint pressedPaint;
    private boolean shadowMode;
    private float strokeWidth;

    public RoundedBitmapDrawable(Bitmap bitmap, int margin) {
        super(bitmap);
        this.path = new Path();
        this.mMargin = margin;
        this.pressedPaint = new Paint();
        this.pressedPaint.setColor(OdnoklassnikiApplication.getContext().getResources().getColor(2131492919));
        this.pressedPaint.setAntiAlias(true);
    }

    public void setStroke(float width, int color) {
        this.shadowMode = false;
        this.strokeWidth = width;
        if (this.strokeWidth > 0.0f) {
            this.paintStroke = new Paint();
            this.paintStroke.setAntiAlias(true);
            this.paintStroke.setColor(color);
            this.paintStroke.setStrokeWidth(width);
            this.paintStroke.setStyle(Style.STROKE);
            return;
        }
        this.paintStroke = null;
    }

    public void setShadowStroke(float width, int color) {
        this.shadowMode = true;
        this.strokeWidth = width;
        if (this.strokeWidth > 0.0f) {
            this.paintStroke = new Paint();
            this.paintStroke.setAntiAlias(true);
            this.paintStroke.setColor(color);
            this.paintStroke.setShadowLayer(width, 0.0f, 0.0f, color);
            return;
        }
        this.paintStroke = null;
    }

    public int getIntrinsicWidth() {
        return -1;
    }

    public int getIntrinsicHeight() {
        return -1;
    }

    protected void updateBounds(Rect drawableBounds, RectF outBitmapBounds) {
        int innerMargin = (int) (((float) this.mMargin) + this.strokeWidth);
        int drawableWidth = drawableBounds.width();
        int drawableHeight = drawableBounds.height();
        fillPath(this.path, (float) drawableWidth, (float) drawableHeight, (float) innerMargin);
        outBitmapBounds.set((float) innerMargin, (float) innerMargin, (float) (drawableWidth - innerMargin), (float) (drawableHeight - innerMargin));
    }

    public static void fillPath(Path path, float w, float h, float margin) {
        path.rewind();
        float wm = w - (2.0f * margin);
        float hm = h - (2.0f * margin);
        path.moveTo(w / 2.0f, margin);
        path.cubicTo((w - margin) - (wm * 0.24545401f), (hm * 0.014181f) + margin, w - margin, margin, w - margin, h / 2.0f);
        path.cubicTo(w - margin, h - margin, (w - margin) - (0.24545401f * wm), (h - margin) - (0.014181f * hm), w / 2.0f, h - margin);
        path.cubicTo((0.24545401f * wm) + margin, (h - margin) - (hm * 0.014181f), margin, h - margin, margin, h / 2.0f);
        path.cubicTo(margin, margin, (wm * 0.24545401f) + margin, (hm * 0.014181f) + margin, w / 2.0f, margin);
        path.close();
    }

    protected void drawBitmapShader(Canvas canvas, Paint bitmapShaderPaint) {
        if (!this.shadowMode) {
            canvas.drawPath(this.path, bitmapShaderPaint);
        }
        if (this.paintStroke != null) {
            canvas.drawPath(this.path, this.paintStroke);
        }
        if (this.shadowMode) {
            canvas.drawPath(this.path, bitmapShaderPaint);
        }
        if (this.isPressed) {
            canvas.drawPath(this.path, this.pressedPaint);
        }
    }
}
