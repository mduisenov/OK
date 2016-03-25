package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.request.image.LikePhotoRequest;

public final class LikePhotoProcessor {
    @Subscribe(on = 2131623944, to = 2131624016)
    public void likePhoto(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        String pid = bundleInput.getString("pid");
        String gid = bundleInput.getString("gid");
        bundleOutput.putString("pid", pid);
        try {
            JsonHttpResult response = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new LikePhotoRequest(pid, gid));
            resultCode = -1;
        } catch (Throwable exc) {
            Logger.m179e(exc, "CAN'T LIKE PHOTO");
        }
        GlobalBus.send(2131624191, new BusEvent(bundleInput, bundleOutput, resultCode));
    }
}
