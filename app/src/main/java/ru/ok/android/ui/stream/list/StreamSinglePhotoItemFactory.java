package ru.ok.android.ui.stream.list;

import android.support.annotation.NonNull;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.app.GifAsMp4PlayerHelper.AutoplayContext;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;

public class StreamSinglePhotoItemFactory {
    @NonNull
    public static AbsStreamSinglePhotoItem createStreamSinglePhotoActionsItem(FeedWithState feed, AbsFeedPhotoEntity photo, MediaItemPhoto mediaItem, float aspectRatio, boolean lastInFeed) {
        int bottomEdgeType = lastInFeed ? 4 : 2;
        return GifAsMp4PlayerHelper.shouldPlayGifAsMp4InPlace(photo.getPhotoInfo(), AutoplayContext.FEED) ? new StreamSingleGifAsMp4PhotoActionsItem(bottomEdgeType, feed, photo, mediaItem, aspectRatio) : new StreamSingleStaticPhotoActionsItem(bottomEdgeType, feed, photo, mediaItem, aspectRatio);
    }

    @NonNull
    public static AbsStreamSinglePhotoItem createStreamSinglePhotoItem(FeedWithState feed, AbsFeedPhotoEntity photo, MediaItemPhoto mediaItem) {
        return GifAsMp4PlayerHelper.shouldPlayGifAsMp4InPlace(photo.getPhotoInfo(), AutoplayContext.FEED) ? new StreamSingleGifAsMp4PhotoItem(feed, photo, mediaItem) : new StreamSingleStaticPhotoItem(feed, photo, mediaItem);
    }
}
