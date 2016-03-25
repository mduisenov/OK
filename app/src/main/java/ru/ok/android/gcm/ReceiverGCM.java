package ru.ok.android.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.caps.EntityCapsManager;
import ru.ok.android.services.app.IntentUtils;
import ru.ok.android.services.app.OdnoklassnikiService;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.android.utils.settings.Settings;

public final class ReceiverGCM extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        boolean isForCurrentUser = true;
        Logger.m173d("action: %s, extras: %s", intent.getAction(), IntentUtils.createIntentExtrasString(intent));
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        if (gcm == null) {
            Logger.m184w("Could not obtain GoogleCloudMessaging instance");
            return;
        }
        String messageType = gcm.getMessageType(intent);
        Logger.m173d("Message type: %s", messageType);
        if (messageType != null) {
            int i = -1;
            switch (messageType.hashCode()) {
                case 102161:
                    if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
                        i = 0;
                        break;
                    }
                    break;
            }
            switch (i) {
                case RECEIVED_VALUE:
                    String intentTargetUserId = intent.getExtras().getString(EntityCapsManager.ELEMENT);
                    String transportProviderUserId = JsonSessionTransportProvider.getInstance().getStateHolder().getUserId();
                    if (!(intentTargetUserId == null || TextUtils.equals(intentTargetUserId, transportProviderUserId)) || TextUtils.isEmpty(transportProviderUserId)) {
                        isForCurrentUser = false;
                    }
                    if (!TextUtils.isEmpty(Settings.getToken(context)) && isForCurrentUser) {
                        handleMessage(context, intent);
                    }
                default:
            }
        }
    }

    private void handleMessage(Context context, Intent intent) {
        if (intent != null) {
            Bundle data = intent.getExtras();
            if (data != null) {
                if ("1079260813460".equals(data.getString("from"))) {
                    pushGeneralLogic(data);
                    Intent i = getNotificationIntent(context, data, null);
                    i.setPackage("ru.ok.android");
                    context.sendOrderedBroadcast(i, null);
                    context.startService(getNotificationIntent(context, data, OdnoklassnikiService.class));
                }
            }
        }
    }

    private Intent getNotificationIntent(Context context, Bundle data, Class<?> cl) {
        String msg = data.getString(NotificationCompat.CATEGORY_MESSAGE);
        String collapseKey = data.getString("collapse_key");
        String cid = data.getString("cid");
        String server = data.getString("srv");
        String callerName = data.getString("callerName");
        String conversationId = data.getString("mc");
        String conversationTopic = data.getString("convTopic");
        String hideConversationId = data.getString("hmc");
        String senderId = data.getString("suid");
        String messageId = data.getString("msgid");
        String creationTimeString = data.getString("ctime");
        long creationTime = 0;
        try {
            creationTime = !TextUtils.isEmpty(creationTimeString) ? Long.parseLong(creationTimeString) : 0;
        } catch (Throwable e) {
            Logger.m180e(e, "Failed to parse creation time: %s", creationTimeString);
        }
        String discussionId = data.getString(Logger.METHOD_D);
        String makePresentToUserId = data.getString("p");
        String makePresentHolidayId = data.getString("hid");
        String openNotificationsPage = data.getString("n");
        String mediatopicId = data.getString("mtid");
        String presentNotificationId = data.getString("nid");
        String groupId = data.getString("gid");
        String videoId = data.getString("vdid");
        String videoProvider = data.getString("vprv");
        String videoStatus = data.getString("vdst");
        String hiddenStr = data.getString("hdn");
        boolean hidden = false;
        if (!TextUtils.isEmpty(hiddenStr)) {
            try {
                hidden = Integer.parseInt(hiddenStr) > 0;
            } catch (Throwable e2) {
                Logger.m178e(e2);
            }
        }
        String uri = data.getString("uri");
        Intent intent = new Intent("ru.ok.android.action.NOTIFY");
        if (cl != null) {
            intent.setClass(context, cl);
        }
        intent.putExtra("key", collapseKey);
        intent.putExtra(Message.ELEMENT, msg);
        intent.putExtra("cid", cid);
        intent.putExtra("caller_name", callerName);
        intent.putExtra("server", server);
        intent.putExtra("conversation_id", conversationId);
        intent.putExtra("conversation_topic", conversationTopic);
        intent.putExtra("hide_conversation_id", hideConversationId);
        intent.putExtra("sender_id", senderId);
        intent.putExtra("message_id", messageId);
        intent.putExtra("push_creation_date", creationTime);
        intent.putExtra("dsc_id", discussionId);
        intent.putExtra("present_to_id", makePresentToUserId);
        intent.putExtra("present_holiday_id", makePresentHolidayId);
        intent.putExtra("open_notifications_page", openNotificationsPage);
        intent.putExtra("mediatopic_id", mediatopicId);
        intent.putExtra("present_notification_id", presentNotificationId);
        intent.putExtra("group_id", groupId);
        intent.putExtra("video_id", videoId);
        intent.putExtra("video_status", videoStatus);
        intent.putExtra("hidden", hidden);
        intent.putExtra("uri", uri);
        intent.putExtra("type", data.getString("type"));
        intent.putExtra("action_conversation_id", data.getString("convId"));
        intent.putExtra("PARTICIPANT_ID", data.getString("actorId"));
        long actionTime = 0;
        String actionTimeString = data.getString("actionTime");
        try {
            actionTime = !TextUtils.isEmpty(actionTimeString) ? Long.parseLong(actionTimeString) : 0;
        } catch (Throwable e22) {
            Logger.m180e(e22, "Failed to parse read status action time: %s", actionTimeString);
        }
        intent.putExtra("PARTICIPANT_LAST_VIEW_TIME", actionTime);
        return intent;
    }

    private void pushGeneralLogic(Bundle data) {
        String eventsCountString = data.getString("events");
        if (!TextUtils.isEmpty(eventsCountString)) {
            try {
                EventsManager.updateAppIconBadget(Integer.parseInt(eventsCountString));
            } catch (Exception e) {
                Logger.m180e(e, "Failed to parse events count: %s", eventsCountString);
            }
        }
    }
}
