package ru.ok.android.ui.image.crop.gallery;

import android.net.Uri;

public interface IImageList {
    void close();

    int getCount();

    IImage getImageAt(int i);

    IImage getImageForUri(Uri uri);
}
