package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpGetPlayStatusMusicRequest extends BaseRequestWmf {
    private String ids;
    private String uid;

    public HttpGetPlayStatusMusicRequest(String ids, String uid, String url) {
        super(url);
        this.ids = ids;
        this.uid = uid;
    }

    public String getMethodName() {
        return "/playlistMT";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        if (this.uid == null || this.uid.length() == 0) {
            serializer.add(SerializeWmfParamName.TRACKS_LIST, this.ids).add(SerializeWmfParamName.CLIENT, "android");
        } else {
            serializer.add(SerializeWmfParamName.USER_ID, this.uid).add(SerializeWmfParamName.TRACKS_LIST, this.ids).add(SerializeWmfParamName.CLIENT, "android");
        }
    }
}
