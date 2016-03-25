package ru.ok.java.api.request.image;

import android.text.TextUtils;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class DeletePhotoAlbumRequest extends BaseRequest {
    private String aid;
    private String gid;

    public DeletePhotoAlbumRequest(String aid, String gid) {
        this.aid = aid;
        this.gid = gid;
    }

    public String getMethodName() {
        return "photos.deleteAlbum";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.ALBUM_ID, this.aid);
        if (!TextUtils.isEmpty(this.gid)) {
            serializer.add(SerializeParamName.GID, this.gid);
        }
    }
}
