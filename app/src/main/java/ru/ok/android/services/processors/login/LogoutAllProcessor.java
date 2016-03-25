package ru.ok.android.services.processors.login;

import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.request.LogoutAllRequest;

public final class LogoutAllProcessor {
    @Subscribe(on = 2131623944, to = 2131623985)
    public final void logoutAll(BusEvent busEvent) {
        try {
            String token = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new LogoutAllRequest(busEvent.bundleInput.getString("password"))).getResultAsObject().optString("auth_token");
            Bundle bundle = new Bundle();
            bundle.putString("token", token);
            GlobalBus.send(2131624237, new BusEvent(busEvent.bundleInput, bundle, -1));
        } catch (Exception e) {
            GlobalBus.send(2131624237, new BusEvent(busEvent.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }
}
