package ru.ok.java.api.request.users;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasSessionKey = false, hasTargetUrl = false, hasUserId = false)
public final class GetCurrentUserInfoRequest extends BaseRequest {
    private final String fields;

    public GetCurrentUserInfoRequest(String fields) {
        this.fields = fields;
    }

    public String getMethodName() {
        return "users.getCurrentUser";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        if (this.fields != null) {
            serializer.add(SerializeParamName.FIELDS, this.fields);
        }
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
    }
}
