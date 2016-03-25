package ru.ok.android.services.app.upgrade.tasks;

import android.content.Context;
import java.io.File;
import ru.ok.android.services.app.upgrade.AppUpgradeException;
import ru.ok.android.services.app.upgrade.AppUpgradeTask;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.Logger;

public class RemoveImageCacheTask implements AppUpgradeTask {
    public int getUpgradeVersion() {
        return 145;
    }

    public void upgrade(Context context) throws AppUpgradeException {
        File file = FileUtils.getCacheDir(context, "images");
        if (!file.exists() || !file.isDirectory()) {
            Logger.m184w("Failed to find old image cache!");
        } else if (FileUtils.deleteFolder(file)) {
            Logger.m172d("Old image cache has been deleted!");
        } else {
            throw new AppUpgradeException("Failed to delete old image cache!");
        }
    }
}
