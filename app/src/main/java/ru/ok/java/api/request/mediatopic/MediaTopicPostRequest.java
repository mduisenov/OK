package ru.ok.java.api.request.mediatopic;

import org.json.JSONObject;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.java.api.HttpMethodType;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasSessionKey = true, httpType = HttpMethodType.POST)
public final class MediaTopicPostRequest extends BaseRequest {
    private final JSONObject attachment;
    private final String groupId;
    private final boolean setStatus;
    private final MediaTopicType type;

    private MediaTopicPostRequest(MediaTopicType type, String groupId, JSONObject attachement, boolean setStatus) {
        this.type = type;
        this.attachment = attachement;
        this.groupId = groupId;
        this.setStatus = setStatus;
    }

    public static MediaTopicPostRequest user(JSONObject attachement, boolean setStatus) {
        return new MediaTopicPostRequest(MediaTopicType.USER, null, attachement, setStatus);
    }

    public static MediaTopicPostRequest groupTheme(String groupId, JSONObject attachement) {
        return new MediaTopicPostRequest(MediaTopicType.GROUP_THEME, groupId, attachement, false);
    }

    public static MediaTopicPostRequest groupThemeSuggested(String groupId, JSONObject attachement) {
        return new MediaTopicPostRequest(MediaTopicType.GROUP_SUGGESTED, groupId, attachement, false);
    }

    public String getMethodName() {
        return "mediatopic.post";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.TYPE, getTypeValue(this.type, this.setStatus));
        if (this.attachment != null) {
            serializer.add(SerializeParamName.ATTACHMENT, this.attachment.toString());
        }
        if (this.groupId != null) {
            serializer.add(SerializeParamName.GID, this.groupId);
        }
    }

    private static String getTypeValue(MediaTopicType type, boolean setStatus) {
        switch (1.$SwitchMap$ru$ok$java$api$request$mediatopic$MediaTopicType[type.ordinal()]) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return "GROUP_THEME";
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return "GROUP_SUGGESTED";
            default:
                return setStatus ? "USER_STATUS" : "USER_NOTE";
        }
    }
}
