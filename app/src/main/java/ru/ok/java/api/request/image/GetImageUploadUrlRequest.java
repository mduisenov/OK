package ru.ok.java.api.request.image;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public final class GetImageUploadUrlRequest extends BaseRequest {
    private final String aid;
    private final int count;
    private final String gid;

    public GetImageUploadUrlRequest(String aid, String gid, int count) {
        this.aid = aid;
        this.gid = gid;
        this.count = count;
    }

    public String getMethodName() {
        return "photosV2.getUploadUrl";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        if (this.gid != null) {
            serializer.add(SerializeParamName.GID, this.gid);
        }
        if (this.aid != null) {
            serializer.add(SerializeParamName.ALBUM_ID, this.aid);
        }
        serializer.add(SerializeParamName.COUNT, this.count);
    }
}
