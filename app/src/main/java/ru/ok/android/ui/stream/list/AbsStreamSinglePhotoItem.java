package ru.ok.android.ui.stream.list;

import android.view.View;
import java.util.Collections;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.StreamUtils;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;

public abstract class AbsStreamSinglePhotoItem extends AbsStreamClickableItem {
    private final float aspectRatio;
    protected final AbsFeedPhotoEntity photo;
    private final List<PhotoInfo> tagPhotos;

    protected static class InnerViewHolder extends ViewHolder {
        public InnerViewHolder(View view) {
            super(view);
        }
    }

    protected AbsStreamSinglePhotoItem(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feed, AbsFeedPhotoEntity photo, MediaItemPhoto mediaItem, float aspectRatio) {
        super(viewType, topEdgeType, bottomEdgeType, feed, new PhotoClickAction(feed, photo, mediaItem));
        this.aspectRatio = aspectRatio;
        this.photo = photo;
        this.tagPhotos = Collections.singletonList(photo.getPhotoInfo());
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        super.bindView(holder, streamItemViewController, layoutConfig);
        holder.itemView.setTag(2131624334, this.tagPhotos);
    }

    protected boolean needToResizeView() {
        return false;
    }

    protected int calculateMaximumWidth() {
        if (!needToResizeView()) {
            return Integer.MAX_VALUE;
        }
        return (int) (((float) ((int) (((float) this.photo.getPhotoInfo().getStandartWidth()) * ((float) (OdnoklassnikiApplication.getContext().getResources().getDimensionPixelSize(2131230982) / 120))))) * 1.5f);
    }

    public float getAspectRatio() {
        return this.aspectRatio;
    }

    void applyExtraMarginsToPaddings(ViewHolder holder, int extraLeftMargin, int extraRightMargin, StreamLayoutConfig layoutConfig) {
        if (holder instanceof InnerViewHolder) {
            StreamUtils.applyExtraMarginsToLandscapeImagePaddings(holder, layoutConfig);
        } else {
            super.applyExtraMarginsToPaddings(holder, extraLeftMargin, extraRightMargin, layoutConfig);
        }
    }

    boolean sharePressedState() {
        return false;
    }
}
