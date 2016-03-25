package ru.ok.android.ui.custom.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.TextView;

public class HighlightTextView extends TextView {
    private Bitmap bottomMask;
    private Paint maskPaint;
    private Bitmap topMask;

    public HighlightTextView(Context context) {
        super(context);
        init();
    }

    public HighlightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HighlightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void onDraw(Canvas canvas) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        if (this.bottomMask == null || this.bottomMask.getWidth() != canvasWidth) {
            this.bottomMask = Bitmap.createBitmap(canvasWidth, canvasHeight / 4, Config.ARGB_8888);
            this.topMask = Bitmap.createBitmap(canvasWidth, canvasHeight / 4, Config.ARGB_8888);
            Paint maskGradient = new Paint();
            maskGradient.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) this.topMask.getHeight(), -1, ViewCompat.MEASURED_SIZE_MASK, TileMode.MIRROR));
            new Canvas(this.topMask).drawPaint(maskGradient);
            maskGradient.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) this.bottomMask.getHeight(), ViewCompat.MEASURED_SIZE_MASK, -1, TileMode.MIRROR));
            new Canvas(this.bottomMask).drawPaint(maskGradient);
        }
        super.onDraw(canvas);
        int topOffset = getScrollY();
        if (topOffset > 0) {
            if (topOffset < this.bottomMask.getHeight()) {
                topOffset -= this.bottomMask.getHeight() - topOffset;
            }
            canvas.drawBitmap(this.topMask, 0.0f, (float) topOffset, this.maskPaint);
        }
        int bottomOffset = (getLayout().getHeight() - canvasHeight) - getScrollY();
        if (bottomOffset > 0) {
            int maskPosition = (canvasHeight - this.bottomMask.getHeight()) + getScrollY();
            if (bottomOffset < this.bottomMask.getHeight()) {
                maskPosition = (this.bottomMask.getHeight() + maskPosition) - bottomOffset;
            }
            canvas.drawBitmap(this.bottomMask, 0.0f, (float) maskPosition, this.maskPaint);
        }
    }

    private void init() {
        this.maskPaint = new Paint();
        this.maskPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
        setLayerType(1, null);
    }
}
