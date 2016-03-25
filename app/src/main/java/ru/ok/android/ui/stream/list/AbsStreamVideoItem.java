package ru.ok.android.ui.stream.list;

import android.view.View;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.VideoThumbView;
import ru.ok.android.utils.StreamUtils;

public abstract class AbsStreamVideoItem extends StreamItem {
    protected AbsStreamVideoItem(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feed) {
        super(viewType, topEdgeType, bottomEdgeType, feed);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        StreamUtils.applyExtraMarginsToLandscapeImagePaddings(holder, layoutConfig);
        holder.itemView.setTag(2131624322, this.feedWithState);
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    protected void clearSiblingVideoTags(View view) {
        view.setTag(2131624348, null);
        view.setTag(2131624347, null);
        view.setTag(2131624321, null);
    }

    public void updateForLayoutSize(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        super.updateForLayoutSize(holder, streamItemViewController, layoutConfig);
        if (holder.itemView instanceof VideoThumbView) {
            StreamUtils.applyExtraMarginsToLandscapeImagePaddings(holder, layoutConfig);
        }
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        view.setOnClickListener(streamItemViewController.getVideoClickListener());
        return StreamItem.newViewHolder(view);
    }

    boolean sharePressedState() {
        return true;
    }
}
