package ru.ok.java.api.request.video;

import android.text.TextUtils;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.model.messages.Attachment.AttachmentType;

@HttpPreamble(hasSessionKey = true)
public final class GetVideoUploadUrlRequest extends BaseRequest {
    private final String fileName;
    private final long fileSize;
    private final String groupId;
    private final AttachmentType type;

    public GetVideoUploadUrlRequest(String groupId, String fileName, long fileSize, AttachmentType type) {
        this.groupId = groupId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.type = type;
    }

    public String getMethodName() {
        return "video.getUploadUrl";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        if (!TextUtils.isEmpty(this.groupId)) {
            serializer.add(SerializeParamName.GID, this.groupId);
        }
        serializer.add(SerializeParamName.FILE_NAME, this.fileName).add(SerializeParamName.FILE_SIZE, this.fileSize);
        if (this.type != null) {
            serializer.add(SerializeParamName.ATTACHMENT_TYPE, this.type.getStrValue());
        }
    }
}
