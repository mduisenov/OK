package ru.ok.java.api.request;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@Deprecated
@HttpPreamble(hasSessionKey = true, signType = Scope.SESSION)
public final class SystemSettingsRequest extends BaseSystemSettingsRequest {
    public /* bridge */ /* synthetic */ String getMethodName() {
        return super.getMethodName();
    }

    public /* bridge */ /* synthetic */ void serializeInternal(RequestSerializer x0) {
        super.serializeInternal(x0);
    }
}
