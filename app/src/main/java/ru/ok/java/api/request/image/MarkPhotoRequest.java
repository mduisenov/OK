package ru.ok.java.api.request.image;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public final class MarkPhotoRequest extends BaseRequest {
    private final int mMark;
    private final String mPhotoId;

    public MarkPhotoRequest(String photoId, int mark) {
        this.mPhotoId = photoId;
        this.mMark = mark;
    }

    public String getMethodName() {
        return "photos.markUserPhoto";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.PHOTOID, this.mPhotoId);
        serializer.add(SerializeParamName.MARK, this.mMark);
    }
}
