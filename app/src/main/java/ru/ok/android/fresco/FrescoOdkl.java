package ru.ok.android.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.components.DeferredReleaser;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.core.PriorityThreadFactory;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import ru.ok.android.drawable.LevelListenerDrawable;
import ru.ok.android.fresco.controller.CustomDrawableControllerFactory;
import ru.ok.android.fresco.controller.DrawableFactory;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;

public final class FrescoOdkl {
    public static int ACTUAL_IMAGE;
    public static int PLACEHOLDER_IMAGE;
    private static final Executor imageFetcherExecutor;

    /* renamed from: ru.ok.android.fresco.FrescoOdkl.1 */
    static class C03421 extends BaseDataSubscriber<Void> {
        final /* synthetic */ CountDownLatch val$countDownLatch;

        C03421(CountDownLatch countDownLatch) {
            this.val$countDownLatch = countDownLatch;
        }

        protected void onNewResultImpl(DataSource<Void> dataSource) {
            this.val$countDownLatch.countDown();
        }

        protected void onFailureImpl(DataSource<Void> dataSource) {
            this.val$countDownLatch.countDown();
        }
    }

    /* renamed from: ru.ok.android.fresco.FrescoOdkl.2 */
    static class C03432 extends BaseDataSubscriber<CloseableReference<CloseableImage>> {
        final /* synthetic */ Exchanger val$exchanger;

        C03432(Exchanger exchanger) {
            this.val$exchanger = exchanger;
        }

        protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
            try {
                Logger.m172d("Success");
                this.val$exchanger.exchange(dataSource.getResult());
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }

        protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
            try {
                Logger.m172d("failure");
                this.val$exchanger.exchange(dataSource.getResult());
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }
    }

    public interface BitmapProcessor {
        void processBitmap(@Nullable Bitmap bitmap);
    }

    public interface ProgressCallback {
        void updateProgress(int i);
    }

    public enum SideCrop {
        TOP_CENTER(0.5f, 0.0f),
        TOP_LEFT(0.0f, 0.0f),
        CENTER(0.5f, 0.5f);
        
        private PointF pointF;

        private SideCrop(float x, float y) {
            this.pointF = new PointF(x, y);
        }

        public PointF getPointF() {
            return this.pointF;
        }
    }

    static {
        imageFetcherExecutor = Executors.newSingleThreadExecutor(new PriorityThreadFactory(-1));
        ACTUAL_IMAGE = 1;
        PLACEHOLDER_IMAGE = 2;
    }

    public static GenericDraweeHierarchy createProgressListenerHierarchy(Context context, ProgressCallback progressCallback, @Nullable Bitmap placeholderBitmap) {
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources()).setActualImageScaleType(ScaleType.FIT_CENTER).setProgressBarImage(new LevelListenerDrawable(progressCallback)).setFadeDuration(0);
        if (placeholderBitmap != null) {
            builder.setPlaceholderImage(new BitmapDrawable(context.getResources(), placeholderBitmap), ScaleType.FIT_CENTER);
        }
        return builder.build();
    }

    public static DraweeHolder createCircleDrawee(Context context) {
        return DraweeHolder.create(new GenericDraweeHierarchyBuilder(context.getResources()).setRoundingParams(new RoundingParams().setRoundAsCircle(true)).build(), context);
    }

    public static DraweeController createSimpleController(@Nullable DraweeHolder holder, Uri uri) {
        DraweeController controller = null;
        if (!(holder == null || holder.getController() == null)) {
            controller = holder.getController();
        }
        return ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setUri(uri).setOldController(controller)).build();
    }

    public static PipelineDraweeControllerBuilder newDraweeControllerBuilder(@NonNull DrawableFactory drawableFactory, @NonNull Context context) {
        return new PipelineDraweeControllerBuilder(context, new CustomDrawableControllerFactory(context.getResources(), DeferredReleaser.getInstance(), ImagePipelineFactory.getInstance().getAnimatedDrawableFactory(), UiThreadImmediateExecutorService.getInstance(), drawableFactory), ImagePipelineFactory.getInstance().getImagePipeline(), null);
    }

    public static void getBitmapOnUiThread(ImageRequest imageRequest, BitmapProcessor bitmapProcessor) {
        DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().fetchImageFromBitmapCache(imageRequest, null);
        CloseableReference imageReference;
        try {
            imageReference = (CloseableReference) dataSource.getResult();
            if (imageReference != null) {
                CloseableImage image = (CloseableImage) imageReference.get();
                if (image == null) {
                    bitmapProcessor.processBitmap(null);
                }
                if (image instanceof CloseableBitmap) {
                    bitmapProcessor.processBitmap(((CloseableBitmap) image).getUnderlyingBitmap());
                } else {
                    bitmapProcessor.processBitmap(null);
                }
                CloseableReference.closeSafely(imageReference);
            } else {
                bitmapProcessor.processBitmap(null);
            }
            dataSource.close();
        } catch (Throwable th) {
            dataSource.close();
        }
    }

    public static Uri getUriContentDescription(@NonNull Uri imageUri) {
        String bid = imageUri.getQueryParameter("bid");
        String t = imageUri.getQueryParameter("t");
        if (TextUtils.isEmpty(bid) || TextUtils.isEmpty(t)) {
            return imageUri;
        }
        return Uri.parse("ok-image-cache:bid=" + bid + "&t=" + t);
    }

    public static DraweeHolder<GenericDraweeHierarchy> createAvatarDraweeHolder(@NonNull Context context, Drawable placeholder) {
        return DraweeHolder.create(GenericDraweeHierarchyBuilder.newInstance(context.getResources()).setFadeDuration(0).setPlaceholderImage(placeholder).build(), context);
    }

    public static void cropToSide(SimpleDraweeView currentImageView, SideCrop sideCrop) {
        cropToSide(currentImageView, sideCrop, ACTUAL_IMAGE | PLACEHOLDER_IMAGE);
    }

    public static void cropToSide(SimpleDraweeView currentImageView, SideCrop sideCrop, int cropMask) {
        if ((ACTUAL_IMAGE & cropMask) != 0) {
            ((GenericDraweeHierarchy) currentImageView.getHierarchy()).setActualImageScaleType(ScaleType.FOCUS_CROP);
            ((GenericDraweeHierarchy) currentImageView.getHierarchy()).setActualImageFocusPoint(sideCrop.getPointF());
        }
        if ((PLACEHOLDER_IMAGE & cropMask) != 0) {
            ((GenericDraweeHierarchy) currentImageView.getHierarchy()).setPlaceholderImageFocusPoint(sideCrop.getPointF());
        }
    }

    public static Uri uriFromResId(int imageResourceId) {
        return Uri.parse("res://ru.ok.android/" + imageResourceId);
    }

    public static void prefetchSync(@NonNull String url) {
        if (!TextUtils.isEmpty(url)) {
            prefetchSync(Uri.parse(url));
        }
    }

    public static void prefetchSync(@NonNull Uri uri) {
        if (uri != null) {
            CountDownLatch countDownLatch;
            if (ThreadUtil.isMainThread()) {
                countDownLatch = new CountDownLatch(1);
                Fresco.getImagePipeline().prefetchToBitmapCache(ImageRequest.fromUri(uri), null).subscribe(new C03421(countDownLatch), ThreadUtil.executorService);
            } else {
                countDownLatch = new CountDownLatch(1);
                Fresco.getImagePipeline().prefetchToBitmapCache(ImageRequest.fromUri(uri), null).subscribe(new C03421(countDownLatch), ThreadUtil.executorService);
            }
            try {
                countDownLatch.await(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Logger.m172d("Time out!");
            }
        }
    }

    public static ScaleType convertScaleType(ImageView.ScaleType scaleType) {
        if (scaleType != ImageView.ScaleType.MATRIX) {
            return ScaleType.valueOf(scaleType.name());
        }
        Logger.m184w("Fresco doesn't support MATRIX scale type!");
        return ScaleType.CENTER_CROP;
    }

    public static CloseableReference<CloseableImage> getCachedBitmapReference(Uri uri, @Nullable ResizeOptions resizeOptions) {
        Exchanger<CloseableReference<CloseableImage>> exchanger = new Exchanger();
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        builder.setResizeOptions(resizeOptions);
        builder.setLowestPermittedRequestLevel(RequestLevel.DISK_CACHE);
        Fresco.getImagePipeline().fetchDecodedImage(builder.build(), null).subscribe(new C03432(exchanger), imageFetcherExecutor);
        try {
            long t = SystemClock.elapsedRealtime();
            CloseableReference<CloseableImage> closeableReference = (CloseableReference) exchanger.exchange(null);
            Logger.m173d("Spent time: %d ms", Long.valueOf(SystemClock.elapsedRealtime() - t));
            return closeableReference;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
