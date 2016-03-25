package ru.ok.android.ui.stream.view;

import android.content.Context;
import android.text.TextUtils;
import ru.ok.android.ui.stream.view.OptionsPopupWindow.OptionsPopupListener;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.Feed;

public class FeedOptionsPopupWindow extends OptionsPopupWindow {
    private final FeedOptionsPopupListener adapterListener;
    protected Feed feed;
    protected int feedPosition;
    protected int itemAdapterPosition;

    public interface FeedOptionsPopupListener {
        void onDeleteClicked(int i, Feed feed, int i2);

        void onMarkAsSpamClicked(int i, Feed feed, int i2);
    }

    class DefaultPopupListener implements OptionsPopupListener {
        DefaultPopupListener() {
        }

        public void onMarkAsSpamClicked() {
            Logger.m173d("Mark as spam feed clicked: %d", Long.valueOf(FeedOptionsPopupWindow.this.feed.getId()));
            FeedOptionsPopupWindow.this.adapterListener.onMarkAsSpamClicked(FeedOptionsPopupWindow.this.feedPosition, FeedOptionsPopupWindow.this.feed, FeedOptionsPopupWindow.this.itemAdapterPosition);
        }

        public void onDeleteClicked() {
            Logger.m173d("delete feed clicked: %d", Long.valueOf(FeedOptionsPopupWindow.this.feed.getId()));
            FeedOptionsPopupWindow.this.adapterListener.onDeleteClicked(FeedOptionsPopupWindow.this.feedPosition, FeedOptionsPopupWindow.this.feed, FeedOptionsPopupWindow.this.itemAdapterPosition);
        }
    }

    public FeedOptionsPopupWindow(Context context, FeedOptionsPopupListener listener) {
        super(context);
        this.adapterListener = listener;
        setListener(new DefaultPopupListener());
    }

    public static boolean isOptionsButtonVisible(Feed feed) {
        return (TextUtils.isEmpty(feed.getSpamId()) && TextUtils.isEmpty(feed.getDeleteId())) ? false : true;
    }

    public void setFeed(int feedPosition, Feed feed, int itemAdapterPosition) {
        boolean z;
        boolean z2 = true;
        this.feedPosition = feedPosition;
        this.feed = feed;
        this.itemAdapterPosition = itemAdapterPosition;
        if (TextUtils.isEmpty(feed.getDeleteId())) {
            z = false;
        } else {
            z = true;
        }
        if (TextUtils.isEmpty(feed.getSpamId())) {
            z2 = false;
        }
        setOptionVisible(z, z2);
    }
}
