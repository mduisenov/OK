package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.FeedFooterInfo;
import ru.ok.android.ui.stream.view.FeedFooterView;

public class StreamFeedFooterItem extends StreamItem {
    private final FeedFooterInfo info;

    protected StreamFeedFooterItem(FeedWithState feed, FeedFooterInfo info) {
        this(feed, info, true);
    }

    protected StreamFeedFooterItem(FeedWithState feed, FeedFooterInfo info, boolean isCardBottom) {
        int i;
        if (isCardBottom) {
            i = 4;
        } else {
            i = 1;
        }
        super(1, 1, i, feed);
        this.info = info;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent, StreamItemViewController streamItemViewController) {
        FeedFooterView feedFooterView = (FeedFooterView) inflater.inflate(2130903476, parent, false);
        feedFooterView.setOnCommentsClickListener(streamItemViewController.getCommentsClickListener());
        feedFooterView.setOnLikeListener(streamItemViewController.getLikeClickListener());
        return feedFooterView;
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder.itemView instanceof FeedFooterView) {
            holder.itemView.setInfo(this.info);
            if (this.bottomEdgeType == 4) {
                holder.itemView.setBackgroundResource(2130837896);
            } else {
                holder.itemView.setBackgroundResource(2130838679);
            }
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }
}
