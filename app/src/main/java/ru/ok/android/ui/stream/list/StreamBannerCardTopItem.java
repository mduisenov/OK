package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public class StreamBannerCardTopItem extends AbsStreamWithOptionsItem {
    private final BannerClickAction clickAction;
    private final String headerText;

    protected static class CardTopViewHolder extends OptionsViewHolder {
        public final TextView label;

        public CardTopViewHolder(View view) {
            super(view);
            this.label = (TextView) view.findViewById(2131625342);
        }
    }

    protected StreamBannerCardTopItem(String headerText, FeedWithState feed, BannerClickAction clickAction) {
        super(24, 4, 3, feed, true);
        this.headerText = headerText;
        this.clickAction = clickAction;
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new CardTopViewHolder(view);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        super.bindView(holder, streamItemViewController, layoutConfig);
        if (holder instanceof CardTopViewHolder) {
            CardTopViewHolder cardTopViewHolder = (CardTopViewHolder) holder;
            AbsStreamClickableItem.setupClick(cardTopViewHolder.itemView, streamItemViewController, this.clickAction);
            cardTopViewHolder.label.setText(this.headerText);
        }
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903468, parent, false);
    }
}
