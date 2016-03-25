package ru.ok.android.fresco.postprocessors;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.net.Uri;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.request.BasePostprocessor;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.utils.BitmapRender;

public class ImageBlurCenterToCropPostprocessor extends BasePostprocessor {
    private final int blurRadius;
    private final int height;
    private final int opacity;
    private final Uri uri;
    private final int width;

    public ImageBlurCenterToCropPostprocessor(Uri uri, int width, int height, int opacity, int blurRadius) {
        this.uri = uri;
        this.width = width;
        this.height = height;
        this.opacity = opacity;
        this.blurRadius = blurRadius;
    }

    public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey(getClass().getCanonicalName() + ";" + FrescoOdkl.getUriContentDescription(this.uri) + ";" + this.width + MUCUser.ELEMENT + this.height + ";" + this.opacity + ";" + this.blurRadius);
    }

    public CloseableReference<Bitmap> process(Bitmap sourceBitmap, PlatformBitmapFactory bitmapFactory) {
        CloseableReference reference = null;
        try {
            int destHeight = this.height;
            int destWidth = this.width;
            int width = sourceBitmap.getWidth();
            int height = sourceBitmap.getHeight();
            reference = bitmapFactory.createBitmap(destWidth, destHeight);
            Bitmap dest = (Bitmap) reference.get();
            dest.eraseColor(-65536);
            Canvas canvasDest = new Canvas(dest);
            int srcCropHeight = (int) Math.floor((double) ((((float) width) * ((float) destHeight)) / ((float) destWidth)));
            int startY = (height - srcCropHeight) / 2;
            canvasDest.drawBitmap(sourceBitmap, new Rect(0, startY, width, startY + srcCropHeight), new Rect(0, 0, destWidth, destHeight), new Paint());
            BitmapRender.fastBlur(dest, this.blurRadius, true);
            canvasDest.drawColor(this.opacity << 24, Mode.SRC_OVER);
            CloseableReference<Bitmap> cloneOrNull = CloseableReference.cloneOrNull(reference);
            return cloneOrNull;
        } finally {
            CloseableReference.closeSafely(reference);
        }
    }
}
