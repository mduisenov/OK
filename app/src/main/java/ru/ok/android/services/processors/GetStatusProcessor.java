package ru.ok.android.services.processors;

import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.JsonResultGetStatusParser;
import ru.ok.java.api.request.GetStatusRequest;

public final class GetStatusProcessor {
    @Subscribe(on = 2131623944, to = 2131624070)
    public void getStatus(BusEvent event) {
        Logger.m172d("visit on get status processor");
        try {
            SetStatusProcessor.sendStatusChangedBroadcast(getStatus());
        } catch (Exception e) {
            Logger.m173d("status get error %s", e);
        }
    }

    private String getStatus() throws Exception {
        return new JsonResultGetStatusParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GetStatusRequest())).parse();
    }
}
