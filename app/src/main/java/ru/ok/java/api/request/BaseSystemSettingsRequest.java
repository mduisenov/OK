package ru.ok.java.api.request;

import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;

@Deprecated
abstract class BaseSystemSettingsRequest extends BaseRequest {
    BaseSystemSettingsRequest() {
    }

    public String getMethodName() {
        return "settings.get";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.FIELDS, "GLOBAL,APP");
        serializer.add(SerializeParamName.SET_ONLINE, "false");
    }
}
