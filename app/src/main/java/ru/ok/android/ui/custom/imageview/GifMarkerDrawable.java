package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import ru.ok.android.utils.Utils;

public class GifMarkerDrawable extends Drawable {
    private static final int CORNER_RADIUS_PX;
    private static final int MARGIN_PX;
    private static final int TEXT_PADDING_PX;
    private final RectF mBgBounds;
    private final Paint mBgPaint;
    private final String mText;
    private final Rect mTextBounds;
    private final Paint mTextPaint;
    private float mTextWidth;

    static {
        MARGIN_PX = (int) Utils.dipToPixels(4.0f);
        CORNER_RADIUS_PX = (int) Utils.dipToPixels(8.0f);
        TEXT_PADDING_PX = (int) Utils.dipToPixels(4.0f);
    }

    public GifMarkerDrawable(@NonNull Context context) {
        this.mBgPaint = new Paint(1);
        this.mTextPaint = new Paint(1);
        this.mTextBounds = new Rect();
        this.mBgBounds = new RectF();
        Resources res = context.getResources();
        this.mText = res.getString(2131165906);
        initTextPaint(res.getDimensionPixelSize(2131231014), res.getColor(2131493001));
        initBackgroundPaint(res.getColor(2131493000));
    }

    private void initBackgroundPaint(int color) {
        this.mBgPaint.setColor(color);
        float width = ((float) (TEXT_PADDING_PX * 2)) + this.mTextWidth;
        this.mBgBounds.set(0.0f, 0.0f, width, (float) ((TEXT_PADDING_PX * 2) + this.mTextBounds.height()));
    }

    private void initTextPaint(int textSize, int textColor) {
        this.mTextPaint.setColor(textColor);
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTextSize((float) textSize);
        this.mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        this.mTextPaint.getTextBounds(this.mText, 0, this.mText.length(), this.mTextBounds);
        this.mTextWidth = this.mTextPaint.measureText(this.mText);
    }

    public void draw(@NonNull Canvas canvas) {
        canvas.drawRoundRect(this.mBgBounds, (float) CORNER_RADIUS_PX, (float) CORNER_RADIUS_PX, this.mBgPaint);
        canvas.drawText(this.mText, this.mBgBounds.centerX() - (this.mTextWidth / 2.0f), this.mBgBounds.centerY() + ((float) (this.mTextBounds.height() / 2)), this.mTextPaint);
    }

    public void setAlpha(int alpha) {
        this.mBgPaint.setAlpha(alpha);
        this.mTextPaint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        this.mBgPaint.setColorFilter(cf);
        this.mTextPaint.setColorFilter(cf);
    }

    public int getOpacity() {
        return -3;
    }

    public int getIntrinsicWidth() {
        return (int) this.mBgBounds.width();
    }

    public int getIntrinsicHeight() {
        return (int) this.mBgBounds.height();
    }

    public static void draw(@NonNull GifMarkerDrawable gifMarkerDrawable, @NonNull View view, @NonNull Canvas canvas) {
        int left = MARGIN_PX + view.getPaddingLeft();
        int top = ((view.getMeasuredHeight() - view.getPaddingBottom()) - MARGIN_PX) - gifMarkerDrawable.getIntrinsicHeight();
        canvas.save();
        canvas.translate((float) left, (float) top);
        gifMarkerDrawable.draw(canvas);
        canvas.restore();
    }
}
