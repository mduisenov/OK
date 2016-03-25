package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetMyTunersRequest extends BaseRequestWmf {
    private final String locale;

    public HttpGetMyTunersRequest(String locale, String url) {
        super(url);
        this.locale = locale;
    }

    public String getMethodName() {
        return "/myTuners";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.CLIENT, "android").add(SerializeParamName.LOCALE, this.locale).add(SerializeWmfParamName.APP_CLIENT, Api.CLIENT_NAME);
    }
}
