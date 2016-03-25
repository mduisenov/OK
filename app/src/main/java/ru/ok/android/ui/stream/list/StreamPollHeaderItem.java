package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.model.stream.DiscussionSummary;

public class StreamPollHeaderItem extends AbsStreamClickableItem {
    private final CharSequence text;

    protected StreamPollHeaderItem(FeedWithState feed, CharSequence text, DiscussionSummary discussionSummary) {
        super(17, 3, 3, feed, discussionSummary == null ? null : new DiscussionClickAction(feed, discussionSummary));
        this.text = text;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903495, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder.itemView instanceof TextView) {
            holder.itemView.setText(this.text);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }
}
