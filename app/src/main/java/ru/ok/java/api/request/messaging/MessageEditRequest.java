package ru.ok.java.api.request.messaging;

import ru.ok.java.api.HttpMethodType;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(httpType = HttpMethodType.POST)
public final class MessageEditRequest extends BaseRequest {
    private final String conversationId;
    private final String messageId;
    private final String newText;

    public MessageEditRequest(String conversationId, String messageId, String newText) {
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.newText = newText;
    }

    public String getMethodName() {
        return "messagesV2.edit";
    }

    protected void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.CONVERSATION_ID, this.conversationId).add(SerializeParamName.MESSAGE_ID, this.messageId).add(SerializeParamName.TEXT, this.newText);
    }
}
