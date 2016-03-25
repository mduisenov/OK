package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public class StreamBannerCardBottomItem extends AbsStreamClickableItem {
    final CharSequence text;

    public static class ViewHolder extends ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(2131625339);
        }

        public void setText(CharSequence text) {
            if (this.textView != null) {
                this.textView.setText(text);
            }
        }
    }

    protected StreamBannerCardBottomItem(FeedWithState feed, CharSequence text, BannerClickAction clickAction) {
        super(25, 1, 4, feed, clickAction);
        this.text = text;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903467, parent, false);
    }

    public static ViewHolder newViewHolder(View view) {
        return new ViewHolder(view);
    }

    public void bindView(ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).setText(this.text);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }
}
