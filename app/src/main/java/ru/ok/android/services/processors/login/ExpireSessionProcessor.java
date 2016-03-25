package ru.ok.android.services.processors.login;

import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.LogOutRequest;

public final class ExpireSessionProcessor {
    @Subscribe(on = 2131623944, to = 2131624041)
    public void expireSession(BusEvent event) {
        try {
            JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new LogOutRequest());
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to expire session.");
        }
    }
}
