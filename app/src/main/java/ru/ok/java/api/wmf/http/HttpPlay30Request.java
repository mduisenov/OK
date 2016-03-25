package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpPlay30Request extends BaseRequestWmf {
    private long trackId;

    public HttpPlay30Request(long trackId, String url) {
        super(url);
        this.trackId = trackId;
    }

    public String getMethodName() {
        return "/play30";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.TRACK_ID, Long.toString(this.trackId)).add(SerializeWmfParamName.CLIENT, "android");
    }
}
