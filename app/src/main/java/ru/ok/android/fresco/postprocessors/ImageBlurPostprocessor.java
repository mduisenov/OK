package ru.ok.android.fresco.postprocessors;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.request.BasePostprocessor;
import ru.ok.android.utils.BitmapRender;

public class ImageBlurPostprocessor extends BasePostprocessor {
    private static final float[] COLOR_MATRIX;
    private final int alpha;
    private final String baseKey;
    private final int blurRadius;
    private final int destBitmapDrawRatio;
    private final int destBitmapHeightRatio;

    public String getName() {
        return "ImageBlurProcessor";
    }

    public ImageBlurPostprocessor(String baseKey) {
        this.baseKey = baseKey;
        this.destBitmapHeightRatio = 4;
        this.destBitmapDrawRatio = 2;
        this.alpha = 80;
        this.blurRadius = 12;
    }

    public ImageBlurPostprocessor(String baseKey, int destBitmapHeightRatio, int destBitmapDrawRatio, int alpha, int blurRadius) {
        this.baseKey = baseKey;
        this.destBitmapHeightRatio = destBitmapHeightRatio;
        this.destBitmapDrawRatio = destBitmapDrawRatio;
        this.alpha = alpha;
        this.blurRadius = blurRadius;
    }

    public CloseableReference<Bitmap> process(Bitmap sourceBitmap, PlatformBitmapFactory bitmapFactory) {
        int width = sourceBitmap.getWidth();
        int height = sourceBitmap.getHeight();
        Paint paintBlur = new Paint();
        int destHeight = height / this.destBitmapHeightRatio;
        CloseableReference ref = bitmapFactory.createBitmap(width, destHeight);
        try {
            Bitmap dest = (Bitmap) ref.get();
            Canvas canvas = new Canvas(dest);
            paintBlur.setColor(ViewCompat.MEASURED_STATE_MASK);
            paintBlur.setColorFilter(new ColorMatrixColorFilter(COLOR_MATRIX));
            canvas.drawBitmap(sourceBitmap, 0.0f, 0.0f, paintBlur);
            paintBlur.setColorFilter(null);
            paintBlur.setAlpha(this.alpha);
            int startY = (height - (height / this.destBitmapDrawRatio)) / 2;
            canvas.drawBitmap(sourceBitmap, new Rect(0, startY, width, startY + destHeight), new Rect(0, 0, width, destHeight), paintBlur);
            BitmapRender.fastBlur(dest, this.blurRadius, true);
            CloseableReference<Bitmap> cloneOrNull = CloseableReference.cloneOrNull(ref);
            return cloneOrNull;
        } finally {
            CloseableReference.closeSafely(ref);
        }
    }

    public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey(this.baseKey + "|" + this.destBitmapHeightRatio + "|" + this.destBitmapDrawRatio + "|" + this.alpha + "|" + this.blurRadius);
    }

    static {
        COLOR_MATRIX = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    }
}
