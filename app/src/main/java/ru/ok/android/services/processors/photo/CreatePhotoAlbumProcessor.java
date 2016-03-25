package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.ServerReturnErrorException;
import ru.ok.java.api.request.image.CreatePhotoAlbumRequest;
import ru.ok.model.photo.PhotoAlbumInfo.AccessType;

public final class CreatePhotoAlbumProcessor {
    @Subscribe(on = 2131623944, to = 2131623961)
    public void createPhotoAlbum(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        String title = bundleInput.getString("ttl");
        int[] accessTypes = bundleInput.getIntArray("accss");
        String gid = bundleInput.getString("gid");
        bundleOutput.putString("ttl", title);
        bundleOutput.putIntArray("accss", accessTypes);
        bundleOutput.putString("gid", gid);
        if (title != null) {
            title = title.trim();
            if (!TextUtils.isEmpty(title)) {
                try {
                    bundleOutput.putString("aid", JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new CreatePhotoAlbumRequest(title, TextUtils.isEmpty(gid) ? getAccessTypeString(accessTypes) : null, gid)).httpResponse.replace("\"", ""));
                    resultCode = -1;
                } catch (ServerReturnErrorException rex) {
                    if (rex.getErrorCode() == 454) {
                        resultCode = 1;
                    }
                } catch (Throwable exc) {
                    Logger.m178e(exc);
                }
                GlobalBus.send(2131624140, new BusEvent(bundleInput, bundleOutput, resultCode));
            }
        }
        resultCode = 2;
        GlobalBus.send(2131624140, new BusEvent(bundleInput, bundleOutput, resultCode));
    }

    private String getAccessTypeString(int[] accessTypes) {
        if (accessTypes == null || accessTypes.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int accessTypeInt : accessTypes) {
            AccessType accessType = AccessType.values()[accessTypeInt];
            if (accessType.equals(AccessType.PUBLIC) || accessType.equals(AccessType.FRIENDS)) {
                return accessType.getApiJsonParamValue();
            }
            builder.append(accessType.getApiJsonParamValue()).append(",");
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }
}
