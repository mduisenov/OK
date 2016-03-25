package ru.ok.java.api.request.registration;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.NoLoginNeeded;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@NoLoginNeeded
@HttpPreamble(signType = Scope.APPLICATION)
public final class CheckPhoneRequest extends BaseRequest {
    private String lang;
    private String pin;
    private String userId;

    public CheckPhoneRequest(String userId, String pin, String lang) {
        this.userId = userId;
        this.lang = lang;
        this.pin = pin;
    }

    public String getMethodName() {
        return "register.registerCheckPhone";
    }

    protected void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.USER_ID, this.userId).add(SerializeParamName.PIN, this.pin).add(SerializeParamName.LANG, this.lang);
    }
}
