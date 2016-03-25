package ru.ok.android.widget.music;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.widget.RemoteViews;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.ui.activity.main.OdklActivity;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.Tag;
import ru.ok.android.utils.remote.RemoteViewsUtils;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.wmf.Track;

public abstract class MusicBaseWidget extends AppWidgetProvider {

    private static class WidgetTrack implements Parcelable {
        public static final Creator<WidgetTrack> CREATOR;
        private final String _artistName;
        private final Bitmap _bitmap;
        private final boolean _isPlaying;
        private final String _songName;

        /* renamed from: ru.ok.android.widget.music.MusicBaseWidget.WidgetTrack.1 */
        static class C15051 implements Creator<WidgetTrack> {
            C15051() {
            }

            public WidgetTrack createFromParcel(Parcel in) {
                return new WidgetTrack(null);
            }

            public WidgetTrack[] newArray(int size) {
                return new WidgetTrack[size];
            }
        }

        static {
            CREATOR = new C15051();
        }

        private WidgetTrack(String songName, String artistName, Bitmap bitmap, boolean isPlaying) {
            this._songName = songName;
            this._artistName = artistName;
            this._bitmap = bitmap;
            this._isPlaying = isPlaying;
        }

        private WidgetTrack(Parcel in) {
            this(in.readString(), in.readString(), (Bitmap) in.readParcelable(WidgetTrack.class.getClassLoader()), ((Boolean) in.readValue(WidgetTrack.class.getClassLoader())).booleanValue());
        }

        public static WidgetTrack byServiceParams(Track track, Bitmap bitmap, boolean playing) {
            String str = null;
            String str2 = track == null ? null : track.name;
            if (!(track == null || track.artist == null)) {
                str = track.artist.name;
            }
            return new WidgetTrack(str2, str, bitmap, playing);
        }

        public String getSongName() {
            return this._songName;
        }

        public String getArtistName() {
            return this._artistName;
        }

        public Bitmap getBitmap() {
            return this._bitmap;
        }

        public boolean isPlaying() {
            return this._isPlaying;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeString(this._songName);
            out.writeString(this._artistName);
            out.writeParcelable(this._bitmap, flags);
            out.writeValue(Boolean.valueOf(this._isPlaying));
        }
    }

    protected abstract int getLayoutId(Context context, AppWidgetManager appWidgetManager, int i);

    public void onReceive(Context context, Intent intent) {
        if ("android.appwidget.action.APPWIDGET_UPDATE".equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int[] appWidgetIds = extras.getIntArray("appWidgetIds");
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds, intent);
                    return;
                }
                return;
            }
            return;
        }
        super.onReceive(context, intent);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, Intent intent) {
        boolean isFromMusicService = extractIsFromMusicService(intent);
        for (int id : appWidgetIds) {
            appWidgetManager.updateAppWidget(id, buildWidgetView(context, appWidgetManager, id, intent));
            if (!isFromMusicService) {
                requestAppWidgetUpdate(context, id);
            }
        }
    }

    private RemoteViews buildWidgetView(Context context, AppWidgetManager manager, int appWidgetId, Intent intent) {
        if (!isUserLoggedIn(context)) {
            return createLoginViews(context);
        }
        if (isPlayerAvailable(intent)) {
            return createPlayerRemoteViews(context, manager, appWidgetId, intent);
        }
        return createStartPlayerViews(context);
    }

    private static boolean isUserLoggedIn(Context context) {
        return !TextUtils.isEmpty(Settings.getToken(context));
    }

    private static boolean isPlayerAvailable(Intent intent) {
        WidgetTrack track = extractTrack(intent);
        if (track == null) {
            return false;
        }
        if (TextUtils.isEmpty(track.getSongName()) && TextUtils.isEmpty(track.getArtistName())) {
            return false;
        }
        return true;
    }

    private RemoteViews createPlayerRemoteViews(Context context, AppWidgetManager manager, int appWidgetId, Intent intent) {
        boolean isLarge;
        boolean z;
        boolean z2 = true;
        int layoutId = getLayoutId(context, manager, appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
        WidgetTrack track = extractTrack(intent);
        if (layoutId == 2130903575) {
            isLarge = true;
        } else {
            isLarge = false;
        }
        RemoteViewsUtils.setMusicActions(context, views, track.isPlaying(), true, isLarge);
        String songName = track.getSongName();
        String artistName = track.getArtistName();
        boolean isPlaying = track.isPlaying();
        Bitmap bitmap = track.getBitmap();
        if (isLarge) {
            z = false;
        } else {
            z = true;
        }
        if (isLarge) {
            z2 = false;
        }
        RemoteViewsUtils.updateMusicData(views, songName, artistName, isPlaying, bitmap, z, z2);
        return views;
    }

    private RemoteViews createLoginViews(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), 2130903577);
        views.setOnClickPendingIntent(2131625015, createLoginIntent(context));
        return views;
    }

    private RemoteViews createStartPlayerViews(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), 2130903579);
        views.setOnClickPendingIntent(2131625437, createOpenMusicIntent(context));
        return views;
    }

    private static PendingIntent createLoginIntent(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), OdklActivity.class);
        AppLaunchLog.fillWidgetLogin(intent);
        return PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 134217728);
    }

    private static PendingIntent createOpenMusicIntent(Context context) {
        Intent intent = NavigationHelper.createIntentForTag(context, Tag.music);
        AppLaunchLog.fillWidgetMusic(intent);
        return PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 134217728);
    }

    private static boolean extractIsFromMusicService(Intent intent) {
        return intent.hasExtra("ru.ok.android.widget.music.EXTRA_TRACK");
    }

    private static WidgetTrack extractTrack(Intent intent) {
        return (WidgetTrack) intent.getParcelableExtra("ru.ok.android.widget.music.EXTRA_TRACK");
    }

    public static void requestAllWidgetsUpdate(Context context) {
        sendForClasses(context, null);
    }

    public static void updateAllWidgetsByMusicInfo(Context context, Bitmap bitmap, Track track, boolean isPlaying) {
        sendForClasses(context, WidgetTrack.byServiceParams(track, bitmap, isPlaying));
    }

    private static void sendForClasses(Context context, WidgetTrack widgetTrack) {
        sendForClass(context, MusicOneRowWidget.class, widgetTrack, 0);
        sendForClass(context, MusicTwoRowsWidget.class, widgetTrack, 0);
        sendForClass(context, MusicResizableWidget.class, widgetTrack, 0);
    }

    public static void updateWidget(Context context, Class<?> widgetClass, int appWidgetId, Bitmap bitmap, Track track, boolean isPlaying) {
        sendForClass(context, widgetClass, WidgetTrack.byServiceParams(track, bitmap, isPlaying), appWidgetId);
    }

    private static void sendForClass(Context context, Class<?> clazz, WidgetTrack widgetTrack, int appWidgetId) {
        context = context.getApplicationContext();
        Intent updateIntent = new Intent(context, clazz);
        updateIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int[] appWidgetIds = appWidgetId != 0 ? new int[]{appWidgetId} : AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, clazz));
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            updateIntent.putExtra("appWidgetIds", appWidgetIds);
            if (widgetTrack != null) {
                updateIntent.putExtra("ru.ok.android.widget.music.EXTRA_TRACK", widgetTrack);
            }
            context.sendBroadcast(updateIntent);
        }
    }

    protected void requestAppWidgetUpdate(Context context, int appWidgetId) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.UPDATE_WIDGET");
        intent.putExtra("ru.ok.android.widget.music.EXTRA_WIDGET_ID", appWidgetId);
        intent.putExtra("ru.ok.android.widget.music.EXTRA_WIDGET_CLASS", getClass());
        context.startService(intent);
    }
}
