package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.image.SetAlbumMainPhotoRequest;
import ru.ok.model.photo.PhotoInfo;

public final class SetAlbumMainPhotoProcessor {
    @Subscribe(on = 2131623944, to = 2131624114)
    public void setAlbumMainPhoto(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        String aid = bundleInput.getString("aid");
        String pid = bundleInput.getString("pid");
        String gid = bundleInput.getString("gid");
        PhotoInfo photoInfo = (PhotoInfo) bundleInput.getParcelable("pnfo");
        bundleOutput.putString("aid", aid);
        bundleOutput.putString("pid", pid);
        bundleOutput.putString("gid", gid);
        bundleOutput.putParcelable("pnfo", photoInfo);
        if (!(TextUtils.isEmpty(aid) || TextUtils.isEmpty(pid))) {
            try {
                JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new SetAlbumMainPhotoRequest(aid, pid, gid));
                resultCode = -1;
            } catch (Throwable exc) {
                Logger.m178e(exc);
            }
        }
        GlobalBus.send(2131624258, new BusEvent(bundleInput, bundleOutput, resultCode));
    }
}
