package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.image.MarkPhotoSpamRequest;
import ru.ok.java.api.request.image.MarkPhotoSpamRequest.PhotoType;

public final class MarkPhotoSpamProcessor {
    @Subscribe(on = 2131623944, to = 2131624089)
    public void markPhotoSpam(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        String pid = bundleInput.getString("pid");
        int photoType = bundleInput.getInt("ptype");
        bundleOutput.putString("pid", pid);
        if (!TextUtils.isEmpty(pid)) {
            try {
                JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MarkPhotoSpamRequest(pid, PhotoType.values()[photoType]));
                resultCode = -1;
            } catch (Exception exc) {
                Logger.m177e(exc.getMessage(), exc);
            }
        }
        GlobalBus.send(2131624229, new BusEvent(bundleInput, bundleOutput, resultCode));
    }
}
