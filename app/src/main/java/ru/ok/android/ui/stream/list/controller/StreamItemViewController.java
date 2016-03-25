package ru.ok.android.ui.stream.list.controller;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import ru.ok.android.fragments.web.hooks.WebLinksProcessor;
import ru.ok.android.statistics.stream.StreamBannerStatisticsHandler;
import ru.ok.android.ui.adapters.ScrollLoadBlocker;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView.OnImageSetListener;
import ru.ok.android.ui.custom.text.OdklUrlsTextView.OnSelectOdklLinkListener;
import ru.ok.android.ui.stream.StreamListStatistics;
import ru.ok.android.ui.stream.ViewPagerStateHolder;
import ru.ok.android.ui.stream.friendship.FriendShipDataHolder;
import ru.ok.android.ui.stream.groups.GroupsUsersHolder;
import ru.ok.android.ui.stream.list.ShownOnScrollListener;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;
import ru.ok.android.ui.stream.list.StreamSpannableTextItem.EntityClickListener;
import ru.ok.android.ui.stream.list.controller.AbsStreamItemViewController.NotifyContentChangeListener;
import ru.ok.android.ui.stream.music.PlayerStateHolder;
import ru.ok.android.ui.stream.view.FeedFooterView.OnCommentsClickListener;
import ru.ok.android.ui.stream.view.FeedFooterView.OnLikeListener;
import ru.ok.android.ui.stream.view.FeedHeaderView.FeedHeaderViewListener;
import ru.ok.android.ui.stream.view.StreamTrackView.StreamTrackViewListener;
import ru.ok.android.ui.stream.viewcache.StreamViewCache;
import ru.ok.android.ui.utils.ViewDrawObserver;
import ru.ok.android.ui.utils.ViewDrawObserver.ViewDrawListener;
import ru.ok.model.stream.Feed;

public interface StreamItemViewController {
    void clear();

    void close();

    void closeOptions();

    Activity getActivity();

    OnClickListener getBannerClickListener();

    OnCommentsClickListener getCommentsClickListener();

    WebLinksProcessor getExternalWebLinksProcessor();

    FeedHeaderViewListener getFeedHeaderViewListener();

    FeedHeaderViewListener getFeedReshareHeaderViewListener();

    FriendShipDataHolder getFriendShipDataHolder();

    OnClickListener getGeneralUsersClickListener();

    OnClickListener getGroupMembersClickListener();

    GroupsUsersHolder getGroupsFriendsHolder();

    ScrollLoadBlocker getImageLoadBlocker();

    OnClickListener getJoinGroupClickListener();

    LayoutInflater getLayoutInflater();

    OnLikeListener getLikeClickListener();

    OnClickListener getLinkClickListener();

    OnClickListener getMakePresentClickListener();

    OnClickListener getNavigateInternalListener();

    OnClickListener getOptionsClickListener();

    OnImageSetListener getPhotoActionsVisibilityListener();

    OnClickListener getPhotoClickListener();

    OnClickListener getPlacesClickListener();

    PlayerStateHolder getPlayerStateHolder();

    OnClickListener getPollAnswerClickListener();

    OnClickListener getPresentClickListener();

    OnClickListener getShowMoreClickListener();

    EntityClickListener getSpanClickListener();

    StreamAdapterListener getStreamAdapterListener();

    StreamBannerStatisticsHandler getStreamBannerStatisticsHandler();

    StreamListStatistics getStreamListStatistics();

    @Nullable
    StreamTrackViewListener getStreamTrackViewListener();

    OnClickListener getTextEditClickListener();

    OnSelectOdklLinkListener getTextViewLinkListener();

    OnClickListener getUserClickListener();

    OnClickListener getUserNamesClickListener();

    OnClickListener getVideoClickListener();

    StreamViewCache getViewCache();

    ViewDrawListener getViewDrawListener();

    ViewDrawObserver getViewDrawObserver();

    ViewPagerStateHolder getViewPagerStateHolder();

    WebLinksProcessor getWebLinksProcessor();

    boolean isDebugMode();

    boolean isOptionsButtonVisible(Feed feed);

    void onLayoutChanged();

    void setDebugMode(boolean z);

    void setFeedHeaderViewListener(FeedHeaderViewListener feedHeaderViewListener);

    void setFeedReshareHeaderViewListener(FeedHeaderViewListener feedHeaderViewListener);

    void setNotifyContentChangeListener(NotifyContentChangeListener notifyContentChangeListener);

    void setShownOnScrollListener(ShownOnScrollListener shownOnScrollListener);

    void setStreamBannerStatisticsHandler(StreamBannerStatisticsHandler streamBannerStatisticsHandler);

    void setStreamListStatistics(StreamListStatistics streamListStatistics);

    void setViewDrawObserver(ViewDrawObserver viewDrawObserver);
}
