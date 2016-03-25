package ru.ok.java.api.request.presents;

import android.support.annotation.NonNull;
import ru.ok.java.api.Scope;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.serializer.http.TargetUrlGetter;

@HttpPreamble(hasSessionKey = false, hasTargetUrl = true, signType = Scope.NONE)
public final class PresentPaymentRequest extends BaseRequest implements TargetUrlGetter {
    private final String completeLink;
    private final int presentCost;
    private final String url;

    public PresentPaymentRequest(@NonNull String url, @NonNull String completeLink, int presentCost) {
        this.url = url;
        this.completeLink = completeLink;
        this.presentCost = presentCost;
    }

    public String getMethodName() {
        return String.format("paymentforward/present/price/%d", new Object[]{Integer.valueOf(this.presentCost)});
    }

    protected void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.ST_RTU, "/" + this.completeLink);
    }

    public String getTargetUrl() {
        return this.url;
    }
}
