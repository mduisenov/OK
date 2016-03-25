package ru.ok.android.onelog;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import ru.ok.android.utils.Logger;
import ru.ok.onelog.app.launch.AppLaunchCommonSource;

public class AppLaunchMonitor implements ActivityLifecycleCallbacks {
    private static final AppLaunchMonitor instance;
    private long lastStopTimestamp;
    private int launchDetectionState;
    private int launchId;
    private int startedActivities;

    static {
        instance = new AppLaunchMonitor();
    }

    public static AppLaunchMonitor getInstance() {
        return instance;
    }

    private AppLaunchMonitor() {
        this.launchId = 0;
        this.launchDetectionState = 0;
    }

    public void onActivityStarted(@NonNull Activity activity) {
        this.startedActivities++;
        if (this.startedActivities == 1 && System.currentTimeMillis() - this.lastStopTimestamp > 5000 && this.launchDetectionState == 0) {
            this.launchId++;
            this.launchDetectionState = 1;
        }
        Logger.m173d("Started activities: %d. Launch state: %d", Integer.valueOf(this.startedActivities), Integer.valueOf(this.launchDetectionState));
    }

    public void onActivityResumed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityStopped(@NonNull Activity activity) {
        this.startedActivities--;
        if (this.startedActivities == 0) {
            if (this.launchDetectionState == 1) {
                AppLaunchLog.common(AppLaunchCommonSource.history);
            }
            this.launchDetectionState = 0;
        }
        this.lastStopTimestamp = System.currentTimeMillis();
        Logger.m173d("Stopped activities: %d. Launch state: %d", Integer.valueOf(this.startedActivities), Integer.valueOf(this.launchDetectionState));
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        Intent intent = activity.getIntent();
        if (intent != null && intent.hasCategory("android.intent.category.LAUNCHER") && "android.intent.action.MAIN".equals(intent.getAction())) {
            this.launchId++;
            AppLaunchLog.common(AppLaunchCommonSource.home_screen);
        }
    }

    public int getLaunchId() {
        return this.launchId;
    }

    void reportLaunchConsumed() {
        if (this.launchDetectionState == 0) {
            this.launchId++;
        }
        this.launchDetectionState = 2;
    }

    static void notify(Intent intent) {
        if (intent.getBooleanExtra("extra_notify_app_launch_monitor", false)) {
            getInstance().reportLaunchConsumed();
        }
        intent.removeExtra("extra_notify_app_launch_monitor");
    }
}
