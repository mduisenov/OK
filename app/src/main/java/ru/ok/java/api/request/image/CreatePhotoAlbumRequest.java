package ru.ok.java.api.request.image;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public final class CreatePhotoAlbumRequest extends BaseRequest {
    final String accessType;
    final String gid;
    final String title;

    public CreatePhotoAlbumRequest(String title, String accessType, String gid) {
        this.title = title;
        this.accessType = accessType;
        this.gid = gid;
    }

    public String getMethodName() {
        return "photos.createAlbum";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        if (this.title != null) {
            serializer.add(SerializeParamName.TITLE, this.title);
        }
        if (this.accessType != null) {
            serializer.add(SerializeParamName.TYPE, this.accessType);
        }
        if (this.gid != null) {
            serializer.add(SerializeParamName.GID, this.gid);
        }
    }
}
