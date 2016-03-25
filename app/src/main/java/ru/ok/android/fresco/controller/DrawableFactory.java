package ru.ok.android.fresco.controller;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public interface DrawableFactory {
    @NonNull
    Drawable createDrawable(@NonNull Bitmap bitmap);
}
