package ru.ok.android.ui.mediatopics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.Collection;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.mediatopic.MediaTopicsResponse;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.custom.loadmore.LoadMoreAdapterListener;
import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.ui.dialogs.DeleteMediaTopicDialog;
import ru.ok.android.ui.dialogs.SetToStatusMediaTopicDialog;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.ui.groups.data.MediaTopicsListLoader;
import ru.ok.android.ui.groups.data.MediaTopicsListLoaderResult;
import ru.ok.android.ui.groups.fragments.PagerSelectedListener;
import ru.ok.android.ui.stream.BaseStreamRefreshRecyclerFragment;
import ru.ok.android.ui.stream.list.StreamItem;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;
import ru.ok.android.ui.stream.list.controller.MediaTopicsStreamViewController;
import ru.ok.android.ui.stream.list.controller.MediaTopicsStreamViewController.MediaTopicsStreamAdapterListener;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.utils.HideTabbarItemDecorator;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.bus.BusMediatopicHelper;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.Feed;

public abstract class MediaTopicsListFragment extends BaseStreamRefreshRecyclerFragment implements LoaderCallbacks<MediaTopicsListLoaderResult>, LoadMoreAdapterListener, PagerSelectedListener, MediaTopicsStreamAdapterListener {
    private String filter;
    protected String groupId;
    private boolean isPageSelected;
    private boolean isPageWasSelected;
    private String stateAnchorForward;
    private Long stateTagId;
    protected String userId;

    /* renamed from: ru.ok.android.ui.mediatopics.MediaTopicsListFragment.1 */
    static /* synthetic */ class C10371 {
        static final /* synthetic */ int[] f110x22ae40df;

        static {
            f110x22ae40df = new int[ErrorType.values().length];
            try {
                f110x22ae40df[ErrorType.NO_INTERNET.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f110x22ae40df[ErrorType.RESTRICTED_ACCESS_ACTION_BLOCKED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f110x22ae40df[ErrorType.USER_BLOCKED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f110x22ae40df[ErrorType.YOU_ARE_IN_BLACK_LIST.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f110x22ae40df[ErrorType.RESTRICTED_ACCESS_FOR_NON_FRIENDS.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f110x22ae40df[ErrorType.RESTRICTED_ACCESS_SECTION_FOR_FRIENDS.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    protected abstract int getMediaTopicDeleteFailedStringResId();

    protected abstract int getMediaTopicDeleteSuccessStringResId();

    public MediaTopicsListFragment() {
        this.isPageWasSelected = false;
        this.isPageSelected = false;
    }

    public static Bundle newArguments(String userId, String groupId, String filter) {
        Bundle bundle = new Bundle();
        bundle.putString("user_id", userId);
        bundle.putString("group_id", groupId);
        bundle.putString("filter", filter);
        return bundle;
    }

    public static Bundle newArguments(String userId, String groupId, String filter, Long tagId) {
        Bundle bundle = newArguments(userId, groupId, filter);
        if (tagId != null) {
            bundle.putLong("tag_id", tagId.longValue());
        }
        return bundle;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userId = getArguments().getString("user_id");
        this.groupId = getArguments().getString("group_id");
        this.filter = getArguments().getString("filter");
        if (getArguments().get("tag_id") != null) {
            this.stateTagId = Long.valueOf(getArguments().getLong("tag_id"));
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(123, getArguments(), this);
        Context context = getActivity();
        if (DeviceUtils.isSmall(context)) {
            this.recyclerView.addItemDecoration(new HideTabbarItemDecorator(context));
        }
    }

    public Loader<MediaTopicsListLoaderResult> onCreateLoader(int id, Bundle args) {
        return new MediaTopicsListLoader(getActivity(), this.groupId, this.userId, this.filter, this.stateTagId, Integer.valueOf(20));
    }

    public void onLoadFinished(Loader<MediaTopicsListLoaderResult> loader, MediaTopicsListLoaderResult result) {
        boolean z = true;
        Type type = getSmartEmptyViewAnimatedType();
        this.loadMoreAdapter.getController().setBottomCurrentState(LoadMoreState.IDLE);
        if (result.isSuccess) {
            MediaTopicsResponse response = result.mediaTopicsResponse;
            List<StreamItem> loadedItems = result.mediaTopicStreamItems;
            if (result.requestAnchor == null) {
                this.recyclerView.getLayoutManager().scrollToPosition(0);
                if (this.scrollTopView != null) {
                    this.scrollTopView.onScroll(false, true, false, false);
                }
                this.streamItemRecyclerAdapter.setItems(loadedItems);
                this.streamItemRecyclerAdapter.notifyDataSetChanged();
            } else {
                int positionChangedStart = this.streamItemRecyclerAdapter.getItemCount();
                this.streamItemRecyclerAdapter.addItems(loadedItems);
                this.loadMoreAdapter.notifyItemRangeInserted(this.loadMoreAdapter.getController().getExtraTopElements() + positionChangedStart, loadedItems.size());
            }
            if (result.requestDirection == PagingDirection.FORWARD) {
                this.stateAnchorForward = response.anchor;
                this.loadMoreAdapter.getController().setBottomPermanentState(result.mediaTopicsResponse.hasMore ? LoadMoreState.LOAD_POSSIBLE_NO_LABEL : LoadMoreState.DISABLED);
            }
        } else {
            Logger.m185w("Failed load group topics for groupId %s", this.groupId);
            type = convertErrorType(result.errorType);
            LoadMoreState loadingState = (result.errorType != ErrorType.NO_INTERNET || this.streamItemRecyclerAdapter.getItemCount() <= 0) ? LoadMoreState.DISABLED : LoadMoreState.DISCONNECTED;
            this.loadMoreAdapter.getController().setBottomPermanentState(loadingState);
        }
        this.streamItemRecyclerAdapter.setLoading(false);
        this.refreshProvider.refreshCompleted();
        this.emptyView.setType(type);
        this.emptyView.setState(State.LOADED);
        RecyclerView recyclerView = this.recyclerView;
        if (this.emptyView.getVisibility() == 0) {
            z = false;
        }
        recyclerView.setNestedScrollingEnabled(z);
    }

    public boolean isEmpty() {
        return this.emptyView != null && this.emptyView.getVisibility() == 0;
    }

    private Type convertErrorType(ErrorType errorType) {
        switch (C10371.f110x22ae40df[errorType.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return Type.NO_INTERNET;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                if (this.groupId != null) {
                    return Type.GROUP_BLOCKED;
                }
                return Type.USER_BLOCKED;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return Type.RESTRICTED_YOU_ARE_IN_BLACK_LIST;
            case Message.UUID_FIELD_NUMBER /*5*/:
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return Type.RESTRICTED_ACCESS_FOR_FRIENDS;
            default:
                return Type.ERROR;
        }
    }

    private void loadNextBottomChunk() {
        MediaTopicsListLoader topicsListLoader = getMediaTopicsListLoader();
        if (topicsListLoader != null) {
            this.emptyView.setState(State.LOADING);
            topicsListLoader.setAnchor(this.stateAnchorForward);
            topicsListLoader.setTagId(this.stateTagId);
            topicsListLoader.setDirection(PagingDirection.FORWARD);
            topicsListLoader.forceLoad();
            this.streamItemRecyclerAdapter.setLoading(true);
        }
    }

    protected Type getSmartEmptyViewAnimatedType() {
        return Type.GROUP_TOPICS_LIST;
    }

    public void onLoaderReset(Loader<MediaTopicsListLoaderResult> loader) {
    }

    public void openDiscussion(DiscussionSummary discussionSummary, Page page) {
        NavigationHelper.showDiscussionCommentsFragment(getActivity(), discussionSummary.discussion, page, null);
    }

    public void onCommentClicked(int position, Feed feed, DiscussionSummary discussionSummary) {
        openDiscussion(discussionSummary, Page.MESSAGES);
    }

    public void onMediaTopicClicked(int position, Feed feed, DiscussionSummary discussionSummary) {
        openDiscussion(discussionSummary, Page.INFO);
    }

    public void onPageSelected() {
        if (!this.isPageWasSelected) {
            this.isPageWasSelected = true;
            onRefresh();
        }
        if (this.scrollTopView != null) {
            getCoordinatorManager().ensureFab(this.scrollTopView, "fab_stream");
            this.scrollTopView.setAlpha(1.0f);
            this.scrollTopView.setOnClickListener(this);
            if (!this.isPageSelected) {
                this.scrollTopView.onScroll(false, true, false, true);
            }
        }
        this.isPageSelected = true;
    }

    public void onPageNotSelected() {
        this.isPageSelected = false;
    }

    public void onPageScrolledOffset(float offset) {
        float absOffset = Math.abs(offset);
        if (absOffset >= 0.5f) {
            this.scrollTopView.setVisibility(8);
        } else if (this.scrollTopView.getVisibility() == 0) {
            this.scrollTopView.setAlpha(1.0f - (2.0f * absOffset));
        }
    }

    public void setSwipeRefreshRefreshing(boolean refreshing) {
        this.swipeRefreshLayout.setRefreshing(refreshing);
    }

    public void onRefresh() {
        this.emptyView.setState(State.LOADING);
        MediaTopicsListLoader topicsListLoader = getMediaTopicsListLoader();
        topicsListLoader.setAnchor(null);
        topicsListLoader.setTagId(this.stateTagId);
        topicsListLoader.forceLoad();
    }

    protected Collection<? extends GeneralUserInfo> getFilteredUsers() {
        return null;
    }

    public void setGroupTagId(Long tagId) {
        if (tagId != this.stateTagId) {
            this.stateTagId = tagId;
            this.stateAnchorForward = null;
            MediaTopicsListLoader topicsListLoader = getMediaTopicsListLoader();
            topicsListLoader.setTagId(this.stateTagId);
            topicsListLoader.setAnchor(null);
            topicsListLoader.forceLoad();
        }
    }

    public void onDeleteClicked(int position, Feed feed, int itemAdapterPosition) {
        DeleteMediaTopicDialog dialog = DeleteMediaTopicDialog.newInstance(feed.getId(), feed.getDeleteId());
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "mediatopic-delete");
    }

    public void onMediaTopicSetToStatusClicked(int position, Feed feed) {
        SetToStatusMediaTopicDialog dialog = SetToStatusMediaTopicDialog.newInstance(feed.getId());
        dialog.setTargetFragment(this, 2);
        dialog.show(getFragmentManager(), "mediatopic-set-to-status");
    }

    public void onMediaTopicPinClicked(int position, Feed feed) {
        BusMediatopicHelper.pin(feed.getId(), true, null);
    }

    public void onMediaTopicUnPinClicked(int position, Feed feed) {
        BusMediatopicHelper.pin(feed.getId(), false, null);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RECEIVED_VALUE:
                if (resultCode == -1) {
                    BusMediatopicHelper.delete(data.getLongExtra("MEDIATOPIC_ID", 0), data.getStringExtra("MEDIATOPIC_DELETE_ID"), null);
                }
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (resultCode == -1) {
                    BusMediatopicHelper.setToStatus(data.getLongExtra("MEDIATOPIC_ID", 0), null);
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624192)
    public void onMediaTopicDelete(BusEvent event) {
        if (event.resultCode == -1) {
            long mediaTopicId = event.bundleInput.getLong("mediatopic_id", 0);
            if (mediaTopicId != 0) {
                this.streamItemRecyclerAdapter.deleteItemsWithFeedId(mediaTopicId);
                TimeToast.show(getContext(), getMediaTopicDeleteSuccessStringResId(), 0);
                return;
            }
            return;
        }
        TimeToast.show(getContext(), getMediaTopicDeleteFailedStringResId(), 1);
    }

    @Subscribe(on = 2131623946, to = 2131624194)
    public void onMediaTopicSetToStatus(BusEvent event) {
        if (event.resultCode == -1) {
            TimeToast.show(getContext(), 2131166178, 0);
        } else {
            TimeToast.show(getContext(), 2131166176, 1);
        }
    }

    protected StreamItemViewController obtainStreamItemViewController(Activity activity, StreamAdapterListener streamAdapterListener, String logContext) {
        return new MediaTopicsStreamViewController(activity, streamAdapterListener, this, logContext);
    }

    public void onLoadMoreTopClicked() {
    }

    public void onLoadMoreBottomClicked() {
        if (!this.streamItemRecyclerAdapter.isLoading()) {
            loadNextBottomChunk();
        }
    }

    protected MediaTopicsListLoader getMediaTopicsListLoader() {
        return (MediaTopicsListLoader) getLoaderManager().getLoader(123);
    }
}
