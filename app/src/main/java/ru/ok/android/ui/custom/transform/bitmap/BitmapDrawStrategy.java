package ru.ok.android.ui.custom.transform.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView.ScaleType;

interface BitmapDrawStrategy {
    void draw(Canvas canvas, Bitmap bitmap, Rect rect, ScaleType scaleType, Paint paint);

    void setIsTopCrop(boolean z);
}
