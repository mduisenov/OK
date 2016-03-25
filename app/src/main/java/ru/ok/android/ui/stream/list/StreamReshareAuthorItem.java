package ru.ok.android.ui.stream.list;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.FeedHeaderInfo;
import ru.ok.android.ui.stream.view.FeedHeaderView;

public class StreamReshareAuthorItem extends AbsStreamWithOptionsItem {
    private final FeedHeaderInfo info;

    protected StreamReshareAuthorItem(FeedWithState feed, FeedHeaderInfo info) {
        super(31, 3, 3, feed, true);
        this.info = info;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903498, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder.itemView instanceof FeedHeaderView) {
            holder.itemView.setFeedHeaderInfo(this.info);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        FeedHeaderView feedHeaderView = null;
        if (view instanceof FeedHeaderView) {
            feedHeaderView = (FeedHeaderView) view;
            feedHeaderView.setListener(streamItemViewController.getFeedReshareHeaderViewListener());
        }
        ViewHolder holder = AbsStreamWithOptionsItem.newViewHolder(view, streamItemViewController);
        if ((holder instanceof OptionsViewHolder) && feedHeaderView != null) {
            feedHeaderView.disableDrawableStateChange(((OptionsViewHolder) holder).optionsView);
        }
        return holder;
    }

    int getVSpacingBottom(Context context) {
        return (int) TypedValue.applyDimension(1, 8.0f, context.getResources().getDisplayMetrics());
    }

    boolean sharePressedState() {
        return false;
    }
}
