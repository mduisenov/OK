package ru.ok.java.api.request.image;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class GetPhotoInfosByIdsRequest extends BaseRequest {
    private final String aid;
    private final String fid;
    private final String fields;
    private final String gid;
    private final String[] pids;
    private final String uid;

    @NonNull
    public static List<GetPhotoInfosByIdsRequest> create(String uid, String fid, String gid, String aid, @Nullable String[] pids, @NonNull String fields) {
        if (pids == null || pids.length == 0) {
            return new ArrayList();
        }
        return createChunkRequests(uid, fid, gid, aid, pids, fields);
    }

    @NonNull
    private static List<GetPhotoInfosByIdsRequest> createChunkRequests(String uid, String fid, String gid, String aid, @NonNull String[] pids, @NonNull String fields) {
        int requestCount = (int) Math.ceil(((double) pids.length) / 100.0d);
        List<GetPhotoInfosByIdsRequest> requests = new ArrayList(requestCount);
        for (int i = 0; i < requestCount; i++) {
            int start = i * 100;
            int len = Math.min(pids.length - start, 100);
            String[] chunkPids = new String[len];
            System.arraycopy(pids, start, chunkPids, 0, len);
            requests.add(new GetPhotoInfosByIdsRequest(uid, fid, gid, aid, chunkPids, fields));
        }
        return requests;
    }

    private GetPhotoInfosByIdsRequest(String uid, String fid, String gid, String aid, @NonNull String[] pids, @NonNull String fields) {
        this.uid = uid;
        this.fid = fid;
        this.gid = gid;
        this.aid = aid;
        this.pids = pids;
        this.fields = fields;
    }

    public String getMethodName() {
        return "photos.getInfo";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        if (!TextUtils.isEmpty(this.uid)) {
            serializer.add(SerializeParamName.USER_ID, this.uid);
        }
        if (!TextUtils.isEmpty(this.fid)) {
            serializer.add(SerializeParamName.FRIEND_ID, this.fid);
        }
        if (!TextUtils.isEmpty(this.gid)) {
            serializer.add(SerializeParamName.GID, this.gid);
        }
        if (!TextUtils.isEmpty(this.aid)) {
            serializer.add(SerializeParamName.ALBUM_ID, this.aid);
        }
        serializer.add(SerializeParamName.PHOTO_IDS, TextUtils.join(",", this.pids));
        serializer.add(SerializeParamName.FIELDS, this.fields);
    }
}
