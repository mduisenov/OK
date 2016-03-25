package ru.ok.java.api.request;

import java.util.Collections;
import java.util.Map;
import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;

@HttpPreamble(hasSessionKey = false, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class GetUrlsRequester extends BaseRequest implements TargetUrlGetter {
    private Map<String, String> mapParams;
    private final String methodName;
    private final String url;

    public GetUrlsRequester(String baseWebUrl, String methodName, Map<String, String> mapParams) {
        this.url = baseWebUrl;
        this.methodName = methodName;
        if (mapParams == null) {
            mapParams = Collections.emptyMap();
        }
        this.mapParams = mapParams;
    }

    public GetUrlsRequester(String baseWebUrl, String methodName) {
        this.url = baseWebUrl;
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void serializeInternal(RequestSerializer<?> requestSerializer) {
    }

    public String getTargetUrl() {
        return this.url;
    }
}
