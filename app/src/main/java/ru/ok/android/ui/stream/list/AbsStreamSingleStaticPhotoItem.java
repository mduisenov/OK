package ru.ok.android.ui.stream.list;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.imagepipeline.request.ImageRequest;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fresco.FrescoGifMarkerView;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;

public abstract class AbsStreamSingleStaticPhotoItem extends AbsStreamSinglePhotoItem {
    private final Uri imageUri;
    private final Uri imageUriLowQuality;

    static class StaticImageViewHolder extends InnerViewHolder {
        final TextView commentTextView;
        final FrescoGifMarkerView imageView;

        public StaticImageViewHolder(View view, StreamItemViewController streamItemViewController) {
            super(view);
            this.imageView = (FrescoGifMarkerView) view.findViewById(C0263R.id.image);
            this.commentTextView = (TextView) view.findViewById(2131624887);
        }

        public void setImageListener(PipelineDraweeControllerBuilder controller) {
        }
    }

    protected AbsStreamSingleStaticPhotoItem(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feed, AbsFeedPhotoEntity photo, MediaItemPhoto mediaItem, float aspectRatio) {
        super(viewType, topEdgeType, bottomEdgeType, feed, photo, mediaItem, aspectRatio);
        PhotoInfo photoInfo = photo.getPhotoInfo();
        this.imageUri = calculateHighQualityPhotoUri(photoInfo);
        this.imageUriLowQuality = calculateLowQualityPhotoUri(photoInfo);
    }

    private Uri calculateHighQualityPhotoUri(@NonNull PhotoInfo photoInfo) {
        String url = photoInfo.getClosestSizeUrl(DeviceUtils.getStreamHighQualityPhotoWidth(), 0);
        return TextUtils.isEmpty(url) ? null : Uri.parse(url);
    }

    private Uri calculateLowQualityPhotoUri(@NonNull PhotoInfo photoInfo) {
        String url = photoInfo.getSizeFloorUrl(DeviceUtils.getStreamLowQualityPhotoWidth());
        return TextUtils.isEmpty(url) ? null : Uri.parse(url);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        boolean commentExist = true;
        int i = 0;
        if (holder instanceof StaticImageViewHolder) {
            FrescoGifMarkerView imageView = ((StaticImageViewHolder) holder).imageView;
            imageView.setAspectRatio(getAspectRatio());
            imageView.setMaximumWidth(calculateMaximumWidth());
            PipelineDraweeControllerBuilder controllerBuilder = (PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setRetainImageOnFailure(true)).setUri(this.imageUri).setOldController(imageView.getController())).setLowResImageRequest(ImageRequest.fromUri(this.imageUriLowQuality));
            ((StaticImageViewHolder) holder).setImageListener(controllerBuilder);
            imageView.setController(controllerBuilder.build());
            imageView.setUri(this.imageUri);
            imageView.setTag(2131624320, this.photo);
            ((GenericDraweeHierarchy) imageView.getHierarchy()).setFadeDuration(0);
            imageView.setShouldDrawGifMarker(GifAsMp4PlayerHelper.shouldShowGifAsMp4(this.photo.getPhotoInfo()));
            String comment = this.photo.getPhotoInfo().getComment();
            if (TextUtils.isEmpty(comment)) {
                commentExist = false;
            }
            TextView commentTextView = ((StaticImageViewHolder) holder).commentTextView;
            if (commentTextView != null) {
                if (!commentExist) {
                    i = 8;
                }
                commentTextView.setVisibility(i);
                if (commentExist) {
                    commentTextView.setText(comment);
                }
            }
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public float getAspectRatio() {
        return Math.max(0.5625f, super.getAspectRatio());
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new StaticImageViewHolder(view, streamItemViewController);
    }
}
