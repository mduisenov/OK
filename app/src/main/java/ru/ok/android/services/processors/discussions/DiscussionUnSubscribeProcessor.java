package ru.ok.android.services.processors.discussions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.json.JsonBooleanParser;
import ru.ok.java.api.request.discussions.DiscussionUnSubscribeRequest;
import ru.ok.model.Discussion;

public final class DiscussionUnSubscribeProcessor extends CommandProcessor {
    private static final String BASE_COMMAND_NAME;

    static {
        BASE_COMMAND_NAME = DiscussionUnSubscribeProcessor.class.getName();
    }

    public DiscussionUnSubscribeProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static String commandName(Discussion discussion) {
        return BASE_COMMAND_NAME + "/" + discussion.id;
    }

    public static boolean isIt(String commandName, Discussion discussion) {
        return commandName.equals(commandName(discussion));
    }

    public static void fillIntent(Intent intent, Discussion discussion) {
        intent.putExtra("DISCUSSION_ID", discussion.id);
        intent.putExtra("DISCUSSION_TYPE", discussion.type);
    }

    protected int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        String discussionId = data.getStringExtra("DISCUSSION_ID");
        boolean success = ((Boolean) new JsonBooleanParser(this._transportProvider.execJsonHttpMethod(new DiscussionUnSubscribeRequest(discussionId, data.getStringExtra("DISCUSSION_TYPE")))).parse()).booleanValue();
        if (success) {
            outBundle.putString("DISCUSSION_ID", discussionId);
        }
        return success ? 1 : 2;
    }
}
