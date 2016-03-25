package ru.ok.android.ui.stream.list;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewStub;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.FeedFooterView;

public class FeedFooterViewHelper {
    private FeedFooterView mFeedFooterView;

    private void inflateView(@NonNull View view, @NonNull StreamItemViewController streamItemViewController) {
        ((ViewStub) view.findViewById(2131625373)).inflate();
        this.mFeedFooterView = (FeedFooterView) view.findViewById(2131625189);
        this.mFeedFooterView.setOnLikeListener(streamItemViewController.getLikeClickListener());
        this.mFeedFooterView.setOnCommentsClickListener(streamItemViewController.getCommentsClickListener());
    }

    public FeedFooterView getView(@NonNull View view, @NonNull StreamItemViewController streamItemViewController) {
        if (this.mFeedFooterView == null) {
            inflateView(view, streamItemViewController);
        }
        return this.mFeedFooterView;
    }

    public void hideView() {
        if (this.mFeedFooterView != null) {
            this.mFeedFooterView.setVisibility(4);
        }
    }
}
