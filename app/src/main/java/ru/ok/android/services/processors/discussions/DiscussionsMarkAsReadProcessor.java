package ru.ok.android.services.processors.discussions;

import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.discussions.DiscussionsMarkAsReadRequest;
import ru.ok.java.api.request.discussions.DiscussionsMarkDiscussionsAsReadRequest;

public final class DiscussionsMarkAsReadProcessor {
    @Subscribe(on = 2131623944, to = 2131623970)
    public void markAsRead(BusEvent event) {
        int resultCode = -2;
        try {
            if ("true".equals(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new DiscussionsMarkAsReadRequest()).getResultAsString())) {
                resultCode = -1;
            }
        } catch (Throwable ex) {
            Logger.m179e(ex, "Mark discussions as read request failed");
            CommandProcessor.fillErrorBundle(null, ex);
        }
        GlobalBus.send(2131624149, new BusEvent(event.bundleInput, null, resultCode));
    }

    @Subscribe(on = 2131623944, to = 2131623971)
    public void markDiscussionAsRead(BusEvent event) {
        int resultCode = -2;
        try {
            if ("true".equals(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new DiscussionsMarkDiscussionsAsReadRequest(event.bundleInput.getString("DISCUSSION_ID"), event.bundleInput.getString("DISCUSSION_TYPE"))).getResultAsString())) {
                resultCode = -1;
            }
        } catch (Throwable ex) {
            Logger.m179e(ex, "Mark discussion as read request failed");
            CommandProcessor.fillErrorBundle(null, ex);
        }
        GlobalBus.send(2131624150, new BusEvent(event.bundleInput, null, resultCode));
    }
}
