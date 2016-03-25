package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.json.photo.JsonGetPhotoAlbumInfoParser;
import ru.ok.java.api.request.image.GetPhotoAlbumInfoRequest;
import ru.ok.java.api.request.image.GetPhotoAlbumInfoRequest.FIELDS;
import ru.ok.java.api.request.param.BaseStringParam;
import ru.ok.java.api.utils.JsonUtil;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.photo.PhotoAlbumInfo;

public final class GetPhotoAlbumProcessor {
    @Subscribe(on = 2131623944, to = 2131624000)
    public void getAlbumInfo(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resultCode = -2;
        String aid = bundleInput.getString("aid");
        if (!TextUtils.isEmpty(aid)) {
            GetPhotoAlbumInfoRequest request;
            String fid = bundleInput.getString("fid");
            String gid = bundleInput.getString("gid");
            RequestFieldsBuilder builder = new RequestFieldsBuilder().addFields(FIELDS.ALBUM_ALL, FIELDS.PHOTO_ALL, FIELDS.LIKE_SUMMARY);
            if (!TextUtils.isEmpty(gid)) {
                builder.withPrefix("group_");
            }
            String fields = builder.build();
            if (TextUtils.isEmpty(gid)) {
                request = new GetPhotoAlbumInfoRequest(new BaseStringParam(aid), new BaseStringParam(fid), null);
            } else {
                request = new GetPhotoAlbumInfoRequest(new BaseStringParam(aid), null, new BaseStringParam(gid));
            }
            request.setFields(fields);
            try {
                JsonHttpResult httpResult = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request);
                JsonGetPhotoAlbumInfoParser parser = new JsonGetPhotoAlbumInfoParser();
                PhotoAlbumInfo albumInfo = JsonGetPhotoAlbumInfoParser.parse(JsonUtil.getJsonObjectSafely(httpResult.getResultAsObject(), "album"));
                if (albumInfo != null) {
                    bundleOutput.putParcelable("lbmnfo", albumInfo);
                    resultCode = -1;
                }
            } catch (Throwable exc) {
                Logger.m178e(exc);
            }
            GlobalBus.send(2131624177, new BusEvent(bundleInput, bundleOutput, resultCode));
        }
    }
}
