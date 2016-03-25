package ru.ok.java.api.request.image;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class SetGroupMainPhotoRequest extends BaseRequest {
    private final String gid;
    private final String pid;

    public SetGroupMainPhotoRequest(String gid, String pid) {
        this.gid = gid;
        this.pid = pid;
    }

    public String getMethodName() {
        return "group.setMainPhoto";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.GROUP_ID, this.gid);
        serializer.add(SerializeParamName.PHOTO_ID, this.pid);
    }
}
