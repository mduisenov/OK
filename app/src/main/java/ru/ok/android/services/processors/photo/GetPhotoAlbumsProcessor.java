package ru.ok.android.services.processors.photo;

import android.os.Bundle;
import org.json.JSONException;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.photo.JsonPhotoAlbumsInfoParser;
import ru.ok.java.api.request.image.GetPhotoAlbumsRequest;
import ru.ok.model.photo.PhotoAlbumsInfo;

public final class GetPhotoAlbumsProcessor {
    @Subscribe(on = 2131623944, to = 2131624002)
    public void getPhotoAlbums(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        Bundle bundleOutput = new Bundle();
        int resulCode = -2;
        String anchor = bundleInput.getString("anchr");
        boolean getAll = bundleInput.getBoolean("gtll");
        try {
            JsonPhotoAlbumsInfoParser parser = new JsonPhotoAlbumsInfoParser();
            PhotoAlbumsInfo pai = requestPhotoAlbumsInfo(anchor, parser, bundleInput);
            if (getAll && pai.isHasMore()) {
                while (pai.isHasMore()) {
                    PhotoAlbumsInfo nextPai = requestPhotoAlbumsInfo(pai.getPagingAnchor(), parser, bundleInput);
                    if (nextPai != null) {
                        pai.getAlbums().addAll(nextPai.getAlbums());
                        pai.setPagingAnchor(nextPai.getPagingAnchor());
                        pai.setHasMore(nextPai.isHasMore());
                    } else {
                        pai.setHasMore(false);
                    }
                }
            }
            bundleOutput.putParcelable("albmsnfo", pai);
            resulCode = -1;
        } catch (Throwable exc) {
            Logger.m178e(exc);
        }
        GlobalBus.send(2131624179, new BusEvent(bundleInput, bundleOutput, resulCode));
    }

    private PhotoAlbumsInfo requestPhotoAlbumsInfo(String anchor, JsonPhotoAlbumsInfoParser parser, Bundle data) throws BaseApiException, JSONException {
        GetPhotoAlbumsRequest request;
        boolean forward = data.getBoolean("fwd", true);
        int count = data.getInt("cnt");
        boolean detectTotalCount = data.getBoolean("dtctcnt");
        String fields = data.getString("flds");
        String ownerId = data.getString("ownrid");
        if (data.getInt("ownr") == 0) {
            request = new GetPhotoAlbumsRequest(null, ownerId, null, anchor, forward, count, detectTotalCount);
        } else {
            GetPhotoAlbumsRequest getPhotoAlbumsRequest = new GetPhotoAlbumsRequest(null, null, ownerId, anchor, forward, count, detectTotalCount);
        }
        request.setFields(fields);
        return parser.parse(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request).getResultAsObject());
    }
}
