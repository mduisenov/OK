package ru.ok.android.ui.fragments.users.loader;

import android.content.Context;
import ru.ok.android.services.processors.messaging.MessageLikesProcessor;

public final class MessageLikesLoader extends LikesBaseLoader {
    private final String conversationId;
    private final String messageId;

    public MessageLikesLoader(Context context, String conversationId, String messageId) {
        super(context);
        this.conversationId = conversationId;
        this.messageId = messageId;
    }

    protected void callService(String anchor) {
        getServiceHelper().loadMessageLikes(this.conversationId, this.messageId, anchor);
    }

    protected boolean isRightCommand(String commandName) {
        return MessageLikesProcessor.isIt(commandName, this.messageId);
    }
}
