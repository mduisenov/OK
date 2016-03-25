package ru.ok.android.ui.stream.list;

import java.util.TreeSet;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.VideoThumbView;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.PhotoUtil;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.stream.banner.VideoData;

public class StreamVideoBannerItem extends AbsStreamVideoItem {
    final TreeSet<PhotoSize> thumbs;
    final VideoData videoData;

    protected StreamVideoBannerItem(FeedWithState feed, VideoData videoData, TreeSet<PhotoSize> thumbs) {
        super(15, 2, 2, feed);
        this.videoData = videoData;
        this.thumbs = thumbs;
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder.itemView instanceof VideoThumbView) {
            VideoThumbView view = holder.itemView;
            clearSiblingVideoTags(view);
            view.setVideo(this.thumbs, null, this.videoData.durationSec);
            view.setTag(2131624348, this.videoData.videoUrl);
            view.setTag(2131624347, this.videoData);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public void prefetch() {
        PhotoSize size = PhotoUtil.getClosestSize(DeviceUtils.getStreamHighQualityPhotoWidth(), 0, this.thumbs);
        if (size != null) {
            PrefetchUtils.prefetchUrl(size.getUrl());
        }
    }
}
