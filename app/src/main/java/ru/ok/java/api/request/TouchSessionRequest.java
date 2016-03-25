package ru.ok.java.api.request;

import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class TouchSessionRequest extends BaseRequest {
    private final boolean online;

    public TouchSessionRequest(boolean online) {
        this.online = online;
    }

    public String getMethodName() {
        return "auth.touchSession";
    }

    public boolean isMakeUserOnline() {
        return this.online;
    }

    public void serializeInternal(RequestSerializer<?> requestSerializer) throws SerializeException {
    }
}
