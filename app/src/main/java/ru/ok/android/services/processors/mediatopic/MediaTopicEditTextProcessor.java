package ru.ok.android.services.processors.mediatopic;

import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.json.mediatopics.MediaTopicEditTextParser;
import ru.ok.java.api.request.mediatopic.MediaTopicEditTextRequest;
import ru.ok.java.api.response.mediatopics.MediaTopicEditTextResponse;

public final class MediaTopicEditTextProcessor {
    @Subscribe(on = 2131623944, to = 2131624090)
    public void editMediaTopicText(BusEvent event) {
        try {
            int i;
            MediaTopicEditTextResponse result = new MediaTopicEditTextParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MediaTopicEditTextRequest(event.bundleInput.getString("mediatopic_id"), event.bundleInput.getString("new_text"), event.bundleInput.getInt("block_index"))).getResultAsObject()).parse();
            Bundle bundle = event.bundleInput;
            if (result.success) {
                i = -1;
            } else {
                i = -2;
            }
            GlobalBus.send(2131624230, new BusEvent(bundle, null, i));
        } catch (Exception e) {
            GlobalBus.send(2131624230, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e, true), -2));
        }
    }
}
