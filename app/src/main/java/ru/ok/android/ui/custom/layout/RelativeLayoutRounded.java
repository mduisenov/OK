package ru.ok.android.ui.custom.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import ru.ok.android.C0206R;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView.RoundingType;
import ru.ok.android.ui.custom.imageview.RoundedBitmapDrawable;

public final class RelativeLayoutRounded extends RelativeLayout {
    private final int fillColor;
    private final Path outlinePath;
    private final RoundingType roundingType;
    private final Paint strokePaint;

    public RelativeLayoutRounded(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RelativeLayoutRounded(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        this.outlinePath = new Path();
        this.strokePaint = new Paint();
        if (VERSION.SDK_INT >= 11 && VERSION.SDK_INT < 18) {
            setLayerType(1, null);
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, C0206R.styleable.RelativeLayoutRounded, defStyle, 0);
        this.strokePaint.setAntiAlias(true);
        this.strokePaint.setStyle(Style.STROKE);
        this.strokePaint.setColor(a.getColor(1, -1));
        this.strokePaint.setStrokeWidth(a.getDimension(3, 0.0f));
        this.fillColor = a.getColor(2, 0);
        this.roundingType = RoundingType.fromInt(a.getInt(0, 0));
        a.recycle();
    }

    protected void dispatchDraw(Canvas canvas) {
        int savedState = canvas.save();
        canvas.clipPath(this.outlinePath);
        canvas.drawColor(this.fillColor);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(savedState);
        if (this.strokePaint.getStrokeWidth() > 0.0f) {
            canvas.drawPath(this.outlinePath, this.strokePaint);
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float margin = this.strokePaint.getStrokeWidth();
        if (this.roundingType == RoundingType.ROUNDED) {
            RoundedBitmapDrawable.fillPath(this.outlinePath, (float) getWidth(), (float) getHeight(), margin);
            return;
        }
        float radius = ((float) getWidth()) / 2.0f;
        this.outlinePath.rewind();
        this.outlinePath.addCircle(radius, radius, radius - margin, Direction.CW);
        this.outlinePath.close();
    }
}
