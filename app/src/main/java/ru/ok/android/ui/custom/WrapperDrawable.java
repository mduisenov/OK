package ru.ok.android.ui.custom;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

public class WrapperDrawable extends Drawable {
    protected final Drawable baseDrawable;

    public WrapperDrawable(Drawable baseDrawable) {
        this.baseDrawable = baseDrawable;
    }

    public void draw(Canvas canvas) {
        this.baseDrawable.draw(canvas);
    }

    public void setAlpha(int alpha) {
        this.baseDrawable.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        this.baseDrawable.setColorFilter(cf);
    }

    public int getOpacity() {
        return this.baseDrawable.getOpacity();
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        this.baseDrawable.setBounds(left, top, right, bottom);
    }

    public int getIntrinsicWidth() {
        return this.baseDrawable.getIntrinsicWidth();
    }

    public int getIntrinsicHeight() {
        return this.baseDrawable.getIntrinsicHeight();
    }

    public boolean setState(int[] stateSet) {
        return this.baseDrawable.setState(stateSet);
    }

    public boolean isStateful() {
        return this.baseDrawable.isStateful();
    }

    public int[] getState() {
        return this.baseDrawable.getState();
    }

    protected boolean onStateChange(int[] state) {
        this.baseDrawable.setState(state);
        return super.onStateChange(state);
    }

    protected boolean onLevelChange(int level) {
        return true;
    }
}
