package ru.ok.java.api.request;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, signType = Scope.SESSION)
public class SettingsGetRequest extends BaseSettingsGetRequest {
    public /* bridge */ /* synthetic */ String getMethodName() {
        return super.getMethodName();
    }

    public /* bridge */ /* synthetic */ void serializeInternal(RequestSerializer x0) {
        super.serializeInternal(x0);
    }

    public SettingsGetRequest(String settingNameWildcard, int versionCode) {
        super(settingNameWildcard, versionCode);
    }

    public SettingsGetRequest(String[] settingNameWildcards, int versionCode) {
        super(settingNameWildcards, versionCode);
    }
}
