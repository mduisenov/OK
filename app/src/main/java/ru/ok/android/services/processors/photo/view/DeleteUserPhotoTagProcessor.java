package ru.ok.android.services.processors.photo.view;

import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.JsonBooleanParser;
import ru.ok.java.api.request.image.DeleteUserPhotoTagRequest;

public final class DeleteUserPhotoTagProcessor {
    @Subscribe(on = 2131623944, to = 2131623978)
    public void deletePhotoTag(BusEvent event) {
        Bundle bundleOutput = new Bundle();
        bundleOutput.putAll(event.bundleInput);
        int resultCode = -2;
        try {
            resultCode = ((Boolean) new JsonBooleanParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new DeleteUserPhotoTagRequest(event.bundleInput.getStringArray("pids")))).parse()).booleanValue() ? -1 : -2;
        } catch (Throwable exc) {
            Logger.m178e(exc);
        }
        GlobalBus.send(2131624158, new BusEvent(event.bundleInput, bundleOutput, resultCode));
    }
}
