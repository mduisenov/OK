package ru.ok.java.api.request.registration;

import android.text.TextUtils;
import ru.ok.java.api.Scope;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.NoLoginNeeded;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@NoLoginNeeded
@HttpPreamble(signType = Scope.APPLICATION)
public final class UserConfirmationByPhoneRequest extends BaseRequest {
    private String lang;
    private String login;
    private String newPassword;
    private String pin;
    private String userId;

    public UserConfirmationByPhoneRequest(String userId, String login, String pin, String newPassword, String lang) {
        this.userId = userId;
        this.lang = lang;
        this.pin = pin;
        this.newPassword = newPassword;
        this.login = login;
    }

    public String getMethodName() {
        return "register.confirmPhoneRegistration";
    }

    protected void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.USER_ID, this.userId).add(SerializeParamName.PIN, this.pin).add(SerializeParamName.LOGIN, this.login).add(SerializeParamName.LANG, this.lang);
        if (!TextUtils.isEmpty(this.newPassword)) {
            serializer.add(SerializeParamName.PASSWORD, this.newPassword);
        }
    }
}
