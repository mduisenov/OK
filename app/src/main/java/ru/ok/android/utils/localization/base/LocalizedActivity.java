package ru.ok.android.utils.localization.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.onelog.AppLaunchLogHelper;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.compat.BaseCompatDrawerToolbarActivity;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.utils.ViewServer;
import ru.ok.android.utils.localization.LocalizationManager;

public abstract class LocalizedActivity extends BaseCompatDrawerToolbarActivity implements LocalizationSupportingView {
    private final LocalizedViewUtils _utils;

    protected abstract void onCreateLocalized(Bundle bundle);

    public LocalizedActivity() {
        this._utils = new LocalizedViewUtils(this);
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        this._utils.setRootResourceId(layoutResID);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AppLaunchLogHelper.logIntent(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(5);
        super.onCreate(savedInstanceState);
        AppLaunchLogHelper.logIntent(getIntent());
        onCreateLocalized(savedInstanceState);
        this._utils.onCreate();
    }

    protected void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow((Activity) this);
    }

    protected void onDestroy() {
        super.onDestroy();
        this._utils.onDestroy();
    }

    public void finish() {
        this._utils.onFinishingActivity();
        super.finish();
    }

    protected void onStart() {
        super.onStart();
        StatisticManager.getInstance().startSession(this);
    }

    protected void onStop() {
        StatisticManager.getInstance().endSession(this);
        super.onStop();
    }

    public void onLocalizationChanged() {
        if (!isFinishing() && BaseCompatToolbarActivity.isUseTabbar(this) && getTabbarView() != null) {
            getTabbarView().invalidateLocale();
        }
    }

    public final Context getContext() {
        return this;
    }

    public String getStringLocalized(int stringId) {
        return LocalizationManager.getString((Context) this, stringId);
    }

    protected String getStringLocalized(int stringId, Object... args) {
        return LocalizationManager.getString(this, stringId, args);
    }
}
