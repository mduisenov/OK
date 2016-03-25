package ru.ok.android.fresco.controller;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.components.DeferredReleaser;
import com.facebook.drawee.drawable.OrientedDrawable;
import com.facebook.imagepipeline.animated.factory.AnimatedDrawableFactory;
import com.facebook.imagepipeline.image.CloseableAnimatedImage;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import java.util.concurrent.Executor;

public class CustomDrawableController extends PipelineDraweeController {
    private final AnimatedDrawableFactory animatedDrawableFactory;
    private final DrawableFactory drawableFactory;

    public CustomDrawableController(Resources resources, DeferredReleaser deferredReleaser, AnimatedDrawableFactory animatedDrawableFactory, Executor uiThreadExecutor, Supplier<DataSource<CloseableReference<CloseableImage>>> dataSourceSupplier, String id, Object callerContext, DrawableFactory drawableFactory) {
        super(resources, deferredReleaser, animatedDrawableFactory, uiThreadExecutor, dataSourceSupplier, id, callerContext);
        this.drawableFactory = drawableFactory;
        this.animatedDrawableFactory = animatedDrawableFactory;
    }

    protected Drawable createDrawable(CloseableReference<CloseableImage> image) {
        Preconditions.checkState(CloseableReference.isValid(image));
        CloseableImage closeableImage = (CloseableImage) image.get();
        if (closeableImage instanceof CloseableStaticBitmap) {
            CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) closeableImage;
            Drawable drawable = this.drawableFactory.createDrawable(closeableStaticBitmap.getUnderlyingBitmap());
            if (closeableStaticBitmap.getRotationAngle() == 0 || closeableStaticBitmap.getRotationAngle() == -1) {
                return drawable;
            }
            return new OrientedDrawable(drawable, closeableStaticBitmap.getRotationAngle());
        } else if (closeableImage instanceof CloseableAnimatedImage) {
            return this.animatedDrawableFactory.create(((CloseableAnimatedImage) closeableImage).getImageResult());
        } else {
            throw new UnsupportedOperationException("Unrecognized image class: " + closeableImage);
        }
    }
}
