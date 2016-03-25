package ru.ok.java.api.request.image;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class GetTagsRequest extends BaseRequest {
    private final String photoId;

    public GetTagsRequest(String photoId) {
        this.photoId = photoId;
    }

    public String getMethodName() {
        return "photos.getTags";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.PHOTO_ID, this.photoId);
    }
}
