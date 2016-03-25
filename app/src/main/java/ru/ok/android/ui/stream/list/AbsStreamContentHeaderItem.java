package ru.ok.android.ui.stream.list;

import android.view.View;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.FeedHeaderInfo;
import ru.ok.android.ui.stream.view.FeedHeaderView;

public abstract class AbsStreamContentHeaderItem extends AbsStreamWithOptionsItem {
    final FeedHeaderInfo info;

    protected AbsStreamContentHeaderItem(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feedWithState, FeedHeaderInfo info, boolean canShowOptions) {
        super(viewType, topEdgeType, bottomEdgeType, feedWithState, canShowOptions);
        this.info = info;
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        ViewHolder holder = AbsStreamWithOptionsItem.newViewHolder(view, streamItemViewController);
        FeedHeaderView feedHeaderView = null;
        if (holder.itemView instanceof FeedHeaderView) {
            feedHeaderView = holder.itemView;
            feedHeaderView.setListener(streamItemViewController.getFeedHeaderViewListener());
            feedHeaderView.setDebugMode(streamItemViewController.isDebugMode());
        }
        if ((holder instanceof OptionsViewHolder) && feedHeaderView != null) {
            feedHeaderView.disableDrawableStateChange(((OptionsViewHolder) holder).optionsView);
        }
        return holder;
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder.itemView instanceof FeedHeaderView) {
            holder.itemView.setFeedHeaderInfo(this.info);
            if (this.topEdgeType == 4) {
                holder.itemView.setBackgroundResource(2130837910);
            } else {
                holder.itemView.setBackgroundResource(2130838679);
            }
            boolean isPinned = this.info != null && this.info.feed.feed.isPinned();
            setPaddingTop(isPinned ? -holder.itemView.getResources().getDimensionPixelOffset(2131230969) : holder.originalTopPadding);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    boolean sharePressedState() {
        return false;
    }
}
