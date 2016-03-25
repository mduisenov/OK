package ru.ok.android.services.processors.discussions;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.helper.ServiceHelper;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.DiscussionsStorageFacade;
import ru.ok.android.offline.OfflineAlarmHelper;
import ru.ok.android.services.processors.offline.OfflineBaseAddProcessor;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.model.Discussion;
import ru.ok.model.messages.Attachment;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase.RepliedTo;
import ru.ok.model.messages.MessageComment;

public final class DiscussionAddProcessor extends OfflineBaseAddProcessor<MessageComment> {
    @Subscribe(on = 2131623944, to = 2131623963)
    public void addComment(BusEvent event) {
        doLogic(2131624142, event);
    }

    protected OfflineMessage<MessageComment> cursor2Message(Cursor cursor) {
        return DiscussionsStorageFacade.cursor2Comment(cursor);
    }

    protected Uri insertDataIntoDB(Bundle data, String message, long date, MessageAuthor author, RepliedTo replyTo) {
        Discussion discussion = extractDiscussion(data);
        return DiscussionsStorageFacade.insertComment(discussion.id, discussion.type, author, date, message, replyTo, extractServerId(data), true);
    }

    protected Uri insertDataIntoDB(Bundle data, Attachment[] attachments, long date, MessageAuthor author, RepliedTo replyTo, Integer taskId) {
        return null;
    }

    protected void scheduleFailureAlarm(Uri uri, Bundle data) {
        OfflineAlarmHelper.scheduleDiscussionUndeliveredNotification(OdnoklassnikiApplication.getContext(), extractDiscussion(data), Integer.parseInt(uri.getLastPathSegment()));
    }

    protected void startSendCommand(Uri uri) {
        ServiceHelper.from().sendUndeliveredDiscussionComments();
    }

    private static Discussion extractDiscussion(Bundle data) {
        return (Discussion) data.getParcelable("DISCUSSION");
    }

    private static String extractServerId(Bundle data) {
        return data.getString("SERVER_ID");
    }
}
