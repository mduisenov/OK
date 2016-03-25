package ru.ok.android.widget.music;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

@TargetApi(16)
public final class MusicResizableWidget extends MusicBaseWidget {
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        requestAppWidgetUpdate(context, appWidgetId);
    }

    protected int getLayoutId(Context context, AppWidgetManager manager, int appWidgetId) {
        if ((transformHeight(context, manager.getAppWidgetOptions(appWidgetId).getInt("appWidgetMinHeight", 0)) + 30) / 70 < 2) {
            return 2130903578;
        }
        return 2130903575;
    }

    private static int transformHeight(Context context, int minHeight) {
        if (!TextUtils.equals("C6603", Build.MODEL)) {
            return minHeight;
        }
        return (int) ((((float) minHeight) / context.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
