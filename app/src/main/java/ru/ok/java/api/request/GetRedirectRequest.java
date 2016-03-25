package ru.ok.java.api.request;

import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasSessionKey = false, hasTargetUrl = true, hasUserId = true)
public final class GetRedirectRequest extends BaseRequest implements TargetUrlGetter {
    private String encodedRedirectToUrl;
    private String info;
    private String url;

    public GetRedirectRequest(String baseWebUrl, String encodedRedirectToUrl, String info) {
        this.encodedRedirectToUrl = encodedRedirectToUrl;
        this.url = baseWebUrl;
        this.info = info;
    }

    public String getMethodName() {
        return "api/goto";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.URL, this.encodedRedirectToUrl);
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
        serializer.add(SerializeParamName.CID, Api.CID_VALUE);
        if (this.info != null) {
            serializer.add(SerializeParamName.UDATA, this.info);
        }
    }

    public String getTargetUrl() {
        return this.url;
    }
}
