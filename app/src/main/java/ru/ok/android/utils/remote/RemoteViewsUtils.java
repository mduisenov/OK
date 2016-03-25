package ru.ok.android.utils.remote;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.RemoteViews;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.Tag;
import ru.ok.model.wmf.Track;

public final class RemoteViewsUtils {
    public static void setMusicActions(Context context, RemoteViews views, boolean isPlaying, boolean isWidget, boolean isLarge) {
        Context appContext = context.getApplicationContext();
        views.setOnClickPendingIntent(2131625124, createPreviousIntent(appContext, prevLogEvent(isWidget, isLarge)));
        views.setOnClickPendingIntent(2131625125, createNextIntent(appContext, nextLogEvent(isWidget, isLarge)));
        views.setOnClickPendingIntent(C0158R.id.play_pause, isWidget ? createStopIntent(context, playPauseLogName(isPlaying, isWidget, isLarge)) : createPauseIntent(appContext, playPauseLogName(isPlaying, isWidget, isLarge)));
        views.setOnClickPendingIntent(2131625121, createHideNotificationIntent(appContext, closeLogEvent(isLarge)));
        views.setOnClickPendingIntent(2131625120, createOpenMusicIntent(appContext, goToPlayerLogEvent(isWidget, isLarge)));
        views.setOnClickPendingIntent(2131625436, createOpenMusicIntent(appContext, goToPlayerLogEvent(isWidget, isLarge)));
    }

    private static String playPauseLogName(boolean isPlaying, boolean isWidget, boolean isLarge) {
        return isPlaying ? pauseLogEvent(isWidget, isLarge) : playLogEvent(isWidget, isLarge);
    }

    private static Intent addLog(Intent intent, String eventName) {
        return intent.putExtra("ru.ok.android.music.LOG_EVENT", eventName);
    }

    private static String prevLogEvent(boolean isWidget, boolean isLarge) {
        return isWidget ? isLarge ? "music-widget-large-prev" : "music-widget-small-prev" : isLarge ? "music-notification-large-prev" : "music-notification-small-prev";
    }

    private static String nextLogEvent(boolean isWidget, boolean isLarge) {
        return isWidget ? isLarge ? "music-widget-large-next" : "music-widget-small-next" : isLarge ? "music-notification-large-next" : "music-notification-small-next";
    }

    private static String closeLogEvent(boolean isLarge) {
        return isLarge ? "music-notification-large-close" : "music-notification-small-close";
    }

    private static String goToPlayerLogEvent(boolean isWidget, boolean isLarge) {
        return isWidget ? isLarge ? "music-widget-large-go-to-player" : "music-widget-small-go-to-player" : isLarge ? "music-notification-large-go-to-player" : "music-notification-small-go-to-player";
    }

    private static String playLogEvent(boolean isWidget, boolean isLarge) {
        return isWidget ? isLarge ? "music-widget-large-play" : "music-widget-small-play" : isLarge ? "music-notification-large-play" : "music-notification-small-play";
    }

    private static String pauseLogEvent(boolean isWidget, boolean isLarge) {
        return isWidget ? isLarge ? "music-widget-large-pause" : "music-widget-small-pause" : isLarge ? "music-notification-large-pause" : "music-notification-small-pause";
    }

    private static PendingIntent createPauseIntent(Context appContext, String logName) {
        return PendingIntent.getService(appContext, logName.hashCode(), addLog(MusicService.getTogglePlayIntent(appContext, false), logName), 134217728);
    }

    private static PendingIntent createStopIntent(Context appContext, String logName) {
        Intent intentToggle = new Intent("ru.ok.android.music.TOGGLE_PLAY", null, appContext, MusicService.class);
        intentToggle.putExtra("ru.ok.android.STOP_FOREGRAUND", true);
        return PendingIntent.getService(appContext, logName.hashCode(), addLog(intentToggle, logName), 134217728);
    }

    private static PendingIntent createHideNotificationIntent(Context appContext, String logName) {
        return PendingIntent.getService(appContext, logName.hashCode(), addLog(MusicService.getHideNotificationIntent(appContext), logName), 134217728);
    }

    private static PendingIntent createNextIntent(Context appContext, String logName) {
        return PendingIntent.getService(appContext, logName.hashCode(), addLog(new Intent("ru.ok.android.music.PLAY_NEXT", null, appContext, MusicService.class), logName), 134217728);
    }

    private static PendingIntent createPreviousIntent(Context appContext, String logName) {
        return PendingIntent.getService(appContext, logName.hashCode(), addLog(new Intent("ru.ok.android.music.PLAY_PREV", null, appContext, MusicService.class), logName), 134217728);
    }

    private static PendingIntent createOpenMusicIntent(Context appContext, String logName) {
        Intent intent = NavigationHelper.createIntentForTag(appContext, Tag.music);
        AppLaunchLog.fillWidgetMusic(intent);
        return PendingIntent.getActivity(appContext, logName.hashCode(), addLog(intent, logName), 134217728);
    }

    public static void updateMusicData(RemoteViews views, String songName, String artistName, boolean isPlaying, Bitmap coverBitmap, boolean smallButton, boolean glueNameWithArtist) {
        int i = 8;
        int start = 0;
        views.setTextViewText(2131625122, songName);
        if (glueNameWithArtist) {
            Spannable spannable = new SpannableString(TextUtils.join(" - ", new String[]{songName, artistName}));
            if (!TextUtils.isEmpty(songName)) {
                start = songName.length();
            }
            spannable.setSpan(new ForegroundColorSpan(-7829368), start, TextUtils.isEmpty(artistName) ? start : spannable.length(), 33);
            views.setTextViewText(2131625122, spannable);
        } else {
            int i2;
            if (TextUtils.isEmpty(songName)) {
                i2 = 8;
            } else {
                i2 = 0;
            }
            views.setViewVisibility(2131625122, i2);
            if (!TextUtils.isEmpty(artistName)) {
                i = 0;
            }
            views.setViewVisibility(2131625123, i);
            views.setTextViewText(2131625123, artistName);
        }
        if (coverBitmap != null) {
            views.setImageViewBitmap(2131625120, coverBitmap);
        } else {
            views.setInt(2131625120, "setImageResource", 2130838690);
        }
        if (smallButton) {
            views.setInt(C0158R.id.play_pause, "setImageResource", isPlaying ? 2130838751 : 2130838752);
        } else {
            views.setInt(C0158R.id.play_pause, "setImageResource", isPlaying ? 2130838550 : 2130838551);
        }
    }

    public static void updateMusicData(RemoteViews views, Track track, boolean isPlaying, Bitmap coverBitmap, boolean smallButtons, boolean uniteNames) {
        String str = null;
        String str2 = track == null ? null : track.name;
        if (!(track == null || track.artist == null)) {
            str = track.artist.name;
        }
        updateMusicData(views, str2, str, isPlaying, coverBitmap, smallButtons, uniteNames);
    }
}
