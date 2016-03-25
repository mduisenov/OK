package ru.ok.java.api.request.image;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.fields.RequestField;

@HttpPreamble(hasSessionKey = true)
public class GetPhotoAlbumsRequest extends BaseRequest {
    private int count;
    private boolean detectTotalCount;
    private String fid;
    private String fields;
    private boolean forward;
    private String gid;
    private String pagingAnchor;
    private String uid;

    public enum FILEDS implements RequestField {
        ALBUM_ALL("album.*"),
        ALBUM_AID("album.aid"),
        ALBUM_TITLE("album.title"),
        ALBUM_DESCRIPTION("album.description"),
        ALBUM_CREATED("album.created"),
        ALBUM_TYPE("album.type"),
        ALBUM_USER_ID("album.user_id"),
        ALBUM_TYPES("album.types"),
        ALBUM_TYPE_CHANGE_ENABLED("album.type_change_enabled"),
        ALBUM_COMMENTS_COUNT("album.comments_count"),
        ALBUM_PHOTOS_COUNT("album.photos_count"),
        PHOTO_ALL("photo.*"),
        PHOTO_ID("photo.id"),
        PHOTO_PIC_50("photo.pic50x50"),
        PHOTO_PIC_128("photo.pic128x128"),
        PHOTO_PIC_640("photo.pic640x480");
        
        private String name;

        private FILEDS(String name) {
            this.name = name;
        }

        public final String getName() {
            return this.name;
        }
    }

    public GetPhotoAlbumsRequest(String uid, String fid, String gid, String pagingAnchor, boolean forward, int count, boolean detectTotalCount) {
        this.uid = uid;
        this.fid = fid;
        this.gid = gid;
        this.pagingAnchor = pagingAnchor;
        this.forward = forward;
        this.count = count;
        this.detectTotalCount = detectTotalCount;
    }

    public String getMethodName() {
        return "photos.getAlbums";
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
        if (this.pagingAnchor != null) {
            serializer.add(SerializeParamName.PAGING_ANCHOR, this.pagingAnchor);
        }
        if (!this.forward) {
            serializer.add(SerializeParamName.PAGING_DIRECTION, "backward");
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

    public String toString() {
        return "GetPhotoAlbumsRequest{uid='" + this.uid + '\'' + ", fid='" + this.fid + '\'' + ", gid='" + this.gid + '\'' + '}';
    }

    public final void setFields(String fields) {
        this.fields = fields;
    }
}
