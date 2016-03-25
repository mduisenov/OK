package ru.ok.android.fresco.controller;

import android.content.res.Resources;
import com.facebook.common.internal.Supplier;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerFactory;
import com.facebook.drawee.components.DeferredReleaser;
import com.facebook.imagepipeline.animated.factory.AnimatedDrawableFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import java.util.concurrent.Executor;

public class CustomDrawableControllerFactory extends PipelineDraweeControllerFactory {
    private AnimatedDrawableFactory mAnimatedDrawableFactory;
    private DeferredReleaser mDeferredReleaser;
    private DrawableFactory mDrawableFactory;
    private Resources mResources;
    private Executor mUiThreadExecutor;

    public CustomDrawableControllerFactory(Resources resources, DeferredReleaser deferredReleaser, AnimatedDrawableFactory animatedDrawableFactory, Executor uiThreadExecutor, DrawableFactory drawableFactory) {
        super(resources, deferredReleaser, animatedDrawableFactory, uiThreadExecutor);
        this.mResources = resources;
        this.mDeferredReleaser = deferredReleaser;
        this.mAnimatedDrawableFactory = animatedDrawableFactory;
        this.mUiThreadExecutor = uiThreadExecutor;
        this.mDrawableFactory = drawableFactory;
    }

    public CustomDrawableController newController(Supplier<DataSource<CloseableReference<CloseableImage>>> dataSourceSupplier, String id, Object callerContext) {
        return new CustomDrawableController(this.mResources, this.mDeferredReleaser, this.mAnimatedDrawableFactory, this.mUiThreadExecutor, dataSourceSupplier, id, callerContext, this.mDrawableFactory);
    }
}
