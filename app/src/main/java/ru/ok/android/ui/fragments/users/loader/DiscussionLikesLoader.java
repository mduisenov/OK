package ru.ok.android.ui.fragments.users.loader;

import android.content.Context;
import ru.ok.android.services.processors.discussions.DiscussionLikesProcessor;
import ru.ok.model.Discussion;

public final class DiscussionLikesLoader extends LikesBaseLoader {
    private final Discussion _discussion;

    public DiscussionLikesLoader(Context context, Discussion discussion) {
        super(context);
        this._discussion = discussion;
    }

    protected void callService(String anchor) {
        getServiceHelper().loadDiscussionLikes(this._discussion, anchor);
    }

    protected boolean isRightCommand(String commandName) {
        return DiscussionLikesProcessor.isIt(commandName, this._discussion);
    }
}
