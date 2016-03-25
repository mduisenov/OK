package ru.ok.android.services.processors.messaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.processors.discussions.data.UsersLikesParcelable;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.json.discussions.JsonUsersLikesParser;
import ru.ok.java.api.request.messaging.MessageLikesRequest;
import ru.ok.java.api.request.paging.PagingDirection;

public final class MessageLikesProcessor extends CommandProcessor {
    private static final String BASE_COMMAND_NAME;

    static {
        BASE_COMMAND_NAME = MessageLikesProcessor.class.getName();
    }

    public static String commandName(String messageId) {
        return BASE_COMMAND_NAME + "/" + messageId;
    }

    public MessageLikesProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static void fillIntent(Intent intent, String conversationId, String messageId, String anchor) {
        intent.putExtra("CONVERSATION_ID", conversationId);
        intent.putExtra("MESSAGE_ID", messageId);
        intent.putExtra("ANCHOR", anchor);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        outBundle.putParcelable("USERS", UsersLikesParcelable.fromResponse(new JsonUsersLikesParser(this._transportProvider.execJsonHttpMethod(new MessageLikesRequest(data.getStringExtra("CONVERSATION_ID"), data.getStringExtra("MESSAGE_ID"), data.getStringExtra("ANCHOR"), PagingDirection.FORWARD, 50, "user.*")).getResultAsObject()).parse(), 50));
        return 1;
    }

    public static boolean isIt(String commandName, String commentId) {
        return TextUtils.equals(commandName, commandName(commentId));
    }
}
