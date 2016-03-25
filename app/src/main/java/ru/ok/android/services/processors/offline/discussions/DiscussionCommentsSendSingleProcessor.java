package ru.ok.android.services.processors.offline.discussions;

import android.content.Intent;
import android.net.Uri;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.services.transport.JsonSessionTransportProvider;

public final class DiscussionCommentsSendSingleProcessor extends DiscussionCommentsSendBaseProcessor {
    private static final String BASE_COMMAND_NAME;

    static {
        BASE_COMMAND_NAME = DiscussionCommentsSendSingleProcessor.class.getName();
    }

    public DiscussionCommentsSendSingleProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static String commandName(int commentId) {
        return BASE_COMMAND_NAME + "/" + commentId;
    }

    public static void fillIntent(Intent intent, int commentId) {
        intent.putExtra("COMMENT_ID", commentId);
    }

    protected Uri contentUri(Intent data) {
        return OdklProvider.commentUri((long) data.getIntExtra("COMMENT_ID", 0));
    }

    protected boolean isMultipleSendingAllowed() {
        return false;
    }
}
