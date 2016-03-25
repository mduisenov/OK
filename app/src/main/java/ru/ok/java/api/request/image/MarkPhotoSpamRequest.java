package ru.ok.java.api.request.image;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class MarkPhotoSpamRequest extends BaseRequest {
    private final PhotoType photoType;
    private final String pid;

    public MarkPhotoSpamRequest(String pid, PhotoType photoType) {
        this.pid = pid;
        this.photoType = photoType;
    }

    public String getMethodName() {
        return "photos.markAsSpam";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.PHOTO_ID, this.pid);
        serializer.add(SerializeParamName.PHOTO_TYPE, this.photoType.getApiParamName());
    }
}
