package ru.ok.android.ui.messaging.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.content.TwoSourcesDataLoader.Result;
import android.support.v4.content.TwoSourcesDataLoaderHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.helper.ServiceHelper;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.app.IntentUtils;
import ru.ok.android.services.app.NotifyReceiver;
import ru.ok.android.services.app.messaging.OdklMessagingEventsService;
import ru.ok.android.services.app.messaging.OdklMessagingEventsService.OdklMessagingEventsServiceBinder;
import ru.ok.android.services.app.notification.NotificationSignal;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.main.OdklActivity;
import ru.ok.android.ui.adapters.ScrollLoadRecyclerViewBlocker;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.scroll.ScrollListenerRecyclerSet;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.ui.dialogs.DeleteConversationDialog;
import ru.ok.android.ui.dialogs.actions.ConversationDoActionBox;
import ru.ok.android.ui.dialogs.actions.ConversationDoActionBox.ConversationSelectListener;
import ru.ok.android.ui.fragments.base.BaseRefreshFragment;
import ru.ok.android.ui.fragments.messages.adapter.ConversationsAdapter;
import ru.ok.android.ui.fragments.messages.adapter.ConversationsAdapter.ConversationsAdapterListener;
import ru.ok.android.ui.fragments.messages.helpers.ConversationParticipantsUtils;
import ru.ok.android.ui.messaging.data.ConversationsData;
import ru.ok.android.ui.messaging.data.ConversationsLoader;
import ru.ok.android.ui.messaging.drawable.CloverImageViewBitmapRenderer;
import ru.ok.android.ui.messaging.drawable.CloverImageViewBitmapRenderer.IRenderCloverImageViewToBitmapCallback;
import ru.ok.android.ui.users.fragments.UsersByIdFragment;
import ru.ok.android.ui.utils.DividerItemDecorator;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.bus.BusMessagingHelper;
import ru.ok.model.UserInfo;

public final class ConversationsFragment extends BaseRefreshFragment implements LoaderCallbacks<Result<ConversationsData>>, ConversationSelectListener, ConversationsAdapterListener {
    private ConversationsAdapter adapter;
    private boolean doShowSelection;
    private SmartEmptyViewAnimated emptyView;
    private boolean isReceiverRegistered;
    private RecyclerView list;
    private ConversationsFragmentListener listener;
    private ScrollListenerRecyclerSet listeners;
    private ConversationsLoader loader;
    private TwoSourcesDataLoaderHelper loaderHelper;
    private MessagingPromptController messagingPromptController;
    private View networkStatusView;
    private NewMessageAndReadStatusLocalBroadcastReceiver newMessageAndReadStatusLocalBroadcastReceiver;
    private IntentFilter notificationsIntentFilter;
    private BroadcastReceiver notificationsReceiver;
    OdklMessagingEventsServiceBinder odklMessagingEventsServiceBinder;
    private ServiceConnection serviceConnection;
    private final Handler updateTimesHandler;

    public interface ConversationsFragmentListener {
        void onConversationSelected(String str, String str2);

        void onListCrated(RecyclerView recyclerView);

        void onUpdatedConversationsCounter(int i);
    }

    /* renamed from: ru.ok.android.ui.messaging.fragments.ConversationsFragment.1 */
    class C10451 extends Handler {
        C10451() {
        }

        public void handleMessage(Message msg) {
            ConversationsFragment.this.adapter.notifyDataSetChanged();
            ConversationsFragment.this.updateTimesHandler.sendEmptyMessageDelayed(0, 60000);
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.fragments.ConversationsFragment.2 */
    class C10462 implements OnStubButtonClickListener {
        C10462() {
        }

        public void onStubButtonClick(Type type) {
            ConversationsFragment.this.onRefresh();
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.fragments.ConversationsFragment.3 */
    class C10473 implements Runnable {
        final /* synthetic */ Activity val$activity;

        C10473(Activity activity) {
            this.val$activity = activity;
        }

        public void run() {
            NavigationHelper.showMessagesForUser(this.val$activity, null);
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.fragments.ConversationsFragment.4 */
    class C10484 implements Runnable {
        final /* synthetic */ ConversationsData val$data;

        C10484(ConversationsData conversationsData) {
            this.val$data = conversationsData;
        }

        public void run() {
            for (int i = 0; i < Math.min(this.val$data.conversations.size(), 3); i++) {
                Logger.m173d("Prefetch conversation: %d - %s", Integer.valueOf(i), ((Conversation) this.val$data.conversations.get(i)).getId());
                MessagesCache.getInstance().prefetch(conversationId, 50);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.fragments.ConversationsFragment.5 */
    class C10495 implements ServiceConnection {
        C10495() {
        }

        public void onServiceConnected(ComponentName className, IBinder binder) {
            ConversationsFragment.this.odklMessagingEventsServiceBinder = (OdklMessagingEventsServiceBinder) binder;
            if (ConversationsFragment.this.adapter != null) {
                ConversationsFragment.this.odklMessagingEventsServiceBinder.addChatStateHandler(ConversationsFragment.this.adapter);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (ConversationsFragment.this.adapter != null) {
                ConversationsFragment.this.odklMessagingEventsServiceBinder.removeChatStateHandler(ConversationsFragment.this.adapter);
            }
            ConversationsFragment.this.odklMessagingEventsServiceBinder = null;
        }
    }

    /* renamed from: ru.ok.android.ui.messaging.fragments.ConversationsFragment.6 */
    static class C10506 implements IRenderCloverImageViewToBitmapCallback {
        final /* synthetic */ Context val$context;
        final /* synthetic */ Conversation val$conversation;
        final /* synthetic */ String val$source;

        C10506(Context context, Conversation conversation, String str) {
            this.val$context = context;
            this.val$conversation = conversation;
            this.val$source = str;
        }

        public void run(Bitmap bitmap) {
            IntentUtils.installShortcut(this.val$context, this.val$conversation.getBuiltTopic(), bitmap, NavigationHelper.smartLaunchMessagesIntent(this.val$context, this.val$conversation.getId()));
            StatisticManager.getInstance().addStatisticEvent("conversation-shortcut-added", new Pair("source", this.val$source));
        }
    }

    private class ConversationsReceiver extends BroadcastReceiver {
        private ConversationsReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (NotifyReceiver.isNotificationForConversation(intent) && !tryAbortNewMessageBroadcast(intent)) {
                ConversationsFragment.this.loadNewMessages(context, intent, this, true);
            }
        }

        private boolean tryAbortNewMessageBroadcast(Intent intent) {
            if (!ConversationsFragment.this.isFragmentVisible() || !OdklMessagingEventsService.isXmppNewMessagePushEnabled() || NotifyReceiver.isNotificationForMessageServerError(intent)) {
                return false;
            }
            abortBroadcast();
            Logger.m172d("Skipped chat list update notification due to enabled new message XMPP push");
            return true;
        }
    }

    private class NewMessageAndReadStatusLocalBroadcastReceiver extends BroadcastReceiver {
        private NewMessageAndReadStatusLocalBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (ConversationsFragment.this.getActivity() != null) {
                String action = intent.getAction();
                if (NotifyReceiver.isNotificationForConversation(intent)) {
                    if (TextUtils.equals("CHAT_LAST_VIEW_TIME_CHANGED", action) || TextUtils.equals("CHAT_STATE_UPDATED_XMPP", action)) {
                        ConversationsFragment.this.loadNewMessages(context, intent, this, false);
                    } else if (TextUtils.equals("CHAT_NEW_MESSAGE_ARRIVED_XMPP", action)) {
                        ConversationsFragment.this.loadNewMessages(context, intent, this, true);
                    }
                } else if ("CHAT_NEW_MESSAGE_ARRIVED_XMPP".equals(action)) {
                    ConversationsFragment.this.loaderHelper.startLoader(true, false);
                }
            }
        }
    }

    public ConversationsFragment() {
        this.updateTimesHandler = new C10451();
        this.serviceConnection = new C10495();
    }

    public static ConversationsFragment newInstance(boolean doShowSelection, String userId, String conversationId, boolean disableSomeConversations) {
        Bundle args = new Bundle();
        args.putBoolean("show_selection", doShowSelection);
        args.putBoolean("disable_some_conversations", disableSomeConversations);
        args.putString("user_id", userId);
        args.putString("conversation_id", conversationId);
        ConversationsFragment fragment = new ConversationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String getSelectedUserId() {
        return getArguments().getString("user_id");
    }

    private String getSelectedConversationId() {
        return getArguments().getString("conversation_id");
    }

    private boolean isDisableSomeConversations() {
        return getArguments().getBoolean("disable_some_conversations");
    }

    public void setListener(ConversationsFragmentListener listener) {
        this.listener = listener;
    }

    protected void notifyOnConversationSelected(String conversationId, String userId) {
        ConversationsFragmentListener listener = this.listener;
        if (listener != null) {
            listener.onConversationSelected(conversationId, userId);
        }
    }

    protected void notifyOnUpdatedConversationsCounter(int counter) {
        ConversationsFragmentListener listener = this.listener;
        if (listener != null) {
            listener.onUpdatedConversationsCounter(counter);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.doShowSelection = getArguments().getBoolean("show_selection", false);
        View view = inflater.inflate(getLayoutId(), container, false);
        Context context = getActivity();
        if (!(this.odklMessagingEventsServiceBinder == null || this.adapter == null)) {
            this.odklMessagingEventsServiceBinder.addChatStateHandler(this.adapter);
        }
        this.list = (RecyclerView) view.findViewById(2131624731);
        ScrollLoadRecyclerViewBlocker imageLoadBlocker = ScrollLoadRecyclerViewBlocker.forIdleAndTouchIdle();
        this.adapter = new ConversationsAdapter(context, isDisableSomeConversations(), this);
        this.list.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.list.addItemDecoration(new DividerItemDecorator(context));
        this.list.setAdapter(this.adapter);
        this.listeners = new ScrollListenerRecyclerSet();
        this.listeners.addListener(imageLoadBlocker);
        this.list.setOnScrollListener(this.listeners);
        this.networkStatusView = view.findViewById(2131624730);
        this.emptyView = (SmartEmptyViewAnimated) view.findViewById(C0263R.id.empty_view);
        this.emptyView.setButtonClickListener(new C10462());
        setSelectedUser(getSelectedUserId());
        setSelectedConversation(getSelectedConversationId());
        ConversationsFragmentListener listener = this.listener;
        if (listener != null) {
            listener.onListCrated(this.list);
        }
        return view;
    }

    public void onResume() {
        super.onResume();
        if (isVisible()) {
            onShowFragment();
        }
    }

    public void onPause() {
        super.onPause();
        if (isVisible()) {
            onHideFragment();
        }
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.messagingPromptController = new MessagingPromptController(getActivity());
    }

    protected void onHideFragment() {
        this.updateTimesHandler.removeMessages(0);
        unregisterReceiver();
        if (this.loader != null) {
            this.loader.setInvisible(true);
        }
        if (isFragmentVisible()) {
            this.messagingPromptController.hide();
        }
    }

    protected void onShowFragment() {
        registerReceiver();
        this.updateTimesHandler.removeMessages(0);
        this.updateTimesHandler.sendEmptyMessageDelayed(0, 60000);
        if (this.loader != null) {
            this.loader.setInvisible(false);
        }
        if (isFragmentVisible()) {
            this.messagingPromptController.showIfNeeded();
        }
    }

    protected ServiceHelper getServiceHelper() {
        return Utils.getServiceHelper();
    }

    private void registerReceiver() {
        if (!this.isReceiverRegistered) {
            if (this.notificationsIntentFilter == null) {
                this.notificationsIntentFilter = new IntentFilter("ru.ok.android.action.NOTIFY");
                this.notificationsIntentFilter.setPriority(1);
            }
            if (this.notificationsReceiver == null) {
                this.notificationsReceiver = new ConversationsReceiver();
            }
            getContext().registerReceiver(this.notificationsReceiver, this.notificationsIntentFilter);
            this.isReceiverRegistered = true;
        }
        if (this.newMessageAndReadStatusLocalBroadcastReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("CHAT_LAST_VIEW_TIME_CHANGED");
            intentFilter.addAction("CHAT_NEW_MESSAGE_ARRIVED_XMPP");
            intentFilter.addAction("CHAT_STATE_UPDATED_XMPP");
            intentFilter.setPriority(1);
            Context context = getContext();
            BroadcastReceiver newMessageAndReadStatusLocalBroadcastReceiver = new NewMessageAndReadStatusLocalBroadcastReceiver();
            this.newMessageAndReadStatusLocalBroadcastReceiver = newMessageAndReadStatusLocalBroadcastReceiver;
            context.registerReceiver(newMessageAndReadStatusLocalBroadcastReceiver, intentFilter);
        }
    }

    private void unregisterReceiver() {
        if (this.isReceiverRegistered) {
            getContext().unregisterReceiver(this.notificationsReceiver);
            this.isReceiverRegistered = false;
        }
        if (this.newMessageAndReadStatusLocalBroadcastReceiver != null) {
            getContext().unregisterReceiver(this.newMessageAndReadStatusLocalBroadcastReceiver);
            this.newMessageAndReadStatusLocalBroadcastReceiver = null;
        }
    }

    private void loadNewMessages(Context context, Intent intent, BroadcastReceiver broadcastReceiver, boolean shouldNotify) {
        onRefresh();
        if (isFragmentVisible()) {
            if (shouldNotify) {
                NotificationSignal.notifyWithTypeNoNotification(context, NotifyReceiver.getNotificationsSettings(context));
            }
            broadcastReceiver.abortBroadcast();
            BusMessagingHelper.loadNextPortion(intent.getStringExtra("conversation_id"), false, true, true);
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.loaderHelper = new TwoSourcesDataLoaderHelper(this.emptyView, getLoaderManager(), 0, this, this.refreshProvider, false);
        this.loaderHelper.setEmptyViewType(Type.CONVERSATIONS_LIST);
        this.loaderHelper.startLoader(false, true);
    }

    protected int getLayoutId() {
        return 2130903137;
    }

    public void onRefresh() {
        this.emptyView.setState(State.LOADING);
        this.loaderHelper.startLoader(true, false);
    }

    public Loader<Result<ConversationsData>> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case RECEIVED_VALUE:
                Loader conversationsLoader = new ConversationsLoader(getActivity(), this.loaderHelper.isPerformWebLoading(bundle));
                this.loader = conversationsLoader;
                return conversationsLoader;
            default:
                throw new IllegalArgumentException("Don't know " + loaderId + " loader id");
        }
    }

    public void onLoadFinished(Loader<Result<ConversationsData>> loader, @Nullable Result<ConversationsData> data) {
        switch (loader.getId()) {
            case RECEIVED_VALUE:
                prefetchMessages((ConversationsData) data.loadedData);
                this.adapter.updateData((ConversationsData) data.loadedData);
                this.loaderHelper.onLoadFinished(loader, data);
                SmartEmptyViewAnimated smartEmptyViewAnimated = this.emptyView;
                int i = (data == null || ((ConversationsData) data.loadedData).conversations.isEmpty()) ? 0 : 8;
                smartEmptyViewAnimated.setVisibility(i);
                if (data != null) {
                    notifyOnUpdatedConversationsCounter(((ConversationsData) data.loadedData).totalUnreadCount);
                }
                String conversationId = this.adapter.getSelectionConversationId();
                String userId = this.adapter.getSelectionUserId();
                if (TextUtils.isEmpty(conversationId) && TextUtils.isEmpty(userId)) {
                    Activity activity = getActivity();
                    if (activity instanceof OdklActivity) {
                        getView().post(new C10473(activity));
                    }
                }
                updateNetworkStatusViewVisibility();
            default:
                throw new IllegalArgumentException("Don't know " + loader.getId() + " loader id");
        }
    }

    private void prefetchMessages(ConversationsData data) {
        if (data != null && !data.conversations.isEmpty()) {
            GlobalBus.post(new C10484(data), 2131623945);
        }
    }

    private void updateNetworkStatusViewVisibility() {
        boolean hasSelection;
        int i = 0;
        if (this.adapter == null || (TextUtils.isEmpty(this.adapter.getSelectionConversationId()) && TextUtils.isEmpty(this.adapter.getSelectionUserId()))) {
            hasSelection = false;
        } else {
            hasSelection = true;
        }
        View view = this.networkStatusView;
        if (hasSelection || this.emptyView.getVisibility() == 0) {
            i = 8;
        }
        view.setVisibility(i);
    }

    public void onLoaderReset(Loader<Result<ConversationsData>> loader) {
    }

    public void onConversationAvatarClicked(Conversation conversation) {
        boolean z = true;
        if (conversation.getParticipantsCount() > 0) {
            if (conversation.getType() != Conversation.Type.CHAT) {
                NavigationHelper.showUserInfo(getActivity(), ConversationParticipantsUtils.findNonCurrentUserIdProto(conversation.getParticipantsList()));
            } else {
                ArrayList<String> userIds = ConversationParticipantsUtils.toIdsWithoutCurrentProto(conversation.getParticipantsList());
                if (userIds.isEmpty()) {
                    NavigationHelper.showUserInfo(getActivity(), ConversationParticipantsUtils.findNonCurrentUserIdProto(conversation.getParticipantsList()));
                } else {
                    UsersByIdFragment fragment = UsersByIdFragment.newInstance(userIds, 2131165635, false);
                    fragment.setTargetFragment(this, 2);
                    fragment.show(getFragmentManager(), "users-list");
                }
            }
            StatisticManager instance = StatisticManager.getInstance();
            String str = "conversations-avatar-clicked";
            Pair[] pairArr = new Pair[1];
            String str2 = "isMultichat";
            if (conversation.getType() != Conversation.Type.CHAT) {
                z = false;
            }
            pairArr[0] = new Pair(str2, String.valueOf(z));
            instance.addStatisticEvent(str, pairArr);
        }
    }

    public void onConversationContextMenuButtonClicked(Conversation conversation, View anchorView) {
        if (conversation != null) {
            showConversationContextMenu(conversation, anchorView);
        }
    }

    public void onConversationSelected(Conversation conversation, int index) {
        String conversationId = conversation.getId();
        setSelectedConversation(conversationId);
        StatisticManager instance = StatisticManager.getInstance();
        String str = "conversation-selected";
        Pair[] pairArr = new Pair[2];
        pairArr[0] = new Pair("has-unread", conversation.getNewMessagesCount() > 0 ? "true" : "false");
        pairArr[1] = new Pair("index", String.valueOf(index));
        instance.addStatisticEvent(str, pairArr);
        notifyOnConversationSelected(conversationId, ConversationParticipantsUtils.findNonCurrentUserIdProto(conversation.getParticipantsList()));
    }

    private void showConversationContextMenu(Conversation conversation, View anchor) {
        ConversationDoActionBox doBox = new ConversationDoActionBox(anchor.getContext(), conversation, anchor);
        doBox.setConversationSelectListener(this);
        doBox.show();
    }

    public void setSelectedConversation(String conversationId) {
        if (this.doShowSelection) {
            this.adapter.setSelectionConversationId(conversationId);
            updateNetworkStatusViewVisibility();
        }
    }

    public void clearSelection() {
        if (this.doShowSelection) {
            this.adapter.setSelectionConversationId(null);
            this.adapter.setSelectionUserId(null);
            updateNetworkStatusViewVisibility();
        }
    }

    public void setSelectedUser(String userId) {
        if (this.doShowSelection) {
            this.adapter.setSelectionUserId(userId);
            updateNetworkStatusViewVisibility();
        }
    }

    public void onDeleteConversationSelect(Conversation conversation, View view) {
        DeleteConversationDialog dialog = DeleteConversationDialog.newInstance(conversation.getId(), conversation.getBuiltTopic());
        dialog.setTargetFragment(this, 1);
        dialog.show(getFragmentManager(), "delete-conversation-dialog");
        StatisticManager.getInstance().addStatisticEvent("conversations-delete", new Pair[0]);
    }

    public void onLeaveSelected(Conversation conversation, View view) {
        BusMessagingHelper.leaveChat(conversation.getId());
        StatisticManager.getInstance().addStatisticEvent("conversations-leave-chat", new Pair[0]);
    }

    public void onCallSelect(String userId, View view) {
        Activity activity = getActivity();
        if (activity != null) {
            StatisticManager.getInstance().addStatisticEvent("conversations-call-user", new Pair[0]);
            NavigationHelper.onCallUser(activity, userId);
        }
    }

    public void onShortcutSelected(@NonNull Conversation conversation, @NonNull View anchor) {
        if (getActivity() != null) {
            installConversationShortcut(getActivity(), conversation, this.adapter.getUsers4Conversation(conversation), "conversaions");
        }
    }

    @Subscribe(on = 2131623946, to = 2131624138)
    public void onConversationDeleted(BusEvent event) {
        if (isFragmentVisible()) {
            Context activity = getActivity();
            if (activity != null) {
                if (event.resultCode != -1) {
                    ErrorType errorType = ErrorType.from(event.bundleOutput);
                    int msgId = errorType == ErrorType.GENERAL ? 2131165676 : errorType.getDefaultErrorMessage();
                    if (msgId > 0) {
                        TimeToast.show(activity, msgId, 0);
                    }
                }
                activity.setProgressBarIndeterminateVisibility(false);
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624211)
    public void onUnreadCountChanged(BusEvent event) {
        if (getActivity() != null && !isFragmentVisible()) {
            this.loaderHelper.startLoader(false, false);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                if (resultCode == -1) {
                    requestDeleteConversation(data.getStringExtra("EXTRA_CONVERSATION_ID"));
                }
            case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (resultCode == -1) {
                    NavigationHelper.showUserInfo(getActivity(), data.getStringExtra("USER_ID"));
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void requestDeleteConversation(String conversationId) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.setProgressBarIndeterminateVisibility(true);
        }
        BusMessagingHelper.deleteConversation(conversationId);
    }

    public void onStart() {
        super.onStart();
        OdklMessagingEventsService.bindToMe(getActivity(), this.serviceConnection);
    }

    public void onStop() {
        super.onStop();
        getActivity().unbindService(this.serviceConnection);
    }

    protected void onInternetAvailable() {
        super.onInternetAvailable();
        if (this.adapter.getItemCount() <= 0) {
            Logger.m172d("Perform conversations loading");
            onRefresh();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624133)
    public void onChatLeave(BusEvent e) {
        if (isFragmentVisible() && e.resultCode != -1) {
            ErrorType error = ErrorType.from(e.bundleOutput);
            int msgId = error == ErrorType.GENERAL ? 2131166036 : error.getDefaultErrorMessage();
            if (msgId > 0) {
                TimeToast.show(getActivity(), msgId, 0);
            }
        }
    }

    public boolean handleBack() {
        return this.messagingPromptController.handleBack() || super.handleBack();
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.messagingPromptController.hide();
    }

    public static void installConversationShortcut(Context context, Conversation conversation, List<UserInfo> users, String source) {
        CloverImageViewBitmapRenderer cloverImageViewBitmapRenderer = new CloverImageViewBitmapRenderer();
        CloverImageViewBitmapRenderer.render(context, new C10506(context, conversation, source), conversation, users, (int) Utils.dipToPixels(48.0f));
    }
}
