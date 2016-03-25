package ru.ok.java.api.request.registration;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.NoLoginNeeded;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@NoLoginNeeded
@HttpPreamble(signType = Scope.APPLICATION)
public final class RegisterWithLibVerifyRequest extends BaseRequest {
    private String lang;
    private String phone;
    private String sessionId;
    private String token;

    public RegisterWithLibVerifyRequest(String token, String sessionId, String phone, String lang) {
        this.token = token;
        this.sessionId = sessionId;
        this.phone = phone;
        this.lang = lang;
    }

    public String getMethodName() {
        return "register.registerWithLibVerify";
    }

    protected void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME).add(SerializeParamName.TOKEN, this.token).add(SerializeParamName.SESSION_ID, this.sessionId).add(SerializeParamName.PHONE, this.phone).add(SerializeParamName.LANG, this.lang);
    }
}
