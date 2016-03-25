package ru.ok.android.ui.custom.transform;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.support.v7.widget.LinearLayoutManager;
import android.view.animation.Interpolator;

public class Transformation {
    int backgroundAlpha;
    int contentAlpha;
    int duration;
    Runnable endRunnable;
    int height;
    Interpolator interpolator;
    AnimatorListener listener;
    protected final BasicTransformView mTransformView;
    int width;
    int f100x;
    int f101y;

    Transformation(BasicTransformView transformView) {
        this.f100x = LinearLayoutManager.INVALID_OFFSET;
        this.f101y = LinearLayoutManager.INVALID_OFFSET;
        this.width = LinearLayoutManager.INVALID_OFFSET;
        this.height = LinearLayoutManager.INVALID_OFFSET;
        this.backgroundAlpha = LinearLayoutManager.INVALID_OFFSET;
        this.contentAlpha = LinearLayoutManager.INVALID_OFFSET;
        this.duration = LinearLayoutManager.INVALID_OFFSET;
        this.mTransformView = transformView;
    }

    public Transformation m164x(int x) {
        this.f100x = x;
        return this;
    }

    public Transformation m165y(int y) {
        this.f101y = y;
        return this;
    }

    public Transformation width(int width) {
        this.width = width;
        return this;
    }

    public Transformation height(int height) {
        this.height = height;
        return this;
    }

    public Transformation backgroundAlpha(int backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
        return this;
    }

    public Transformation contentAlpha(int contentAlpha) {
        this.contentAlpha = contentAlpha;
        return this;
    }

    public Transformation withDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public Transformation withListener(AnimatorListener listener) {
        this.listener = listener;
        return this;
    }

    public Animator prepare() {
        return this.mTransformView.prepare(this);
    }

    public Animator start() {
        Animator animator = prepare();
        animator.start();
        return animator;
    }
}
