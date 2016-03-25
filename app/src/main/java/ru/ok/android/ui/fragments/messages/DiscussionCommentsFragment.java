package ru.ok.android.ui.fragments.messages;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import java.util.List;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.C0206R;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.helper.ServiceHelper.CommandListener;
import ru.ok.android.app.helper.ServiceHelper.ResultCode;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.base.OfflineTable.Status;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.web.shortlinks.ShortLink;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.app.IntentUtils;
import ru.ok.android.services.app.NotifyReceiver;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.discussions.DiscussionSubscribeProcessor;
import ru.ok.android.services.processors.discussions.DiscussionUnSubscribeProcessor;
import ru.ok.android.ui.adapters.section.Sectionizer;
import ru.ok.android.ui.custom.OnSizeChangedListener;
import ru.ok.android.ui.custom.animationlist.AnimateChangesListView.BoundsAnimationListener;
import ru.ok.android.ui.custom.animationlist.AnimateChangesListView.RectTypeEvaluator;
import ru.ok.android.ui.custom.animationlist.RowInfo;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.ListCellCreateAnimationCreator;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.ListCellRemoveAnimationCreator;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.ListFinalPositionCallback;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.UpdateListDataCommandBuilder;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView.WebState;
import ru.ok.android.ui.custom.loadmore.DefaultLoadMoreViewProvider;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapter;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapterListener;
import ru.ok.android.ui.custom.loadmore.LoadMoreMode;
import ru.ok.android.ui.custom.loadmore.LoadMoreView;
import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;
import ru.ok.android.ui.custom.loadmore.LoadMoreViewProvider;
import ru.ok.android.ui.custom.scroll.ScrollTopView.NewEventsMode;
import ru.ok.android.ui.dialogs.highlight.HighlightDialogFragment;
import ru.ok.android.ui.discussions.adapters.DiscussionsAdapter;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.ui.fragments.messages.adapter.MessagesBaseAdapter;
import ru.ok.android.ui.fragments.messages.adapter.MessagesDiscussionAdapter;
import ru.ok.android.ui.fragments.messages.adapter.MessagesDiscussionAdapter.CommentActionsBuilder;
import ru.ok.android.ui.fragments.messages.loaders.MessagesDiscussionLoader;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesBundle;
import ru.ok.android.ui.fragments.messages.loaders.data.MessagesLoaderBundle;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.android.ui.fragments.messages.loaders.data.RepliedToInfo;
import ru.ok.android.ui.fragments.messages.view.DiscussionInfoView;
import ru.ok.android.ui.fragments.messages.view.DiscussionInfoView.DiscussionInfoDialogClickListener;
import ru.ok.android.ui.fragments.messages.view.DiscussionInfoView.DiscussionInfoViewListener;
import ru.ok.android.ui.fragments.messages.view.state.DiscussionMediaTopicState;
import ru.ok.android.ui.fragments.messages.view.state.DiscussionPhotoState;
import ru.ok.android.ui.fragments.messages.view.state.DiscussionState;
import ru.ok.android.ui.image.view.PhotoLayerAnimationHelper;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.ui.quickactions.QuickActionList;
import ru.ok.android.ui.quickactions.QuickActionList.OnActionItemClickListener;
import ru.ok.android.ui.utils.RowPosition;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.android.utils.animation.AnimationBundleHandler;
import ru.ok.android.utils.animation.AnimationHelper;
import ru.ok.android.utils.animation.SyncBus.MessageCallback;
import ru.ok.android.utils.bus.BusProtocol;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.paging.PagingAnchor;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Permissions;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;
import ru.ok.java.api.response.video.VideoGetResponse;
import ru.ok.model.Discussion;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase;
import ru.ok.model.messages.MessageComment;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.stream.LikeInfoContext;

public final class DiscussionCommentsFragment extends MessageBaseFragment<MessageComment, DiscussionInfoResponse, MessagesDiscussionLoader> implements CommandListener, CommentActionsBuilder, DiscussionInfoDialogClickListener, DiscussionInfoViewListener, MessageCallback {
    private MenuItem _copyShortLink;
    private MenuItem _goToEnd;
    private MenuItem _goToTop;
    private BroadcastReceiver _notificationsReceiver;
    private MenuItem _subscribeItem;
    private MenuItem _unSubscribeItem;
    private AnimationBundleHandler animationBundleHandler;
    private DiscussionInfoResponse fullDiscussionInfo;
    private PagingAnchor initialAnchor;
    private boolean isScrollingToTop;
    private View loadMoreButton;
    private LoadMoreView loadTopView;
    private boolean skipFirstScroll;
    private Animation slideInAnimation;
    private Animation slideOutAnimation;
    private ImageView stickyHeaderView;
    private LinearLayout stickyItemLayout;
    private ViewGroup stickyItemView;
    private DiscussionInfoView topicView;

    /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.10 */
    class AnonymousClass10 implements ListFinalPositionCallback<MessagesBundle<MessageComment, DiscussionInfoResponse>> {
        final /* synthetic */ int val$position;

        AnonymousClass10(int i) {
            this.val$position = i;
        }

        public boolean setFinalPosition(MessagesBundle<MessageComment, DiscussionInfoResponse> messagesBundle, MessagesBundle<MessageComment, DiscussionInfoResponse> messagesBundle2, Object userData) {
            int offset = (DiscussionCommentsFragment.this.stickyItemView == null || DiscussionCommentsFragment.this.stickyItemView.getVisibility() != 0) ? 0 : DiscussionCommentsFragment.this.stickyItemView.getMeasuredHeight();
            DiscussionCommentsFragment.this.list.setSelectionFromTop(DiscussionCommentsFragment.this.convertDataIndexToViewPosition(this.val$position), offset);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.15 */
    class AnonymousClass15 extends LoadMoreAdapter {
        AnonymousClass15(Context x0, BaseAdapter x1, LoadMoreAdapterListener x2, LoadMoreMode x3, LoadMoreViewProvider x4) {
            super(x0, x1, x2, x3, x4);
        }

        public boolean isEnabled(int position) {
            if (!super.isEnabled(position)) {
                return false;
            }
            if (getController().isTopView(position) && (getController().getTopPermanentState() != LoadMoreState.LOAD_POSSIBLE || getController().topAutoLoad)) {
                return false;
            }
            if (!getController().isBottomView(position, getCount()) || (getController().getBottomCurrentState() == LoadMoreState.LOAD_POSSIBLE && !getController().bottomAutoLoad)) {
                return true;
            }
            return false;
        }

        public void onTopAutoLoadChanged() {
            super.onTopAutoLoadChanged();
        }

        public void onBottomAutoLoadChanged() {
            super.onBottomAutoLoadChanged();
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.17 */
    static /* synthetic */ class AnonymousClass17 {
        static final /* synthetic */ int[] f103x22ae40df;
        static final /* synthetic */ int[] $SwitchMap$ru$ok$java$api$request$paging$PagingAnchor;

        static {
            f103x22ae40df = new int[ErrorType.values().length];
            try {
                f103x22ae40df[ErrorType.USER_DO_NOT_RECEIVE_MESSAGES.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f103x22ae40df[ErrorType.RESTRICTED_ACCESS_SECTION_FOR_FRIENDS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f103x22ae40df[ErrorType.RESTRICTED_ACCESS_ACTION_BLOCKED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f103x22ae40df[ErrorType.RESTRICTED_ACCESS_FOR_NON_MEMBERS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            $SwitchMap$ru$ok$java$api$request$paging$PagingAnchor = new int[PagingAnchor.values().length];
            try {
                $SwitchMap$ru$ok$java$api$request$paging$PagingAnchor[PagingAnchor.LAST.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$java$api$request$paging$PagingAnchor[PagingAnchor.UNREAD.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$java$api$request$paging$PagingAnchor[PagingAnchor.FIRST.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.1 */
    class C08301 implements AnimationListener {
        C08301() {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            DiscussionCommentsFragment.this.stickyItemLayout.setVisibility(4);
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.2 */
    class C08312 implements OnScrollListener {
        C08312() {
        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (DiscussionCommentsFragment.this.isScrollingToTop && scrollState == 0) {
                DiscussionCommentsFragment.this.isScrollingToTop = false;
                DiscussionCommentsFragment.this.list.setSelection(0);
            }
        }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (DiscussionCommentsFragment.this.stickyItemView != null && DiscussionCommentsFragment.this.needStickyHeader()) {
                if (firstVisibleItem != 0) {
                    DiscussionCommentsFragment.this.showStickyItem();
                } else if (DiscussionCommentsFragment.this.topicView.getHeight() - Math.abs(DiscussionCommentsFragment.this.topicView.getTop()) < DiscussionCommentsFragment.this.stickyItemView.getMeasuredHeight()) {
                    if (DiscussionCommentsFragment.this.stickyHeaderView == null) {
                        DiscussionCommentsFragment.this.createStickyHeader();
                    }
                    DiscussionCommentsFragment.this.showStickyHeader();
                    DiscussionCommentsFragment.this.showStickyItem();
                } else {
                    DiscussionCommentsFragment.this.hideStickyItem();
                    DiscussionCommentsFragment.this.hideStickyHeader();
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.3 */
    class C08323 implements OnLayoutChangeListener {
        C08323() {
        }

        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (DiscussionCommentsFragment.this.stickyHeaderView != null && DiscussionCommentsFragment.this.stickyItemView != null) {
                DiscussionCommentsFragment.this.updateStickyHeader();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.4 */
    class C08344 implements OnClickListener {

        /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.4.1 */
        class C08331 implements Runnable {
            C08331() {
            }

            public void run() {
                DiscussionCommentsFragment.this.list.smoothScrollToPositionFromTop(0, 0, 250);
                DiscussionCommentsFragment.this.isScrollingToTop = true;
            }
        }

        C08344() {
        }

        public void onClick(View v) {
            DiscussionCommentsFragment.this.list.post(new C08331());
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.5 */
    class C08355 implements OnActionItemClickListener {
        final /* synthetic */ OfflineMessage val$offlineMessage;

        C08355(OfflineMessage offlineMessage) {
            this.val$offlineMessage = offlineMessage;
        }

        public void onItemClick(QuickActionList source, int pos, int actionId) {
            DiscussionCommentsFragment.this.processForMessageItem(actionId, this.val$offlineMessage);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.6 */
    class C08366 implements ListCellRemoveAnimationCreator {
        C08366() {
        }

        public void createAnimations(Drawable drawable, RowInfo rowInfo, List<Animator> animators, BoundsAnimationListener boundsAnimationListener) {
            Rect startBounds = new Rect(rowInfo.left, rowInfo.top, rowInfo.right, rowInfo.bottom);
            drawable.setBounds(startBounds);
            new Rect(startBounds).offset(0, DiscussionCommentsFragment.this.initialAnchor == PagingAnchor.FIRST ? DiscussionCommentsFragment.this.list.getHeight() * 2 : (-DiscussionCommentsFragment.this.list.getHeight()) * 2);
            ObjectAnimator animation = ObjectAnimator.ofObject(drawable, "bounds", new RectTypeEvaluator(), new Object[]{startBounds, endBounds});
            animation.setDuration(500);
            animation.addUpdateListener(boundsAnimationListener);
            animators.add(animation);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.7 */
    class C08377 implements ListCellCreateAnimationCreator {
        C08377() {
        }

        public void createAnimations(View row, List<Animator> animators) {
            String str = "translationY";
            float[] fArr = new float[2];
            fArr[0] = DiscussionCommentsFragment.this.initialAnchor == PagingAnchor.FIRST ? (float) (-DiscussionCommentsFragment.this.list.getHeight()) : (float) DiscussionCommentsFragment.this.list.getHeight();
            fArr[1] = 0.0f;
            Animator animation = ObjectAnimator.ofFloat(row, str, fArr);
            animation.setDuration(500);
            animators.add(animation);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.8 */
    class C08388 implements ListCellCreateAnimationCreator {
        C08388() {
        }

        public void createAnimations(View row, List<Animator> list) {
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.9 */
    class C08399 implements ListCellRemoveAnimationCreator {
        C08399() {
        }

        public void createAnimations(Drawable drawable, RowInfo previousPosition, List<Animator> list, BoundsAnimationListener boundsAnimationListener) {
        }
    }

    private class DiscussionCommentsReceiver extends BroadcastReceiver {
        private DiscussionCommentsReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (!TextUtils.equals("ru.ok.android.action.NOTIFY", intent.getAction())) {
                return;
            }
            if (NotifyReceiver.isNotificationForDiscussionServerError(intent, DiscussionCommentsFragment.this.getDiscussion())) {
                String message = intent.getStringExtra(Message.ELEMENT);
                if (TextUtils.isEmpty(message)) {
                    message = DiscussionCommentsFragment.this.getStringLocalized(2131165708);
                }
                Toast.makeText(DiscussionCommentsFragment.this.getActivity(), message, 1).show();
                abortBroadcast();
            } else if (NotifyReceiver.isNotificationForDiscussionComment(intent, DiscussionCommentsFragment.this.getDiscussion())) {
                ((MessagesDiscussionLoader) DiscussionCommentsFragment.this.getLoader()).loadNew(DiscussionCommentsFragment.this.isFragmentVisible());
                if (DiscussionCommentsFragment.this.isResumed() && DiscussionCommentsFragment.this.isVisible()) {
                    abortBroadcast();
                }
            }
        }
    }

    public DiscussionCommentsFragment() {
        this.initialAnchor = PagingAnchor.UNREAD;
        this.skipFirstScroll = false;
        this.isScrollingToTop = false;
    }

    @Subscribe(on = 2131623946, to = 2131624243)
    public void onError(BusEvent event) {
        if (event.bundleOutput.getInt(BusProtocol.PREF_PLAY_INFO_ERROR_KEY, 404) == C0206R.styleable.Theme_radioButtonStyle) {
            showToastIfVisible(2131165650, 0);
        }
    }

    public static Bundle newArguments(Discussion discussion, Page page, String interseptedUrl) {
        Bundle args = new Bundle();
        args.putParcelable("DISCUSSION", discussion);
        args.putString("URL", interseptedUrl);
        args.putString("PAGE", page.name());
        args.putBoolean("fragment_is_dialog", true);
        return args;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments().getString("PAGE", Page.MESSAGES.toString()).equals(Page.INFO.toString())) {
            this.skipFirstScroll = true;
        }
        this.slideInAnimation = AnimationUtils.loadAnimation(getContext(), 2130968638);
        this.slideInAnimation.setStartOffset(100);
        this.slideOutAnimation = AnimationUtils.loadAnimation(getContext(), 2130968642);
        this.slideOutAnimation.setAnimationListener(new C08301());
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        initList(view);
        return view;
    }

    private Discussion getDiscussion() {
        return (Discussion) getArguments().getParcelable("DISCUSSION");
    }

    protected void initList(ViewGroup view) {
        super.initList(view);
        if (this.fullDiscussionInfo != null) {
            processDiscussionInfo(this.fullDiscussionInfo);
        }
        this.list.setBackgroundResource(2131492983);
        this.list.setOnScrollListener(new C08312());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 777 && resultCode == -1) {
            onRefresh();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void hideStickyItem() {
        if (this.stickyItemLayout.getVisibility() != 4) {
            this.stickyItemLayout.clearAnimation();
            this.stickyItemLayout.startAnimation(this.slideOutAnimation);
        }
    }

    private void hideStickyHeader() {
        if (this.stickyHeaderView != null && this.stickyHeaderView.getVisibility() != 4) {
            this.stickyHeaderView.setVisibility(4);
        }
    }

    private void showStickyItem() {
        if (this.stickyItemLayout.getVisibility() != 0) {
            this.stickyItemLayout.setVisibility(0);
            this.stickyItemLayout.clearAnimation();
            this.stickyItemLayout.startAnimation(this.slideInAnimation);
        }
    }

    private void showStickyHeader() {
        if (this.stickyHeaderView.getVisibility() != 0) {
            this.stickyHeaderView.setVisibility(0);
        }
    }

    private void createStickyHeader() {
        if (this.topicView != null && this.topicView.getWidth() > 0) {
            this.stickyHeaderView = new ImageView(getActivity());
            this.stickyHeaderView.setLayoutParams(new LayoutParams(-1, -2));
            ViewGroup parent = (ViewGroup) getView().findViewById(2131625090);
            parent.addView(this.stickyHeaderView, parent.getChildCount() - 1);
            updateStickyHeader();
        }
    }

    private void updateStickyHeader() {
        int width = this.topicView.getWidth();
        int height = this.stickyItemView.getHeight();
        if (width <= 0 || height <= 0) {
            this.stickyHeaderView.setImageBitmap(null);
            return;
        }
        Bitmap headerBottomBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(headerBottomBitmap);
        canvas.drawColor(-1);
        canvas.translate(0.0f, (float) (height - this.topicView.getHeight()));
        this.topicView.draw(canvas);
        this.stickyHeaderView.setImageBitmap(headerBottomBitmap);
    }

    protected void onPresetAdapter() {
        super.onPresetAdapter();
        this.topicView = new DiscussionInfoView(getActivity(), null);
        this.topicView.setListener(this);
        this.topicView.setDialogClickListener(this);
        this.topicView.addOnLayoutChangeListener(new C08323());
        this.list.addHeaderView(this.topicView);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.fullDiscussionInfo != null) {
            getLoadMoreController().setTopPermanentState(LoadMoreState.LOADING);
        }
    }

    protected void updateEmptyViewVisibility(boolean isEmpty) {
        if (isEmpty) {
            getLoadMoreController().setTopMessageForState(LoadMoreState.DISABLED, 2131165709);
            this.loadTopView.findViewById(2131624783).setBackgroundResource(0);
        } else {
            getLoadMoreController().setTopMessageForState(LoadMoreState.DISABLED, 0);
            this.loadTopView.findViewById(2131624783).setBackgroundResource(2130837838);
        }
        getLoadMoreController().setTopPermanentState(getLoadMoreController().getTopPermanentState());
    }

    protected void processMessageAdded(MessagesBundle<MessageComment, DiscussionInfoResponse> data) {
        if (!data.hasMorePrev) {
            getLoadMoreController().setTopPermanentState(LoadMoreState.LOAD_IMPOSSIBLE);
        }
        updateTopLoadViewVisibility();
        super.processMessageAdded(data);
    }

    protected CharSequence getTitle() {
        if (this.fullDiscussionInfo != null) {
            CharSequence title = Utils.removeTextBetweenBraces(this.fullDiscussionInfo.generalInfo.message);
            if (!TextUtils.isEmpty(title)) {
                return title;
            }
        }
        return "";
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        PhotoLayerAnimationHelper.registerCallback(1, this);
        PhotoLayerAnimationHelper.registerCallback(2, this);
    }

    public void onDestroy() {
        super.onDestroy();
        PhotoLayerAnimationHelper.unregisterCallback(1, this);
        PhotoLayerAnimationHelper.unregisterCallback(2, this);
    }

    protected MessagesBaseAdapter createMessagesAdapter() {
        MessagesDiscussionAdapter messagesDiscussionAdapter = new MessagesDiscussionAdapter(getActivity(), OdnoklassnikiApplication.getCurrentUser().uid, this);
        messagesDiscussionAdapter.setCommentActionsBuilder(this);
        return messagesDiscussionAdapter;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (inflateMenuLocalized(2131689486, menu)) {
            this._subscribeItem = menu.findItem(2131625457);
            this._unSubscribeItem = menu.findItem(2131624772);
            this._goToTop = menu.findItem(2131624784);
            this._goToEnd = menu.findItem(2131625456);
            this._copyShortLink = menu.findItem(2131625454);
            this._subscribeItem.setOnMenuItemClickListener(this);
            this._unSubscribeItem.setOnMenuItemClickListener(this);
            updateMenuItemsVisibility();
        }
    }

    public void onDialogItemClick(long id) {
        if (getActivity() != null) {
            if (id == 2131625458) {
                NavigationHelper.showGroupInfo(getActivity(), getLoadedDiscussionInfo().group.id);
            } else if (id == 2131625460) {
                NavigationHelper.showUserInfo(getActivity(), getLoadedDiscussionInfo().user.id);
            } else if (id == 2131625461) {
                NavigationHelper.showPhotoAlbum(getActivity(), ((DiscussionInfoResponse) getLoadedBundle().generalInfo).albumInfo);
            } else if (id == 2131625459) {
                getWebLinksProcessor().processUrl(WebUrlCreator.getHappening(((DiscussionInfoResponse) getLoadedBundle().generalInfo).happeningInfo.id));
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131624772:
                getServiceHelper().unSubscribeFromDiscussion(getDiscussion());
                return true;
            case 2131624784:
                this.initialAnchor = PagingAnchor.FIRST;
                getLoadMoreController().setBottomAutoLoad(false);
                break;
            case 2131625454:
                if (!(getLoader() == null || ((MessagesDiscussionLoader) getLoader()).getBundle() == null)) {
                    ShortLink.createDiscussionLink((DiscussionInfoResponse) ((MessagesDiscussionLoader) getLoader()).getBundle().generalInfo).copy(getContext(), true);
                }
                return true;
            case 2131625456:
                if (getLoadMoreController().getBottomPermanentState() != LoadMoreState.LOAD_IMPOSSIBLE) {
                    this.initialAnchor = PagingAnchor.LAST;
                    getLoadMoreController().setBottomAutoLoad(false);
                    break;
                }
                super.onScrollTopClick(0);
                ((MessagesDiscussionLoader) getLoader()).loadNew(true);
                this.refreshProvider.refreshCompleted();
                return true;
            case 2131625457:
                getServiceHelper().subscribeToDiscussion(getDiscussion());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        this.refreshProvider.setRefreshEnabled(false);
        this.wholeEmptyView.setWebState(WebState.PROGRESS);
        this.wholeEmptyView.setVisibility(0);
        getLoadMoreController().setTopPermanentState(LoadMoreState.DISABLED);
        getLoadMoreController().setBottomPermanentState(LoadMoreState.DISABLED);
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    protected void processResultGeneral(MessagesBundle<MessageComment, DiscussionInfoResponse> data) {
        updateTopLoadViewVisibility();
        super.processResultGeneral(data);
    }

    private void updateTopLoadViewVisibility() {
        if (getLoadMoreController().getTopPermanentState() == LoadMoreState.LOAD_IMPOSSIBLE) {
            this.loadTopView.findViewById(2131624783).setVisibility(8);
        } else {
            this.loadTopView.findViewById(2131624783).setVisibility(0);
        }
    }

    private void updateMenuItemsVisibility() {
        if (this._subscribeItem != null) {
            boolean hasShortLink;
            boolean unSubscribeVisible;
            boolean z;
            boolean goToTopVisibility;
            boolean goToEndVisibility;
            DiscussionGeneralInfo info = getLoadedDiscussionInfo();
            if (info == null) {
                hasShortLink = false;
                unSubscribeVisible = false;
                z = false;
            } else {
                Permissions permissions = info.permissions;
                z = permissions.canSubscribe;
                unSubscribeVisible = permissions.canUnsubscribe;
                hasShortLink = !ShortLink.createDiscussionLink((DiscussionInfoResponse) getLoadedBundle().generalInfo).isEmpty();
            }
            this._subscribeItem.setVisible(z);
            this._unSubscribeItem.setVisible(unSubscribeVisible);
            this._copyShortLink.setVisible(hasShortLink);
            if (getLoadMoreAdapter() == null || getLoadMoreController().getTopPermanentState() != LoadMoreState.LOAD_POSSIBLE) {
                goToTopVisibility = false;
            } else {
                goToTopVisibility = true;
            }
            this._goToTop.setVisible(goToTopVisibility);
            this.loadMoreButton.setVisibility(goToTopVisibility ? 0 : 8);
            this.loadTopView.findViewById(2131624785).setEnabled(goToTopVisibility);
            if (getLoadMoreAdapter() == null || getLoadMoreAdapter().isEmpty()) {
                goToEndVisibility = false;
            } else {
                goToEndVisibility = true;
            }
            this._goToEnd.setVisible(goToEndVisibility);
        }
    }

    private void registerReceiver() {
        if (this._notificationsReceiver == null) {
            IntentFilter filter = new IntentFilter("ru.ok.android.action.NOTIFY");
            filter.setPriority(1);
            FragmentActivity activity = getActivity();
            BroadcastReceiver discussionCommentsReceiver = new DiscussionCommentsReceiver();
            this._notificationsReceiver = discussionCommentsReceiver;
            activity.registerReceiver(discussionCommentsReceiver, filter);
        }
    }

    public void onResume() {
        super.onResume();
        getServiceHelper().addListener(this);
        removeExistingNotification();
        registerReceiver();
    }

    protected void onShowFragment() {
        super.onShowFragment();
        this.topicView.onShow();
    }

    protected void onHideFragment() {
        super.onHideFragment();
        if (this.topicView != null) {
            this.topicView.onHide();
        }
    }

    public void onPause() {
        super.onPause();
        getServiceHelper().removeListener(this);
        if (this._notificationsReceiver != null) {
            getActivity().unregisterReceiver(this._notificationsReceiver);
            this._notificationsReceiver = null;
        }
    }

    private void removeExistingNotification() {
        ((NotificationManager) getActivity().getSystemService("notification")).cancel(2);
    }

    protected MessagesDiscussionLoader createMessagesLoader() {
        return new MessagesDiscussionLoader(getActivity(), getDiscussion(), this.initialAnchor);
    }

    private void processDiscussionInfo(DiscussionInfoResponse info) {
        if (this.topicView != null) {
            this.topicView.configureForDiscussion(info, this);
            this.topicView.onShow();
        }
        if (this.loadTopView != null) {
            this.loadTopView.setVisibility(0);
        }
        updateAdminEnabledState(info);
        updateActionBarState();
    }

    public void onLoadFinished(Loader<MessagesLoaderBundle<MessageComment, DiscussionInfoResponse>> loader, MessagesLoaderBundle<MessageComment, DiscussionInfoResponse> loaderData) {
        super.onLoadFinished((Loader) loader, (MessagesLoaderBundle) loaderData);
        createStickyItem();
        if (this.stickyItemLayout == null) {
            return;
        }
        if (this.list.getFirstVisiblePosition() == 0 || !needStickyHeader()) {
            this.stickyItemLayout.setVisibility(4);
        } else {
            this.stickyItemLayout.setVisibility(0);
        }
    }

    private void createStickyItem() {
        if (needStickyHeader() && this.stickyItemView == null && getLoadedBundle() != null) {
            this.stickyItemView = (ViewGroup) DiscussionsAdapter.getDiscussionView(getActivity(), (DiscussionInfoResponse) getLoadedBundle().generalInfo);
            this.stickyItemView.setLayoutParams(new LayoutParams(-1, -2));
            this.stickyItemView.findViewById(2131624798).setVisibility(4);
            this.stickyItemView.findViewById(2131624974).setVisibility(8);
            this.stickyItemView.findViewById(2131624718).setVisibility(8);
            this.stickyItemView.setBackgroundResource(2130837845);
            ImageView stickyItemShadow = new ImageView(getContext());
            stickyItemShadow.setBackgroundResource(2130837607);
            LayoutParams params = new LayoutParams(-1, (int) Utils.dipToPixels(2.0f));
            params.addRule(3, 2131625185);
            stickyItemShadow.setLayoutParams(params);
            this.stickyItemLayout = new LinearLayout(getContext());
            this.stickyItemLayout.setOrientation(1);
            this.stickyItemLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            this.stickyItemLayout.addView(this.stickyItemView);
            this.stickyItemLayout.addView(stickyItemShadow);
            ((ViewGroup) getView().findViewById(2131625090)).addView(this.stickyItemLayout);
            this.stickyItemView.setOnClickListener(new C08344());
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!DeviceUtils.isPortrait(getActivity()) && !DeviceUtils.isTablet(getActivity())) {
            if (this.stickyItemLayout != null) {
                this.stickyItemLayout.setVisibility(4);
            }
            hideStickyHeader();
        } else if (this.stickyItemView == null) {
            createStickyItem();
        } else if (this.stickyItemView.getVisibility() != 0) {
            showStickyItem();
        }
    }

    private boolean needStickyHeader() {
        if (!DeviceUtils.isPortrait(getActivity()) && !DeviceUtils.isTablet(getActivity())) {
            return false;
        }
        if (this.list.getFirstVisiblePosition() > 1 || this.list.getLastVisiblePosition() != this.list.getCount() - 1) {
            return true;
        }
        return false;
    }

    protected void initCreateMessageView(View view) {
        super.initCreateMessageView(view);
        if (getLoader() != null && ((MessagesDiscussionLoader) getLoader()).isDataPresents()) {
            updateAdminEnabledState((DiscussionInfoResponse) ((MessagesDiscussionLoader) getLoader()).getLastData().bundle.generalInfo);
        }
    }

    private void updateAdminEnabledState(DiscussionInfoResponse info) {
        if (this.createMessageView != null && info != null && info.generalInfo != null) {
            this.createMessageView.setAdminEnabled(info.generalInfo.permissions.isAdmin);
            showInfoPopupIfNeeded();
        }
    }

    private void showInfoPopupIfNeeded() {
        DiscussionGeneralInfo info = getLoadedDiscussionInfo();
        if (info != null && info.permissions.isAdmin) {
            int gravity = 1;
            if (DeviceUtils.getType(getActivity()) != DeviceLayoutType.SMALL) {
                gravity = 0;
            }
            HighlightDialogFragment.highlightIfNecessary("send-as-admin", getChildFragmentManager(), getStringLocalized(2131165717), getStringLocalized(2131165716), gravity, this.createMessageView.getAdminView(), false);
        }
    }

    protected void processResultCustom(MessagesBundle<MessageComment, DiscussionInfoResponse> data) {
        if (this.fullDiscussionInfo == null && data.generalInfo == null) {
            this.fullDiscussionInfo = (DiscussionInfoResponse) data.generalInfo;
        } else {
            this.fullDiscussionInfo = (DiscussionInfoResponse) data.generalInfo;
        }
        if (this.fullDiscussionInfo != null) {
            processDiscussionInfo((DiscussionInfoResponse) data.generalInfo);
            this.topicView.configureForUserLikes(this.fullDiscussionInfo, ((MessagesDiscussionLoader) getLoader()).getLikedUsers());
        }
    }

    protected boolean isResetAdminState(MessageComment comment) {
        DiscussionGeneralInfo info = getLoadedDiscussionInfo();
        return (info == null || info.group == null || !TextUtils.equals(comment.authorId, info.group.id)) ? false : true;
    }

    public void onPhotoClicked(PhotoInfo photoInfo, PhotoAlbumInfo albumInfo) {
        int ownerType;
        String authorId;
        PhotoOwner photoOwner = new PhotoOwner();
        if (albumInfo == null || albumInfo.getOwnerType() == OwnerType.USER) {
            ownerType = 0;
            if (albumInfo != null) {
                authorId = albumInfo.getUserId();
            } else {
                authorId = null;
            }
        } else {
            ownerType = 1;
            authorId = albumInfo != null ? albumInfo.getGroupId() : null;
        }
        photoOwner.setType(ownerType);
        if (albumInfo == null) {
            authorId = photoInfo.getOwnerId();
        }
        photoOwner.setId(authorId);
        NavigationHelper.showPhoto(getActivity(), IntentUtils.createIntentForPhotoView(getActivity(), photoOwner, albumInfo == null ? null : albumInfo.getId(), photoInfo, null, 5), GifAsMp4PlayerHelper.shouldShowGifAsMp4(photoInfo) ? null : PhotoLayerAnimationHelper.makeScaleUpAnimationBundle(this.topicView.findViewById(C0263R.id.image), photoInfo.getStandartWidth(), photoInfo.getStandartHeight(), 0));
    }

    public RowPosition getRowPositionType(int position, int extendedPosition) {
        return RowPosition.SINGLE_FIRST_DATE;
    }

    public Bundle onMessage(android.os.Message message) {
        if (this.topicView == null) {
            return null;
        }
        String photoId = message.getData().getString("id");
        DiscussionState discussionState = this.topicView.getCurrentState();
        if (discussionState instanceof DiscussionPhotoState) {
            View photoView = this.topicView.findViewById(C0263R.id.image);
            PhotoInfo photoInfo = ((DiscussionPhotoState) discussionState).getPhotoInfo();
            if (photoInfo != null) {
                if (message.what == 1) {
                    photoView.setVisibility(TextUtils.equals(photoId, photoInfo.getId()) ? 4 : 0);
                } else if (message.what == 2 && TextUtils.equals(photoId, photoInfo.getId())) {
                    return PhotoLayerAnimationHelper.makeScaleDownAnimationBundle(photoView);
                }
            }
        } else if (discussionState instanceof DiscussionMediaTopicState) {
            if (this.animationBundleHandler == null) {
                this.animationBundleHandler = AnimationHelper.createStreamPhotoAnimationHandler(this.topicView);
            }
            return this.animationBundleHandler.onMessage(message, photoId);
        }
        return null;
    }

    public void onAlbumClicked(PhotoAlbumInfo albumInfo) {
        NavigationHelper.showPhotoAlbum(getActivity(), albumInfo);
    }

    public void onLikeInfoChanged(LikeInfoContext likeInfo) {
        if (getLoader() != null) {
            ((MessagesDiscussionLoader) getLoader()).onLikeInfo(likeInfo);
        }
    }

    public void onLikeCountClicked(boolean selfLike) {
        NavigationHelper.showDiscussionLikes(getActivity(), getDiscussion(), selfLike);
    }

    public void onMovieClicked(VideoGetResponse videoInfo) {
        NavigationHelper.showVideo(getActivity(), videoInfo.id, null);
    }

    public void onCommandResult(String commandName, ResultCode resultCode, Bundle data) {
        int textResId = 0;
        if (DiscussionUnSubscribeProcessor.isIt(commandName, getDiscussion())) {
            if (resultCode == ResultCode.SUCCESS) {
                this._subscribeItem.setVisible(true);
                this._unSubscribeItem.setVisible(false);
                textResId = 2131166751;
                NavigationHelper.finishActivity(getActivity());
            } else {
                textResId = 2131166750;
            }
        } else if (DiscussionSubscribeProcessor.isIt(commandName, getDiscussion())) {
            if (resultCode == ResultCode.SUCCESS) {
                this._subscribeItem.setVisible(false);
                this._unSubscribeItem.setVisible(true);
                textResId = 2131166661;
            } else {
                textResId = 2131166660;
            }
        }
        if (textResId != 0) {
            Toast.makeText(getActivity(), LocalizationManager.getString(getActivity(), textResId), 1).show();
        }
    }

    protected MessageAuthor getCurrentAuthor() {
        DiscussionGeneralInfo info = getLoadedDiscussionInfo();
        if (info == null || !this.createMessageView.isAdminSelected()) {
            return new MessageAuthor(OdnoklassnikiApplication.getCurrentUser().uid, null);
        }
        return new MessageAuthor(info.group.id, "GROUP");
    }

    public void onLikeCountClicked(String messageId) {
        NavigationHelper.showCommentLikes(getActivity(), getDiscussion(), messageId);
    }

    protected String getCustomTagForPositionInternal(MessageComment message) {
        return null;
    }

    protected int getAllLoadedMessageId() {
        return 0;
    }

    public void onAdminStateChanged(boolean isAdmin) {
        super.onAdminStateChanged(isAdmin);
        if (this.replyTo.getOriginalComment() != null) {
            String authorId = this.replyTo.getOriginalComment().authorId;
            DiscussionGeneralInfo info = getLoadedDiscussionInfo();
            if ((info != null && TextUtils.equals(authorId, info.group.id)) || TextUtils.equals(authorId, OdnoklassnikiApplication.getCurrentUser().uid)) {
                this.replyTo.setVisibility(8);
            }
        }
    }

    protected boolean isCommentingAllowed(DiscussionInfoResponse info) {
        return (info == null || info.generalInfo == null || !info.generalInfo.permissions.commentAllowed) ? false : true;
    }

    protected int getMessagingDisabledHintId() {
        return 2131165606;
    }

    protected int getNoMessagesTextId() {
        return 2131165709;
    }

    protected String getSettingsName() {
        return "discussion-comments-" + getDiscussion().type + "-" + getDiscussion().id;
    }

    protected int getWriteMessageHintId() {
        return 2131165335;
    }

    protected boolean isUserBlocked(MessagesBundle<MessageComment, DiscussionInfoResponse> data) {
        return data.generalInfo == null || ((DiscussionInfoResponse) data.generalInfo).generalInfo == null;
    }

    protected void positionListOnFirstPortion(ListView list) {
        int offset = 0;
        if (this.skipFirstScroll) {
            this.skipFirstScroll = false;
            return;
        }
        switch (AnonymousClass17.$SwitchMap$ru$ok$java$api$request$paging$PagingAnchor[this.initialAnchor.ordinal()]) {
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                selectLastRow();
            case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                positionOnFirstUnread(getAdapter().getData());
            case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                if (this.stickyItemView != null && this.stickyItemView.getVisibility() == 0) {
                    offset = this.stickyItemView.getMeasuredHeight() - 1;
                }
                list.setSelectionFromTop(getLoadMoreController().getExtraTopElements() + list.getHeaderViewsCount(), offset);
            default:
        }
    }

    protected int getMessageMenuId() {
        return 2131689481;
    }

    protected int getListBackgroundResourceId() {
        return 2131492983;
    }

    protected int getGeneralErrorTextId() {
        return 2131165708;
    }

    public void buildActions(QuickActionList action, OfflineMessage<MessageComment> offlineMessage) {
        ErrorType errorType;
        MessageComment message = offlineMessage.message;
        String currentId = OdnoklassnikiApplication.getCurrentUser().uid;
        Status status;
        if (offlineMessage.offlineData != null) {
            status = offlineMessage.offlineData.status;
        } else {
            status = null;
        }
        if (isMessageCopyAllowed(message)) {
            action.addActionItem(new ActionItem(2131625445, 2131165641));
        }
        if (message.flags.deletionAllowed && (status == null || Status.DELETE_ALLOWED.contains(status))) {
            action.addActionItem(new ActionItem(C0263R.id.delete, 2131165675));
        }
        if (message.flags.markAsSpamAllowed) {
            action.addActionItem(new ActionItem(2131625446, 2131166616));
        }
        if (message.flags.blockAllowed) {
            action.addActionItem(new ActionItem(2131625447, 2131165446));
        }
        if (isResendPossible(offlineMessage)) {
            action.addActionItem(new ActionItem(2131625448, 2131166463));
        }
        if (isEditPossible(offlineMessage)) {
            action.addActionItem(new ActionItem(2131625449, 2131165726));
        }
        if (offlineMessage.offlineData != null) {
            errorType = offlineMessage.offlineData.errorType;
        } else {
            errorType = null;
        }
        if (errorType != null) {
            action.addActionItem(new ActionItem(2131625450, 2131165820));
        }
        action.setOnActionItemClickListener(new C08355(offlineMessage));
    }

    protected boolean onInterceptMessageClick(OfflineMessage<MessageComment> offlineMessage) {
        return true;
    }

    protected int getErrorTextId(ErrorType errorType) {
        if (errorType != null) {
            switch (AnonymousClass17.f103x22ae40df[errorType.ordinal()]) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    return 2131165713;
                case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                    return 2131165801;
                case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                    return 2131165606;
                case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    return 2131165608;
            }
        }
        return super.getErrorTextId(errorType);
    }

    protected int getMessageEditTitleResourceId() {
        return 2131165725;
    }

    @Nullable
    private MessagesBundle<MessageComment, DiscussionInfoResponse> getLoadedBundle() {
        MessagesDiscussionLoader loader = (MessagesDiscussionLoader) getLoader();
        return loader == null ? null : loader.getBundle();
    }

    @Nullable
    private DiscussionGeneralInfo getLoadedDiscussionInfo() {
        MessagesBundle<MessageComment, DiscussionInfoResponse> bundle = getLoadedBundle();
        if (bundle == null || bundle.generalInfo == null) {
            return null;
        }
        return ((DiscussionInfoResponse) bundle.generalInfo).generalInfo;
    }

    protected void setupFirstPortionAnimations(UpdateListDataCommandBuilder<MessagesBundle<MessageComment, DiscussionInfoResponse>> builder) {
        builder.withRemoveAnimation(new C08366());
        builder.withCreateAnimation(new C08377());
        builder.withSlideInAnimation(new C08388());
        builder.withSlideOutAnimation(new C08399());
    }

    public void onScrollTopClick(int count) {
        onOptionsItemSelected(this._goToEnd);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    }

    private void scrollToPosition(int position) {
        this.list.setData(new UpdateListDataCommandBuilder().doNotChangeData(true).withListFinalPosition(new AnonymousClass10(position)).build());
    }

    protected void setListSizeChangeListener() {
        this.list.setOnSizeChangedListener(new OnSizeChangedListener() {

            /* renamed from: ru.ok.android.ui.fragments.messages.DiscussionCommentsFragment.11.1 */
            class C08291 implements Runnable {
                final /* synthetic */ int val$height;
                final /* synthetic */ int val$oldHeight;

                C08291(int i, int i2) {
                    this.val$oldHeight = i;
                    this.val$height = i2;
                }

                public void run() {
                    DiscussionCommentsFragment.this.scroll(this.val$oldHeight - this.val$height);
                }
            }

            public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
                if (!(DiscussionCommentsFragment.this.topicView == null || DiscussionCommentsFragment.this.getLoadedBundle() == null || DiscussionCommentsFragment.this.getLoadedBundle().generalInfo == null)) {
                    DiscussionCommentsFragment.this.topicView.updateState((DiscussionInfoResponse) DiscussionCommentsFragment.this.getLoadedBundle().generalInfo);
                }
                if (DiscussionCommentsFragment.this.messagesAdapter.getCount() > 0) {
                    OfflineMessage<MessageComment> replyingMessage = ((MessagesDiscussionAdapter) DiscussionCommentsFragment.this.getAdapter()).getReplyingMessage();
                    if (replyingMessage != null) {
                        DiscussionCommentsFragment.this.scrollToPosition(((MessagesDiscussionAdapter) DiscussionCommentsFragment.this.getAdapter()).getMessagePosition(replyingMessage));
                    } else if (height < oldHeight && width == oldWidth) {
                        DiscussionCommentsFragment.this.getView().post(new C08291(oldHeight, height));
                    }
                }
            }
        });
    }

    protected void setupNewMessagesView() {
        this.newMessagesView.setNewEventsMode(NewEventsMode.TEXT_AND_ARROW);
        this.newMessagesView.setTextResourceId(2131165609);
    }

    public void onReplyClicked(OfflineMessage<MessageComment> message) {
        super.onReplyClicked(message);
        int position = ((MessagesDiscussionAdapter) getAdapter()).getMessagePosition(message);
        if (position > 0) {
            scrollToPosition(position);
        }
        ((MessagesDiscussionAdapter) getAdapter()).setReplyingMessage(message);
        getAdapter().notifyDataSetChanged();
    }

    public void onReplyToCloseClicked() {
        super.onReplyToCloseClicked();
        ((MessagesDiscussionAdapter) getAdapter()).setReplyingMessage(null);
        getAdapter().notifyDataSetChanged();
    }

    public void onRepliedToClicked(OfflineMessage<MessageComment> message) {
        super.onRepliedToClicked(message);
        if (message.repliedToInfo != null && message.repliedToInfo.status == RepliedToInfo.Status.EXPANDED) {
            int position = ((MessagesDiscussionAdapter) getAdapter()).getMessagePosition(message);
            if (position > 1) {
                scrollToPosition(position - 1);
            }
        }
    }

    private void createTopLoadMoreView(Context context, ViewGroup parent) {
        this.loadTopView = (LoadMoreView) LayoutInflater.from(context).inflate(2130903276, parent, false);
        this.loadTopView.setVisibility(4);
        this.loadTopView.findViewById(2131624785).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DiscussionCommentsFragment.this.getLoadMoreController().startTopLoading();
            }
        });
        this.loadMoreButton = this.loadTopView.findViewById(2131624784);
        this.loadMoreButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DiscussionCommentsFragment.this.loadTopView.setVisibility(8);
                DiscussionCommentsFragment.this.onMenuItemClick(DiscussionCommentsFragment.this._goToTop);
            }
        });
    }

    protected LoadMoreAdapter createLoadMoreAdapter(BaseAdapter baseAdapter, ListView listView) {
        createTopLoadMoreView(getContext(), listView);
        LoadMoreAdapter adapter = new AnonymousClass15(getActivity(), baseAdapter, this, LoadMoreMode.BOTH, new DefaultLoadMoreViewProvider() {
            public LoadMoreView createLoadMoreView(Context context, boolean isTopView, ViewGroup parent) {
                if (isTopView) {
                    return DiscussionCommentsFragment.this.loadTopView;
                }
                return super.createLoadMoreView(context, isTopView, parent);
            }
        });
        adapter.getController().setTopMessageForState(LoadMoreState.LOAD_POSSIBLE, 2131166046);
        return adapter;
    }

    public void onSendText(String text, @Nullable MessageBase replyToMessage) {
        super.onSendText(text, replyToMessage);
        ((MessagesDiscussionAdapter) getAdapter()).setReplyingMessage(null);
    }

    protected int getShowAttachSourceId() {
        return 5;
    }

    @NonNull
    protected Sectionizer createSectionizer(Activity activity) {
        return new Sectionizer<MessagesBaseAdapter>() {
            public String getSectionTitleForItem(MessagesBaseAdapter adapter, int index) {
                return null;
            }
        };
    }

    protected boolean isTimeToLoadTop(int position) {
        return false;
    }
}
