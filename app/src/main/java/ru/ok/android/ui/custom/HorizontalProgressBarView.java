package ru.ok.android.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import ru.ok.android.C0206R;

public class HorizontalProgressBarView extends View {
    private float mCurrentProgress;
    private final Paint mProgressBarPaint;
    private int mTotalProgress;

    public HorizontalProgressBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mProgressBarPaint = new Paint();
        onCreate(attrs);
    }

    public HorizontalProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mProgressBarPaint = new Paint();
        onCreate(attrs);
    }

    public HorizontalProgressBarView(Context context) {
        super(context);
        this.mProgressBarPaint = new Paint();
        onCreate(null);
    }

    private void onCreate(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, C0206R.styleable.ProgressBarView);
            setProgressColor(typedArray.getColor(0, ViewCompat.MEASURED_STATE_MASK));
            typedArray.recycle();
        }
    }

    protected void onDraw(Canvas canvas) {
        if (!isInEditMode() && this.mTotalProgress != 0) {
            Canvas canvas2 = canvas;
            canvas2.drawRect((float) getPaddingLeft(), (float) getPaddingTop(), (float) (getPaddingLeft() + ((int) (this.mCurrentProgress * ((float) (((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) / this.mTotalProgress))))), (float) (getMeasuredHeight() - getPaddingBottom()), this.mProgressBarPaint);
        }
    }

    public int getTotalProgress() {
        return this.mTotalProgress;
    }

    public void setTotalProgress(int totalProgress) {
        this.mTotalProgress = totalProgress;
        postInvalidate();
    }

    public float getCurrentProgress() {
        return this.mCurrentProgress;
    }

    public void setCurrentProgress(float currentProgress) {
        this.mCurrentProgress = currentProgress;
        postInvalidate();
    }

    public final void setProgressColor(int color) {
        this.mProgressBarPaint.setColor(color);
        postInvalidate();
    }

    public final int getProgressColor() {
        return this.mProgressBarPaint.getColor();
    }
}
