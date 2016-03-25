package ru.ok.android.db;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import ru.ok.android.utils.Logger;

public class DbFailureActivity extends Activity {
    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void onCreate(android.os.Bundle r3) {
        /*
        r2 = this;
        super.onCreate(r3);
        r1 = "";
        ru.ok.android.utils.Logger.m172d(r1);
        r1 = r2.getIntent();	 Catch:{ Exception -> 0x0033 }
        ru.ok.android.onelog.AppLaunchLogHelper.logIntent(r1);	 Catch:{ Exception -> 0x0033 }
        r1 = r2.startAppDeatailsSettigns();	 Catch:{ Exception -> 0x0033 }
        if (r1 != 0) goto L_0x0022;
    L_0x0016:
        r1 = r2.startFroyoInstalledAppDetailsActivity();	 Catch:{ Exception -> 0x0033 }
        if (r1 != 0) goto L_0x0022;
    L_0x001c:
        r1 = r2.startManageAppsSettings();	 Catch:{ Exception -> 0x0033 }
        if (r1 == 0) goto L_0x002c;
    L_0x0022:
        r1 = "Launched app settings";
        ru.ok.android.utils.Logger.m172d(r1);	 Catch:{ Exception -> 0x0033 }
    L_0x0028:
        r2.finish();
    L_0x002b:
        return;
    L_0x002c:
        r1 = "Failed to launch app settings";
        ru.ok.android.utils.Logger.m184w(r1);	 Catch:{ Exception -> 0x0033 }
        goto L_0x0028;
    L_0x0033:
        r0 = move-exception;
        ru.ok.android.utils.Logger.m178e(r0);	 Catch:{ all -> 0x003b }
        r2.finish();
        goto L_0x002b;
    L_0x003b:
        r1 = move-exception;
        r2.finish();
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.db.DbFailureActivity.onCreate(android.os.Bundle):void");
    }

    private boolean startAppDeatailsSettigns() {
        Logger.m172d("");
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Logger.m184w("Activity not found: " + e);
            return false;
        }
    }

    private boolean startManageAppsSettings() {
        Logger.m172d("");
        try {
            Intent intent = new Intent("android.settings.MANAGE_APPLICATIONS_SETTINGS");
            intent.addCategory("android.intent.category.DEFAULT");
            startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Logger.m184w("Activity not found: " + e);
            return false;
        }
    }

    public boolean startFroyoInstalledAppDetailsActivity() {
        Logger.m172d("");
        try {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.setAction("android.intent.action.VIEW");
            intent.putExtra("pkg", getPackageName());
            intent.setFlags(268435456);
            startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Logger.m184w("Activity not found: " + e);
            return false;
        }
    }
}
