package ru.ok.android.utils.controls.authorization;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import ru.ok.android.ui.activity.main.OdklActivity;
import ru.ok.android.utils.localization.LocalizationManager;

public class OnLogoutDefaultListener implements OnLogoutListener {
    private final Activity activity;

    public OnLogoutDefaultListener(Activity activity) {
        this.activity = activity;
    }

    public void onLogoutSuccessful() {
    }

    public void onLogoutError(Exception e) {
    }

    public void onStartLogout() {
        onLogoutEvent();
    }

    protected void onLogoutEvent() {
        if (this.activity instanceof OdklActivity) {
            OdklActivity odklActivity = this.activity;
            if (odklActivity.getTabbarView() != null) {
                odklActivity.getTabbarView().clear();
            }
        }
        ((NotificationManager) this.activity.getSystemService("notification")).cancelAll();
        Intent intent = new Intent("kill");
        intent.setType("ru.ok.android/logout");
        this.activity.sendBroadcast(intent);
        this.activity.finish();
        LocalizationManager localizationManager = LocalizationManager.from(this.activity);
        if (localizationManager != null) {
            localizationManager.resetLocale();
        }
        Intent i1 = new Intent(this.activity, OdklActivity.class);
        i1.setFlags(268533760);
        i1.putExtra("key_need_check_login", true);
        this.activity.startActivity(i1);
    }
}
