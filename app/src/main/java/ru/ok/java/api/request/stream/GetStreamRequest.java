package ru.ok.java.api.request.stream;

import android.text.TextUtils;
import java.util.List;
import org.json.JSONObject;
import ru.ok.java.api.HttpMethodType;
import ru.ok.java.api.Scope;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.java.api.request.stream.GetStream.FilterOption;
import ru.ok.java.api.request.stream.GetStream.VideoEntity.Fields;
import ru.ok.java.api.utils.Constants.Api;

@HttpPreamble(hasSessionKey = true, httpType = HttpMethodType.POST, signType = Scope.SESSION)
public class GetStreamRequest extends BaseRequest {
    public static final String[] FIELDS_ALL_TEST;
    public static final String[] FIELDS_ONLY_FEEDS;
    public static final String[] FIELDS_PROD;
    final String anchorId;
    final JSONObject bannerOpt;
    final int count;
    final String fieldSet;
    final List<FilterOption> filterOptions;
    final String gid;
    final boolean markAsRead;
    final String[] requestedFields;
    final String supportedPatterns;
    final String uid;

    static {
        FIELDS_PROD = new String[]{"feed.pattern", "feed.date", "feed.feed_owner_refs", "feed.receiver_refs", "feed.owner_refs", "feed.sender_refs", "feed.actor_refs", "feed.pin_refs", "feed.friend_refs", "feed.present_refs", "feed.target_refs", "feed.place_refs", "feed.message_tokens", "feed.like_summary", "feed.discussion_summary", "feed.mark_as_spam_id", "feed.delete_id", "feed.actions", "feed.title_tokens", "feed.author_refs", "feed.pinned", "feed.banner", "feed.action_type", "user.uid", "user.first_name", "user.last_name", "user.name", "user.gender", "user.pic128x128", "user.age", "user.location", "photo.id", "photo.album_id", "photo.pic128x128", "photo.pic240min", "photo.pic640x480", "photo.pic1024x768", "photo.picmp4", "photo.like_summary", "photo.discussion_summary", "photo.user_id", "photo.standard_width", "photo.standard_height", "photo.like_allowed", "photo.delete_allowed", "photo.mark_allowed", "photo.mark_allowed", "photo.modify_allowed", "photo.text", "photo.context", "group_photo.group_id", "group_photo.like_allowed", "group_photo.delete_allowed", "group_photo.mark_allowed", "group_photo.mark_allowed", "group_photo.modify_allowed", "group_photo.topic_id", "media_topic.id", "media_topic.has_more", "media_topic.author_ref", "media_topic.with_friend_refs", "media_topic.place_ref", "media_topic.like_summary", "media_topic.discussion_summary", "media_topic.media", "media_topic.media_description", "media_topic.media_media_topic_refs", "media_topic.media_movie_refs", "media_topic.media_photo_refs", "media_topic.media_poll_refs", "media_topic.media_reshare", "media_topic.media_reshare_owner_refs", "media_topic.media_text", "media_topic.media_title", "media_topic.media_music_track_refs", "media_topic.media_type", "media_topic.media_url", "media_topic.media_url_image", "poll.id", "poll.question", "poll.answers", "poll.options", "poll.answer_id", "poll.answer_text", "poll.answer_vote_summary", "music_track.id", "music_track.title", "music_track.album_name", "music_track.artist_name", "music_track.image", "music_track.album_ref", "music_track.artist_ref", "music_album.id", "music_album.name", "music_album.image", "music_artist.id", "music_artist.name", "music_artist.image", "music_playlist.id", "music_playlist.title", "music_playlist.image", "music_playlist.track_refs", "album.aid", "album.title", "album.main_photo", "album.like_summary", "group_album.aid", "group_album.title", "group_album.main_photo", "group_album.like_summary", "video.id", "video.title", "video.description", "video." + Fields.THUMBNAIL, "video." + Fields.THUMBNAIL_SMALL, "video." + Fields.THUMBNAIL_BIG, "video." + Fields.THUMBNAIL_HIGH, "video." + Fields.THUMBNAIL_HD, "video.duration", "video.like_summary", "video.discussion_summary", "present.*", "present_type.id", "present_type.pic70x70", "present_type.pic128x128", "present_type.pic256x256", "present_type.is_animated", "present_type.sprite70", "present_type.sprite128", "present_type.sprite140", "present_type.sprite256", "present_type.animation_properties", "present_type.pic640x320", "present_type.isLive", "group.uid", "group.name", "group.pic_avatar", "group.category", "group.private", "group.premium", "group.main_photo", "group.created_ms", "group.attrs", "group_photo.id", "group_photo.album_id", "group_photo.pic128x128", "group_photo.pic240min", "group_photo.pic640x480", "group_photo.pic1024x768", "group_photo.like_summary", "group_photo.discussion_summary", "group_photo.user_id", "group_photo.standard_width", "group_photo.standard_height", "achievement_type.id", "achievement_type.pic76x76", "achievement_type.title", "achievement_type.description", "place.id", "place.name", "achievement.id", "achievement.receiver_ref", "achievement.type_ref"};
        FIELDS_ALL_TEST = FIELDS_PROD;
        FIELDS_ONLY_FEEDS = new String[]{"feed.*", "feed.feed_owner_refs"};
    }

    public GetStreamRequest(String supportedPatterns, String[] requestedFields, String fieldSet, String anchorId, int count, String uid, String gid, boolean markAsRead, JSONObject bannerOpt) {
        this.supportedPatterns = supportedPatterns;
        this.requestedFields = requestedFields;
        this.fieldSet = fieldSet;
        this.anchorId = anchorId;
        this.count = count;
        this.uid = uid;
        this.gid = gid;
        this.filterOptions = GetStream.DEFAULT_FILTER_OPTIONS;
        this.markAsRead = markAsRead;
        this.bannerOpt = bannerOpt;
    }

    public String getMethodName() {
        return "stream.get";
    }

    public void serializeInternal(RequestSerializer<?> serializer) {
        serializer.add(SerializeParamName.PATTERNS, this.supportedPatterns == null ? "FRIENDSHIP,JOIN,MESSAGE,PRESENT,PIN,CONTENT,GIFTS_CAMPAIGN,BANNER" : this.supportedPatterns);
        String fields = this.requestedFields == null ? null : TextUtils.join(",", this.requestedFields);
        if (fields != null) {
            serializer.add(SerializeParamName.FIELDS, fields);
        }
        if (this.fieldSet != null) {
            serializer.add(SerializeParamName.FIELDSET, this.fieldSet);
        }
        if (this.anchorId != null) {
            serializer.add(SerializeParamName.ANCHOR, this.anchorId);
        }
        serializer.add(SerializeParamName.DIRECTION, PagingDirection.FORWARD.getValue());
        if (this.count > 0) {
            serializer.add(SerializeParamName.COUNT, Integer.toString(this.count));
        }
        if (!TextUtils.isEmpty(this.uid)) {
            serializer.add(SerializeParamName.USER_ID, this.uid);
        }
        if (!TextUtils.isEmpty(this.gid)) {
            serializer.add(SerializeParamName.GID, this.gid);
        }
        if (!(this.filterOptions == null || this.filterOptions.isEmpty())) {
            serializer.add(SerializeParamName.FILTER_OPTIONS, joinFilterOptions(this.filterOptions));
        }
        serializer.add(SerializeParamName.CLIENT, Api.CLIENT_NAME);
        serializer.add(SerializeParamName.STREAM_VERSION, "android.1");
        serializer.add(SerializeParamName.MARK_AS_READ, this.markAsRead);
        if (this.bannerOpt != null) {
            serializer.add(SerializeParamName.BANNER_OPT, this.bannerOpt.toString());
        }
    }

    private static String joinFilterOptions(List<FilterOption> filterOptions) {
        StringBuilder sb = new StringBuilder();
        for (FilterOption filterOption : filterOptions) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(filterOption.getApiName());
        }
        return sb.toString();
    }
}
