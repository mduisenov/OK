package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public class StreamCardBottomItem extends AbsStreamClickableItem {
    private int height;

    static class CardBottomHolder extends ViewHolder {
        final int defaultHeight;

        public CardBottomHolder(View view) {
            super(view);
            LayoutParams lp = view.getLayoutParams();
            if (lp != null) {
                this.defaultHeight = lp.height;
            } else {
                this.defaultHeight = view.getResources().getDimensionPixelOffset(2131231000);
            }
        }
    }

    protected StreamCardBottomItem(FeedWithState feed, ClickAction clickAction) {
        super(22, 1, 4, feed, clickAction);
        this.height = -1;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof CardBottomHolder) {
            LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null) {
                if (this.height == -1) {
                    lp.height = ((CardBottomHolder) holder).defaultHeight;
                } else {
                    lp.height = this.height;
                }
            }
            holder.itemView.setLayoutParams(lp);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903472, parent, false);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new CardBottomHolder(view);
    }
}
