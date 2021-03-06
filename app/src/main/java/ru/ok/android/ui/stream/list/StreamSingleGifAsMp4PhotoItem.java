package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;

public class StreamSingleGifAsMp4PhotoItem extends AbsStreamSingleGifAsMp4PhotoItem {
    protected StreamSingleGifAsMp4PhotoItem(FeedWithState feed, AbsFeedPhotoEntity photo, MediaItemPhoto mediaItem) {
        super(37, 2, 2, feed, photo, mediaItem, photo.getPhotoInfo().calculateAspectRatio());
    }

    protected boolean needToResizeView() {
        return true;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903502, parent, false);
    }
}
