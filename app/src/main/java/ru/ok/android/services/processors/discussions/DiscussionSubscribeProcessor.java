package ru.ok.android.services.processors.discussions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.json.JsonBooleanParser;
import ru.ok.java.api.request.discussions.DiscussionSubscribeRequest;
import ru.ok.model.Discussion;

public final class DiscussionSubscribeProcessor extends CommandProcessor {
    private static final String BASE_COMMAND_NAME;

    static {
        BASE_COMMAND_NAME = DiscussionSubscribeProcessor.class.getName();
    }

    public DiscussionSubscribeProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static String commandName(Discussion discussion) {
        return BASE_COMMAND_NAME + "/" + discussion.id;
    }

    public static void fillIntent(Intent intent, Discussion discussion) {
        intent.putExtra("DISCUSSION_ID", discussion.id);
        intent.putExtra("DISCUSSION_TYPE", discussion.type);
    }

    public static boolean isIt(String commandName, Discussion discussion) {
        return commandName.equals(commandName(discussion));
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        return ((Boolean) new JsonBooleanParser(this._transportProvider.execJsonHttpMethod(new DiscussionSubscribeRequest(data.getStringExtra("DISCUSSION_ID"), data.getStringExtra("DISCUSSION_TYPE")))).parse()).booleanValue() ? 1 : 2;
    }
}
