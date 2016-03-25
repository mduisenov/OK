package ru.ok.java.api.request.registration;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasSessionKey = false, hasTargetUrl = false, hasUserId = false)
public final class GetCountryListRequest extends BaseRequest {
    private String lang;

    public GetCountryListRequest(String lang) {
        this.lang = lang;
    }

    public String getMethodName() {
        return "system.getLocations";
    }

    protected void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME).add(SerializeParamName.LANG, this.lang);
    }
}
