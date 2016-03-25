package ru.ok.android.ui.stream.list;

import android.content.res.Resources;
import android.view.View;
import ru.ok.android.ui.custom.TouchFloatingDelegate;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public abstract class AbsStreamWithOptionsItem extends StreamItemAdjustablePaddings {
    private final boolean canShowOptions;

    protected static class OptionsViewHolder extends ViewHolder {
        public final View optionsView;

        public OptionsViewHolder(View view) {
            super(view);
            this.optionsView = view.findViewById(2131625328);
        }
    }

    protected AbsStreamWithOptionsItem(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feedWithState, boolean canShowOptions) {
        super(viewType, topEdgeType, bottomEdgeType, feedWithState);
        this.canShowOptions = canShowOptions;
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        OptionsViewHolder holder = new OptionsViewHolder(view);
        if (holder.optionsView != null) {
            holder.optionsView.setOnClickListener(streamItemViewController.getOptionsClickListener());
            Resources res = holder.optionsView.getResources();
            int offsetH = res.getDimensionPixelSize(2131231099);
            int offsetV = res.getDimensionPixelSize(2131231102);
            ((View) holder.optionsView.getParent()).setTouchDelegate(new TouchFloatingDelegate(holder.optionsView, offsetH, offsetV, offsetH, offsetV));
        }
        return holder;
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof OptionsViewHolder) {
            View optionsView = ((OptionsViewHolder) holder).optionsView;
            if (optionsView != null) {
                boolean optionsVisible;
                if (this.canShowOptions && streamItemViewController.isOptionsButtonVisible(this.feedWithState.feed)) {
                    optionsVisible = true;
                } else {
                    optionsVisible = false;
                }
                optionsView.setVisibility(optionsVisible ? 0 : 4);
                optionsView.setClickable(optionsVisible);
                optionsView.setOnClickListener(optionsVisible ? streamItemViewController.getOptionsClickListener() : null);
                optionsView.setTag(2131624322, this.feedWithState);
                optionsView.setTag(2131624311, Integer.valueOf(holder.adapterPosition));
            }
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }
}
