package ru.ok.java.api.request.image;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class EditPhotoRequest extends BaseRequest {
    private String description;
    private String gid;
    private String pid;

    public EditPhotoRequest(String pid, String gid, String description) {
        this.pid = pid;
        this.gid = gid;
        this.description = description;
    }

    public String getMethodName() {
        return "photos.editPhoto";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.PHOTO_ID, this.pid);
        serializer.add(SerializeParamName.GID, this.gid);
        serializer.add(SerializeParamName.DESCRIPTION, this.description);
    }
}
