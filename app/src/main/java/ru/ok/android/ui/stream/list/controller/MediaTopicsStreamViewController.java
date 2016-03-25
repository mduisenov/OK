package ru.ok.android.ui.stream.list.controller;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;
import ru.ok.android.ui.stream.view.MediaTopicOptionsPopupWindow;
import ru.ok.android.ui.stream.view.MediaTopicOptionsPopupWindow.MediaTopicFeedOptionsPopupListener;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.Feed;

public class MediaTopicsStreamViewController extends DefaultStreamViewController {
    protected final MediaTopicFeedOptionsPopupListener mediaTopicFeedOptionsPopupListener;
    private final MediaTopicsStreamAdapterListener mediaTopicsStreamAdapterListener;
    private final OnClickListener optionsClickListener;
    private boolean topicPinEnabled;
    private boolean topicToStatusEnabled;

    public interface MediaTopicsStreamAdapterListener {
        void onMediaTopicPinClicked(int i, Feed feed);

        void onMediaTopicSetToStatusClicked(int i, Feed feed);

        void onMediaTopicUnPinClicked(int i, Feed feed);
    }

    /* renamed from: ru.ok.android.ui.stream.list.controller.MediaTopicsStreamViewController.1 */
    class C12521 implements MediaTopicFeedOptionsPopupListener {
        C12521() {
        }

        public void onSetToStatusClicked(int position, Feed feed) {
            MediaTopicsStreamViewController.this.optionsWindow.dismiss();
            MediaTopicsStreamViewController.this.mediaTopicsStreamAdapterListener.onMediaTopicSetToStatusClicked(position, feed);
        }

        public void onPinClicked(int position, Feed feed) {
            MediaTopicsStreamViewController.this.optionsWindow.dismiss();
            MediaTopicsStreamViewController.this.mediaTopicsStreamAdapterListener.onMediaTopicPinClicked(position, feed);
        }

        public void onUnPinClicked(int position, Feed feed) {
            MediaTopicsStreamViewController.this.optionsWindow.dismiss();
            MediaTopicsStreamViewController.this.mediaTopicsStreamAdapterListener.onMediaTopicUnPinClicked(position, feed);
        }

        public void onMarkAsSpamClicked(int position, Feed feed, int itemAdapterPosition) {
            MediaTopicsStreamViewController.this.optionsWindow.dismiss();
            MediaTopicsStreamViewController.this.getStreamAdapterListener().onMarkAsSpamClicked(position, feed, itemAdapterPosition);
        }

        public void onDeleteClicked(int position, Feed feed, int itemAdapterPosition) {
            MediaTopicsStreamViewController.this.optionsWindow.dismiss();
            MediaTopicsStreamViewController.this.getStreamAdapterListener().onDeleteClicked(position, feed, itemAdapterPosition);
        }
    }

    /* renamed from: ru.ok.android.ui.stream.list.controller.MediaTopicsStreamViewController.2 */
    class C12532 implements OnClickListener {
        C12532() {
        }

        public void onClick(View optionsView) {
            if (MediaTopicsStreamViewController.this.optionsWindow == null) {
                MediaTopicsStreamViewController.this.optionsWindow = new MediaTopicOptionsPopupWindow(MediaTopicsStreamViewController.this.getActivity(), MediaTopicsStreamViewController.this.mediaTopicFeedOptionsPopupListener);
                ((MediaTopicOptionsPopupWindow) MediaTopicsStreamViewController.this.optionsWindow).setSetToStatusEnabled(MediaTopicsStreamViewController.this.topicToStatusEnabled);
                ((MediaTopicOptionsPopupWindow) MediaTopicsStreamViewController.this.optionsWindow).setPinEnabled(MediaTopicsStreamViewController.this.topicPinEnabled);
            } else if (MediaTopicsStreamViewController.this.optionsWindow.isShowing()) {
                MediaTopicsStreamViewController.this.optionsWindow.dismiss();
                return;
            }
            FeedWithState feed = (FeedWithState) optionsView.getTag(2131624322);
            Logger.m173d("onOptionsClicked: feed=%s itemAdapterPosition=%d", feed, Integer.valueOf(((Integer) optionsView.getTag(2131624311)).intValue()));
            MediaTopicsStreamViewController.this.optionsWindow.setFeed(feed.position, feed.feed, itemAdapterPosition);
            MediaTopicsStreamViewController.this.optionsWindow.show(optionsView);
        }
    }

    public MediaTopicsStreamViewController(Activity activity, StreamAdapterListener listener, MediaTopicsStreamAdapterListener mediaTopicsStreamAdapterListener, String logContext) {
        super(activity, listener, logContext);
        this.mediaTopicFeedOptionsPopupListener = new C12521();
        this.optionsClickListener = new C12532();
        this.mediaTopicsStreamAdapterListener = mediaTopicsStreamAdapterListener;
    }

    public OnClickListener getOptionsClickListener() {
        return this.optionsClickListener;
    }

    public void setTopicToStatusEnabled(boolean topicToStatusEnabled) {
        this.topicToStatusEnabled = topicToStatusEnabled;
    }

    public void setTopicCanPinEnabled(boolean topicPinEnabled) {
        this.topicPinEnabled = topicPinEnabled;
    }

    public boolean isOptionsButtonVisible(Feed feed) {
        return MediaTopicOptionsPopupWindow.isOptionsButtonVisible(feed, this.topicToStatusEnabled, this.topicPinEnabled);
    }
}
