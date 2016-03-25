package ru.ok.java.api.wmf.http;

import ru.ok.java.api.Scope;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.model.wmf.Track;

@HttpPreamble(hasSessionKey = true, hasTargetUrl = true, hasUserId = false, signType = Scope.NONE)
public class HttpAddTrackRequest extends BaseRequestWmf {
    private Track[] tracksId;

    public HttpAddTrackRequest(Track[] tracksId, String url) {
        super(url);
        this.tracksId = new Track[0];
        this.tracksId = tracksId;
    }

    private Long[] getTracksIdArray(Track[] tracksIdArray) {
        Long[] tracksId = new Long[tracksIdArray.length];
        for (int i = 0; i < tracksIdArray.length; i++) {
            tracksId[i] = Long.valueOf(tracksIdArray[i].id);
        }
        return tracksId;
    }

    public String getMethodName() {
        return "/like";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeWmfParamName.TRACK_ID, getTracksIdArray(this.tracksId));
        serializer.add(SerializeWmfParamName.CLIENT, "android");
    }
}
