package ru.ok.android.ui.image.crop.gallery;

import android.graphics.Bitmap;

public interface IImage {
    Bitmap fullSizeBitmap(int i, int i2);

    long getDateTaken();

    boolean rotateImageBy(int i);

    Bitmap thumbBitmap(boolean z);
}
