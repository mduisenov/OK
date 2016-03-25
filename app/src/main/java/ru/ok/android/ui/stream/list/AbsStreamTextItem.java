package ru.ok.android.ui.stream.list;

import android.view.View;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.text.OdklUrlsTextView;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.model.stream.DiscussionSummary;

public abstract class AbsStreamTextItem<TText extends CharSequence> extends AbsStreamClickableItem {
    final TText text;

    static class ViewHolder extends ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder {
        private final OdklUrlsTextView odklUrlsTextView;
        final TextView text;

        public ViewHolder(View view) {
            super(view);
            TextView tv = (TextView) view.findViewById(C0263R.id.text);
            if (tv == null && (view instanceof TextView)) {
                tv = (TextView) view;
            }
            this.text = tv;
            this.odklUrlsTextView = this.text instanceof OdklUrlsTextView ? (OdklUrlsTextView) this.text : null;
        }
    }

    protected AbsStreamTextItem(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feed, TText text, DiscussionSummary discussionSummary) {
        super(viewType, topEdgeType, bottomEdgeType, feed, discussionSummary == null ? null : new DiscussionClickAction(feed, discussionSummary));
        this.text = text;
    }

    protected AbsStreamTextItem(int viewType, int topEdgeType, int bottomEdgeType, FeedWithState feed, TText text, ClickAction clickAction) {
        super(viewType, topEdgeType, bottomEdgeType, feed, clickAction);
        this.text = text;
    }

    public void bindView(ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof ViewHolder) {
            ViewHolder absViewHolder = (ViewHolder) holder;
            if (absViewHolder.odklUrlsTextView != null) {
                absViewHolder.odklUrlsTextView.setText(this.text);
            } else if (absViewHolder.text != null) {
                absViewHolder.text.setText(this.text);
            }
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        ViewHolder viewHolder = new ViewHolder(view);
        if (viewHolder.odklUrlsTextView != null) {
            viewHolder.odklUrlsTextView.setLinkListener(streamItemViewController.getTextViewLinkListener());
        }
        return viewHolder;
    }

    boolean sharePressedState() {
        return super.sharePressedState();
    }

    public String toString() {
        return String.format("AbsStreamTextItem{text %s}", new Object[]{this.text});
    }
}
