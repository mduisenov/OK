package ru.ok.android.services.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.model.cache.ram.ConversationsCache;
import ru.ok.android.onelog.PushDeliveryLog;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.services.app.messaging.OdklMessagingEventsService;
import ru.ok.android.services.processors.notification.NotificationSignalFactory;
import ru.ok.android.ui.fragments.messages.helpers.DecodedChatId;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NotificationsUtils;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.android.utils.settings.NotificationType;
import ru.ok.android.utils.settings.Settings;
import ru.ok.android.videochat.VideochatController;
import ru.ok.java.api.utils.Utils;
import ru.ok.model.Discussion;

public final class NotifyReceiver extends BroadcastReceiver {

    /* renamed from: ru.ok.android.services.app.NotifyReceiver.1 */
    static class C04221 implements Runnable {
        final /* synthetic */ long val$actionTime;
        final /* synthetic */ String val$actorId;
        final /* synthetic */ Context val$context;
        final /* synthetic */ String val$conversationId;

        C04221(String str, String str2, long j, Context context) {
            this.val$conversationId = str;
            this.val$actorId = str2;
            this.val$actionTime = j;
            this.val$context = context;
        }

        public void run() {
            if (ConversationsCache.getInstance().updateConversationLastViewTime(this.val$conversationId, this.val$actorId, this.val$actionTime)) {
                this.val$context.getContentResolver().notifyChange(OdklProvider.conversationUri(this.val$conversationId), null);
                Intent intent = new Intent("CHAT_LAST_VIEW_TIME_CHANGED").putExtra("conversation_id", this.val$conversationId).putExtra("PARTICIPANT_ID", this.val$actorId).putExtra("PARTICIPANT_LAST_VIEW_TIME", this.val$actionTime).putExtra(Message.ELEMENT, "");
                intent.setPackage("ru.ok.android");
                this.val$context.sendOrderedBroadcast(intent, null);
            }
        }
    }

    public void onReceive(Context context, Intent intent) {
        if ("ru.ok.android.action.NOTIFY".equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String msg = bundle.getString(Message.ELEMENT);
                String cid = bundle.getString("cid");
                if (cid != null) {
                    String userName = bundle.getString("caller_name");
                    VideochatController.instance().processIncomingCall(bundle.getString("server"), cid, userName);
                } else if (!hideEventNotification(context, bundle) && msg != null) {
                    showEventNotification(context, bundle);
                }
            }
        }
    }

    public static boolean isNotificationForConversation(Intent intent, Conversation conversation) {
        if (conversation == null) {
            return false;
        }
        Bundle bundle = intent.getExtras();
        if (bundle.getBoolean("general_error", false) || !isNotificationForConversation(intent)) {
            return false;
        }
        return TextUtils.equals(bundle.getString("conversation_id"), conversation.getId());
    }

    public static boolean isNotificationForConversation(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null || bundle.getString(Message.ELEMENT) == null || TextUtils.isEmpty(bundle.getString("conversation_id"))) {
            return false;
        }
        return true;
    }

    public static boolean isNotificationForDiscussionServerError(Intent intent, Discussion discussion) {
        if (isNotificationForServerError(intent, "dsc_id")) {
            return TextUtils.equals(intent.getStringExtra("dsc_id"), discussion.id + ":" + discussion.type);
        }
        return false;
    }

    public static boolean isNotificationForMessageServerError(Intent intent) {
        return isNotificationForServerError(intent, "conversation_id");
    }

    public static boolean isNotificationForDiscussionComment(Intent intent, Discussion discussion) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return false;
        }
        return TextUtils.equals(discussion.id + ":" + discussion.type, bundle.getString("dsc_id"));
    }

    public static NotificationType getNotificationsSettings(Context context) {
        return getNotificationsSettingsWithSound(context, Settings.getBoolValueInvariable(context, context.getString(2131166301), true));
    }

    public static NotificationType getNotificationsSettingsWithSound(Context context, boolean sound) {
        return new NotificationType(Settings.getBoolValueInvariable(context, context.getString(2131166285), false), sound, Settings.getBoolValueInvariable(context, context.getString(2131166295), true), Settings.getBoolValueInvariable(context, context.getString(2131166305), true), Settings.getBoolValueInvariable(context, context.getString(2131166289), true), Settings.getBoolValueInvariable(context, context.getString(2131166300), false));
    }

    private static boolean isNotificationForServerError(Intent intent, String key) {
        Bundle bundle = intent.getExtras();
        if (bundle == null || bundle.getString(Message.ELEMENT) == null || bundle.getString(key) == null) {
            return false;
        }
        return bundle.getBoolean("server_error", false);
    }

    private static void showEventNotification(Context context, Bundle bundle) {
        if (!getNotificationsSettings(context).disableNotifications) {
            if (bundle.getString("conversation_id") != null) {
                PushDeliveryLog.conversation(bundle.getLong("push_creation_date", 0));
                startMessagesNotificationsService(context, bundle);
                EventsManager.getInstance().updateNow();
                return;
            }
            String type = bundle.getString("type");
            String conversationId = bundle.getString("action_conversation_id");
            String actorId = bundle.getString("PARTICIPANT_ID");
            long actionTime = bundle.getLong("PARTICIPANT_LAST_VIEW_TIME");
            if (type == null || !type.equals("ConversationRead") || actionTime == 0) {
                new NotificationSignalFactory(context).logCreationTime(bundle);
                GlobalBus.send(2131624093, new BusEvent(bundle));
                EventsManager.getInstance().updateNow();
                return;
            }
            updateChatReadStatus(context, conversationId, actorId, actionTime, true);
        }
    }

    public static void updateUiForConversationOnNewMessage(Context context, DecodedChatId decodedChatId) {
        Intent intent = new Intent("CHAT_NEW_MESSAGE_ARRIVED_XMPP").putExtra("conversation_id", OdklMessagingEventsService.getExistingEncodedChatId(decodedChatId, ConversationsCache.getInstance().getAllConversationsIds())).putExtra(Message.ELEMENT, "");
        intent.setPackage("ru.ok.android");
        context.sendOrderedBroadcast(intent, null);
    }

    public static void updateUiForConversation(Context context, DecodedChatId decodedChatId) {
        String encodedChatId = OdklMessagingEventsService.getExistingEncodedChatId(decodedChatId, ConversationsCache.getInstance().getAllConversationsIds());
        if (encodedChatId != null) {
            Intent intent = new Intent("CHAT_STATE_UPDATED_XMPP").putExtra("conversation_id", encodedChatId).putExtra(Message.ELEMENT, "");
            intent.setPackage("ru.ok.android");
            context.sendOrderedBroadcast(intent, null);
        }
    }

    public static void updateDecodedChatReadStatusXmpp(Context context, DecodedChatId decodedChatId, String actorId, long actionTime) {
        updateChatReadStatus(context, OdklMessagingEventsService.getExistingEncodedChatId(decodedChatId, ConversationsCache.getInstance().getAllConversationsIds()), String.valueOf(Utils.xorId(actorId)), actionTime, false);
    }

    private static void updateChatReadStatus(Context context, String conversationId, String actorId, long actionTime, boolean skipIfXmppIsOk) {
        if (conversationId != null) {
            if (skipIfXmppIsOk && OdklMessagingEventsService.isXmppMessageReadStatusPushEnabled()) {
                Logger.m172d("Skipped chat read status notification due to fine XMPP connection");
            } else {
                ThreadUtil.execute(new C04221(conversationId, actorId, actionTime, context));
            }
        }
    }

    private static void startMessagesNotificationsService(Context context, Bundle originalData) {
        Intent intent = new Intent(context, NotificationsService.class);
        intent.setAction("show_notification");
        intent.putExtras(originalData);
        WakefulBroadcastReceiver.startWakefulService(context, intent);
    }

    private static boolean hideEventNotification(Context context, Bundle bundle) {
        String conversationId = bundle.getString("hide_conversation_id");
        boolean result = false;
        if (!TextUtils.isEmpty(conversationId)) {
            NotificationsUtils.hideNotificationForConversation(context, conversationId);
            result = true;
        }
        if (result) {
            EventsManager.getInstance().updateNow();
        }
        return result;
    }
}
