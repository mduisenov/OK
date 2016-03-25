package ru.ok.android.statistics.stream;

import android.text.TextUtils;
import android.util.Pair;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.stream.list.PresentInfo;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.onelog.feed.FeedClick.Target;
import ru.ok.onelog.feed.FeedClickFactory;
import ru.ok.onelog.feed.FeedShowFactory;

public class StreamStats {
    public static void clickPhoto(int position, Feed feed, String photoId) {
        oneLogClick(position, feed, Target.CONTENT_COLLAGE, photoId);
        StatisticManager.getInstance().addStatisticEvent("feed-action_photo", false, new Pair("feed_type", Integer.toString(feed.getFeedType())));
    }

    public static void clickVideo(int position, Feed feed, String videoId) {
        oneLogClick(position, feed, Target.CONTENT_VIDEO_PLAY, videoId);
        StatisticManager.getInstance().addStatisticEvent("feed-action_video", false, new Pair("feed_type", Integer.toString(feed.getFeedType())));
    }

    public static void clickBanner() {
        StatisticManager.getInstance().addStatisticEvent("feed-action_banner", false, new Pair[0]);
    }

    public static void clickPromoLink() {
        StatisticManager.getInstance().addStatisticEvent("feed-action_promo", false, new Pair[0]);
    }

    public static void clickUser(String source) {
        StatisticManager.getInstance().addStatisticEvent("feed-action_user", false, new Pair("source", source));
    }

    public static void clickGroup(String source) {
        StatisticManager.getInstance().addStatisticEvent("feed-action_group", false, new Pair("source", source));
    }

    public static void clickMediaTopic() {
        StatisticManager.getInstance().addStatisticEvent("feed-action_topic", false, new Pair[0]);
    }

    public static void clickPlace() {
        StatisticManager.getInstance().addStatisticEvent("feed-action_place", false, new Pair[0]);
    }

    public static void clickMutualFriends() {
        StatisticManager.getInstance().addStatisticEvent("feed-action_mutual_friends", false, new Pair[0]);
    }

    public static void clickMembers() {
        StatisticManager.getInstance().addStatisticEvent("feed-action_members", false, new Pair[0]);
    }

    public static void clickFeedOptions(int position, Feed feed) {
        StatisticManager.getInstance().addStatisticEvent("feed-action_options", false, new Pair("feed_type", Integer.toString(feed.getFeedType())));
    }

    public static void clickComplain(int position, Feed feed) {
        StatisticManager.getInstance().addStatisticEvent("feed-action_complain", false, new Pair("feed_type", Integer.toString(feed.getFeedType())));
    }

    public static void clickHide(int position, Feed feed) {
        StatisticManager.getInstance().addStatisticEvent("feed-action_hide", false, new Pair("feed_type", Integer.toString(feed.getFeedType())));
    }

    public static void clickHideConfirm(int position, String feedStatInfo) {
        oneLogClick(position, feedStatInfo, Target.REMOVE);
    }

    public static void clickHideConfirmUnsubscribe(int position, String feedStatInfo) {
        oneLogClick(position, feedStatInfo, Target.REMOVEMENU_UNSUBSCRIBE);
    }

    public static void clickLike(int position, Feed feed, LikeInfoContext info) {
        clickLike(position, feed, info, info.self ? Target.UNLIKE : Target.LIKE);
    }

    public static void clickLikePhoto(int position, Feed feed, LikeInfoContext info) {
        clickLike(position, feed, info, info.self ? Target.CONTENT_COLLAGE_UNLIKE : Target.CONTENT_COLLAGE_LIKE);
    }

    private static void clickLike(int position, Feed feed, LikeInfoContext info, Target target) {
        oneLogClick(position, feed, target);
        StatisticManager.getInstance().addStatisticEvent("feed-action_like", false, new Pair("feed_type", Integer.toString(feed.getFeedType())), new Pair("entity_type", getEntityTypeName(info)), new Pair("unlike", Boolean.toString(info.self)));
    }

    public static void clickComment(int position, Feed feed, DiscussionSummary info) {
        clickComment(position, feed, info, Target.COMMENT);
    }

    public static void clickCommentPhoto(int position, Feed feed, DiscussionSummary info) {
        clickComment(position, feed, info, Target.CONTENT_COLLAGE_COMMENT);
    }

    private static void clickComment(int position, Feed feed, DiscussionSummary info, Target target) {
        oneLogClick(position, feed, target);
        StatisticManager.getInstance().addStatisticEvent("feed-action_comment", false, new Pair("feed_type", Integer.toString(feed.getFeedType())), new Pair("discussion_type", info.discussion.type));
    }

    public static void clickLikeCount(int position, Feed feed, LikeInfoContext info) {
        StatisticManager.getInstance().addStatisticEvent("feed-action_like_count", false, new Pair("feed_type", Integer.toString(feed.getFeedType())), new Pair("entity_type", getEntityTypeName(info)));
    }

    public static void clickLink(int position, Feed feed, String linkUrl) {
        oneLogClick(position, feed, Target.CONTENT_LINK_EXT, linkUrl);
        StatisticManager.getInstance().addStatisticEvent("feed-action_link", false, new Pair("feed_type", Integer.toString(feed.getFeedType())), new Pair("source", "link_block"));
    }

    public static void clickPresent(int position, Feed feed, PresentInfo info) {
        clickPresent(position, feed, info, "present_image", Target.PRESENT);
    }

    public static void clickMakePresent(int position, Feed feed, PresentInfo info) {
        clickPresent(position, feed, info, "present_button", Target.MAKE_PRESENT);
    }

    private static void clickPresent(int position, Feed feed, PresentInfo presentInfo, String source, Target target) {
        oneLogClick(position, feed, target, presentInfo.getPresentTypeId());
        String presentType = presentInfo.isMusic ? "music" : presentInfo.isBig ? "big" : "small";
        StatisticManager.getInstance().addStatisticEvent("feed-action_present", false, new Pair("feed_type", Integer.toString(feed.getFeedType())), new Pair("source", source), new Pair("present_type", presentType));
    }

    public static void clickAchievement(int position, Feed feed, String presentId) {
        oneLogClick(position, feed, Target.PRESENT, presentId);
        StatisticManager.getInstance().addStatisticEvent("feed-action_achievement", false, new Pair("feed_type", Integer.toString(feed.getFeedType())), new Pair("source", "present_button"));
    }

    public static void clickPlayMusic(int position, Feed feed, String source, String action, long trackId) {
        oneLogClick(position, feed, Target.CONTENT_MUSIC_PLAY, String.valueOf(trackId));
        StatisticManager.getInstance().addStatisticEvent("feed-action_play_music", false, new Pair("feed_type", Integer.toString(feed.getFeedType())), new Pair("source", source), new Pair("action", action));
    }

    public static void clickPlayPresent(int position, Feed feed) {
        StatisticManager.getInstance().addStatisticEvent("feed-action_play_present", false, new Pair("feed_type", Integer.toString(feed.getFeedType())));
    }

    public static void clickMore(int position, Feed feed) {
        oneLogClick(position, feed, Target.CONTENT_MORE);
    }

    public static void clickVotePoll(int position, Feed feed, long answerId) {
        oneLogClick(position, feed, Target.CONTENT_POLL_VOTE, String.valueOf(answerId));
        StatisticManager.getInstance().addStatisticEvent("feed-action_vote_poll", false, new Pair("feed_type", Integer.toString(feed.getFeedType())));
    }

    public static void showFull(int position, long duration, String feedStatInfo) {
        OneLog.log(FeedShowFactory.get(position, duration, feedStatInfo));
    }

    public static String getEntityTypeName(LikeInfoContext info) {
        if (info == null) {
            return null;
        }
        switch (info.entityType) {
            case Message.UUID_FIELD_NUMBER /*5*/:
            case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                return "photo";
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                return "topic";
            case Conversation.OWNERID_FIELD_NUMBER /*13*/:
                return "video";
            default:
                return "other";
        }
    }

    public static void clickEntity1(int position, Feed feed) {
        oneLogClick(position, feed, Target.ENTITY_1);
    }

    public static void clickEntity2(int position, Feed feed) {
        oneLogClick(position, feed, Target.ENTITY_2);
    }

    private static void oneLogClick(int position, Feed feed, Target target) {
        oneLogClick(position, feed.getFeedStatInfo(), target);
    }

    private static void oneLogClick(int position, Feed feed, Target target, String targetId) {
        oneLogClick(position, feed.getFeedStatInfo(), target, targetId);
    }

    private static void oneLogClick(int position, String feedStatInfo, Target target) {
        oneLogClick(position, feedStatInfo, target, null);
    }

    private static void oneLogClick(int position, String feedStatInfo, Target target, String targetId) {
        if (TextUtils.isEmpty(targetId)) {
            targetId = "n/a";
        }
        OneLog.log(FeedClickFactory.get(position, target, feedStatInfo, targetId));
    }
}
