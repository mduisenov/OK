package ru.ok.android.ui.messaging.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class ComposingPencilView extends View {
    private final float animationAngle;
    private float animationAngleTopPadding;
    ObjectAnimator animator;
    Bitmap bitmap;
    Paint circlePaint;
    private int circleRadius;
    private float imageDiagonal;
    private int imageHeight;
    float previousX;
    private int shiftSize;
    float f111x;

    public ComposingPencilView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.animationAngle = 4.0f;
        this.previousX = -1.0f;
        this.bitmap = ((BitmapDrawable) getResources().getDrawable(2130838122)).getBitmap();
        this.animator = ObjectAnimator.ofFloat(this, "t", new float[]{0.0f, 1.0f});
        this.animator.setDuration(1200);
        this.animator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.animator.setRepeatCount(-1);
        this.animator.setRepeatMode(2);
        this.circlePaint = new Paint();
        this.circlePaint.setStyle(Style.FILL);
        this.circlePaint.setColor(getResources().getColor(2131493015));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int imageWidth = this.bitmap.getWidth();
        this.imageHeight = this.bitmap.getHeight();
        if (this.imageDiagonal == 0.0f) {
            this.imageDiagonal = (float) Math.sqrt((double) ((imageWidth * imageWidth) + (this.imageHeight * this.imageHeight)));
        }
        this.animationAngleTopPadding = ((float) (Math.sin(0.8552113334772214d) - Math.sin(0.7853981633974483d))) * this.imageDiagonal;
        float animationAngleRightPadding = ((float) (Math.cos(0.715584993317675d) - Math.cos(0.7853981633974483d))) * this.imageDiagonal;
        this.shiftSize = Math.round(((float) imageWidth) / 2.0f);
        this.circleRadius = Math.round(((float) this.imageHeight) / 14.0f);
        setMeasuredDimension((((this.shiftSize + imageWidth) + getPaddingLeft()) + getPaddingRight()) + Math.round(animationAngleRightPadding), ((this.imageHeight + getPaddingTop()) + getPaddingBottom()) + Math.round(this.animationAngleTopPadding));
    }

    public void startAnimation() {
        this.animator.start();
    }

    public void stopAnimation() {
        this.animator.cancel();
    }

    public boolean hasStarted() {
        return this.animator.getCurrentPlayTime() != 0;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float offset = this.f111x * ((float) this.shiftSize);
        canvas.save();
        canvas.rotate(((((float) Math.sin(9.42477796076938d * ((double) this.f111x))) * 4.0f) - 4.0f) + 0.5f, 0.0f, (float) this.imageHeight);
        canvas.translate(offset, this.animationAngleTopPadding);
        canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, null);
        canvas.restore();
        int sectorSize = this.shiftSize / 2;
        int numberOfPoints = Math.round(offset / ((float) sectorSize));
        for (int i = 0; i <= numberOfPoints; i++) {
            canvas.drawCircle((float) ((i * sectorSize) + this.circleRadius), (((float) this.imageHeight) + this.animationAngleTopPadding) - ((float) this.circleRadius), (float) this.circleRadius, this.circlePaint);
        }
    }

    void setT(float value) {
        this.f111x = value;
        int newScaledX = Math.round(this.f111x * ((float) this.shiftSize));
        if (this.previousX != ((float) newScaledX)) {
            this.previousX = (float) newScaledX;
            invalidate();
        }
    }
}
