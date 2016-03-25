package ru.ok.android.ui.dialogs.rate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.onelog.rate.RateDialogShownFactory;

public class RateDialog {
    public static String getKeyTime(String nameFicha) {
        return "key_time_show_rate" + nameFicha;
    }

    public static String getKeyCountShow(String nameFicha) {
        return "key_count_show_rate" + nameFicha;
    }

    public static String getKeyElseRateKey(String nameFicha) {
        return "key_show_else_rate" + nameFicha;
    }

    private static void showDialog(FragmentManager manager, int resPos, int resNeg, int resNeu, int resTitle, int resText, String nameFicha) {
        RateDialogFragment.newInstance(nameFicha, resPos, resNeg, resNeu, resTitle, resText).show(manager, null);
    }

    private static void initFirstTime(Context context, String nameFicha) {
        if (Settings.getLongValue(context, getKeyTime(nameFicha), 0) == 0) {
            Settings.storeLongValue(context, getKeyTime(nameFicha), System.currentTimeMillis());
        }
    }

    private static boolean isNeedToShowByTime(Context context, String nameFicha, long timeInMillis) {
        Logger.m173d("firstTimeShown=%d elapsedTime=%d", Long.valueOf(Settings.getLongValue(context, getKeyTime(nameFicha), System.currentTimeMillis())), Long.valueOf(System.currentTimeMillis() - Settings.getLongValue(context, getKeyTime(nameFicha), System.currentTimeMillis())));
        if (System.currentTimeMillis() - Settings.getLongValue(context, getKeyTime(nameFicha), System.currentTimeMillis()) > timeInMillis) {
            return true;
        }
        return false;
    }

    private static boolean isNeedToShowByLaunchCount(Context context, String nameFicha, int countCheck) {
        int count = Settings.getIntValue(context, getKeyCountShow(nameFicha), 0);
        Logger.m173d("launchCount=%d", Integer.valueOf(count));
        if (countCheck == 0 || (count % countCheck == 0 && count != 0)) {
            return true;
        }
        return false;
    }

    private static boolean hasNotBeenShownPreviously(Context context, String nameFicha) {
        return Settings.getBoolValueInvariable(context, getKeyElseRateKey(nameFicha), true);
    }

    static void setHasBeenShownDialog(Context context, String nameFicha) {
        Settings.storeBoolValueInvariable(context, getKeyElseRateKey(nameFicha), false);
    }

    private static void incrementLaunchCount(Context context, String nameFicha) {
        Settings.storeIntValue(context, getKeyCountShow(nameFicha), Settings.getIntValue(context, getKeyCountShow(nameFicha), 0) + 1);
    }

    public static void clearCounter(Context context, String nameFicha) {
        Settings.storeLongValue(context, getKeyTime(nameFicha), 0);
        Settings.storeIntValue(context, getKeyCountShow(nameFicha), 0);
    }

    public static void showDialogIfNeeded(@NonNull Context context, FragmentManager fragmentManager) {
        String feature = Settings.getStrValueInvariable(context, "rate.dialog.feature", null);
        if (TextUtils.isEmpty(feature)) {
            Logger.m172d("Feature for rate dialog is not set.");
            return;
        }
        Context context2 = context;
        FragmentManager fragmentManager2 = fragmentManager;
        showDialogIfNeeded(context2, fragmentManager2, feature, (long) Settings.getIntValueInvariable(context, "rate.dialog.interval.time", Integer.MAX_VALUE), Settings.getIntValueInvariable(context, "rate.dialog.interval.launches", Integer.MAX_VALUE), 2131166427, 2131166425, 2131166426, 2131166429, 2131166428);
    }

    public static void showDialogIfNeeded(@NonNull Context context, FragmentManager fragmentManager, String featureId, long timeIntervalSec, int launchInterval, int resPos, int resNeg, int resNeu, int resTitle, int resText) {
        Logger.m173d("featureId=%s launchInterval=%d timeIntervalSec=%d", featureId, Integer.valueOf(launchInterval), Long.valueOf(timeIntervalSec));
        initFirstTime(context, featureId);
        if (hasNotBeenShownPreviously(context, featureId)) {
            boolean doShow = false;
            if (isNeedToShowByLaunchCount(context, featureId, launchInterval)) {
                Logger.m172d("Rate dialog will be shown by launch count");
                doShow = true;
            } else if (isNeedToShowByTime(context, featureId, 1000 * timeIntervalSec)) {
                Logger.m172d("Rate dialog will be shown by time");
                doShow = true;
            }
            if (doShow) {
                showDialog(fragmentManager, resPos, resNeg, resNeu, resTitle, resText, featureId);
                clearCounter(context, featureId);
                OneLog.log(RateDialogShownFactory.get(featureId));
                return;
            }
            incrementLaunchCount(context, featureId);
            return;
        }
        Logger.m173d("Rate dialog was already shown for featureId=%s", featureId);
    }
}
