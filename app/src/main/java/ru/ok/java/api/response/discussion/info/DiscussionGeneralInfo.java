package ru.ok.java.api.response.discussion.info;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Serializable;
import ru.ok.model.messages.MessageBase.Flags;
import ru.ok.model.stream.LikeInfoContext;

public class DiscussionGeneralInfo implements Parcelable, Serializable {
    public static final Creator<DiscussionGeneralInfo> CREATOR;
    private static final long serialVersionUID = 1;
    public final int commentsCount;
    public final long creationDate;
    private Flags flags;
    public final DiscussionGroup group;
    public final String id;
    public final boolean isNews;
    public final long lastUserAccessDate;
    private LikeInfoContext likeInfo;
    public final String message;
    private int newCommentsCount;
    public final String ownerUid;
    public final Permissions permissions;
    public final String title;
    public final String topicOwnerId;
    public final Type type;
    public final DiscussionUser user;

    public enum Type {
        USER_STATUS,
        GROUP_TOPIC,
        USER_PHOTO,
        GROUP_PHOTO,
        USER_ALBUM,
        GROUP_ALBUM,
        GROUP_MOVIE,
        MOVIE,
        SHARE,
        HAPPENING_TOPIC,
        USER_FORUM,
        SCHOOL_FORUM,
        CITY_NEWS,
        UNKNOWN;

        public static Type safeValueOf(String type) {
            for (Type value : values()) {
                if (value.name().equals(type)) {
                    return value;
                }
            }
            return UNKNOWN;
        }
    }

    public DiscussionGeneralInfo(String id, Type type, String ownerUid, String topicOwnerId, String title, String message, int commentsCount, int newCommentsCount, boolean isNews, long creationDate, long lastUserAccessDate, DiscussionUser user, DiscussionGroup group, LikeInfoContext likeInfo, Permissions permissions, Flags flags) {
        this.id = id;
        this.type = type;
        this.ownerUid = ownerUid;
        this.topicOwnerId = topicOwnerId;
        this.title = title;
        this.message = message;
        this.commentsCount = commentsCount;
        this.newCommentsCount = newCommentsCount;
        this.isNews = isNews;
        this.creationDate = creationDate;
        this.lastUserAccessDate = lastUserAccessDate;
        this.user = user;
        this.group = group;
        this.likeInfo = likeInfo;
        this.permissions = permissions;
        this.flags = flags;
    }

    public void setLikeInfo(LikeInfoContext likeInfo) {
        this.likeInfo = likeInfo;
    }

    public LikeInfoContext getLikeInfo() {
        return this.likeInfo;
    }

    public int getNewCommentsCount() {
        return this.newCommentsCount;
    }

    public Flags getFlags() {
        return this.flags;
    }

    public boolean isMusicStatus() {
        return this.type == Type.USER_STATUS && this.title != null && this.title.startsWith("music://");
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int parcelFlags) {
        dest.writeString(this.id);
        dest.writeSerializable(this.type);
        dest.writeString(this.ownerUid);
        dest.writeString(this.topicOwnerId);
        dest.writeString(this.title);
        dest.writeString(this.message);
        dest.writeInt(this.commentsCount);
        dest.writeInt(this.newCommentsCount);
        dest.writeSerializable(Boolean.valueOf(this.isNews));
        dest.writeLong(this.creationDate);
        dest.writeLong(this.lastUserAccessDate);
        dest.writeParcelable(this.user, parcelFlags);
        dest.writeParcelable(this.group, parcelFlags);
        dest.writeParcelable(this.likeInfo, parcelFlags);
        dest.writeParcelable(this.permissions, parcelFlags);
        dest.writeParcelable(this.flags, parcelFlags);
    }

    static {
        CREATOR = new 1();
    }
}
