package ru.ok.java.api.request.messaging.send;

import android.text.TextUtils;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.HttpMethodType;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(httpType = HttpMethodType.POST)
public final class MessageSendRequest extends BaseRequest {
    private final List<Map<String, String>> attachments;
    private final String conversationId;
    private final String replyToMessageId;
    private final String text;
    private final String uuid;

    public MessageSendRequest(String conversationId, String message, List<Map<String, String>> attachments, String replyToMessageId, String uuid) {
        this.attachments = attachments;
        this.conversationId = conversationId;
        this.replyToMessageId = replyToMessageId;
        this.uuid = uuid;
        this.text = message;
    }

    public String getMethodName() {
        return "messagesV2.send";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.CONVERSATION_ID, this.conversationId);
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
        if (!(this.attachments == null || this.attachments.isEmpty())) {
            serializer.add(SerializeParamName.ATTACHMENTS, getAttachmentsAsJson().toString());
        }
        if (!TextUtils.isEmpty(this.text)) {
            serializer.add(SerializeParamName.TEXT, this.text);
        }
        if (this.replyToMessageId != null && this.replyToMessageId.length() > 0) {
            serializer.add(SerializeParamName.REPLY_TO_MESSAGE_ID, this.replyToMessageId);
        }
        if (this.uuid != null) {
            serializer.add(SerializeParamName.UUID, this.uuid);
        }
    }

    private JSONObject getAttachmentsAsJson() {
        JSONObject result = new JSONObject();
        try {
            JSONArray attaches = new JSONArray();
            for (Map<String, String> attachment : this.attachments) {
                JSONObject attach = new JSONObject();
                for (Entry<String, String> entry : attachment.entrySet()) {
                    attach.put((String) entry.getKey(), entry.getValue());
                }
                attaches.put(attach);
            }
            result.put("attachments", attaches);
        } catch (Throwable exc) {
            Logger.m178e(exc);
        }
        return result;
    }
}
