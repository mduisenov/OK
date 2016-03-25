package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import org.json.JSONObject;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.image.MarkPhotoRequest;

public final class MarkPhotoProcessor {
    @Subscribe(on = 2131623944, to = 2131624088)
    public void markPhoto(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        String photoId = bundleInput.getString("pid");
        int mark = bundleInput.getInt("mrk");
        bundleOutput.putString("pid", photoId);
        try {
            JSONObject result = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MarkPhotoRequest(photoId, mark)).getResultAsObject();
            if (result != null && result.has("success") && result.getBoolean("success")) {
                resultCode = -1;
            } else {
                resultCode = 1;
            }
        } catch (Exception exc) {
            Logger.m177e("CAN'T MARK PHOTO", exc);
        }
        GlobalBus.send(2131624228, new BusEvent(bundleInput, bundleOutput, resultCode));
    }
}
