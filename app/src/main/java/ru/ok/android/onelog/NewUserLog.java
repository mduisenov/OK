package ru.ok.android.onelog;

import ru.ok.onelog.newuser.NewUserAction;
import ru.ok.onelog.newuser.NewUserFactory;

public class NewUserLog {
    private static int lastLaunchId;

    static {
        lastLaunchId = 0;
    }

    public static void logAppLaunch() {
        int launchId = AppLaunchMonitor.getInstance().getLaunchId();
        if (launchId != lastLaunchId) {
            lastLaunchId = launchId;
            OneLog.log(NewUserFactory.get(NewUserAction.launch_application));
        }
    }
}
