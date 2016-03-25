package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetUserMusicCollectionsRequest extends BaseRequestWmf {
    private String userId;

    public HttpGetUserMusicCollectionsRequest(String userId, String url) {
        super(url);
        this.userId = userId;
    }

    public String getMethodName() {
        return "/playlistsGet";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.CLIENT, "android").add(SerializeWmfParamName.USER_ID, this.userId);
    }
}
