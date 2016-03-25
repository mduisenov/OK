package ru.ok.android.ui.stream.list;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.FeedFooterView;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;

public class StreamSingleGifAsMp4PhotoActionsItem extends AbsStreamSingleGifAsMp4PhotoItem {
    private final FeedFooterContextBinder mFeedFooterContextBinder;

    static class GifAsMp4ImageActionsViewHolder extends GifAsMp4ImageViewHolder implements FeedFooterViewHolder {
        public final FeedFooterViewHelper feedFooterViewHelper;

        GifAsMp4ImageActionsViewHolder(View view) {
            super(view);
            this.feedFooterViewHelper = new FeedFooterViewHelper();
        }

        public FeedFooterView getFeedFooterView(@NonNull StreamItemViewController streamItemViewController) {
            return this.feedFooterViewHelper.getView(this.itemView, streamItemViewController);
        }

        public void hideFeedFooterView() {
            this.feedFooterViewHelper.hideView();
        }

        public void setTagFor(FeedFooterView view) {
            this.animatedView.setTag(2131624319, view);
        }
    }

    protected StreamSingleGifAsMp4PhotoActionsItem(int bottomEdgeType, FeedWithState feed, AbsFeedPhotoEntity photo, MediaItemPhoto mediaItem, float aspectRatio) {
        super(38, 2, bottomEdgeType, feed, photo, mediaItem, aspectRatio);
        this.mFeedFooterContextBinder = new FeedFooterContextBinder(feed, photo);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new GifAsMp4ImageActionsViewHolder(view);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903503, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof GifAsMp4ImageActionsViewHolder) {
            GifAsMp4ImageActionsViewHolder vh = (GifAsMp4ImageActionsViewHolder) holder;
            this.mFeedFooterContextBinder.bind(streamItemViewController, vh, vh, this.photo);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }
}
