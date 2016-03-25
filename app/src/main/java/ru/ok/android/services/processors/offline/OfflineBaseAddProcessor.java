package ru.ok.android.services.processors.offline;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.AnyRes;
import java.util.ArrayList;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.utils.Logger;
import ru.ok.model.messages.Attachment;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase;
import ru.ok.model.messages.MessageBase.RepliedTo;

public abstract class OfflineBaseAddProcessor<M extends MessageBase> {
    protected abstract OfflineMessage<M> cursor2Message(Cursor cursor);

    protected abstract Uri insertDataIntoDB(Bundle bundle, String str, long j, MessageAuthor messageAuthor, RepliedTo repliedTo);

    protected abstract Uri insertDataIntoDB(Bundle bundle, Attachment[] attachmentArr, long j, MessageAuthor messageAuthor, RepliedTo repliedTo, Integer num);

    protected abstract void scheduleFailureAlarm(Uri uri, Bundle bundle);

    protected abstract void startSendCommand(Uri uri);

    protected final void doLogic(@AnyRes int kind, BusEvent event) {
        Uri uri;
        Bundle data = event.bundleInput;
        String message = data.getString("TEXT");
        if (message != null) {
            message = message.trim();
        }
        Attachment[] attachments = (Attachment[]) data.getParcelableArray("ATTACHMENTS");
        if (Logger.isLoggingEnable()) {
            Logger.m173d("Adding message with text \"%s\" and %d attachments", message, Integer.valueOf(attachments == null ? 0 : attachments.length));
            for (int i = 0; i < attachCount; i++) {
                Logger.m173d("attachments[%d]=%s", Integer.valueOf(i), attachments[i]);
            }
        }
        MessageAuthor author = (MessageAuthor) data.getParcelable("AUTHOR");
        RepliedTo replyTo = (RepliedTo) data.getParcelable("REPLY_TO");
        Integer taskId = null;
        if (data.containsKey("TASK_ID")) {
            taskId = Integer.valueOf(data.getInt("TASK_ID"));
        }
        if (attachments == null || attachments.length <= 0) {
            uri = insertDataIntoDB(data, message, System.currentTimeMillis(), author, replyTo);
        } else {
            uri = insertDataIntoDB(data, attachments, System.currentTimeMillis(), author, replyTo, taskId);
        }
        Cursor cursor = OdnoklassnikiApplication.getContext().getContentResolver().query(uri, null, null, null, null);
        Bundle output = new Bundle();
        try {
            if (cursor.moveToFirst()) {
                ArrayList<OfflineMessage<M>> messages = new ArrayList();
                messages.add(cursor2Message(cursor));
                output.putParcelableArrayList("MESSAGES", messages);
            }
            cursor.close();
            scheduleFailureAlarm(uri, data);
            startSendCommand(uri);
            GlobalBus.send(kind, new BusEvent(event.bundleInput, output, -1));
        } catch (Throwable th) {
            cursor.close();
        }
    }
}
