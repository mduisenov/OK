package ru.ok.java.api.request.registration;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.NoLoginNeeded;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@NoLoginNeeded
@HttpPreamble(signType = Scope.APPLICATION)
public final class ProfileFieldsFlagsRequest extends BaseRequest {
    private int packageVersionCode;

    public ProfileFieldsFlagsRequest(int packageVersionCode) {
        this.packageVersionCode = packageVersionCode;
    }

    public String getMethodName() {
        return "settings.get";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.KEYS, "users.setProfileData.isBackButtonDisabled,users.setProfileData.birthdayRequired,users.setProfileData.firstLastNameRequired,registration.avatar.visible,registration.avatar.separate").add(SerializeParamName.APP_VERSION, this.packageVersionCode);
    }
}
