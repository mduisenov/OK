package ru.ok.android.ui.fragments.users.loader;

import android.content.Context;
import ru.ok.android.services.processors.discussions.DiscussionCommentLikesProcessor;
import ru.ok.model.Discussion;

public final class CommentLikesLoader extends LikesBaseLoader {
    private final String _commentId;
    private final Discussion _discussion;

    public CommentLikesLoader(Context context, Discussion discussion, String commentId) {
        super(context);
        this._discussion = discussion;
        this._commentId = commentId;
    }

    protected void callService(String anchor) {
        getServiceHelper().loadCommentLikes(this._discussion, this._commentId, anchor);
    }

    protected boolean isRightCommand(String commandName) {
        return DiscussionCommentLikesProcessor.isIt(commandName, this._commentId);
    }
}
