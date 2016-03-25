package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetSearchAlbumsRequest extends BaseRequestWmf {
    private int count;
    private int start;
    private String text;

    public HttpGetSearchAlbumsRequest(String text, int start, int count, String url) {
        super(url);
        this.start = -1;
        this.count = -1;
        this.start = start;
        this.count = count;
        this.text = text;
    }

    public String getMethodName() {
        return "/albums";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        if (this.start < 0 || this.count < 0) {
            serializer.add(SerializeWmfParamName.CLIENT, "android").add(SerializeWmfParamName.TEXT_Q, this.text);
        } else {
            serializer.add(SerializeWmfParamName.START, this.start).add(SerializeWmfParamName.COUNT, this.count).add(SerializeWmfParamName.CLIENT, "android").add(SerializeWmfParamName.TEXT_Q, this.text);
        }
    }
}
