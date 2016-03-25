package ru.ok.android.services.app.upgrade;

import android.content.Context;

public interface AppUpgradeTask {
    int getUpgradeVersion();

    void upgrade(Context context) throws AppUpgradeException;
}
