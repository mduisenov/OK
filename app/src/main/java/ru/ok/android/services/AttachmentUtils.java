package ru.ok.android.services;

import android.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.proto.MessagesProto.Attach.Status;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.bus.BusMessagingHelper;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.messages.JsonAttachmentParser;
import ru.ok.java.api.request.messaging.AttachmentRequest;
import ru.ok.model.messages.Attachment;
import ru.ok.model.messages.Attachment.AttachmentType;

public class AttachmentUtils {
    public static Attachment getAttachments(String id) throws BaseApiException, JSONException {
        JSONArray array = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new AttachmentRequest(new String[]{id})).getResultAsObject().optJSONArray("attachments");
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                if (object != null) {
                    return JsonAttachmentParser.parse(object);
                }
            }
        }
        return null;
    }

    public static void sendShowAttachStatEvents(AttachmentType type) {
        StatisticManager.getInstance().addStatisticEvent("attach-show", new Pair("type", type.getStrValue()));
    }

    public static void updateAttachmentState(int messageId, long attachmentId, Status newStatus) {
        Logger.m173d("attachmentDatabaseId=%d, new status=%s", Long.valueOf(attachmentId), newStatus);
        MessagesCache.getInstance().updateAttachmentStatus(messageId, attachmentId, newStatus);
        BusMessagingHelper.messageUpdated(messageId);
    }
}
