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
public final class GetExistingUserBySmsRequest extends BaseRequest {
    private String phone;
    private String pin;

    public GetExistingUserBySmsRequest(String phone, String pin) {
        this.phone = phone;
        this.pin = pin;
    }

    public String getMethodName() {
        return "register.getExistingUserBySMS";
    }

    protected void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.PHONE, this.phone).add(SerializeParamName.PIN, this.pin);
    }
}
