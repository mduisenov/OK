package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetCollectionInfoRequest extends BaseRequestWmf {
    private long collectionId;

    public HttpGetCollectionInfoRequest(long collectionId, String url) {
        super(url);
        this.collectionId = collectionId;
    }

    public String getMethodName() {
        return "/collection";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.START, 0).add(SerializeWmfParamName.COUNT, 100).add(SerializeWmfParamName.POP_COLLECTION_ID, this.collectionId).add(SerializeWmfParamName.CLIENT, "android");
    }
}
