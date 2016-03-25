package ru.ok.android.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import java.util.Arrays;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.services.app.OdnoklassnikiService;
import ru.ok.android.ui.activity.main.OdklActivity;
import ru.ok.android.ui.image.AddImagesActivity;
import ru.ok.android.ui.settings.SettingsActivity;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;

public class OdnoklassnikiWidgetProvider extends AppWidgetProvider {
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Logger.m172d("onReceive " + intent.getAction());
        if (!"com.google.android.c2dm.intent.RECEIVE".equals(intent.getAction()) && !"com.google.android.c2dm.intent.C2D_MESSAGE".equals(intent.getAction())) {
            if ("ru.odnoklassniki.android.widget.STATE_CHANGED".equals(intent.getAction())) {
                Settings.storeIntValue(context, "incomingEvents", 0);
                update(context, "");
                askDataUpdate(context);
            } else if ("ru.odnoklassniki.android.widget.STATUS_CHANGED".equals(intent.getAction())) {
                Bundle data = intent.getExtras();
                if (data != null && data.containsKey(NotificationCompat.CATEGORY_STATUS)) {
                    Logger.m172d("onReceive update " + intent.getAction());
                    updateStatus(context, data.getString(NotificationCompat.CATEGORY_STATUS));
                }
            } else if (!"ru.oodnoklassniki.android.widget.PULL_EVENTS".equals(intent.getAction())) {
            }
        }
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        String str = "onUpdate(): appWidgetIds=%s";
        Object[] objArr = new Object[1];
        objArr[0] = Logger.isLoggingEnable() ? Arrays.toString(appWidgetIds) : "";
        Logger.m173d(str, objArr);
        update(context, null);
    }

    public void update(Context context, String status) {
        RemoteViews views = getViews(context, status);
        if (views != null) {
            Logger.m172d("onReceive update ui " + status);
            AppWidgetManager.getInstance(context).updateAppWidget(getComponentName(context), views);
        }
    }

    private RemoteViews getViews(Context context, String status) {
        RemoteViews views = new RemoteViews(context.getPackageName(), 2130903354);
        if (Settings.getIntValue(context, "incomingEvents", 0) > 0) {
            views.setViewVisibility(2131625145, 8);
            views.setViewVisibility(2131625144, 0);
            assignPendingIntent(context, views, 2131625144, OdklActivity.class);
            assignPendingIntent(context, views, (int) C0158R.id.settings, SettingsActivity.class);
            assignPendingIntent(context, views, 2131625143, AddImagesActivity.class);
            assignPendingIntent(context, views, 2131625146, new ComponentName(context, "ru.ok.android.ui.activity.MediaComposerUserActivity"));
            Logger.m172d("onReceive update get ui remote : " + status);
        } else {
            views.setViewVisibility(2131625145, 8);
            views.setViewVisibility(2131625144, 0);
            assignPendingIntent(context, views, 2131625144, OdklActivity.class);
            assignPendingIntent(context, views, (int) C0158R.id.settings, SettingsActivity.class);
            assignPendingIntent(context, views, 2131625143, AddImagesActivity.class);
            assignPendingIntent(context, views, 2131625146, new ComponentName(context, "ru.ok.android.ui.activity.MediaComposerUserActivity"));
            Logger.m172d("onReceive update get ui remote : " + status);
        }
        if (status != null) {
            views.setTextViewText(2131625146, status);
        }
        return views;
    }

    private static final ComponentName getComponentName(Context context) {
        return new ComponentName(context.getPackageName(), OdnoklassnikiWidgetProvider.class.getName());
    }

    private void askDataUpdate(Context context) {
        Logger.m172d("Ask service for status");
        Intent intent = new Intent(context, OdnoklassnikiService.class);
        intent.setAction("getStatus");
        context.startService(intent);
    }

    private void updateStatus(Context context, String status) {
        Logger.m172d("onReceive update status " + status);
        update(context, status);
    }

    private void assignPendingIntent(Context context, RemoteViews views, int viewId, Class<?> targetActivity) {
        assignPendingIntent(context, views, viewId, new ComponentName(context, targetActivity));
    }

    private void assignPendingIntent(Context context, RemoteViews views, int viewId, ComponentName targetActivity) {
        Intent intent = new Intent();
        intent.setComponent(targetActivity);
        if (!(2131625144 == viewId || 2131625145 == viewId)) {
            intent.setFlags(268435456);
        }
        int requestCode = 0;
        if (2131625144 == viewId) {
            requestCode = 1;
        } else if (2131625145 == viewId) {
            requestCode = 1;
        } else if (2131625143 == viewId) {
            intent.putExtra("photoAlbum", "application");
            intent.putExtra("camera", true);
            intent.putExtra("comments_enabled", true);
            intent.putExtra("do_upload", true);
            intent.putExtra("moveToBack", true);
            requestCode = 2;
        } else if (2131625146 == viewId) {
            requestCode = 3;
        }
        AppLaunchLog.fillWidgetMain(intent);
        views.setOnClickPendingIntent(viewId, PendingIntent.getActivity(context, requestCode, intent, 134217728));
    }
}
