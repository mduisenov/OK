package ru.ok.android.ui.stream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.mediatopic_polls.MtPollsManager.PollAnswersChangeListener;
import ru.ok.android.statistics.stream.StreamBannerStatisticsHandler;
import ru.ok.android.statistics.stream.StreamStats;
import ru.ok.android.storage.Storages;
import ru.ok.android.ui.RecyclerViewSizeListenable;
import ru.ok.android.ui.activity.compat.CoordinatorManager;
import ru.ok.android.ui.custom.OnSizeChangedListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapterListener;
import ru.ok.android.ui.custom.loadmore.LoadMoreMode;
import ru.ok.android.ui.custom.loadmore.LoadMoreRecyclerAdapter;
import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;
import ru.ok.android.ui.custom.scroll.ScrollTopView.OnClickScrollListener;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.ui.dialogs.ConfirmationDialog;
import ru.ok.android.ui.dialogs.DeleteFeedDialog;
import ru.ok.android.ui.groups.list.StreamItemRecyclerAdapter;
import ru.ok.android.ui.stream.FeedHeaderActionsDialog.FeedHeaderActionsDialogListener;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;
import ru.ok.android.ui.stream.list.StreamLayoutConfig;
import ru.ok.android.ui.stream.list.controller.DefaultStreamViewController;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.FeedHeaderInfo;
import ru.ok.android.ui.stream.view.FeedHeaderView.FeedHeaderViewListener;
import ru.ok.android.ui.stream.view.StreamScrollTopView;
import ru.ok.android.ui.utils.FabHelper;
import ru.ok.android.ui.utils.ItemCountChangedDataObserver;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.SpannableUtils;
import ru.ok.android.utils.StreamUtils;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.bus.BusStreamHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedGroupEntity;
import ru.ok.model.stream.entities.FeedUserEntity;
import ru.ok.model.stream.message.FeedActorSpan;
import ru.ok.model.stream.message.FeedTargetSpan;

public abstract class BaseStreamRefreshRecyclerFragment extends BaseRefreshRecyclerFragment implements OnClickListener, PollAnswersChangeListener, LoadMoreAdapterListener, OnClickScrollListener, FeedHeaderActionsDialogListener, StreamAdapterListener, FeedHeaderViewListener {
    private static String LOG_CONTEXT;
    protected int currentScreenOrientation;
    protected final StreamLayoutConfig layoutConfig;
    final OnSizeChangedListener listViewSizeChangedListener;
    protected LoadMoreRecyclerAdapter loadMoreAdapter;
    protected StreamScrollTopView scrollTopView;
    protected StreamBannerStatisticsHandler statHandler;
    protected StreamItemRecyclerAdapter streamItemRecyclerAdapter;

    /* renamed from: ru.ok.android.ui.stream.BaseStreamRefreshRecyclerFragment.1 */
    class C12211 implements OnSizeChangedListener {
        C12211() {
        }

        public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
            Activity activity = BaseStreamRefreshRecyclerFragment.this.getActivity();
            if (activity != null) {
                StreamUtils.updateLayoutConfig(BaseStreamRefreshRecyclerFragment.this.layoutConfig, width, BaseStreamRefreshRecyclerFragment.this.currentScreenOrientation, activity, BaseStreamRefreshRecyclerFragment.this.getFragmentManager());
                BaseStreamRefreshRecyclerFragment.this.updateStreamLayout();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.stream.BaseStreamRefreshRecyclerFragment.2 */
    class C12222 extends OnScrollListener {
        C12222() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int deltaY) {
            boolean wantToShow;
            boolean wantToShowExpanded;
            boolean wantToHide;
            boolean wantToHideExpanded = true;
            int firstVisisbleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            if (firstVisisbleItem <= 2 || deltaY > -1) {
                wantToShow = false;
            } else {
                wantToShow = true;
            }
            if (firstVisisbleItem <= 2) {
                wantToShowExpanded = true;
            } else {
                wantToShowExpanded = false;
            }
            if (firstVisisbleItem <= 2 || deltaY > 1) {
                wantToHide = true;
            } else {
                wantToHide = false;
            }
            if (firstVisisbleItem <= 2) {
                wantToHideExpanded = false;
            }
            BaseStreamRefreshRecyclerFragment.this.scrollTopView.onScroll(wantToShow, wantToHide, wantToShowExpanded, wantToHideExpanded);
        }
    }

    /* renamed from: ru.ok.android.ui.stream.BaseStreamRefreshRecyclerFragment.3 */
    class C12233 extends ItemCountChangedDataObserver {
        C12233() {
        }

        public void onItemCountMayChange() {
            boolean isEmpty;
            int i = 0;
            if (BaseStreamRefreshRecyclerFragment.this.streamItemRecyclerAdapter.getItemCount() == 0) {
                isEmpty = true;
            } else {
                isEmpty = false;
            }
            SmartEmptyViewAnimated smartEmptyViewAnimated = BaseStreamRefreshRecyclerFragment.this.emptyView;
            if (!isEmpty) {
                i = 8;
            }
            smartEmptyViewAnimated.setVisibility(i);
            if (isEmpty) {
                BaseStreamRefreshRecyclerFragment.this.appBarExpand();
            }
        }
    }

    protected abstract Collection<? extends GeneralUserInfo> getFilteredUsers();

    public BaseStreamRefreshRecyclerFragment() {
        this.layoutConfig = new StreamLayoutConfig();
        this.currentScreenOrientation = 0;
        this.listViewSizeChangedListener = new C12211();
    }

    static {
        LOG_CONTEXT = "group-topics";
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.currentScreenOrientation = getContext().getResources().getConfiguration().orientation;
        Storages.getInstance(getActivity(), OdnoklassnikiApplication.getCurrentUser().getId()).getMtPollsManager().addWeakPollAnswersChangeListener(this);
        this.statHandler = new StreamBannerStatisticsHandler(getActivity());
    }

    protected void onShowFragment() {
        Logger.m172d("");
        super.onShowFragment();
    }

    protected void onHideFragment() {
        Logger.m172d("");
        super.onHideFragment();
    }

    protected void ensureFab() {
        super.ensureFab();
        if (this.scrollTopView != null) {
            FabHelper.updateScrollTopAnchoring(getCoordinatorManager().coordinatorLayout, this.scrollTopView);
            getCoordinatorManager().ensureFab(this.scrollTopView, "fab_stream");
        }
    }

    protected void removeFab() {
        super.removeFab();
        if (this.scrollTopView != null) {
            getCoordinatorManager().remove(this.scrollTopView);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(2131493183));
        onCreateScrollTopView(view);
        return view;
    }

    protected int getLayoutId() {
        return 2130903366;
    }

    protected void onCreateScrollTopView(View fragmentView) {
        CoordinatorManager coordinatorManager = getCoordinatorManager();
        if (coordinatorManager != null) {
            this.scrollTopView = (StreamScrollTopView) coordinatorManager.getFabById(2131624644);
            if (this.scrollTopView == null) {
                this.scrollTopView = FabHelper.createStreamScrollTop(getContext(), coordinatorManager.coordinatorLayout);
                this.scrollTopView.setOnClickListener(this);
            }
            this.recyclerViewScrollListeners.addListener(new C12222());
        }
    }

    public void onClick(View v) {
        if (v.getId() == 2131624644) {
            onScrollTopClick(1);
        }
    }

    public void onScrollTopClick(int count) {
        scrollToTop();
        appBarExpand();
    }

    public void scrollToTop() {
        int firstCompletelyVisibleItemPosition = this.recyclerLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (firstCompletelyVisibleItemPosition > 0) {
            if (firstCompletelyVisibleItemPosition > 15) {
                this.recyclerView.scrollToPosition(15);
            }
            this.recyclerView.smoothScrollToPosition(0);
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((RecyclerViewSizeListenable) this.recyclerView).setOnSizeChangedListener(this.listViewSizeChangedListener);
    }

    protected Adapter createRecyclerAdapter() {
        this.streamItemRecyclerAdapter = new StreamItemRecyclerAdapter(getActivity(), obtainStreamItemViewController(getActivity(), this, getLogContext()), this, this.statHandler, getLogContext());
        this.streamItemRecyclerAdapter.getStreamItemViewController().setFeedHeaderViewListener(this);
        this.streamItemRecyclerAdapter.getStreamItemViewController().setFeedReshareHeaderViewListener(this);
        this.streamItemRecyclerAdapter.setHasStableIds(true);
        this.loadMoreAdapter = new LoadMoreRecyclerAdapter(getActivity(), this.streamItemRecyclerAdapter, this, LoadMoreMode.BOTTOM);
        this.loadMoreAdapter.getController().setBottomPermanentState(LoadMoreState.DISABLED);
        this.loadMoreAdapter.getController().setBottomAutoLoad(true);
        return this.loadMoreAdapter;
    }

    protected void registerEmptyViewVisibilityAdapterObserver() {
        this.streamItemRecyclerAdapter.registerAdapterDataObserver(new C12233());
    }

    protected String getLogContext() {
        return LOG_CONTEXT;
    }

    protected void updateStreamLayout() {
        if (getActivity() != null && this.streamItemRecyclerAdapter != null) {
            this.streamItemRecyclerAdapter.updateForLayoutSize(this.recyclerView, this.layoutConfig);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        if (orientation != this.currentScreenOrientation) {
            this.currentScreenOrientation = orientation;
            onConfigurationOrientationChanged();
        }
    }

    protected void onConfigurationOrientationChanged() {
    }

    public void onPause() {
        super.onPause();
        this.streamItemRecyclerAdapter.onPause();
        this.streamItemRecyclerAdapter.getStreamItemViewController().closeOptions();
    }

    public void onResume() {
        super.onResume();
        this.streamItemRecyclerAdapter.onResume();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (resultCode == -1) {
                    BusStreamHelper.feedMarkAsSpam(data.getLongExtra("FEED_ID", 0), data.getStringExtra("MARK_AS_SPAM_ID"), LOG_CONTEXT);
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onMarkAsSpamClicked(int position, Feed feed, int itemAdapterPosition) {
        ConfirmationDialog dialog = ConfirmationDialog.newInstance(2131165870, 2131165869, 2131165623, 2131165476, 1);
        dialog.setTargetFragment(this, 1);
        dialog.getArguments().putLong("FEED_ID", feed.getId());
        dialog.getArguments().putString("MARK_AS_SPAM_ID", feed.getSpamId());
        dialog.show(getFragmentManager(), null);
    }

    public void onLikeClicked(int position, Feed feed, LikeInfoContext likeInfo) {
        Logger.m173d("feedId=%d, likeInfo=%s", Long.valueOf(feed.getId()), likeInfo);
        Activity activity = getActivity();
        if (activity != null) {
            Storages.getInstance(activity, OdnoklassnikiApplication.getCurrentUser().getId()).getLikeManager().toggle(likeInfo);
        }
    }

    public LikeInfoContext onLikePhotoClicked(int position, Feed feed, LikeInfoContext likeInfo) {
        Logger.m173d("feedId=%d, likeInfo=%s", Long.valueOf(feed.getId()), likeInfo);
        StreamStats.clickLikePhoto(position, feed, likeInfo);
        Activity activity = getActivity();
        if (activity != null) {
            return Storages.getInstance(activity, OdnoklassnikiApplication.getCurrentUser().getId()).getLikeManager().toggle(likeInfo);
        }
        return likeInfo;
    }

    public void onMediaTopicTextEditClick(String topicId, int blockIndex, String text) {
    }

    @Subscribe(on = 2131623946, to = 2131624249)
    public void onStreamMarkAsSpamResult(BusEvent event) {
        if (event.resultCode == -1) {
            TimeToast.show(getContext(), 2131166067, 0);
        } else {
            Toast.makeText(getContext(), LocalizationManager.getString(getContext(), 2131166614), 1).show();
        }
    }

    public void onDeleteClicked(int position, Feed feed, int itemAdapterPosition) {
        Logger.m173d("feedId=%d", Long.valueOf(feed.getId()));
        ArrayList<String> friendIds = new ArrayList();
        ArrayList<String> groupIds = new ArrayList();
        List<? extends BaseEntity> feedOwners = feed.getFeedOwners();
        String currentUserId = OdnoklassnikiApplication.getCurrentUser().uid;
        for (BaseEntity feedOwner : feedOwners) {
            switch (feedOwner.getType()) {
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    groupIds.add(((FeedGroupEntity) feedOwner).getGroupInfo().getId());
                    break;
                case Message.ATTACHES_FIELD_NUMBER /*7*/:
                    String uid = ((FeedUserEntity) feedOwner).getUserInfo().uid;
                    if (!TextUtils.equals(currentUserId, uid)) {
                        friendIds.add(uid);
                        break;
                    }
                    break;
                default:
                    break;
            }
        }
        DeleteFeedDialog dialog = DeleteFeedDialog.newInstance(position, feed, friendIds, groupIds, itemAdapterPosition);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "feed-delete");
        if (isStreamStatsEnabled()) {
            StreamStats.clickHide(position, feed);
        }
    }

    public void onClickedAvatar(FeedHeaderInfo info) {
        Logger.m173d("info=%s", info.message);
        onClickedFeedHeader(info, null, false, "avatar_header");
    }

    private void openInfo(GeneralUserInfo info, String source) {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            if (info.getObjectType() == 1) {
                NavigationHelper.showGroupInfo(activity, info.getId());
                if (isStreamStatsEnabled()) {
                    StreamStats.clickGroup(source);
                }
            } else if (info.getObjectType() == 0) {
                NavigationHelper.showUserInfo(activity, info.getId());
                if (isStreamStatsEnabled()) {
                    StreamStats.clickUser(source);
                }
            }
        }
    }

    public void onUsersSelected(int position, Feed feed, ArrayList<UserInfo> users) {
        Logger.m173d("users=%s", users);
        FeedHeaderActionsDialog dialog = FeedHeaderActionsDialog.newInstance(users, "with_friends");
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "with_friends");
        if (isStreamStatsEnabled()) {
            StreamStats.clickEntity2(position, feed);
        }
    }

    public void onClickedFeedHeader(FeedHeaderInfo info) {
        Feed feed = info.feed.feed;
        Logger.m173d("info.message=%s", info.message);
        boolean isLike = info.isUsersAreLikeAuthors;
        onClickedFeedHeader(info, isLike ? StringUtils.uppercaseFirst(DateFormatter.formatDeltaTimePast(getActivity(), feed.getDate(), false, false)) : null, isLike, "header_text");
    }

    private void onClickedFeedHeader(FeedHeaderInfo info, String title, boolean showSingleUserInDialog, String source) {
        Feed feed = info.feed.feed;
        int position = info.feed.position;
        Logger.m177e("Like: %s", Boolean.valueOf(feed.hasDataFlag(4)));
        openUserInfos(info.referencedUsers, source, "feed_header_dialog", title, showSingleUserInDialog);
        if (isStreamStatsEnabled()) {
            int actorSpanStart = SpannableUtils.getFirstSpanStart(info.message, FeedActorSpan.class);
            int targetSpanStart = SpannableUtils.getFirstSpanStart(info.message, FeedTargetSpan.class);
            if (actorSpanStart >= 0 && (targetSpanStart < 0 || actorSpanStart <= targetSpanStart)) {
                StreamStats.clickEntity1(position, feed);
            } else if (targetSpanStart >= 0 && (actorSpanStart < 0 || targetSpanStart <= actorSpanStart)) {
                StreamStats.clickEntity2(position, feed);
            }
        }
        ArrayList<String> authorClickPixels = feed.getStatPixels(3);
        if (authorClickPixels != null && this.statHandler != null) {
            this.statHandler.onClick(authorClickPixels);
        }
    }

    private void openUserInfos(ArrayList<GeneralUserInfo> infos, String source, String dialogTag, String title, boolean showSingleUserInDialog) {
        if (infos != null && !infos.isEmpty()) {
            Collection<? extends GeneralUserInfo> filtered = getFilteredUsers();
            if (!(filtered == null || filtered.isEmpty())) {
                ArrayList<GeneralUserInfo> infos2 = new ArrayList(infos);
                infos2.removeAll(filtered);
                infos = infos2;
            }
            int infoCount = infos.size();
            if (infoCount > 0) {
                if (infoCount != 1 || showSingleUserInDialog) {
                    FeedHeaderActionsDialog dialog = FeedHeaderActionsDialog.newInstance(infos, title, source);
                    dialog.setTargetFragment(this, 0);
                    dialog.show(getFragmentManager(), dialogTag);
                    return;
                }
                onFeedHeaderActionSelected((GeneralUserInfo) infos.get(0), source);
            }
        }
    }

    public void onFeedHeaderActionSelected(GeneralUserInfo info, String source) {
        Logger.m173d("info=%s", info);
        openInfo(info, source);
    }

    public void onGeneralUsersInfosClicked(int position, Feed feed, ArrayList<GeneralUserInfo> infos, String source) {
        Logger.m173d("infos=%s", infos);
        openUserInfos(infos, source, "general-users", null, false);
        if (isStreamStatsEnabled()) {
            StreamStats.clickEntity1(position, feed);
        }
    }

    public void onDestroy() {
        this.streamItemRecyclerAdapter.close();
        super.onDestroy();
    }

    protected StreamItemViewController obtainStreamItemViewController(Activity activity, StreamAdapterListener streamAdapterListener, String logContext) {
        return new DefaultStreamViewController(activity, streamAdapterListener, logContext);
    }

    protected boolean isStreamStatsEnabled() {
        return false;
    }

    public void onPollAnswersChanged(String pollId) {
        Logger.m173d("pollId=%s", pollId);
        int streamItemsTopOffset = getRecyclerAdapterStreamItemsTopOffset();
        LinearLayoutManager lm = getRecyclerViewLayoutManager();
        int firstVisiblePosition = lm.findFirstVisibleItemPosition();
        int lastVisiblePosition = lm.findLastVisibleItemPosition();
        if (firstVisiblePosition != -1 && lastVisiblePosition != -1) {
            this.streamItemRecyclerAdapter.notifyPollAnswersChanged(pollId, Math.max(0, firstVisiblePosition - streamItemsTopOffset), lastVisiblePosition - streamItemsTopOffset);
        }
    }

    protected int getRecyclerAdapterStreamItemsTopOffset() {
        return getHeadersCount() + this.loadMoreAdapter.getController().getExtraTopElements();
    }

    protected int getHeadersCount() {
        return 0;
    }
}
