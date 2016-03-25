package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.exceptions.ServerReturnErrorException;
import ru.ok.java.api.request.image.EditPhotoRequest;

public final class EditPhotoProcessor {
    @Subscribe(on = 2131623944, to = 2131623980)
    public void editPhoto(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        String pid = bundleInput.getString("pid");
        String gid = bundleInput.getString("gid");
        String description = bundleInput.getString("descr");
        bundleOutput.putString("pid", pid);
        bundleOutput.putString("gid", gid);
        bundleOutput.putString("descr", description);
        try {
            if (JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new EditPhotoRequest(pid, gid, description)).httpResponse.contains("true")) {
                resultCode = -1;
            }
        } catch (ServerReturnErrorException rex) {
            if (rex.getErrorCode() == 454) {
                resultCode = 1;
            }
        } catch (Exception e) {
        }
        GlobalBus.send(2131624160, new BusEvent(bundleInput, bundleOutput, resultCode));
    }
}
