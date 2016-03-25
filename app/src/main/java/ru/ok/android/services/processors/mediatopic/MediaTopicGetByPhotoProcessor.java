package ru.ok.android.services.processors.mediatopic;

import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.JsonTopicGetByPhotoParser;
import ru.ok.java.api.request.mediatopic.MediaTopicGetByPhotoRequest;

public final class MediaTopicGetByPhotoProcessor {
    @Subscribe(on = 2131623944, to = 2131624091)
    public void process(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        String pid = bundleInput.getString("pid");
        try {
            String id = JsonTopicGetByPhotoParser.parse(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MediaTopicGetByPhotoRequest(pid)).getResultAsObject());
            if (TextUtils.isEmpty(id)) {
                Logger.m177e("Failed to parse mediatopic id: %s", result.getResultAsString());
            } else {
                bundleOutput.putString("tid", id);
                resultCode = -1;
            }
        } catch (Throwable exc) {
            Logger.m179e(exc, "Error getting media topic id by photo. PID: " + pid);
        }
        GlobalBus.send(2131624231, new BusEvent(bundleInput, bundleOutput, resultCode));
    }
}
