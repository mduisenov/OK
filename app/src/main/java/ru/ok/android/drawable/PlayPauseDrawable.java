package ru.ok.android.drawable;

import android.animation.ArgbEvaluator;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import java.util.LinkedList;
import java.util.Queue;

public class PlayPauseDrawable extends ShapeDrawable {
    private final float animationDuration;
    private ArgbEvaluator argbEvaluator;
    private final float internalHeight;
    private final float internalWidth;
    private Matrix leftMatrix;
    private Path leftPath;
    private Paint paint;
    private int pauseColor;
    private final float pauseElementHeight;
    private final float pauseElementMargin;
    private final float pauseElementWidth;
    private int playColor;
    private final float playElementOffset;
    private final float playElementWidth;
    private float prevT;
    private Matrix rightMatrix;
    private Path rightPath;
    private long startTime;
    private AnimationState state;
    private Queue<AnimationState> tasks;

    private enum AnimationState {
        TO_PAUSE,
        TO_PLAY,
        STOP
    }

    public PlayPauseDrawable(int playColor, int pauseColor, float pauseElementWidth, float pauseElementHeight, float pauseElementMargin, float playElementWidth, float playElementOffset, int animationDuration) {
        this.leftPath = new Path();
        this.rightPath = new Path();
        this.leftMatrix = new Matrix();
        this.rightMatrix = new Matrix();
        this.paint = new Paint(1);
        this.argbEvaluator = new ArgbEvaluator();
        this.tasks = new LinkedList();
        this.playColor = playColor;
        this.pauseColor = pauseColor;
        this.pauseElementWidth = pauseElementWidth;
        this.pauseElementHeight = pauseElementHeight;
        this.pauseElementMargin = pauseElementMargin;
        this.playElementWidth = playElementWidth;
        this.playElementOffset = playElementOffset;
        this.animationDuration = (float) animationDuration;
        this.internalWidth = Math.max((2.0f * pauseElementWidth) + pauseElementMargin, playElementWidth);
        this.internalHeight = pauseElementHeight;
        this.paint.setStyle(Style.FILL);
    }

    private void updateValues(float t) {
        float initPoint = this.pauseElementWidth * t;
        this.leftPath.reset();
        this.leftPath.moveTo(initPoint, 0.0f);
        this.leftPath.lineTo(this.pauseElementWidth, 0.0f);
        this.leftPath.lineTo(this.pauseElementWidth, this.pauseElementHeight);
        this.leftPath.lineTo(0.0f, this.pauseElementHeight);
        this.leftPath.close();
        this.rightPath.reset();
        this.rightPath.moveTo(this.pauseElementWidth - initPoint, 0.0f);
        this.rightPath.lineTo(0.0f, 0.0f);
        this.rightPath.lineTo(0.0f, this.pauseElementHeight);
        this.rightPath.lineTo(this.pauseElementWidth, this.pauseElementHeight);
        this.rightPath.close();
        this.paint.setColor(((Integer) this.argbEvaluator.evaluate(t, Integer.valueOf(this.pauseColor), Integer.valueOf(this.playColor))).intValue());
    }

    public void draw(Canvas canvas) {
        float t;
        if (this.state != AnimationState.STOP) {
            t = ((float) (System.currentTimeMillis() - this.startTime)) / this.animationDuration;
            if (this.state == AnimationState.TO_PLAY) {
                t = 1.0f - t;
            }
        } else {
            t = this.prevT;
        }
        if (t < 0.0f) {
            t = 0.0f;
            this.state = AnimationState.STOP;
        }
        if (t > 1.0f) {
            t = 1.0f;
            this.state = AnimationState.STOP;
        }
        updateValues(t);
        updateMatrices(t);
        animate(canvas);
        if (this.state != AnimationState.STOP) {
            invalidateSelf();
        } else if (this.tasks.isEmpty()) {
            this.prevT = t;
        } else {
            this.startTime = System.currentTimeMillis();
            this.state = (AnimationState) this.tasks.poll();
            invalidateSelf();
        }
    }

    private void updateMatrices(float t) {
        float w = this.internalWidth;
        float h = this.internalHeight;
        float scaleX = ((1.0f - t) * 1.0f) + (((h / this.pauseElementWidth) * t) / 2.0f);
        float scaleY = ((1.0f - t) * 1.0f) + ((this.playElementWidth / h) * t);
        float startDx = ((w - this.pauseElementMargin) - (this.pauseElementWidth * 2.0f)) / 2.0f;
        fillTransformMatrix(this.leftMatrix, t, scaleX, scaleY, interpolateDx(startDx, t), interpolateDy(0.0f, scaleX, t, true));
        fillTransformMatrix(this.rightMatrix, t, scaleX, scaleY, interpolateDx((w - startDx) - this.pauseElementWidth, t), interpolateDy(0.0f, scaleX, t, false));
    }

    private void fillTransformMatrix(Matrix matrix, float t, float scaleX, float scaleY, float dx, float dy) {
        float halfWidth = this.pauseElementWidth / 2.0f;
        float halfHeight = this.internalHeight / 2.0f;
        matrix.reset();
        matrix.postScale(scaleX, scaleY, halfWidth, halfHeight);
        matrix.postRotate(90.0f * t, halfWidth, halfHeight);
        matrix.postTranslate(dx, dy);
    }

    private float interpolateDx(float startDx, float t) {
        return ((1.0f - t) * startDx) + ((((this.internalWidth / 2.0f) - (this.pauseElementWidth / 2.0f)) + this.playElementOffset) * t);
    }

    private float interpolateDy(float startDy, float scaleX, float t, boolean top) {
        return ((1.0f - t) * startDy) + (((((((float) (top ? -1 : 1)) * this.pauseElementWidth) / 2.0f) * scaleX) + (top ? 1.0f : 0.0f)) * t);
    }

    private void animate(Canvas canvas) {
        this.leftPath.transform(this.leftMatrix);
        canvas.drawPath(this.leftPath, this.paint);
        this.rightPath.transform(this.rightMatrix);
        canvas.drawPath(this.rightPath, this.paint);
    }

    public void pause() {
        if (this.state != AnimationState.TO_PAUSE) {
            this.tasks.clear();
            this.tasks.add(AnimationState.TO_PAUSE);
            invalidateSelf();
        }
    }

    public void play() {
        if (this.state != AnimationState.TO_PLAY) {
            this.tasks.clear();
            this.tasks.add(AnimationState.TO_PLAY);
            invalidateSelf();
        }
    }

    public int getIntrinsicWidth() {
        return (int) Math.ceil((double) this.internalWidth);
    }

    public int getIntrinsicHeight() {
        return (int) Math.ceil((double) this.internalHeight);
    }

    public void forcePlay() {
        forceAnimate(AnimationState.TO_PLAY);
    }

    public void forcePause() {
        forceAnimate(AnimationState.TO_PAUSE);
    }

    private void forceAnimate(AnimationState animationState) {
        this.tasks.clear();
        this.startTime = 0;
        this.state = animationState;
        invalidateSelf();
    }
}
