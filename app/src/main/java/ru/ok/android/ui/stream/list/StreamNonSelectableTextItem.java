package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public class StreamNonSelectableTextItem extends AbsStreamClickableItem {
    private final CharSequence text;

    private static class ViewHolder extends ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder {
        private final TextView text;

        public ViewHolder(View view) {
            super(view);
            TextView tv = (TextView) view.findViewById(C0263R.id.text);
            if (tv == null && (view instanceof TextView)) {
                tv = (TextView) view;
            }
            this.text = tv;
        }
    }

    protected StreamNonSelectableTextItem(FeedWithState feedWithState, CharSequence text, ClickAction clickAction) {
        super(34, 1, 1, feedWithState, clickAction);
        this.text = text;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903487, parent, false);
    }

    public static ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new ViewHolder(view);
    }

    public void bindView(ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof ViewHolder) {
            ViewHolder absViewHolder = (ViewHolder) holder;
            if (absViewHolder.text != null) {
                absViewHolder.text.setText(this.text);
            }
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    boolean sharePressedState() {
        return false;
    }
}
