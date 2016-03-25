package ru.ok.android.ui.fragments.users;

import android.os.Bundle;
import ru.ok.android.ui.fragments.users.loader.DiscussionLikesLoader;
import ru.ok.android.ui.fragments.users.loader.LikesBaseLoader;
import ru.ok.model.Discussion;

public final class UsersLikedDiscussionFragment extends UsersLikedBaseFragment {
    public static Bundle newArguments(Discussion discussion) {
        return newArguments(discussion, false);
    }

    public static Bundle newArguments(Discussion discussion, boolean selfLike) {
        Bundle args = UsersLikedBaseFragment.newArguments(selfLike);
        args.putParcelable("DISCUSSION", discussion);
        return args;
    }

    private Discussion getDiscussion() {
        return (Discussion) getArguments().getParcelable("DISCUSSION");
    }

    protected LikesBaseLoader createLoader() {
        return new DiscussionLikesLoader(getActivity(), getDiscussion());
    }
}
