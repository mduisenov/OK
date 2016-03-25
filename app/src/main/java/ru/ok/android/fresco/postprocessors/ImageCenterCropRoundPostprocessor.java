package ru.ok.android.fresco.postprocessors;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.request.BasePostprocessor;
import ru.ok.android.ui.custom.imageview.RoundedBitmapDrawable;

public class ImageCenterCropRoundPostprocessor extends BasePostprocessor {
    private final boolean drawBorder;
    private final Uri uri;

    public ImageCenterCropRoundPostprocessor(Uri uri) {
        this(uri, false);
    }

    public ImageCenterCropRoundPostprocessor(Uri uri, boolean drawBorder) {
        this.uri = uri;
        this.drawBorder = drawBorder;
    }

    public CloseableReference<Bitmap> process(Bitmap sourceBitmap, PlatformBitmapFactory bitmapFactory) {
        int size;
        int width = sourceBitmap.getWidth();
        int height = sourceBitmap.getHeight();
        if (width < height) {
            size = width;
        } else {
            size = height;
        }
        int destWidth = size;
        int destHeight = size;
        CloseableReference ref = bitmapFactory.createBitmap(destWidth, destHeight);
        CloseableReference refCropped = null;
        Bitmap croppedBitmap = null;
        if (width != height) {
            try {
                refCropped = bitmapFactory.createBitmap(destWidth, destHeight);
                croppedBitmap = (Bitmap) refCropped.get();
                Canvas canvas = new Canvas(croppedBitmap);
                int left = 0;
                int right = width;
                int top = 0;
                int bottom = height;
                int offset;
                if (width < height) {
                    offset = (height - width) / 2;
                    top = offset;
                    bottom = height - offset;
                } else if (width > height) {
                    offset = (width - height) / 2;
                    left = offset;
                    right = width - offset;
                }
                canvas.drawBitmap(sourceBitmap, new Rect(left, top, right, bottom), new Rect(0, 0, destWidth, destHeight), null);
            } catch (Throwable th) {
                CloseableReference.closeSafely(ref);
                if (refCropped != null) {
                    CloseableReference.closeSafely(refCropped);
                }
            }
        }
        Bitmap dest = (Bitmap) ref.get();
        dest.setHasAlpha(true);
        if (refCropped != null) {
            sourceBitmap = croppedBitmap;
        }
        RoundedBitmapDrawable drawable = new RoundedBitmapDrawable(sourceBitmap, 0);
        drawable.setBounds(0, 0, destWidth, destHeight);
        drawable.draw(new Canvas(dest));
        CloseableReference<Bitmap> cloneOrNull = CloseableReference.cloneOrNull(ref);
        CloseableReference.closeSafely(ref);
        if (refCropped != null) {
            CloseableReference.closeSafely(refCropped);
        }
        return cloneOrNull;
    }

    public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey("centerCrop-round-drawBorder-" + this.drawBorder + "|" + this.uri.toString());
    }
}
