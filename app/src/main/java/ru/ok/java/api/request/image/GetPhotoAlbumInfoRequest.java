package ru.ok.java.api.request.image;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.param.BaseRequestParam;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public final class GetPhotoAlbumInfoRequest extends BaseRequest {
    private final BaseRequestParam aid;
    private final BaseRequestParam fid;
    private String fields;
    private final BaseRequestParam gid;

    public GetPhotoAlbumInfoRequest(BaseRequestParam aid, BaseRequestParam fid, BaseRequestParam gid) {
        this.aid = aid;
        this.fid = fid;
        this.gid = gid;
    }

    public String getMethodName() {
        return "photos.getAlbumInfo";
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        if (this.aid != null) {
            serializer.add(SerializeParamName.ALBUM_ID, this.aid);
        }
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

    public final void setFields(String fields) {
        this.fields = fields;
    }
}
