package ru.ok.android.services.app.upgrade;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Arrays;
import java.util.Comparator;
import ru.ok.android.services.app.upgrade.tasks.RemoveImageCacheTask;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;

public class AppUpgradeService extends IntentService {

    /* renamed from: ru.ok.android.services.app.upgrade.AppUpgradeService.1 */
    class C04321 implements Comparator<AppUpgradeTask> {
        C04321() {
        }

        public int compare(AppUpgradeTask lhs, AppUpgradeTask rhs) {
            return lhs.getUpgradeVersion() - rhs.getUpgradeVersion();
        }
    }

    public static class AppUpgradeReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Logger.m172d("AppUpgradeReceived!");
            context.startService(new Intent(context, AppUpgradeService.class));
        }
    }

    public AppUpgradeService() {
        super("app-upgrade-worker-thread");
        setIntentRedelivery(true);
    }

    protected void onHandleIntent(Intent intent) {
        int lastUpgradedVersion = Settings.getLastAppUpgradeVersion(this);
        AppUpgradeTask[] upgradeTasks = getTasks();
        if (lastUpgradedVersion > 182) {
            lastUpgradedVersion = 0;
        }
        int highestTaskVersion = upgradeTasks[upgradeTasks.length - 1].getUpgradeVersion();
        if (highestTaskVersion <= lastUpgradedVersion) {
            Logger.m173d("Skip upgrade! maxTaskVersion: %d. Last upgraded version: %d.", Integer.valueOf(highestTaskVersion), Integer.valueOf(lastUpgradedVersion));
            return;
        }
        int oldUpgradeVersion = lastUpgradedVersion;
        for (AppUpgradeTask task : upgradeTasks) {
            int taskUpgradeVersion = task.getUpgradeVersion();
            if (lastUpgradedVersion < taskUpgradeVersion) {
                try {
                    task.upgrade(this);
                } catch (Throwable e) {
                    Logger.m179e(e, "Failed to perform app upgrade!");
                }
                oldUpgradeVersion = taskUpgradeVersion;
            }
        }
        Settings.setAppLastUpgradeVersion(this, highestTaskVersion);
    }

    private AppUpgradeTask[] getTasks() {
        AppUpgradeTask[] upgradeTasks = new AppUpgradeTask[]{new RemoveImageCacheTask()};
        Arrays.sort(upgradeTasks, new C04321());
        return upgradeTasks;
    }
}
