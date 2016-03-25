package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.common.references.CloseableReference;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.lang.ref.WeakReference;
import ru.ok.android.drawable.FixedDimensionDrawable;
import ru.ok.android.fresco.UriProvider;
import ru.ok.android.services.processors.image.upload.ResizeSettings;
import ru.ok.android.ui.custom.photo.ThrowAwayViewTouchHelper.Callback;
import ru.ok.android.ui.custom.photo.ThrowAwayViewTouchHelper.OnDragListener;
import ru.ok.android.ui.custom.photo.ThrowAwayViewTouchHelper.OnThrowAwayListener;
import ru.ok.android.ui.custom.photo.ThrowAwayViewTouchHelper.OnThrowedAwayListener;
import ru.ok.android.ui.image.view.DecorHandler;
import ru.ok.android.utils.Logger;

public abstract class AbstractPhotoView extends RelativeLayout implements UriProvider {
    private final ThrowAwayViewTouchHelper fvtHelper;
    protected DecorHandler mDecorViewsHandler;
    private boolean mDragging;
    protected OnDragListener mOnDragListener;
    protected OnThrowAwayListener mOnThrowAwayListener;
    @Nullable
    private CloseableReference<CloseableImage> placeholderRef;
    protected View stubView;

    private static class ProgressListener extends BaseControllerListener<ImageInfo> {
        private final WeakReference<StaticPhoto> containerRef;
        private final WeakReference<FixedDimensionDrawable> fixedDimensionDrawableRef;
        private final boolean hasCachedPreview;
        private final Uri imageUri;

        public ProgressListener(Uri imageUri, StaticPhoto container, FixedDimensionDrawable fixedDimensionDrawable, boolean hasCachedPreview) {
            this.imageUri = imageUri;
            this.containerRef = new WeakReference(container);
            this.hasCachedPreview = hasCachedPreview;
            this.fixedDimensionDrawableRef = new WeakReference(fixedDimensionDrawable);
        }

        public void onSubmit(String id, Object callerContext) {
            super.onSubmit(id, callerContext);
            StaticPhoto container = (StaticPhoto) this.containerRef.get();
            if (container == null) {
                Logger.m172d("Skip");
                return;
            }
            if (!this.hasCachedPreview) {
                container.setProgressVisible(true);
            }
            container.setStubViewVisible(false);
        }

        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            super.onFinalImageSet(id, imageInfo, animatable);
            Logger.m173d("FinalImageSet. HasPreview? %s Uri: %s", Boolean.valueOf(this.hasCachedPreview), this.imageUri);
            StaticPhoto container = (StaticPhoto) this.containerRef.get();
            FixedDimensionDrawable fixedDimensionDrawable = (FixedDimensionDrawable) this.fixedDimensionDrawableRef.get();
            if (container == null || fixedDimensionDrawable == null) {
                Logger.m172d("Skip");
                return;
            }
            int width = imageInfo.getWidth();
            int height = imageInfo.getHeight();
            if (imageInfo instanceof CloseableStaticBitmap) {
                int angle = ((CloseableStaticBitmap) imageInfo).getRotationAngle();
                if (angle == 90 || angle == 270) {
                    int tmp = width;
                    width = height;
                    height = tmp;
                }
            }
            fixedDimensionDrawable.setFixedDimensions(width, height);
            container.setImageDrawable(null);
            container.setImageDrawable(fixedDimensionDrawable);
            container.setReadyForAnimation(true);
            container.setProgressVisible(false);
            container.setStubViewVisible(false);
        }

        public void onFailure(String id, Throwable throwable) {
            super.onFailure(id, throwable);
            Logger.m186w(throwable, "onFailure. Cause.");
            StaticPhoto container = (StaticPhoto) this.containerRef.get();
            if (container == null) {
                Logger.m172d("Skip");
                return;
            }
            if (!this.hasCachedPreview) {
                container.setProgressVisible(false);
                container.setStubViewVisible(true);
                container.setStubViewImage(2130838527);
                container.setStubViewTitle(2131166656);
                if (throwable instanceof IllegalArgumentException) {
                    container.setStubViewSubtitle(2131165810);
                } else {
                    container.setStubViewSubtitle(2131166655);
                }
            }
            Logger.m173d("onFailure. HasCachedPreview? %s. Url %s. ", Boolean.valueOf(this.hasCachedPreview), this.imageUri);
        }
    }

    private final class ThrowCallback implements Callback {
        private ThrowCallback() {
        }

        public void onStartedDrag() {
            AbstractPhotoView.this.mDragging = true;
            if (AbstractPhotoView.this.mOnDragListener != null) {
                AbstractPhotoView.this.mOnDragListener.onStartDrag();
            }
            AbstractPhotoView.this.onDragStart();
        }

        public void onBouncedBack() {
            AbstractPhotoView.this.mDragging = false;
            if (AbstractPhotoView.this.mOnDragListener != null) {
                AbstractPhotoView.this.mOnDragListener.onFinishDrag();
            }
            AbstractPhotoView.this.onBounceBack();
        }

        public void onThrowAway(boolean up) {
            if (AbstractPhotoView.this.mOnThrowAwayListener != null) {
                AbstractPhotoView.this.mOnThrowAwayListener.onThrowAway(up);
            }
        }

        public void onScrollUpdate() {
            AbstractPhotoView.this.onUpdateScroll();
        }

        public boolean isBlocked(MotionEvent event) {
            return AbstractPhotoView.this.isThrowBlocked(event);
        }

        public void onTap() {
            AbstractPhotoView.this.onTapped();
        }
    }

    public abstract RectF getImageDisplayRect();

    public abstract int getImageDisplayedHeight();

    public abstract int getImageDisplayedWidth();

    public abstract int getImageDisplayedX();

    public abstract int getImageDisplayedY();

    public abstract float getImageScale();

    public AbstractPhotoView(Context context) {
        super(context);
        this.fvtHelper = new ThrowAwayViewTouchHelper(this, new ThrowCallback());
    }

    protected void initStubView() {
        this.stubView = findViewById(2131624623);
    }

    protected void onViewTap() {
        if (this.mDecorViewsHandler == null) {
            return;
        }
        if (this.mDecorViewsHandler.isDecorShown()) {
            this.mDecorViewsHandler.setDecorVisibility(false, true);
            this.mDecorViewsHandler.setVisibilityChangeLocked(true);
            return;
        }
        this.mDecorViewsHandler.setVisibilityChangeLocked(false);
        this.mDecorViewsHandler.setDecorVisibility(true, true);
    }

    public void initHierarchy(@Nullable CloseableReference<CloseableImage> placeholderRef) {
        this.placeholderRef = CloseableReference.cloneOrNull((CloseableReference) placeholderRef);
    }

    public Drawable getDrawable() {
        return null;
    }

    public boolean isReadyForAnimation() {
        return false;
    }

    public boolean isTouching() {
        return this.fvtHelper.isTouching();
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.fvtHelper.onInterceptTouchEvent(event);
    }

    public void computeScroll() {
        this.fvtHelper.computeScroll();
    }

    public final void throwAway(boolean up, OnThrowedAwayListener onThrowAwayListener) {
        this.fvtHelper.throwAway(up, onThrowAwayListener);
    }

    protected void onDragStart() {
    }

    protected void onBounceBack() {
    }

    protected void onUpdateScroll() {
    }

    protected void onTapped() {
    }

    protected boolean isThrowBlocked(MotionEvent event) {
        return false;
    }

    public void setProgressVisibile(boolean visible) {
    }

    public void setStubViewVisible(boolean visible) {
        if (this.stubView.getVisibility() != 8 || visible) {
            this.stubView.setVisibility(visible ? 0 : 4);
        }
    }

    public void setStubViewImage(@DrawableRes int imageRes) {
        ((ImageView) findViewById(2131625216)).setImageResource(imageRes);
    }

    public void setStubViewTitle(@StringRes int titleRes) {
        ((TextView) findViewById(2131625217)).setText(titleRes);
    }

    public void setStubViewSubtitle(@StringRes int subtitleRes) {
        ((TextView) findViewById(2131625218)).setText(subtitleRes);
    }

    public Uri getUri() {
        return null;
    }

    public Uri getPreviewUri() {
        return null;
    }

    public void updateProgress(int progress) {
    }

    public void setProgress(int progress) {
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CloseableReference.closeSafely(this.placeholderRef);
    }

    public boolean isDragging() {
        return this.mDragging;
    }

    public void setOnThrowAwayListener(OnThrowAwayListener onThrowAwayListener) {
        this.mOnThrowAwayListener = onThrowAwayListener;
    }

    public void setOnDragListener(OnDragListener onDragListener) {
        this.mOnDragListener = onDragListener;
    }

    public void setDecorViewsHandler(DecorHandler decorViewsHandler) {
        this.mDecorViewsHandler = decorViewsHandler;
    }

    protected void setImageConstantSize(DraweeHolder holder, StaticPhoto container, Uri imageUri, Uri previewUri) {
        if (holder != null) {
            FixedDimensionDrawable fixedDimensionDrawable = new FixedDimensionDrawable();
            if (container.hasPlaceholder()) {
                fixedDimensionDrawable.setFixedDimensions(container.getPlaceholderWidth(), container.getPlaceholderHeight());
                container.setReadyForAnimation(true);
            }
            PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
            Logger.m173d("Show image(Has placeholder? %s) \nCUrl: %s  --\nPUrl: %s  --", Boolean.valueOf(container.hasPlaceholder()), imageUri, previewUri);
            builder.setControllerListener(new ProgressListener(imageUri, container, fixedDimensionDrawable, container.hasPlaceholder()));
            ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(imageUri);
            if (imageUri != null && "file".equals(imageUri.getScheme())) {
                ResizeSettings resizeSettings = new ResizeSettings();
                requestBuilder.setResizeOptions(new ResizeOptions(resizeSettings.getDesiredWidth(), resizeSettings.getDesiredHeight()));
            }
            AbstractDraweeController controller = ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) builder.setImageRequest(requestBuilder.build())).setOldController(holder.getController())).setAutoPlayAnimations(true)).build();
            fixedDimensionDrawable.setProxy(holder.getTopLevelDrawable());
            holder.setController(controller);
            container.setImageDrawable(fixedDimensionDrawable);
        }
    }
}
