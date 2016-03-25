package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public class StreamStubItem extends StreamItem {
    private final String text;

    private static class ViewHolder extends ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder {
        public final TextView textView;

        public ViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(C0263R.id.text);
        }
    }

    public StreamStubItem(FeedWithState feed, String text) {
        super(45, 1, 1, feed);
        this.text = text;
    }

    public void bindView(ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        super.bindView(holder, streamItemViewController, layoutConfig);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).textView.setText(this.text);
        }
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903506, parent, false);
    }

    public static ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder newViewHolder(View v, StreamItemViewController controller) {
        return new ViewHolder(v);
    }
}
