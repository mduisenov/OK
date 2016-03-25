package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetTracksForPlayListRequest extends BaseRequestWmf {
    private long collectionId;
    private int count;
    private int start;

    public HttpGetTracksForPlayListRequest(long collectionId, int start, int count, String url) {
        super(url);
        this.start = -1;
        this.count = -1;
        this.collectionId = collectionId;
        this.start = start;
        this.count = count;
    }

    public String getMethodName() {
        return "/my";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.START, this.start).add(SerializeWmfParamName.COUNT, this.count).add(SerializeWmfParamName.PLAYLIST_ID, this.collectionId).add(SerializeWmfParamName.CLIENT, "android");
    }
}
