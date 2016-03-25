package ru.ok.java.api.request;

import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(useHttps = true)
public class LogoutAllRequest extends BaseRequest {
    private final String password;

    public LogoutAllRequest(String password) {
        this.password = password;
    }

    public String getMethodName() {
        return "auth.logoutAll";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.PASSWORD, this.password);
    }
}
