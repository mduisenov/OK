package ru.ok.android.playservices;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import com.google.android.gms.common.GooglePlayServicesUtil;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.PopupDialogsSyncUtils;
import ru.ok.android.ui.dialogs.AlertFragmentDialog;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;

public final class PlayServicesUtils {

    /* renamed from: ru.ok.android.playservices.PlayServicesUtils.1 */
    static class C03851 implements Runnable {
        final /* synthetic */ Dialog val$dialog;

        C03851(Dialog dialog) {
            this.val$dialog = dialog;
        }

        public void run() {
            StatisticManager.getInstance().addStatisticEvent("gps-recoverable-dialog-show", new Pair[0]);
            this.val$dialog.show();
        }
    }

    /* renamed from: ru.ok.android.playservices.PlayServicesUtils.2 */
    static class C03862 implements Runnable {
        final /* synthetic */ FragmentActivity val$activity;

        C03862(FragmentActivity fragmentActivity) {
            this.val$activity = fragmentActivity;
        }

        public void run() {
            AlertFragmentDialog.newInstance(LocalizationManager.getString(this.val$activity, 2131165923), LocalizationManager.getString(this.val$activity, 2131165922), 0).show(this.val$activity.getSupportFragmentManager(), "gps");
            Settings.storeBoolValueInvariable(this.val$activity, "gps:unavailable_show", true);
            StatisticManager.getInstance().addStatisticEvent("gps-unrecoverable-dialog-show", new Pair[0]);
        }
    }

    public static boolean isPlayServicesAvailable(Context context) {
        Logger.m173d("resultCode: %d", Integer.valueOf(GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)));
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == 0) {
            return true;
        }
        return false;
    }

    public static void showRecoveryDialog(FragmentActivity activity, int requestCode) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != 0) {
            Logger.m173d("resultCode: %d", Integer.valueOf(resultCode));
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, requestCode);
                if (dialog == null) {
                    Logger.m184w("GooglePlayServicesUtil#getErrorDialog returned null");
                    showGPSUnavailabilityDialog(activity);
                    return;
                }
                PopupDialogsSyncUtils.atomicCheckAndShow(activity, new C03851(dialog));
                return;
            }
            Logger.m184w("We can't recover GPS unavailability :(");
            showGPSUnavailabilityDialog(activity);
        }
    }

    private static void showGPSUnavailabilityDialog(FragmentActivity activity) {
        if (!Settings.getBoolValueInvariable(activity, "gps:unavailable_show", false)) {
            PopupDialogsSyncUtils.atomicCheckAndShow(activity, new C03862(activity));
        }
    }
}
