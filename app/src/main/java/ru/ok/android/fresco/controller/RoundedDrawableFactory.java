package ru.ok.android.fresco.controller;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import ru.ok.android.ui.custom.imageview.RoundedBitmapDrawable;

public class RoundedDrawableFactory implements DrawableFactory {
    private int color;
    private boolean isShadow;
    private int strokeWidth;

    public RoundedDrawableFactory(int strokeWidth, boolean isShadow, int color) {
        this.strokeWidth = strokeWidth;
        this.isShadow = isShadow;
        this.color = color;
    }

    @NonNull
    public RoundedBitmapDrawable createDrawable(@NonNull Bitmap bitmap) {
        RoundedBitmapDrawable drawable = new RoundedBitmapDrawable(bitmap, 0);
        if (this.isShadow) {
            drawable.setShadowStroke((float) this.strokeWidth, this.color);
        } else {
            drawable.setStroke((float) this.strokeWidth, -1);
        }
        return drawable;
    }
}
