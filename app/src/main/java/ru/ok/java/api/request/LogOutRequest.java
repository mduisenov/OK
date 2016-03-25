package ru.ok.java.api.request;

import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.utils.Constants.Api;

@NoLoginNeeded
public final class LogOutRequest extends BaseRequest {
    public String getMethodName() {
        return "auth.expireSession";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
    }
}
