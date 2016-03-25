package ru.ok.android.services.app.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;
import ru.ok.android.ui.activity.main.OdklActivity;
import ru.ok.android.utils.remote.RemoteViewsUtils;
import ru.ok.model.wmf.Track;

public final class MusicNotificationHelper {
    public static Notification createNotification(Context context, Track track, boolean isPlaying, Bitmap coverBitmap) {
        Builder builder = new Builder(context);
        builder.setSmallIcon(2130838509).setTicker(track.name).setContentTitle(track.name).setContentText(track.artist == null ? "" : track.artist.name).setWhen(System.currentTimeMillis()).setContentIntent(buildClickIntent(context)).setOnlyAlertOnce(true).setContent(createNotificationView(context, track, isPlaying, 2130903339, coverBitmap, true, false));
        Notification notification = builder.build();
        notification.flags |= 32;
        createBigView(context, notification, track, isPlaying, coverBitmap);
        return notification;
    }

    @SuppressLint({"NewApi"})
    private static void createBigView(Context context, Notification notification, Track track, boolean isPlaying, Bitmap coverBitmap) {
        if (VERSION.SDK_INT >= 16) {
            notification.bigContentView = createNotificationView(context, track, isPlaying, 2130903338, coverBitmap, false, true);
        }
    }

    private static RemoteViews createNotificationView(Context context, Track track, boolean isPlaying, int layoutId, Bitmap coverBitmap, boolean changeVisibilityOnPlay, boolean isLarge) {
        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
        RemoteViewsUtils.setMusicActions(context, views, isPlaying, false, isLarge);
        RemoteViewsUtils.updateMusicData(views, track, isPlaying, coverBitmap, false, false);
        if (changeVisibilityOnPlay) {
            if (isPlaying) {
                views.setViewVisibility(2131625121, 8);
                views.setViewVisibility(2131625124, 0);
                views.setViewVisibility(2131625125, 0);
            } else {
                views.setViewVisibility(2131625121, 0);
                views.setViewVisibility(2131625124, 8);
                views.setViewVisibility(2131625125, 8);
            }
        }
        return views;
    }

    private static PendingIntent buildClickIntent(Context context) {
        Intent notificationIntent = new Intent(context.getApplicationContext(), OdklActivity.class);
        notificationIntent.putExtra("FROM_PLAYER", true);
        notificationIntent.putExtra("FORCE_PROCESS_INTENT", true);
        notificationIntent.setFlags(67239936);
        return PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, 0);
    }
}
