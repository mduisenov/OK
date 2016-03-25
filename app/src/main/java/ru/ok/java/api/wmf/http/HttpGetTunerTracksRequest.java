package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetTunerTracksRequest extends BaseRequestWmf {
    private String data;

    public HttpGetTunerTracksRequest(String url, String data) {
        super(url);
        this.data = data;
    }

    public String getMethodName() {
        return "/myRadio";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.CLIENT, "android").add(SerializeWmfParamName.TUNER, this.data).add(SerializeWmfParamName.APP_CLIENT, Api.CLIENT_NAME);
    }
}
