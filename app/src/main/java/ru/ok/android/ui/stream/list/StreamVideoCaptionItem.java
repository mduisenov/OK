package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.model.stream.entities.FeedVideoEntity;

public class StreamVideoCaptionItem extends StreamItem {
    final FeedVideoEntity video;

    static class StreamVideoCaptionItemViewHolder extends ViewHolder {
        final TextView text;

        public StreamVideoCaptionItemViewHolder(View view, StreamItemViewController streamItemViewController) {
            super(view);
            TextView tv = (TextView) view.findViewById(C0263R.id.text);
            if (tv == null && (view instanceof TextView)) {
                tv = (TextView) view;
            }
            this.text = tv;
            if (this.text != null) {
                this.text.setOnClickListener(streamItemViewController.getVideoClickListener());
            }
        }
    }

    protected StreamVideoCaptionItem(FeedWithState feed, FeedVideoEntity video) {
        super(16, 2, 2, feed);
        this.video = video;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903514, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof StreamVideoCaptionItemViewHolder) {
            StreamVideoCaptionItemViewHolder streamVideoCaptionItemViewHolder = (StreamVideoCaptionItemViewHolder) holder;
            streamVideoCaptionItemViewHolder.text.setText(this.video.title);
            streamVideoCaptionItemViewHolder.itemView.setTag(2131624322, this.feedWithState);
            streamVideoCaptionItemViewHolder.itemView.setTag(2131624321, this.video);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new StreamVideoCaptionItemViewHolder(view, streamItemViewController);
    }

    boolean sharePressedState() {
        return true;
    }
}
