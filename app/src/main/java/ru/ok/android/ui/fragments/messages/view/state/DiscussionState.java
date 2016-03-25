package ru.ok.android.ui.fragments.messages.view.state;

import android.content.Context;
import android.view.View;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;

public abstract class DiscussionState {
    public abstract void configureView(View view, DiscussionInfoResponse discussionInfoResponse);

    public abstract View createContentView(Context context);

    public abstract void onContentClicked();

    protected DiscussionState() {
    }

    public boolean isDateVisible() {
        return true;
    }

    public boolean isMessageVisible() {
        return true;
    }

    public void onShow() {
    }

    public void onHide() {
    }
}
