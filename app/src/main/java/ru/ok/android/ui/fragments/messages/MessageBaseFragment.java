package ru.ok.android.ui.fragments.messages;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import io.github.eterverda.sntp.SNTP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jivesoftware.smack.packet.Stanza;
import ru.mail.libverify.C0176R;
import ru.ok.android.C0206R;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.base.OfflineTable;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.emoji.container.RelativePanelLayout;
import ru.ok.android.emoji.smiles.SmileTextProcessor;
import ru.ok.android.emoji.stickers.StickersSet;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.AttachmentUtils;
import ru.ok.android.services.app.NotifyReceiver;
import ru.ok.android.services.app.notification.NotificationSignal;
import ru.ok.android.services.processors.banners.BannerLinksUtils;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.stickers.StickersHelper;
import ru.ok.android.services.processors.stickers.StickersHelper.StickerHelperListener;
import ru.ok.android.services.processors.stickers.StickersManager;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.adapters.section.Sectionizer;
import ru.ok.android.ui.adapters.section.SimpleSectionAdapter;
import ru.ok.android.ui.custom.CreateMessageView;
import ru.ok.android.ui.custom.CreateMessageView.OnAudioAttachListener;
import ru.ok.android.ui.custom.CreateMessageView.OnPhotoAttachClickListener;
import ru.ok.android.ui.custom.CreateMessageView.OnSendMessageClickListener;
import ru.ok.android.ui.custom.CreateMessageView.OnVideoAttachClickListener;
import ru.ok.android.ui.custom.OkViewStub;
import ru.ok.android.ui.custom.OnSizeChangedListener;
import ru.ok.android.ui.custom.ReplyToCommentView;
import ru.ok.android.ui.custom.ReplyToCommentView.ReplyToCommentListener;
import ru.ok.android.ui.custom.animationlist.AnimateChangesListView;
import ru.ok.android.ui.custom.animationlist.RowInfo;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.ListFinalPositionCallback;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.ListInitialPositionCallback;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.ListRestorePositionCallback;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.OnDataSetCallback;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.UpdateListDataCommandBuilder;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.OnRepeatClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.WebState;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapter;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapterListener;
import ru.ok.android.ui.custom.loadmore.LoadMoreConditionCallback;
import ru.ok.android.ui.custom.loadmore.LoadMoreController;
import ru.ok.android.ui.custom.loadmore.LoadMoreMode;
import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;
import ru.ok.android.ui.custom.scroll.ScrollListenerSet;
import ru.ok.android.ui.custom.scroll.ScrollTopView;
import ru.ok.android.ui.custom.scroll.ScrollTopView.NewEventsMode;
import ru.ok.android.ui.custom.scroll.ScrollTopView.OnClickScrollListener;
import ru.ok.android.ui.custom.scroll.ScrollTopViewScrollListener;
import ru.ok.android.ui.custom.scroll.ScrollTopViewScrollListener.ScrollTopViewScrollListenerCallback;
import ru.ok.android.ui.dialogs.SendErrorDialog;
import ru.ok.android.ui.dialogs.SendErrorDialog.SendErrorDialogListener;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter;
import ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.AttachmentSelectionListener;
import ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter.MessagesAdapterListener;
import ru.ok.android.ui.fragments.messages.helpers.MessagesSettingsHelper;
import ru.ok.android.ui.fragments.messages.loaders.MessagesBaseLoader;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesBundle;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesLoaderBundle;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesLoaderBundle.ChangeReason;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.ui.fragments.messages.loaders.data.RepliedToInfo;
import ru.ok.android.ui.fragments.messages.loaders.data.RepliedToInfo.Status;
import ru.ok.android.ui.image.view.PhotoLayerAnimationHelper;
import ru.ok.android.ui.messaging.activity.PayStickersActivity;
import ru.ok.android.ui.swiperefresh.SwipeUpRefreshLayout;
import ru.ok.android.ui.tabbar.HideTabbarListener;
import ru.ok.android.ui.tabbar.OdklTabbar;
import ru.ok.android.ui.tabbar.manager.BaseTabbarManager;
import ru.ok.android.ui.utils.RowPosition;
import ru.ok.android.ui.utils.RowPositionUtils;
import ru.ok.android.utils.AudioPlaybackController;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.animation.AnimationBundleHandler;
import ru.ok.android.utils.animation.AnimationBundleHandler.PhotoIdExtractor;
import ru.ok.android.utils.animation.SyncBus.MessageCallback;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.refresh.RefreshProvider;
import ru.ok.android.utils.refresh.RefreshProviderOnRefreshListener;
import ru.ok.android.utils.refresh.SwipeUpRefreshProvider;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.model.UserInfo;
import ru.ok.model.messages.Attachment;
import ru.ok.model.messages.Attachment.AttachmentType;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase;
import ru.ok.model.messages.MessageBase.RepliedTo;

public abstract class MessageBaseFragment<M extends MessageBase, G extends Parcelable, L extends MessagesBaseLoader<M, G>> extends BaseFragment implements LoaderCallbacks<MessagesLoaderBundle<M, G>>, OnMenuItemClickListener, OnItemClickListener, StickerHelperListener, OnAudioAttachListener, OnPhotoAttachClickListener, OnSendMessageClickListener, OnVideoAttachClickListener, ReplyToCommentListener, OnRepeatClickListener, LoadMoreAdapterListener, OnClickScrollListener, AttachmentSelectionListener, MessagesAdapterListener<M>, MessageCallback, RefreshProviderOnRefreshListener {
    private ActionMode actionMode;
    private final Callback actionModeCallback;
    private AnimationBundleHandler animationBundleHandler;
    private OkViewStub audioButtonsStub;
    protected CreateMessageView createMessageView;
    private RelativePanelLayout emojiLayout;
    protected AnimateChangesListView<MessagesBundle<M, G>> list;
    protected LoadMoreAdapter loadMoreAdapter;
    protected MessagesBaseAdapter messagesAdapter;
    protected L messagesLoader;
    private View networkStatusView;
    protected ScrollTopView newMessagesView;
    private SmartEmptyView noMessagesView;
    protected RefreshProvider refreshProvider;
    protected ReplyToCommentView replyTo;
    protected SimpleSectionAdapter sectionAdapter;
    private MessagesSettingsHelper settings;
    private StickersHelper stickersHelper;
    SmartEmptyView wholeEmptyView;

    /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.16 */
    class AnonymousClass16 implements OnDataSetCallback<MessagesBundle<M, G>> {
        final /* synthetic */ MessagesLoaderBundle val$loaderData;

        AnonymousClass16(MessagesLoaderBundle messagesLoaderBundle) {
            this.val$loaderData = messagesLoaderBundle;
        }

        public Boolean onPreDataSet(MessagesBundle<M, G> data) {
            MessageBaseFragment.this.disableAutoLoading();
            MessageBaseFragment.this.processNextPortion(data);
            if (this.val$loaderData.hasUnreadData) {
                if (MessageBaseFragment.this.isFragmentVisible()) {
                    NotificationSignal.notifyWithTypeNoNotification(MessageBaseFragment.this.getActivity(), NotifyReceiver.getNotificationsSettingsWithSound(MessageBaseFragment.this.getActivity(), false));
                }
                MessageBaseFragment.this.newMessagesView.setNewEventCount(1, false);
            }
            return Boolean.valueOf(MessageBaseFragment.this.isLastElementVisible());
        }

        public void onPostDataSet(MessagesBundle<M, G> messagesBundle) {
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.17 */
    class AnonymousClass17 implements ListFinalPositionCallback<MessagesBundle<M, G>> {
        final /* synthetic */ MessagesLoaderBundle val$loaderData;

        AnonymousClass17(MessagesLoaderBundle messagesLoaderBundle) {
            this.val$loaderData = messagesLoaderBundle;
        }

        public boolean setFinalPosition(MessagesBundle<M, G> messagesBundle, MessagesBundle<M, G> newData, Object atListEnd) {
            MessageBaseFragment.this.enableAutoLoading();
            if (!((Boolean) atListEnd).booleanValue() || !this.val$loaderData.hasNewData) {
                return false;
            }
            MessageBaseFragment.this.positionOnFirstUnread(newData);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.1 */
    class C08401 implements PhotoIdExtractor {
        C08401() {
        }

        public String getViewPhotoId(View view) {
            return (String) view.getTag(2131624331);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.26 */
    class AnonymousClass26 implements SendErrorDialogListener {
        final /* synthetic */ OfflineMessage val$message;

        AnonymousClass26(OfflineMessage offlineMessage) {
            this.val$message = offlineMessage;
        }

        public void onResendClicked() {
            Logger.m172d("");
            MessageBaseFragment.this.tryResendMessage(this.val$message);
        }

        public void onUndoEditClicked() {
            Logger.m172d("");
            MessageBaseFragment.this.getLoader().undoMessageEdit(this.val$message);
        }

        public void onPayStickersClicked() {
            Logger.m172d("");
            MessageBaseFragment.this.startPayStickersActivity(MessageBaseFragment.this.getContext());
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.27 */
    static /* synthetic */ class AnonymousClass27 {
        static final /* synthetic */ int[] f104x5c76f804;

        static {
            f104x5c76f804 = new int[ChangeReason.values().length];
            try {
                f104x5c76f804[ChangeReason.FIRST.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f104x5c76f804[ChangeReason.PREVIOUS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f104x5c76f804[ChangeReason.NEXT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f104x5c76f804[ChangeReason.NEW.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f104x5c76f804[ChangeReason.ADDED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f104x5c76f804[ChangeReason.SPAM.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f104x5c76f804[ChangeReason.UPDATED.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.2 */
    class C08412 implements Runnable {
        C08412() {
        }

        public void run() {
            SNTP.safeCurrentTimeMillis();
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.3 */
    class C08423 implements LoadMoreConditionCallback {
        C08423() {
        }

        public boolean isTimeToLoadTop(int position, int count) {
            return MessageBaseFragment.this.isTimeToLoadTop(position);
        }

        public boolean isTimeToLoadBottom(int position, int count) {
            return position >= count + -25;
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.4 */
    class C08434 implements ScrollTopViewScrollListenerCallback {
        C08434() {
        }

        public boolean isAllEventRead(AbsListView view) {
            return view.getLastVisiblePosition() >= MessageBaseFragment.this.loadMoreAdapter.getCount() + -1;
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.5 */
    class C08445 implements Sectionizer<MessagesBaseAdapter> {
        final /* synthetic */ Activity val$activity;

        C08445(Activity activity) {
            this.val$activity = activity;
        }

        public String getSectionTitleForItem(MessagesBaseAdapter adapter, int index) {
            OfflineMessage message = adapter.getItem(index);
            if (message == null) {
                return null;
            }
            return DateFormatter.getFormatStringFromDateNoTime(this.val$activity, message.message.date);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.6 */
    class C08466 implements OnSizeChangedListener {

        /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.6.1 */
        class C08451 implements Runnable {
            final /* synthetic */ int val$height;
            final /* synthetic */ int val$oldHeight;

            C08451(int i, int i2) {
                this.val$oldHeight = i;
                this.val$height = i2;
            }

            public void run() {
                MessageBaseFragment.this.scroll(this.val$oldHeight - this.val$height);
            }
        }

        C08466() {
        }

        public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
            if (MessageBaseFragment.this.messagesAdapter.getCount() > 0 && height < oldHeight && width == oldWidth) {
                MessageBaseFragment.this.getView().post(new C08451(oldHeight, height));
            }
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.7 */
    class C08477 implements ListFinalPositionCallback<MessagesBundle<M, G>> {
        final /* synthetic */ int val$delta;

        C08477(int i) {
            this.val$delta = i;
        }

        public boolean setFinalPosition(MessagesBundle<M, G> messagesBundle, MessagesBundle<M, G> messagesBundle2, Object userData) {
            Pair<Integer, Integer> savedPosition = (Pair) userData;
            MessageBaseFragment.this.list.setSelectionFromTop(((Integer) savedPosition.first).intValue(), ((Integer) savedPosition.second).intValue() - this.val$delta);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.8 */
    class C08488 implements OnDataSetCallback<MessagesBundle<M, G>> {
        C08488() {
        }

        public Object onPreDataSet(MessagesBundle<M, G> messagesBundle) {
            return new Pair(Integer.valueOf(MessageBaseFragment.this.list.getLastVisiblePosition()), Integer.valueOf(MessageBaseFragment.this.list.getChildAt(MessageBaseFragment.this.list.getChildCount() - 1).getTop()));
        }

        public void onPostDataSet(MessagesBundle<M, G> messagesBundle) {
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.MessageBaseFragment.9 */
    class C08499 implements OnDataSetCallback<MessagesBundle<M, G>> {
        C08499() {
        }

        public Void onPreDataSet(MessagesBundle<M, G> data) {
            MessageBaseFragment.this.disableAutoLoading();
            MessageBaseFragment.this.processFirstPortion(data);
            return null;
        }

        public void onPostDataSet(MessagesBundle<M, G> messagesBundle) {
        }
    }

    private class MessagesActionModeCallback implements Callback {
        private MenuItem deleteItem;
        private MenuItem spamItem;

        private MessagesActionModeCallback() {
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (MessageBaseFragment.this.inflateMenuLocalized(2131689509, menu)) {
                this.deleteItem = menu.findItem(C0263R.id.delete);
                this.spamItem = menu.findItem(2131625446);
            }
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            boolean z;
            boolean z2 = false;
            SparseBooleanArray array = MessageBaseFragment.this.list.getCheckedItemPositions();
            int checkedCount = 0;
            boolean deleteAllowing = true;
            boolean spamAllowing = true;
            int size = array == null ? 0 : array.size();
            for (int i = 0; i < size; i++) {
                int position = array.keyAt(i);
                if (array.get(position)) {
                    checkedCount++;
                    OfflineMessage<M> message = MessageBaseFragment.this.getAdapter().getItem(MessageBaseFragment.this.convertViewPositionToRawDataIndex(position));
                    deleteAllowing &= message.message.flags.deletionAllowed;
                    spamAllowing &= message.message.flags.markAsSpamAllowed;
                }
            }
            MenuItem menuItem = this.deleteItem;
            if (checkedCount <= 0 || !deleteAllowing) {
                z = false;
            } else {
                z = true;
            }
            menuItem.setEnabled(z);
            MenuItem menuItem2 = this.spamItem;
            if (checkedCount > 0 && spamAllowing) {
                z2 = true;
            }
            menuItem2.setEnabled(z2);
            return true;
        }

        @TargetApi(11)
        private ArrayList<OfflineMessage<M>> getSelectedMessages() {
            SparseBooleanArray positions = MessageBaseFragment.this.list.getCheckedItemPositions();
            ArrayList<OfflineMessage<M>> result = new ArrayList();
            if (positions != null) {
                for (int i = 0; i < positions.size(); i++) {
                    int position = positions.keyAt(i);
                    if (positions.get(position)) {
                        result.add((OfflineMessage) MessageBaseFragment.this.loadMoreAdapter.getItem(position - MessageBaseFragment.this.list.getHeaderViewsCount()));
                    }
                }
            }
            return result;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case C0263R.id.delete /*2131624801*/:
                    MessageBaseFragment.this.getLoader().deleteMessages(getSelectedMessages(), false);
                    MessageBaseFragment.this.leaveEditMode();
                    return true;
                case 2131625446:
                    MessageBaseFragment.this.getLoader().spamMessages(getSelectedMessages());
                    MessageBaseFragment.this.leaveEditMode();
                    return true;
                default:
                    return false;
            }
        }

        public void onDestroyActionMode(ActionMode mode) {
            MessageBaseFragment.this.actionMode = null;
            this.spamItem = null;
            this.deleteItem = null;
            MessageBaseFragment.this.leaveEditMode();
        }
    }

    public enum Page {
        MESSAGES(1),
        INFO(0);
        
        public final int index;

        private Page(int index) {
            this.index = index;
        }

        public static Page byIndex(int position) {
            for (Page page : values()) {
                if (page.index == position) {
                    return page;
                }
            }
            return null;
        }
    }

    protected abstract MessagesBaseAdapter<M, G> createMessagesAdapter();

    protected abstract L createMessagesLoader();

    protected abstract int getAllLoadedMessageId();

    protected abstract MessageAuthor getCurrentAuthor();

    protected abstract String getCustomTagForPositionInternal(M m);

    protected abstract int getGeneralErrorTextId();

    protected abstract int getListBackgroundResourceId();

    protected abstract int getMessageEditTitleResourceId();

    protected abstract int getMessageMenuId();

    protected abstract int getMessagingDisabledHintId();

    protected abstract int getNoMessagesTextId();

    protected abstract String getSettingsName();

    protected abstract int getShowAttachSourceId();

    protected abstract int getWriteMessageHintId();

    protected abstract boolean isCommentingAllowed(G g);

    protected abstract boolean isResetAdminState(M m);

    protected abstract boolean isUserBlocked(MessagesBundle<M, G> messagesBundle);

    protected abstract void positionListOnFirstPortion(ListView listView);

    protected abstract void processResultCustom(MessagesBundle<M, G> messagesBundle);

    protected abstract void setupFirstPortionAnimations(UpdateListDataCommandBuilder<MessagesBundle<M, G>> updateListDataCommandBuilder);

    public MessageBaseFragment() {
        this.actionModeCallback = new MessagesActionModeCallback();
    }

    public void onLikeCountClicked(String commentId) {
    }

    public void onStart() {
        super.onStart();
        PhotoLayerAnimationHelper.registerCallback(1, this);
        PhotoLayerAnimationHelper.registerCallback(2, this);
        PhotoLayerAnimationHelper.registerCallback(3, this);
        if (!isHidden() && this.messagesLoader != null && this.messagesLoader.isDataPresents()) {
            this.messagesLoader.loadNew(true);
        }
    }

    public void onStop() {
        super.onStop();
        PhotoLayerAnimationHelper.unregisterCallback(1, this);
        PhotoLayerAnimationHelper.unregisterCallback(2, this);
        PhotoLayerAnimationHelper.unregisterCallback(3, this);
        if (this.createMessageView != null) {
            this.createMessageView.handleStop();
        }
    }

    private AnimationBundleHandler getAnimationBundleHandler() {
        if (this.animationBundleHandler == null && this.list != null) {
            this.animationBundleHandler = new AnimationBundleHandler(this.list, new C08401());
        }
        return this.animationBundleHandler;
    }

    public Bundle onMessage(Message message) {
        String photoId = message.getData().getString("id");
        AnimationBundleHandler handler = getAnimationBundleHandler();
        return handler != null ? handler.onMessage(message, photoId) : null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThreadUtil.execute(new C08412());
        StickersManager.callUpdateStickersSet(getContext());
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCreateMessageView(view);
        initReplyTo(view);
        initEmptyView(view);
        initStickersHelper(savedInstanceState);
        startLoaders();
    }

    protected void initReplyTo(View view) {
        this.replyTo = (ReplyToCommentView) view.findViewById(2131625092);
        if (this.replyTo != null) {
            this.replyTo.bringToFront();
            if (this.audioButtonsStub != null) {
                this.audioButtonsStub.bringToFront();
            }
            this.replyTo.setListener(this);
        }
    }

    protected BaseAdapter getAdapterForSectionAdapter() {
        return getAdapter();
    }

    protected void initList(ViewGroup view) {
        SwipeUpRefreshLayout swipeUpRefreshLayout = (SwipeUpRefreshLayout) view.findViewById(2131625093);
        swipeUpRefreshLayout.setBackgroundResource(getListBackgroundResourceId());
        this.refreshProvider = new SwipeUpRefreshProvider(swipeUpRefreshLayout);
        this.refreshProvider.setOnRefreshListener(this);
        this.list = (AnimateChangesListView) view.findViewById(2131625094);
        this.list.setOnItemClickListener(this);
        this.list.setChoiceMode(0);
        tieLoaderAndList();
        registerForContextMenu(this.list);
        this.sectionAdapter = new SimpleSectionAdapter(getActivity(), getAdapterForSectionAdapter(), 2130903431, C0176R.id.title, createSectionizer(getActivity()));
        this.loadMoreAdapter = createLoadMoreAdapter(this.sectionAdapter, this.list);
        LoadMoreController loadMoreController = this.loadMoreAdapter.getController();
        loadMoreController.setBottomAutoLoad(false);
        loadMoreController.setConditionCallback(new C08423());
        onPresetAdapter();
        this.list.setAdapter(this.loadMoreAdapter);
        this.list.setDataAdapter(this.messagesAdapter);
        setListSizeChangeListener();
        this.sectionAdapter.finalInit();
        loadMoreController.setTopMessageForState(LoadMoreState.LOAD_IMPOSSIBLE, getAllLoadedMessageId());
        loadMoreController.setTopPermanentState(LoadMoreState.DISABLED);
        loadMoreController.setBottomPermanentState(LoadMoreState.DISABLED);
        initNewMessagesView(view);
        this.list.setOnScrollListener(new ScrollListenerSet().addListener(getAdapter().getScrollListener()).addListener(new ScrollTopViewScrollListener(this.newMessagesView, null)).addListener(new ScrollTopViewScrollListener(this.newMessagesView, new C08434())));
        this.noMessagesView = (SmartEmptyView) view.findViewById(2131625096);
        this.noMessagesView.setEmptyText(getNoMessagesTextId());
        this.networkStatusView = view.findViewById(2131624730);
        Loader loader = getLoader();
        if (loader != null && loader.getBundle() != null) {
            onLoadFinished(loader, loader.getLastData());
        }
    }

    protected boolean isTimeToLoadTop(int position) {
        return position <= 25;
    }

    @NonNull
    protected Sectionizer createSectionizer(Activity activity) {
        return new C08445(activity);
    }

    protected void onPresetAdapter() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(getLayoutId(), container, false);
        initSettings();
        return view;
    }

    protected void initSettings() {
        this.settings = new MessagesSettingsHelper(getActivity());
    }

    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        if (BaseCompatToolbarActivity.isUseTabbar(activity)) {
            BaseTabbarManager tabbarManager = (BaseTabbarManager) activity;
            OdklTabbar tabbarView = tabbarManager.getTabbarView();
            if (tabbarView != null) {
                HideTabbarListener.animateTo(tabbarManager, tabbarView.getHeight());
            }
        }
        if (this.createMessageView != null) {
            this.createMessageView.clearFocus();
        }
    }

    protected void setListSizeChangeListener() {
        this.list.setOnSizeChangedListener(new C08466());
    }

    protected void scroll(int delta) {
        if (getLoader() != null && getLoader().isDataPresents()) {
            UpdateListDataCommandBuilder<MessagesBundle<M, G>> builder = new UpdateListDataCommandBuilder();
            builder.doNotChangeData(true).withOnDataSet(new C08488()).withListFinalPosition(new C08477(delta)).withInterpolator(new DecelerateInterpolator()).withDuration(250);
            this.list.setData(builder.build());
        }
    }

    private void initNewMessagesView(ViewGroup view) {
        this.newMessagesView = (ScrollTopView) view.findViewById(2131625097);
        this.newMessagesView.setOnClickScrollListener(this);
        setupNewMessagesView();
    }

    protected void setupNewMessagesView() {
        this.newMessagesView.setNewEventsMode(NewEventsMode.STRAIGHT_ARROW);
    }

    protected void initCreateMessageView(View view) {
        this.createMessageView = (CreateMessageView) view.findViewById(2131625091);
        if (this.createMessageView != null) {
            this.createMessageView.setPermissionRequester(PermissionUtils.createRequester(this, 130));
            this.createMessageView.bringToFront();
            this.audioButtonsStub = (OkViewStub) view.findViewById(2131625099);
            if (this.audioButtonsStub != null) {
                this.audioButtonsStub.bringToFront();
                this.createMessageView.setAudioRecordingControlsStub(view, this.audioButtonsStub);
            }
            updateCreateMessageViewMode();
            this.createMessageView.setOnSendMessageClickListener(this);
            this.createMessageView.setOnPhotoAttachListener(this);
            this.createMessageView.setOnVideoAttachListener(this);
            this.createMessageView.setAttachAudioListener(this);
            this.createMessageView.setMaxTextLength(getMessageMaxLength());
            updateSendMessageAllowedState();
            this.createMessageView.setText(this.settings.getMessageDraft(OdnoklassnikiApplication.getCurrentUser(), getSettingsName()));
            this.emojiLayout = (RelativePanelLayout) view.findViewById(2131625090);
            this.createMessageView.setCircleVisible(StickersSet.hasNew(StickersManager.getCurrentSet4Lib(getContext())));
        }
    }

    protected void updateCreateMessageViewMode() {
        if (this.createMessageView != null) {
            this.createMessageView.setSendMode(isSendAudioAttachEnabled(null) ? 0 : 1);
        }
    }

    protected void updateSendMessageAllowedState() {
        G generalInfo = null;
        if (this.createMessageView != null) {
            MessagesBundle<M, G> info;
            if (getLoader() != null) {
                info = getLoader().getBundle();
            } else {
                info = null;
            }
            if (info != null) {
                generalInfo = (Parcelable) info.generalInfo;
            }
            boolean commentAllowed = isCommentingAllowed(generalInfo);
            boolean audioAttachAllowed = isSendAudioAttachEnabled(generalInfo);
            boolean videoAttachAllowed = isSendVideoAttachEnabled(generalInfo);
            if (!commentAllowed) {
                this.createMessageView.setText("");
            }
            CreateMessageView createMessageView = this.createMessageView;
            int writeMessageHintId = info != null ? commentAllowed ? getWriteMessageHintId() : getMessagingDisabledHintId() : 0;
            createMessageView.setHintId(writeMessageHintId);
            this.createMessageView.setEnabledStates(commentAllowed, audioAttachAllowed, videoAttachAllowed, true);
        }
    }

    private void startLoaders() {
        getLoaderManager().restartLoader(0, null, this);
    }

    public MessagesBaseLoader<M, G> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case RECEIVED_VALUE:
                this.messagesLoader = createMessagesLoader();
                tieLoaderAndList();
                return this.messagesLoader;
            default:
                throw new IllegalArgumentException(String.format("Id %d for loader not recognized", new Object[]{Integer.valueOf(id)}));
        }
    }

    private void tieLoaderAndList() {
        if (this.list != null && this.messagesLoader != null) {
            this.list.addOnIdleListener(this.messagesLoader);
            this.messagesLoader.setAnimationChangeListView(this.list);
        }
    }

    public void onLoadFinished(Loader<MessagesLoaderBundle<M, G>> loader, MessagesLoaderBundle<M, G> loaderData) {
        boolean z = true;
        if (this.list != null) {
            switch (loader.getId()) {
                case RECEIVED_VALUE:
                    loaderData.processed = true;
                    if (loaderData.errorType != null) {
                        processMessagesLoadingError(loaderData.reason, loaderData.errorType);
                        return;
                    }
                    UpdateListDataCommandBuilder<MessagesBundle<M, G>> builder = new UpdateListDataCommandBuilder();
                    builder.withData(loaderData.bundle);
                    OnDataSetCallback<MessagesBundle<M, G>> onDataSetCallback = null;
                    ListFinalPositionCallback<MessagesBundle<M, G>> finalPositionCallback = null;
                    updateCreateMessageViewMode();
                    switch (AnonymousClass27.f104x5c76f804[loaderData.reason.ordinal()]) {
                        case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                            onDataSetCallback = new C08499();
                            finalPositionCallback = new ListFinalPositionCallback<MessagesBundle<M, G>>() {
                                public boolean setFinalPosition(MessagesBundle<M, G> messagesBundle, MessagesBundle<M, G> messagesBundle2, Object none) {
                                    MessageBaseFragment.this.positionListOnFirstPortion(MessageBaseFragment.this.list);
                                    MessageBaseFragment.this.enableAutoLoading();
                                    return true;
                                }
                            };
                            setupFirstPortionAnimations(builder);
                            break;
                        case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                            onDataSetCallback = new OnDataSetCallback<MessagesBundle<M, G>>() {
                                public Void onPreDataSet(MessagesBundle<M, G> data) {
                                    MessageBaseFragment.this.disableAutoLoading();
                                    MessageBaseFragment.this.processPreviousPortion(data);
                                    return null;
                                }

                                public void onPostDataSet(MessagesBundle<M, G> messagesBundle) {
                                }
                            };
                            finalPositionCallback = new ListFinalPositionCallback<MessagesBundle<M, G>>() {
                                public boolean setFinalPosition(MessagesBundle<M, G> messagesBundle, MessagesBundle<M, G> messagesBundle2, Object none) {
                                    MessageBaseFragment.this.enableAutoLoading();
                                    return false;
                                }
                            };
                            builder.withRestorePosition(new ListRestorePositionCallback() {
                                public void onRestorePosition(RowInfo initialRowInfo, int viewIndex) {
                                    MessageBaseFragment.this.list.setSelectionFromTop(viewIndex, initialRowInfo.bottom - MessageBaseFragment.this.measureRowView(viewIndex).getMeasuredHeight());
                                }
                            });
                            break;
                        case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                            onDataSetCallback = new OnDataSetCallback<MessagesBundle<M, G>>() {
                                public Void onPreDataSet(MessagesBundle<M, G> data) {
                                    MessageBaseFragment.this.disableAutoLoading();
                                    MessageBaseFragment.this.processNextPortion(data);
                                    return null;
                                }

                                public void onPostDataSet(MessagesBundle<M, G> messagesBundle) {
                                }
                            };
                            finalPositionCallback = new ListFinalPositionCallback<MessagesBundle<M, G>>() {
                                public boolean setFinalPosition(MessagesBundle<M, G> messagesBundle, MessagesBundle<M, G> messagesBundle2, Object atListEnd) {
                                    MessageBaseFragment.this.enableAutoLoading();
                                    return false;
                                }
                            };
                            builder.saveListPosition(true);
                            break;
                        case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                            onDataSetCallback = new AnonymousClass16(loaderData);
                            finalPositionCallback = new AnonymousClass17(loaderData);
                            builder.saveListPosition(loaderData.hasUnreadData);
                            if (loaderData.hasNewData) {
                                z = false;
                            }
                            builder.doNotChangeData(z);
                            break;
                        case MessagesProto.Message.UUID_FIELD_NUMBER /*5*/:
                            onDataSetCallback = new OnDataSetCallback<MessagesBundle<M, G>>() {
                                public Boolean onPreDataSet(MessagesBundle<M, G> data) {
                                    MessageBaseFragment.this.disableAutoLoading();
                                    MessageBaseFragment.this.processMessageAdded(data);
                                    return Boolean.valueOf(false);
                                }

                                public void onPostDataSet(MessagesBundle<M, G> messagesBundle) {
                                }
                            };
                            finalPositionCallback = new ListFinalPositionCallback<MessagesBundle<M, G>>() {
                                public boolean setFinalPosition(MessagesBundle<M, G> messagesBundle, MessagesBundle<M, G> messagesBundle2, Object none) {
                                    MessageBaseFragment.this.enableAutoLoading();
                                    MessageBaseFragment.this.selectLastRow();
                                    return true;
                                }
                            };
                            builder.withListInitialPosition(new ListInitialPositionCallback<MessagesBundle<M, G>>() {
                                public boolean isWantToChangePosition(MessagesBundle<M, G> messagesBundle, MessagesBundle<M, G> messagesBundle2) {
                                    return true;
                                }

                                public void setInitialPosition() {
                                    MessageBaseFragment.this.list.setSelection(MessageBaseFragment.this.list.getCount() - 1);
                                }
                            });
                            break;
                        case MessagesProto.Message.REPLYTO_FIELD_NUMBER /*6*/:
                            showToastIfVisible(2131166067, 1);
                            break;
                        case MessagesProto.Message.ATTACHES_FIELD_NUMBER /*7*/:
                            break;
                    }
                    onDataSetCallback = new OnDataSetCallback<MessagesBundle<M, G>>() {
                        public Void onPreDataSet(MessagesBundle<M, G> data) {
                            MessageBaseFragment.this.disableAutoLoading();
                            MessageBaseFragment.this.processResultGeneral(data);
                            return null;
                        }

                        public void onPostDataSet(MessagesBundle<M, G> messagesBundle) {
                        }
                    };
                    finalPositionCallback = new ListFinalPositionCallback<MessagesBundle<M, G>>() {
                        public boolean setFinalPosition(MessagesBundle<M, G> messagesBundle, MessagesBundle<M, G> messagesBundle2, Object none) {
                            MessageBaseFragment.this.enableAutoLoading();
                            return true;
                        }
                    };
                    if (loaderData.skipAnimation && loaderData.reason == ChangeReason.UPDATED) {
                        Object object = null;
                        if (onDataSetCallback != null) {
                            object = onDataSetCallback.onPreDataSet(loaderData.bundle);
                        }
                        getAdapter().setData(loaderData.bundle);
                        if (onDataSetCallback != null) {
                            onDataSetCallback.onPostDataSet(loaderData.bundle);
                        }
                        getAdapter().notifyDataSetChanged();
                        if (finalPositionCallback != null) {
                            finalPositionCallback.setFinalPosition(getAdapter().getData(), loaderData.bundle, object);
                            return;
                        }
                        return;
                    }
                    this.list.setData(builder.withOnDataSet(onDataSetCallback).withListFinalPosition(finalPositionCallback).withData(loaderData.bundle).build());
                default:
            }
        }
    }

    protected boolean isLastElementVisible() {
        return this.list.getLastVisiblePosition() >= (this.list.getCount() - this.loadMoreAdapter.getController().getExtraBottomElements()) + -1;
    }

    protected void positionOnFirstUnread(MessagesBundle<M, G> newData) {
        String currentId = OdnoklassnikiApplication.getCurrentUser().uid;
        long lastAccessDate = getLoader().extractInitialAccessDate((Parcelable) newData.generalInfo);
        if (lastAccessDate > 0) {
            int position = 0;
            while (position < newData.messages.size()) {
                M message = ((OfflineMessage) newData.messages.get(position)).message;
                if (message.date <= lastAccessDate || TextUtils.equals(message.authorId, currentId)) {
                    position++;
                } else {
                    int selectionFromTop = 0;
                    if (position > 0) {
                        selectionFromTop = Math.min(measureRowView(convertDataIndexToViewPosition(position - 1)).getMeasuredHeight(), this.list.getHeight() / 4);
                    }
                    this.list.setSelectionFromTop(convertDataIndexToViewPosition(position), selectionFromTop);
                    return;
                }
            }
        }
        selectLastRow();
    }

    private void processMessagesLoadingError(ChangeReason reason, ErrorType errorType) {
        boolean z = true;
        if (getActivity() != null) {
            int errorTextId = getErrorTextId(errorType);
            if (errorType != ErrorType.NO_INTERNET) {
                showTimedToastIfVisible(errorTextId, 1);
            }
            switch (AnonymousClass27.f104x5c76f804[reason.ordinal()]) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    this.wholeEmptyView.setErrorText(errorTextId);
                    this.wholeEmptyView.setWebState(WebState.ERROR);
                    updateSendMessageAllowedState();
                case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                    getLoadMoreController().setTopCurrentState(LoadMoreState.IDLE);
                    getLoadMoreController().setTopMessageForState(LoadMoreState.DISABLED, errorTextId);
                    getLoadMoreController().setTopPermanentState(LoadMoreState.DISABLED);
                case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    getLoadMoreController().setBottomCurrentState(LoadMoreState.IDLE);
                    getLoadMoreController().setBottomPermanentState(LoadMoreState.DISABLED);
                    RefreshProvider refreshProvider = this.refreshProvider;
                    if (getLoader().getBundle() != null && getLoader().getBundle().hasMoreNext) {
                        z = false;
                    }
                    refreshProvider.setRefreshEnabled(z);
                    this.refreshProvider.refreshCompleted();
                case MessagesProto.Message.UUID_FIELD_NUMBER /*5*/:
                    updateSendMessageAllowedState();
                default:
            }
        }
    }

    public void onLoaderReset(Loader<MessagesLoaderBundle<M, G>> loader) {
    }

    public void onPause() {
        super.onPause();
        if (this.createMessageView != null) {
            String enteredText = this.createMessageView.getText().toString();
            L loader = getLoader();
            if (!(loader == null || loader.getBundle() == null || !isCommentingAllowed((Parcelable) loader.getBundle().generalInfo))) {
                this.settings.setMessageDraft(OdnoklassnikiApplication.getCurrentUser(), getSettingsName(), enteredText);
            }
        }
        leaveEditMode();
        AudioPlaybackController.dismissPlayer();
        this.stickersHelper.onPause();
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.createMessageView != null) {
            this.createMessageView.handleDestroyView();
        }
    }

    protected void selectLastRow() {
        if (this.sectionAdapter.getCount() > 0) {
            int lastIndex = this.list.getCount() - 1;
            this.list.setSelectionFromTop(lastIndex, (((this.list.getHeight() - measureRowView(lastIndex).getMeasuredHeight()) - this.list.getListPaddingBottom()) - this.list.getPaddingTop()) - getFooterHeight());
        }
    }

    protected int getFooterHeight() {
        return 0;
    }

    private View measureRowView(int viewIndex) {
        View view = this.list.getAdapter().getView(viewIndex, null, this.list);
        view.measure(MeasureSpec.makeMeasureSpec((this.list.getWidth() - this.list.getPaddingLeft()) - this.list.getPaddingRight(), LinearLayoutManager.INVALID_OFFSET), MeasureSpec.makeMeasureSpec(0, 0));
        return view;
    }

    public boolean handleBack() {
        if (this.createMessageView != null && this.createMessageView.handleBackPress()) {
            return true;
        }
        if (this.replyTo != null && this.replyTo.getVisibility() == 0) {
            this.replyTo.setVisibility(8);
            return true;
        } else if (this.stickersHelper.onBackPressed()) {
            return true;
        } else {
            return false;
        }
    }

    public void onRetryClick(SmartEmptyView emptyView) {
        if (this.messagesAdapter != null && this.messagesAdapter.getCount() == 0) {
            getLoaderManager().restartLoader(0, null, this);
        } else if (this.messagesLoader != null) {
            this.messagesLoader.loadNew(true);
        }
    }

    protected final L getLoader() {
        return this.messagesLoader;
    }

    protected MessagesBaseAdapter<M, G> getAdapter() {
        if (this.messagesAdapter == null) {
            this.messagesAdapter = createMessagesAdapter();
            this.messagesAdapter.setAttachmentSelectionListener(this);
        }
        return this.messagesAdapter;
    }

    public void onContextMenuClosed() {
        super.onContextMenuClosed();
        getAdapter().clearSelection();
    }

    public void onLoadMoreTopClicked() {
        getLoader().loadPrevious();
    }

    public void onLoadMoreBottomClicked() {
        getLoader().loadNext();
    }

    public void onRepliedToClicked(OfflineMessage<M> message) {
        RepliedToInfo info = message.repliedToInfo;
        if (info != null) {
            info.status = info.status == Status.EXPANDED ? Status.COLLAPSED : Status.EXPANDED;
        } else {
            message.repliedToInfo = new RepliedToInfo(null, Status.LOADING);
            getLoader().loadRepliedToComment(message);
        }
        getAdapter().notifyDataSetChanged();
    }

    public void onReplyClicked(OfflineMessage<M> message) {
        if (this.messagesLoader != null && this.messagesLoader.isDataPresents()) {
            String userName;
            if (this.createMessageView.isAdminSelected() && isResetAdminState(message.message)) {
                this.createMessageView.setAdminSelected(false);
            }
            this.replyTo.setVisibility(0);
            if ("GROUP".equals(message.message.authorType)) {
                userName = getAdapter().getGroupName();
            } else {
                UserInfo user = (UserInfo) getAdapter().getUsers().get(message.message.authorId);
                userName = user != null ? user.getAnyName() : getStringLocalized(2131165423);
            }
            this.replyTo.setComment(message.message, userName);
            this.createMessageView.startEditing();
        }
    }

    protected void onInternetAvailable() {
        super.onInternetAvailable();
        if (this.loadMoreAdapter != null) {
            boolean shouldRedrawList = false;
            LoadMoreController loadMoreController = this.loadMoreAdapter.getController();
            if (loadMoreController.getTopPermanentState() == LoadMoreState.DISABLED && loadMoreController.getTopCurrentState() == LoadMoreState.IDLE && getLoader() != null && getLoader().isDataPresents() && getLoader().getBundle().hasMorePrev) {
                loadMoreController.setTopPermanentState(LoadMoreState.LOAD_POSSIBLE);
                shouldRedrawList = true;
            }
            if (loadMoreController.getBottomPermanentState() == LoadMoreState.DISABLED && loadMoreController.getBottomCurrentState() == LoadMoreState.IDLE && getLoader() != null && getLoader().isDataPresents() && getLoader().getBundle().hasMoreNext) {
                loadMoreController.setBottomPermanentState(LoadMoreState.LOAD_POSSIBLE);
                shouldRedrawList = true;
            }
            if (shouldRedrawList) {
                this.list.setData(new UpdateListDataCommandBuilder().doNotChangeData(true).build());
            }
        }
    }

    private void initEmptyView(View view) {
        this.wholeEmptyView = (SmartEmptyView) view.findViewById(C0263R.id.empty_view);
        if (this.wholeEmptyView == null) {
            this.wholeEmptyView = (SmartEmptyView) view.findViewById(2131625096);
        }
        this.wholeEmptyView.setOnRepeatClickListener(this);
    }

    private void initStickersHelper(Bundle savedInstanceState) {
        this.stickersHelper = new StickersHelper(getActivity(), this.createMessageView.getEditText(), this.createMessageView.getSmileCheckBox(), this, this.emojiLayout, false);
        this.stickersHelper.onRestoreInstanceState(savedInstanceState);
    }

    public void onScrollTopClick(int count) {
        if (getActivity() != null && getLoader() != null && getLoader().isDataPresents()) {
            if (VERSION.SDK_INT >= 8) {
                this.list.smoothScrollBy(0, 0);
            }
            UpdateListDataCommandBuilder<MessagesBundle<M, G>> builder = new UpdateListDataCommandBuilder();
            builder.doNotChangeData(true).withInterpolator(new DecelerateInterpolator()).withListInitialPosition(new ListInitialPositionCallback<MessagesBundle<M, G>>() {
                public boolean isWantToChangePosition(MessagesBundle<M, G> messagesBundle, MessagesBundle<M, G> messagesBundle2) {
                    return MessageBaseFragment.this.list.getLastVisiblePosition() < MessageBaseFragment.this.list.getCount() + -4;
                }

                public void setInitialPosition() {
                    MessageBaseFragment.this.list.setSelectionFromTop(MessageBaseFragment.this.list.getCount() - 4, MessageBaseFragment.this.list.getHeight());
                }
            }).withListFinalPosition(new ListFinalPositionCallback<MessagesBundle<M, G>>() {
                public boolean setFinalPosition(MessagesBundle<M, G> messagesBundle, MessagesBundle<M, G> messagesBundle2, Object unused) {
                    MessageBaseFragment.this.selectLastRow();
                    return true;
                }
            }).withDuration(250);
            this.list.setData(builder.build());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreateContextMenu(android.view.ContextMenu r25, android.view.View r26, android.view.ContextMenu.ContextMenuInfo r27) {
        /*
        r24 = this;
        super.onCreateContextMenu(r25, r26, r27);
        r11 = r27;
        r11 = (android.widget.AdapterView.AdapterContextMenuInfo) r11;
        r0 = r24;
        r0 = r0.loadMoreAdapter;
        r21 = r0;
        r21 = r21.getController();
        r0 = r11.position;
        r22 = r0;
        r0 = r24;
        r0 = r0.list;
        r23 = r0;
        r23 = r23.getHeaderViewsCount();
        r22 = r22 - r23;
        r15 = r21.getDataPosition(r22);
        r0 = r24;
        r0 = r0.sectionAdapter;
        r21 = r0;
        r0 = r21;
        r14 = r0.getItem(r15);
        r14 = (ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage) r14;
        if (r14 != 0) goto L_0x0036;
    L_0x0035:
        return;
    L_0x0036:
        r13 = r14.message;
        r0 = r24;
        r21 = r0.isContextMenuCreationIntercepted(r13);
        if (r21 != 0) goto L_0x0035;
    L_0x0040:
        r21 = r24.getAdapter();
        r22 = 0;
        r0 = r21;
        r1 = r22;
        r21 = r0.isReplyDisallowed(r1, r13);
        if (r21 != 0) goto L_0x0181;
    L_0x0050:
        r16 = 1;
    L_0x0052:
        r0 = r13.flags;
        r21 = r0;
        r0 = r21;
        r12 = r0.markAsSpamAllowed;
        r0 = r14.offlineData;
        r21 = r0;
        if (r21 == 0) goto L_0x0185;
    L_0x0060:
        r0 = r14.offlineData;
        r21 = r0;
        r0 = r21;
        r0 = r0.status;
        r20 = r0;
    L_0x006a:
        r0 = r24;
        r5 = r0.isMessageCopyAllowed(r13);
        r0 = r13.flags;
        r21 = r0;
        r0 = r21;
        r0 = r0.deletionAllowed;
        r21 = r0;
        if (r21 == 0) goto L_0x0189;
    L_0x007c:
        if (r20 == 0) goto L_0x008a;
    L_0x007e:
        r21 = ru.ok.android.db.base.OfflineTable.Status.DELETE_ALLOWED;
        r0 = r21;
        r1 = r20;
        r21 = r0.contains(r1);
        if (r21 == 0) goto L_0x0189;
    L_0x008a:
        r7 = 1;
    L_0x008b:
        r0 = r13.flags;
        r21 = r0;
        r0 = r21;
        r4 = r0.blockAllowed;
        r0 = r24;
        r17 = r0.isResendPossible(r14);
        r0 = r24;
        r8 = r0.isEditPossible(r14);
        r21 = ru.ok.android.app.OdnoklassnikiApplication.getCurrentUser();
        r0 = r21;
        r6 = r0.uid;
        r0 = r13.authorType;
        r21 = r0;
        r21 = android.text.TextUtils.isEmpty(r21);
        if (r21 == 0) goto L_0x018c;
    L_0x00b1:
        r0 = r13.authorId;
        r21 = r0;
        r0 = r21;
        r21 = android.text.TextUtils.equals(r0, r6);
        if (r21 != 0) goto L_0x018c;
    L_0x00bd:
        r18 = 1;
    L_0x00bf:
        r21 = "GROUP";
        r0 = r13.authorType;
        r22 = r0;
        r19 = r21.equals(r22);
        r0 = r14.offlineData;
        r21 = r0;
        if (r21 == 0) goto L_0x0190;
    L_0x00d0:
        r0 = r14.offlineData;
        r21 = r0;
        r0 = r21;
        r10 = r0.errorType;
    L_0x00d8:
        if (r10 == 0) goto L_0x0193;
    L_0x00da:
        r9 = 1;
    L_0x00db:
        r21 = r24.getMessageMenuId();
        r0 = r24;
        r1 = r21;
        r2 = r25;
        r0.inflateMenuLocalized(r1, r2);
        r21 = 2131625085; // 0x7f0e047d float:1.8877368E38 double:1.0531627243E-314;
        r0 = r24;
        r1 = r25;
        r2 = r21;
        r3 = r16;
        r0.initMenuItem(r1, r2, r3);
        r21 = 2131625443; // 0x7f0e05e3 float:1.8878094E38 double:1.053162901E-314;
        r0 = r24;
        r1 = r25;
        r2 = r21;
        r3 = r18;
        r0.initMenuItem(r1, r2, r3);
        r21 = 2131625444; // 0x7f0e05e4 float:1.8878096E38 double:1.0531629017E-314;
        r0 = r24;
        r1 = r25;
        r2 = r21;
        r3 = r19;
        r0.initMenuItem(r1, r2, r3);
        r21 = 2131625445; // 0x7f0e05e5 float:1.8878098E38 double:1.053162902E-314;
        r0 = r24;
        r1 = r25;
        r2 = r21;
        r0.initMenuItem(r1, r2, r5);
        r21 = 2131624801; // 0x7f0e0361 float:1.8876792E38 double:1.053162584E-314;
        r0 = r24;
        r1 = r25;
        r2 = r21;
        r0.initMenuItem(r1, r2, r7);
        r21 = 2131625446; // 0x7f0e05e6 float:1.88781E38 double:1.0531629027E-314;
        r0 = r24;
        r1 = r25;
        r2 = r21;
        r0.initMenuItem(r1, r2, r12);
        r21 = 2131625447; // 0x7f0e05e7 float:1.8878102E38 double:1.053162903E-314;
        r0 = r24;
        r1 = r25;
        r2 = r21;
        r0.initMenuItem(r1, r2, r4);
        r21 = 2131625448; // 0x7f0e05e8 float:1.8878104E38 double:1.0531629037E-314;
        r0 = r24;
        r1 = r25;
        r2 = r21;
        r3 = r17;
        r0.initMenuItem(r1, r2, r3);
        r21 = 2131625449; // 0x7f0e05e9 float:1.8878106E38 double:1.053162904E-314;
        r0 = r24;
        r1 = r25;
        r2 = r21;
        r0.initMenuItem(r1, r2, r8);
        r21 = 2131625450; // 0x7f0e05ea float:1.8878108E38 double:1.0531629046E-314;
        r0 = r24;
        r1 = r25;
        r2 = r21;
        r0.initMenuItem(r1, r2, r9);
        r21 = r24.getShowsDialog();
        if (r21 != 0) goto L_0x0035;
    L_0x016e:
        r21 = r24.getAdapter();
        r22 = 1;
        r23 = 0;
        r0 = r21;
        r1 = r22;
        r2 = r23;
        r0.setSelected(r14, r1, r2);
        goto L_0x0035;
    L_0x0181:
        r16 = 0;
        goto L_0x0052;
    L_0x0185:
        r20 = 0;
        goto L_0x006a;
    L_0x0189:
        r7 = 0;
        goto L_0x008b;
    L_0x018c:
        r18 = 0;
        goto L_0x00bf;
    L_0x0190:
        r10 = 0;
        goto L_0x00d8;
    L_0x0193:
        r9 = 0;
        goto L_0x00db;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.fragments.messages.MessageBaseFragment.onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu$ContextMenuInfo):void");
    }

    private void initMenuItem(ContextMenu menu, int menuId, boolean visible) {
        MenuItem item = menu.findItem(menuId);
        item.setVisible(visible);
        item.setOnMenuItemClickListener(this);
    }

    protected boolean isContextMenuCreationIntercepted(M m) {
        return false;
    }

    public void onRefresh() {
        getLoader().loadNew(true);
    }

    protected boolean isResendPossible(OfflineMessage<M> message) {
        if (message.offlineData == null || !OfflineTable.Status.RESEND_POSSIBLE.contains(message.offlineData.status)) {
            return false;
        }
        if (message.message.hasAttachments()) {
            boolean canResend = false;
            for (Attachment attachment : message.message.attachments) {
                if (attachment.typeValue == AttachmentType.AUDIO_RECORDING) {
                    canResend = true;
                    break;
                }
                if (!TextUtils.equals(attachment.getStatus(), "ERROR")) {
                    canResend = true;
                }
            }
            return canResend;
        } else if (message.message.hasServerId()) {
            return false;
        } else {
            return true;
        }
    }

    protected final boolean isEditPossible(OfflineMessage<M> message) {
        if (message.offlineData == null || message.message.hasAttachments() || message.message.flags.editDisabled) {
            return false;
        }
        if (isResendPossible(message)) {
            return true;
        }
        if (!message.message.hasServerId() || this.messagesAdapter == null || !this.messagesAdapter.isMy(message.message.authorId) || message.message.flags.editDisabled) {
            return false;
        }
        return canEditMessageByTime(message);
    }

    private boolean canEditMessageByTime(OfflineMessage<M> message) {
        return SNTP.safeCurrentTimeMillisFromCache() - message.message.date < (95 * ServicesSettingsHelper.getServicesSettings().getMessageEditTimeoutMs()) / 100;
    }

    protected boolean processForMessageItem(int itemId, OfflineMessage<M> message) {
        if (message == null) {
            return false;
        }
        Activity activity = getActivity();
        if (activity == null) {
            return false;
        }
        switch (itemId) {
            case C0263R.id.delete /*2131624801*/:
                getLoader().deleteMessages(new ArrayList(Arrays.asList(new OfflineMessage[]{message})), false);
                return true;
            case 2131625085:
                onReplyClicked(message);
                return true;
            case 2131625443:
                NavigationHelper.showUserInfo(activity, message.message.authorId);
                return true;
            case 2131625444:
                NavigationHelper.showGroupInfo(activity, message.message.authorId);
                return true;
            case 2131625445:
                ((ClipboardManager) activity.getSystemService("clipboard")).setText(SmileTextProcessor.trimSmileSizes(message.message.getActualText()));
                return true;
            case 2131625446:
                getLoader().spamMessages(new ArrayList(Arrays.asList(new OfflineMessage[]{message})));
                return true;
            case 2131625447:
                getLoader().deleteMessages(new ArrayList(Arrays.asList(new OfflineMessage[]{message})), true);
                return true;
            case 2131625448:
                tryResendMessage(message);
                return true;
            case 2131625449:
                if (canEditMessageByTime(message)) {
                    NavigationHelper.showEditMessageActivity(this, message, getMessageEditTitleResourceId(), 123);
                } else {
                    Toast.makeText(activity, getStringLocalized(2131166195), 0).show();
                }
                return true;
            case 2131625450:
                onStatusClicked(message);
                return true;
            default:
                return false;
        }
    }

    protected void tryResendMessage(OfflineMessage<M> message) {
        getLoader().resendMessage(message);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (this.wholeEmptyView.getVisibility() == 0) {
            return false;
        }
        switch (menuItem.getItemId()) {
            case 2131625485:
                this.actionMode = ((BaseCompatToolbarActivity) getActivity()).getSupportToolbar().startActionMode(this.actionModeCallback);
                enterSelectedMessagesState();
                return true;
            default:
                return processForMessageItem(menuItem.getItemId(), getCommentForContextMenu(menuItem));
        }
    }

    private OfflineMessage<M> getCommentForContextMenu(MenuItem item) {
        int index = this.loadMoreAdapter.getController().getDataPosition(((AdapterContextMenuInfo) item.getMenuInfo()).position - this.list.getHeaderViewsCount());
        if (index < 0 || index >= this.sectionAdapter.getCount()) {
            return null;
        }
        return (OfflineMessage) this.sectionAdapter.getItem(index);
    }

    protected LoadMoreAdapter getLoadMoreAdapter() {
        return this.loadMoreAdapter;
    }

    protected LoadMoreController getLoadMoreController() {
        return getLoadMoreAdapter().getController();
    }

    protected void onHideFragment() {
        super.onHideFragment();
        if (getAdapter() != null) {
            hideKeyboard();
            getAdapter().clearSelection();
            leaveEditMode();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 123:
                if (resultCode == -1) {
                    OfflineMessage<M> message = (OfflineMessage) data.getParcelableExtra(org.jivesoftware.smack.packet.Message.ELEMENT);
                    Logger.m173d("New message '%s' received for message: %s, can edit: %s", data.getStringExtra(Stanza.TEXT), message, Boolean.valueOf(canEditMessageByTime(message)));
                    if (canEditMessageByTime(message)) {
                        getLoader().editMessage(message, newText);
                    } else {
                        Toast.makeText(getActivity(), getStringLocalized(2131166195), 0).show();
                    }
                }
            case 124:
                Logger.m172d("REQUEST_PAY_STICKERS");
                this.stickersHelper.onActivityResult(resultCode);
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.stickersHelper.onSaveInstanceState(outState);
    }

    protected void processResultGeneral(MessagesBundle<M, G> data) {
        getAdapter().updateUserInfos(data.users);
        updateSendMessageAllowedState();
        processResultCustom(data);
        Activity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
        EventsManager.getInstance().updateNow();
    }

    private void processBlocked(MessagesBundle<M, G> data) {
        if (isUserBlocked(data)) {
            this.wholeEmptyView.setVisibility(0);
            this.wholeEmptyView.setEmptyText(2131165711);
        }
    }

    private void processFirstPortion(MessagesBundle<M, G> data) {
        LoadMoreState bottomState;
        LoadMoreState topState;
        boolean isEmpty = data.messages.isEmpty();
        Logger.m173d("isEmpty: %s, isBlocked: %s", Boolean.valueOf(isEmpty), Boolean.valueOf(isUserBlocked(data)));
        LoadMoreController loadMoreController = this.loadMoreAdapter.getController();
        if (isEmpty) {
            bottomState = LoadMoreState.DISABLED;
            topState = bottomState;
            this.refreshProvider.setRefreshEnabled(true);
        } else {
            topState = data.hasMorePrev ? LoadMoreState.LOAD_POSSIBLE : LoadMoreState.LOAD_IMPOSSIBLE;
            bottomState = data.hasMoreNext ? LoadMoreState.LOAD_POSSIBLE : LoadMoreState.LOAD_IMPOSSIBLE;
            loadMoreController.setBottomAutoLoad(data.hasMoreNext);
        }
        this.noMessagesView.setWebState(WebState.EMPTY);
        loadMoreController.setTopPermanentState(topState);
        loadMoreController.setBottomPermanentState(bottomState);
        this.refreshProvider.refreshCompleted();
        loadMoreController.setBottomCurrentState(LoadMoreState.IDLE);
        this.wholeEmptyView.setWebState(WebState.EMPTY);
        if (isBlocked) {
            processBlocked(data);
            return;
        }
        this.wholeEmptyView.setVisibility(8);
        updateEmptyViewVisibility(isEmpty);
        loadMoreController.setBottomAutoLoad(data.hasMoreNext);
        processResultGeneral(data);
    }

    protected void updateEmptyViewVisibility(boolean isEmpty) {
        int i;
        int i2 = 8;
        SmartEmptyView smartEmptyView = this.noMessagesView;
        if (isEmpty) {
            i = 0;
        } else {
            i = 8;
        }
        smartEmptyView.setVisibility(i);
        View view = this.networkStatusView;
        if (!isEmpty) {
            i2 = 0;
        }
        view.setVisibility(i2);
    }

    private void processPreviousPortion(MessagesBundle<M, G> data) {
        LoadMoreController controller = this.loadMoreAdapter.getController();
        controller.setTopCurrentState(LoadMoreState.IDLE);
        controller.setTopPermanentState(data.hasMorePrev ? LoadMoreState.LOAD_POSSIBLE : LoadMoreState.LOAD_IMPOSSIBLE);
        processResultGeneral(data);
    }

    private void processNextPortion(MessagesBundle<M, G> data) {
        LoadMoreController controller = this.loadMoreAdapter.getController();
        controller.setBottomCurrentState(LoadMoreState.IDLE);
        controller.setBottomPermanentState(data.hasMoreNext ? LoadMoreState.LOAD_POSSIBLE : LoadMoreState.LOAD_IMPOSSIBLE);
        controller.setBottomAutoLoad(data.hasMoreNext);
        this.refreshProvider.setRefreshEnabled(!data.hasMoreNext);
        this.refreshProvider.refreshCompleted();
        processResultGeneral(data);
    }

    protected void processMessageAdded(MessagesBundle<M, G> data) {
        Logger.m173d("isEmpty: %s", Boolean.valueOf(data.messages.isEmpty()));
        updateEmptyViewVisibility(isEmpty);
        this.wholeEmptyView.setVisibility(8);
    }

    protected boolean isSendAudioAttachEnabled(G g) {
        return false;
    }

    protected boolean isSendVideoAttachEnabled(G g) {
        return false;
    }

    private void disableAutoLoading() {
        getLoadMoreController().setAutoLoadSuppressed(true);
    }

    private void enableAutoLoading() {
        getLoadMoreController().setAutoLoadSuppressed(false);
    }

    protected int convertDataIndexToViewPosition(int dataIndex) {
        return ((this.sectionAdapter.getSectionsCountPriorDataPosition(dataIndex) + dataIndex) + this.list.getHeaderViewsCount()) + getLoadMoreController().getExtraTopElements();
    }

    protected int convertViewPositionToRawDataIndex(int viewPosition) {
        return this.sectionAdapter.getDataIndexForPosition(viewPosition - (this.list.getHeaderViewsCount() + getLoadMoreController().getExtraTopElements()));
    }

    public RowPosition getRowPositionType(int position, int extendedPosition) {
        String prevAuthorId = getAuthorId(position - 1);
        String authorId = getAuthorId(position);
        String nextAuthorId = getAuthorId(position + 1);
        long prevDate = getDate(position - 1);
        long date = getDate(position);
        long nextDate = getDate(position + 1);
        String prevSectionName = this.sectionAdapter.sectionTitleForDataPosition(extendedPosition - 1);
        String sectionName = this.sectionAdapter.sectionTitleForDataPosition(extendedPosition);
        String nextSectionName = this.sectionAdapter.sectionTitleForDataPosition(extendedPosition + 1);
        return RowPositionUtils.determineRowPosition(authorId, sectionName, date, getCustomTagForPosition(position), prevAuthorId, prevSectionName, prevDate, getCustomTagForPosition(position - 1), nextAuthorId, nextSectionName, nextDate, getCustomTagForPosition(position + 1));
    }

    private String getCustomTagForPosition(int position) {
        List<OfflineMessage<M>> messages = getAdapter().getMessages();
        if (messages == null || position < 0 || position >= messages.size()) {
            return null;
        }
        return getCustomTagForPositionInternal(((OfflineMessage) messages.get(position)).message);
    }

    private String getAuthorId(int position) {
        List<OfflineMessage<M>> messages = getAdapter().getMessages();
        if (messages == null || position < 0 || position >= messages.size()) {
            return null;
        }
        return ((OfflineMessage) messages.get(position)).message.authorId;
    }

    private long getDate(int position) {
        List<OfflineMessage<M>> comments = getAdapter().getMessages();
        if (comments == null || position < 0 || position >= comments.size()) {
            return 0;
        }
        return ((OfflineMessage) comments.get(position)).message.date;
    }

    public void onShowNewSet(boolean hasNew) {
        this.createMessageView.setCircleVisible(hasNew);
    }

    public void onSendText(String text, @Nullable MessageBase replyToMessage) {
        RepliedTo repliedTo;
        if (replyToMessage == null && this.replyTo != null) {
            replyToMessage = this.replyTo.getComment();
        }
        if (replyToMessage == null) {
            repliedTo = new RepliedTo(null, null, null);
        } else {
            repliedTo = new RepliedTo(replyToMessage.id, replyToMessage.authorId, replyToMessage.authorType);
        }
        if (VERSION.SDK_INT <= 17) {
            this.replyTo.postDelayed(new Runnable() {
                public void run() {
                    MessageBaseFragment.this.replyTo.setVisibility(8);
                }
            }, 1000);
        } else {
            this.replyTo.setVisibility(8);
        }
        this.messagesLoader.addMessage(text, repliedTo, getCurrentAuthor());
    }

    public void onSendMessageClick(View view) {
        MessageBase messageBase = null;
        if (this.messagesLoader != null) {
            String text = this.createMessageView.getText().toString();
            this.createMessageView.setText(null);
            if (this.replyTo != null) {
                messageBase = this.replyTo.getComment();
            }
            onSendText(text, messageBase);
        }
    }

    public void startActivityForResult(Intent intent) {
        startActivityForResult(intent, 124);
    }

    @Subscribe(on = 2131623946, to = 2131624245)
    public void onStickersUpdated(BusEvent event) {
        this.stickersHelper.onStickersUpdated();
    }

    public void onAudioAttachRequested(String fileName, byte[] audioWave) {
    }

    public void onAudioAttachRecording(boolean recording) {
    }

    public void onPhotoSelectClick(View view) {
    }

    public void onCameraClick(View view) {
    }

    public void onAttachVideoClick(boolean recordNewVideo) {
    }

    public void onAttachmentSelected(View view, List<Attachment> attachments, Attachment selected) {
        if (getActivity() != null && selected.typeValue != null) {
            if (selected.typeValue == AttachmentType.PHOTO) {
                processPhotoAttachClick(view, attachments, selected);
                AttachmentUtils.sendShowAttachStatEvents(AttachmentType.PHOTO);
            } else if (selected.typeValue.isVideo()) {
                processVideoAttachClick(selected);
                AttachmentUtils.sendShowAttachStatEvents(AttachmentType.VIDEO);
            } else if (selected.typeValue == AttachmentType.TOPIC) {
                BannerLinksUtils.navigateExternalUrl(getActivity(), selected.linkUrl);
            }
        }
    }

    private void processPhotoAttachClick(View view, List<Attachment> attachments, Attachment selected) {
        Bundle animationBundle = null;
        if (selected.getStatus() == null) {
            selected.setStatus("WAITING");
        }
        String status = selected.getStatus();
        Object obj = -1;
        switch (status.hashCode()) {
            case -1107307769:
                if (status.equals("RECOVERABLE_ERROR")) {
                    obj = null;
                    break;
                }
                break;
            case 66247144:
                if (status.equals("ERROR")) {
                    obj = 1;
                    break;
                }
                break;
        }
        switch (obj) {
            case RECEIVED_VALUE:
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                new Builder(getActivity()).setTitle(getStringLocalized(2131165791)).setMessage(getStringLocalized(getErrorRes(selected.uploadErrorCode))).setPositiveButton(getStringLocalized(2131165284), null).show();
            default:
                ArrayList<Attachment> list = new ArrayList(attachments.size());
                for (Attachment attach : attachments) {
                    if (!"ERROR".equals(attach.getStatus())) {
                        list.add(attach);
                    }
                }
                if (!GifAsMp4PlayerHelper.shouldShowGifAsMp4(selected)) {
                    animationBundle = PhotoLayerAnimationHelper.makeScaleUpAnimationBundle(view.findViewById(C0263R.id.image), selected.standard_width, selected.standard_height, selected.getRotation());
                }
                NavigationHelper.showAttachImage(getActivity(), animationBundle, list, selected, getShowAttachSourceId());
        }
    }

    private void processVideoAttachClick(Attachment attach) {
        long videoId = attach.mediaId;
        NavigationHelper.showVideo(getActivity(), videoId != 0 ? String.valueOf(videoId) : null, attach.path);
    }

    private static int getErrorRes(int errorCode) {
        switch (errorCode) {
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                return 2131165812;
            case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
            case C0206R.styleable.Toolbar_titleMarginTop /*15*/:
            case C0206R.styleable.Toolbar_titleMarginBottom /*16*/:
                return 2131165814;
            case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return 2131165815;
            case C0206R.styleable.Toolbar_titleMarginEnd /*14*/:
                return 2131165816;
            default:
                return 2131165841;
        }
    }

    @Subscribe(on = 2131623946, to = 2131624127)
    public void onAttachmentProcessingCheck(BusEvent event) {
        if (this.messagesAdapter.isSendingAttachment(event.bundleOutput.getString("ATTACHMENT_LOCAL_ID")) && this.messagesLoader != null) {
            this.messagesLoader.loadNew(true);
        }
    }

    public void onAdminStateChanged(boolean isAdmin) {
        getAdapter().setIsAdmin(isAdmin);
    }

    public void onReplyToCloseClicked() {
        this.replyTo.setVisibility(8);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (!this.loadMoreAdapter.processItemClick(position - this.list.getHeaderViewsCount())) {
            OfflineMessage<M> item = (OfflineMessage) this.loadMoreAdapter.getItem(position - this.list.getHeaderViewsCount());
            if (item == null) {
                Logger.m185w("Null item clicked: %d", Integer.valueOf(position));
            } else if (this.actionMode != null) {
                getAdapter().setSelected(item, this.list.isItemChecked(position), false);
                this.actionMode.invalidate();
            } else if (!onInterceptMessageClick(item)) {
                view.showContextMenu();
            }
        }
    }

    protected boolean onInterceptMessageClick(OfflineMessage<M> offlineMessage) {
        return false;
    }

    public void onStatusClicked(OfflineMessage<M> message) {
        ErrorType errorType = message.offlineData.errorType;
        if (errorType == null && message.offlineData.status == OfflineTable.Status.FAILED) {
            errorType = ErrorType.GENERAL;
        }
        if (errorType != null) {
            boolean showUndoEdit;
            boolean showPayStickers;
            int textId = errorType == ErrorType.GENERAL ? getGeneralErrorTextId() : getErrorTextId(errorType);
            boolean showResend = isResendPossible(message);
            if (TextUtils.isEmpty(message.message.textEdited)) {
                showUndoEdit = false;
            } else {
                showUndoEdit = true;
            }
            if (errorType == ErrorType.STICKER_SERVICE_UNAVAILABLE) {
                showPayStickers = true;
            } else {
                showPayStickers = false;
            }
            SendErrorDialog dialog = SendErrorDialog.newInstance(LocalizationManager.getString(getActivity(), 2131165791), LocalizationManager.getString(getActivity(), textId), showResend, showUndoEdit, showPayStickers);
            dialog.show(getFragmentManager(), "error-type");
            dialog.setResendListener(new AnonymousClass26(message));
        }
    }

    private void startPayStickersActivity(Context context) {
        StatisticManager.getInstance().addStatisticEvent("smile-stickers-payment-started", new Pair[0]);
        startActivityForResult(new Intent(context, PayStickersActivity.class), 124);
    }

    public void onAuthorClicked(String uid, String type) {
        Activity activity = getActivity();
        if (TextUtils.isEmpty(type)) {
            NavigationHelper.showUserInfo(activity, uid);
        } else if ("GROUP".equals(type)) {
            NavigationHelper.showGroupInfo(activity, uid);
        }
    }

    public void onLikeClicked(M message) {
        getLoader().likeMessage(message);
    }

    public void onLinkClicked(String url) {
        if (getActivity() != null) {
            getWebLinksProcessor().processUrl(url);
        }
    }

    public void onMessageChecked(int position, boolean isChecked) {
        this.list.setItemChecked(convertDataIndexToViewPosition(position), isChecked);
        this.loadMoreAdapter.notifyDataSetChanged();
        if (this.actionMode != null) {
            this.actionMode.invalidate();
        }
    }

    public void onStickersClicked(M message) {
        this.stickersHelper.onMessageStickersClicked(message);
    }

    public void onEditedClicked(OfflineMessage<M> message) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, getStringLocalized(2131165731, DateFormatter.formatDeltaTimePast(context, message.message.dateEdited, true, false)), 0).show();
            StatisticManager.getInstance().addStatisticEvent("message-edited-date-shown", new Pair[0]);
        }
    }

    public boolean onMenuItemClick(MenuItem item) {
        return onOptionsItemSelected(item);
    }

    @TargetApi(11)
    private void enterSelectedMessagesState() {
        if (!getAdapter().isInActionMode()) {
            this.list.setChoiceMode(2);
            getAdapter().showSelectedUse();
            this.sectionAdapter.notifyDataSetChanged();
            this.createMessageView.setVisibility(8);
        }
    }

    @TargetApi(11)
    private void leaveEditMode() {
        if (this.list != null) {
            this.list.clearChoices();
            this.list.setChoiceMode(0);
            getAdapter().cancelSelectedUse();
            getAdapter().clearSelection();
        }
        if (this.createMessageView != null) {
            this.createMessageView.setVisibility(0);
        }
        if (this.actionMode != null) {
            this.actionMode.finish();
        }
    }

    protected int getLayoutId() {
        return 2130903327;
    }

    protected int getMessageMaxLength() {
        return 0;
    }

    protected int getErrorTextId(ErrorType errorType) {
        return errorType.getDefaultErrorMessage();
    }

    protected boolean isMessageCopyAllowed(M message) {
        return !message.hasAttachments();
    }

    protected LoadMoreAdapter createLoadMoreAdapter(BaseAdapter baseAdapter, ListView listView) {
        return new LoadMoreAdapter(getActivity(), this.sectionAdapter, this, LoadMoreMode.BOTH, null);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.stickersHelper.onConfigurationChanged();
    }
}
