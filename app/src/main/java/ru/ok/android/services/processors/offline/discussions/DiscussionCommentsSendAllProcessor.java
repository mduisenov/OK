package ru.ok.android.services.processors.offline.discussions;

import android.content.Intent;
import android.net.Uri;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.services.transport.JsonSessionTransportProvider;

public final class DiscussionCommentsSendAllProcessor extends DiscussionCommentsSendBaseProcessor {
    private static final String COMMAND_NAME;

    static {
        COMMAND_NAME = DiscussionCommentsSendAllProcessor.class.getName();
    }

    public DiscussionCommentsSendAllProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    public static String commandName() {
        return COMMAND_NAME;
    }

    protected Uri contentUri(Intent data) {
        return OdklProvider.commentsUri();
    }

    protected boolean isMultipleSendingAllowed() {
        return true;
    }
}
