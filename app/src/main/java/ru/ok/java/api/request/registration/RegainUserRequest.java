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
public final class RegainUserRequest extends BaseRequest {
    private String lang;
    private String newPassword;
    private String pin;
    private String uidToRegain;
    private String userId;

    public RegainUserRequest(String userId, String uidToRegain, String pin, String newPassword, String lang) {
        this.userId = userId;
        this.lang = lang;
        this.pin = pin;
        this.newPassword = newPassword;
        this.uidToRegain = uidToRegain;
    }

    public String getMethodName() {
        return "register.regainUser";
    }

    protected void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.USER_ID, this.userId).add(SerializeParamName.USER_ID_TO_REGAIN, this.uidToRegain).add(SerializeParamName.PIN, this.pin).add(SerializeParamName.LANG, this.lang);
        if (!TextUtils.isEmpty(this.newPassword)) {
            serializer.add(SerializeParamName.PASSWORD, this.newPassword);
        }
    }
}
