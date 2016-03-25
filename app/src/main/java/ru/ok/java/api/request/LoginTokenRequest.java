package ru.ok.java.api.request;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@NoLoginNeeded
@HttpPreamble(hasGeolocation = true, signType = Scope.APPLICATION)
public class LoginTokenRequest extends BaseRequest {
    private String deviceId;
    private String reffered;
    private boolean setOnline;
    private String token;
    private String verificationToken;
    private String verificationVersion;

    public LoginTokenRequest(String token, String verificationToken, String deviceId, String reffered, boolean setOnline, String verificationVersion) {
        this.token = token;
        this.deviceId = deviceId;
        this.reffered = reffered;
        this.verificationToken = verificationToken;
        this.setOnline = setOnline;
        this.verificationVersion = verificationVersion;
    }

    public String getMethodName() {
        return "auth.loginByToken";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.TOKEN, this.token).add(SerializeParamName.REFFERRER, this.reffered).add(SerializeParamName.VERIFICATION_SUPPORTED, true).add(SerializeParamName.VERIFICATION_TOKEN, this.verificationToken).add(SerializeParamName.CLIENT, Api.CLIENT_NAME).add(SerializeParamName.DEVICEID, this.deviceId).add(SerializeParamName.VERIFICATION_VERSION, this.verificationVersion);
    }

    public boolean isMakeUserOnline() {
        return this.setOnline;
    }
}
