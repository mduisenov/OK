package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetRelevantRequest extends BaseRequestWmf {
    private String text;

    public HttpGetRelevantRequest(String text, String url) {
        super(url);
        this.text = text;
    }

    public String getMethodName() {
        return "/relevant";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.CLIENT, "android").add(SerializeWmfParamName.TEXT_Q, this.text);
    }
}
