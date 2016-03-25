package ru.ok.android.services.messages;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import java.util.List;
import ru.ok.android.model.cache.ram.MessageModel;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.offline.NotificationUtils;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.proto.MessagesProto.Message.EditInfo;
import ru.ok.android.utils.Logger;

final class OverdueHelper {
    static void processOverdueMessages(MessagesService service) {
        List<MessageModel> overdueMessages = MessagesCache.getInstance().getOverdueMessages();
        if (overdueMessages != null) {
            for (MessageModel message : overdueMessages) {
                Message dbMessage = message.message;
                EditInfo editInfo = dbMessage.getEditInfo();
                NotificationUtils.sendMessageFailedBroadcast(service, message.conversationId, editInfo != null ? editInfo.getNewText() : dbMessage.getText());
                MessagesService.addErrorStatisticsMessage(editInfo == null ? "message-send-failed" : "message-edit-failed", "overdue");
            }
        }
    }

    static boolean scheduleOverdueProcessingIfNeeded(Context context) {
        Logger.m173d("%d messages are potential for overdue", Integer.valueOf(MessagesCache.getInstance().getPotentialOverdueCount()));
        if (MessagesCache.getInstance().getPotentialOverdueCount() <= 0) {
            return false;
        }
        scheduleOverdueProcessing(context);
        return true;
    }

    static void unScheduleOverdueProcessing(Context context) {
        Logger.m172d("");
        ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).cancel(createOverduePendingIntent(context));
    }

    static void scheduleOverdueProcessing(Context context) {
        Logger.m172d("");
        ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).set(2, SystemClock.elapsedRealtime() + 180000, createOverduePendingIntent(context));
    }

    private static PendingIntent createOverduePendingIntent(Context context) {
        Intent intent = new Intent(context, MessagesService.class);
        intent.setAction("overdue");
        return PendingIntent.getService(context, 0, intent, 134217728);
    }
}
