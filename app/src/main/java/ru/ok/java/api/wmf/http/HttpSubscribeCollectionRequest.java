package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpSubscribeCollectionRequest extends BaseRequestWmf {
    private long collectionId;

    public HttpSubscribeCollectionRequest(long collectionId, String url) {
        super(url);
        this.collectionId = collectionId;
    }

    public String getMethodName() {
        return "/playlistsAdd";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.PLAYLIST_ID, this.collectionId).add(SerializeWmfParamName.CLIENT, "android");
    }
}
