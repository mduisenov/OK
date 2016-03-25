package ru.ok.android.ui.stream.list;

import android.support.annotation.NonNull;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.FeedFooterInfo;
import ru.ok.android.ui.stream.view.FeedFooterView;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;

public class FeedFooterContextBinder {
    private final FeedFooterInfo mFeedFooterInfo;

    public FeedFooterContextBinder(@NonNull FeedWithState feedWithState, @NonNull AbsFeedPhotoEntity photo) {
        LikeInfoContext likeInfo = photo.getLikeInfo();
        DiscussionSummary discussionSummary = photo.getDiscussionSummary();
        if (likeInfo == null && discussionSummary == null) {
            this.mFeedFooterInfo = null;
        } else {
            this.mFeedFooterInfo = new FeedFooterInfo(feedWithState, photo.getLikeInfo(), photo.getDiscussionSummary(), null);
        }
    }

    public void bind(@NonNull StreamItemViewController streamItemViewController, @NonNull ViewHolder holder, @NonNull FeedFooterViewHolder feedFooterViewHolder, @NonNull AbsFeedPhotoEntity photo) {
        if (this.mFeedFooterInfo != null) {
            FeedFooterView feedFooterView = feedFooterViewHolder.getFeedFooterView(streamItemViewController);
            feedFooterView.setTag(2131624320, photo);
            feedFooterView.setTag(2131624349, holder);
            feedFooterView.setInfo(this.mFeedFooterInfo);
            feedFooterViewHolder.setTagFor(feedFooterView);
            feedFooterView.setVisibility(4);
            return;
        }
        feedFooterViewHolder.setTagFor(null);
        feedFooterViewHolder.hideFeedFooterView();
    }
}
