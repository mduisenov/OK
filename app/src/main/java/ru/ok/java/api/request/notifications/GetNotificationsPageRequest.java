package ru.ok.java.api.request.notifications;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasTargetUrl = true, hasUserId = true)
public final class GetNotificationsPageRequest extends BaseRequest implements TargetUrlGetter {
    private String url;

    public GetNotificationsPageRequest(String baseWebUrl) {
        this.url = baseWebUrl;
    }

    public String getMethodName() {
        return "api/notifications";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME).add(SerializeParamName.CID, Api.CID_VALUE);
    }

    public String getTargetUrl() {
        return this.url;
    }
}
