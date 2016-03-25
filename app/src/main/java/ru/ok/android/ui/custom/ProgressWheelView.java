package ru.ok.android.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import java.lang.ref.WeakReference;
import ru.ok.android.C0206R;

public class ProgressWheelView extends ProgressBar {
    private boolean attached;
    private int barColor;
    private int barLength;
    private Paint barPaint;
    private int barWidth;
    private RectF circleBounds;
    private int circleColor;
    private Paint circlePaint;
    private int circleRadius;
    private int delayMillis;
    private int fullRadius;
    private int paddingBottom;
    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    int progress;
    private RectF rectBounds;
    private int rimColor;
    private Paint rimPaint;
    private int rimWidth;
    private Handler spinHandler;
    int spinProgress;
    private int spinSpeed;
    private String[] splitText;
    private String text;
    private int textColor;
    private Paint textPaint;
    private int textSize;

    private static class SpinHandler extends Handler {
        private final WeakReference<ProgressWheelView> viewRef;

        public SpinHandler(ProgressWheelView view) {
            this.viewRef = new WeakReference(view);
        }

        public void handleMessage(Message msg) {
            ProgressWheelView progressView = (ProgressWheelView) this.viewRef.get();
            if (progressView != null) {
                progressView.invalidate();
                progressView.spinProgress += progressView.spinSpeed;
                if (progressView.spinProgress > 360) {
                    progressView.spinProgress = 0;
                }
                progressView.spinHandler.sendEmptyMessageDelayed(0, (long) progressView.delayMillis);
            }
        }
    }

    public ProgressWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.fullRadius = 100;
        this.circleRadius = 80;
        this.barLength = 20;
        this.barWidth = 20;
        this.rimWidth = 20;
        this.textSize = 20;
        this.paddingTop = 5;
        this.paddingBottom = 5;
        this.paddingLeft = 5;
        this.paddingRight = 5;
        this.barColor = -1442840576;
        this.circleColor = 0;
        this.rimColor = -1428300323;
        this.textColor = ViewCompat.MEASURED_STATE_MASK;
        this.barPaint = new Paint();
        this.circlePaint = new Paint();
        this.rimPaint = new Paint();
        this.textPaint = new Paint();
        this.rectBounds = new RectF();
        this.circleBounds = new RectF();
        this.spinSpeed = 2;
        this.delayMillis = 0;
        this.spinHandler = new SpinHandler(this);
        this.progress = 0;
        this.spinProgress = 0;
        this.text = "";
        this.splitText = new String[0];
        parseAttributes(context.obtainStyledAttributes(attrs, C0206R.styleable.ProgressWheel));
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        setupBounds();
        setupPaints();
        invalidate();
        if (getVisibility() == 0) {
            this.spinHandler.sendEmptyMessage(0);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
        this.spinHandler.removeMessages(0);
    }

    private void setupPaints() {
        this.barPaint.setColor(this.barColor);
        this.barPaint.setAntiAlias(true);
        this.barPaint.setStyle(Style.STROKE);
        this.barPaint.setStrokeWidth((float) this.barWidth);
        this.rimPaint.setColor(this.rimColor);
        this.rimPaint.setAntiAlias(true);
        this.rimPaint.setStyle(Style.STROKE);
        this.rimPaint.setStrokeWidth((float) this.rimWidth);
        this.circlePaint.setColor(this.circleColor);
        this.circlePaint.setAntiAlias(true);
        this.circlePaint.setStyle(Style.FILL);
        this.textPaint.setColor(this.textColor);
        this.textPaint.setStyle(Style.FILL);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize((float) this.textSize);
    }

    private void setupBounds() {
        this.paddingTop = getPaddingTop();
        this.paddingBottom = getPaddingBottom();
        this.paddingLeft = getPaddingLeft();
        this.paddingRight = getPaddingRight();
        this.rectBounds = new RectF((float) this.paddingLeft, (float) this.paddingTop, (float) (getLayoutParams().width - this.paddingRight), (float) (getLayoutParams().height - this.paddingBottom));
        this.circleBounds = new RectF((float) (this.paddingLeft + this.barWidth), (float) (this.paddingTop + this.barWidth), (float) ((getLayoutParams().width - this.paddingRight) - this.barWidth), (float) ((getLayoutParams().height - this.paddingBottom) - this.barWidth));
        this.fullRadius = ((getLayoutParams().width - this.paddingRight) - this.barWidth) / 2;
        this.circleRadius = (this.fullRadius - this.barWidth) + 1;
    }

    private void parseAttributes(TypedArray a) {
        this.barWidth = (int) a.getDimension(10, (float) this.barWidth);
        this.rimWidth = (int) a.getDimension(5, (float) this.rimWidth);
        this.spinSpeed = (int) a.getDimension(6, (float) this.spinSpeed);
        this.delayMillis = a.getInteger(7, 16);
        if (this.delayMillis < 0) {
            this.delayMillis = 0;
        }
        this.barColor = a.getColor(3, this.barColor);
        this.barLength = a.getInteger(11, this.barLength);
        this.textSize = (int) a.getDimension(2, (float) this.textSize);
        this.textColor = a.getColor(1, this.textColor);
        setText(a.getString(0));
        this.rimColor = a.getColor(4, this.rimColor);
        this.circleColor = a.getColor(8, this.circleColor);
    }

    public void setVisibility(int v) {
        super.setVisibility(v);
        this.spinHandler.removeMessages(0);
        if (v == 0 && this.attached) {
            this.spinHandler.sendEmptyMessage(0);
        }
    }

    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ViewCompat.MEASURED_STATE_MASK);
        canvas.drawArc(this.circleBounds, 360.0f, 360.0f, false, this.rimPaint);
        canvas.drawArc(this.circleBounds, (float) (this.spinProgress - 90), getBarLength(), false, this.barPaint);
        canvas.drawCircle(((this.circleBounds.width() / 2.0f) + ((float) this.rimWidth)) + ((float) this.paddingLeft), ((this.circleBounds.height() / 2.0f) + ((float) this.rimWidth)) + ((float) this.paddingTop), (float) this.circleRadius, this.circlePaint);
        int offsetNum = 0;
        for (String s : this.splitText) {
            canvas.drawText(s, ((float) (getWidth() / 2)) - (this.textPaint.measureText(s) / 2.0f), (float) (((getHeight() / 2) + (this.textSize * offsetNum)) - ((this.splitText.length - 1) * (this.textSize / 2))), this.textPaint);
            offsetNum++;
        }
    }

    private float getBarLength() {
        return ((float) this.barLength) + ((((float) this.progress) / 360.0f) * ((float) (360 - this.barLength)));
    }

    public void setProgress(int i) {
        this.progress = i;
    }

    public void setText(String text) {
        this.text = text;
        if (text != null) {
            this.splitText = this.text.split("\n");
        }
    }

    public int getTextSize() {
        return this.textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getPaddingTop() {
        return this.paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public int getPaddingBottom() {
        return this.paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public int getPaddingLeft() {
        return this.paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int getPaddingRight() {
        return this.paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public int getTextColor() {
        return this.textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getSpinProgress() {
        return this.spinProgress;
    }

    public final void setSpinProgress(int spinProgress) {
        this.spinProgress = spinProgress;
    }
}
