package ru.ok.java.api.request.image;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public final class GetPhotoInfoRequest extends BaseRequest {
    private final String fid;
    private String fields;
    private final String gid;
    private final String id;

    public GetPhotoInfoRequest(String id, String fid, String gid) {
        this.id = id;
        this.fid = fid;
        this.gid = gid;
    }

    public String getMethodName() {
        return "photos.getPhotoInfo";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.PHOTO_ID, this.id);
        if (this.fid != null) {
            serializer.add(SerializeParamName.FRIEND_ID, this.fid);
        }
        if (this.gid != null) {
            serializer.add(SerializeParamName.GID, this.gid);
        }
        if (this.fields != null) {
            serializer.add(SerializeParamName.FIELDS, this.fields);
        }
    }

    public String getUserIdSupplier() {
        return getMethodName() + ".user_ids";
    }

    public final void setFields(String fields) {
        this.fields = fields;
    }

    public String toString() {
        return "GetPhotoInfoRequest{id='" + this.id + '\'' + ", fid='" + this.fid + '\'' + ", gid='" + this.gid + '\'' + '}';
    }
}
