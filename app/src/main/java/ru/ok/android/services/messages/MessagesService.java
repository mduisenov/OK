package ru.ok.android.services.messages;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.proto.MessagesProto.Attach;
import ru.ok.android.proto.MessagesProto.Attach.Type;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.Logger;

public final class MessagesService extends Service {
    private Handler quitHandler;
    private HandlerThread thread;
    private Handler threadHandler;

    /* renamed from: ru.ok.android.services.messages.MessagesService.1 */
    class C04421 extends Handler {
        C04421() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    Logger.m173d("Call stopSelf %d", Integer.valueOf(msg.arg1));
                    MessagesService.this.stopSelf(msg.arg1);
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.services.messages.MessagesService.2 */
    static /* synthetic */ class C04432 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type;

        static {
            $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type = new int[Type.values().length];
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type[Type.PHOTO.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type[Type.VIDEO.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type[Type.MOVIE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type[Type.AUDIO_RECORDING.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    class MessagesHandler extends Handler {
        public MessagesHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            MessagesService.this.quitHandler.removeMessages(1);
            boolean sendServiceStop = true;
            switch (msg.what) {
                case RECEIVED_VALUE:
                    Logger.m172d("MSG_SEND_ALL message received");
                    MessagesService.this.sendAllMessages();
                    OverdueHelper.processOverdueMessages(MessagesService.this);
                    if (OverdueHelper.scheduleOverdueProcessingIfNeeded(MessagesService.this)) {
                        sendServiceStop = false;
                        break;
                    }
                    break;
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    Logger.m172d("MSG_OVERDUE message received");
                    OverdueHelper.processOverdueMessages(MessagesService.this);
                    break;
                case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                    Logger.m172d("MSG_MARK_AS_READ message received");
                    MarkAsReadHelper.actionMarkAsRead(MessagesService.this, (Intent) msg.obj);
                    break;
                default:
                    Logger.m185w("Unknown command: %d", Integer.valueOf(msg.what));
                    break;
            }
            if (sendServiceStop) {
                MessagesService.this.sendQuitMessageWithStartId(1, msg.arg1, 30000);
            }
        }
    }

    public MessagesService() {
        this.thread = new HandlerThread(MessagesService.class.getSimpleName());
        this.quitHandler = new C04421();
    }

    public static void sendActionSendAll(Context context) {
        Intent intent = new Intent(context, MessagesService.class);
        intent.setAction("send_all");
        context.startService(intent);
    }

    @NonNull
    public static Intent markAsReadIntent(String conversationId, String messageId, Context context, boolean fromNotification) {
        Intent markAsRead = new Intent(context, MessagesService.class);
        markAsRead.setAction("mark_as_read");
        markAsRead.putExtra("message_id", messageId);
        markAsRead.putExtra("conversation_id", conversationId);
        markAsRead.putExtra("from-notification", fromNotification);
        return markAsRead;
    }

    public void onCreate() {
        super.onCreate();
        Logger.m172d("");
        this.thread.start();
        this.threadHandler = new MessagesHandler(this.thread.getLooper());
    }

    public void onDestroy() {
        super.onDestroy();
        Logger.m172d("");
        this.thread.quit();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.m172d(String.valueOf(intent));
        if (intent == null) {
            Logger.m184w("Null intent passed");
            return 2;
        }
        String action = intent.getAction();
        int i = -1;
        switch (action.hashCode()) {
            case -1091295072:
                if (action.equals("overdue")) {
                    i = 1;
                    break;
                }
                break;
            case -736926191:
                if (action.equals("mark_as_read")) {
                    i = 2;
                    break;
                }
                break;
            case 1247769706:
                if (action.equals("send_all")) {
                    i = 0;
                    break;
                }
                break;
        }
        switch (i) {
            case RECEIVED_VALUE:
                if (!this.threadHandler.hasMessages(0)) {
                    OverdueHelper.unScheduleOverdueProcessing(this);
                    sendThreadMessageWithStartId(0, startId);
                }
                return 3;
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                if (!this.threadHandler.hasMessages(1)) {
                    sendThreadMessageWithStartId(1, startId);
                }
                return 3;
            case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (!this.threadHandler.hasMessages(2)) {
                    Message message = this.threadHandler.obtainMessage(2);
                    message.arg1 = startId;
                    message.obj = intent;
                    this.threadHandler.sendMessage(message);
                }
                return 3;
            default:
                Logger.m184w("Unknown action " + action);
                sendQuitMessageWithStartId(1, startId, 30000);
                return 2;
        }
    }

    private void sendThreadMessageWithStartId(int msg, int startId) {
        sendMessageToHandler(this.threadHandler, msg, startId, 0);
    }

    private void sendQuitMessageWithStartId(int msg, int startId, long delay) {
        sendMessageToHandler(this.quitHandler, msg, startId, delay);
    }

    private void sendMessageToHandler(Handler handler, int msg, int startId, long delay) {
        Message message = handler.obtainMessage(msg);
        message.arg1 = startId;
        handler.sendMessageDelayed(message, delay);
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void sendAllMessages() {
        /*
        r27 = this;
        r3 = 1;
        r0 = r27;
        r8 = ru.ok.android.utils.NetUtils.isConnectionAvailable(r0, r3);
        if (r8 != 0) goto L_0x0010;
    L_0x0009:
        r3 = "Not connected, quitting";
        ru.ok.android.utils.Logger.m172d(r3);
    L_0x000f:
        return;
    L_0x0010:
        r9 = r27.getApplicationContext();
        r15 = ru.ok.android.model.cache.ram.MessagesCache.getInstance();
    L_0x0018:
        r14 = r15.getPendingMessage();
        if (r14 != 0) goto L_0x0025;
    L_0x001e:
        r3 = "No message found into db, quitting...";
        ru.ok.android.utils.Logger.m172d(r3);
        goto L_0x000f;
    L_0x0025:
        r3 = "Sending message: %s";
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r4[r5] = r14;
        ru.ok.android.utils.Logger.m173d(r3, r4);
        r10 = r14.databaseId;
        r0 = r27;
        r3 = r15.processAttachmentsMessage(r0, r10);
        if (r3 != 0) goto L_0x006c;
    L_0x003b:
        r14 = r15.getMessage(r10);
        if (r14 != 0) goto L_0x0048;
    L_0x0041:
        r3 = "Message not found twice";
        ru.ok.android.utils.Logger.m184w(r3);
        goto L_0x0018;
    L_0x0048:
        r3 = r14.status;
        r4 = ru.ok.android.proto.MessagesProto.Message.Status.WAITING;
        if (r3 == r4) goto L_0x005a;
    L_0x004e:
        r3 = r14.status;
        r4 = ru.ok.android.proto.MessagesProto.Message.Status.SENDING;
        if (r3 == r4) goto L_0x005a;
    L_0x0054:
        r3 = r14.status;
        r4 = ru.ok.android.proto.MessagesProto.Message.Status.FAILED;
        if (r3 != r4) goto L_0x0018;
    L_0x005a:
        r3 = "Bad messages status: %s";
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r4[r5] = r14;
        ru.ok.android.utils.Logger.m177e(r3, r4);
        r3 = ru.ok.android.proto.MessagesProto.Message.Status.WAITING_ATTACHMENT;
        r15.updateStatus(r14, r3);
        goto L_0x0018;
    L_0x006c:
        r3 = r14.serverId;
        r13 = android.text.TextUtils.isEmpty(r3);
        if (r13 == 0) goto L_0x0134;
    L_0x0074:
        r3 = ru.ok.android.proto.MessagesProto.Message.Status.SENDING;
        r15.updateStatus(r14, r3);
    L_0x0079:
        if (r13 == 0) goto L_0x018b;
    L_0x007b:
        r2 = new ru.ok.java.api.request.messaging.send.MessageSendRequest;
        r3 = r14.conversationId;
        r4 = r14.message;
        r4 = r4.getText();
        r5 = r14.message;
        r5 = r5.getAttachesList();
        r5 = convertAttachments(r5);
        r6 = r14.message;
        r6 = r6.getReplyTo();
        if (r6 == 0) goto L_0x0188;
    L_0x0097:
        r6 = r14.message;
        r6 = r6.getReplyTo();
        r6 = r6.getMessageId();
    L_0x00a1:
        r7 = r14.message;
        r7 = r7.getUuid();
        r2.<init>(r3, r4, r5, r6, r7);
        r12 = "message-send-failed";
        r25 = "message-send-success";
        r0 = r14.date;
        r16 = r0;
    L_0x00b4:
        r3 = ru.ok.android.services.transport.JsonSessionTransportProvider.getInstance();	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r22 = r3.execJsonHttpMethod(r2);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r20 = 0;
        if (r13 == 0) goto L_0x01d3;
    L_0x00c0:
        r23 = ru.ok.java.api.json.messages.MessageSendParser.parse(r22);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r0 = r23;
        r3 = r0.serverId;	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        if (r3 != 0) goto L_0x01aa;
    L_0x00ce:
        r3 = r14.conversationId;	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r0 = r23;
        r15.updateBySentResult(r3, r10, r0);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r20 = 1;
        addSuccessStatisticsMessage(r25);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
    L_0x00da:
        if (r20 == 0) goto L_0x0018;
    L_0x00dc:
        r4 = java.lang.System.currentTimeMillis();	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r4 = r4 - r16;
        r6 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r3 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r3 > 0) goto L_0x0018;
    L_0x00e8:
        ru.ok.android.services.app.notification.NotificationSignal.playSentSound(r27);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        goto L_0x0018;
    L_0x00ed:
        r11 = move-exception;
        r3 = 1;
        r21 = ru.ok.android.services.processors.base.CommandProcessor.ErrorType.fromServerException(r11, r3);
        r3 = "Server error for message %s";
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r4[r5] = r14;
        ru.ok.android.utils.Logger.m180e(r11, r3, r4);
        r3 = ru.ok.android.model.cache.ram.MessagesCache.getInstance();
        r4 = ru.ok.android.proto.MessagesProto.Message.Status.SERVER_ERROR;
        r0 = r21;
        r3.updateStatusAndFailureReason(r10, r4, r0);
        r3 = r14.conversationId;
        r4 = r21.getDefaultErrorMessage();
        r4 = ru.ok.android.utils.localization.LocalizationManager.getString(r9, r4);
        r5 = r14.message;
        r5 = r5.getText();
        ru.ok.android.offline.NotificationUtils.sendMessageFailedServerBroadcast(r9, r3, r4, r5);
        r3 = r11.getErrorCode();
        r4 = 707; // 0x2c3 float:9.91E-43 double:3.493E-321;
        if (r3 != r4) goto L_0x012c;
    L_0x0125:
        r4 = 0;
        r0 = r27;
        ru.ok.android.services.processors.stickers.StickersManager.updatePaymentEndDate(r0, r4);
    L_0x012c:
        r3 = "server_error";
        addErrorDetailedStatisticsMessage(r12, r3, r11);
        goto L_0x0018;
    L_0x0134:
        r3 = r14.message;
        r3 = r3.getEditInfo();
        r19 = r3.getNewText();
        r3 = android.text.TextUtils.isEmpty(r19);
        if (r3 != 0) goto L_0x014b;
    L_0x0144:
        r3 = ru.ok.android.proto.MessagesProto.Message.Status.SENDING;
        r15.updateEditStatus(r14, r3);
        goto L_0x0079;
    L_0x014b:
        r3 = "Message has serverId and no EditInfo:\n%s";
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r4[r5] = r14;
        ru.ok.android.utils.Logger.m177e(r3, r4);
        r3 = ru.ok.android.graylog.GrayLog.isEnabled();
        if (r3 == 0) goto L_0x0174;
    L_0x015d:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "----- MessagesCache -----\nMessage has serverId and no EditInfo:\n";
        r3 = r3.append(r4);
        r3 = r3.append(r14);
        r18 = r3.toString();
        ru.ok.android.graylog.GrayLog.log(r18);
    L_0x0174:
        r3 = ru.ok.android.model.cache.ram.MessagesCache.getInstance();
        r4 = ru.ok.android.proto.MessagesProto.Message.Status.SENT;
        r3.updateStatus(r10, r4);
        r3 = ru.ok.android.model.cache.ram.MessagesCache.getInstance();
        r4 = ru.ok.android.proto.MessagesProto.Message.Status.UNRECOGNIZED;
        r3.updateEditStatus(r14, r4);
        goto L_0x0018;
    L_0x0188:
        r6 = 0;
        goto L_0x00a1;
    L_0x018b:
        r2 = new ru.ok.java.api.request.messaging.MessageEditRequest;
        r3 = r14.conversationId;
        r4 = r14.serverId;
        r5 = r14.message;
        r5 = r5.getEditInfo();
        r5 = r5.getNewText();
        r2.<init>(r3, r4, r5);
        r12 = "message-edit-failed";
        r25 = "message-edit-success";
        r0 = r14.dateEdited;
        r16 = r0;
        goto L_0x00b4;
    L_0x01aa:
        r3 = ru.ok.android.proto.MessagesProto.Message.Status.FAILED;	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r15.updateStatus(r14, r3);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r3 = "no_server_id";
        addErrorStatisticsMessage(r12, r3);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        goto L_0x00da;
    L_0x01b7:
        r11 = move-exception;
    L_0x01b8:
        r3 = "\u0421onnection problem on message %s";
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r4[r5] = r14;
        ru.ok.android.utils.Logger.m180e(r11, r3, r4);
        if (r13 == 0) goto L_0x0214;
    L_0x01c6:
        r3 = ru.ok.android.proto.MessagesProto.Message.Status.WAITING;
        r15.updateStatus(r14, r3);
    L_0x01cb:
        r3 = "connection";
        addErrorDetailedStatisticsMessage(r12, r3, r11);
        goto L_0x000f;
    L_0x01d3:
        r24 = ru.ok.java.api.json.messages.JsonMessageEditParser.parse(r22);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r3 = android.text.TextUtils.isEmpty(r24);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        if (r3 != 0) goto L_0x01eb;
    L_0x01dd:
        r3 = ru.ok.android.proto.MessagesProto.Message.Status.SENT;	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r15.updateEditStatus(r14, r3);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r20 = 1;
        addSuccessStatisticsMessage(r25);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        goto L_0x00da;
    L_0x01e9:
        r11 = move-exception;
        goto L_0x01b8;
    L_0x01eb:
        r3 = ru.ok.android.proto.MessagesProto.Message.Status.FAILED;	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r15.updateEditStatus(r14, r3);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        r3 = "no_server_id";
        addErrorStatisticsMessage(r12, r3);	 Catch:{ ServerReturnErrorException -> 0x00ed, HttpStatusException -> 0x01b7, NetworkException -> 0x01e9, JsonParseException -> 0x01f8, Exception -> 0x0220 }
        goto L_0x00da;
    L_0x01f8:
        r11 = move-exception;
        r3 = "Parse problem %s";
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r4[r5] = r14;
        ru.ok.android.utils.Logger.m180e(r11, r3, r4);
        if (r13 == 0) goto L_0x021a;
    L_0x0207:
        r3 = ru.ok.android.proto.MessagesProto.Message.Status.WAITING;
        r15.updateStatus(r14, r3);
    L_0x020c:
        r3 = "parse";
        addErrorDetailedStatisticsMessage(r12, r3, r11);
        goto L_0x000f;
    L_0x0214:
        r3 = ru.ok.android.proto.MessagesProto.Message.Status.WAITING;
        r15.updateEditStatus(r14, r3);
        goto L_0x01cb;
    L_0x021a:
        r3 = ru.ok.android.proto.MessagesProto.Message.Status.WAITING;
        r15.updateEditStatus(r14, r3);
        goto L_0x020c;
    L_0x0220:
        r11 = move-exception;
        r3 = "Other error %s";
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r4[r5] = r14;
        ru.ok.android.utils.Logger.m180e(r11, r3, r4);
        r0 = r11 instanceof ru.ok.android.services.transport.exception.TransportLevelException;
        r26 = r0;
        if (r13 == 0) goto L_0x0242;
    L_0x0233:
        r3 = ru.ok.android.proto.MessagesProto.Message.Status.FAILED;
        r15.updateStatus(r14, r3);
    L_0x0238:
        if (r26 == 0) goto L_0x0248;
    L_0x023a:
        r3 = "transport";
    L_0x023d:
        addErrorDetailedStatisticsMessage(r12, r3, r11);
        goto L_0x000f;
    L_0x0242:
        r3 = ru.ok.android.proto.MessagesProto.Message.Status.FAILED;
        r15.updateEditStatus(r14, r3);
        goto L_0x0238;
    L_0x0248:
        r3 = "other_error";
        goto L_0x023d;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.messages.MessagesService.sendAllMessages():void");
    }

    private static List<Map<String, String>> convertAttachments(List<Attach> attaches) {
        if (attaches.isEmpty()) {
            return null;
        }
        List<Map<String, String>> result = new ArrayList();
        for (Attach attachment : attaches) {
            Map<String, String> attach = new HashMap();
            switch (C04432.$SwitchMap$ru$ok$android$proto$MessagesProto$Attach$Type[attachment.getType().ordinal()]) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    attach.put("type", "UPLOADED_PHOTO");
                    attach.put("token", attachment.getPhoto().getRemoteToken());
                    break;
                case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                    attach.put("type", "UPLOADED_MOVIE");
                    attach.put("movieId", String.valueOf(attachment.getVideo().getServerId()));
                    break;
                case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    attach.put("type", "UPLOADED_MOVIE");
                    attach.put("movieId", String.valueOf(attachment.getAudio().getServerId()));
                    break;
                default:
                    Logger.m185w("Unknown attach type: %d", attachment.getType());
                    continue;
            }
            result.add(attach);
        }
        return result;
    }

    private static void addSuccessStatisticsMessage(String eventId) {
        StatisticManager.getInstance().addStatisticEvent(eventId, new Pair[0]);
    }

    static void addErrorStatisticsMessage(String event, String reason) {
        StatisticManager.getInstance().addStatisticEvent(event, new Pair("reason", reason));
    }

    static void addErrorDetailedStatisticsMessage(String event, String reason, Exception e) {
        StatisticManager.getInstance().addStatisticEvent(event, new Pair("reason", reason), new Pair(reason, buildDetailedMessage(e)));
    }

    @NonNull
    private static String buildDetailedMessage(Exception e) {
        String causeString;
        Throwable cause = e.getCause();
        if (cause != null) {
            causeString = "; " + buildExceptionMessage(cause);
        } else {
            causeString = "";
        }
        return buildExceptionMessage(e) + causeString;
    }

    @NonNull
    private static String buildExceptionMessage(Throwable cause) {
        return cause.getClass().getName() + ": " + cause.getMessage();
    }
}
