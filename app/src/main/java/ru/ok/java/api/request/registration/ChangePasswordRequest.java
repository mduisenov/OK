package ru.ok.java.api.request.registration;

import ru.ok.java.api.HttpMethodType;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasSessionKey = false, hasTargetUrl = false, hasUserId = false, httpType = HttpMethodType.POST)
public final class ChangePasswordRequest extends BaseRequest {
    private String newPassword;
    private String oldPassword;

    public ChangePasswordRequest(String oldPassword1, String newPassword) {
        this.oldPassword = oldPassword1;
        this.newPassword = newPassword;
    }

    public String getMethodName() {
        return "users.changePassword";
    }

    protected void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME).add(SerializeParamName.OLD_PW, this.oldPassword).add(SerializeParamName.NEW_PW, this.newPassword);
    }
}
