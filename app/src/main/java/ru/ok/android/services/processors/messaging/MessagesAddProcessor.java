package ru.ok.android.services.processors.messaging;

import android.os.Bundle;
import android.text.TextUtils;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.emoji.smiles.SmileTextProcessor;
import ru.ok.android.model.cache.ram.MessageModel;
import ru.ok.android.model.cache.ram.MessageModel.Builder;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.proto.MessagesProto.Attach;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.proto.MessagesProto.Message.Capabilities;
import ru.ok.android.proto.MessagesProto.Message.ReplyTo;
import ru.ok.android.proto.MessagesProto.Message.Status;
import ru.ok.android.proto.MessagesProto.Message.Type;
import ru.ok.android.proto.ProtoProxy;
import ru.ok.android.services.messages.MessagesService;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.utils.Logger;
import ru.ok.model.messages.Attachment;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase.RepliedTo;

public final class MessagesAddProcessor {
    public static void addMessage(BusEvent event) {
        try {
            Bundle data = event.bundleInput;
            String conversationId = data.getString("CONVERSATION_ID");
            if (TextUtils.isEmpty(conversationId)) {
                Logger.m184w("Trying to add message with empty conversationId");
                GlobalBus.send(2131624197, new BusEvent(event.bundleInput, null, -2));
                return;
            }
            Type type;
            int intValue;
            String message = data.getString("TEXT");
            if (message != null) {
                message = message.trim();
            }
            Attachment[] attachments = (Attachment[]) data.getParcelableArray("ATTACHMENTS");
            if (Logger.isLoggingEnable()) {
                Logger.m173d("Adding message with text \"%s\" and %d attachments", message, Integer.valueOf(attachments == null ? 0 : attachments.length));
                for (int i = 0; i < attachCount; i++) {
                    Logger.m173d("attachments[%d]=%s", Integer.valueOf(i), attachments[i]);
                }
            }
            MessageAuthor author = (MessageAuthor) data.getParcelable("AUTHOR");
            RepliedTo replyTo = (RepliedTo) data.getParcelable("REPLY_TO");
            Integer taskId = null;
            if (data.containsKey("TASK_ID")) {
                taskId = Integer.valueOf(data.getInt("TASK_ID"));
            }
            List<Attach> protoAttachments = attachments != null ? ProtoProxy.api2Proto(attachments) : Collections.emptyList();
            ReplyTo protoReplyTo = ReplyTo.getDefaultInstance();
            if (replyTo != null) {
                if (!TextUtils.isEmpty(replyTo.messageId)) {
                    protoReplyTo = ReplyTo.newBuilder().setMessageId(replyTo.messageId).setAuthorId(replyTo.authorId).build();
                }
            }
            boolean isSticker = SmileTextProcessor.isSticker(message);
            Builder status = new Builder().setConversationId(conversationId).setDate(System.currentTimeMillis()).setStatus(Status.WAITING);
            Message.Builder newBuilder = Message.newBuilder();
            if (message == null) {
                message = "";
            }
            Message.Builder uuid = newBuilder.setText(message).setAuthorId(author.getId()).setUuid(UUID.randomUUID().toString());
            if (isSticker) {
                type = Type.STICKER;
            } else {
                type = Type.USER;
            }
            uuid = uuid.setType(type).addAllAttaches(protoAttachments).setReplyTo(protoReplyTo);
            if (taskId != null) {
                intValue = taskId.intValue();
            } else {
                intValue = 0;
            }
            MessageModel messageModel = MessagesCache.getInstance().addNewMessage(status.setMessage(uuid.setTaskId(intValue).setCapabilities(Capabilities.newBuilder().setCanDelete(true).setCantEdit(isSticker).build()).build()).build());
            Bundle output = new Bundle();
            output.putInt("MESSAGE_ID", messageModel.databaseId);
            MessagesService.sendActionSendAll(OdnoklassnikiApplication.getContext());
            GlobalBus.send(2131624197, new BusEvent(event.bundleInput, output, -1));
        } catch (Throwable e) {
            Logger.m178e(e);
            GlobalBus.send(2131624197, new BusEvent(event.bundleInput, CommandProcessor.createErrorBundle(e), -2));
        }
    }
}
