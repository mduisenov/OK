package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.java.api.request.image.DeletePhotoRequest;

public final class DeletePhotoProcessor {
    @Subscribe(on = 2131623944, to = 2131623977)
    public void deletePhoto(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        String pid = bundleInput.getString("pid");
        String gid = bundleInput.getString("gid");
        String oid = bundleInput.getString("oid");
        bundleOutput.putAll(bundleInput);
        if (!TextUtils.isEmpty(pid)) {
            try {
                if (JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new DeletePhotoRequest(pid, gid)).httpResponse.contains("true") && !TextUtils.isEmpty(oid) && OdnoklassnikiApplication.getCurrentUser() != null && oid.equals(OdnoklassnikiApplication.getCurrentUser().getId())) {
                    GlobalBus.send(2131624052, new BusEvent());
                }
                resultCode = -1;
                EventsManager.getInstance().changePhotoCounter(-1);
            } catch (Exception e) {
            }
        }
        GlobalBus.send(2131624157, new BusEvent(bundleInput, bundleOutput, resultCode));
    }
}
