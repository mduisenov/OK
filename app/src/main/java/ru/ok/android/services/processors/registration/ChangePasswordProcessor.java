package ru.ok.android.services.processors.registration;

import android.content.Context;
import android.os.Bundle;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.authorization.LoginControl;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.ServiceStateHolder;
import ru.ok.java.api.request.registration.ChangePasswordRequest;

public final class ChangePasswordProcessor {
    @Subscribe(on = 2131623944, to = 2131623949)
    public void changePassword(BusEvent event) {
        int resultCode;
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(event.bundleInput.getString("old_passwrod"), event.bundleInput.getString("new_passwrod"));
        JsonSessionTransportProvider transportProvider = JsonSessionTransportProvider.getInstance();
        Context context = OdnoklassnikiApplication.getContext();
        Bundle bundleOutput = new Bundle();
        try {
            JsonHttpResult jsonHttpResult = transportProvider.execJsonHttpMethod(changePasswordRequest, transportProvider.getStateHolder().getBaseUrl().replace("http://", "https://"));
            GlobalBus.send(2131624041, new BusEvent());
            String newToken = jsonHttpResult.getResultAsObject().optString("auth_token", null);
            ServiceStateHolder serviceStateHolder = transportProvider.getStateHolder();
            serviceStateHolder.setAuthenticationHash(null);
            Settings.storeStrValue(context, "authHash", null);
            if (newToken != null) {
                Settings.storeToken(context, newToken);
                serviceStateHolder.setAuthenticationToken(newToken);
                LoginControl.generalLoginLogic(context);
            } else {
                Settings.storeToken(context, "");
            }
            resultCode = -1;
        } catch (Throwable e) {
            Logger.m178e(e);
            CommandProcessor.fillErrorBundle(bundleOutput, e);
            resultCode = -2;
        }
        GlobalBus.send(2131624129, new BusEvent(event.bundleInput, bundleOutput, resultCode));
    }
}
