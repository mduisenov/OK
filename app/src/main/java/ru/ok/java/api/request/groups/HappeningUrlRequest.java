package ru.ok.java.api.request.groups;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasTargetUrl = true, hasUserId = true)
public final class HappeningUrlRequest extends BaseRequest implements TargetUrlGetter {
    private String happeningId;
    private String url;

    public HappeningUrlRequest(String baseWebUrl, String happeningId) {
        this.url = baseWebUrl;
        this.happeningId = happeningId;
    }

    public String getMethodName() {
        return "api/happening";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.HAPPENING_ID, this.happeningId).add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
    }

    public String getTargetUrl() {
        return this.url;
    }
}
