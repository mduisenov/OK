package ru.ok.android.ui.fragments.messages.view.state;

import android.content.Context;
import android.view.View;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;

public final class DiscussionEmptyState extends DiscussionState {
    public void configureView(View contentView, DiscussionInfoResponse discussion) {
    }

    public View createContentView(Context context) {
        return new View(context);
    }

    public void onContentClicked() {
    }

    public boolean isDateVisible() {
        return false;
    }

    public boolean isMessageVisible() {
        return false;
    }
}
