package ru.ok.android.services.processors.registration;

import android.content.Context;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.settings.SettingsGetProcessor;
import ru.ok.android.utils.Logger;

public final class AuthorizationSettingsProcessor {
    private final Context context;

    public AuthorizationSettingsProcessor(Context context) {
        this.context = context;
    }

    @Subscribe(on = 2131623944, to = 2131623948)
    public void getLoginVersion() {
        syncAuthorizationSettings();
        GlobalBus.send(2131624128, new BusEvent());
    }

    private void syncAuthorizationSettings() {
        try {
            AuthorizationPreferences.savePreferences(SettingsGetProcessor.performSettingGetJsonRequest(this.context, AuthorizationPreferences.getSettingsKeys()));
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }
}
