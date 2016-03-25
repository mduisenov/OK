package ru.ok.java.api.request;

import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;

@Deprecated
@HttpPreamble(hasTargetUrl = true, hasUserId = true)
public class PaymentRequest extends BaseRequest implements TargetUrlGetter {
    private String uid;
    private String url;

    public PaymentRequest(String baseWebUrl, String uid) {
        this.url = baseWebUrl;
        this.uid = uid;
    }

    public String getMethodName() {
        return "api/show-payment";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.USER_ID, this.uid);
        serializer.add(SerializeParamName.SRV_ID, 22);
    }

    public String getTargetUrl() {
        return this.url;
    }
}
