package ru.ok.android.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;

public class FixedDimensionDrawable extends Drawable implements Callback {
    private boolean dimesionsSet;
    private int height;
    private boolean mMutated;
    private Drawable mProxy;
    private int width;

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        if (this.mProxy != null) {
            this.mProxy.setBounds(left, top, right, bottom);
        }
    }

    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (this.mProxy != null) {
            this.mProxy.setBounds(bounds);
        }
    }

    public FixedDimensionDrawable() {
        this.dimesionsSet = false;
    }

    protected boolean onLevelChange(int level) {
        if (this.mProxy != null) {
            return this.mProxy.setLevel(level);
        }
        return super.onLevelChange(level);
    }

    public void setProxy(Drawable proxy) {
        if (proxy != this) {
            this.mProxy = proxy;
            proxy.setCallback(this);
        }
    }

    public void draw(Canvas canvas) {
        if (this.mProxy != null) {
            this.mProxy.draw(canvas);
        }
    }

    public void setFixedDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        this.dimesionsSet = true;
        invalidateSelf();
    }

    public int getIntrinsicWidth() {
        if (this.dimesionsSet) {
            return this.width;
        }
        return this.mProxy != null ? this.mProxy.getIntrinsicWidth() : -1;
    }

    public int getIntrinsicHeight() {
        if (this.dimesionsSet) {
            return this.height;
        }
        return this.mProxy != null ? this.mProxy.getIntrinsicHeight() : -1;
    }

    public int getOpacity() {
        return this.mProxy != null ? this.mProxy.getOpacity() : -2;
    }

    public void setFilterBitmap(boolean filter) {
        if (this.mProxy != null) {
            this.mProxy.setFilterBitmap(filter);
        }
    }

    public void setDither(boolean dither) {
        if (this.mProxy != null) {
            this.mProxy.setDither(dither);
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        if (this.mProxy != null) {
            this.mProxy.setColorFilter(colorFilter);
        }
    }

    public void setAlpha(int alpha) {
        if (this.mProxy != null) {
            this.mProxy.setAlpha(alpha);
        }
    }

    public Drawable mutate() {
        if (!(this.mProxy == null || this.mMutated || super.mutate() != this)) {
            this.mProxy.mutate();
            this.mMutated = true;
        }
        return this;
    }

    public void invalidateDrawable(Drawable who) {
        invalidateSelf();
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        scheduleSelf(what, when);
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        unscheduleSelf(what);
    }
}
