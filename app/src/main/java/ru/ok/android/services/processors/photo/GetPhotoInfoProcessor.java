package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.json.photo.JsonGetPhotoInfoParser;
import ru.ok.java.api.request.image.GetPhotoInfoRequest;
import ru.ok.java.api.request.image.GetPhotoInfoRequest.FIELDS;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;

public final class GetPhotoInfoProcessor {
    @Subscribe(on = 2131623944, to = 2131624003)
    public void getPhotoInfo(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int recultCode = -2;
        String pid = bundleInput.getString("id");
        String fid = bundleInput.getString("fid");
        String gid = bundleInput.getString("gid");
        if (pid != null) {
            bundleOutput.putString("id", pid);
            GetPhotoInfoRequest request = new GetPhotoInfoRequest(pid, fid, gid);
            RequestFieldsBuilder builder = new RequestFieldsBuilder().addField(FIELDS.ALL);
            if (!TextUtils.isEmpty(gid)) {
                builder.withPrefix("group_");
            }
            request.setFields(builder.build());
            try {
                JsonHttpResult httpResult = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request);
                JsonGetPhotoInfoParser parser = new JsonGetPhotoInfoParser();
                recultCode = -1;
                bundleOutput.putParcelable("xtrpi", JsonGetPhotoInfoParser.parse(httpResult.getResultAsObject()));
            } catch (Throwable exc) {
                Logger.m178e(exc);
            }
        }
        GlobalBus.send(2131624180, new BusEvent(bundleInput, bundleOutput, recultCode));
    }
}
