package ru.ok.android.ui.image.crop.gallery;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import ru.ok.android.ui.image.crop.Util;

public abstract class BaseImage implements IImage {
    protected BaseImageList mContainer;
    protected ContentResolver mContentResolver;
    protected String mDataPath;
    private final long mDateTaken;
    private final String mDisplayName;
    private int mHeight;
    protected long mId;
    protected final int mIndex;
    protected String mMimeType;
    protected long mMiniThumbMagic;
    private String mTitle;
    protected Uri mUri;
    private int mWidth;

    protected BaseImage(BaseImageList container, ContentResolver cr, long id, int index, Uri uri, String dataPath, long miniThumbMagic, String mimeType, long dateTaken, String title, String displayName) {
        this.mWidth = -1;
        this.mHeight = -1;
        this.mContainer = container;
        this.mContentResolver = cr;
        this.mId = id;
        this.mIndex = index;
        this.mUri = uri;
        this.mDataPath = dataPath;
        this.mMiniThumbMagic = miniThumbMagic;
        this.mMimeType = mimeType;
        this.mDateTaken = dateTaken;
        this.mTitle = title;
        this.mDisplayName = displayName;
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof Image)) {
            return false;
        }
        return this.mUri.equals(((Image) other).mUri);
    }

    public int hashCode() {
        return this.mUri.hashCode();
    }

    public Bitmap fullSizeBitmap(int minSideLength, int maxNumberOfPixels) {
        return fullSizeBitmap(minSideLength, maxNumberOfPixels, true, false);
    }

    public Bitmap fullSizeBitmap(int minSideLength, int maxNumberOfPixels, boolean rotateAsNeeded, boolean useNative) {
        Uri url = this.mContainer.contentUri(this.mId);
        if (url == null) {
            return null;
        }
        Bitmap b = Util.makeBitmap(minSideLength, maxNumberOfPixels, url, this.mContentResolver, useNative);
        if (b == null || !rotateAsNeeded) {
            return b;
        }
        return Util.rotate(b, getDegreesRotated());
    }

    public long getDateTaken() {
        return this.mDateTaken;
    }

    public int getDegreesRotated() {
        return 0;
    }

    public String toString() {
        return this.mUri.toString();
    }
}
