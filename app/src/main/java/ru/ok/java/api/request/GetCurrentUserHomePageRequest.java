package ru.ok.java.api.request;

import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasTargetUrl = true, hasUserId = true)
public final class GetCurrentUserHomePageRequest extends BaseRequest implements TargetUrlGetter {
    private String url;

    public GetCurrentUserHomePageRequest(String baseWebUrl) {
        this.url = baseWebUrl;
    }

    public String getMethodName() {
        return "api/user";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
    }

    public String getTargetUrl() {
        return this.url;
    }
}
