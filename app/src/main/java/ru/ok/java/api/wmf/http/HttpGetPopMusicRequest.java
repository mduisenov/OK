package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetPopMusicRequest extends BaseRequestWmf {
    private int count;
    private int start;

    public HttpGetPopMusicRequest(int start, int count, String url) {
        super(url);
        this.start = -1;
        this.count = -1;
        this.start = start;
        this.count = count;
    }

    public String getMethodName() {
        return "/pop";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        if (this.start < 0 || this.count < 0) {
            serializer.add(SerializeWmfParamName.CLIENT, "android");
        } else {
            serializer.add(SerializeWmfParamName.START, this.start).add(SerializeWmfParamName.COUNT, this.count).add(SerializeWmfParamName.CLIENT, "android");
        }
    }

    public String toString() {
        return "HttpGetPopMusicRequest{start=" + this.start + ", count=" + this.count + '}';
    }
}
