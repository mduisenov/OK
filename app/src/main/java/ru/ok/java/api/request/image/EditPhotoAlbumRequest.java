package ru.ok.java.api.request.image;

import android.text.TextUtils;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class EditPhotoAlbumRequest extends BaseRequest {
    final String accessType;
    final String aid;
    final String gid;
    final String title;

    public EditPhotoAlbumRequest(String aid, String title, String accessType, String gid) {
        this.aid = aid;
        this.title = title;
        this.accessType = accessType;
        this.gid = gid;
    }

    public String getMethodName() {
        return "photos.editAlbum";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.ALBUM_ID, this.aid);
        if (!TextUtils.isEmpty(this.title)) {
            serializer.add(SerializeParamName.TITLE, this.title);
        }
        if (!TextUtils.isEmpty(this.accessType)) {
            serializer.add(SerializeParamName.TYPE, this.accessType);
        }
        if (!TextUtils.isEmpty(this.gid)) {
            serializer.add(SerializeParamName.GID, this.gid);
        }
    }
}
