package ru.ok.android.ui.image.crop.gallery;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.File;
import ru.ok.android.ui.image.crop.Util;
import ru.ok.android.utils.BitmapRender;
import ru.ok.android.utils.IOUtils;

class UriImage implements IImage {
    private final IImageList mContainer;
    private final ContentResolver mContentResolver;
    private int mRotationDegrees;
    private final Uri mUri;

    UriImage(IImageList container, ContentResolver cr, Uri uri) {
        this.mContainer = container;
        this.mContentResolver = cr;
        this.mUri = uri;
    }

    private ParcelFileDescriptor getPFD() {
        boolean valid = false;
        try {
            Options bounds = new Options();
            bounds.inJustDecodeBounds = true;
            ParcelFileDescriptor pfd = this.mContentResolver.openFileDescriptor(this.mUri, "r");
            if (pfd != null) {
                BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, bounds);
                if (!(bounds.mCancel || bounds.outWidth == -1 || bounds.outHeight == -1)) {
                    valid = true;
                }
            }
            if (!valid) {
                if (pfd != null) {
                    IOUtils.closeSilently(pfd);
                }
                if (this.mUri.getScheme().equals("file")) {
                    pfd = ParcelFileDescriptor.open(new File(this.mUri.toString().replaceFirst("file://", "")), 268435456);
                    if (pfd != null) {
                        BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, bounds);
                        if (!(bounds.mCancel || bounds.outWidth == -1 || bounds.outHeight == -1)) {
                            valid = true;
                        }
                    }
                }
            }
            if (valid) {
                return pfd;
            }
            IOUtils.closeSilently(pfd);
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Bitmap fullSizeBitmap(int minSideLength, int maxNumberOfPixels) {
        return fullSizeBitmap(minSideLength, maxNumberOfPixels, true, false);
    }

    public Bitmap fullSizeBitmap(int minSideLength, int maxNumberOfPixels, boolean rotateAsNeeded) {
        return fullSizeBitmap(minSideLength, maxNumberOfPixels, rotateAsNeeded, false);
    }

    public Bitmap fullSizeBitmap(int minSideLength, int maxNumberOfPixels, boolean rotateAsNeeded, boolean useNative) {
        try {
            Bitmap b = Util.makeBitmap(minSideLength, maxNumberOfPixels, getPFD(), useNative);
            if (!rotateAsNeeded || this.mRotationDegrees <= 0) {
                return b;
            }
            return BitmapRender.rotate(b, (float) this.mRotationDegrees);
        } catch (Exception ex) {
            Log.e("UriImage", "got exception decoding bitmap ", ex);
            return null;
        }
    }

    public Bitmap thumbBitmap(boolean rotateAsNeeded) {
        return fullSizeBitmap(320, 196608, rotateAsNeeded);
    }

    public long getDateTaken() {
        return 0;
    }

    public boolean rotateImageBy(int degrees) {
        this.mRotationDegrees = degrees;
        return true;
    }
}
