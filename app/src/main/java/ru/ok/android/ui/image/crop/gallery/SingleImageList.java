package ru.ok.android.ui.image.crop.gallery;

import android.content.ContentResolver;
import android.net.Uri;

public class SingleImageList implements IImageList {
    private IImage mSingleImage;
    private Uri mUri;

    public SingleImageList(ContentResolver resolver, Uri uri) {
        this.mUri = uri;
        this.mSingleImage = new UriImage(this, resolver, uri);
    }

    public int getCount() {
        return 1;
    }

    public IImage getImageAt(int i) {
        return i == 0 ? this.mSingleImage : null;
    }

    public IImage getImageForUri(Uri uri) {
        return uri.equals(this.mUri) ? this.mSingleImage : null;
    }

    public void close() {
        this.mSingleImage = null;
        this.mUri = null;
    }
}
