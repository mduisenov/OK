package ru.ok.java.api.request.image;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class LikePhotoRequest extends BaseRequest {
    private final String gid;
    private final String pid;

    public LikePhotoRequest(String pid, String gid) {
        this.pid = pid;
        this.gid = gid;
    }

    public String getMethodName() {
        return "photos.addPhotoLike";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.PHOTO_ID, this.pid);
        if (this.gid != null) {
            serializer.add(SerializeParamName.GID, this.gid);
        }
    }
}
