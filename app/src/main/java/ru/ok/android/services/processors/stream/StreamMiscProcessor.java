package ru.ok.android.services.processors.stream;

import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.JsonBooleanResultParser;
import ru.ok.java.api.request.stream.StreamMarkAsSpamRequest;

public final class StreamMiscProcessor {
    @Subscribe(on = 2131623944, to = 2131624107)
    public void feedMarkAsSpam(BusEvent event) {
        String spamId = event.bundleInput.getString("SPAM_ID");
        String deleteId = event.bundleInput.getString("DELETE_ID");
        String logContext = event.bundleInput.getString("LOG_CONTEXT");
        Logger.m173d("spamId=%s deleteId=%s", spamId, deleteId);
        try {
            int i;
            boolean result = ((Boolean) new JsonBooleanResultParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new StreamMarkAsSpamRequest(spamId, deleteId, logContext))).parse()).booleanValue();
            Bundle bundle = event.bundleInput;
            if (result) {
                i = -1;
            } else {
                i = -2;
            }
            GlobalBus.send(2131624249, new BusEvent(bundle, null, i));
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to mark feed as spam");
            GlobalBus.send(2131624249, new BusEvent(event.bundleInput, null, -2));
        }
    }
}
