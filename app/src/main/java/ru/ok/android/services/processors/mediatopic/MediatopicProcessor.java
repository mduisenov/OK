package ru.ok.android.services.processors.mediatopic;

import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.mediatopics.MediaTopicDeleteParser;
import ru.ok.java.api.json.mediatopics.MediaTopicPinParser;
import ru.ok.java.api.json.mediatopics.MediaTopicSetToStatusParser;
import ru.ok.java.api.request.mediatopic.MediaTopicDeleteRequest;
import ru.ok.java.api.request.mediatopic.MediaTopicPinRequest;
import ru.ok.java.api.request.mediatopic.MediaTopicSetToStatusRequest;
import ru.ok.java.api.response.mediatopics.MediaTopicDeleteResponse;
import ru.ok.java.api.response.mediatopics.MediaTopicPinResponse;
import ru.ok.java.api.response.mediatopics.MediaTopicSetToStatusResponse;

public final class MediatopicProcessor {
    @Subscribe(on = 2131623944, to = 2131624018)
    public void delete(BusEvent event) {
        try {
            int i;
            MediaTopicDeleteResponse result = new MediaTopicDeleteParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MediaTopicDeleteRequest(event.bundleInput.getLong("mediatopic_id", 0), event.bundleInput.getString("log_context"))).getResultAsObject()).parse();
            Bundle bundle = event.bundleInput;
            if (result.success) {
                i = -1;
            } else {
                i = -2;
            }
            GlobalBus.send(2131624192, new BusEvent(bundle, null, i));
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to delete mediatopic");
            GlobalBus.send(2131624192, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131624019)
    public void pin(BusEvent event) {
        try {
            int i;
            MediaTopicPinResponse result = new MediaTopicPinParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MediaTopicPinRequest(event.bundleInput.getLong("mediatopic_id", 0), event.bundleInput.getBoolean("pin_on", false), event.bundleInput.getString("log_context"))).getResultAsObject()).parse();
            Bundle bundle = event.bundleInput;
            if (result.success) {
                i = -1;
            } else {
                i = -2;
            }
            GlobalBus.send(2131624193, new BusEvent(bundle, null, i));
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to pin mediatopic");
            GlobalBus.send(2131624193, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }

    @Subscribe(on = 2131623944, to = 2131624020)
    public void setToStatus(BusEvent event) {
        try {
            int i;
            MediaTopicSetToStatusResponse result = new MediaTopicSetToStatusParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MediaTopicSetToStatusRequest(event.bundleInput.getLong("mediatopic_id", 0), event.bundleInput.getString("log_context"))).getResultAsObject()).parse();
            Bundle bundle = event.bundleInput;
            if (result.success) {
                i = -1;
            } else {
                i = -2;
            }
            GlobalBus.send(2131624194, new BusEvent(bundle, null, i));
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to set to status mediatopic");
            GlobalBus.send(2131624194, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }
}
