package ru.ok.java.api.request.video;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class VideoDeleteRequest extends BaseRequest {
    private final Long videoId;

    public VideoDeleteRequest(Long videoId) {
        this.videoId = videoId;
    }

    public String getMethodName() {
        return "video.delete";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.VIDEO_ID, this.videoId.longValue());
    }
}
