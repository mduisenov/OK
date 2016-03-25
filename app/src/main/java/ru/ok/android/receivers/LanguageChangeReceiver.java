package ru.ok.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;

public class LanguageChangeReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (!Settings.hasLoginData(context)) {
            LocalizationManager localizationManager = LocalizationManager.from(context);
            if (localizationManager != null) {
                localizationManager.resetLocale();
            }
        }
    }
}
