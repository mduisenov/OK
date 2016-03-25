package ru.ok.android.ui.stream.list;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.fresco.FrescoGifMarkerView;
import ru.ok.android.ui.custom.imageview.ViewWithSlideMode;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.TwoPhotoCollageItem.PhotoLocate;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;

public class PhotoCollagePart {
    private final PhotoClickAction clickAction;
    private final Uri highUri;
    private final Uri lowUri;
    private boolean panorama;
    private final AbsFeedPhotoEntity photoEntity;
    private final PhotoLocate photoLocate;

    /* renamed from: ru.ok.android.ui.stream.list.PhotoCollagePart.1 */
    class C12351 extends BaseControllerListener<ImageInfo> {
        final /* synthetic */ FrescoGifMarkerView val$imageView;

        C12351(FrescoGifMarkerView frescoGifMarkerView) {
            this.val$imageView = frescoGifMarkerView;
        }

        public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
            if ((this.val$imageView instanceof ViewWithSlideMode) && imageInfo != null) {
                ((GenericDraweeHierarchy) this.val$imageView.getHierarchy()).setActualImageScaleType(ScaleType.FOCUS_CROP);
                this.val$imageView.setImageDimensions(imageInfo.getHeight(), imageInfo.getWidth());
            }
        }
    }

    public PhotoCollagePart(FeedWithState feedWithState, AbsFeedPhotoEntity photoEntity, PhotoLocate locate, @Nullable MediaItemPhoto itemPhoto, int photosInRow) {
        Uri uri = null;
        this.clickAction = new PhotoClickAction(feedWithState, photoEntity, itemPhoto);
        this.photoEntity = photoEntity;
        int lowQualitySize = DeviceUtils.getStreamLowQualityPhotoWidth() / photosInRow;
        String highUrl = photoEntity.getPhotoInfo().getSizeFloorUrl(DeviceUtils.getStreamHighQualityPhotoWidth() / photosInRow);
        this.highUri = TextUtils.isEmpty(highUrl) ? null : Uri.parse(highUrl);
        String lowUrl = photoEntity.getPhotoInfo().getSizeFloorUrl(lowQualitySize);
        if (!TextUtils.isEmpty(lowUrl)) {
            uri = Uri.parse(lowUrl);
        }
        this.lowUri = uri;
        this.photoLocate = locate;
        this.panorama = ((float) photoEntity.getPhotoInfo().getStandartWidth()) / ((float) photoEntity.getPhotoInfo().getStandartHeight()) > 3.0f;
    }

    PhotoInfo getPhotoInfo() {
        return this.photoEntity.getPhotoInfo();
    }

    public void bindView(ViewHolder holder, View view, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (view instanceof FrescoGifMarkerView) {
            FrescoGifMarkerView imageView = (FrescoGifMarkerView) view;
            imageView.setShouldDrawGifMarker(GifAsMp4PlayerHelper.shouldShowGifAsMp4(this.photoEntity.getPhotoInfo()));
            this.photoLocate.locate(holder, imageView, layoutConfig);
            imageView.setTag(2131624320, this.photoEntity);
            imageView.setUri(this.highUri);
            imageView.setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setRetainImageOnFailure(true)).setUri(this.highUri).setOldController(imageView.getController())).setControllerListener(new C12351(imageView))).setLowResImageRequest(ImageRequest.fromUri(this.lowUri))).build());
            AbsStreamClickableItem.setupClick(imageView, streamItemViewController, this.clickAction);
        }
    }

    public void prefetch() {
        PrefetchUtils.prefetchUrl(this.lowUri, false);
    }

    public boolean isPanorama() {
        return this.panorama;
    }
}
