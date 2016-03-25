package ru.ok.java.api.request;

import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasUserId = true)
public class GetStatusRequest extends BaseRequest {
    public String getMethodName() {
        return "users.getInfo";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.FIELDS, "current_status").add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
    }
}
