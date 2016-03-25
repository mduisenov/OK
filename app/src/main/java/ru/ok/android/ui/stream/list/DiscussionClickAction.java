package ru.ok.android.ui.stream.list;

import android.view.View;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.model.stream.DiscussionSummary;

public class DiscussionClickAction implements ClickAction {
    private final DiscussionSummary discussionSummary;
    private final FeedWithState feedWithState;

    public DiscussionClickAction(FeedWithState feedWithState, DiscussionSummary discussionSummary) {
        this.feedWithState = feedWithState;
        this.discussionSummary = discussionSummary;
    }

    public void setClickListener(View view, StreamItemViewController streamItemViewController) {
        view.setOnClickListener(streamItemViewController.getShowMoreClickListener());
    }

    public void setTags(View view) {
        view.setTag(2131624322, this.feedWithState);
        view.setTag(2131624317, this.discussionSummary);
        view.setTag(2131624342, this.feedWithState.feed);
    }
}
