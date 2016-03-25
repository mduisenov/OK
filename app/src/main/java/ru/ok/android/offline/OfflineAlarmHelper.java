package ru.ok.android.offline;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import ru.ok.android.services.app.OdnoklassnikiService;
import ru.ok.android.services.processors.offline.discussions.DiscussionCommentsSendAllProcessor;
import ru.ok.android.services.processors.offline.discussions.DiscussionShowNotificationProcessor;
import ru.ok.android.utils.Logger;
import ru.ok.model.Discussion;

public final class OfflineAlarmHelper {
    public static void scheduleDiscussionUndeliveredNotification(Context context, Discussion discussion, int commentId) {
        Logger.m173d("OfflineAlarmHelper", "scheduleDiscussionUndeliveredNotification: " + discussion + ", " + commentId);
        ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).set(2, SystemClock.elapsedRealtime() + 180000, createDiscussionNotificationPendingIntent(context, discussion.id, discussion.type, commentId));
    }

    public static void unScheduleDiscussionUndeliveredNotification(Context context, String discussionId, String discussionType, int commentId) {
        Logger.m173d("OfflineAlarmHelper", "unScheduleDiscussionUndeliveredNotification: " + discussionId + ", " + discussionType + ", " + commentId);
        ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).cancel(createDiscussionNotificationPendingIntent(context, discussionId, discussionType, commentId));
    }

    private static PendingIntent createDiscussionNotificationPendingIntent(Context context, String discussionId, String discussionType, int commentId) {
        Intent intent = new Intent(context, OdnoklassnikiService.class);
        DiscussionShowNotificationProcessor.fillIntent(intent, discussionId, discussionType, commentId);
        return PendingIntent.getService(context, commentId, intent, 134217728);
    }

    public static void scheduleNextAttempt(Context context) {
        Logger.m173d("OfflineAlarmHelper", "scheduleNextAttempt");
        unScheduleNextAttempt(context);
        ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).set(2, SystemClock.elapsedRealtime() + 30000, createDiscussionsPendingIntent(context));
    }

    public static void unScheduleNextAttempt(Context context) {
        Logger.m173d("OfflineAlarmHelper", "unScheduleNextAttempt");
        ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).cancel(createDiscussionsPendingIntent(context));
    }

    private static PendingIntent createDiscussionsPendingIntent(Context context) {
        Intent intent = new Intent(context, OdnoklassnikiService.class);
        intent.putExtra("COMMAND_NAME", DiscussionCommentsSendAllProcessor.commandName());
        return PendingIntent.getService(context, 0, intent, 134217728);
    }
}
