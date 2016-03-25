package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.widget.MenuView;
import ru.ok.java.api.request.image.SetUserMainPhotoRequest;

public final class SetMainPhotoProcessor {
    @Subscribe(on = 2131623944, to = 2131624115)
    public void setMainPhoto(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        String pid = bundleInput.getString("pid");
        bundleOutput.putString("pid", pid);
        if (!TextUtils.isEmpty(pid)) {
            try {
                String result = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new SetUserMainPhotoRequest(pid)).getResultAsString();
                if (!(result == null || result.contains("false"))) {
                    resultCode = -1;
                }
            } catch (Throwable exc) {
                Logger.m178e(exc);
            }
        }
        GlobalBus.send(2131624259, new BusEvent(bundleInput, bundleOutput, resultCode));
        MenuView.updateAvatar();
    }
}
