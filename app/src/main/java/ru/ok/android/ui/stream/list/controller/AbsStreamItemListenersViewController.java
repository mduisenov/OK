package ru.ok.android.ui.stream.list.controller;

import android.app.Activity;
import android.media.AudioManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.web.shortlinks.SendPresentShortLinkBuilder;
import ru.ok.android.model.pagination.impl.PhotoInfoPage;
import ru.ok.android.services.processors.banners.BannerLinksUtils;
import ru.ok.android.statistics.stream.StreamBannerStatisticsHandler;
import ru.ok.android.statistics.stream.StreamStats;
import ru.ok.android.storage.Storages;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView.OnImageSetListener;
import ru.ok.android.ui.custom.text.OdklUrlsTextView.OnSelectOdklLinkListener;
import ru.ok.android.ui.groups.data.GroupSectionItem;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.PresentInfo;
import ru.ok.android.ui.stream.list.StreamItemAdapter;
import ru.ok.android.ui.stream.list.StreamItemAdapter.StreamAdapterListener;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.StreamPollAnswerItem;
import ru.ok.android.ui.stream.list.StreamSpannableTextItem.EntityClickListener;
import ru.ok.android.ui.stream.photos.PhotosFeedAdapter;
import ru.ok.android.ui.stream.view.FeedFooterInfo;
import ru.ok.android.ui.stream.view.FeedFooterView;
import ru.ok.android.ui.stream.view.FeedFooterView.OnCommentsClickListener;
import ru.ok.android.ui.stream.view.FeedFooterView.OnLikeListener;
import ru.ok.android.ui.stream.view.FeedHeaderView.FeedHeaderViewListener;
import ru.ok.android.ui.stream.view.FeedOptionsPopupWindow;
import ru.ok.android.ui.stream.view.FeedOptionsPopupWindow.FeedOptionsPopupListener;
import ru.ok.android.ui.stream.view.StreamTrackView;
import ru.ok.android.ui.stream.view.StreamTrackView.StreamTrackViewListener;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.GroupInfo;
import ru.ok.model.GroupType;
import ru.ok.model.Location;
import ru.ok.model.UserInfo;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.model.stream.banner.Banner;
import ru.ok.model.stream.banner.StatPixelHolder;
import ru.ok.model.stream.banner.VideoData;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedGroupEntity;
import ru.ok.model.stream.entities.FeedPlaceEntity;
import ru.ok.model.stream.entities.FeedUserEntity;
import ru.ok.model.stream.entities.FeedVideoEntity;

public abstract class AbsStreamItemListenersViewController extends AbsStreamItemStateHolderViewController implements StreamTrackViewListener {
    private OnClickListener bannerClickListener;
    public final OnCommentsClickListener commentsClickListener;
    private FeedHeaderViewListener feedHeaderViewListener;
    protected final FeedOptionsPopupListener feedOptionsPopupListener;
    private FeedHeaderViewListener feedReshareHeaderViewListener;
    private OnClickListener generalUsersClickListener;
    private OnClickListener groupMembersClickListener;
    private OnClickListener joinGroupClickListener;
    private final OnLikeListener likeClickListener;
    private OnClickListener linkClickListener;
    private OnClickListener makePresentClickListener;
    private OnClickListener navigateInternalListener;
    private final OnClickListener optionsClickListener;
    protected FeedOptionsPopupWindow optionsWindow;
    private OnImageSetListener photoActionsVisibilityListener;
    private OnClickListener photoClickListener;
    private OnClickListener placesClickListener;
    private OnClickListener pollAnswerClickListener;
    private OnClickListener presentClickListener;
    private OnClickListener showMoreClickListener;
    private EntityClickListener spanClickListener;
    private OnClickListener textEditClickListener;
    private OnSelectOdklLinkListener textViewLinkListener;
    private OnClickListener userClickListener;
    private OnClickListener userNamesClickListener;
    private OnClickListener videoClickListener;

    /* renamed from: ru.ok.android.ui.stream.list.controller.AbsStreamItemListenersViewController.1 */
    class C12421 implements FeedOptionsPopupListener {
        C12421() {
        }

        public void onMarkAsSpamClicked(int position, Feed feed, int itemAdapterPosition) {
            AbsStreamItemListenersViewController.this.optionsWindow.dismiss();
            AbsStreamItemListenersViewController.this.getStreamAdapterListener().onMarkAsSpamClicked(position, feed, itemAdapterPosition);
        }

        public void onDeleteClicked(int position, Feed feed, int itemAdapterPosition) {
            AbsStreamItemListenersViewController.this.optionsWindow.dismiss();
            AbsStreamItemListenersViewController.this.getStreamAdapterListener().onDeleteClicked(position, feed, itemAdapterPosition);
        }
    }

    /* renamed from: ru.ok.android.ui.stream.list.controller.AbsStreamItemListenersViewController.2 */
    class C12432 implements OnClickListener {
        C12432() {
        }

        public void onClick(View v) {
            AbsFeedPhotoEntity photo = (AbsFeedPhotoEntity) v.getTag(2131624320);
            MediaItemPhoto mediaItem = (MediaItemPhoto) v.getTag(2131624330);
            Boolean isAnimatePhoto = (Boolean) v.getTag(2131624324);
            PhotoInfoPage photoInfoPage = (PhotoInfoPage) v.getTag(2131624332);
            Logger.m173d("%s - %s", (FeedWithState) v.getTag(2131624322), photo);
            if (photo != null) {
                View view = null;
                if (isAnimatePhoto == null || isAnimatePhoto.booleanValue()) {
                    if (v instanceof ViewGroup) {
                        ViewGroup group = (ViewGroup) v;
                        for (int i = 0; i < group.getChildCount(); i++) {
                            View child = group.getChildAt(i);
                            if ((child instanceof ImageView) || (child instanceof AsyncDraweeView)) {
                                view = child;
                                break;
                            }
                        }
                    } else {
                        view = v;
                    }
                }
                PhotosFeedAdapter.clickToPhoto(AbsStreamItemListenersViewController.this.getActivity(), photoInfoPage, photo, feed, mediaItem, view);
            }
            AbsStreamItemListenersViewController.this.sendClickPixels(v, 2);
        }
    }

    /* renamed from: ru.ok.android.ui.stream.list.controller.AbsStreamItemListenersViewController.3 */
    class C12443 implements OnLikeListener {
        C12443() {
        }

        public void onLikeClicked(FeedFooterView feedFooterView, FeedFooterInfo info, LikeInfoContext likeInfo) {
            FeedWithState feed = info.feed;
            if (feed != null) {
                long feedId = feed.feed.getId();
                Logger.m173d("Feed like clicked: %d", Long.valueOf(feedId));
                if (likeInfo == null) {
                    Logger.m172d("No like info found :|");
                    return;
                }
                AbsStreamItemListenersViewController.this.getStreamAdapterListener().onLikeClicked(feed.position, feed.feed, likeInfo);
                if (!likeInfo.self) {
                    ArrayList<String> likePixels = feed.feed.getStatPixels(4);
                    StreamBannerStatisticsHandler handler = AbsStreamItemListenersViewController.this.getStreamBannerStatisticsHandler();
                    if (handler != null && likePixels != null) {
                        handler.onClick(likePixels);
                    }
                }
            }
        }

        public void onLikeCountClicked(FeedFooterView feedFooterView, FeedFooterInfo info) {
            NavigationHelper.showDiscussionLikes(AbsStreamItemListenersViewController.this.getActivity(), info.discussionSummary.discussion);
            StreamStats.clickLikeCount(info.feed.position, info.feed.feed, info.klassInfo);
        }
    }

    /* renamed from: ru.ok.android.ui.stream.list.controller.AbsStreamItemListenersViewController.4 */
    class C12454 implements OnCommentsClickListener {
        C12454() {
        }

        public void onCommentsClicked(FeedFooterView feedFooterView, FeedFooterInfo info) {
            FeedWithState feed = info.feed;
            Logger.m173d("Feed comments clicked: %d", Long.valueOf(feed.feed.getId()));
            if (info.discussionSummary == null) {
                Logger.m184w("No discussion info");
                return;
            }
            AbsStreamItemListenersViewController.this.getStreamAdapterListener().onCommentClicked(feed.position, feed.feed, info.discussionSummary);
            ArrayList<String> commentPixels = feed.feed.getStatPixels(8);
            StreamBannerStatisticsHandler handler = AbsStreamItemListenersViewController.this.getStreamBannerStatisticsHandler();
            if (handler != null && commentPixels != null) {
                handler.onClick(commentPixels);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.stream.list.controller.AbsStreamItemListenersViewController.5 */
    class C12465 implements OnClickListener {
        C12465() {
        }

        public void onClick(View v) {
            if (!AbsStreamItemListenersViewController.this.getActivity().isFinishing()) {
                String linkUrl = (String) v.getTag(2131624327);
                if (!TextUtils.isEmpty(linkUrl)) {
                    BannerLinksUtils.navigateExternalUrl(AbsStreamItemListenersViewController.this.getActivity(), linkUrl);
                }
                FeedWithState feed = (FeedWithState) v.getTag(2131624322);
                if (feed != null) {
                    StreamStats.clickLink(feed.position, feed.feed, linkUrl);
                }
                ArrayList<String> linkPixels = feed.feed.getStatPixels(5);
                StreamBannerStatisticsHandler statHandler = AbsStreamItemListenersViewController.this.getStreamBannerStatisticsHandler();
                if (linkPixels != null && statHandler != null) {
                    statHandler.onClick(linkPixels);
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.stream.list.controller.AbsStreamItemListenersViewController.6 */
    class C12476 implements OnSelectOdklLinkListener {
        C12476() {
        }

        public void onSelectOdklLink(String url) {
            AbsStreamItemListenersViewController.this.getWebLinksProcessor().processUrl(url);
        }
    }

    /* renamed from: ru.ok.android.ui.stream.list.controller.AbsStreamItemListenersViewController.7 */
    class C12487 implements OnClickListener {
        C12487() {
        }

        public void onClick(View v) {
            FeedGroupEntity group = (FeedGroupEntity) v.getTag(2131624777);
            if (group != null) {
                GroupInfo groupInfo = group.getGroupInfo();
                GroupType type = groupInfo == null ? null : groupInfo.getType();
                if (type == GroupType.CUSTOM || type == GroupType.HAPPENING) {
                    NavigationHelper.showGroupInfo(AbsStreamItemListenersViewController.this.getActivity(), group.getId());
                } else {
                    NavigationHelper.showGroupInfoWeb(AbsStreamItemListenersViewController.this.getActivity(), group.getId());
                }
                String source = (String) v.getTag(2131624343);
                if (source != null) {
                    StreamStats.clickGroup(source);
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.stream.list.controller.AbsStreamItemListenersViewController.8 */
    class C12498 implements OnClickListener {
        C12498() {
        }

        public void onClick(View v) {
            FeedGroupEntity groupEntity = (FeedGroupEntity) v.getTag(2131624777);
            if (groupEntity != null) {
                NavigationHelper.showExternalUrlPage(AbsStreamItemListenersViewController.this.getActivity(), WebUrlCreator.getUrl(GroupSectionItem.MEMBERS.getMethodName(), groupEntity.getGroupInfo().getId(), null), false);
                StreamStats.clickMembers();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.stream.list.controller.AbsStreamItemListenersViewController.9 */
    class C12509 implements OnClickListener {
        C12509() {
        }

        public void onClick(View v) {
            FeedWithState feed = (FeedWithState) v.getTag(2131624322);
            DiscussionSummary discussionSummary = (DiscussionSummary) v.getTag(2131624317);
            Logger.m173d("Show more clicked on disc summary: %s", discussionSummary);
            if (discussionSummary != null) {
                AbsStreamItemListenersViewController.this.getStreamAdapterListener().onMediaTopicClicked(feed.position, feed.feed, discussionSummary);
                StreamStats.clickMore(feed.position, feed.feed);
            }
            AbsStreamItemListenersViewController.this.sendClickPixels(v, 2);
        }
    }

    public AbsStreamItemListenersViewController(Activity activity, StreamAdapterListener listener, String logContext) {
        super(activity, listener, logContext);
        this.feedOptionsPopupListener = new C12421();
        this.likeClickListener = new C12443();
        this.commentsClickListener = new C12454();
        this.optionsClickListener = new OnClickListener() {
            public void onClick(View optionsView) {
                if (AbsStreamItemListenersViewController.this.optionsWindow == null) {
                    AbsStreamItemListenersViewController.this.optionsWindow = new FeedOptionsPopupWindow(AbsStreamItemListenersViewController.this.getActivity(), AbsStreamItemListenersViewController.this.feedOptionsPopupListener);
                } else if (AbsStreamItemListenersViewController.this.optionsWindow.isShowing()) {
                    AbsStreamItemListenersViewController.this.optionsWindow.dismiss();
                    return;
                }
                FeedWithState feed = (FeedWithState) optionsView.getTag(2131624322);
                Logger.m173d("onOptionsClicked: feed=%s itemAdapterPosition=%d", feed, Integer.valueOf(((Integer) optionsView.getTag(2131624311)).intValue()));
                AbsStreamItemListenersViewController.this.optionsWindow.setFeed(feed.position, feed.feed, itemAdapterPosition);
                AbsStreamItemListenersViewController.this.optionsWindow.show(optionsView);
                StreamStats.clickFeedOptions(feed.position, feed.feed);
            }
        };
    }

    public FeedHeaderViewListener getFeedHeaderViewListener() {
        return this.feedHeaderViewListener;
    }

    public FeedHeaderViewListener getFeedReshareHeaderViewListener() {
        return this.feedReshareHeaderViewListener;
    }

    public void setFeedHeaderViewListener(FeedHeaderViewListener feedHeaderViewListener) {
        this.feedHeaderViewListener = feedHeaderViewListener;
    }

    public void setFeedReshareHeaderViewListener(FeedHeaderViewListener feedReshareHeaderViewListener) {
        this.feedReshareHeaderViewListener = feedReshareHeaderViewListener;
    }

    public OnClickListener getPhotoClickListener() {
        if (this.photoClickListener == null) {
            this.photoClickListener = new C12432();
        }
        return this.photoClickListener;
    }

    public OnLikeListener getLikeClickListener() {
        return this.likeClickListener;
    }

    public OnCommentsClickListener getCommentsClickListener() {
        return this.commentsClickListener;
    }

    public OnClickListener getLinkClickListener() {
        if (this.linkClickListener == null) {
            this.linkClickListener = new C12465();
        }
        return this.linkClickListener;
    }

    public OnSelectOdklLinkListener getTextViewLinkListener() {
        if (this.textViewLinkListener == null) {
            this.textViewLinkListener = new C12476();
        }
        return this.textViewLinkListener;
    }

    public OnClickListener getJoinGroupClickListener() {
        if (this.joinGroupClickListener == null) {
            this.joinGroupClickListener = new C12487();
        }
        return this.joinGroupClickListener;
    }

    public OnClickListener getGroupMembersClickListener() {
        if (this.groupMembersClickListener == null) {
            this.groupMembersClickListener = new C12498();
        }
        return this.groupMembersClickListener;
    }

    public OnClickListener getShowMoreClickListener() {
        if (this.showMoreClickListener == null) {
            this.showMoreClickListener = new C12509();
        }
        return this.showMoreClickListener;
    }

    public OnClickListener getUserClickListener() {
        if (this.userClickListener == null) {
            this.userClickListener = new OnClickListener() {
                public void onClick(View v) {
                    GeneralUserInfo info = (GeneralUserInfo) v.getTag(2131624354);
                    if (info.getObjectType() == 0) {
                        NavigationHelper.showUserInfo(AbsStreamItemListenersViewController.this.getActivity(), info.getId());
                        String source = (String) v.getTag(2131624343);
                        if (source != null) {
                            StreamStats.clickUser(source);
                        }
                    }
                }
            };
        }
        return this.userClickListener;
    }

    public OnClickListener getTextEditClickListener() {
        if (this.textEditClickListener == null) {
            this.textEditClickListener = new OnClickListener() {
                public void onClick(View v) {
                    String topicId = (String) v.getTag(2131624351);
                    Integer blockIndex = (Integer) v.getTag(2131623943);
                    String text = (String) v.getTag(C0263R.id.text);
                    if (AbsStreamItemListenersViewController.this.getStreamAdapterListener() != null && topicId != null && blockIndex != null) {
                        AbsStreamItemListenersViewController.this.getStreamAdapterListener().onMediaTopicTextEditClick(topicId, blockIndex.intValue(), text);
                    }
                }
            };
        }
        return this.textEditClickListener;
    }

    public OnClickListener getPresentClickListener() {
        if (this.presentClickListener == null) {
            this.presentClickListener = new OnClickListener() {
                public void onClick(View v) {
                    AbsStreamItemListenersViewController.this.processPresentClick(v, false);
                }
            };
        }
        return this.presentClickListener;
    }

    public OnClickListener getMakePresentClickListener() {
        if (this.makePresentClickListener == null) {
            this.makePresentClickListener = new OnClickListener() {
                public void onClick(View v) {
                    AbsStreamItemListenersViewController.this.processPresentClick(v, true);
                }
            };
        }
        return this.makePresentClickListener;
    }

    private void processPresentClick(View v, boolean makePresent) {
        PresentInfo presentInfo = (PresentInfo) v.getTag(2131624336);
        if (presentInfo == null) {
            Logger.m184w("PresentInfo object not found by tag R.id.tag_present_info");
            return;
        }
        FeedWithState feed = (FeedWithState) v.getTag(2131624322);
        if (presentInfo.presentType != null) {
            String holidayId;
            UserInfo user = (UserInfo) v.getTag(2131624354);
            String userId = user == null ? null : user.getId();
            if (presentInfo.holiday != null) {
                holidayId = presentInfo.holiday.id;
            } else {
                holidayId = null;
            }
            String presentId = presentInfo.presentType.getId();
            Logger.m173d("Make present clicked: %s, holiday: %s, user: %s, suppress present id: %s", presentId, holidayId, userId, Boolean.valueOf(makePresent));
            if (makePresent) {
                SendPresentShortLinkBuilder openPresents = SendPresentShortLinkBuilder.openPresents();
                if (presentInfo.isBadge) {
                    userId = OdnoklassnikiApplication.getCurrentUser().getId();
                }
                openPresents = openPresents.setUser(userId);
                if (!presentInfo.isBadge) {
                    presentId = null;
                }
                SendPresentShortLinkBuilder builder = openPresents.setPresent(presentId).setHoliday(holidayId).setOrigin("F");
                if (presentInfo.presentType != null && presentInfo.presentType.isLive()) {
                    builder.setSection("Live");
                }
                NavigationHelper.makePresent(getActivity(), builder);
            } else {
                NavigationHelper.showExternalUrlPage(getActivity(), StreamItemAdapter.buildMakePresentRequest(userId, presentId, holidayId), false, Type.friend_presents);
            }
            if (makePresent) {
                StreamStats.clickMakePresent(feed.position, feed.feed, presentInfo);
            } else {
                StreamStats.clickPresent(feed.position, feed.feed, presentInfo);
            }
        } else if (presentInfo.achievementType != null) {
            Logger.m172d("achievement clicked");
            StreamStats.clickAchievement(feed.position, feed.feed, presentInfo.achievementType.getId());
        }
    }

    public OnClickListener getPollAnswerClickListener() {
        if (this.pollAnswerClickListener == null) {
            this.pollAnswerClickListener = new OnClickListener() {
                public void onClick(View v) {
                    Activity activity = AbsStreamItemListenersViewController.this.getActivity();
                    if (activity != null) {
                        String currentUserId = OdnoklassnikiApplication.getCurrentUser().getId();
                        if (!TextUtils.isEmpty(currentUserId)) {
                            StreamPollAnswerItem pollAnswerItem = (StreamPollAnswerItem) v.getTag(2131624335);
                            if (pollAnswerItem != null) {
                                FeedWithState feedWithState = pollAnswerItem.feedWithState;
                                pollAnswerItem.setAnswer(Storages.getInstance(activity, currentUserId).getMtPollsManager().toggle(pollAnswerItem.poll, pollAnswerItem.getAnswer(), AbsStreamItemListenersViewController.this.getLogContext()));
                                ViewHolder viewHolder = (ViewHolder) v.getTag(2131624349);
                                if (viewHolder != null) {
                                    pollAnswerItem.bindAnswerView(viewHolder);
                                }
                                StreamStats.clickVotePoll(feedWithState.position, feedWithState.feed, pollAnswerItem.getId());
                                ArrayList<String> votePixels = feedWithState.feed.getStatPixels(7);
                                StreamBannerStatisticsHandler statHandler = AbsStreamItemListenersViewController.this.getStreamBannerStatisticsHandler();
                                if (votePixels != null && statHandler != null) {
                                    statHandler.onClick(votePixels);
                                }
                            }
                        }
                    }
                }
            };
        }
        return this.pollAnswerClickListener;
    }

    public OnClickListener getUserNamesClickListener() {
        if (this.userNamesClickListener == null) {
            this.userNamesClickListener = new OnClickListener() {
                public void onClick(View v) {
                    FeedWithState feed = (FeedWithState) v.getTag(2131624322);
                    ArrayList<UserInfo> users = (ArrayList) v.getTag(2131624346);
                    if (users != null && AbsStreamItemListenersViewController.this.getStreamAdapterListener() != null) {
                        AbsStreamItemListenersViewController.this.getStreamAdapterListener().onUsersSelected(feed.position, feed.feed, users);
                    }
                }
            };
        }
        return this.userNamesClickListener;
    }

    public OnClickListener getBannerClickListener() {
        if (this.bannerClickListener == null) {
            this.bannerClickListener = new OnClickListener() {
                public void onClick(View v) {
                    if (!AbsStreamItemListenersViewController.this.getActivity().isFinishing()) {
                        String promoGroupId = (String) v.getTag(2131624338);
                        String promoUserId = (String) v.getTag(2131624339);
                        if (!TextUtils.isEmpty(promoGroupId)) {
                            NavigationHelper.showGroupInfo(AbsStreamItemListenersViewController.this.getActivity(), promoGroupId, "action_join");
                            AbsStreamItemListenersViewController.this.sendClickPixels(v, 9);
                        } else if (TextUtils.isEmpty(promoUserId)) {
                            int pixelType = BannerLinksUtils.processBannerClick((Banner) v.getTag(2131624313), AbsStreamItemListenersViewController.this.getActivity(), AbsStreamItemListenersViewController.this.getWebLinksProcessor());
                            if (pixelType >= 0) {
                                AbsStreamItemListenersViewController.this.sendClickPixels(v, pixelType);
                            }
                        } else {
                            NavigationHelper.showUserInfo(AbsStreamItemListenersViewController.this.getActivity(), promoUserId, "action_friendship");
                            AbsStreamItemListenersViewController.this.sendClickPixels(v, 9);
                        }
                        StreamStats.clickBanner();
                    }
                }
            };
        }
        return this.bannerClickListener;
    }

    public OnClickListener getNavigateInternalListener() {
        if (this.navigateInternalListener == null) {
            this.navigateInternalListener = new OnClickListener() {
                public void onClick(View v) {
                    String url = (String) v.getTag(2131624327);
                    if (!TextUtils.isEmpty(url)) {
                        BannerLinksUtils.navigateInternal(AbsStreamItemListenersViewController.this.getExternalWebLinksProcessor(), url);
                    }
                }
            };
        }
        return this.navigateInternalListener;
    }

    public OnClickListener getPlacesClickListener() {
        if (this.placesClickListener == null) {
            this.placesClickListener = new OnClickListener() {
                public void onClick(View v) {
                    List<FeedPlaceEntity> places = (List) v.getTag(2131624328);
                    if (!(places == null || places.isEmpty())) {
                        FeedPlaceEntity place = (FeedPlaceEntity) places.get(0);
                        NavigationHelper.showAddressLocation(AbsStreamItemListenersViewController.this.getActivity(), new Location(Double.valueOf(place.getLatitude()), Double.valueOf(place.getLongitude())), null, place.getName());
                    }
                    StreamStats.clickPlace();
                }
            };
        }
        return this.placesClickListener;
    }

    public EntityClickListener getSpanClickListener() {
        if (this.spanClickListener == null) {
            this.spanClickListener = new EntityClickListener() {
                public void onClick(int position, Feed feed, BaseEntity entity, View view) {
                    Logger.m173d("entity=%s", entity);
                    if (AbsStreamItemListenersViewController.this.getStreamAdapterListener() != null) {
                        GeneralUserInfo info = null;
                        if (entity instanceof FeedUserEntity) {
                            info = ((FeedUserEntity) entity).getUserInfo();
                        } else if (entity instanceof FeedGroupEntity) {
                            info = ((FeedGroupEntity) entity).getGroupInfo();
                        }
                        if (info != null) {
                            ArrayList<GeneralUserInfo> infos = new ArrayList();
                            infos.add(info);
                            AbsStreamItemListenersViewController.this.getStreamAdapterListener().onGeneralUsersInfosClicked(position, feed, infos, "inline_link");
                        }
                    }
                }
            };
        }
        return this.spanClickListener;
    }

    public OnClickListener getVideoClickListener() {
        if (this.videoClickListener == null) {
            this.videoClickListener = new OnClickListener() {
                public void onClick(View view) {
                    String str = null;
                    if (!AbsStreamItemListenersViewController.this.getActivity().isFinishing()) {
                        FeedVideoEntity video = (FeedVideoEntity) view.getTag(2131624321);
                        String videoUrl = (String) view.getTag(2131624348);
                        VideoData videoData = (VideoData) view.getTag(2131624347);
                        if (video != null) {
                            Logger.m173d("Opening MP4 video: id = %s", video.id);
                            NavigationHelper.showVideo(AbsStreamItemListenersViewController.this.getActivity(), video.id, null, videoData);
                        } else if (videoUrl != null) {
                            Logger.m173d("Opening MP4 video url: %s", videoUrl);
                            NavigationHelper.showVideo(AbsStreamItemListenersViewController.this.getActivity(), null, videoUrl, videoData);
                        }
                        FeedWithState feed = (FeedWithState) view.getTag(2131624322);
                        int i = feed.position;
                        Feed feed2 = feed.feed;
                        if (video != null) {
                            str = video.getId();
                        }
                        StreamStats.clickVideo(i, feed2, str);
                        AbsStreamItemListenersViewController.this.sendVideoPlayPixels(feed.feed);
                    }
                }
            };
        }
        return this.videoClickListener;
    }

    public OnClickListener getGeneralUsersClickListener() {
        if (this.generalUsersClickListener == null) {
            this.generalUsersClickListener = new OnClickListener() {
                public void onClick(View v) {
                    FeedWithState feed = (FeedWithState) v.getTag(2131624322);
                    ArrayList<GeneralUserInfo> userInfos = (ArrayList) v.getTag(2131624346);
                    String source = (String) v.getTag(2131624343);
                    if (userInfos != null) {
                        Logger.m173d("General users clicked: %s", userInfos);
                        AbsStreamItemListenersViewController.this.getStreamAdapterListener().onGeneralUsersInfosClicked(feed.position, feed.feed, userInfos, source);
                    }
                }
            };
        }
        return this.generalUsersClickListener;
    }

    public OnImageSetListener getPhotoActionsVisibilityListener() {
        if (this.photoActionsVisibilityListener == null) {
            this.photoActionsVisibilityListener = new OnImageSetListener() {
                public void onFinishedSetImage(View view, boolean imageIsShown) {
                    FeedFooterView feedFooterView = (FeedFooterView) view.getTag(2131624319);
                    if (feedFooterView != null) {
                        feedFooterView.setVisibility(imageIsShown ? 0 : 4);
                    }
                }

                public void onJustSetImage(AsyncDraweeView view) {
                }
            };
        }
        return this.photoActionsVisibilityListener;
    }

    public void closeOptions() {
        if (this.optionsWindow != null) {
            this.optionsWindow.dismiss();
        }
    }

    public void onLayoutChanged() {
        if (this.optionsWindow != null) {
            this.optionsWindow.dismiss();
        }
    }

    public OnClickListener getOptionsClickListener() {
        return this.optionsClickListener;
    }

    public boolean isOptionsButtonVisible(Feed feed) {
        return FeedOptionsPopupWindow.isOptionsButtonVisible(feed);
    }

    private void sendClickPixels(View view, int pixelType) {
        StatPixelHolder pixels = (StatPixelHolder) view.getTag(2131624342);
        if (pixels != null) {
            ArrayList<String> pixelUrls = pixels.getStatPixels(pixelType);
            StreamBannerStatisticsHandler handler = getStreamBannerStatisticsHandler();
            if (pixelUrls != null && handler != null) {
                handler.onClick(pixelUrls);
            }
        }
    }

    @Nullable
    public StreamTrackViewListener getStreamTrackViewListener() {
        return this;
    }

    public void onPlayStreamTrack(StreamTrackView view, long trackId) {
        reportStreamTrackClick(view, trackId, true);
    }

    public void onPauseStreamTrack(StreamTrackView view, long trackId) {
        reportStreamTrackClick(view, trackId, false);
    }

    private void reportStreamTrackClick(StreamTrackView view, long trackId, boolean play) {
        if (play) {
            sendClickPixels(view, 6);
        }
        String streamStatSource = (String) view.getTag(2131624343);
        FeedWithState feed = (FeedWithState) view.getTag(2131624322);
        if (streamStatSource != null && feed != null) {
            StreamStats.clickPlayMusic(feed.position, feed.feed, streamStatSource, play ? "play" : "pause", trackId);
        }
    }

    private void sendVideoPlayPixels(Feed feed) {
        StreamBannerStatisticsHandler statHandler = getStreamBannerStatisticsHandler();
        if (statHandler != null) {
            ArrayList<String> playVideoPixels = feed.getStatPixels(10);
            if (playVideoPixels != null) {
                statHandler.onClick(playVideoPixels);
            }
            AudioManager audioManager = (AudioManager) getActivity().getSystemService("audio");
            boolean isVolumeOn = audioManager != null && audioManager.getStreamVolume(3) > 0;
            ArrayList<String> volumePixels = feed.getStatPixels(isVolumeOn ? 24 : 25);
            if (volumePixels != null) {
                statHandler.onClick(volumePixels);
            }
        }
    }
}
