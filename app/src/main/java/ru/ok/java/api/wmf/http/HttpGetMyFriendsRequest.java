package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetMyFriendsRequest extends BaseRequestWmf {
    private int count;
    private int start;

    public HttpGetMyFriendsRequest(int start, int count, String url) {
        super(url);
        this.start = start;
        this.count = count;
    }

    public String getMethodName() {
        return "/friends";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.START, this.start).add(SerializeWmfParamName.COUNT, this.count).add(SerializeWmfParamName.CLIENT, "android").add(SerializeWmfParamName.APP_CLIENT, Api.CLIENT_NAME);
    }
}
