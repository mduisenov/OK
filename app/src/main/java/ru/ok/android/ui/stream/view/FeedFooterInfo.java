package ru.ok.android.ui.stream.view;

import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.model.stream.ActionCountInfo;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.LikeInfoContext;

public final class FeedFooterInfo {
    public final DiscussionSummary discussionSummary;
    public final FeedWithState feed;
    public LikeInfoContext klassInfo;
    public final ActionCountInfo shareInfo;

    public FeedFooterInfo(FeedWithState feed, LikeInfoContext klassInfo, DiscussionSummary discussionSummary, ActionCountInfo shareInfo) {
        this.feed = feed;
        this.klassInfo = klassInfo;
        this.discussionSummary = discussionSummary;
        this.shareInfo = shareInfo;
    }
}
