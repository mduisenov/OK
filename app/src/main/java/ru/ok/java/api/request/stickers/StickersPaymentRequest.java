package ru.ok.java.api.request.stickers;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;

@HttpPreamble(hasTargetUrl = true, hasUserId = true)
public final class StickersPaymentRequest extends BaseRequest implements TargetUrlGetter {
    private final String url;

    public StickersPaymentRequest(String baseWebUrl) {
        this.url = baseWebUrl;
    }

    public String getMethodName() {
        return "api/show-payment";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.SRV_ID, 19);
    }

    public String getTargetUrl() {
        return this.url;
    }
}
