package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetMyMusicCollectionsRequest extends BaseRequestWmf {
    public HttpGetMyMusicCollectionsRequest(String url) {
        super(url);
    }

    public String getMethodName() {
        return "/playlistsGet";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.CLIENT, "android");
    }
}
