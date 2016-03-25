package ru.ok.java.api.request.registration;

import ru.ok.java.api.HttpMethodType;
import ru.ok.java.api.Scope;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.NoLoginNeeded;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@NoLoginNeeded
@HttpPreamble(httpType = HttpMethodType.POST, signType = Scope.APPLICATION)
public final class RecoverUserBySmsRequest extends BaseRequest {
    private String password;
    private String pin;
    private String uid;

    public RecoverUserBySmsRequest(String uid, String pin, String password) {
        this.password = password;
        this.pin = pin;
        this.uid = uid;
    }

    public String getMethodName() {
        return "register.recoverUserBySMS";
    }

    protected void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.USER_ID, this.uid).add(SerializeParamName.PASSWORD, this.password).add(SerializeParamName.PIN, this.pin);
    }
}
