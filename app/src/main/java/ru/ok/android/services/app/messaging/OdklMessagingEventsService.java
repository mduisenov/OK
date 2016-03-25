package ru.ok.android.services.app.messaging;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings.Secure;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.android.AndroidSmackInitializer;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.Builder;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jivesoftware.smackx.address.packet.MultipleAddresses;
import org.jivesoftware.smackx.address.packet.MultipleAddresses.Address;
import org.jivesoftware.smackx.caps.EntityCapsManager;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.MUCInitialPresence;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.proto.ConversationProto.Conversation.Type;
import ru.ok.android.services.app.NotifyReceiver;
import ru.ok.android.services.app.messaging.MessageReadExtensionElement.Provider;
import ru.ok.android.services.processors.xmpp.XmppSettingsContainer;
import ru.ok.android.services.processors.xmpp.XmppSettingsPreferences;
import ru.ok.android.ui.fragments.messages.adapter.IChatStateHandler;
import ru.ok.android.ui.fragments.messages.adapter.IChatStateProvider;
import ru.ok.android.ui.fragments.messages.helpers.ComposingUserInfo;
import ru.ok.android.ui.fragments.messages.helpers.DecodedChatId;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.java.api.utils.Utils;

public class OdklMessagingEventsService extends Service {
    private static Map<DecodedChatId, String> chatIdCacheByDecoded;
    private static Map<String, DecodedChatId> chatIdCacheByEncoded;
    private static volatile XMPPTCPConnection connection;
    private final int DELAY_BEFORE_COMPOSING_AUTOSTOP;
    private final long SERVICE_SHUTDOWN_PERIOD;
    Handler autostopComposingHandler;
    private int bindedActivitiesCounter;
    ConcurrentHashMap<Long, ArrayList<Long>> chatIdComposingUserIds;
    ChatManager chatManager;
    private ArrayList<IChatStateHandler> chatStateHandlers;
    private final Handler connectedStateHandler;
    private BroadcastReceiver connectedStateReceiver;
    MultiUserChatManager multiUserChatManager;
    private final IBinder odklEventsServiceBinder;
    String resourceId;
    private Handler stopServiceHandler;
    private ExecutorService threadPool;
    protected boolean wasAuthenticated;

    /* renamed from: ru.ok.android.services.app.messaging.OdklMessagingEventsService.1 */
    class C04231 extends Handler {
        C04231() {
        }

        public void handleMessage(Message msg) {
            OdklMessagingEventsService.this.connect();
        }
    }

    /* renamed from: ru.ok.android.services.app.messaging.OdklMessagingEventsService.2 */
    class C04242 implements Callback {
        C04242() {
        }

        public boolean handleMessage(Message msg) {
            if (OdklMessagingEventsService.this.bindedActivitiesCounter <= 0) {
                OdklMessagingEventsService.this.stopSelf();
            }
            return true;
        }
    }

    /* renamed from: ru.ok.android.services.app.messaging.OdklMessagingEventsService.3 */
    class C04253 extends XMPPTCPConnection {
        C04253(XMPPTCPConnectionConfiguration x0) {
            super(x0);
        }

        protected void setWasAuthenticated() {
            OdklMessagingEventsService.this.wasAuthenticated = true;
            super.setWasAuthenticated();
        }
    }

    /* renamed from: ru.ok.android.services.app.messaging.OdklMessagingEventsService.4 */
    class C04264 implements Callback {
        C04264() {
        }

        public boolean handleMessage(Message msg) {
            ComposingUserInfo composingUserInfo = msg.obj;
            Logger.m172d("composing state autostop");
            OdklMessagingEventsService.this.changeComposingUsers(composingUserInfo.chatId, composingUserInfo.composingUserId, false);
            return true;
        }
    }

    /* renamed from: ru.ok.android.services.app.messaging.OdklMessagingEventsService.5 */
    class C04275 implements StanzaListener {
        C04275() {
        }

        public void processPacket(Stanza packet) throws NotConnectedException {
            org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message) packet;
            DecodedChatAndSenderId decodedChatAndSenderId = OdklMessagingEventsService.this.parseChatDescription(message);
            if (decodedChatAndSenderId != null) {
                Logger.m172d("+XMPP MESSAGE : " + message.toString());
                if (((OdklMessagingEventsService.isXmppNewMessagePushEnabled() || OdklMessagingEventsService.isXmppMessageReadStatusPushEnabled()) && OdklMessagingEventsService.this.checkNewMessageOrReadStatus(decodedChatAndSenderId, message.getExtensions())) || !OdklMessagingEventsService.isXmppMessageComposingPushEnabled() || !OdklMessagingEventsService.this.checkChatState(decodedChatAndSenderId, message)) {
                }
            }
        }
    }

    /* renamed from: ru.ok.android.services.app.messaging.OdklMessagingEventsService.6 */
    class C04286 implements ConnectionListener {
        C04286() {
        }

        public void connected(XMPPConnection connection) {
            if (!OdklMessagingEventsService.this.wasAuthenticated) {
                try {
                    OdklMessagingEventsService.connection.login();
                } catch (Throwable e) {
                    Logger.m178e(e);
                }
            }
        }

        public void authenticated(XMPPConnection connection, boolean resumed) {
            OdklMessagingEventsService.this.mainLogic(connection);
        }

        public void connectionClosed() {
            Logger.m172d("connectionClosed");
        }

        public void connectionClosedOnError(Exception e) {
            Logger.m172d("connectionClosedOnError " + e.getMessage());
        }

        public void reconnectionSuccessful() {
            Logger.m172d("reconnectionSuccessful");
        }

        public void reconnectingIn(int seconds) {
            Logger.m172d("reconnectingIn");
        }

        public void reconnectionFailed(Exception e) {
            Logger.m172d("reconnectionFailed");
        }
    }

    /* renamed from: ru.ok.android.services.app.messaging.OdklMessagingEventsService.7 */
    class C04297 implements Runnable {
        C04297() {
        }

        public void run() {
            OdklMessagingEventsService.connection.disconnect();
            OdklMessagingEventsService.connection = null;
            OdklMessagingEventsService.this.threadPool.shutdown();
        }
    }

    /* renamed from: ru.ok.android.services.app.messaging.OdklMessagingEventsService.8 */
    class C04308 implements Runnable {
        C04308() {
        }

        public void run() {
            if (XmppSettingsPreferences.getXmppSettingsContainer(OdklMessagingEventsService.this.getApplicationContext()).isXmppEnabled) {
                OdklMessagingEventsService.this.initializeConnection();
                if (!OdklMessagingEventsService.connection.isConnected()) {
                    synchronized (OdklMessagingEventsService.this) {
                        if (!OdklMessagingEventsService.connection.isConnected()) {
                            try {
                                OdklMessagingEventsService.connection.connect();
                            } catch (Throwable e) {
                                Logger.m178e(e);
                            }
                        }
                    }
                }
            }
        }
    }

    private class ConnectedStateReceiver extends BroadcastReceiver {
        private Context serviceContext;

        public ConnectedStateReceiver(Context context) {
            this.serviceContext = context;
        }

        public void onReceive(Context context, Intent intent) {
            if (NetUtils.isConnectionAvailable(this.serviceContext, false)) {
                OdklMessagingEventsService.this.connectedStateHandler.sendEmptyMessage(0);
            }
        }
    }

    public class OdklMessagingEventsServiceBinder extends Binder implements IChatStateProvider {
        public void reportStateToServer(long chatId, Type conversationType, ChatState state) {
            if (OdklMessagingEventsService.isXmppMessageComposingPushEnabled()) {
                String from = OdklMessagingEventsService.connection.getUser();
                boolean isChat = conversationType == Type.CHAT;
                String to = String.valueOf(chatId) + "@" + (isChat ? "chat." : "") + "odnoklassniki.ru/" + OdklMessagingEventsService.this.resourceId;
                org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
                message.setStanzaId("msg-" + Base64.encode(UUID.randomUUID().toString()));
                message.setFrom(from);
                message.setTo(to);
                message.addExtension(new ChatStateExtension(state));
                message.setType(isChat ? org.jivesoftware.smack.packet.Message.Type.groupchat : org.jivesoftware.smack.packet.Message.Type.chat);
                try {
                    OdklMessagingEventsService.connection.sendStanza(message);
                } catch (NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void addChatStateHandler(IChatStateHandler chatStateHandler) {
            if (!OdklMessagingEventsService.this.chatStateHandlers.contains(chatStateHandler)) {
                OdklMessagingEventsService.this.chatStateHandlers.add(chatStateHandler);
                chatStateHandler.setChatStateProvider(this);
            }
        }

        public void removeChatStateHandler(IChatStateHandler chatStateHandler) {
            if (OdklMessagingEventsService.this.chatStateHandlers.contains(chatStateHandler)) {
                OdklMessagingEventsService.this.chatStateHandlers.remove(chatStateHandler);
                chatStateHandler.setChatStateProvider(null);
            }
        }

        public List<Long> getServerState(String encodedConversationId) {
            return (List) OdklMessagingEventsService.this.chatIdComposingUserIds.get(Long.valueOf(OdklMessagingEventsService.decodeChatId(encodedConversationId).chatId));
        }
    }

    public OdklMessagingEventsService() {
        this.bindedActivitiesCounter = 0;
        this.SERVICE_SHUTDOWN_PERIOD = 60000;
        this.wasAuthenticated = false;
        this.connectedStateHandler = new C04231();
        this.DELAY_BEFORE_COMPOSING_AUTOSTOP = 15000;
        this.chatIdComposingUserIds = new ConcurrentHashMap();
        this.chatStateHandlers = new ArrayList();
        this.autostopComposingHandler = new Handler(new C04264());
        this.odklEventsServiceBinder = new OdklMessagingEventsServiceBinder();
    }

    public static void bindToMe(Activity activity, ServiceConnection connection) {
        activity.startService(new Intent(activity, OdklMessagingEventsService.class));
        activity.bindService(new Intent(activity, OdklMessagingEventsService.class), connection, 1);
    }

    public void onCreate() {
        super.onCreate();
        this.threadPool = Executors.newSingleThreadExecutor();
        this.resourceId = "OK-" + OdklSaslMechanism.md5(Secure.getString(getContentResolver(), "android_id"));
        this.stopServiceHandler = new Handler(new C04242());
        connect();
        if (this.connectedStateReceiver == null) {
            Context context = getApplication().getApplicationContext();
            BroadcastReceiver connectedStateReceiver = new ConnectedStateReceiver(context);
            this.connectedStateReceiver = connectedStateReceiver;
            context.registerReceiver(connectedStateReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }

    private void initializeConnection() {
        if (connection == null) {
            new AndroidSmackInitializer().initialize();
            SASLAuthentication.registerSASLMechanism(new OdklSaslMechanism());
            connection = new C04253(((Builder) ((Builder) ((Builder) ((Builder) ((Builder) ((Builder) XMPPTCPConnectionConfiguration.builder().setServiceName("xmpp.odnoklassniki.ru")).setResource(this.resourceId)).allowEmptyOrNullUsernames()).setHost(ConfigurationPreferences.getInstance().getXmppServer())).setPort(5222)).setSecurityMode(SecurityMode.required)).setCompressionEnabled(false).build());
            connection.setPacketReplyTimeout(10000);
            ServiceDiscoveryManager discoveryManager = ServiceDiscoveryManager.getInstanceFor(connection);
            discoveryManager.addIdentity(new Identity("handheld", "OK for Android", "client"));
            discoveryManager.addFeature(EntityCapsManager.NAMESPACE);
            discoveryManager.addFeature(DiscoverInfo.NAMESPACE);
            discoveryManager.addFeature("http://jabber.org/protocol/disco#items");
            discoveryManager.addFeature(MUCInitialPresence.NAMESPACE);
            discoveryManager.addFeature(MUCUser.NAMESPACE);
            discoveryManager.addFeature(ChatStateExtension.NAMESPACE);
            ProviderManager.addExtensionProvider("messageread", "http://ok.ru/messageread", new Provider());
            ProviderManager.addExtensionProvider("newmessage", "http://ok.ru/newmessage", new NewMessageExtensionElement.Provider());
            ProviderManager.addExtensionProvider("updatechat", "http://ok.ru/updatechat", new UpdateChatExtensionElement.Provider());
            EntityCapsManager.getInstanceFor(connection).enableEntityCaps();
            addConnectionListeners();
        }
    }

    private void changeComposingUsers(long chatIdDecoded, long userIdDecoded, boolean isComposing) {
        Long userIdEncoded = Long.valueOf(Utils.xorId(userIdDecoded));
        boolean hasChanged = false;
        ArrayList<Long> composingUserIds = (ArrayList) this.chatIdComposingUserIds.get(Long.valueOf(chatIdDecoded));
        if (isComposing) {
            if (composingUserIds == null) {
                composingUserIds = new ArrayList();
                composingUserIds.add(userIdEncoded);
                this.chatIdComposingUserIds.put(Long.valueOf(chatIdDecoded), composingUserIds);
                hasChanged = true;
            } else if (!composingUserIds.contains(userIdEncoded)) {
                composingUserIds.add(userIdEncoded);
                hasChanged = true;
            }
        } else if (this.chatIdComposingUserIds.containsKey(Long.valueOf(chatIdDecoded))) {
            hasChanged = composingUserIds.remove(userIdEncoded);
        }
        if (hasChanged) {
            Iterator i$ = this.chatStateHandlers.iterator();
            while (i$.hasNext()) {
                IChatStateHandler chatStateHandler = (IChatStateHandler) i$.next();
                if (isComposing) {
                    chatStateHandler.notifyComposing(chatIdDecoded, userIdEncoded.longValue());
                } else {
                    chatStateHandler.notifyPaused(chatIdDecoded, userIdEncoded.longValue());
                }
            }
        }
    }

    private void addConnectionListeners() {
        connection.addAsyncStanzaListener(new C04275(), new StanzaTypeFilter(org.jivesoftware.smack.packet.Message.class));
        connection.addConnectionListener(new C04286());
    }

    private DecodedChatAndSenderId parseChatDescription(org.jivesoftware.smack.packet.Message message) {
        if (message == null || message.getFrom() == null || message.getTo() == null) {
            return null;
        }
        int indexOfAtInGetFrom = message.getFrom().indexOf(64);
        int indexOfAtInGetTo = message.getTo().indexOf(64);
        if (indexOfAtInGetFrom <= 0 || indexOfAtInGetTo <= 0) {
            Logger.m184w("error in message stanza from=" + message.getFrom() + " to " + message.getTo());
            return null;
        }
        try {
            long chatId = Long.parseLong(message.getFrom().substring(0, indexOfAtInGetFrom));
            long fromUserId = chatId;
            if (message.getType() == org.jivesoftware.smack.packet.Message.Type.chat) {
                return new DecodedChatAndSenderId(chatId, fromUserId, false);
            }
            if (message.getType() != org.jivesoftware.smack.packet.Message.Type.groupchat) {
                return null;
            }
            ExtensionElement adressesExtensionElement = message.getExtension(MultipleAddresses.NAMESPACE);
            if (adressesExtensionElement != null) {
                MultipleAddresses multipleAddresses = adressesExtensionElement instanceof MultipleAddresses ? (MultipleAddresses) adressesExtensionElement : null;
                if (multipleAddresses != null) {
                    List<Address> addresses = multipleAddresses.getAddressesOfType(MultipleAddresses.Type.ofrom);
                    if (!(addresses == null || addresses.isEmpty())) {
                        String jid = ((Address) addresses.get(0)).getJid();
                        int indexOfAtInJid = jid.indexOf(64);
                        if (indexOfAtInJid > 0) {
                            try {
                                fromUserId = Long.parseLong(jid.substring(0, indexOfAtInJid));
                            } catch (Throwable e) {
                                Logger.m178e(e);
                                return null;
                            }
                        }
                    }
                }
            }
            ExtensionElement extensionElement = message.getExtension("newmessage", "http://ok.ru/newmessage");
            if (extensionElement instanceof NewMessageExtensionElement) {
                fromUserId = ((NewMessageExtensionElement) extensionElement).userId;
            }
            extensionElement = message.getExtension("messageread", "http://ok.ru/messageread");
            if (extensionElement instanceof MessageReadExtensionElement) {
                fromUserId = ((MessageReadExtensionElement) extensionElement).userId;
            }
            return new DecodedChatAndSenderId(chatId, fromUserId, true);
        } catch (Throwable e2) {
            Logger.m178e(e2);
            return null;
        }
    }

    private boolean checkChatState(DecodedChatAndSenderId decodedChatAndSenderId, org.jivesoftware.smack.packet.Message message) {
        ExtensionElement chatStateElement = message.getExtension(ChatStateExtension.NAMESPACE);
        if (chatStateElement == null) {
            return false;
        }
        ChatStateExtension chatStateExtension = chatStateElement instanceof ChatStateExtension ? (ChatStateExtension) chatStateElement : null;
        if (chatStateExtension == null) {
            return false;
        }
        ChatState chatState = chatStateExtension.getChatState();
        if (chatState == null) {
            Logger.m184w("composing state UNKNOWN");
            return false;
        }
        long chatIdDecoded = decodedChatAndSenderId.chatId;
        long composingUserIdDecoded = decodedChatAndSenderId.senderId;
        if (chatState == ChatState.composing) {
            changeComposingUsers(chatIdDecoded, composingUserIdDecoded, true);
            sendComposingAutostopMessage(chatIdDecoded, composingUserIdDecoded);
            return true;
        } else if (chatState != ChatState.paused && chatState != ChatState.inactive && chatState != ChatState.gone) {
            return false;
        } else {
            removeComposingAutostopMessage(chatIdDecoded, composingUserIdDecoded);
            changeComposingUsers(chatIdDecoded, composingUserIdDecoded, false);
            return true;
        }
    }

    private boolean checkNewMessageOrReadStatus(DecodedChatAndSenderId decodedChatAndSenderId, List<ExtensionElement> extensions) {
        for (ExtensionElement extensionElement : extensions) {
            if (isXmppNewMessagePushEnabled() && (extensionElement instanceof NewMessageExtensionElement)) {
                NotifyReceiver.updateUiForConversationOnNewMessage(getApplicationContext(), decodedChatAndSenderId);
                return true;
            } else if (isXmppNewMessagePushEnabled() && (extensionElement instanceof UpdateChatExtensionElement)) {
                NotifyReceiver.updateUiForConversation(getApplicationContext(), decodedChatAndSenderId);
                return true;
            } else if (isXmppMessageReadStatusPushEnabled() && (extensionElement instanceof MessageReadExtensionElement)) {
                MessageReadExtensionElement messageReadExtensionElement = (MessageReadExtensionElement) extensionElement;
                NotifyReceiver.updateDecodedChatReadStatusXmpp(getApplicationContext(), new DecodedChatId(decodedChatAndSenderId.chatId, decodedChatAndSenderId.isMultichat), String.valueOf(messageReadExtensionElement.userId), messageReadExtensionElement.timestamp);
                return true;
            }
        }
        return false;
    }

    private void sendComposingAutostopMessage(long chatIdDecoded, long composingUserIdDecoded) {
        int messageWhat = removeComposingAutostopMessage(chatIdDecoded, composingUserIdDecoded);
        ComposingUserInfo composingUserInfo = new ComposingUserInfo(chatIdDecoded, composingUserIdDecoded);
        Message autostopMessage = this.autostopComposingHandler.obtainMessage();
        autostopMessage.what = messageWhat;
        autostopMessage.obj = composingUserInfo;
        this.autostopComposingHandler.sendMessageDelayed(autostopMessage, 15000);
    }

    private int removeComposingAutostopMessage(long chatIdDecoded, long composingUserIdDecoded) {
        int messageWhat = (int) (chatIdDecoded ^ composingUserIdDecoded);
        this.autostopComposingHandler.removeMessages(messageWhat);
        return messageWhat;
    }

    private void mainLogic(XMPPConnection connection) {
        try {
            connection.sendStanza(new Presence(Presence.Type.available));
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
        this.chatManager = ChatManager.getInstanceFor(connection);
        this.multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.connectedStateReceiver != null) {
            getApplication().getApplicationContext().unregisterReceiver(this.connectedStateReceiver);
        }
        disconnect();
        this.stopServiceHandler.removeMessages(0);
    }

    private void disconnect() {
        if (connection != null && connection.isConnected()) {
            this.threadPool.execute(new C04297());
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return 2;
    }

    public IBinder onBind(Intent intent) {
        this.bindedActivitiesCounter++;
        this.stopServiceHandler.removeMessages(0);
        return this.odklEventsServiceBinder;
    }

    private void connect() {
        this.threadPool.execute(new C04308());
    }

    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        this.bindedActivitiesCounter--;
        this.stopServiceHandler.sendEmptyMessageDelayed(0, 60000);
        return false;
    }

    static {
        chatIdCacheByEncoded = new ConcurrentHashMap();
        chatIdCacheByDecoded = new ConcurrentHashMap();
    }

    public static DecodedChatId decodeChatId(String encodedApiChatUid) {
        if (encodedApiChatUid == null) {
            return null;
        }
        DecodedChatId decodedChatIdFromCache = (DecodedChatId) chatIdCacheByEncoded.get(encodedApiChatUid);
        if (decodedChatIdFromCache != null) {
            return decodedChatIdFromCache;
        }
        DecodedChatId result = null;
        String decodedIdContainer = new String(android.util.Base64.decode(encodedApiChatUid, 0));
        if (decodedIdContainer.startsWith("CHAT")) {
            result = new DecodedChatId(Utils.xorId(decodedIdContainer.substring(decodedIdContainer.indexOf(":") + 1, decodedIdContainer.lastIndexOf(":"))), true);
        } else if (decodedIdContainer.startsWith("PRIVATE")) {
            result = new DecodedChatId(Utils.xorId(decodedIdContainer.substring(decodedIdContainer.indexOf(":") + 1)), false);
        }
        chatIdCacheByEncoded.put(encodedApiChatUid, result);
        chatIdCacheByDecoded.put(result, encodedApiChatUid);
        return result;
    }

    public static String getExistingEncodedChatId(DecodedChatId decodedChatId, List<String> encodedChatIds) {
        String result = (String) chatIdCacheByDecoded.get(decodedChatId);
        if (result == null) {
            for (String encodedChatId : encodedChatIds) {
                DecodedChatId currentDecodedChatId = decodeChatId(encodedChatId);
                if (currentDecodedChatId != null && currentDecodedChatId.equals(decodedChatId)) {
                    chatIdCacheByEncoded.put(encodedChatId, decodedChatId);
                    chatIdCacheByDecoded.put(decodedChatId, encodedChatId);
                    return encodedChatId;
                }
            }
        }
        return result;
    }

    private static XmppSettingsContainer getXmmpSettings() {
        return XmppSettingsPreferences.getXmppSettingsContainer(OdnoklassnikiApplication.getContext());
    }

    private static boolean isXmmpConnectionOk(XmppSettingsContainer xmppSettingsContainer) {
        return connection != null && connection.isConnected() && connection.isAuthenticated() && xmppSettingsContainer.isXmppEnabled;
    }

    public static boolean isXmppMessageComposingPushEnabled() {
        XmppSettingsContainer xmppSettingsContainer = getXmmpSettings();
        return isXmmpConnectionOk(xmppSettingsContainer) && xmppSettingsContainer.isPushFeatureComposingEnabled;
    }

    public static boolean isXmppNewMessagePushEnabled() {
        XmppSettingsContainer xmppSettingsContainer = getXmmpSettings();
        return isXmmpConnectionOk(xmppSettingsContainer) && xmppSettingsContainer.isPushFeatureNewMessageEnabled;
    }

    public static boolean isXmppMessageReadStatusPushEnabled() {
        XmppSettingsContainer xmppSettingsContainer = getXmmpSettings();
        return isXmmpConnectionOk(xmppSettingsContainer) && xmppSettingsContainer.isPushFeatureMessageReadEnabled;
    }
}
