package ru.ok.java.api.request.discussions;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasSessionKey = false, hasTargetUrl = true, hasUserId = true)
public class GetDiscussionRequest extends BaseRequest implements TargetUrlGetter {
    private String url;

    public GetDiscussionRequest(String baseWebUrl) {
        this.url = baseWebUrl;
    }

    public String getMethodName() {
        return "api/discussions";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
        serializer.add(SerializeParamName.CID, Api.CID_VALUE);
    }

    public String getTargetUrl() {
        return this.url;
    }
}
