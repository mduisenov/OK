package ru.ok.android.ui.image.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ImageView.ScaleType;
import com.facebook.common.references.CloseableReference;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import ru.ok.android.fresco.ResizeOptionsProvider;
import ru.ok.android.fresco.UriProvider;
import ru.ok.android.ui.custom.photo.PhotoScaleDataProvider;
import ru.ok.android.ui.custom.transform.bitmap.TransformBitmapView;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.animation.SyncBus;
import ru.ok.android.utils.animation.SyncBus.MessageCallback;

public class PhotoLayerAnimationHelper {
    private static final SyncBus SYNCBUS;

    static {
        SYNCBUS = new SyncBus();
    }

    public static Bundle makeScaleUpAnimationBundle(Context context, Uri photoUri, int srcWidth, int srcHeight, int srcX, int srcY, int realWidth, int realHeight) {
        Bundle bundle = new Bundle();
        Logger.m173d("MakeScaleUpBundle.1. Uri: %s", photoUri);
        Fresco.getImagePipeline().prefetchToBitmapCache(ImageRequest.fromUri(photoUri), null);
        bundle.putParcelable("pla_image_uri", photoUri);
        bundle.putInt("pla_src_wdth", srcWidth);
        bundle.putInt("pla_src_hght", srcHeight);
        bundle.putInt("pla_rlp_wdth", DimenUtils.getRealDisplayPixels(realWidth, context));
        bundle.putInt("pla_rlp_hght", DimenUtils.getRealDisplayPixels(realHeight, context));
        bundle.putInt("pla_src_x", srcX);
        bundle.putInt("pla_src_y", srcY);
        return bundle;
    }

    public static Bundle makeScaleUpAnimationBundle(Context context, PhotoScaleDataProvider dataProvider) {
        if (dataProvider == null) {
            return null;
        }
        Fresco.getImagePipeline().prefetchToBitmapCache(ImageRequest.fromUri(dataProvider.getUri()), null);
        Logger.m173d("MakeScaleUpBundle.2. Uri: %s", dataProvider.getUri());
        return makeScaleUpAnimationBundle(context, dataProvider.getUri(), dataProvider.getDisplayedWidth(), dataProvider.getDisplayedHeight(), dataProvider.getDisplayedX(), dataProvider.getDisplayedY(), dataProvider.getRealWidth(), dataProvider.getRealHeight());
    }

    public static Bundle makeScaleUpAnimationBundle(View uriProviderView, int realWidth, int realHeight, int rotation) {
        Bundle bundle = null;
        if (uriProviderView != null && (uriProviderView instanceof UriProvider)) {
            Uri uri = ((UriProvider) uriProviderView).getUri();
            Logger.m173d("MakeScaleUpBundle.3. Uri: %s", uri);
            if (uri != null) {
                Fresco.getImagePipeline().prefetchToBitmapCache(ImageRequest.fromUri(uri), null);
                int srcWidth = (uriProviderView.getWidth() - uriProviderView.getPaddingLeft()) - uriProviderView.getPaddingRight();
                int srcHeight = (uriProviderView.getHeight() - uriProviderView.getPaddingTop()) - uriProviderView.getPaddingBottom();
                int[] windowCoords = new int[2];
                uriProviderView.getLocationOnScreen(windowCoords);
                int srcX = windowCoords[0] + uriProviderView.getPaddingLeft();
                int srcY = windowCoords[1] + uriProviderView.getPaddingTop();
                if (rotation == 90 || rotation == 270) {
                    int tmp = realWidth;
                    realWidth = realHeight;
                    realHeight = tmp;
                }
                bundle = makeScaleUpAnimationBundle(uriProviderView.getContext(), uri, srcWidth, srcHeight, srcX, srcY, realWidth, realHeight);
                if (uriProviderView instanceof ResizeOptionsProvider) {
                    resizeOptionsToBundle((ResizeOptionsProvider) uriProviderView, bundle);
                }
            }
        }
        return bundle;
    }

    public static Bundle makeScaleDownAnimationBundle(int tgtWidth, int tgtHeight, int tgtX, int tgtY) {
        Bundle bundle = new Bundle();
        bundle.putInt("pla_tgt_wdth", tgtWidth);
        bundle.putInt("pla_tgt_hght", tgtHeight);
        bundle.putInt("pla_tgt_x", tgtX);
        bundle.putInt("pla_tgt_y", tgtY);
        return bundle;
    }

    static void fillExtraScaleDownParams(Bundle bundle, Uri uri, int srcWidth, int srcHeight, int srcX, int srcY, int scrollX, int scrollY, int bcgAlpha) {
        bundle.putParcelable("pla_image_uri", uri);
        bundle.putInt("pla_src_wdth", srcWidth);
        bundle.putInt("pla_src_hght", srcHeight);
        bundle.putInt("pla_src_x", srcX);
        bundle.putInt("pla_src_y", srcY);
        bundle.putInt("pla_src_scrlx", scrollX);
        bundle.putInt("pla_src_scrly", scrollY);
        bundle.putInt("pla_bcga", bcgAlpha);
    }

    public static Bundle makeScaleDownAnimationBundle(PhotoScaleDataProvider dataProvider) {
        if (dataProvider != null) {
            return makeScaleDownAnimationBundle(dataProvider.getDisplayedWidth(), dataProvider.getDisplayedHeight(), dataProvider.getDisplayedX(), dataProvider.getDisplayedY());
        }
        return null;
    }

    public static Bundle makeScaleDownAnimationBundle(View view) {
        if (view == null) {
            return null;
        }
        int tgtWidth = (view.getWidth() - view.getPaddingLeft()) - view.getPaddingRight();
        int tgtHeight = (view.getHeight() - view.getPaddingTop()) - view.getPaddingBottom();
        int[] windowCoords = new int[2];
        view.getLocationOnScreen(windowCoords);
        return makeScaleDownAnimationBundle(tgtWidth, tgtHeight, windowCoords[0] + view.getPaddingLeft(), windowCoords[1] + view.getPaddingTop());
    }

    static Animator startScaleUpAnimation(TransformBitmapView transformView, Bundle animationBundle, CloseableReference<CloseableImage> bitmapReference, AnimatorListener listener) {
        Logger.m173d("ScaleUp. Source. (%d, %d; %d, %d)", Integer.valueOf(animationBundle.getInt("pla_src_wdth")), Integer.valueOf(animationBundle.getInt("pla_src_hght")), Integer.valueOf(animationBundle.getInt("pla_src_x")), Integer.valueOf(animationBundle.getInt("pla_src_y")));
        int[] transformViewCoords = new int[2];
        transformView.getLocationOnScreen(transformViewCoords);
        transformView.cancelAnimation();
        transformView.setBitmapReference(bitmapReference);
        transformView.setScaleType(ScaleType.CENTER_CROP);
        transformView.setBackgroundAlpha(0);
        transformView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        transformView.setWidth(srcWidth);
        transformView.setHeight(srcHeight);
        transformView.setX(srcX - transformViewCoords[0]);
        transformView.setY(srcY - transformViewCoords[1]);
        int containerWidth = transformView.getWidth();
        int containerHeight = transformView.getHeight();
        int widthToUse = animationBundle.getInt("pla_rlp_wdth");
        int heightToUse = animationBundle.getInt("pla_rlp_hght");
        float imgMinScale = Math.min(((float) containerWidth) / ((float) widthToUse), ((float) containerHeight) / ((float) heightToUse));
        int tgtWidth = (int) (((float) widthToUse) * imgMinScale);
        int tgtHeight = (int) (((float) heightToUse) * imgMinScale);
        int tgtX = (containerWidth - tgtWidth) / 2;
        int tgtY = (containerHeight - tgtHeight) / 2;
        return transformView.transform().m164x(tgtX).m165y(tgtY).width(tgtWidth).height(tgtHeight).backgroundAlpha(MotionEventCompat.ACTION_MASK).withListener(listener).withDuration(300).start();
    }

    static Animator startScaleDownAnimation(TransformBitmapView transformView, Bundle animationBundle, CloseableReference<CloseableImage> bitmapReference, AnimatorListener listener) {
        boolean isTopCrop = animationBundle.getBoolean("is_top_crop");
        int srcWidth = animationBundle.getInt("pla_src_wdth");
        int srcHeight = animationBundle.getInt("pla_src_hght");
        int srcX = animationBundle.getInt("pla_src_x");
        int srcY = animationBundle.getInt("pla_src_y") - animationBundle.getInt("pla_src_scrly");
        int srcAlpha = animationBundle.getInt("pla_bcga");
        Logger.m173d("ScaleDown. Target. (%d, %d; %d, %d)", Integer.valueOf(animationBundle.getInt("pla_tgt_wdth")), Integer.valueOf(animationBundle.getInt("pla_tgt_hght")), Integer.valueOf(animationBundle.getInt("pla_tgt_x")), Integer.valueOf(animationBundle.getInt("pla_tgt_y")));
        int[] transformViewCoords = new int[2];
        transformView.getLocationOnScreen(transformViewCoords);
        transformView.cancelAnimation();
        transformView.setIsTopCrop(isTopCrop);
        transformView.setScaleType(ScaleType.CENTER_CROP);
        transformView.setBitmapReference(bitmapReference);
        transformView.setWidth(srcWidth);
        transformView.setHeight(srcHeight);
        transformView.setX(srcX);
        transformView.setY(srcY);
        transformView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        transformView.setBackgroundAlpha(srcAlpha);
        return transformView.transform().m164x(animationBundle.getInt("pla_tgt_x") - transformViewCoords[0]).m165y(animationBundle.getInt("pla_tgt_y") - transformViewCoords[1]).width(animationBundle.getInt("pla_tgt_wdth")).height(animationBundle.getInt("pla_tgt_hght")).backgroundAlpha(0).withListener(listener).withDuration(300).start();
    }

    public static final Bundle sendMessage(Message message) {
        return SYNCBUS.message(message);
    }

    public static final void registerCallback(int what, MessageCallback messageCallback) {
        SYNCBUS.registerCallback(what, messageCallback);
    }

    public static final void unregisterCallback(int what, MessageCallback messageCallback) {
        SYNCBUS.unregisterCallback(what, messageCallback);
    }

    public static ResizeOptions bundleToResizeOptions(Bundle animationBundle) {
        int width = animationBundle.getInt("extra_photo_resize_width", -1);
        int height = animationBundle.getInt("extra_photo_resize_height", -1);
        if (width == -1 || height == -1) {
            return null;
        }
        return new ResizeOptions(width, height);
    }

    private static void resizeOptionsToBundle(ResizeOptionsProvider resizeOptionsProvider, Bundle bundle) {
        ResizeOptions resizeOptions = resizeOptionsProvider.getResizeOptions();
        if (resizeOptions != null) {
            bundle.putInt("extra_photo_resize_width", resizeOptions.width);
            bundle.putInt("extra_photo_resize_height", resizeOptions.height);
        }
    }
}
