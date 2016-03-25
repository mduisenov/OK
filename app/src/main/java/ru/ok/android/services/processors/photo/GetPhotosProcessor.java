package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.json.photo.JsonPhotosInfoParser;
import ru.ok.java.api.request.image.GetPhotoInfoRequest.FIELDS;
import ru.ok.java.api.request.image.GetPhotosRequest;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;

public final class GetPhotosProcessor {
    @Subscribe(on = 2131623944, to = 2131624005)
    public void getPhotos(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        String albumid = bundleInput.getString("aid");
        String anchor = bundleInput.getString("anchr");
        boolean forward = bundleInput.getBoolean("fwd");
        int count = bundleInput.getInt("cnt");
        boolean detectTotalCount = bundleInput.getBoolean("dtctcnt");
        PhotoOwner photoOwner = (PhotoOwner) bundleInput.getParcelable("pwnr");
        bundleOutput.putString("aid", albumid);
        bundleOutput.putString("anchr", anchor);
        bundleOutput.putBoolean("fwd", forward);
        bundleOutput.putParcelable("pwnr", photoOwner);
        String fid = null;
        String gid = null;
        if (photoOwner.getType() == 1) {
            gid = photoOwner.getId();
        } else {
            fid = photoOwner.getId();
        }
        GetPhotosRequest request = new GetPhotosRequest(null, fid, gid, albumid, anchor, forward, count, detectTotalCount);
        RequestFieldsBuilder fieldsBuilder = new RequestFieldsBuilder().addField(FIELDS.ALL);
        if (photoOwner.getType() == 0) {
            fieldsBuilder.addField(FIELDS.TAG_COUNT);
        } else {
            fieldsBuilder.withPrefix("group_");
        }
        String fields = fieldsBuilder.build();
        request.setFields(fields);
        String str = " anchor: ";
        str = " forward: ";
        str = " fields: ";
        Logger.m172d("Trying load photos with params: aid: " + albumid + r22 + anchor + r22 + forward + r22 + fields);
        try {
            JsonHttpResult httpResult = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request);
            resultCode = -1;
            bundleOutput.putParcelable("phtsnfo", new JsonPhotosInfoParser().parse(httpResult.getResultAsObject()));
        } catch (Throwable exc) {
            Logger.m178e(exc);
        }
        GlobalBus.send(2131624182, new BusEvent(bundleInput, bundleOutput, resultCode));
    }
}
