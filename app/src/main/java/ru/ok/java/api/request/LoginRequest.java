package ru.ok.java.api.request;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@NoLoginNeeded
@HttpPreamble(hasGeolocation = true, signType = Scope.APPLICATION)
public final class LoginRequest extends BaseRequest {
    private String deviceId;
    private String password;
    private String reffered;
    private String username;
    private String verificationToken;
    private String verificationVersion;

    public LoginRequest(String username, String password, String verificationToken, String reffered, String deviceId, String verificationVersion) {
        this.username = username;
        this.password = password;
        this.deviceId = deviceId;
        this.reffered = reffered;
        this.verificationToken = verificationToken;
        this.verificationVersion = verificationVersion;
    }

    public String getMethodName() {
        return "auth.login";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.GEN_TOKEN, Boolean.toString(true).toLowerCase()).add(SerializeParamName.PASSWORD, this.password).add(SerializeParamName.USER_NAME, this.username).add(SerializeParamName.CLIENT, Api.CLIENT_NAME).add(SerializeParamName.REFFERRER, this.reffered).add(SerializeParamName.VERIFICATION_SUPPORTED, true).add(SerializeParamName.VERIFICATION_TOKEN, this.verificationToken).add(SerializeParamName.DEVICEID, this.deviceId).add(SerializeParamName.VERIFICATION_VERSION, this.verificationVersion);
    }
}
