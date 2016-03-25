package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.request.image.LikePhotoAlbumRequest;

public final class LikePhotoAlbumProcessor {
    @Subscribe(on = 2131623944, to = 2131624015)
    public void likePhotoAlbum(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        int resultCode = -2;
        try {
            String result = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new LikePhotoAlbumRequest(bundleInput.getString("aid"), bundleInput.getString("gid"))).getResultAsString();
            if (!(TextUtils.isEmpty(result) || result.contains("false"))) {
                resultCode = -1;
            }
        } catch (Exception e) {
        }
        GlobalBus.send(2131624190, new BusEvent(bundleInput, null, resultCode));
    }
}
