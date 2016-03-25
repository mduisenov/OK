package ru.ok.android.ui.fragments.users;

import android.os.Bundle;
import ru.ok.android.ui.fragments.users.loader.CommentLikesLoader;
import ru.ok.android.ui.fragments.users.loader.LikesBaseLoader;
import ru.ok.model.Discussion;

public final class UsersLikedDiscussionCommentFragment extends UsersLikedBaseFragment {
    public static Bundle newArguments(Discussion discussion, String commentId) {
        Bundle args = UsersLikedBaseFragment.newArguments();
        args.putParcelable("DISCUSSION", discussion);
        args.putString("COMMENT_ID", commentId);
        return args;
    }

    private Discussion getDiscussion() {
        return (Discussion) getArguments().getParcelable("DISCUSSION");
    }

    private String getCommentId() {
        return getArguments().getString("COMMENT_ID");
    }

    protected LikesBaseLoader createLoader() {
        return new CommentLikesLoader(getActivity(), getDiscussion(), getCommentId());
    }
}
