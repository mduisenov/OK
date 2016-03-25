package ru.ok.java.api.request.video;

import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true)
public class VideoUpdateRequest extends BaseRequest {
    private final String description;
    private final String privacy;
    private final String tags;
    private final String title;
    private final Long videoId;

    public VideoUpdateRequest(Long videoId, String title, String description, String tags, String privacy) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.privacy = privacy;
    }

    public String getMethodName() {
        return "video.update";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.VIDEO_ID, this.videoId.longValue());
        if (this.title != null) {
            serializer.add(SerializeParamName.TITLE, this.title);
        }
        if (this.description != null) {
            serializer.add(SerializeParamName.DESCRIPTION, this.description);
        }
        if (this.tags != null) {
            serializer.add(SerializeParamName.TAGS, this.tags);
        }
        if (this.privacy != null) {
            serializer.add(SerializeParamName.PRIVACY, this.privacy);
        }
    }
}
