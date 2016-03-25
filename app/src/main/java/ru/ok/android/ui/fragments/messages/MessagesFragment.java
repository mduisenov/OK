package ru.ok.android.ui.fragments.messages;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.appcompat.C0027R;
import android.support.v7.internal.widget.TintTypedArray;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import ru.mail.libverify.C0176R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.model.cache.ram.ConversationsCache;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.proto.ConversationProto;
import ru.ok.android.proto.ConversationProto.Conversation.Type;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.proto.MessagesProto.Attach.Status;
import ru.ok.android.proto.ProtoProxy;
import ru.ok.android.services.AttachmentUtils;
import ru.ok.android.services.app.IntentUtils;
import ru.ok.android.services.app.NotifyReceiver;
import ru.ok.android.services.app.messaging.OdklMessagingEventsService;
import ru.ok.android.services.app.messaging.OdklMessagingEventsService.OdklMessagingEventsServiceBinder;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.video.FileLocation;
import ru.ok.android.services.processors.video.MediaInfo;
import ru.ok.android.services.processors.xmpp.XmppSettingsPreferences;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.main.OdklActivity;
import ru.ok.android.ui.adapters.section.Sectionizer;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.UpdateListDataCommandBuilder;
import ru.ok.android.ui.custom.imageview.MultiUserAvatar;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.ui.dialogs.AlertFragmentDialog.OnAlertDismissListener;
import ru.ok.android.ui.dialogs.DeleteConversationDialog;
import ru.ok.android.ui.fragments.SaveToFileFragment;
import ru.ok.android.ui.fragments.SaveToFileFragment.SaveToFileFragmentListener;
import ru.ok.android.ui.fragments.messages.adapter.ChatStateReporter;
import ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter;
import ru.ok.android.ui.fragments.messages.adapter.MessagesConversationAdapter;
import ru.ok.android.ui.fragments.messages.adapter.MessagesReadStatusAdapter;
import ru.ok.android.ui.fragments.messages.helpers.ConversationParticipantsUtils;
import ru.ok.android.ui.fragments.messages.helpers.MenuItemsVisibilityHelper;
import ru.ok.android.ui.fragments.messages.loaders.ConversationLoader;
import ru.ok.android.ui.fragments.messages.loaders.MessagesBaseLoader;
import ru.ok.android.ui.fragments.messages.loaders.MessagesConversationLoader;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesBundle;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesLoaderBundle;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesLoaderBundle.ChangeReason;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.ui.fragments.messages.view.ParticipantsPreviewView;
import ru.ok.android.ui.fragments.users.UsersLikedMessageFragment;
import ru.ok.android.ui.messaging.fragments.ConversationsFragment;
import ru.ok.android.ui.messaging.views.ComposingView;
import ru.ok.android.ui.users.UserDisabledSelectionParams;
import ru.ok.android.ui.users.UsersSelectionParams;
import ru.ok.android.ui.users.fragments.UsersByIdFragment;
import ru.ok.android.ui.utils.RowPosition;
import ru.ok.android.utils.AudioPlaybackController;
import ru.ok.android.utils.BitmapRender;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.FriendlySpannableStringBuilder;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.MediaUploadUtils;
import ru.ok.android.utils.MimeTypes;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NotificationsUtils;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.UserMedia;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.bus.BusMessagingHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.model.Conversation;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.UserInfo.UserOnlineType;
import ru.ok.model.messages.Attachment;
import ru.ok.model.messages.Attachment.AttachmentType;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase;
import ru.ok.model.messages.MessageBase.RepliedTo;
import ru.ok.model.messages.MessageConversation;

public final class MessagesFragment extends MessageBaseFragment<MessageConversation, Conversation, MessagesConversationLoader> implements OnAlertDismissListener, SaveToFileFragmentListener {
    ChatStateReporter chatStateReporter;
    protected ComposingView composingView;
    private ConversationProto.Conversation conversationInfo;
    private RelativeLayout customAppBar;
    private ViewGroup header;
    private final Handler loadNewMessagesHandler;
    private MenuItemsVisibilityHelper menuHelper;
    MessagesReadStatusAdapter messagesReadStatusAdapter;
    private NewMessageAndReadStatusLocalBroadcastReceiver newMessageAndReadStatusLocalBroadcastReceiver;
    private BroadcastReceiver notificationsReceiver;
    OdklMessagingEventsServiceBinder odklMessagingEventsServiceBinder;
    private final List<UserInfo> participants;
    private final Map<String, UserInfo> participantsMap;
    private ParticipantsPreviewView participantsPreview;
    private boolean saveInstanceStateCalled;
    private ServiceConnection serviceConnection;
    private final Handler subtitleHandler;
    private TextView topic;

    /* renamed from: ru.ok.android.ui.fragments.messages.MessagesFragment.12 */
    class AnonymousClass12 implements Sectionizer<MessagesReadStatusAdapter> {
        final /* synthetic */ Activity val$activity;

        AnonymousClass12(Activity activity) {
            this.val$activity = activity;
        }

        public String getSectionTitleForItem(MessagesReadStatusAdapter adapter, int index) {
            OfflineMessage message = (OfflineMessage) adapter.getItem(index);
            if (message == null) {
                return null;
            }
            return DateFormatter.getFormatStringFromDateNoTime(this.val$activity, message.message.date);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessagesFragment.13 */
    static /* synthetic */ class AnonymousClass13 {
        static final /* synthetic */ int[] f105x5c76f804;

        static {
            f105x5c76f804 = new int[ChangeReason.values().length];
            try {
                f105x5c76f804[ChangeReason.FIRST.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f105x5c76f804[ChangeReason.NEW.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f105x5c76f804[ChangeReason.NEXT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessagesFragment.1 */
    class C08501 extends Handler {
        C08501() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MessagesFragment.this.updateActionBarState();
            MessagesFragment.this.subtitleHandler.sendEmptyMessageDelayed(0, 60000);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessagesFragment.2 */
    class C08512 extends Handler {
        C08512() {
        }

        public void handleMessage(Message msg) {
            Logger.m172d("Load new messages by timer");
            ((MessagesConversationLoader) MessagesFragment.this.getLoader()).loadNew(true);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessagesFragment.3 */
    class C08523 implements OnClickListener {
        C08523() {
        }

        public void onClick(View v) {
            if (MessagesFragment.this.conversationInfo == null || MessagesFragment.this.conversationInfo.getType() == Type.PRIVATE) {
                NavigationHelper.showUserInfo(MessagesFragment.this.getActivity(), MessagesFragment.this.getUserId());
                return;
            }
            EditTopicPopup popup = EditTopicPopup.newInstance(MessagesFragment.this.conversationInfo.getTopic());
            popup.setTargetFragment(MessagesFragment.this, 1005);
            popup.show(MessagesFragment.this.getFragmentManager(), "change-topic");
            StatisticManager.getInstance().addStatisticEvent("multichat-topic-clicked", new Pair[0]);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessagesFragment.4 */
    class C08534 implements OnClickListener {
        C08534() {
        }

        public void onClick(View v) {
            if (MessagesFragment.this.conversationInfo == null || MessagesFragment.this.conversationInfo.getType() != Type.PRIVATE) {
                NavigationHelper.showConversationParticipants(MessagesFragment.this.getActivity(), MessagesFragment.this.getConversationId());
            } else {
                NavigationHelper.showUserInfo(MessagesFragment.this.getActivity(), MessagesFragment.this.getUserId());
            }
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessagesFragment.5 */
    class C08545 implements LoaderCallbacks<Pair<ConversationProto.Conversation, List<UserInfo>>> {
        C08545() {
        }

        public Loader<Pair<ConversationProto.Conversation, List<UserInfo>>> onCreateLoader(int id, Bundle args) {
            return new ConversationLoader(MessagesFragment.this.getContext(), MessagesFragment.this.getConversationId());
        }

        public void onLoadFinished(Loader<Pair<ConversationProto.Conversation, List<UserInfo>>> loader, Pair<ConversationProto.Conversation, List<UserInfo>> data) {
            if (data != null) {
                ConversationProto.Conversation conversation = data.first;
                MessagesFragment.this.participants.clear();
                MessagesFragment.this.participants.addAll((Collection) data.second);
                MessagesFragment.this.participantsMap.clear();
                for (UserInfo user : MessagesFragment.this.participants) {
                    MessagesFragment.this.participantsMap.put(user.uid, user);
                }
                if (MessagesFragment.this.participantsPreview != null) {
                    MessagesFragment.this.participantsPreview.setParticipants(MessagesFragment.this.participants);
                }
                MessagesFragment.this.conversationInfo = conversation;
                boolean isLastElementVisible = MessagesFragment.this.isLastElementVisible();
                MessagesFragment.this.getMessagesReadStatusAdapter().setConversationInfo(MessagesFragment.this.conversationInfo, MessagesFragment.this.participants);
                if (isLastElementVisible) {
                    MessagesFragment.this.selectLastRow();
                }
                MessagesFragment.this.updateAudioAttachEnabled();
                MessagesFragment.this.updateActionBarState();
                MessagesFragment.this.updateMenuVisibility();
                MessagesFragment.this.processArgumentUris();
                MessagesFragment.this.updateCreateMessageViewMode();
                MessagesFragment.this.updateAudioAttachEnabled();
                MessagesFragment.this.updateSendMessageAllowedState();
                MessagesFragment.this.initStateReporter();
                if (MessagesFragment.this.composingView != null) {
                    MessagesFragment.this.composingView.setUserInfos(MessagesFragment.this.conversationInfo, MessagesFragment.this.participants);
                }
            }
        }

        public void onLoaderReset(Loader<Pair<ConversationProto.Conversation, List<UserInfo>>> loader) {
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessagesFragment.6 */
    class C08556 extends AsyncTask<String, Void, Integer> {
        C08556() {
        }

        protected Integer doInBackground(String... params) {
            int errorMessageId = 0;
            if (!UsersStorageFacade.isUserFriend(params[0])) {
                errorMessageId = 2131165418;
            }
            return Integer.valueOf(errorMessageId);
        }

        protected void onPostExecute(Integer result) {
            if (MessagesFragment.this.getActivity() != null && result != null) {
                MessagesFragment.this.createMessageView.setError(result.intValue());
            }
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessagesFragment.7 */
    class C08567 implements OnClickListener {
        C08567() {
        }

        public void onClick(View v) {
            EditTopicPopup popup = EditTopicPopup.newInstance(MessagesFragment.this.conversationInfo.getTopic());
            popup.setTargetFragment(MessagesFragment.this, 1005);
            popup.show(MessagesFragment.this.getFragmentManager(), "change-topic");
            StatisticManager.getInstance().addStatisticEvent("multichat-topic-clicked", new Pair[0]);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessagesFragment.8 */
    class C08578 implements OnClickListener {
        C08578() {
        }

        public void onClick(View v) {
            NavigationHelper.showConversationParticipants(MessagesFragment.this.getActivity(), MessagesFragment.this.getConversationId());
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessagesFragment.9 */
    class C08589 implements DialogInterface.OnClickListener {
        final /* synthetic */ List val$fRetryAttachments;
        final /* synthetic */ List val$fWithErrors;
        final /* synthetic */ OfflineMessage val$message;

        C08589(OfflineMessage offlineMessage, List list, List list2) {
            this.val$message = offlineMessage;
            this.val$fWithErrors = list;
            this.val$fRetryAttachments = list2;
        }

        public void onClick(DialogInterface dialog, int which) {
            MessagesFragment.this.onResendWithoutErrors(this.val$message, this.val$fWithErrors, this.val$fRetryAttachments);
        }
    }

    private class NewMessageAndReadStatusLocalBroadcastReceiver extends BroadcastReceiver {
        private NewMessageAndReadStatusLocalBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (MessagesFragment.this.getActivity() != null) {
                if ((TextUtils.equals("CHAT_NEW_MESSAGE_ARRIVED_XMPP", intent.getAction()) || TextUtils.equals("CHAT_STATE_UPDATED_XMPP", intent.getAction())) && NotifyReceiver.isNotificationForConversation(intent, MessagesFragment.this.conversationInfo) && MessagesFragment.this.isFragmentVisible()) {
                    MessagesFragment.this.loadNewMessages();
                    abortBroadcast();
                }
            }
        }
    }

    private class NotificationsBroadcastReceiver extends BroadcastReceiver {
        private NotificationsBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals("ru.ok.android.action.NOTIFY", intent.getAction()) && !MessagesFragment.this.isHidden()) {
                if (NotifyReceiver.isNotificationForMessageServerError(intent) && (MessagesFragment.this.isResumed() || MessagesFragment.this.isFragmentVisible())) {
                    abortBroadcast();
                    Toast.makeText(MessagesFragment.this.getActivity(), MessagesFragment.this.getStringLocalized(2131166199), 1).show();
                }
                if (MessagesFragment.this.isFragmentVisible() && NotifyReceiver.isNotificationForConversation(intent, MessagesFragment.this.conversationInfo)) {
                    if (OdklMessagingEventsService.isXmppNewMessagePushEnabled()) {
                        Logger.m172d("Skipped chat update notification due to enabled new message XMPP-push");
                    } else {
                        Logger.m173d("Received push for message: %s in conversation: %s", intent.getStringExtra("message_id"), intent.getStringExtra("conversation_id"));
                        MessagesFragment.this.loadNewMessages();
                    }
                    if (MessagesFragment.this.isResumed() && MessagesFragment.this.isFragmentVisible()) {
                        abortBroadcast();
                    }
                }
            }
        }
    }

    public MessagesFragment() {
        this.participants = new ArrayList();
        this.participantsMap = new HashMap();
        this.subtitleHandler = new C08501();
        this.loadNewMessagesHandler = new C08512();
        this.serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder binder) {
                MessagesFragment.this.odklMessagingEventsServiceBinder = (OdklMessagingEventsServiceBinder) binder;
                if (MessagesFragment.this.composingView != null) {
                    MessagesFragment.this.odklMessagingEventsServiceBinder.addChatStateHandler(MessagesFragment.this.composingView);
                }
                MessagesFragment.this.initStateReporter();
            }

            public void onServiceDisconnected(ComponentName className) {
                if (MessagesFragment.this.composingView != null) {
                    MessagesFragment.this.odklMessagingEventsServiceBinder.removeChatStateHandler(MessagesFragment.this.composingView);
                }
                MessagesFragment.this.odklMessagingEventsServiceBinder = null;
            }
        };
    }

    private void loadNewMessages() {
        this.loadNewMessagesHandler.removeMessages(0);
        ((MessagesConversationLoader) getLoader()).loadNew(isFragmentVisible());
        this.refreshProvider.refreshCompleted();
    }

    public static Bundle newArgumentsConversation(String conversationId, String userId, boolean showAsDialog, ArrayList<MediaInfo> mediaInfosToSend) {
        Bundle args = new Bundle();
        args.putString("CONVERSATION_ID", conversationId);
        args.putString("USER_ID", userId);
        args.putBoolean("fragment_is_dialog", showAsDialog);
        args.putParcelableArrayList("media_infos", mediaInfosToSend);
        return args;
    }

    public static Bundle newArgumentsUser(String userId, boolean showAsDialog, ArrayList<MediaInfo> mediaInfosToSend) {
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        args.putBoolean("fragment_is_dialog", showAsDialog);
        args.putParcelableArrayList("media_infos", mediaInfosToSend);
        return args;
    }

    public String getConversationId() {
        return getArguments().getString("CONVERSATION_ID");
    }

    public String getUserId() {
        return getArguments().getString("USER_ID");
    }

    private ArrayList<MediaInfo> getMediaInfosFromArguments() {
        return getArguments().getParcelableArrayList("media_infos");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        registerReceiver();
    }

    public void onDetach() {
        super.onDetach();
        unregisterReceiver();
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.messagesReadStatusAdapter = null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(getLayoutId(), container, false);
        initSettings();
        initList(view);
        initCreateMessageView(view);
        initReplyTo(view);
        return view;
    }

    protected int getLayoutId() {
        return 2130903327;
    }

    protected boolean isSendAudioAttachEnabled(Conversation info) {
        if (info == null) {
            return this.conversationInfo == null || this.conversationInfo.getCapabilities().getCanSendAudio();
        } else {
            return info.capabilities.canSendAudio;
        }
    }

    protected boolean isSendVideoAttachEnabled(Conversation info) {
        if (info == null) {
            return this.conversationInfo == null || this.conversationInfo.getCapabilities().getCanSendVideo();
        } else {
            return info.capabilities.canSendVideo;
        }
    }

    protected CharSequence getTitle() {
        boolean isPrivate;
        CharSequence result = getTitleInternal();
        if (this.topic != null) {
            CharSequence stringLocalized;
            int i;
            boolean empty = TextUtils.isEmpty(result);
            TextView textView = this.topic;
            if (empty) {
                stringLocalized = getStringLocalized(2131166264);
            } else {
                stringLocalized = result;
            }
            textView.setText(stringLocalized);
            textView = this.topic;
            if (empty) {
                i = 2;
            } else {
                i = 0;
            }
            textView.setTypeface(null, i);
        }
        if (this.conversationInfo == null || this.conversationInfo.getType() != Type.PRIVATE) {
            isPrivate = false;
        } else {
            isPrivate = true;
        }
        return (TextUtils.isEmpty(result) || (!isPrivate && DeviceUtils.isTablet(getContext()))) ? LocalizationManager.from(getActivity()).getString(2131165638) : result;
    }

    protected CharSequence getSubtitle() {
        boolean isPrivate = this.conversationInfo != null && this.conversationInfo.getType() == Type.PRIVATE;
        if (isPrivate) {
            UserInfo buddy = ConversationParticipantsUtils.findNonCurrentUser(this.participants);
            if (buddy == null) {
                return null;
            }
            UserOnlineType online = Utils.onlineStatus(buddy);
            if (online != UserOnlineType.OFFLINE) {
                FriendlySpannableStringBuilder sb = new FriendlySpannableStringBuilder();
                ImageSpan imageSpan = new ImageSpan(getActivity(), online == UserOnlineType.MOBILE ? 2130838102 : 2130838024, 1);
                sb.append(" ", imageSpan);
                sb.append(" ").append(getStringLocalized(2131166797));
                return sb.build();
            } else if (buddy.lastOnline <= 0) {
                return getStringLocalized(2131166796);
            } else {
                String genderPart = getStringLocalized(buddy.genderType == UserGenderType.MALE ? 2131166867 : 2131166866);
                String timePart = DateFormatter.formatDeltaTimePast(getActivity(), buddy.lastOnline, true, false);
                return getStringLocalized(2131166868, genderPart, timePart);
            }
        } else if (DeviceUtils.isTablet(getContext())) {
            return null;
        } else {
            return getParticipantsWithCountString(this.conversationInfo);
        }
    }

    protected View getActionBarCustomView() {
        if (DeviceUtils.isTablet(getContext())) {
            return null;
        }
        if (this.customAppBar == null) {
            this.customAppBar = (RelativeLayout) LayoutInflater.from(getContext()).inflate(2130903328, null, false);
            this.customAppBar.setOnClickListener(new C08523());
            ((MultiUserAvatar) this.customAppBar.findViewById(2131624657)).setOnClickListener(new C08534());
            TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(getContext(), null, C0027R.styleable.Toolbar, C0176R.attr.toolbarStyle, 0);
            TextView titleView = (TextView) this.customAppBar.findViewById(C0176R.id.title);
            int mTitleTextAppearance = tintTypedArray.getResourceId(10, 0);
            if (mTitleTextAppearance != 0) {
                titleView.setTextAppearance(getContext(), mTitleTextAppearance);
            }
            if (tintTypedArray.hasValue(23)) {
                titleView.setTextColor(tintTypedArray.getColor(23, -1));
            }
            TextView subtitleView = (TextView) this.customAppBar.findViewById(C0158R.id.subtitle);
            int mSubtitleTextAppearance = tintTypedArray.getResourceId(11, 0);
            if (mSubtitleTextAppearance != 0) {
                subtitleView.setTextAppearance(getContext(), mSubtitleTextAppearance);
            }
            if (tintTypedArray.hasValue(24)) {
                subtitleView.setTextColor(tintTypedArray.getColor(24, -1));
            }
            tintTypedArray.recycle();
        }
        if (this.conversationInfo != null) {
            String str;
            List<UserInfo> users = ProtoProxy.proto2ApiP(this.conversationInfo.getParticipantsList());
            MultiUserAvatar cloverImageView = (MultiUserAvatar) this.customAppBar.findViewById(2131624657);
            if (this.conversationInfo.getType() == Type.PRIVATE || this.conversationInfo.getParticipantsCount() <= 1) {
                str = OdnoklassnikiApplication.getCurrentUser().uid;
            } else {
                str = null;
            }
            cloverImageView.setLeaves(MultiUserAvatar.getLeafInfos(users, null, null, str));
            cloverImageView.setVisibility(users.size() > 1 ? 0 : 8);
        }
        ((TextView) this.customAppBar.findViewById(C0176R.id.title)).setText(getTitle());
        ((TextView) this.customAppBar.findViewById(C0158R.id.subtitle)).setText(getSubtitle());
        return this.customAppBar;
    }

    private String getParticipantsWithCountString(ConversationProto.Conversation conversationInfo) {
        if (conversationInfo == null) {
            return null;
        }
        int membersCountRes = StringUtils.plural((long) conversationInfo.getParticipantsCount(), 2131166186, 2131166187, 2131166188);
        return LocalizationManager.getString(getContext(), membersCountRes, Integer.valueOf(count));
    }

    private CharSequence getTitleInternal() {
        if (this.conversationInfo != null) {
            CharSequence topic = this.conversationInfo.getBuiltTopic();
            if (!TextUtils.isEmpty(topic)) {
                return topic;
            }
        }
        return null;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initStateReporter();
        if (!TextUtils.isEmpty(getConversationId())) {
            initLoaders();
        }
    }

    private MessagesReadStatusAdapter getMessagesReadStatusAdapter() {
        if (this.messagesReadStatusAdapter == null) {
            this.messagesReadStatusAdapter = new MessagesReadStatusAdapter(getContext(), getAdapter());
        }
        return this.messagesReadStatusAdapter;
    }

    protected BaseAdapter getAdapterForSectionAdapter() {
        return getMessagesReadStatusAdapter();
    }

    private void initLoaders() {
        getLoaderManager().initLoader(100, null, new C08545());
    }

    private void updateMenuVisibility() {
        if (this.menuHelper != null && getLoader() != null && ((MessagesConversationLoader) getLoader()).getBundle() != null) {
            this.menuHelper.updateVisibility(getContext(), this.conversationInfo, this.participants);
        }
    }

    protected void initCreateMessageView(View view) {
        super.initCreateMessageView(view);
        initStateReporter();
        updateAudioAttachEnabled();
    }

    private void updateAudioAttachEnabled() {
        if (this.conversationInfo != null && this.createMessageView != null) {
            Conversation c = ProtoProxy.proto2Api(this.conversationInfo);
            boolean commentingAllowed = isCommentingAllowed(c);
            boolean sendAudioAttachEnabled = isSendAudioAttachEnabled(c);
            boolean sendVideoAttachEnabled = isSendVideoAttachEnabled(c);
            if (sendAudioAttachEnabled) {
                this.createMessageView.setEnabledStates(commentingAllowed, sendAudioAttachEnabled, sendVideoAttachEnabled, true);
                return;
            }
            this.createMessageView.setEnabledStates(commentingAllowed, false, sendVideoAttachEnabled, true);
            if (this.conversationInfo.getType() != Type.PRIVATE) {
                this.createMessageView.setError(2131165419);
                return;
            }
            if (ConversationParticipantsUtils.findNonCurrentUserIdProto(this.conversationInfo.getParticipantsList()) != null) {
                new C08556().execute(new String[]{remoteUserId});
            }
        }
    }

    public void onLoadFinished(Loader<MessagesLoaderBundle<MessageConversation, Conversation>> loader, MessagesLoaderBundle<MessageConversation, Conversation> loaderData) {
        super.onLoadFinished((Loader) loader, (MessagesLoaderBundle) loaderData);
        switch (loader.getId()) {
            case RECEIVED_VALUE:
                if (loaderData.errorType != null && loaderData.reason == ChangeReason.ADDED) {
                    StatisticManager.getInstance().addStatisticEvent("messages-failure", new Pair("reason", "add-to-db-failed"));
                }
                if (loaderData.errorType != null) {
                    processArgumentUris();
                    return;
                }
                getMessagesReadStatusAdapter().setConversationInfo(this.conversationInfo, this.participants);
                switch (AnonymousClass13.f105x5c76f804[loaderData.reason.ordinal()]) {
                    case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                        if (TextUtils.isEmpty(getConversationId())) {
                            String conversationId = ((Conversation) loaderData.bundle.generalInfo).id;
                            getArguments().putString("CONVERSATION_ID", conversationId);
                            ((MessagesConversationLoader) getLoader()).setConversationId(conversationId);
                            initLoaders();
                            initCreateMessageView(getView());
                        }
                        if (loaderData.bundle.messages.isEmpty()) {
                            sendLoadNewMessages();
                        } else {
                            this.loadNewMessagesHandler.removeMessages(0);
                            ((MessagesConversationLoader) getLoader()).loadNew(true);
                        }
                        if (this.refreshProvider != null) {
                            this.refreshProvider.refreshCompleted();
                        }
                        createHeaderView();
                        processArgumentUris();
                    case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                    case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                        GlobalBus.send(2131624211, null);
                        sendLoadNewMessages();
                    default:
                }
            default:
        }
    }

    private void sendLoadNewMessages() {
        this.loadNewMessagesHandler.removeMessages(0);
        this.loadNewMessagesHandler.sendEmptyMessageDelayed(0, 30000);
    }

    private void processArgumentUris() {
        MessagesConversationLoader loader = (MessagesConversationLoader) getLoader();
        if (loader != null) {
            boolean wasError = loader.isWasError();
            if (!loader.isDataPresents() && !wasError) {
                return;
            }
            if (this.conversationInfo != null || wasError) {
                ArrayList<MediaInfo> mediaInfos = getMediaInfosFromArguments();
                if (mediaInfos != null) {
                    boolean canPost = this.conversationInfo == null || this.conversationInfo.getCapabilities().getCanPost();
                    if (canPost) {
                        processAddedMediaInfos(mediaInfos);
                        getArguments().remove("media_infos");
                    }
                }
            }
        }
    }

    private void showPhotoAttachSelect(boolean forceUseCamera) {
        Context activity = getActivity();
        if (activity != null) {
            Intent intent = IntentUtils.createIntentToAddImages(activity, null, 0, 3, false, false, "imgupldr");
            intent.putExtra("camera", forceUseCamera);
            intent.putExtra("upload_btn_text", LocalizationManager.getString(activity, 2131166507));
            intent.putExtra("can_create_album", false);
            intent.putExtra("can_select_album", false);
            intent.putExtra("actionbar_title", LocalizationManager.getString(activity, 2131166516));
            intent.putExtra("silent_cancel_if_not_edited", true);
            intent.putExtra("cancel_alert_text", getStringLocalized(2131165479));
            intent.putExtra("action_text", getStringLocalized(2131166507));
            intent.putExtra("statistics_prefix", "attach-");
            intent.putExtra("max_count", 10);
            intent.putExtra("edit_images", false);
            startActivityForResult(intent, 1337);
        }
    }

    public void onAudioAttachRequested(String fileName, byte[] audioWave) {
        String str = "fileName=%s audioWave.length=%s";
        Object[] objArr = new Object[2];
        objArr[0] = fileName;
        objArr[1] = audioWave == null ? null : Integer.valueOf(audioWave.length);
        Logger.m173d(str, objArr);
        File audioFile = new File(fileName);
        Attachment attachment = new Attachment(fileName, AttachmentType.AUDIO_RECORDING);
        attachment.duration = (long) AudioPlaybackController.getMediaDuration(audioFile.getPath());
        if (!(audioWave == null || audioWave.length == 0)) {
            attachment.audioProfile = Base64.encodeToString(audioWave, 0);
        }
        sendVideoAttachment(attachment);
        sendAttachStatEvents(AttachmentType.AUDIO_RECORDING.getStrValue());
    }

    public void onAudioAttachRecording(boolean recording) {
        boolean z = false;
        View rootView = getView();
        if (rootView != null) {
            View view = rootView.findViewById(2131625098);
            if (view != null) {
                view.setVisibility(recording ? 0 : 8);
            }
        }
        if (!recording) {
            z = true;
        }
        setMenuVisibility(z);
    }

    public void onPhotoSelectClick(View view) {
        super.onPhotoSelectClick(view);
        showPhotoAttachSelect(false);
    }

    public void onCameraClick(View view) {
        super.onCameraClick(view);
        showPhotoAttachSelect(true);
    }

    public void onAttachVideoClick(boolean recordNewVideo) {
        super.onAttachVideoClick(recordNewVideo);
        Logger.m172d(String.valueOf(recordNewVideo));
        if (recordNewVideo) {
            if (PermissionUtils.checkSelfPermission(getContext(), "android.permission.CAMERA") == 0) {
                startVideoCameraActivity();
                return;
            }
            requestPermissions(new String[]{"android.permission.CAMERA"}, 131);
            return;
        }
        startVideoPickerActivity();
    }

    protected int getShowAttachSourceId() {
        return 4;
    }

    private void createHeaderView() {
        MessagesBaseLoader<MessageConversation, Conversation> loader = getLoader();
        if (loader != null) {
            MessagesBundle<MessageConversation, Conversation> loadedResult = loader.getBundle();
            if (loadedResult != null && loadedResult.generalInfo != null && ((Conversation) loadedResult.generalInfo).type != null && ((Conversation) loadedResult.generalInfo).type != Conversation.Type.PRIVATE) {
                if (DeviceUtils.isTablet(getContext())) {
                    Context activity = getActivity();
                    if (!(this.header == null || this.topic != null || activity == null)) {
                        LocalizationManager.inflate(activity, 2130903329, this.header, true);
                        this.topic = (TextView) this.header.findViewById(2131625102);
                        this.topic.setOnClickListener(new C08567());
                        this.participantsPreview = (ParticipantsPreviewView) this.header.findViewById(2131625101);
                        this.participantsPreview.setParticipants(this.participants);
                        this.participantsPreview.setOnClickListener(new C08578());
                    }
                }
                updateActionBarState();
            }
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (inflateMenuLocalized(2131689508, menu)) {
            this.menuHelper = new MenuItemsVisibilityHelper(menu);
            updateMenuVisibility();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131624879:
                NavigationHelper.showConversationParticipants(getActivity(), getConversationId());
                return true;
            case 2131624926:
                showToastIfVisible(this.conversationInfo.getType() == Type.PRIVATE ? 2131166020 : 2131166019, 1);
                ArrayList<String> disabledIds = ConversationParticipantsUtils.toIdsWithoutCurrentProto(this.conversationInfo.getParticipantsList());
                UserDisabledSelectionParams selectionParams = new UserDisabledSelectionParams(disabledIds, disabledIds, ServicesSettingsHelper.getServicesSettings().getMultichatMaxParticipantsCount());
                if (this.conversationInfo.getType() == Type.CHAT) {
                    NavigationHelper.selectFriendsFiltered(this, selectionParams, 2, LocationStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS);
                } else {
                    NavigationHelper.selectFriendsFilteredForChat(this, selectionParams, 2, LocationStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES);
                }
                StatisticManager.getInstance().addStatisticEvent("multichat-invite", new Pair("place", "messages"));
                return true;
            case 2131625261:
                NavigationHelper.onCallUser(getActivity(), ConversationParticipantsUtils.findNonCurrentUserIdProto(this.conversationInfo.getParticipantsList()));
                return true;
            case 2131625486:
                if (getFragmentManager() != null) {
                    DeleteConversationDialog dialog = DeleteConversationDialog.newInstance(getConversationId(), getTitleInternal());
                    dialog.setTargetFragment(this, LocationStatusCodes.GEOFENCE_NOT_AVAILABLE);
                    dialog.show(getFragmentManager(), "delete-conversation-dialog");
                }
                StatisticManager.getInstance().addStatisticEvent("multichat-delete-all", new Pair[0]);
                return true;
            case 2131625487:
                BusMessagingHelper.leaveChat(getConversationId());
                StatisticManager.getInstance().addStatisticEvent("multichat-leave", new Pair[0]);
                return true;
            case 2131625488:
                ConversationsFragment.installConversationShortcut(getContext(), this.conversationInfo, this.participants, "messages");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void unregisterReceiver() {
        if (this.notificationsReceiver != null) {
            getActivity().unregisterReceiver(this.notificationsReceiver);
            this.notificationsReceiver = null;
        }
        if (this.newMessageAndReadStatusLocalBroadcastReceiver != null) {
            getContext().unregisterReceiver(this.newMessageAndReadStatusLocalBroadcastReceiver);
            this.newMessageAndReadStatusLocalBroadcastReceiver = null;
        }
    }

    private boolean shouldProcessResult(BusEvent event, int errorTextId) {
        Context context = getContext();
        if (context == null || !TextUtils.equals(event.bundleInput.getString("CONVERSATION_ID"), getConversationId())) {
            return false;
        }
        if (event.resultCode == -1) {
            return true;
        }
        ErrorType errorType = ErrorType.from(event.bundleOutput);
        if (errorType != ErrorType.GENERAL) {
            errorTextId = errorType.getDefaultErrorMessage();
        }
        TimeToast.show(context, errorTextId, 0);
        return false;
    }

    @Subscribe(on = 2131623946, to = 2131624133)
    public void onChatLeave(BusEvent event) {
        if (shouldProcessResult(event, 2131165571)) {
            leaveConversation();
        }
    }

    private void leaveConversation() {
        Activity activity = getActivity();
        if (activity != null && !this.saveInstanceStateCalled && isVisible()) {
            if (activity instanceof OdklActivity) {
                NavigationHelper.showMessagesForUser(activity, null);
            } else {
                activity.onBackPressed();
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624130)
    public void onParticipantsAdded(BusEvent event) {
        if (shouldProcessResult(event, 2131165566) && isVisible()) {
            ArrayList<String> blockedUids = event.bundleOutput.getStringArrayList("BLOCKED_USER_IDS");
            if (!blockedUids.isEmpty()) {
                UsersBlockedFragment fragment = UsersBlockedFragment.newInstance(blockedUids);
                fragment.setTargetFragment(this, 1003);
                fragment.show(getFragmentManager(), "blocked-users");
            }
            this.loadNewMessagesHandler.removeMessages(0);
            ((MessagesConversationLoader) getLoader()).loadNew(true);
        }
    }

    public void onStart() {
        super.onStart();
        OdklMessagingEventsService.bindToMe(getActivity(), this.serviceConnection);
    }

    public void onStop() {
        super.onStop();
        Activity activity = getActivity();
        if (this.odklMessagingEventsServiceBinder != null) {
            this.odklMessagingEventsServiceBinder.removeChatStateHandler(this.composingView);
        }
        activity.unbindService(this.serviceConnection);
        if (this.chatStateReporter != null) {
            this.chatStateReporter.reportChatInactive();
        }
        if (this.createMessageView != null) {
            this.createMessageView.removeTextWatcher(this.chatStateReporter);
        }
    }

    public void onResume() {
        super.onResume();
        removeExistingNotification();
        this.subtitleHandler.removeMessages(0);
        this.subtitleHandler.sendEmptyMessageDelayed(0, 60000);
        this.saveInstanceStateCalled = false;
        MediaUploadUtils.onResume(getActivity().getSupportFragmentManager(), this);
        sendLoadNewMessages();
    }

    public void onPause() {
        super.onPause();
        this.subtitleHandler.removeMessages(0);
        if (this.chatStateReporter != null) {
            this.chatStateReporter.reportChatPaused();
        }
        this.loadNewMessagesHandler.removeMessages(0);
    }

    protected void onHideFragment() {
        super.onHideFragment();
        this.subtitleHandler.removeMessages(0);
    }

    protected void onShowFragment() {
        super.onShowFragment();
        this.subtitleHandler.removeMessages(0);
        this.subtitleHandler.sendEmptyMessageDelayed(0, 60000);
        removeExistingNotification();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.saveInstanceStateCalled = true;
    }

    public void onSendMessageClick(View view) {
        if (TextUtils.isEmpty(getConversationId())) {
            Logger.m185w("User tried to send message before knowing of conversationId. Buddy: %s", getUserId());
            return;
        }
        super.onSendMessageClick(view);
    }

    private void removeExistingNotification() {
        if (isFragmentVisible()) {
            NotificationsUtils.hideNotificationForConversation(getActivity(), getConversationId());
        }
    }

    private void registerReceiver() {
        if (this.notificationsReceiver == null) {
            IntentFilter filter = new IntentFilter("ru.ok.android.action.NOTIFY");
            filter.setPriority(2);
            FragmentActivity activity = getActivity();
            BroadcastReceiver notificationsBroadcastReceiver = new NotificationsBroadcastReceiver();
            this.notificationsReceiver = notificationsBroadcastReceiver;
            activity.registerReceiver(notificationsBroadcastReceiver, filter);
        }
        if (this.newMessageAndReadStatusLocalBroadcastReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("CHAT_LAST_VIEW_TIME_CHANGED");
            intentFilter.addAction("CHAT_NEW_MESSAGE_ARRIVED_XMPP");
            intentFilter.addAction("CHAT_STATE_UPDATED_XMPP");
            intentFilter.setPriority(2);
            Context context = getContext();
            notificationsBroadcastReceiver = new NewMessageAndReadStatusLocalBroadcastReceiver();
            this.newMessageAndReadStatusLocalBroadcastReceiver = notificationsBroadcastReceiver;
            context.registerReceiver(notificationsBroadcastReceiver, intentFilter);
        }
    }

    protected MessagesBaseAdapter createMessagesAdapter() {
        return new MessagesConversationAdapter(getActivity(), OdnoklassnikiApplication.getCurrentUser().uid, this);
    }

    protected MessagesConversationLoader createMessagesLoader() {
        return new MessagesConversationLoader(getActivity(), getConversationId(), getUserId());
    }

    protected void processResultCustom(MessagesBundle data) {
        if (this.composingView != null && this.conversationInfo != null) {
            this.composingView.setUserInfos(this.conversationInfo, data.users);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LocationStatusCodes.GEOFENCE_NOT_AVAILABLE /*1000*/:
                onDeleteConversationResult(resultCode);
                return;
            case LocationStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES /*1001*/:
                onSelectFriendsResult(resultCode, data);
                return;
            case LocationStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS /*1002*/:
                onSelectFriendsChatResult(resultCode, data);
                return;
            case 1003:
                onBlockedUsersResult(resultCode, data);
                return;
            case 1004:
                onMentionedUsersResult(resultCode, data);
                return;
            case 1005:
                onChangeTopicResult(resultCode, data);
                return;
            case 1310:
                onSelectVideoResult(resultCode, data, AttachmentType.MOVIE);
                return;
            case 1311:
                onSelectVideoResult(resultCode, data, AttachmentType.VIDEO);
                return;
            case 1337:
                onAttachPhotoResult(resultCode, data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onAttachPhotoResult(int resultCode, Intent data) {
        if (resultCode == -1) {
            processAddedEditedImages(data.getParcelableArrayListExtra("imgs"));
            sendAttachStatEvents(AttachmentType.PHOTO.getStrValue());
        }
    }

    private void onChangeTopicResult(int resultCode, Intent data) {
        if (resultCode == -1) {
            BusMessagingHelper.setTopic(getConversationId(), EditTopicPopup.extractTopic(data));
        }
    }

    private void onMentionedUsersResult(int resultCode, Intent data) {
        if (resultCode == -1) {
            NavigationHelper.showUserInfo(getActivity(), data.getStringExtra("USER_ID"));
        }
    }

    private void onBlockedUsersResult(int resultCode, Intent data) {
        if (resultCode == -1) {
            NavigationHelper.showUserInfo(getActivity(), data.getStringExtra("USER_ID"));
        }
    }

    private void onSelectFriendsChatResult(int resultCode, Intent data) {
        if (resultCode == -1) {
            UsersSelectionParams params = (UsersSelectionParams) data.getParcelableExtra("selection_params");
            ArrayList<String> selectedIds = data.getStringArrayListExtra("selected_ids");
            ArrayList<String> disabledIds = params instanceof UserDisabledSelectionParams ? ((UserDisabledSelectionParams) params).getDisabledIds(null) : new ArrayList();
            if (params != null && selectedIds != null) {
                selectedIds.removeAll(disabledIds);
                if (selectedIds.size() > 0) {
                    BusMessagingHelper.addParticipants(getConversationId(), selectedIds);
                }
            }
        }
    }

    private void onSelectFriendsResult(int resultCode, Intent data) {
        if (resultCode == -1) {
            NavigationHelper.showMessagesForConversation(getActivity(), data.getStringExtra("conversation_id"), null);
        }
    }

    private void onDeleteConversationResult(int resultCode) {
        if (resultCode == -1) {
            BusMessagingHelper.deleteConversation(getConversationId());
        }
    }

    private void sendAttachStatEvents(String typeValue) {
        StatisticManager.getInstance().addStatisticEvent("attach-video-send", new Pair("type", typeValue));
    }

    private void onSelectVideoResult(int resultCode, Intent data, AttachmentType type) {
        if (resultCode == -1) {
            MediaInfo mediaInfo = MediaInfo.fromUri(getContext(), data == null ? null : data.getData(), "video-" + System.currentTimeMillis());
            Logger.m173d("Video selected: %s %s", videoUri, mediaInfo);
            if (mediaInfo != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("attachment_type", type.ordinal());
                MediaUploadUtils.startCopyFile(getActivity(), this, true, 1006, MediaUploadUtils.createSaveToFileVideoFragment(getActivity(), mediaInfo, bundle), this);
            }
            sendAttachStatEvents(type.getStrValue());
        }
    }

    public void onSaveToFileFinished(SaveToFileFragment fragment, boolean successful, Bundle additionalArgs) {
        String thumbPath = null;
        Logger.m173d("result: %s", Boolean.valueOf(successful));
        if (successful) {
            String videoPath;
            AttachmentType attachmentType = AttachmentType.values()[additionalArgs.getInt("attachment_type")];
            File videoFile = fragment.getDestFile(0);
            File thumbFile = fragment.getDestFile(1);
            FileLocation mediaLocation = FileLocation.createFromExternalFile(videoFile);
            FileLocation thumbLocation = FileLocation.createFromExternalFile(thumbFile);
            if (mediaLocation != null) {
                videoPath = mediaLocation.getUriSafe().toString();
            } else {
                videoPath = null;
            }
            if (thumbLocation != null) {
                thumbPath = thumbLocation.getUriSafe().toString();
            }
            Logger.m173d("type: %s, video: %s, thumbnail: %s", attachmentType, videoPath, thumbPath);
            sendVideoAttach(videoPath, thumbPath, attachmentType);
            MediaUploadUtils.hideDialogs(getActivity().getSupportFragmentManager(), fragment);
            return;
        }
        MediaUploadUtils.showAlert(getActivity(), this, 2131166095, 2131166832, 1007);
    }

    public void onAlertDismiss(int requestCode) {
        Logger.m173d("requestCode=%d", Integer.valueOf(requestCode));
        if (requestCode == 1006) {
            MediaUploadUtils.onCopyProgressCancelled(getActivity(), this, 1007);
        }
    }

    private void processAddedMediaInfos(@NonNull ArrayList<MediaInfo> mediaInfos) {
        List attachments = new ArrayList(mediaInfos.size());
        Iterator i$ = mediaInfos.iterator();
        while (i$.hasNext()) {
            attachments.add(toAttachment((MediaInfo) i$.next()));
        }
        ((MessagesConversationLoader) this.messagesLoader).addMessage(attachments, getRepliedTo(), getCurrentAuthor());
    }

    private void processAddedEditedImages(ArrayList<ImageEditInfo> images) {
        List attachments = new ArrayList(images.size());
        Iterator i$ = images.iterator();
        while (i$.hasNext()) {
            attachments.add(toAttachment((ImageEditInfo) i$.next()));
        }
        ((MessagesConversationLoader) this.messagesLoader).addMessage(attachments, getRepliedTo(), getCurrentAuthor());
    }

    @NonNull
    private RepliedTo getRepliedTo() {
        MessageBase comment = null;
        if (this.replyTo != null) {
            comment = this.replyTo.getComment();
        }
        return comment != null ? new RepliedTo(comment.id, comment.authorId, comment.authorType) : new RepliedTo(null, null, null);
    }

    @NonNull
    private static Attachment toAttachment(@NonNull ImageEditInfo imageEditInfo) {
        Attachment attachment = new Attachment(imageEditInfo.getUri(), AttachmentType.PHOTO, imageEditInfo.getRotation());
        attachment.localId = imageEditInfo.getId();
        attachment.standard_width = imageEditInfo.getWidth();
        attachment.standard_height = imageEditInfo.getHeight();
        attachment.setStatus("WAITING");
        if (MimeTypes.isGif(imageEditInfo.getMimeType())) {
            attachment.gifUrl = imageEditInfo.getUri().getPath();
        }
        return attachment;
    }

    @NonNull
    private static Attachment toAttachment(@NonNull MediaInfo mediaInfo) {
        Context appContext = OdnoklassnikiApplication.getContext();
        Uri mediaInfoUri = mediaInfo.getUri();
        int rotation = UserMedia.getImageRotation(appContext, mediaInfoUri);
        Options options = BitmapRender.getBitmapInfo(appContext.getContentResolver(), mediaInfoUri).options;
        Attachment attachment = new Attachment(mediaInfoUri, AttachmentType.PHOTO, rotation);
        attachment.localId = UUID.randomUUID().toString();
        attachment.standard_width = options.outWidth;
        attachment.standard_height = options.outHeight;
        attachment.setStatus("WAITING");
        if (MimeTypes.isGif(mediaInfo.getMimeType())) {
            attachment.gifUrl = mediaInfoUri.getPath();
        }
        return attachment;
    }

    private void sendVideoAttach(String fileLocation, String thumbnailLocation, AttachmentType attachmentType) {
        Attachment attach = new Attachment(fileLocation, attachmentType);
        attach.thumbnailUrl = thumbnailLocation;
        sendVideoAttachment(attach);
        Logger.m173d("send video attach %s", attachmentType.getStrValue());
    }

    public void startVideoPickerActivity() {
        Intent startChooseVideo = new Intent("android.intent.action.GET_CONTENT");
        startChooseVideo.setType("video/*");
        startActivityForResult(Intent.createChooser(startChooseVideo, getString(2131166354)), 1310);
    }

    public void startVideoCameraActivity() {
        Intent takeVideoIntent = new Intent("android.media.action.VIDEO_CAPTURE");
        takeVideoIntent.putExtra("android.intent.extra.durationLimit", ServicesSettingsHelper.getServicesSettings().getVideoAttachRecordingMaxDuration());
        if (takeVideoIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, 1311);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 131:
                if (PermissionUtils.getGrantResult(grantResults) == 0) {
                    startVideoCameraActivity();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void sendVideoAttachment(Attachment attach) {
        RepliedTo repliedTo = getRepliedTo();
        List attachments = new ArrayList();
        attach.localId = UUID.randomUUID().toString();
        attachments.add(attach);
        ((MessagesConversationLoader) this.messagesLoader).addMessage(attachments, repliedTo, getCurrentAuthor());
    }

    protected void tryResendMessage(OfflineMessage<MessageConversation> message) {
        if (((MessageConversation) message.message).hasAttachments()) {
            Attachment[] attachments = ((MessageConversation) message.message).attachments;
            List<Long> withErrors = null;
            List<Long> retryAttachments = null;
            for (Attachment attachment : attachments) {
                if ("ERROR".equals(attachment.getStatus())) {
                    if (withErrors == null) {
                        withErrors = new ArrayList(attachments.length);
                    }
                    withErrors.add(Long.valueOf(attachment._id));
                } else if ("RECOVERABLE_ERROR".equals(attachment.getStatus())) {
                    if (retryAttachments == null) {
                        retryAttachments = new ArrayList(attachments.length);
                    }
                    retryAttachments.add(Long.valueOf(attachment._id));
                }
            }
            if (withErrors == null || withErrors.isEmpty()) {
                if (retryAttachments != null) {
                    setStatusRetry(message.offlineData.databaseId, retryAttachments);
                }
                super.tryResendMessage(message);
                return;
            }
            new Builder(getActivity()).setTitle(getStringLocalized(2131166465)).setMessage(getStringLocalized(2131166464)).setNegativeButton(getStringLocalized(2131166257), null).setPositiveButton(getStringLocalized(2131166507), new C08589(message, withErrors, retryAttachments)).show();
            return;
        }
        super.tryResendMessage(message);
    }

    private void onResendWithoutErrors(OfflineMessage<MessageConversation> message, List<Long> withErrors, List<Long> retryAttachments) {
        MessagesCache.getInstance().removeAttachments(message.offlineData.databaseId, withErrors, retryAttachments);
        super.tryResendMessage(message);
    }

    private void setStatusRetry(int messageId, Collection<Long> attachments) {
        for (Long id : attachments) {
            AttachmentUtils.updateAttachmentState(messageId, id.longValue(), Status.RETRY);
        }
    }

    public RowPosition getRowPositionType(int position, int extendedPosition) {
        if (((MessageConversation) ((OfflineMessage) getAdapter().getMessages().get(position)).message).type == MessageConversation.Type.SYSTEM) {
            return RowPosition.SINGLE;
        }
        return super.getRowPositionType(position, this.messagesReadStatusAdapter.getEmbeddedItemsCountBeforeViewPosition(position) + position);
    }

    protected String getCustomTagForPositionInternal(MessageConversation messageBase) {
        return messageBase.type.name();
    }

    public void onLikeCountClicked(String messageId) {
        UsersLikedMessageFragment.newInstance(getConversationId(), messageId).show(getFragmentManager(), "likes");
    }

    protected int getAllLoadedMessageId() {
        return 2131166203;
    }

    protected MessageAuthor getCurrentAuthor() {
        return new MessageAuthor(OdnoklassnikiApplication.getCurrentUser().uid, null);
    }

    protected boolean isCommentingAllowed(Conversation conversation) {
        return !TextUtils.isEmpty(getConversationId()) && (conversation == null || (conversation.capabilities != null && conversation.capabilities.canPost));
    }

    protected int getMessagingDisabledHintId() {
        return 2131166204;
    }

    protected int getNoMessagesTextId() {
        return 2131166275;
    }

    protected String getSettingsName() {
        return "conversation-" + getConversationId();
    }

    protected boolean isContextMenuCreationIntercepted(MessageConversation message) {
        if (message.type != MessageConversation.Type.SYSTEM) {
            return false;
        }
        Set<String> mentionedUsers = ru.ok.java.api.utils.Utils.extractUserIds(message.text);
        mentionedUsers.remove(OdnoklassnikiApplication.getCurrentUser().uid);
        if (!mentionedUsers.isEmpty()) {
            UsersByIdFragment fragment = UsersByIdFragment.newInstance(new ArrayList(mentionedUsers), 2131166805, false);
            fragment.setTargetFragment(this, 1004);
            fragment.show(getFragmentManager(), "users-list");
        }
        return true;
    }

    @Subscribe(on = 2131623946, to = 2131624134)
    public void onSetTopicResult(BusEvent event) {
        if (TextUtils.equals(event.bundleInput.getString("CONVERSATION_ID"), getConversationId())) {
            EditTopicPopup dialog = (EditTopicPopup) getFragmentManager().findFragmentByTag("change-topic");
            if (event.resultCode == -2) {
                ErrorType error = ErrorType.from(event.bundleOutput);
                int errorMsg = error.getDefaultErrorMessage();
                if (error == ErrorType.CENSOR_MATCH) {
                    errorMsg = 2131165575;
                } else if (error == ErrorType.GENERAL) {
                    errorMsg = 2131165574;
                }
                showTimedToastIfVisible(errorMsg, 0);
                if (dialog != null) {
                    dialog.updateSaveButtonAndProgress(true, false);
                    return;
                }
                return;
            }
            if (dialog != null) {
                getFragmentManager().beginTransaction().remove(dialog).commit();
            }
            this.loadNewMessagesHandler.removeMessages(0);
            ((MessagesConversationLoader) getLoader()).loadNew(true);
        }
    }

    public void onRefresh() {
        super.onRefresh();
        this.loadNewMessagesHandler.removeMessages(0);
    }

    protected int getWriteMessageHintId() {
        return 2131165336;
    }

    protected boolean isUserBlocked(MessagesBundle<MessageConversation, Conversation> messagesBundle) {
        return false;
    }

    protected boolean isResetAdminState(MessageConversation message) {
        return false;
    }

    protected int getMessageMaxLength() {
        return ServicesSettingsHelper.getServicesSettings().getMultichatMaxTextLength();
    }

    protected int getErrorTextId(ErrorType errorType) {
        if (errorType != ErrorType.DISCUSSION_DELETED_OR_BLOCKED) {
            return super.getErrorTextId(errorType);
        }
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                ConversationsCache.getInstance().removeConversation(MessagesFragment.this.getConversationId());
                return null;
            }

            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                MessagesFragment.this.leaveConversation();
            }
        }.execute(new Void[0]);
        return 2131166889;
    }

    @Subscribe(on = 2131623946, to = 2131624132)
    public void onUserKicked(BusEvent event) {
        if (getActivity() != null && TextUtils.equals(event.bundleInput.getString("CONVERSATION_ID"), getConversationId()) && event.resultCode == -1) {
            this.loadNewMessagesHandler.removeMessages(0);
            ((MessagesConversationLoader) getLoader()).loadNew(true);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624138)
    public void onConversationDeleted(BusEvent event) {
        if (!TextUtils.equals(event.bundleInput.getString("CONVERSATION_ID"), getConversationId())) {
            return;
        }
        if (event.resultCode != -1) {
            showToastIfVisible(2131165676, 0);
        } else {
            leaveConversation();
        }
    }

    protected int getFooterHeight() {
        return ComposingView.COMPOSING_VIEW_HEIGHT;
    }

    protected void onPresetAdapter() {
        super.onPresetAdapter();
        View spacerForComposingView = new View(getContext());
        spacerForComposingView.setLayoutParams(new LayoutParams(0, getFooterHeight()));
        this.list.addFooterView(spacerForComposingView, null, false);
    }

    protected void initList(ViewGroup view) {
        MessagesReadStatusAdapter messagesReadStatusAdapter = getMessagesReadStatusAdapter();
        super.initList(view);
        messagesReadStatusAdapter.finalInit();
        if (DeviceUtils.isTablet(getContext())) {
            this.header = (ViewGroup) view.findViewById(2131625100);
            this.header.bringToFront();
        }
        this.composingView = (ComposingView) view.findViewById(2131625095);
        this.composingView.setImageLoadBlocker(getAdapter().getBlocker());
        messagesReadStatusAdapter.setHandleBlocker(getAdapter().getBlocker());
        if (!(this.odklMessagingEventsServiceBinder == null || this.composingView == null)) {
            this.odklMessagingEventsServiceBinder.addChatStateHandler(this.composingView);
        }
        createHeaderView();
    }

    protected void positionListOnFirstPortion(ListView list) {
        positionOnFirstUnread(getAdapter().getData());
    }

    protected int getMessageMenuId() {
        return 2131689506;
    }

    protected int getListBackgroundResourceId() {
        return 2130837675;
    }

    protected final boolean isMessageCopyAllowed(MessageConversation message) {
        return super.isMessageCopyAllowed(message) && message.type == MessageConversation.Type.USER;
    }

    protected int getGeneralErrorTextId() {
        return 2131166512;
    }

    protected int getMessageEditTitleResourceId() {
        return 2131165727;
    }

    protected void setupFirstPortionAnimations(UpdateListDataCommandBuilder<MessagesBundle<MessageConversation, Conversation>> updateListDataCommandBuilder) {
    }

    private void initStateReporter() {
        if (!(this.chatStateReporter != null || this.createMessageView == null || this.odklMessagingEventsServiceBinder == null)) {
            this.chatStateReporter = new ChatStateReporter(XmppSettingsPreferences.getXmppSettingsContainer(getContext()));
            this.createMessageView.addTextWatcher(this.chatStateReporter);
            this.chatStateReporter.setTargetService(this.odklMessagingEventsServiceBinder);
        }
        if (this.chatStateReporter != null && this.conversationInfo != null) {
            this.chatStateReporter.initializeActiveChat(this.conversationInfo);
        }
    }

    @NonNull
    protected Sectionizer<MessagesReadStatusAdapter> createSectionizer(Activity activity) {
        return new AnonymousClass12(activity);
    }

    protected int convertDataIndexToViewPosition(int dataIndex) {
        int indexInMessageReadStatusAdapter = dataIndex + this.messagesReadStatusAdapter.getEmbeddedItemsCountBeforeViewPosition(dataIndex);
        return ((this.sectionAdapter.getSectionsCountPriorDataPosition(indexInMessageReadStatusAdapter) + indexInMessageReadStatusAdapter) + this.list.getHeaderViewsCount()) + getLoadMoreController().getExtraTopElements();
    }

    protected int convertViewPositionToRawDataIndex(int viewPosition) {
        return this.messagesReadStatusAdapter.getDataIndexForPosition(this.sectionAdapter.getDataIndexForPosition(viewPosition - (this.list.getHeaderViewsCount() + getLoadMoreController().getExtraTopElements())));
    }
}
