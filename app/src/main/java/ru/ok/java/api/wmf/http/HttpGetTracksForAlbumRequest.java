package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetTracksForAlbumRequest extends BaseRequestWmf {
    private long albumId;

    public HttpGetTracksForAlbumRequest(long albumId, String url) {
        super(url);
        this.albumId = albumId;
    }

    public String getMethodName() {
        return "/album";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.ALBUM_ID, this.albumId).add(SerializeWmfParamName.CLIENT, "android");
    }
}
