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
public final class RegisterByPhoneRequest extends BaseRequest {
    private String lang;
    private String phone;

    public RegisterByPhoneRequest(String phone, String lang) {
        this.phone = phone;
        this.lang = lang;
    }

    public String getMethodName() {
        return "register.registerByPhone";
    }

    protected void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.PHONE, this.phone).add(SerializeParamName.LANG, this.lang);
    }
}
