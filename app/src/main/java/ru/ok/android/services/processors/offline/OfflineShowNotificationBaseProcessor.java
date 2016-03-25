package ru.ok.android.services.processors.offline;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import ru.ok.android.db.base.OfflineTable.Status;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.transport.JsonSessionTransportProvider;

public abstract class OfflineShowNotificationBaseProcessor extends CommandProcessor {
    protected abstract Cursor queryItem(Context context, Intent intent);

    protected abstract void sendSendingFailedBroadcast(Context context, Intent intent, Cursor cursor);

    public OfflineShowNotificationBaseProcessor(JsonSessionTransportProvider transportProvider) {
        super(transportProvider);
    }

    protected final int doCommand(Context context, Intent data, Bundle outBundle) throws Exception {
        Cursor cursor = queryItem(context, data);
        try {
            if (cursor.moveToFirst()) {
                if (!Status.CANT_BECOME_OVERDUE.contains(Status.valueOf(cursor.getString(cursor.getColumnIndex(NotificationCompat.CATEGORY_STATUS))))) {
                    updateStatusToOverdue(context, data);
                    sendSendingFailedBroadcast(context, data, cursor);
                }
            }
            cursor.close();
            return 1;
        } catch (Throwable th) {
            cursor.close();
        }
    }

    private void updateStatusToOverdue(Context context, Intent intent) {
        ContentValues cv = new ContentValues();
        cv.put(NotificationCompat.CATEGORY_STATUS, Status.OVERDUE.name());
        cv.put("failure_reason", ErrorType.NO_INTERNET_TOO_LONG.name());
    }
}
