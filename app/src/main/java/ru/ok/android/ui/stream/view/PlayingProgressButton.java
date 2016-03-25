package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import ru.ok.android.C0206R;

public class PlayingProgressButton extends PlayingStateButton {
    private final RectF arcBounds;
    private final Paint paintPaused;
    private final Paint paintProgress;
    private final Paint paintRest;
    private final float strokeWidth;

    public PlayingProgressButton(Context context) {
        this(context, null);
    }

    public PlayingProgressButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 2131296578);
    }

    public PlayingProgressButton(Context context, AttributeSet attrs, int defThemeAttrId, int defStyleResId) {
        super(context, attrs, defThemeAttrId, defThemeAttrId);
        this.arcBounds = new RectF();
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.PlayingProgressButton, defThemeAttrId, defStyleResId);
        int playingColor = a.getColor(0, -16711936);
        int pausedColor = a.getColor(1, -7829368);
        int remainingColor = a.getColor(2, -16776961);
        this.strokeWidth = a.getDimension(3, 1.5f);
        a.recycle();
        this.paintProgress = createPaint(context, playingColor, 1.0f);
        this.paintRest = createPaint(context, remainingColor, 0.8f);
        this.paintPaused = createPaint(context, pausedColor, 1.0f);
    }

    private Paint createPaint(Context context, int color, float widthCoeff) {
        Paint result = new Paint();
        result.setAntiAlias(true);
        result.setStyle(Style.STROKE);
        result.setColor(color);
        result.setStrokeWidth(this.strokeWidth * widthCoeff);
        return result;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int leftPadding = getPaddingLeft();
        int rightPadding = getPaddingRight();
        int width = right - left;
        int height = bottom - top;
        this.arcBounds.set(Math.min(((float) leftPadding) + this.strokeWidth, (float) width), Math.min(((float) getPaddingTop()) + this.strokeWidth, (float) height), Math.max(((float) (width - rightPadding)) - this.strokeWidth, 0.0f), Math.max(((float) (width - getPaddingBottom())) - this.strokeWidth, 0.0f));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.isPlaying) {
            canvas.drawArc(this.arcBounds, -90.0f, (1.0f - this.progress) * -360.0f, false, this.paintRest);
            canvas.drawArc(this.arcBounds, -90.0f, 360.0f * this.progress, false, this.paintProgress);
        } else if (this.isBuffering) {
            Canvas canvas2 = canvas;
            canvas2.drawArc(this.arcBounds, (float) ((System.currentTimeMillis() / 8) % 360), 315.0f, false, this.paintProgress);
            invalidate();
        } else {
            canvas.drawArc(this.arcBounds, 0.0f, 360.0f, false, this.paintPaused);
        }
    }

    protected void onBufferingStateChanged(boolean isBuffering) {
        invalidate();
    }

    protected void onPlayingStateChanged(boolean isPlaying) {
        invalidate();
    }

    protected void onProgressChanged(float progress) {
        invalidate();
    }
}
