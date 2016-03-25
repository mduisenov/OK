package ru.ok.java.api.request;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@NoLoginNeeded
@HttpPreamble(signType = Scope.APPLICATION)
public final class SettingsGetRequestNoSession extends BaseSettingsGetRequest {
    public /* bridge */ /* synthetic */ String getMethodName() {
        return super.getMethodName();
    }

    public /* bridge */ /* synthetic */ void serializeInternal(RequestSerializer x0) {
        super.serializeInternal(x0);
    }

    public SettingsGetRequestNoSession(String[] settingNameWildcards, int versionCode) {
        super(settingNameWildcards, versionCode);
    }
}
