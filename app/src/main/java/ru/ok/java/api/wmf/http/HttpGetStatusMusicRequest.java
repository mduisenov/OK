package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetStatusMusicRequest extends BaseRequestWmf {
    private int count;
    private int start;
    private long trackId;

    public HttpGetStatusMusicRequest(int start, int count, long trackId, String url) {
        super(url);
        this.start = -1;
        this.count = -1;
        this.start = start;
        this.count = count;
        this.trackId = trackId;
    }

    public String getMethodName() {
        return "/statuspl";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        if (this.start < 0 || this.count < 0) {
            serializer.add(SerializeWmfParamName.TRACK_ID, this.trackId).add(SerializeWmfParamName.CLIENT, "android");
        } else {
            serializer.add(SerializeWmfParamName.START, this.start).add(SerializeWmfParamName.COUNT, this.count).add(SerializeWmfParamName.TRACK_ID, this.trackId).add(SerializeWmfParamName.CLIENT, "android");
        }
    }
}
