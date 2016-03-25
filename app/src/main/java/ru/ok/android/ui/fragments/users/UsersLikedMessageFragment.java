package ru.ok.android.ui.fragments.users;

import android.os.Bundle;
import ru.ok.android.ui.fragments.users.loader.LikesBaseLoader;
import ru.ok.android.ui.fragments.users.loader.MessageLikesLoader;

public final class UsersLikedMessageFragment extends UsersLikedBaseFragment {
    public static UsersLikedMessageFragment newInstance(String conversationId, String messageId) {
        Bundle args = new Bundle();
        args.putString("CONVERSATION_ID", conversationId);
        args.putString("MESSAGE_ID", messageId);
        UsersLikedMessageFragment result = new UsersLikedMessageFragment();
        result.setArguments(args);
        return result;
    }

    protected LikesBaseLoader createLoader() {
        return new MessageLikesLoader(getActivity(), getConversationId(), getMessageId());
    }

    private String getConversationId() {
        return getArguments().getString("CONVERSATION_ID");
    }

    private String getMessageId() {
        return getArguments().getString("MESSAGE_ID");
    }
}
