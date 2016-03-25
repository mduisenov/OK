package ru.ok.android.utils.localization.base;

import android.content.Context;
import android.os.Bundle;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.compat.PreferenceCompatActivity;

public abstract class LocalizedPreferencesActivity extends PreferenceCompatActivity implements LocalizationSupportingView {
    private final LocalizedViewUtils _utils;

    protected abstract void onCreateLocalized(Bundle bundle);

    public LocalizedPreferencesActivity() {
        this._utils = new LocalizedViewUtils(this);
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        this._utils.setRootResourceId(layoutResID);
    }

    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateLocalized(savedInstanceState);
        this._utils.onCreate();
    }

    protected void onDestroy() {
        super.onDestroy();
        this._utils.onDestroy();
    }

    protected void onStart() {
        super.onStart();
        StatisticManager.getInstance().startSession(this);
    }

    protected void onStop() {
        StatisticManager.getInstance().endSession(this);
        super.onStop();
    }

    public void addPreferencesFromResource(int preferencesResId) {
        super.addPreferencesFromResource(preferencesResId);
        this._utils.setRootResourceId(preferencesResId);
    }

    public final Context getContext() {
        return this;
    }

    public void onLocalizationChanged() {
    }
}
