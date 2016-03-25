package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetCustomTracksRequest extends BaseRequestWmf {
    private int count;
    private int start;
    private final long trackId;

    public HttpGetCustomTracksRequest(long trackId, String url) {
        super(url);
        this.start = 0;
        this.count = 100;
        this.trackId = trackId;
    }

    public String getMethodName() {
        return "/custom";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.START, this.start).add(SerializeWmfParamName.COUNT, this.count).add(SerializeWmfParamName.TRACKS_LIST, this.trackId).add(SerializeWmfParamName.CLIENT, "android");
    }
}
