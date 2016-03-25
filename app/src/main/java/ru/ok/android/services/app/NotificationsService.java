package ru.ok.android.services.app;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;
import com.facebook.datasource.DataSource;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import java.util.concurrent.atomic.AtomicReference;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.services.app.notification.NotificationSignal;
import ru.ok.android.services.messages.MessagesService;
import ru.ok.android.ui.messaging.activity.MessageQuickReplyActivity;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.bus.BusMessagingHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.NotificationType;
import ru.ok.android.utils.settings.Settings;

public final class NotificationsService extends IntentService {
    private static final String[] PROJECTION_USER_PIC_NAME;
    private Uri tamtamSettingsUri;

    /* renamed from: ru.ok.android.services.app.NotificationsService.1 */
    class C04211 extends BaseBitmapDataSubscriber {
        final /* synthetic */ String val$conversationId;
        final /* synthetic */ String val$conversationTopic;
        final /* synthetic */ String val$finalUserName;
        final /* synthetic */ boolean val$isError;
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$messageId;
        final /* synthetic */ String val$senderId;
        final /* synthetic */ NotificationType val$settings;
        final /* synthetic */ int val$unreadCount;

        C04211(String str, String str2, String str3, String str4, String str5, String str6, int i, boolean z, NotificationType notificationType) {
            this.val$conversationId = str;
            this.val$conversationTopic = str2;
            this.val$messageId = str3;
            this.val$senderId = str4;
            this.val$finalUserName = str5;
            this.val$message = str6;
            this.val$unreadCount = i;
            this.val$isError = z;
            this.val$settings = notificationType;
        }

        public void onNewResultImpl(@Nullable Bitmap bitmap) {
            Logger.m182v("Avatar fetched");
            NotificationsService.this.showNotification(this.val$conversationId, this.val$conversationTopic, this.val$messageId, this.val$senderId, this.val$finalUserName, bitmap, this.val$message, this.val$unreadCount, this.val$isError, this.val$settings);
        }

        public void onFailureImpl(DataSource dataSource) {
            Logger.m182v("Some error occurs");
            NotificationsService.this.showNotification(this.val$conversationId, this.val$conversationTopic, this.val$messageId, this.val$senderId, this.val$finalUserName, null, this.val$message, this.val$unreadCount, this.val$isError, this.val$settings);
        }
    }

    private boolean ensureMessageLoaded(java.lang.String r24, java.lang.String r25, java.util.concurrent.atomic.AtomicReference<java.lang.Integer> r26) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:58:? in {17, 23, 29, 32, 33, 34, 44, 46, 47, 49, 50, 51, 52, 53, 54, 55, 56, 57, 59, 60} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:57)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r23 = this;
        r19 = android.text.TextUtils.isEmpty(r25);
        if (r19 == 0) goto L_0x001b;
    L_0x0006:
        r19 = "messageId is empty for conversation: %s";
        r20 = 1;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r21 = 0;
        r20[r21] = r24;
        ru.ok.android.utils.Logger.m185w(r19, r20);
        r19 = 1;
    L_0x001a:
        return r19;
    L_0x001b:
        r19 = ru.ok.android.model.cache.ram.MessagesCache.getInstance();
        r0 = r19;
        r1 = r24;
        r2 = r25;
        r12 = r0.getMessageTime(r1, r2);
        r20 = 0;
        r19 = (r12 > r20 ? 1 : (r12 == r20 ? 0 : -1));
        if (r19 <= 0) goto L_0x006f;
    L_0x002f:
        r19 = "Message %s already in DB";
        r20 = 1;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r21 = 0;
        r20[r21] = r25;
        ru.ok.android.utils.Logger.m173d(r19, r20);
        r19 = ru.ok.android.model.cache.ram.ConversationsCache.getInstance();
        r0 = r19;
        r1 = r24;
        r5 = r0.getConversation(r1);
        if (r5 == 0) goto L_0x006c;
    L_0x004f:
        r19 = r5.getNewMessagesCount();
        r19 = java.lang.Integer.valueOf(r19);
        r0 = r26;
        r1 = r19;
        r0.set(r1);
        r6 = r5.getLastViewTime();
        r19 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1));
        if (r19 <= 0) goto L_0x0069;
    L_0x0066:
        r19 = 1;
        goto L_0x001a;
    L_0x0069:
        r19 = 0;
        goto L_0x001a;
    L_0x006c:
        r19 = 1;
        goto L_0x001a;
    L_0x006f:
        r11 = 0;
        r16 = 0;
        r18 = 0;
        r19 = 0;
        r20 = 1;
        r21 = 0;
        r0 = r24;
        r1 = r19;
        r2 = r20;
        r3 = r21;
        r10 = ru.ok.android.utils.bus.BusMessagingHelper.loadNextMessagesBundle(r0, r1, r2, r3);
    L_0x0086:
        r14 = java.lang.System.currentTimeMillis();
        r19 = "[%d], messageId: %s";
        r20 = 2;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r21 = 0;
        r22 = java.lang.Integer.valueOf(r11);
        r20[r21] = r22;
        r21 = 1;
        r20[r21] = r25;
        ru.ok.android.utils.Logger.m173d(r19, r20);
        if (r18 == 0) goto L_0x00ad;
    L_0x00a6:
        r18 = 0;
        r20 = 3000; // 0xbb8 float:4.204E-42 double:1.482E-320;
        java.lang.Thread.sleep(r20);	 Catch:{ InterruptedException -> 0x0115 }
    L_0x00ad:
        r4 = ru.ok.android.services.processors.messaging.MessagesChunksProcessor.performLoadNextMessages(r10);	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r19 = ru.ok.android.model.cache.ram.MessagesCache.getInstance();	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r0 = r19;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r1 = r24;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r2 = r25;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r12 = r0.getMessageTime(r1, r2);	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r20 = 0;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r19 = (r12 > r20 ? 1 : (r12 == r20 ? 0 : -1));	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        if (r19 <= 0) goto L_0x0120;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
    L_0x00c5:
        r19 = "Message found in DB [%d] [%d]";	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r20 = 2;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r0 = r20;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r20 = r0;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r21 = 0;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r22 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r20[r21] = r22;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r21 = 1;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r22 = java.lang.Long.valueOf(r16);	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r20[r21] = r22;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        ru.ok.android.utils.Logger.m173d(r19, r20);	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r19 = "GENERAL_INFO";	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r0 = r19;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r5 = r4.getParcelable(r0);	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r5 = (ru.ok.model.Conversation) r5;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        if (r5 == 0) goto L_0x011a;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
    L_0x00f0:
        r0 = r5.newMessagesCount;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r19 = r0;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
    L_0x00f4:
        r19 = java.lang.Integer.valueOf(r19);	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r0 = r26;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r1 = r19;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r0.set(r1);	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        if (r5 == 0) goto L_0x0109;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
    L_0x0101:
        r0 = r5.lastViewTime;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r20 = r0;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r19 = (r12 > r20 ? 1 : (r12 == r20 ? 0 : -1));
        if (r19 <= 0) goto L_0x011d;
    L_0x0109:
        r19 = 1;
    L_0x010b:
        r20 = java.lang.System.currentTimeMillis();
        r20 = r20 - r14;
        r16 = r16 + r20;
        goto L_0x001a;
    L_0x0115:
        r9 = move-exception;
        ru.ok.android.utils.Logger.m178e(r9);
        goto L_0x00ad;
    L_0x011a:
        r19 = 0;
        goto L_0x00f4;
    L_0x011d:
        r19 = 0;
        goto L_0x010b;
    L_0x0120:
        r20 = java.lang.System.currentTimeMillis();
        r20 = r20 - r14;
        r16 = r16 + r20;
    L_0x0128:
        r11 = r11 + 1;
        r19 = 3;
        r0 = r19;
        if (r11 >= r0) goto L_0x0136;
    L_0x0130:
        r20 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r19 = (r16 > r20 ? 1 : (r16 == r20 ? 0 : -1));
        if (r19 < 0) goto L_0x0086;
    L_0x0136:
        r19 = 1;
        goto L_0x001a;
    L_0x013a:
        r8 = move-exception;
        r19 = "Failed to load next messages";	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r20 = 1;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r0 = r20;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r20 = r0;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r21 = 0;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r22 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r20[r21] = r22;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r0 = r19;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r1 = r20;	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        ru.ok.android.utils.Logger.m180e(r8, r0, r1);	 Catch:{ Exception -> 0x013a, all -> 0x0160 }
        r18 = 1;
        r20 = java.lang.System.currentTimeMillis();
        r20 = r20 - r14;
        r16 = r16 + r20;
        goto L_0x0128;
    L_0x0160:
        r19 = move-exception;
        r20 = java.lang.System.currentTimeMillis();
        r20 = r20 - r14;
        r16 = r16 + r20;
        throw r19;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.app.NotificationsService.ensureMessageLoaded(java.lang.String, java.lang.String, java.util.concurrent.atomic.AtomicReference):boolean");
    }

    private void prepareAndShowNotification(boolean r31, java.lang.String r32, java.lang.String r33, java.lang.String r34, java.lang.String r35, java.lang.String r36, int r37) {
        /* JADX: method processing error */
/*
        Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.ssa.SSATransform.placePhi(SSATransform.java:82)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:50)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r30 = this;
        r24 = 0;
        r29 = 0;
        r25 = 0;
        r2 = android.text.TextUtils.isEmpty(r36);	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        if (r2 != 0) goto L_0x0076;	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
    L_0x000c:
        r2 = r30.getContentResolver();	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        r3 = ru.ok.android.db.provider.OdklContract.Users.getUri(r36);	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        r4 = PROJECTION_USER_PIC_NAME;	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        r5 = 0;	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        r6 = 0;	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        r7 = 0;	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        r25 = r2.query(r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        if (r25 == 0) goto L_0x0070;	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
    L_0x001f:
        r2 = r25.moveToFirst();	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        if (r2 == 0) goto L_0x0070;	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
    L_0x0025:
        r2 = 0;	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        r0 = r25;	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        r24 = r0.getString(r2);	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        r2 = 1;	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        r0 = r25;	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        r7 = r0.getString(r2);	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
    L_0x0033:
        ru.ok.android.utils.IOUtils.closeSilently(r25);
    L_0x0036:
        r12 = ru.ok.android.services.app.NotifyReceiver.getNotificationsSettings(r30);
        r0 = r35;
        r28 = isNotificationSimple(r12, r7, r0);
        r2 = ru.ok.android.utils.URLUtil.isStubUrl(r24);
        if (r2 != 0) goto L_0x0048;
    L_0x0046:
        if (r28 == 0) goto L_0x0088;
    L_0x0048:
        r2 = "No avatar url (%s) in database or notification is simple: %s";
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r4 = 0;
        r3[r4] = r24;
        r4 = 1;
        r5 = java.lang.Boolean.valueOf(r28);
        r3[r4] = r5;
        ru.ok.android.utils.Logger.m183v(r2, r3);
        r8 = 0;
        r2 = r30;
        r3 = r32;
        r4 = r33;
        r5 = r34;
        r6 = r36;
        r9 = r35;
        r10 = r37;
        r11 = r31;
        r2.showNotification(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12);
    L_0x006f:
        return;
    L_0x0070:
        r2 = "Can't find user";	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        ru.ok.android.utils.Logger.m172d(r2);	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
    L_0x0076:
        r7 = r29;
        goto L_0x0033;
    L_0x0079:
        r26 = move-exception;
        ru.ok.android.utils.Logger.m178e(r26);	 Catch:{ Exception -> 0x0079, all -> 0x0083 }
        ru.ok.android.utils.IOUtils.closeSilently(r25);
        r7 = r29;
        goto L_0x0036;
    L_0x0083:
        r2 = move-exception;
        ru.ok.android.utils.IOUtils.closeSilently(r25);
        throw r2;
    L_0x0088:
        r2 = "Try to fetch avatar";
        ru.ok.android.utils.Logger.m182v(r2);
        r19 = r7;
        r27 = com.facebook.imagepipeline.request.ImageRequest.fromUri(r24);
        if (r27 == 0) goto L_0x006f;
    L_0x0096:
        r2 = com.facebook.drawee.backends.pipeline.Fresco.getImagePipeline();
        r0 = r27;
        r1 = r30;
        r2 = r2.fetchDecodedImage(r0, r1);
        r13 = new ru.ok.android.services.app.NotificationsService$1;
        r14 = r30;
        r15 = r32;
        r16 = r33;
        r17 = r34;
        r18 = r36;
        r20 = r35;
        r21 = r37;
        r22 = r31;
        r23 = r12;
        r13.<init>(r15, r16, r17, r18, r19, r20, r21, r22, r23);
        r3 = ru.ok.android.utils.ThreadUtil.executorService;
        r2.subscribe(r13, r3);
        goto L_0x006f;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.app.NotificationsService.prepareAndShowNotification(boolean, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int):void");
    }

    public NotificationsService() {
        super(NotificationsService.class.getSimpleName());
        setIntentRedelivery(true);
    }

    public void onCreate() {
        super.onCreate();
        Logger.m172d("");
    }

    public void onDestroy() {
        super.onDestroy();
        Logger.m172d("");
    }

    protected void onHandleIntent(Intent intent) {
        Object obj = null;
        if (intent == null) {
            try {
                Logger.m184w("Null Intent passed to me");
            } finally {
                WakefulBroadcastReceiver.completeWakefulIntent(intent);
            }
        } else {
            String action = intent.getAction();
            if (action == null) {
                Logger.m184w("null action received");
                WakefulBroadcastReceiver.completeWakefulIntent(intent);
                return;
            }
            Logger.m173d("Action: %s", action);
            switch (action.hashCode()) {
                case 2045156077:
                    if (action.equals("show_notification")) {
                        break;
                    }
                    break;
            }
            obj = -1;
            switch (obj) {
                case RECEIVED_VALUE:
                    actionShowNotification(intent);
                    break;
                default:
                    Logger.m185w("Unknown action %s", action);
                    break;
            }
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void actionShowNotification(Intent intent) {
        boolean isError;
        String conversationId;
        String conversationTopic;
        String message;
        String senderId;
        long creationTime;
        String messageId;
        AtomicReference<Integer> count;
        boolean isHidden = intent.getBooleanExtra("hidden", false) || isHiddenByTamTam();
        if (!intent.getBooleanExtra("general_error", false)) {
            if (!intent.getBooleanExtra("server_error", false)) {
                isError = false;
                conversationId = intent.getStringExtra("conversation_id");
                conversationTopic = intent.getStringExtra("conversation_topic");
                message = intent.getStringExtra(Message.ELEMENT);
                senderId = intent.getStringExtra("sender_id");
                creationTime = intent.getLongExtra("push_creation_date", 0);
                messageId = intent.getStringExtra("message_id");
                Logger.m173d("conversationId: %s, message: %s, messageId: %s, isError: %s, hidden: %s", conversationId, message, messageId, Boolean.valueOf(isError), Boolean.valueOf(isHidden));
                if (!TextUtils.isEmpty(conversationId)) {
                }
                if (isHidden) {
                    count = new AtomicReference();
                    if (isError ? ensureMessageLoaded(conversationId, messageId, count) : true) {
                        Integer countValue = (Integer) count.get();
                        prepareAndShowNotification(isError, conversationId, conversationTopic, messageId, message, senderId, countValue != null ? 0 : countValue.intValue());
                        return;
                    }
                    return;
                }
                BusMessagingHelper.loadNextPortion(conversationId, false, true, false);
            }
        }
        isError = true;
        conversationId = intent.getStringExtra("conversation_id");
        conversationTopic = intent.getStringExtra("conversation_topic");
        message = intent.getStringExtra(Message.ELEMENT);
        senderId = intent.getStringExtra("sender_id");
        creationTime = intent.getLongExtra("push_creation_date", 0);
        messageId = intent.getStringExtra("message_id");
        Logger.m173d("conversationId: %s, message: %s, messageId: %s, isError: %s, hidden: %s", conversationId, message, messageId, Boolean.valueOf(isError), Boolean.valueOf(isHidden));
        if (!TextUtils.isEmpty(conversationId)) {
            if (isHidden) {
                count = new AtomicReference();
                if (isError) {
                }
                if (isError ? ensureMessageLoaded(conversationId, messageId, count) : true) {
                    Integer countValue2 = (Integer) count.get();
                    if (countValue2 != null) {
                    }
                    prepareAndShowNotification(isError, conversationId, conversationTopic, messageId, message, senderId, countValue2 != null ? 0 : countValue2.intValue());
                    return;
                }
                return;
            }
            BusMessagingHelper.loadNextPortion(conversationId, false, true, false);
        }
    }

    private boolean isHiddenByTamTam() {
        String tamtamAuthority = Settings.getStrValueInvariable(this, "tamtam.settings.authority", "ru.ok.tamtam.settings");
        if (TextUtils.isEmpty(tamtamAuthority)) {
            return false;
        }
        Cursor cursor;
        try {
            cursor = getContentResolver().query(getTamTamSettingsUri(tamtamAuthority), null, null, null, null);
            if (cursor == null) {
                return false;
            }
            boolean z;
            do {
                if (cursor.moveToNext()) {
                } else {
                    cursor.close();
                    return false;
                }
            } while (!"okUserId".equals(cursor.getString(cursor.getColumnIndex("key"))));
            String value = cursor.getString(cursor.getColumnIndex("value"));
            Logger.m173d("TamTam user: %s, current user: %s", value, OdnoklassnikiApplication.getCurrentUser().uid);
            if (value == null || !TextUtils.equals(value, currentUserId)) {
                z = false;
            } else {
                z = true;
            }
            cursor.close();
            return z;
        } catch (Throwable e) {
            Logger.m178e(e);
            return false;
        } catch (Throwable th) {
            cursor.close();
        }
    }

    private Uri getTamTamSettingsUri(String tamtamAuthority) {
        if (this.tamtamSettingsUri == null) {
            this.tamtamSettingsUri = Uri.parse("content://" + tamtamAuthority + "/settings");
        }
        return this.tamtamSettingsUri;
    }

    static {
        PROJECTION_USER_PIC_NAME = new String[]{"user_avatar_url", "user_name"};
    }

    private void showNotification(String conversationId, String conversationTopic, String messageId, String userId, String userName, Bitmap avatar, String message, int unreadCount, boolean isError, NotificationType settings) {
        Intent notificationIntent = IntentUtils.createIntentForMessagesFragment(this, conversationId, isError);
        AppLaunchLog.fillPushConversation(notificationIntent);
        int iconId = isError ? 2130838516 : 2130838508;
        String title = "";
        String contentText = null;
        boolean isNotificationSimple = isNotificationSimple(settings, userName, message);
        if (!isNotificationSimple) {
            String startString;
            title = userName;
            String userWithSuffix = userName + ":";
            if (message.startsWith(userWithSuffix)) {
                startString = userWithSuffix;
            } else {
                startString = userName;
            }
            contentText = message.substring(startString.length()).trim();
        }
        if (isNotificationSimple) {
            title = LocalizationManager.getString((Context) this, isError ? 2131166201 : 2131166283);
        }
        if (TextUtils.isEmpty(contentText)) {
            contentText = message;
        }
        NotificationSignal signal = new NotificationSignal(getApplicationContext(), settings).setIntent(notificationIntent).setSmallIcon(iconId).setTitle(title).setTickerText(message).setContentText(contentText).setLargeIcon(avatar).setNotificationTag(conversationId).setNotificationId(isError ? 1 : 0).setPriority(1).setVisibility(0).setCategory(NotificationCompat.CATEGORY_MESSAGE).setCount(unreadCount).setConversationTopic(conversationTopic);
        if (!TextUtils.isEmpty(messageId)) {
            Context context = getBaseContext();
            signal.addAction(2130838146, 2131166461, PendingIntent.getActivity(context, messageId.hashCode(), AppLaunchLog.fillPushConversationQuickReply(MessageQuickReplyActivity.quickReplyIntent(context, conversationId, messageId, contentText, userId, userName)), 1073741824));
            signal.addAction(2130838145, 2131166064, PendingIntent.getService(context, messageId.hashCode(), MessagesService.markAsReadIntent(conversationId, messageId, context, true), 1073741824));
        }
        signal.performNotification();
    }

    private static boolean isNotificationSimple(NotificationType type, String userName, String message) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(message) || !message.startsWith(userName)) {
            return true;
        }
        return false;
    }
}
