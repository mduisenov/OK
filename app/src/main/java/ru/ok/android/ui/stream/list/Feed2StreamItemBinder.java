package ru.ok.android.ui.stream.list;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.TextAppearanceSpan;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.C0206R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.fragments.web.hooks.ShortLinkException;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.settings.PhotoCollageSettings;
import ru.ok.android.spannable.BorderSpan;
import ru.ok.android.statistics.stream.LinkTemplateStats;
import ru.ok.android.ui.stream.LineSpacingSpan;
import ru.ok.android.ui.stream.data.BannerType;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamLinkItem.SimpleTemplateChooser;
import ru.ok.android.ui.stream.view.FeedFooterInfo;
import ru.ok.android.ui.stream.view.FeedHeaderInfo;
import ru.ok.android.ui.stream.view.FeedMediaTopicStyle;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.mediatopics.MediaItem;
import ru.ok.model.mediatopics.MediaItemApp;
import ru.ok.model.mediatopics.MediaItemLink;
import ru.ok.model.mediatopics.MediaItemMusic;
import ru.ok.model.mediatopics.MediaItemPhoto;
import ru.ok.model.mediatopics.MediaItemPoll;
import ru.ok.model.mediatopics.MediaItemStub;
import ru.ok.model.mediatopics.MediaItemText;
import ru.ok.model.mediatopics.MediaItemTopic;
import ru.ok.model.mediatopics.MediaItemType;
import ru.ok.model.mediatopics.MediaItemVideo;
import ru.ok.model.mediatopics.MediaReshareItem;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.photo.PhotoInfo.PhotoContext;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.stream.ActionCountInfo;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.FeedUtils;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.model.stream.banner.Banner;
import ru.ok.model.stream.banner.BannerWithRating;
import ru.ok.model.stream.banner.VideoBanner;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedAchievementEntity;
import ru.ok.model.stream.entities.FeedAchievementTypeEntity;
import ru.ok.model.stream.entities.FeedBannerEntity;
import ru.ok.model.stream.entities.FeedGroupEntity;
import ru.ok.model.stream.entities.FeedMediaTopicEntity;
import ru.ok.model.stream.entities.FeedMusicAlbumEntity;
import ru.ok.model.stream.entities.FeedMusicArtistEntity;
import ru.ok.model.stream.entities.FeedMusicTrackEntity;
import ru.ok.model.stream.entities.FeedPlaceEntity;
import ru.ok.model.stream.entities.FeedPlayListEntity;
import ru.ok.model.stream.entities.FeedPollEntity;
import ru.ok.model.stream.entities.FeedPollEntity.Answer;
import ru.ok.model.stream.entities.FeedPresentEntity;
import ru.ok.model.stream.entities.FeedPresentTypeEntity;
import ru.ok.model.stream.entities.FeedUserEntity;
import ru.ok.model.stream.entities.FeedVideoEntity;
import ru.ok.model.stream.entities.IPresentEntity;
import ru.ok.model.stream.entities.TimestampedEntity;
import ru.ok.model.stream.message.FeedActorSpan;
import ru.ok.model.stream.message.FeedEntitySpan;
import ru.ok.model.stream.message.FeedMessage;
import ru.ok.model.stream.message.FeedMessageBuilder;
import ru.ok.model.stream.message.FeedMessageSpan;
import ru.ok.model.stream.message.FeedTargetSpan;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.Track;

public class Feed2StreamItemBinder {
    private final int achievementSize;
    private final Context context;
    private final int defaultVSpacing;
    private final int dividerBottomVSpacing;
    private final EntitySpanStyle entitySpanStyle;
    private final int fontSizeSmall;
    private final int fontSizeTiny;
    private final int largeVSpacing;
    private final FeedMediaTopicStyle mediaTopicStyle;
    private final int normalVSpacing;
    private final int pagerBottomItemSize;
    private final FeedDisplayParams params;
    private final int presentBigSize;
    private final int presentNormalSize;
    private final int singleTrackBottomItemSize;
    private final int smallVSpacing;
    private final int tinyVSpacing;
    private final int usersRowHeight;
    private final int xlargeVSpacing;

    /* renamed from: ru.ok.android.ui.stream.list.Feed2StreamItemBinder.1 */
    static /* synthetic */ class C12331 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$mediatopics$MediaItemType;

        static {
            $SwitchMap$ru$ok$model$mediatopics$MediaItemType = new int[MediaItemType.values().length];
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.PHOTO_BLOCK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.PHOTO.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.LINK.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.MUSIC.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.POLL.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.TEXT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.VIDEO.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.TOPIC.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.APP.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.STUB.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    public Feed2StreamItemBinder(Context context, FeedDisplayParams params, FeedMediaTopicStyle mediaTopicStyle) {
        this.context = context;
        this.mediaTopicStyle = mediaTopicStyle;
        Resources res = context.getResources();
        this.dividerBottomVSpacing = res.getDimensionPixelOffset(2131230999);
        this.smallVSpacing = res.getDimensionPixelOffset(2131231002);
        this.tinyVSpacing = res.getDimensionPixelOffset(2131231003);
        this.largeVSpacing = res.getDimensionPixelOffset(2131231000);
        this.xlargeVSpacing = res.getDimensionPixelOffset(2131231004);
        this.normalVSpacing = res.getDimensionPixelOffset(2131231001);
        this.fontSizeSmall = res.getDimensionPixelSize(2131230973);
        this.fontSizeTiny = res.getDimensionPixelSize(2131230974);
        this.pagerBottomItemSize = res.getDimensionPixelOffset(2131230985);
        this.presentNormalSize = res.getDimensionPixelSize(2131231138);
        this.presentBigSize = res.getDimensionPixelSize(2131231137);
        this.achievementSize = res.getDimensionPixelSize(2131230862);
        this.singleTrackBottomItemSize = res.getDimensionPixelSize(2131230996);
        this.usersRowHeight = res.getDimensionPixelOffset(2131231006);
        this.defaultVSpacing = this.largeVSpacing;
        this.entitySpanStyle = new EntitySpanStyle(res.getColor(2131493201), false, true);
        if (params == null) {
            params = new FeedDisplayParams();
        }
        this.params = params;
    }

    public Feed2StreamItemBinder(Context context, FeedDisplayParams params) {
        this(context, params, new FeedMediaTopicStyle(context, null, 0, 2131296527));
    }

    public void feeds2items(List<FeedWithState> feedWithStates, ArrayList<StreamItem> outItems) {
        long startTime = System.currentTimeMillis();
        int initialSize = outItems.size();
        for (FeedWithState feedWithState : feedWithStates) {
            bind(feedWithState, outItems);
        }
        Logger.m173d("converted %d feeds to %d items in %d ms", Integer.valueOf(feedWithStates.size()), Integer.valueOf(outItems.size() - initialSize), Long.valueOf(System.currentTimeMillis() - startTime));
    }

    public void bind(FeedWithState feedWithState, List<StreamItem> outItems) {
        int orderInFeed;
        int firstItemPos = outItems.size();
        Feed feed = feedWithState.feed;
        AtomicReference<FeedFooterInfo> outFooterInfo = new AtomicReference(null);
        AtomicBoolean outNoFeedFooter = new AtomicBoolean(false);
        switch (feed.getPattern()) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                orderInFeed = 0 + bindFriendship(feedWithState, 0, outItems, outFooterInfo, outNoFeedFooter);
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                orderInFeed = 0 + bindJoin(feedWithState, 0, outItems, outFooterInfo, outNoFeedFooter);
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                orderInFeed = 0 + bindPresent(feedWithState, 0, outItems, outFooterInfo, outNoFeedFooter);
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                orderInFeed = 0 + bindContent(feedWithState, 0, outItems, outFooterInfo, outNoFeedFooter);
                break;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                orderInFeed = 0 + bindBanner(feedWithState, 0, outItems, outFooterInfo, outNoFeedFooter);
                break;
            case Message.TASKID_FIELD_NUMBER /*8*/:
                orderInFeed = 0 + bindBannerDebug(feedWithState, 0, outItems, outFooterInfo, outNoFeedFooter);
                break;
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                orderInFeed = 0 + bindGiftsCampaign(feedWithState, 0, outItems, outFooterInfo, outNoFeedFooter);
                break;
            default:
                orderInFeed = 0 + bindMessage(feedWithState, 0, outItems, outFooterInfo, outNoFeedFooter);
                break;
        }
        if (!outNoFeedFooter.get()) {
            orderInFeed += bindFeedFooter(feedWithState, orderInFeed, (FeedFooterInfo) outFooterInfo.get(), outItems);
        }
        if (orderInFeed > 0 && outItems.size() > 0) {
            StreamItem lastFeedItem = (StreamItem) outItems.get(outItems.size() - 1);
            if (lastFeedItem.bottomEdgeType != 4) {
                if (lastFeedItem.bottomEdgeType == 2) {
                    orderInFeed += addStreamItem(outItems, new StreamCardVSpaceItem(feedWithState), orderInFeed);
                } else {
                    if (lastFeedItem.bottomEdgeType != 1) {
                        ClickAction clickAction = ((lastFeedItem instanceof AbsStreamClickableItem) && lastFeedItem.sharePressedState()) ? ((AbsStreamClickableItem) lastFeedItem).clickAction : null;
                        int vSpaceSize = this.defaultVSpacing - lastFeedItem.getVSpacingBottom(this.context);
                        if (vSpaceSize > 0) {
                            StreamItem streamVSpaceItem = new StreamVSpaceItem(feedWithState, clickAction, vSpaceSize);
                            orderInFeed += addStreamItem(outItems, streamVSpaceItem, orderInFeed);
                            lastFeedItem = streamVSpaceItem;
                        }
                    }
                    orderInFeed += addStreamItem(outItems, new StreamCardBottomItem(feedWithState, lastFeedItem instanceof AbsStreamClickableItem ? ((AbsStreamClickableItem) lastFeedItem).clickAction : null), orderInFeed);
                }
            }
        }
        if (orderInFeed > 0 && !feedWithState.shownOnScrollSent && feed.getPattern() != 7 && feed.hasStatPixels(1)) {
            ((StreamItem) outItems.get(firstItemPos)).setSendShowOnScroll(true);
        }
        for (int i = 0; i < orderInFeed; i++) {
            ((StreamItem) outItems.get(firstItemPos + i)).setPositionInFeed(i, orderInFeed);
        }
    }

    public int addStreamItem(List<StreamItem> outItems, StreamItem item, int orderInFeed) {
        boolean needSpace = true;
        int startOrder = orderInFeed;
        ClickAction clickAction;
        int vSpaceSize;
        if (orderInFeed != 0) {
            StreamItem previousItem;
            if (outItems.size() > 0) {
                previousItem = (StreamItem) outItems.get(outItems.size() - 1);
                needSpace = StreamItem.needSpaceBetween(previousItem, item);
            } else {
                previousItem = null;
                if (item.topEdgeType == 1) {
                    needSpace = false;
                }
            }
            if (needSpace) {
                if ((previousItem instanceof AbsStreamClickableItem) && previousItem.feedWithState == item.feedWithState && previousItem.sharePressedState()) {
                    clickAction = ((AbsStreamClickableItem) previousItem).clickAction;
                } else {
                    clickAction = null;
                }
                if (clickAction == null) {
                    if ((item instanceof AbsStreamClickableItem) && item.sharePressedState()) {
                        clickAction = ((AbsStreamClickableItem) item).clickAction;
                    } else {
                        clickAction = null;
                    }
                }
                vSpaceSize = (this.defaultVSpacing - previousItem.getVSpacingBottom(this.context)) - item.getVSpacingTop(this.context);
                if (vSpaceSize > 0) {
                    outItems.add(new StreamVSpaceItem(item.feedWithState, clickAction, vSpaceSize));
                    orderInFeed++;
                }
            }
        } else if (item.topEdgeType == 3) {
            clickAction = item instanceof AbsStreamClickableItem ? ((AbsStreamClickableItem) item).clickAction : null;
            vSpaceSize = this.defaultVSpacing - item.getVSpacingTop(this.context);
            if (vSpaceSize > 0) {
                outItems.add(new StreamVSpaceItem(item.feedWithState, clickAction, vSpaceSize));
                orderInFeed++;
            }
        }
        outItems.add(item);
        return (orderInFeed + 1) - startOrder;
    }

    int addItemWithOptionalDivider(StreamItem item, boolean wantDividerBefore, List<StreamItem> outItems, int orderInFeed) {
        int startOrder = orderInFeed;
        if (wantDividerBefore && item.canHaveLineAbove()) {
            orderInFeed += addStreamItem(outItems, new StreamDividerItem(item.feedWithState, this.dividerBottomVSpacing), orderInFeed);
        }
        return (orderInFeed + addStreamItem(outItems, item, orderInFeed)) - startOrder;
    }

    public int bindFeedFooter(FeedWithState feedWithState, int orderInFeed, FeedFooterInfo info, List<StreamItem> outItems) {
        int startOrder = orderInFeed;
        Feed feed = feedWithState.feed;
        if (info == null) {
            LikeInfoContext klassInfo = feed.getLikeInfo();
            DiscussionSummary discInfo = feed.getDiscussionSummary();
            if (!(klassInfo == null && discInfo == null)) {
                info = new FeedFooterInfo(feedWithState, klassInfo, discInfo, null);
            }
        }
        if (info != null) {
            orderInFeed += addStreamItem(outItems, new StreamFeedFooterItem(feedWithState, info), orderInFeed);
        }
        return orderInFeed - startOrder;
    }

    public int bindBannerDebug(FeedWithState feedWithState, int orderInFeed, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> atomicReference, AtomicBoolean outNoFooter) {
        int startOrder = orderInFeed;
        FeedMessage message = feedWithState.feed.getMessage();
        String text = message == null ? null : message.getText();
        if (text != null) {
            orderInFeed += addStreamItem(outItems, new StreamTextItem(feedWithState, text.replaceAll("\\\\/", "/"), (ClickAction) null), orderInFeed);
            outNoFooter.set(true);
        }
        return orderInFeed - startOrder;
    }

    public int bindGiftsCampaign(FeedWithState feedWithState, int orderInFeed, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> atomicReference, AtomicBoolean outNoFooter) {
        int startOrder = orderInFeed;
        StreamItem headerItem = null;
        StreamItem footerItem = null;
        for (BaseEntity entity : feedWithState.feed.getBanners()) {
            if (entity instanceof FeedBannerEntity) {
                Banner banner = ((FeedBannerEntity) entity).getBanner();
                BannerClickAction clickAction = new BannerClickAction(banner, feedWithState.feed);
                String footerText = LocalizationManager.getString(this.context, 2131165323);
                headerItem = new StreamGiftsCampaignHeaderItem(feedWithState, banner, clickAction);
                footerItem = new StreamBannerCardBottomItem(feedWithState, footerText, clickAction);
                break;
            }
        }
        if (headerItem != null) {
            orderInFeed += addStreamItem(outItems, headerItem, orderInFeed);
        }
        StreamItem presentItem = new StreamManyPresentsItem(feedWithState, this.presentNormalSize, this.presentBigSize, this.achievementSize);
        presentItem.setSendShowOnScroll(true);
        orderInFeed += addStreamItem(outItems, presentItem, orderInFeed);
        if (footerItem != null) {
            orderInFeed += addStreamItem(outItems, footerItem, orderInFeed);
            outNoFooter.set(true);
        }
        return orderInFeed - startOrder;
    }

    public int bindBanner(FeedWithState feedWithState, int orderInFeed, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> atomicReference, AtomicBoolean outNoFooter) {
        int startOrder = orderInFeed;
        for (BaseEntity entity : feedWithState.feed.getBanners()) {
            if (entity instanceof FeedBannerEntity) {
                Banner banner = ((FeedBannerEntity) entity).getBanner();
                if (banner != null) {
                    orderInFeed += bindBanner(feedWithState, orderInFeed, banner, outItems);
                }
            }
        }
        outNoFooter.set(true);
        return orderInFeed - startOrder;
    }

    private int bindBanner(FeedWithState feedWithState, int orderInFeed, Banner banner, List<StreamItem> outItems) {
        BannerType bannerType;
        BannerClickAction clickAction;
        StreamItem item;
        int startOrder = orderInFeed;
        BaseEntity owner = FeedUtils.getPromoOwner(feedWithState.feed);
        if (owner instanceof FeedGroupEntity) {
            bannerType = BannerType.JOIN_GROUP;
            clickAction = new BannerClickPromoGroupAction(banner, feedWithState.feed, owner.getId());
        } else if (owner instanceof FeedUserEntity) {
            bannerType = BannerType.JOIN_USER;
            clickAction = new BannerClickPromoUserAction(banner, feedWithState.feed, owner.getId());
        } else {
            bannerType = BannerType.byBanner(banner);
            clickAction = new BannerClickAction(banner, feedWithState.feed);
        }
        orderInFeed += addStreamItem(outItems, new StreamBannerCardTopItem(LocalizationManager.getString(this.context, bannerType.headerMessageResourceId), feedWithState, clickAction), orderInFeed);
        if (banner.template == 6) {
            orderInFeed += bindBannerContentMediaTopic(feedWithState, orderInFeed, banner, outItems);
        } else {
            orderInFeed += bindBannerContentDefault(feedWithState, orderInFeed, banner, clickAction, outItems);
        }
        String actionText = LocalizationManager.getString(this.context, bannerType.footerMessageResourceId);
        if (banner.actionType == 2 && (banner instanceof BannerWithRating)) {
            BannerWithRating bannerWithRating = (BannerWithRating) banner;
            int votes = bannerWithRating.votes;
            FeedWithState feedWithState2 = feedWithState;
            item = new StreamBannerCardBottomAppItem(feedWithState2, actionText, LocalizationManager.getString(this.context, StringUtils.plural((long) votes, 2131165332, 2131165330, 2131165331), Integer.valueOf(votes)), clickAction, bannerWithRating.rating);
        } else {
            item = new StreamBannerCardBottomItem(feedWithState, actionText, clickAction);
        }
        return (orderInFeed + addStreamItem(outItems, item, orderInFeed)) - startOrder;
    }

    private int bindBannerContentDefault(FeedWithState feedWithState, int orderInFeed, Banner banner, BannerClickAction clickAction, List<StreamItem> outItems) {
        StreamItem item;
        int startOrder = orderInFeed;
        CharSequence headerText = getBannerHeaderText(banner);
        CharSequence text = getBannerText(banner);
        Uri iconImageUri = getBannerIconImageUri(banner);
        StreamItem imageItem = getBannerImageOrVideoItem(banner, feedWithState, clickAction);
        boolean needToSetPixels = feedWithState.feed.hasStatPixels(1);
        if (!(iconImageUri == null && headerText == null)) {
            item = new StreamBannerHeaderItem(feedWithState, iconImageUri, headerText, clickAction);
            orderInFeed += addStreamItem(outItems, item, orderInFeed);
            if (needToSetPixels && imageItem == null) {
                item.setSendShowOnScroll(true);
                needToSetPixels = false;
            }
        }
        if (text != null) {
            item = new StreamBannerTextItem(feedWithState, text, clickAction);
            orderInFeed += addStreamItem(outItems, item, orderInFeed);
            if (needToSetPixels && imageItem == null) {
                item.setSendShowOnScroll(true);
                needToSetPixels = false;
            }
        }
        if (imageItem != null) {
            orderInFeed += addStreamItem(outItems, imageItem, orderInFeed);
            if (needToSetPixels) {
                imageItem.setSendShowOnScroll(true);
            }
        }
        return orderInFeed - startOrder;
    }

    @Nullable
    private StreamItem getBannerImageOrVideoItem(Banner banner, FeedWithState feedWithState, BannerClickAction clickAction) {
        if (banner.template == 5 && (banner instanceof VideoBanner)) {
            return createBannerVideoItem(feedWithState, (VideoBanner) banner);
        }
        return createBannerImageItem(feedWithState, banner, clickAction);
    }

    private CharSequence getBannerHeaderText(Banner banner) {
        CharSequence headerText = TextUtils.isEmpty(banner.header) ? null : Html.fromHtml(banner.header);
        if (headerText == null) {
            return headerText;
        }
        SpannableStringBuilder headerSpannable = new SpannableStringBuilder(headerText);
        headerSpannable.append(' ');
        headerSpannable.setSpan(new TextAppearanceSpan(this.context, 2131296721), 0, headerSpannable.length(), 17);
        if (!TextUtils.isEmpty(banner.ageRestriction)) {
            int textHeight = (int) this.context.getResources().getDimension(2131230881);
            int textColor = ContextCompat.getColor(this.context, 2131492914);
            int start = headerSpannable.length();
            headerSpannable.append(banner.ageRestriction);
            int end = headerSpannable.length();
            headerSpannable.setSpan(new ForegroundColorSpan(textColor), start, end, 33);
            headerSpannable.setSpan(new AbsoluteSizeSpan(textHeight), start, end, 33);
            headerSpannable.setSpan(createBorderSpan(textColor), start, end, 33);
        }
        if (!TextUtils.isEmpty(banner.info)) {
            headerSpannable.append("\n \n");
            headerSpannable.setSpan(new LineSpacingSpan(-this.normalVSpacing), headerSpannable.length() - 2, headerSpannable.length() - 1, 17);
            appendSpan(headerSpannable, banner.info, new TextAppearanceSpan(this.context, 2131296694));
        }
        return headerSpannable;
    }

    private CharSequence getBannerText(Banner banner) {
        CharSequence text = TextUtils.isEmpty(banner.text) ? null : Html.fromHtml(banner.text);
        if (text == null || TextUtils.isEmpty(banner.disclaimer)) {
            return text;
        }
        SpannableStringBuilder sb = appendSpan(text, "\n", null);
        appendSpan(sb, LocalizationManager.getString(this.context, 2131165425, banner.disclaimer), new TextAppearanceSpan(this.context, 2131296692));
        return sb;
    }

    private Uri getBannerIconImageUri(Banner banner) {
        if (banner.iconType != 1 && banner.iconType != 2) {
            return null;
        }
        String iconUrl = banner.iconUrlHd;
        if (TextUtils.isEmpty(iconUrl)) {
            iconUrl = banner.iconUrl;
        }
        if (TextUtils.isEmpty(iconUrl)) {
            return null;
        }
        return Uri.parse(iconUrl);
    }

    private int bindBannerContentMediaTopic(FeedWithState feedWithState, int orderInFeed, Banner banner, List<StreamItem> outItems) {
        int startOrder = orderInFeed;
        int startContentOffset = outItems.size();
        AtomicReference<FeedFooterInfo> outFooterInfo = new AtomicReference();
        AtomicBoolean outNoFooter = new AtomicBoolean();
        orderInFeed += bindContent(feedWithState, orderInFeed, outItems, outFooterInfo, outNoFooter);
        if (feedWithState.feed.hasStatPixels(1) && startContentOffset < outItems.size()) {
            ((StreamItem) outItems.get(startContentOffset)).setSendShowOnScroll(true);
        }
        FeedFooterInfo footerInfo = outNoFooter.get() ? null : (FeedFooterInfo) outFooterInfo.get();
        if (footerInfo != null) {
            orderInFeed += addStreamItem(outItems, new StreamFeedFooterItem(feedWithState, footerInfo, false), orderInFeed);
        }
        return orderInFeed - startOrder;
    }

    @NonNull
    private BorderSpan createBorderSpan(int borderColor) {
        return new BorderSpan(borderColor, DimenUtils.getRealDisplayPixels(1, this.context), DimenUtils.getRealDisplayPixels(2, this.context), DimenUtils.getRealDisplayPixels(2, this.context));
    }

    private StreamItem createBannerImageItem(FeedWithState feedWithState, Banner banner, BannerClickAction clickAction) {
        PhotoSize size = banner.getClosestSize(480);
        String imageUrl = size == null ? null : size.getUrl();
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        }
        float aspectRatio;
        Uri bannerImageUri = Uri.parse(imageUrl);
        if (size.getHeight() <= 0) {
            aspectRatio = (float) size.getWidth();
        } else {
            aspectRatio = ((float) size.getWidth()) / ((float) size.getHeight());
        }
        return new StreamBannerImageItem(feedWithState, bannerImageUri, aspectRatio, clickAction);
    }

    private StreamItem createBannerVideoItem(FeedWithState feedWithState, VideoBanner banner) {
        return new StreamVideoBannerItem(feedWithState, banner.videoData, banner.getPics());
    }

    private static SpannableStringBuilder appendSpan(CharSequence srcText, String spanText, Object span) {
        SpannableStringBuilder sb;
        if (srcText instanceof SpannableStringBuilder) {
            sb = (SpannableStringBuilder) srcText;
        } else {
            sb = new SpannableStringBuilder(srcText);
        }
        int spanStart = sb.length();
        sb.append(spanText);
        if (span != null) {
            sb.setSpan(span, spanStart, sb.length(), 17);
        }
        return sb;
    }

    public int bindFriendAuthorInGroup(FeedWithState feedWithState, int orderInFeed, List<StreamItem> outItems, AbsStreamContentHeaderItem headerItem) {
        int startOrder = orderInFeed;
        Feed feed = feedWithState.feed;
        BaseEntity owner = FeedUtils.findFirstOwner(feed);
        if (owner != null && owner.getType() == 2) {
            BaseEntity author = FeedUtils.findFirstAuthor(feed);
            if (author != null && author.getType() == 7) {
                FeedUserEntity user = (FeedUserEntity) author;
                if (!isUsersContainsOtherUser(headerItem.info.referencedUsers, user) && UsersStorageFacade.isUserFriend(user.getId())) {
                    orderInFeed += addStreamItem(outItems, new StreamSecondaryAuthorItem(feedWithState, user.getUserInfo()), orderInFeed);
                }
            }
        }
        return orderInFeed - startOrder;
    }

    public int bindContent(FeedWithState feedWithState, int orderInFeed, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> outFooterInfo, AtomicBoolean outNoFooter) {
        int startOrder = orderInFeed;
        Feed feed = feedWithState.feed;
        StreamItem headerItem = createHeaderItem(feedWithState, this.params.doAuthorInHeader);
        orderInFeed += addStreamItem(outItems, headerItem, orderInFeed);
        boolean isVoteOrLike = feed.hasDataFlag(4) || feed.hasDataFlag(16);
        if (!isVoteOrLike) {
            orderInFeed += bindFriendAuthorInGroup(feedWithState, orderInFeed, outItems, headerItem);
        }
        if (isVoteOrLike) {
            List<? extends BaseEntity> authors = feed.getOwners();
            if (!authors.isEmpty()) {
                List<? extends BaseEntity> content;
                if (feed.hasDataFlag(16) && feed.getPlaceTypesMask() == 2) {
                    content = feed.getPlaces();
                } else {
                    content = feed.getTargets();
                }
                outItems.add(new StreamDividerItem(feedWithState));
                orderInFeed++;
                orderInFeed += bindLikeAuthors(feedWithState, orderInFeed, outItems, authors, getMostRecentCreationTime(content));
            }
        }
        if (feed.hasDataFlag(2)) {
            return (orderInFeed + bindSetAvatarNew(feedWithState, orderInFeed, outItems, outFooterInfo, outNoFooter)) - startOrder;
        }
        int placesCount;
        int i;
        Iterator i$;
        if (feed.hasDataFlag(16) && feed.getPlaceTypesMask() == 2) {
            List<? extends BaseEntity> places = feed.getPlaces();
            placesCount = places.size();
            i = 0;
            for (BaseEntity entity : places) {
                orderInFeed += bindEntity(feedWithState, orderInFeed, entity, outItems, outFooterInfo, outNoFooter, i == placesCount + -1);
                i++;
            }
            return orderInFeed - startOrder;
        } else if (feed.hasDataFlag(8) && feed.getPlaceTypesMask() == 2) {
            placesCount = feed.getPlaces().size();
            i = 0;
            i$ = feed.getPlaces().iterator();
            while (i$.hasNext()) {
                orderInFeed += bindEntity(feedWithState, orderInFeed, (BaseEntity) i$.next(), outItems, outFooterInfo, outNoFooter, i == placesCount + -1);
                i++;
            }
            return orderInFeed - startOrder;
        } else {
            int targetTypesMask = feed.getTargetTypesMask();
            List<? extends BaseEntity> targets = feed.getTargets();
            StreamCardBottomItem bottomItem;
            if (targetTypesMask == 1) {
                List<? extends BaseEntity> photos = FeedUtils.getPhotos(feed);
                if (!photos.isEmpty()) {
                    if (PhotoCollageSettings.isPhotoCollageEnabled()) {
                        for (BaseEntity photo : photos) {
                            if (photo instanceof AbsFeedPhotoEntity) {
                                ((AbsFeedPhotoEntity) photo).getPhotoInfo().setPhotoContext(PhotoContext.NORMAL);
                            }
                        }
                        orderInFeed += bindPhotoCollage(feedWithState, orderInFeed, photos, false, false, new boolean[1], outItems, outFooterInfo);
                    } else {
                        orderInFeed += bindPhotoLayer(feedWithState, orderInFeed, photos, false, false, outItems, photos.size() == 1);
                        outNoFooter.set(true);
                        if (photos.size() > 1) {
                            bottomItem = new StreamCardBottomItem(feedWithState, null);
                            bottomItem.setHeight(this.pagerBottomItemSize);
                            orderInFeed += addStreamItem(outItems, bottomItem, orderInFeed);
                        }
                    }
                }
            } else if ((targetTypesMask & -2113) == 0) {
                List<FeedMusicTrackEntity> arrayList = new ArrayList(targets.size());
                for (BaseEntity target : targets) {
                    if (target instanceof FeedMusicTrackEntity) {
                        arrayList.add((FeedMusicTrackEntity) target);
                    }
                }
                ArrayList<Track> playlist = musicEntities2Tracks(arrayList, Integer.MAX_VALUE);
                int trackCount = playlist.size();
                int bottomItemHeight = 0;
                if (trackCount == 1) {
                    orderInFeed += bindTracks(feedWithState, orderInFeed, playlist, arrayList, 0, 1, false, true, null, outItems);
                    bottomItemHeight = this.singleTrackBottomItemSize;
                } else if (trackCount > 1) {
                    orderInFeed += addStreamItem(outItems, new StreamMusicPagerItem(feedWithState, playlist, arrayList, null, false), orderInFeed);
                    bottomItemHeight = this.pagerBottomItemSize;
                }
                outNoFooter.set(true);
                if (bottomItemHeight > 0) {
                    bottomItem = new StreamCardBottomItem(feedWithState, null);
                    bottomItem.setHeight(bottomItemHeight);
                    orderInFeed += addStreamItem(outItems, bottomItem, orderInFeed);
                }
            } else if (this.params.doCollapseVideos && (targetTypesMask & 4) == 4) {
                orderInFeed += bindAllVideos(feedWithState, orderInFeed, outItems, outFooterInfo, outNoFooter, targets);
            } else {
                orderInFeed += bindContentTargets(feedWithState, orderInFeed, targets, outItems, outFooterInfo, outNoFooter);
            }
            return orderInFeed - startOrder;
        }
    }

    private int bindPhotoCollage(FeedWithState feedWithState, int orderInFeed, List<? extends BaseEntity> photos, boolean needDividerBefore, boolean isLastBlock, boolean[] outShowMore, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> outFooterInfo) {
        return PhotoCollageStreamItemBinder.addPhotoItemWithCollage(this.context, this, feedWithState, photos, needDividerBefore, orderInFeed, isLastBlock, outShowMore, outItems, outFooterInfo);
    }

    private boolean isUsersContainsOtherUser(ArrayList<GeneralUserInfo> referencedUsers, FeedUserEntity user) {
        Iterator i$ = referencedUsers.iterator();
        while (i$.hasNext()) {
            GeneralUserInfo userInfo = (GeneralUserInfo) i$.next();
            if (TextUtils.equals(userInfo.getId(), user.getId()) && userInfo.getClass() == UserInfo.class) {
                return true;
            }
        }
        return false;
    }

    private long getMostRecentCreationTime(List<? extends BaseEntity> entities) {
        long mostRecentTime = 0;
        if (entities != null) {
            for (BaseEntity entity : entities) {
                long creationTime = getCreationTime(entity);
                if (creationTime > mostRecentTime) {
                    mostRecentTime = creationTime;
                }
            }
        }
        return mostRecentTime;
    }

    private long getCreationTime(BaseEntity entity) {
        if (entity instanceof TimestampedEntity) {
            return ((TimestampedEntity) entity).getCreationTime();
        }
        return 0;
    }

    private int bindAllVideos(FeedWithState feedWithState, int orderInFeed, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> outFooterInfo, AtomicBoolean outNoFooter, List<? extends BaseEntity> targets) {
        List<? extends BaseEntity> owners = feedWithState.feed.getOwners();
        if (owners.size() > 1) {
            return bindContentTargets(feedWithState, orderInFeed, targets, outItems, outFooterInfo, outNoFooter);
        }
        String moreVideoUrl = null;
        int moreVideoTextId = 0;
        if (targets.size() > 1) {
            String ownerId = null;
            boolean ownerIsGroup = false;
            for (BaseEntity entity : owners) {
                if (entity instanceof FeedUserEntity) {
                    UserInfo info = ((FeedUserEntity) entity).getUserInfo();
                    if (info != null) {
                        ownerId = info.getId();
                        moreVideoTextId = 2131166217;
                        break;
                    }
                } else if (entity instanceof FeedGroupEntity) {
                    GroupInfo info2 = ((FeedGroupEntity) entity).getGroupInfo();
                    if (info2 != null) {
                        ownerId = info2.getId();
                        moreVideoTextId = 2131166216;
                        ownerIsGroup = true;
                        break;
                    }
                } else {
                    continue;
                }
            }
            if (TextUtils.isEmpty(ownerId)) {
                return bindContentTargets(feedWithState, orderInFeed, targets, outItems, outFooterInfo, outNoFooter);
            }
            if (ownerIsGroup) {
                try {
                    moreVideoUrl = ShortLinkUtils.createGroupVideoShortLink(ownerId);
                } catch (ShortLinkException e) {
                    return bindContentTargets(feedWithState, orderInFeed, targets, outItems, outFooterInfo, outNoFooter);
                }
            }
            moreVideoUrl = ShortLinkUtils.createUserVideoShortLink(ownerId);
        }
        int startOrder = orderInFeed;
        if (!targets.isEmpty()) {
            BaseEntity firstEntity = (BaseEntity) targets.get(0);
            if (firstEntity instanceof FeedVideoEntity) {
                FeedVideoEntity video = (FeedVideoEntity) firstEntity;
                orderInFeed += bindVideo(feedWithState, orderInFeed, video, false, (List) outItems);
                if (!TextUtils.isEmpty(moreVideoUrl)) {
                    SpannableStringBuilder moreVideosText = new SpannableStringBuilder();
                    moreVideosText.append(LocalizationManager.getString(this.context, moreVideoTextId));
                    moreVideosText.setSpan(new TextAppearanceSpan(this.context, 2131296703), 0, moreVideosText.length(), 17);
                    orderInFeed += addStreamItem(outItems, new StreamNonSelectableTextItem(feedWithState, moreVideosText, new NavigateInternalAction(moreVideoUrl)), orderInFeed);
                }
                LikeInfoContext likeInfo = video.getLikeInfo();
                DiscussionSummary discussionSummary = video.getDiscussionSummary();
                if (!(likeInfo == null && discussionSummary == null)) {
                    outFooterInfo.set(new FeedFooterInfo(feedWithState, likeInfo, discussionSummary, null));
                }
            }
        }
        return orderInFeed - startOrder;
    }

    private int bindLikeAuthors(FeedWithState feedWithState, int orderInFeed, List<StreamItem> outItems, List<? extends BaseEntity> klassAuthors, long contentCreationTime) {
        int startOrder = orderInFeed;
        FeedHeaderInfo info = new FeedHeaderInfo(feedWithState, false);
        bindPromoLabel(info, feedWithState.feed);
        SpannableStringBuilder sb = new SpannableStringBuilder();
        buildAuthorsText(klassAuthors, info.avatars, sb);
        if (!info.avatars.isEmpty()) {
            info.setReferencedUsers(info.avatars);
            info.message = sb;
            if (contentCreationTime != 0) {
                info.setDateFormatted(DateFormatter.getFormatStringFromDate(this.context, contentCreationTime));
            }
            AbsStreamContentHeaderItem klassAuthorsItem = new StreamKlassAuthorItem(feedWithState, info);
            orderInFeed += addStreamItem(outItems, klassAuthorsItem, orderInFeed);
            orderInFeed += bindFriendAuthorInGroup(feedWithState, orderInFeed, outItems, klassAuthorsItem);
        }
        return orderInFeed - startOrder;
    }

    private int bindReshareAuthors(FeedWithState feedWithState, int orderInFeed, List<StreamItem> outItems, List<? extends BaseEntity> reshareAuthors) {
        int startOrder = orderInFeed;
        FeedHeaderInfo info = new FeedHeaderInfo(feedWithState, false);
        SpannableStringBuilder sb = new SpannableStringBuilder();
        bindPromoLabel(info, feedWithState.feed);
        sb.append('\u00a0');
        int imageSpanEnd = sb.length();
        sb.setSpan(new ImageSpan(this.context, 2130838240, 1), imageSpanEnd - 1, imageSpanEnd, 17);
        sb.append("\u00a0\u00a0\u00a0");
        buildAuthorsText(reshareAuthors, info.avatars, sb);
        if (info.avatars.size() > 0) {
            info.setReferencedUsers(info.avatars);
            info.message = sb;
            orderInFeed += addStreamItem(outItems, new StreamReshareAuthorItem(feedWithState, info), orderInFeed);
        }
        return orderInFeed - startOrder;
    }

    private static void buildAuthorsText(List<? extends BaseEntity> authors, ArrayList<GeneralUserInfo> outAvatars, SpannableStringBuilder outText) {
        boolean firstAuthor = true;
        for (BaseEntity entity : authors) {
            GeneralUserInfo userInfo = null;
            if (entity instanceof FeedUserEntity) {
                userInfo = ((FeedUserEntity) entity).getUserInfo();
            } else if (entity instanceof FeedGroupEntity) {
                userInfo = ((FeedGroupEntity) entity).getGroupInfo();
            }
            if (userInfo != null) {
                outAvatars.add(userInfo);
                if (firstAuthor) {
                    firstAuthor = false;
                } else {
                    outText.append(", ");
                }
                int spanStart = outText.length();
                outText.append(userInfo.getName());
                outText.setSpan(new FeedActorSpan(), spanStart, outText.length(), 17);
            }
        }
    }

    public int bindPresent(FeedWithState feedWithState, int orderInFeed, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> atomicReference, AtomicBoolean outNoFooter) {
        int startOrder = orderInFeed;
        Feed feed = feedWithState.feed;
        orderInFeed += addStreamItem(outItems, createHeaderItem(feedWithState, false), orderInFeed);
        if ((feed.getPresentTypesMask() & -57473) != 0) {
            return orderInFeed - startOrder;
        }
        if ((feed.getReceiverTypesMask() & 8) != 8) {
            return orderInFeed - startOrder;
        }
        List<? extends BaseEntity> presents = feed.getPresents();
        List<? extends BaseEntity> receivers = feed.getReceivers();
        BaseEntity entity;
        if ((feed.getPresentTypesMask() & -40961) == 0) {
            ArrayList<FeedAchievementTypeEntity> achvmnts = new ArrayList(presents.size());
            for (BaseEntity entity2 : presents) {
                int type = entity2.getType();
                if (type == 19) {
                    achvmnts.add((FeedAchievementTypeEntity) entity2);
                } else if (type == 22) {
                    FeedAchievementTypeEntity achType = ((FeedAchievementEntity) entity2).getAchievementType();
                    if (!(achType == null || achvmnts.contains(achType))) {
                        achvmnts.add(achType);
                    }
                }
            }
            if (achvmnts.size() > 0) {
                orderInFeed += addStreamItem(outItems, new StreamAchievementsItem(feedWithState, achvmnts, this.achievementSize), orderInFeed);
            }
            return orderInFeed - startOrder;
        } else if (receivers.isEmpty()) {
            return orderInFeed - startOrder;
        } else {
            FeedUserEntity receiver = (FeedUserEntity) receivers.get(0);
            boolean isMakePresentVisible = (receiver == null || TextUtils.equals(OdnoklassnikiApplication.getCurrentUser().getId(), receiver.getId())) ? false : true;
            if (presents.size() == 1) {
                entity2 = (BaseEntity) presents.get(0);
                FeedPresentEntity presentEntity = entity2.getType() == 6 ? (FeedPresentEntity) entity2 : null;
                if (presentEntity != null) {
                    IPresentEntity presentTypeEntity = presentEntity.getPresentType();
                    if (!(presentTypeEntity == null || receiver == null)) {
                        PresentInfo presentInfo = new PresentInfo(presentTypeEntity, feed, presentEntity);
                        if (presentTypeEntity.isLive()) {
                            orderInFeed += addStreamItem(outItems, new StreamCardPresentItem(feedWithState, receiver, presentInfo, !presentInfo.isBadge), orderInFeed);
                        } else {
                            boolean z = (presentTypeEntity instanceof FeedPresentTypeEntity) && isMakePresentVisible;
                            orderInFeed += addStreamItem(outItems, new StreamPresentItem(feedWithState, receiver, presentInfo, z, this.presentNormalSize, this.presentBigSize), orderInFeed);
                        }
                    }
                }
            } else if (presents.size() > 1 && receiver != null) {
                orderInFeed += addStreamItem(outItems, new StreamManyPresentsItem(feedWithState, this.presentNormalSize, this.presentBigSize, this.achievementSize), orderInFeed);
            }
            return orderInFeed - startOrder;
        }
    }

    public int bindJoin(FeedWithState feedWithState, int orderInFeed, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> atomicReference, AtomicBoolean outNoFooter) {
        int startOrder = orderInFeed;
        orderInFeed += addStreamItem(outItems, createHeaderItem(feedWithState, false), orderInFeed);
        StreamJoinGroupItem lastItem = null;
        for (BaseEntity target : feedWithState.feed.getTargets()) {
            if (target instanceof FeedGroupEntity) {
                if (lastItem != null) {
                    lastItem.setPaddingBottom(this.xlargeVSpacing);
                }
                StreamJoinGroupItem joinItem = createJoinGroupItem(feedWithState, (FeedGroupEntity) target);
                outItems.add(joinItem);
                orderInFeed++;
                lastItem = joinItem;
            }
        }
        outNoFooter.set(true);
        return orderInFeed - startOrder;
    }

    public int bindFriendship(FeedWithState feedWithState, int orderInFeed, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> atomicReference, AtomicBoolean outNoFooter) {
        int startOrder = orderInFeed;
        orderInFeed += addStreamItem(outItems, createHeaderItem(feedWithState, false), orderInFeed);
        List<FeedUserEntity> friends = FeedUtils.asFeedUserEntities(FeedUtils.getFriends(feedWithState.feed));
        if (friends.size() > 1) {
            orderInFeed += addStreamItem(outItems, new StreamUsersInRowItem(feedWithState, friends, this.usersRowHeight, this.defaultVSpacing - this.tinyVSpacing), orderInFeed);
        } else {
            StreamUserCommonFriendsItem lastItem = null;
            for (FeedUserEntity entity : friends) {
                if (lastItem != null) {
                    lastItem.setPaddingBottom(this.xlargeVSpacing);
                }
                StreamUserCommonFriendsItem userItem = new StreamUserCommonFriendsItem(feedWithState, entity);
                orderInFeed += addStreamItem(outItems, userItem, orderInFeed);
                lastItem = userItem;
            }
        }
        return orderInFeed - startOrder;
    }

    public int bindMessage(FeedWithState feedWithState, int orderInFeed, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> atomicReference, AtomicBoolean outNoFooter) {
        return (orderInFeed + addStreamItem(outItems, createHeaderItem(feedWithState, true), orderInFeed)) - orderInFeed;
    }

    private int bindSetAvatarNew(FeedWithState feedWithState, int orderInFeed, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> outFooterInfo, AtomicBoolean outNoFooter) {
        LikeInfoContext likeInfo;
        DiscussionSummary discInfo;
        int startOrder = orderInFeed;
        Feed feed = feedWithState.feed;
        List<? extends BaseEntity> photos = FeedUtils.getPhotos(feed);
        AbsFeedPhotoEntity photoNew = null;
        if (photos.size() > 1) {
            photoNew = (AbsFeedPhotoEntity) photos.get(1);
        } else if (photos.size() == 1) {
            photoNew = (AbsFeedPhotoEntity) photos.get(0);
        }
        if (photoNew != null) {
            if (PhotoCollageSettings.isPhotoCollageEnabled()) {
                orderInFeed += PhotoCollageStreamItemBinder.addOnePhotoItem(this.context, this, feedWithState, photoNew, null, false, orderInFeed, outItems);
            } else {
                orderInFeed += bindSinglePhoto(feedWithState, orderInFeed, photoNew, null, false, outItems);
            }
        }
        if (photoNew != null) {
            likeInfo = photoNew.getLikeInfo();
            discInfo = photoNew.getDiscussionSummary();
        } else {
            likeInfo = feed.getLikeInfo();
            discInfo = feed.getDiscussionSummary();
        }
        if (!(likeInfo == null && discInfo == null)) {
            outFooterInfo.set(new FeedFooterInfo(feedWithState, likeInfo, discInfo, null));
        }
        return orderInFeed - startOrder;
    }

    private AbsStreamContentHeaderItem createHeaderItem(FeedWithState feedWithState, boolean useFallbackMessage) {
        Feed feed = feedWithState.feed;
        List<GeneralUserInfo> avatars = FeedUtils.getFeedHeaderAvatars(feed, this.params.doAuthorInHeader);
        ArrayList<GeneralUserInfo> messageUsers = new ArrayList();
        boolean isVoteOrLike = feed.hasDataFlag(4) || feed.hasDataFlag(16);
        boolean isBanner = feed.getPattern() == 7;
        boolean isContentOrBanner = isBanner || feed.getPattern() == 5;
        FeedHeaderInfo info = new FeedHeaderInfo(feedWithState, isVoteOrLike);
        info.addAvatar(avatars);
        bindPromoLabel(info, feed);
        boolean isMessageFromAuthor = false;
        if (this.params.doAuthorInHeader && isContentOrBanner && feed.hasDataFlag(1)) {
            FeedMessage messageFromAuthor = FeedMessageBuilder.buildMessageFromAuthor(FeedUtils.findFirstAuthor(feed), null);
            if (messageFromAuthor != null) {
                info.setMessage(fillHeaderSpannableFromFeedMessage(feed, messageFromAuthor, new SpannableStringBuilder(), false, avatars, messageUsers));
                isMessageFromAuthor = true;
            }
        }
        if (!isMessageFromAuthor) {
            info.setMessage(prepareHeaderMessage(feed, useFallbackMessage, avatars, messageUsers));
        }
        if (!isVoteOrLike) {
            info.setDateFormatted(DateFormatter.getFormatStringFromDate(this.context, feed.getDate()));
        }
        if (avatars != null) {
            for (GeneralUserInfo user : avatars) {
                if (!messageUsers.contains(user)) {
                    messageUsers.add(user);
                }
            }
        }
        info.setReferencedUsers(messageUsers);
        boolean hasOptions = !isBanner;
        boolean isCardTop = !isBanner;
        if (isVoteOrLike) {
            return new StreamKlassHeaderItem(feedWithState, info, hasOptions, isCardTop);
        }
        return new StreamFeedHeaderItem(feedWithState, info, hasOptions, isCardTop);
    }

    private void bindPromoLabel(FeedHeaderInfo info, Feed feed) {
        for (BaseEntity entity : feed.getEntities()) {
            if (entity instanceof FeedMediaTopicEntity) {
                info.setIsPromo(((FeedMediaTopicEntity) entity).isPromo());
                return;
            }
        }
        info.setIsPromo(false);
    }

    public Spannable fillHeaderSpannableFromFeedMessage(Feed feed, FeedMessage feedMessage, @NonNull SpannableStringBuilder sb, boolean useFallbackMessage, List<GeneralUserInfo> headerAvatarUsers, List<GeneralUserInfo> outMentionedUsers) {
        if (feedMessage != null) {
            sb.append(feedMessage.getText());
            ArrayList<FeedMessageSpan> spans = feedMessage.getSpans();
            if (spans != null) {
                int size = spans.size();
                for (int i = 0; i < size; i++) {
                    FeedMessageSpan span = (FeedMessageSpan) spans.get(i);
                    sb.setSpan(span, span.getStartIndex(), span.getEndIndex(), 17);
                    if (span instanceof FeedEntitySpan) {
                        FeedEntitySpan entitySpan = (FeedEntitySpan) span;
                        if (isHeaderAvatarUser(entitySpan, headerAvatarUsers)) {
                            sb.setSpan(new FeedActorSpan(), span.getStartIndex(), span.getEndIndex(), 17);
                        } else {
                            sb.setSpan(new FeedTargetSpan(), span.getStartIndex(), span.getEndIndex(), 17);
                        }
                        if (outMentionedUsers != null) {
                            BaseEntity entity = feed.getEntity(entitySpan.getRef());
                            if (entity instanceof FeedUserEntity) {
                                outMentionedUsers.add(((FeedUserEntity) entity).getUserInfo());
                            } else if (entity instanceof FeedGroupEntity) {
                                outMentionedUsers.add(((FeedGroupEntity) entity).getGroupInfo());
                            }
                        }
                    }
                }
            }
        }
        return sb;
    }

    public Spannable prepareHeaderMessage(Feed feed, boolean useFallbackMessage, List<GeneralUserInfo> headerAvatarUsers, List<GeneralUserInfo> outMentionedUsers) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        FeedMessage title = useFallbackMessage ? feed.getMessage() : feed.getTitle();
        if (title != null) {
            fillHeaderSpannableFromFeedMessage(feed, title, sb, useFallbackMessage, headerAvatarUsers, outMentionedUsers);
        } else if (feed.getPattern() == 7) {
            List<? extends BaseEntity> banners = feed.getBanners();
            if (banners.size() > 0) {
                BaseEntity entity = (BaseEntity) banners.get(0);
                if (entity instanceof FeedBannerEntity) {
                    Banner banner = ((FeedBannerEntity) entity).getBanner();
                    sb.append(Html.fromHtml(banner.header));
                    sb.setSpan(new FeedActorSpan(), 0, sb.length(), 17);
                    sb.append('\n');
                    sb.append(Html.fromHtml(banner.text));
                }
            }
        }
        return sb;
    }

    private static boolean isHeaderAvatarUser(FeedEntitySpan span, List<GeneralUserInfo> avatarUsers) {
        if (span == null || avatarUsers == null) {
            return false;
        }
        String id = span.getEntityId();
        if (id == null) {
            return false;
        }
        int type = span.getEntityType();
        if (type != 7 && type != 2) {
            return false;
        }
        for (GeneralUserInfo info : avatarUsers) {
            if ((info.getObjectType() == 0 || info.getObjectType() == 1) && id.equals(info.getId())) {
                return true;
            }
        }
        return false;
    }

    private int bindContentTargets(FeedWithState feedWithState, int orderInFeed, List<? extends BaseEntity> targets, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> outFooterInfo, AtomicBoolean outNoFooter) {
        int startOrder = orderInFeed;
        int targetCount = targets.size();
        int i = 0;
        for (BaseEntity target : targets) {
            orderInFeed += bindEntity(feedWithState, orderInFeed, target, outItems, outFooterInfo, outNoFooter, i == targetCount + -1);
            i++;
        }
        return orderInFeed - startOrder;
    }

    private int bindEntity(FeedWithState feedWithState, int orderInFeed, BaseEntity entity, List<StreamItem> outItems, AtomicReference<FeedFooterInfo> outFooterInfo, AtomicBoolean outNoFooter, boolean lastEntityInFeed) {
        int startOrder = orderInFeed;
        switch (entity.getType()) {
            case Message.UUID_FIELD_NUMBER /*5*/:
            case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                if (!PhotoCollageSettings.isPhotoCollageEnabled()) {
                    orderInFeed += bindSinglePhoto(feedWithState, orderInFeed, entity, null, false, outItems);
                    break;
                }
                orderInFeed += PhotoCollageStreamItemBinder.addOnePhotoItem(this.context, this, feedWithState, entity, null, false, orderInFeed, outItems);
                break;
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                orderInFeed += bindMediaTopic(feedWithState, orderInFeed, (FeedMediaTopicEntity) entity, false, outItems);
                break;
            case Message.EDITINFO_FIELD_NUMBER /*11*/:
                orderInFeed += bindPoll(feedWithState, orderInFeed, (FeedPollEntity) entity, null, false, (List) outItems);
                break;
            case Conversation.OWNERID_FIELD_NUMBER /*13*/:
                orderInFeed += bindVideo(feedWithState, orderInFeed, (FeedVideoEntity) entity, false, (List) outItems);
                break;
            case C0206R.styleable.Toolbar_collapseIcon /*18*/:
                orderInFeed += bindPlaylist(feedWithState, orderInFeed, (FeedPlayListEntity) entity, outItems, false);
                if (lastEntityInFeed) {
                    outNoFooter.set(true);
                    orderInFeed += addStreamItem(outItems, new StreamCardBottomItem(feedWithState, null), orderInFeed);
                    break;
                }
                break;
        }
        if (orderInFeed > startOrder) {
            LikeInfoContext klassInfo = entity.getLikeInfo();
            DiscussionSummary discInfo = entity.getDiscussionSummary();
            if (!(klassInfo == null && discInfo == null)) {
                outFooterInfo.set(new FeedFooterInfo(feedWithState, klassInfo, discInfo, null));
            }
        }
        return orderInFeed - startOrder;
    }

    private int bindPlaylist(FeedWithState feedWithState, int orderInFeed, FeedPlayListEntity playListEntity, List<StreamItem> outItems, boolean isLastInFeed) {
        int startOrder = orderInFeed;
        List<FeedMusicTrackEntity> tracks = playListEntity.getTracks();
        String playlistImageUrl = playListEntity.getImageUrl();
        Uri playlistImageUri = TextUtils.isEmpty(playlistImageUrl) ? null : Uri.parse(playlistImageUrl);
        ArrayList<Track> playlist = musicEntities2Tracks(tracks, Integer.MAX_VALUE);
        int trackCount = playlist.size();
        if (trackCount == 1) {
            orderInFeed += bindTracks(feedWithState, orderInFeed, playlist, tracks, 0, 1, false, true, playlistImageUri, outItems);
        } else if (trackCount > 1) {
            orderInFeed += addStreamItem(outItems, new StreamMusicPagerItem(feedWithState, playlist, tracks, playlistImageUri, isLastInFeed), orderInFeed);
        }
        return orderInFeed - startOrder;
    }

    private int bindVideo(FeedWithState feedWithState, int orderInFeed, FeedVideoEntity video, boolean needDividerBefore, List<StreamItem> outItems) {
        int startOrder = orderInFeed;
        orderInFeed += addItemWithOptionalDivider(new StreamVideoItem(feedWithState, video), needDividerBefore, outItems, orderInFeed);
        return (orderInFeed + addItemWithOptionalDivider(new StreamVideoCaptionItem(feedWithState, video), false, outItems, orderInFeed)) - startOrder;
    }

    private int bindPhotoLayer(FeedWithState feedWithState, int orderInFeed, List<? extends BaseEntity> photos, boolean needDividerBefore, boolean simpleMode, List<StreamItem> outItems, boolean isLastInFeed) {
        int startOrder = orderInFeed;
        StreamItem photoItem = null;
        if (photos.size() == 1) {
            BaseEntity entity = (BaseEntity) photos.get(0);
            if (entity instanceof AbsFeedPhotoEntity) {
                AbsFeedPhotoEntity photoEntity = (AbsFeedPhotoEntity) entity;
                PhotoInfo photoInfo = ((AbsFeedPhotoEntity) entity).getPhotoInfo();
                float aspectRatio = getSinglePhotoAspectRatio(photoInfo);
                String comment = photoInfo.getComment();
                if (!TextUtils.isEmpty(comment)) {
                    orderInFeed += addItemWithOptionalDivider(new StreamTextItem(feedWithState, comment, new PhotoClickAction(feedWithState, photoEntity, null)), false, outItems, orderInFeed);
                }
                photoItem = StreamSinglePhotoItemFactory.createStreamSinglePhotoActionsItem(feedWithState, photoEntity, null, aspectRatio, isLastInFeed);
            }
        }
        if (photoItem == null) {
            photoItem = new StreamPhotoLayerItem(feedWithState, photos, simpleMode, isLastInFeed);
        }
        return (orderInFeed + addItemWithOptionalDivider(photoItem, needDividerBefore, outItems, orderInFeed)) - startOrder;
    }

    private int bindSinglePhoto(FeedWithState feedWithState, int orderInFeed, BaseEntity entity, MediaItemPhoto mediaItem, boolean needDividerBefore, List<StreamItem> outItems) {
        int startOrder = orderInFeed;
        if (entity instanceof AbsFeedPhotoEntity) {
            AbsFeedPhotoEntity photoEntity = (AbsFeedPhotoEntity) entity;
            if (photoEntity.getPhotoInfo().getSizes().isEmpty()) {
                Logger.m185w("Feed '%s' has no one url", feedWithState.feed);
            }
            orderInFeed += addItemWithOptionalDivider(StreamSinglePhotoItemFactory.createStreamSinglePhotoItem(feedWithState, photoEntity, mediaItem), needDividerBefore, outItems, orderInFeed);
        }
        return orderInFeed - startOrder;
    }

    private float getSinglePhotoAspectRatio(PhotoInfo photoInfo) {
        int imageWidth = photoInfo.getStandartWidth();
        int imageHeight = photoInfo.getStandartHeight();
        if (imageWidth <= imageHeight) {
            return 1.0f;
        }
        if (imageHeight > 0) {
            return ((float) imageWidth) / ((float) imageHeight);
        }
        return (float) imageWidth;
    }

    public int bindIndividualMediaTopic(FeedWithState dummyFeedWithState, int orderInFeed, FeedMediaTopicEntity feedMediaTopicEntity, List<StreamItem> streamMediaTopicItems) {
        int startOrder = orderInFeed;
        List<GeneralUserInfo> avatars = new ArrayList();
        ArrayList<GeneralUserInfo> messageUsers = new ArrayList();
        FeedMessage title = FeedMessageBuilder.buildMessageFromAuthor(feedMediaTopicEntity.getAuthor(), avatars);
        FeedHeaderInfo feedHeaderInfo = new FeedHeaderInfo(dummyFeedWithState, false);
        feedHeaderInfo.setDateFormatted(DateFormatter.getFormatStringFromDate(this.context, feedMediaTopicEntity.getCreationTime()));
        feedHeaderInfo.addAvatar(avatars);
        feedHeaderInfo.setIsPromo(feedMediaTopicEntity.isPromo());
        feedHeaderInfo.setMessage(fillHeaderSpannableFromFeedMessage(dummyFeedWithState.feed, title, new SpannableStringBuilder(), false, avatars, messageUsers));
        if (avatars != null) {
            for (GeneralUserInfo user : avatars) {
                if (!messageUsers.contains(user)) {
                    messageUsers.add(user);
                }
            }
        }
        feedHeaderInfo.setReferencedUsers(messageUsers);
        orderInFeed += addStreamItem(streamMediaTopicItems, new StreamFeedHeaderItem(dummyFeedWithState, feedHeaderInfo), orderInFeed);
        orderInFeed += bindMediaTopic(dummyFeedWithState, orderInFeed, feedMediaTopicEntity, false, streamMediaTopicItems);
        LikeInfoContext klassInfo = feedMediaTopicEntity.getLikeInfo();
        DiscussionSummary discInfo = feedMediaTopicEntity.getDiscussionSummary();
        FeedFooterInfo feedFooterInfo = null;
        if (!(klassInfo == null && discInfo == null)) {
            FeedFooterInfo feedFooterInfo2 = new FeedFooterInfo(dummyFeedWithState, klassInfo, discInfo, null);
        }
        return (orderInFeed + addStreamItem(streamMediaTopicItems, new StreamFeedFooterItem(dummyFeedWithState, feedFooterInfo), orderInFeed)) - startOrder;
    }

    public int bindMediaTopic(FeedWithState feedWithState, int orderInFeed, FeedMediaTopicEntity mediaTopic, boolean needDividerBefore, List<StreamItem> outItems) {
        int startOrder = orderInFeed;
        FeedMediaTopicStyle style = this.mediaTopicStyle;
        int blockCount = mediaTopic.getMediaItemsCount();
        int displayedBlockCount = Math.min(blockCount, style.maxDisplayedBlocks);
        boolean showMore = displayedBlockCount < blockCount;
        List<BaseEntity> withFriends = mediaTopic.getWithFriends();
        List<BaseEntity> places = mediaTopic.getPlaces();
        boolean hasPlaces = (places == null || places.isEmpty()) ? false : true;
        boolean hasWithFriends = (withFriends == null || withFriends.isEmpty()) ? false : true;
        if (hasWithFriends) {
            int addedItems = bindWithFriends(feedWithState, orderInFeed, withFriends, needDividerBefore, hasPlaces, outItems);
            orderInFeed += addedItems;
            needDividerBefore &= addedItems == 0 ? 1 : 0;
        }
        if (hasPlaces) {
            addedItems = bindPlaces(feedWithState, orderInFeed, places, needDividerBefore, outItems);
            orderInFeed += addedItems;
            needDividerBefore &= addedItems == 0 ? 1 : 0;
        }
        boolean displayedShowMoreText = false;
        boolean[] itemShowMore = new boolean[1];
        needDividerBefore |= hasPlaces | hasWithFriends;
        int blockOffset = 0;
        while (blockOffset < displayedBlockCount) {
            int i;
            MediaItem mediaItem = mediaTopic.getMediaItem(blockOffset);
            boolean isLastBlock = blockOffset == displayedBlockCount + -1;
            itemShowMore[0] = false;
            addedItems = bindMediaItem(feedWithState, orderInFeed, blockOffset, mediaItem, needDividerBefore, mediaTopic, isLastBlock, outItems, itemShowMore);
            orderInFeed += addedItems;
            showMore |= itemShowMore[0];
            displayedShowMoreText = itemShowMore[0] && mediaItem.getType() == MediaItemType.TEXT;
            if (addedItems == 0) {
                i = 1;
            } else {
                i = 0;
            }
            needDividerBefore &= i;
            blockOffset++;
        }
        if (((showMore | mediaTopic.isHasMore()) & this.mediaTopicStyle.showMoreAtBottom) && !displayedShowMoreText) {
            ClickAction clickAction;
            if (mediaTopic.getDiscussionSummary() != null) {
                ClickAction discussionClickAction = new DiscussionClickAction(feedWithState, mediaTopic.getDiscussionSummary());
            } else {
                clickAction = null;
            }
            orderInFeed += addStreamItem(outItems, new StreamThreeDotsItem(feedWithState, clickAction, null, true), orderInFeed);
        }
        return orderInFeed - startOrder;
    }

    private int bindWithFriends(FeedWithState feedWithState, int orderInFeed, List<BaseEntity> withFriends, boolean needDividerBefore, boolean needSmallSpaceAfter, List<StreamItem> outItems) {
        int startOrder = orderInFeed;
        ArrayList<UserInfo> users = new ArrayList();
        SpannableStringBuilder sb = new SpannableStringBuilder();
        for (BaseEntity entity : withFriends) {
            if (entity instanceof FeedUserEntity) {
                FeedUserEntity user = (FeedUserEntity) entity;
                users.add(user.getUserInfo());
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                int spanStart = sb.length();
                sb.append(user.getUserInfo().getAnyName());
                sb.setSpan(new FeedTargetSpan(), spanStart, sb.length(), 17);
            }
        }
        StreamUserNamesItem userNamesItem = new StreamUserNamesItem(feedWithState, sb, users);
        if (needSmallSpaceAfter) {
            userNamesItem.setPaddingBottom(this.tinyVSpacing);
        }
        return (orderInFeed + addItemWithOptionalDivider(userNamesItem, needDividerBefore, outItems, orderInFeed)) - startOrder;
    }

    private int bindPlaces(FeedWithState feedWithState, int orderInFeed, List<BaseEntity> placeEntities, boolean needDividerBefore, List<StreamItem> outItems) {
        int startOrder = orderInFeed;
        List<FeedPlaceEntity> places = new ArrayList(placeEntities.size());
        StringBuilder sb = new StringBuilder();
        for (BaseEntity entity : placeEntities) {
            if (entity.getType() == 17) {
                FeedPlaceEntity place = (FeedPlaceEntity) entity;
                places.add(place);
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(place.getName());
                places.add(place);
            }
        }
        return (orderInFeed + addItemWithOptionalDivider(new StreamPlacesItem(feedWithState, places, sb), needDividerBefore, outItems, orderInFeed)) - startOrder;
    }

    private int bindMediaItem(FeedWithState feedWithState, int orderInFeed, int orderInTopic, MediaItem mediaItem, boolean needDividerBefore, FeedMediaTopicEntity enclosingMediaTopic, boolean isLastBlock, List<StreamItem> outItems, boolean[] outShowMore) {
        int startOrder = orderInFeed;
        MediaItemType type = mediaItem.getType();
        boolean isResharedItem = (mediaItem instanceof MediaReshareItem) && ((MediaReshareItem) mediaItem).isReshare();
        if (isResharedItem) {
            orderInFeed += bindReshareAuthors(feedWithState, orderInFeed, outItems, ((MediaReshareItem) mediaItem).getReshareOwners());
        }
        switch (C12331.$SwitchMap$ru$ok$model$mediatopics$MediaItemType[type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                MediaItemPhoto mediaItemPhoto = (MediaItemPhoto) mediaItem;
                boolean z = isLastBlock && this.mediaTopicStyle.showMoreAtBottom;
                orderInFeed += bindPhoto(feedWithState, orderInFeed, mediaItemPhoto, needDividerBefore, z, this.mediaTopicStyle.photoNoCollage, outItems, outShowMore);
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                orderInFeed += bindLink(feedWithState, orderInFeed, (MediaItemLink) mediaItem, needDividerBefore, outItems);
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                orderInFeed += bindMusic(feedWithState, orderInFeed, (MediaItemMusic) mediaItem, needDividerBefore, false, outItems, outShowMore);
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                if (mediaItem instanceof MediaItemPoll) {
                    orderInFeed += bindPoll(feedWithState, orderInFeed, (MediaItemPoll) mediaItem, enclosingMediaTopic, needDividerBefore, (List) outItems);
                    break;
                }
                break;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                orderInFeed += bindText(feedWithState, orderInFeed, orderInTopic, (MediaItemText) mediaItem, enclosingMediaTopic, needDividerBefore, outItems, outShowMore);
                break;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                orderInFeed += bindVideo(feedWithState, orderInFeed, (MediaItemVideo) mediaItem, needDividerBefore, (List) outItems);
                break;
            case Message.TASKID_FIELD_NUMBER /*8*/:
                orderInFeed += bindTopic(feedWithState, orderInFeed, (MediaItemTopic) mediaItem, needDividerBefore, outItems);
                break;
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                orderInFeed += bindApp(feedWithState, orderInFeed, (MediaItemApp) mediaItem, needDividerBefore, outItems, enclosingMediaTopic.getDiscussionSummary());
                break;
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                orderInFeed += bindStub(feedWithState, orderInFeed, (MediaItemStub) mediaItem, needDividerBefore, outItems);
                break;
        }
        return orderInFeed - startOrder;
    }

    private int bindStub(FeedWithState feedWithState, int orderInFeed, MediaItemStub mediaItem, boolean needDividerBefore, List<StreamItem> outItems) {
        return (orderInFeed + addItemWithOptionalDivider(new StreamStubItem(feedWithState, mediaItem.getText()), needDividerBefore, outItems, orderInFeed)) - orderInFeed;
    }

    private int bindApp(FeedWithState feedWithState, int orderInFeed, MediaItemApp mediaItem, boolean needDividerBefore, List<StreamItem> outItems, DiscussionSummary discussionSummary) {
        int startOrder = orderInFeed;
        DiscussionClickAction clickAction = new DiscussionClickAction(feedWithState, discussionSummary);
        if (!TextUtils.isEmpty(mediaItem.getText())) {
            orderInFeed += addStreamItem(outItems, new StreamTextItem(feedWithState, mediaItem.getText(), clickAction), orderInFeed);
        }
        return (orderInFeed + addItemWithOptionalDivider(new StreamAppItem(feedWithState, mediaItem, clickAction), needDividerBefore, outItems, orderInFeed)) - startOrder;
    }

    private int bindText(FeedWithState feedWithState, int orderInFeed, int orderInTopic, MediaItemText textItem, FeedMediaTopicEntity enclosingTopic, boolean needDividerBefore, List<StreamItem> outItems, boolean[] outShowMore) {
        SpannableStringBuilder outText;
        int maxSpanEndIndex;
        int startOrder = orderInFeed;
        FeedMediaTopicStyle style = this.mediaTopicStyle;
        FeedMessage text = textItem.getText();
        String string = text.getText();
        boolean exceedMaxLength = string.length() > style.maxTextLengthInBlock;
        boolean exceedMaxLines = StringUtils.linesCount(string) > style.maxTextLinesInBlock;
        if (exceedMaxLength || exceedMaxLines) {
            String trimmedString;
            int threeDotsColor = this.context.getResources().getColor(2131493005);
            if (exceedMaxLength) {
                trimmedString = trimTextByPunctuation(string, style.maxTextLengthInBlock);
            } else {
                trimmedString = trimTextByLines(string, style.maxTextLinesInBlock);
            }
            outText = new SpannableStringBuilder(trimmedString + " \u2022\u2022\u2022");
            outText.setSpan(new AbsoluteSizeSpan(this.fontSizeSmall), outText.length() - 3, outText.length(), 17);
            outText.setSpan(new ForegroundColorSpan(threeDotsColor), outText.length() - 3, outText.length(), 17);
            maxSpanEndIndex = outText.length() - 3;
            outShowMore[0] = true;
        } else {
            outText = new SpannableStringBuilder(text.getText());
            maxSpanEndIndex = outText.length();
        }
        ArrayList<FeedMessageSpan> spans = text.getSpans();
        if (spans != null) {
            int size = spans.size();
            for (int i = 0; i < size; i++) {
                FeedMessageSpan span = (FeedMessageSpan) spans.get(i);
                if (span.getEndIndex() <= maxSpanEndIndex) {
                    outText.setSpan(span, span.getStartIndex(), span.getEndIndex(), 17);
                }
            }
        }
        DiscussionSummary discSummary = enclosingTopic.getDiscussionSummary();
        String currentUid = OdnoklassnikiApplication.getCurrentUser().getId();
        boolean textEditable = false;
        if (this.mediaTopicStyle.textEditable && !enclosingTopic.isUnmodifiable()) {
            if (enclosingTopic.getOwner() instanceof FeedUserEntity) {
                if (currentUid.equals(enclosingTopic.getOwner().getId())) {
                    textEditable = true;
                }
            } else if (enclosingTopic.isEditable()) {
                textEditable = true;
            } else if (enclosingTopic.getAuthor() != null && (enclosingTopic.getAuthor() instanceof FeedUserEntity) && currentUid.equals(enclosingTopic.getAuthor().getId())) {
                textEditable = true;
            }
        }
        return (orderInFeed + addItemWithOptionalDivider(new StreamSpannableTextItem(feedWithState, outText, textEditable, enclosingTopic.getId(), orderInTopic, discSummary, this.entitySpanStyle), needDividerBefore, outItems, orderInFeed)) - startOrder;
    }

    public static boolean isPunctuation(char c) {
        return c == ',' || c == '.' || c == '!' || c == '?' || c == ':' || c == ';';
    }

    private static String trimTextByPunctuation(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        int halfLength = text.length() / 2;
        int whiteSpacePosition = 0;
        int trimPosition = maxLength;
        while (trimPosition > halfLength) {
            char c = text.charAt(trimPosition);
            if (isPunctuation(c)) {
                break;
            }
            if (whiteSpacePosition == 0 && Character.isWhitespace(c)) {
                whiteSpacePosition = trimPosition;
            }
            trimPosition--;
        }
        if (trimPosition == halfLength) {
            if (whiteSpacePosition != 0) {
                trimPosition = whiteSpacePosition;
            } else {
                trimPosition = maxLength;
            }
        }
        return text.substring(0, trimPosition);
    }

    private static String trimTextByLines(String text, int maxLines) {
        int currentLineNumber = 0;
        if (maxLines == 0) {
            return text;
        }
        int trimPosition = text.length();
        int size = text.length();
        for (int i = 0; i < size; i++) {
            if (text.charAt(i) == '\n') {
                currentLineNumber++;
                if (currentLineNumber == maxLines) {
                    trimPosition = i;
                }
            }
        }
        return text.substring(0, trimPosition);
    }

    private int bindLink(FeedWithState feedWithState, int orderInFeed, MediaItemLink linkItem, boolean needDividerBefore, List<StreamItem> outItems) {
        int startOrder = orderInFeed;
        SimpleTemplateChooser templateChooser = new SimpleTemplateChooser(this.context.getResources().getDisplayMetrics(), linkItem);
        LinkTemplateStats.logLinkTemplate(this.context, templateChooser);
        return (orderInFeed + addItemWithOptionalDivider(new StreamLinkItem(feedWithState, templateChooser), needDividerBefore, outItems, orderInFeed)) - startOrder;
    }

    private int bindMusic(FeedWithState feedWithState, int orderInFeed, MediaItemMusic musicItem, boolean needDividerBefore, boolean withCoverImage, List<StreamItem> outItems, boolean[] outShowMore) {
        int startOrder = orderInFeed;
        List<FeedMusicTrackEntity> trackEntities = musicItem.getTracks();
        ArrayList<Track> playlist = musicEntities2Tracks(trackEntities, Integer.MAX_VALUE);
        int maxTracksToDisplay = Math.min(Math.min(trackEntities.size(), this.mediaTopicStyle.maxTracksInBlock), Integer.MAX_VALUE);
        if (outShowMore != null) {
            outShowMore[0] = trackEntities.size() > maxTracksToDisplay;
        }
        if (playlist.size() > 0) {
            if (withCoverImage) {
                int addedItems = bindTracks(feedWithState, orderInFeed, playlist, trackEntities, 0, 1, needDividerBefore, true, null, outItems);
                orderInFeed += addedItems;
                needDividerBefore &= addedItems == 0 ? 1 : 0;
                if (playlist.size() > 1) {
                    orderInFeed += bindTracks(feedWithState, orderInFeed, playlist, trackEntities, 0, maxTracksToDisplay, needDividerBefore, false, null, outItems);
                }
            } else {
                orderInFeed += bindTracks(feedWithState, orderInFeed, playlist, trackEntities, 0, maxTracksToDisplay, needDividerBefore, false, null, outItems);
            }
        }
        return orderInFeed - startOrder;
    }

    private static ArrayList<Track> musicEntities2Tracks(List<FeedMusicTrackEntity> trackEntities, int maxTracks) {
        ArrayList<Track> tracks = new ArrayList();
        for (FeedMusicTrackEntity trackEntity : trackEntities) {
            tracks.add(trackFromEntity(trackEntity));
            if (tracks.size() == maxTracks) {
                break;
            }
        }
        return tracks;
    }

    private int bindTracks(FeedWithState feedWithState, int orderInFeed, ArrayList<Track> playlist, List<FeedMusicTrackEntity> trackEntities, int fromPosition, int toPosition, boolean needDividerBefore, boolean withCoverImage, Uri defaultCoverImageUri, List<StreamItem> outItems) {
        int startOrder = orderInFeed;
        for (int trackPosition = fromPosition; trackPosition < toPosition; trackPosition++) {
            AbsStreamMusicTrackItem item;
            if (withCoverImage) {
                item = new StreamMusicCoverItem(feedWithState, playlist, trackEntities, trackPosition, defaultCoverImageUri);
            } else {
                item = new StreamMusicTrackItem(feedWithState, playlist, trackEntities, trackPosition, defaultCoverImageUri);
            }
            orderInFeed += addItemWithOptionalDivider(item, needDividerBefore, outItems, orderInFeed);
            needDividerBefore = false;
        }
        return orderInFeed - startOrder;
    }

    private static Album albumFromEntity(FeedMusicTrackEntity trackEntity) {
        List<FeedMusicAlbumEntity> albumEntities = trackEntity.getAlbums();
        if (albumEntities == null || albumEntities.isEmpty()) {
            return new Album(0, trackEntity.getAlbumName(), null, null);
        }
        return ((FeedMusicAlbumEntity) albumEntities.get(0)).getAlbum();
    }

    private static Artist artistFromEntity(FeedMusicTrackEntity trackEntity) {
        List<FeedMusicArtistEntity> artistEntities = trackEntity.getArtists();
        if (artistEntities == null || artistEntities.isEmpty()) {
            return new Artist(0, trackEntity.getArtistName(), null);
        }
        return ((FeedMusicArtistEntity) artistEntities.get(0)).getArtist();
    }

    public static Track trackFromEntity(FeedMusicTrackEntity trackEntity) {
        Artist artist = artistFromEntity(trackEntity);
        Album album = albumFromEntity(trackEntity);
        int duration = trackEntity.getDuration();
        return new Track(Long.parseLong(trackEntity.getId()), trackEntity.getTitle(), album.ensemble, trackEntity.getImageUrl(), trackEntity.getFullName(), album, artist, false, duration > 0 ? duration : 180);
    }

    private int bindVideo(FeedWithState feedWithState, int orderInFeed, MediaItemVideo videoItem, boolean needDividerBefore, List<StreamItem> outItems) {
        int startOrder = orderInFeed;
        for (FeedVideoEntity video : videoItem.getVideos()) {
            int addedItems = bindVideo(feedWithState, orderInFeed, video, needDividerBefore, (List) outItems);
            orderInFeed += addedItems;
            needDividerBefore &= addedItems == 0 ? 1 : 0;
        }
        return orderInFeed - startOrder;
    }

    private int bindPoll(FeedWithState feedWithState, int orderInFeed, MediaItemPoll pollItem, FeedMediaTopicEntity enclosingTopic, boolean needDividerBefore, List<StreamItem> outItems) {
        int startOrder = orderInFeed;
        for (FeedPollEntity poll : pollItem.getPolls()) {
            int addedItems = bindPoll(feedWithState, orderInFeed, poll, enclosingTopic, needDividerBefore, (List) outItems);
            orderInFeed += addedItems;
            needDividerBefore &= addedItems == 0 ? 1 : 0;
        }
        return orderInFeed - startOrder;
    }

    private int bindPhoto(FeedWithState feedWithState, int orderInFeed, MediaItemPhoto photoItem, boolean needDividerBefore, boolean isLastBlock, boolean photoNoCollage, List<StreamItem> outItems, boolean[] outShowMore) {
        int startOrder = orderInFeed;
        List<AbsFeedPhotoEntity> photos = photoItem.getPhotos();
        if (photoNoCollage) {
            for (AbsFeedPhotoEntity photo : photos) {
                orderInFeed += bindSinglePhoto(feedWithState, orderInFeed, photo, photoItem, needDividerBefore, outItems);
            }
            return orderInFeed - startOrder;
        }
        if (photos.size() == 1) {
            orderInFeed += bindSinglePhoto(feedWithState, orderInFeed, (BaseEntity) photos.get(0), photoItem, needDividerBefore, outItems);
        } else if (PhotoCollageSettings.isPhotoCollageEnabled()) {
            for (AbsFeedPhotoEntity photo2 : photos) {
                photo2.getPhotoInfo().setPhotoContext(PhotoContext.MEDIATOPIC);
            }
            orderInFeed += bindPhotoCollage(feedWithState, orderInFeed, photos, needDividerBefore, isLastBlock, outShowMore, outItems, null);
        } else {
            orderInFeed += bindPhotoLayer(feedWithState, orderInFeed, photos, needDividerBefore, true, outItems, false);
        }
        return orderInFeed - startOrder;
    }

    private int bindTopic(FeedWithState feedWithState, int orderInFeed, MediaItemTopic topicItem, boolean needDividerBefore, List<StreamItem> outItems) {
        int startOrder = orderInFeed;
        int topicCount = 0;
        for (FeedMediaTopicEntity topic : topicItem.getMediaTopics()) {
            if (topicCount > 0) {
                orderInFeed += addStreamItem(outItems, new StreamDividerItem(feedWithState), orderInFeed);
            }
            int addedItems = bindMediaTopic(feedWithState, orderInFeed, topic, needDividerBefore, outItems);
            orderInFeed += addedItems;
            needDividerBefore &= addedItems == 0 ? 1 : 0;
            topicCount++;
        }
        return orderInFeed - startOrder;
    }

    private StreamJoinGroupItem createJoinGroupItem(FeedWithState feedWithState, FeedGroupEntity group) {
        GroupInfo info = group.getGroupInfo();
        String titleText = info.getName();
        if (TextUtils.isEmpty(titleText)) {
            titleText = null;
        }
        String descrText = info.getDescription();
        if (TextUtils.isEmpty(descrText)) {
            descrText = null;
        }
        return new StreamJoinGroupItem(feedWithState, group, titleText, descrText, info.getAnyPicUrl());
    }

    private int bindPoll(FeedWithState feedWithState, int orderInFeed, FeedPollEntity poll, FeedMediaTopicEntity enclosingTopic, boolean needDividerBefore, List<StreamItem> outItems) {
        int l;
        int startOrder = orderInFeed;
        SpannableStringBuilder headerText = new SpannableStringBuilder();
        headerText.append(poll.question);
        headerText.append('\n');
        int questionEnd = headerText.length();
        headerText.setSpan(new TextAppearanceSpan(this.context, 2131296704), 0, questionEnd, 17);
        headerText.setSpan(new LineSpacingSpan(this.context.getResources().getDimensionPixelOffset(2131230981)), questionEnd, questionEnd, 34);
        headerText.append(LocalizationManager.getString(this.context, StringUtils.plural(totalParticipants, 2131166649, 2131166648, 2131166646, 2131166647), Integer.valueOf(poll.getTotalParticipants())));
        headerText.setSpan(new TextAppearanceSpan(this.context, 2131296707), questionEnd, headerText.length(), 17);
        orderInFeed += addItemWithOptionalDivider(new StreamPollHeaderItem(feedWithState, headerText, enclosingTopic == null ? null : enclosingTopic.getDiscussionSummary()), needDividerBefore, outItems, orderInFeed);
        List<Answer> answers = poll.answers;
        int answersCount = answers.size();
        boolean isMultiAnswersAllowed = !poll.options.contains("SingleChoice");
        int maxVotes = 0;
        boolean hasSelf = false;
        for (l = 0; l < answersCount; l++) {
            ActionCountInfo voteInfo = ((Answer) answers.get(l)).voteInfo;
            int votes = voteInfo == null ? 0 : voteInfo.count;
            if (votes > maxVotes) {
                maxVotes = votes;
            }
            hasSelf |= voteInfo.self;
        }
        for (l = 0; l < answersCount; l++) {
            orderInFeed += addStreamItem(outItems, new StreamPollAnswerItem(feedWithState, poll, (Answer) answers.get(l), maxVotes, isMultiAnswersAllowed, hasSelf), orderInFeed);
        }
        return orderInFeed - startOrder;
    }
}
