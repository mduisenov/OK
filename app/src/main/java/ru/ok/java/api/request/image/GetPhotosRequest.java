package ru.ok.java.api.request.image;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public final class GetPhotosRequest extends BaseRequest {
    private String aid;
    private String anchor;
    private int count;
    private boolean detectTotalCount;
    private String fid;
    private String fields;
    private boolean forward;
    private String gid;
    private String uid;

    public GetPhotosRequest(String uid, String fid, String gid, String albumId, String anchor, boolean forward, int count, boolean detectTotalCount) {
        this.uid = uid;
        this.fid = fid;
        this.gid = gid;
        this.aid = albumId;
        this.anchor = anchor;
        this.forward = forward;
        this.count = count;
        this.detectTotalCount = detectTotalCount;
    }

    public String getMethodName() {
        return "photos.getPhotos";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        if (this.uid != null) {
            serializer.add(SerializeParamName.USER_ID, this.uid);
        }
        if (this.fid != null) {
            serializer.add(SerializeParamName.FRIEND_ID, this.fid);
        }
        if (this.gid != null) {
            serializer.add(SerializeParamName.GID, this.gid);
        }
        if (this.aid != null && this.aid.length() > 0) {
            serializer.add(SerializeParamName.ALBUM_ID, this.aid);
        }
        if (this.anchor != null) {
            serializer.add(SerializeParamName.ANCHOR, this.anchor);
        }
        if (!this.forward) {
            serializer.add(SerializeParamName.DIRECTION, PagingDirection.BACKWARD.name());
        }
        if (this.count > 0) {
            serializer.add(SerializeParamName.COUNT, Math.min(this.count, 100));
        }
        if (this.detectTotalCount) {
            serializer.add(SerializeParamName.DETECT_TOTAL_COUNT, Boolean.TRUE.toString());
        }
        if (this.fields != null) {
            serializer.add(SerializeParamName.FIELDS, this.fields);
        }
    }

    public final void setFields(String fields) {
        this.fields = fields;
    }
}
