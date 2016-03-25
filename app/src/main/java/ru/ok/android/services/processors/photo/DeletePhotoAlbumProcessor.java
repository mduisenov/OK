package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.java.api.request.image.DeletePhotoAlbumRequest;

public final class DeletePhotoAlbumProcessor {
    @Subscribe(on = 2131623944, to = 2131623976)
    public void deletePhotoAlbum(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resulCode = -2;
        String aid = bundleInput.getString("aid");
        String gid = bundleInput.getString("gid");
        if (!TextUtils.isEmpty(aid)) {
            bundleOutput.putString("aid", aid);
            try {
                JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new DeletePhotoAlbumRequest(aid, gid));
                resulCode = -1;
                EventsManager.getInstance().changePhotoCounter(-bundleInput.getInt("pcount"));
            } catch (Throwable exc) {
                Logger.m178e(exc);
            }
        }
        GlobalBus.send(2131624156, new BusEvent(bundleInput, bundleOutput, resulCode));
    }
}
