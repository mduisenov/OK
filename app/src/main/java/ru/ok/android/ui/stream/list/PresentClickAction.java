package ru.ok.android.ui.stream.list;

import android.view.View;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public class PresentClickAction implements ClickAction {
    private final FeedWithState feedWithState;
    private final PresentInfo presentInfo;

    public PresentClickAction(FeedWithState feedWithState, PresentInfo presentInfo) {
        this.feedWithState = feedWithState;
        this.presentInfo = presentInfo;
    }

    public void setClickListener(View view, StreamItemViewController streamItemViewController) {
        view.setOnClickListener(streamItemViewController.getPresentClickListener());
    }

    public void setTags(View view) {
        view.setTag(2131624336, this.presentInfo);
        view.setTag(2131624322, this.feedWithState);
    }
}
