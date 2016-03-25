package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpSetTrackAsStatusRequest extends BaseRequestWmf implements TargetUrlGetter {
    private long trackId;

    public HttpSetTrackAsStatusRequest(long trackId, String url) {
        super(url);
        this.trackId = trackId;
    }

    public String getMethodName() {
        return "/postStatus";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.SONG_ID, this.trackId).add(SerializeWmfParamName.CLIENT, "android");
    }
}
