package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.VideoThumbView;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.PhotoUtil;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.stream.entities.FeedVideoEntity;

public class StreamVideoItem extends AbsStreamVideoItem {
    final FeedVideoEntity video;

    protected StreamVideoItem(FeedWithState feed, FeedVideoEntity video) {
        super(15, 2, 2, feed);
        this.video = video;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903513, parent, false);
    }

    public void prefetch() {
        PhotoSize size = PhotoUtil.getClosestSize(DeviceUtils.getStreamHighQualityPhotoWidth(), 0, this.video.thumbnailUrls);
        if (size != null) {
            PrefetchUtils.prefetchUrl(size.getUrl());
        }
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder.itemView instanceof VideoThumbView) {
            clearSiblingVideoTags(holder.itemView);
            VideoThumbView videoThumbView = holder.itemView;
            videoThumbView.setVideo(this.video);
            videoThumbView.setTag(2131624321, this.video);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }
}
