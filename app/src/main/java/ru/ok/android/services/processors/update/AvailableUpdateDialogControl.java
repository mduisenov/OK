package ru.ok.android.services.processors.update;

import android.content.Context;
import android.text.TextUtils;
import ru.ok.android.ui.PopupDialogsSyncUtils;
import ru.ok.android.utils.localization.LocalizationManager;

public final class AvailableUpdateDialogControl {

    /* renamed from: ru.ok.android.services.processors.update.AvailableUpdateDialogControl.1 */
    static class C04961 implements Runnable {
        final /* synthetic */ Context val$context;
        final /* synthetic */ AvailableUpdateInfo val$info;

        C04961(AvailableUpdateInfo availableUpdateInfo, Context context) {
            this.val$info = availableUpdateInfo;
            this.val$context = context;
        }

        public void run() {
            new AvailableUpdateDialog(this.val$context, TextUtils.isEmpty(this.val$info.message) ? AvailableUpdateDialogControl.getDefaultText(this.val$context) : this.val$info.message, TextUtils.isEmpty(this.val$info.appStoreUrl) ? AvailableUpdateDialogControl.getDefaultAppStoreUrl(this.val$context) : this.val$info.appStoreUrl, !this.val$info.forceUpdate).show();
            AvailableUpdateDialogPreferences.saveShownForVersions(this.val$context, this.val$info.fromVersionCode, this.val$info.toVersionCode, System.currentTimeMillis());
        }
    }

    public static boolean showAvailableUpdateDialog(Context context) {
        AvailableUpdateInfo info = CheckUpdatePreferences.getAvailableUpdateInfo(context);
        return (info.isUpdateAvailable && info.isApplicable(context) && isTimeToShow(context, info)) ? PopupDialogsSyncUtils.atomicCheckAndShow(context, new C04961(info, context)) : false;
    }

    private static boolean isTimeToShow(Context context, AvailableUpdateInfo info) {
        boolean z = true;
        int remindIntervalSec = info.remindIntervalSec < 0 ? 0 : info.remindIntervalSec;
        AvailableUpdateDialogPreferences shownPrefs = AvailableUpdateDialogPreferences.fromSharedPreferences(context);
        if (remindIntervalSec != 0) {
            if (System.currentTimeMillis() - shownPrefs.shownTs <= ((long) remindIntervalSec)) {
                z = false;
            }
            return z;
        } else if (shownPrefs.alreadyShown(info.fromVersionCode, info.toVersionCode)) {
            return false;
        } else {
            return true;
        }
    }

    private static String getDefaultAppStoreUrl(Context context) {
        return "https://play.google.com/store/apps/details?id=" + context.getPackageName();
    }

    private static String getDefaultText(Context context) {
        return LocalizationManager.getString(context, 2131166752);
    }
}
