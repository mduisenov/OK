package ru.ok.android.utils.localization.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import ru.ok.android.utils.localization.LocalizationManager;

public final class LocalizedViewUtils {
    private final BroadcastReceiver _receiver;
    private int _rootResourceId;
    private final LocalizationSupportingView _view;
    private boolean isReceiverRegistered;

    /* renamed from: ru.ok.android.utils.localization.base.LocalizedViewUtils.1 */
    class C14691 extends BroadcastReceiver {
        C14691() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ru.ok.android.utils.localization.LOCALE_CHANGED")) {
                LocalizedViewUtils.this._view.onLocalizationChanged();
            }
        }
    }

    public LocalizedViewUtils(LocalizationSupportingView view) {
        this._receiver = new C14691();
        this.isReceiverRegistered = false;
        this._view = view;
    }

    public void onCreate() {
        callLocalization();
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        if (!this.isReceiverRegistered) {
            this.isReceiverRegistered = true;
            LocalBroadcastManager.getInstance(this._view.getContext()).registerReceiver(this._receiver, new IntentFilter("ru.ok.android.utils.localization.LOCALE_CHANGED"));
        }
    }

    private void unregisterBroadcastReceiver() {
        if (this.isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this._view.getContext()).unregisterReceiver(this._receiver);
            this.isReceiverRegistered = false;
        }
    }

    public void onFinishingActivity() {
        unregisterBroadcastReceiver();
    }

    public void onDestroy() {
        unregisterBroadcastReceiver();
    }

    private void callLocalization() {
        if (this._rootResourceId == 0) {
            return;
        }
        if (this._view instanceof PreferenceActivity) {
            LocalizationManager.from(this._view.getContext()).registerPreferenceActivity((PreferenceActivity) this._view, this._rootResourceId);
        } else if (this._view instanceof Activity) {
            LocalizationManager.from(this._view.getContext()).registerActivity((Activity) this._view, this._rootResourceId);
        } else if (this._view instanceof Fragment) {
            LocalizationManager.from(this._view.getContext()).registerFragment((Fragment) this._view, this._rootResourceId);
        }
    }

    public void setRootResourceId(int rootResourceId) {
        this._rootResourceId = rootResourceId;
    }
}
