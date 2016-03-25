package ru.ok.android.fresco.postprocessors;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.request.BasePostprocessor;
import ru.ok.android.ui.custom.imageview.RoundedBitmapDrawable;

public class ImageRoundShadowPostprocessor extends BasePostprocessor {
    private final int color;
    private final boolean shadow;
    private final float stroke;
    private final Uri uri;

    public String getName() {
        return ImageRoundShadowPostprocessor.class.getCanonicalName();
    }

    public ImageRoundShadowPostprocessor(Uri uri, float stroke, boolean shadow, int color) {
        this.uri = uri;
        this.stroke = stroke;
        this.shadow = shadow;
        this.color = color;
    }

    public void process(Bitmap destBitmap, Bitmap sourceBitmap) {
        destBitmap.setHasAlpha(true);
        Canvas canvas = new Canvas(destBitmap);
        destBitmap.eraseColor(0);
        RoundedBitmapDrawable drawable = new RoundedBitmapDrawable(sourceBitmap, 0);
        if (this.shadow) {
            drawable.setShadowStroke(this.stroke, this.color);
        } else {
            drawable.setStroke(this.stroke, -1);
        }
        drawable.setBounds(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
        drawable.draw(canvas);
    }

    public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey(ImageRoundShadowPostprocessor.class.getCanonicalName() + " " + this.uri + " " + this.stroke + " " + this.shadow + " " + this.color);
    }
}
