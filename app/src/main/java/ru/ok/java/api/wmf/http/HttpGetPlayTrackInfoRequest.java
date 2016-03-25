package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = true, signType = Scope.NONE)
public class HttpGetPlayTrackInfoRequest extends BaseRequestWmf {
    private long trackId;

    public HttpGetPlayTrackInfoRequest(long trackId, String url) {
        super(url);
        this.trackId = trackId;
    }

    public String getMethodName() {
        return "/play";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.TRACK_ID, this.trackId).add(SerializeWmfParamName.CLIENT, "android");
    }
}
