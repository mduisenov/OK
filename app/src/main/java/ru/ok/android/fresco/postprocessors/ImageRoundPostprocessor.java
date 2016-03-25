package ru.ok.android.fresco.postprocessors;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.request.BasePostprocessor;
import ru.ok.android.ui.custom.imageview.RoundedBitmapDrawable;
import ru.ok.android.utils.Utils;

public class ImageRoundPostprocessor extends BasePostprocessor {
    private final boolean drawBorder;
    private final Uri uri;

    public ImageRoundPostprocessor(Uri uri) {
        this(uri, false);
    }

    public ImageRoundPostprocessor(Uri uri, boolean drawBorder) {
        this.uri = uri;
        this.drawBorder = drawBorder;
    }

    public void process(Bitmap destBitmap, Bitmap sourceBitmap) {
        destBitmap.setHasAlpha(true);
        BitmapShader bitmapShader = new BitmapShader(sourceBitmap, TileMode.CLAMP, TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setShader(bitmapShader);
        Canvas canvas = new Canvas(destBitmap);
        Path path = new Path();
        if (this.drawBorder) {
            Paint circlePaint = createBorderPaint();
            RoundedBitmapDrawable.fillPath(path, (float) sourceBitmap.getWidth(), (float) sourceBitmap.getHeight(), circlePaint.getStrokeWidth() / 2.0f);
            canvas.drawPath(path, circlePaint);
        } else {
            RoundedBitmapDrawable.fillPath(path, (float) sourceBitmap.getWidth(), (float) sourceBitmap.getHeight(), 0.0f);
        }
        canvas.drawPath(path, paint);
    }

    public static Paint createBorderPaint() {
        Paint borderPaint = new Paint();
        borderPaint.setColor(-1);
        borderPaint.setAntiAlias(true);
        borderPaint.setFilterBitmap(true);
        borderPaint.setStyle(Style.STROKE);
        borderPaint.setStrokeWidth(Utils.dipToPixels(2.0f));
        return borderPaint;
    }

    public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey("round-drawBorder-" + this.drawBorder + "|" + this.uri.toString());
    }
}
