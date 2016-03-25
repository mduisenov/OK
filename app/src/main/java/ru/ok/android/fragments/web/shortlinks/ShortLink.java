package ru.ok.android.fragments.web.shortlinks;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;
import ru.ok.java.api.utils.Utils;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.photo.PhotoInfo;

public class ShortLink {
    private final String shortLink;

    /* renamed from: ru.ok.android.fragments.web.shortlinks.ShortLink.1 */
    static /* synthetic */ class C03411 {
        static final /* synthetic */ int[] f66x72d467a8;
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$photo$PhotoAlbumInfo$OwnerType;

        static {
            f66x72d467a8 = new int[Type.values().length];
            try {
                f66x72d467a8[Type.USER_STATUS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f66x72d467a8[Type.GROUP_TOPIC.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f66x72d467a8[Type.USER_PHOTO.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f66x72d467a8[Type.GROUP_PHOTO.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f66x72d467a8[Type.USER_ALBUM.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f66x72d467a8[Type.GROUP_ALBUM.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f66x72d467a8[Type.GROUP_MOVIE.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f66x72d467a8[Type.MOVIE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f66x72d467a8[Type.SHARE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                f66x72d467a8[Type.HAPPENING_TOPIC.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                f66x72d467a8[Type.USER_FORUM.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                f66x72d467a8[Type.SCHOOL_FORUM.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                f66x72d467a8[Type.CITY_NEWS.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                f66x72d467a8[Type.UNKNOWN.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            $SwitchMap$ru$ok$model$photo$PhotoAlbumInfo$OwnerType = new int[OwnerType.values().length];
            try {
                $SwitchMap$ru$ok$model$photo$PhotoAlbumInfo$OwnerType[OwnerType.UNKNOWN.ordinal()] = 1;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$ru$ok$model$photo$PhotoAlbumInfo$OwnerType[OwnerType.GROUP.ordinal()] = 2;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$ru$ok$model$photo$PhotoAlbumInfo$OwnerType[OwnerType.USER.ordinal()] = 3;
            } catch (NoSuchFieldError e17) {
            }
        }
    }

    private ShortLink() {
        this(null);
    }

    private ShortLink(String shortLink) {
        this.shortLink = shortLink;
    }

    public static ShortLink createPhotoLink(@NonNull PhotoInfo photoInfo, @NonNull PhotoOwner photoOwner) {
        if (TextUtils.isEmpty(photoInfo.getId()) || TextUtils.isEmpty(photoOwner.getId())) {
            Logger.m176e("Photo ID can't be empty");
            return new ShortLink();
        }
        switch (photoOwner.getType()) {
            case RECEIVED_VALUE:
                if (TextUtils.isEmpty(photoInfo.getAlbumId())) {
                    return buildShortLink("profile", photoOwner.getId(), "pphotos", photoInfo.getId());
                }
                return buildShortLink("profile", photoOwner.getId(), "album", photoInfo.getAlbumId(), photoInfo.getId());
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (TextUtils.isEmpty(photoInfo.getMediaTopicId())) {
                    return buildShortLink("group", photoOwner.getId(), "album", photoInfo.getAlbumId(), photoInfo.getId());
                } else if (photoOwner == null || photoOwner.getType() != 1 || TextUtils.isEmpty(photoOwner.getId())) {
                    return buildShortLink("group", photoOwner.getId(), "topic", photoInfo.getMediaTopicId());
                } else {
                    return buildShortLink("group", photoOwner.getId(), "topic", photoInfo.getMediaTopicId());
                }
            default:
                return new ShortLink();
        }
    }

    public static ShortLink createAlbumLink(@NonNull PhotoAlbumInfo albumInfo, @Nullable PhotoOwner photoOwner) {
        switch (C03411.$SwitchMap$ru$ok$model$photo$PhotoAlbumInfo$OwnerType[albumInfo.getOwnerType().ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (!(albumInfo.getMainPhotoInfo() == null || photoOwner == null)) {
                    if ("tags".equals(albumInfo.getId())) {
                        return buildShortLink("profile", photoOwner.getId(), "pins");
                    }
                    return buildShortLink("profile", photoOwner.getId(), "pphotos");
                }
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return buildShortLink("group", albumInfo.getGroupId(), "album", albumInfo.getId());
            case Message.TYPE_FIELD_NUMBER /*3*/:
                if (TextUtils.isEmpty(albumInfo.getId())) {
                    return buildShortLink("profile", albumInfo.getUserId(), "pphotos");
                }
                return buildShortLink("profile", albumInfo.getUserId(), "album", albumInfo.getId());
        }
        return new ShortLink();
    }

    public static ShortLink createDiscussionLink(@NonNull DiscussionInfoResponse info) {
        DiscussionGeneralInfo discussionInfo = info.generalInfo;
        switch (C03411.f66x72d467a8[discussionInfo.type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return buildShortLink("profile", discussionInfo.topicOwnerId, "statuses", discussionInfo.id);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return buildShortLink("group", discussionInfo.topicOwnerId, "topic", discussionInfo.id);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                if (info.albumInfo != null) {
                    return buildShortLink("profile", info.albumInfo.getUserId(), "album", info.albumInfo.getId(), discussionInfo.id);
                } else if (info.photoInfo != null) {
                    return buildShortLink("profile", info.photoInfo.getOwnerId(), "pphotos", info.photoInfo.getId());
                }
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                if (discussionInfo.group != null) {
                    return buildShortLink("group", discussionInfo.group.id, "album", info.albumInfo.getId(), discussionInfo.id);
                }
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                if (info.albumInfo != null) {
                    return buildShortLink("profile", info.albumInfo.getUserId(), "album", info.albumInfo.getId());
                }
                break;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                if (info.albumInfo != null) {
                    return buildShortLink("group", info.albumInfo.getUserId(), "album", info.albumInfo.getId());
                }
                break;
            case Message.TASKID_FIELD_NUMBER /*8*/:
                if (info.videoInfo != null) {
                    return buildShortLink("video", info.videoInfo.id);
                }
                break;
        }
        return new ShortLink();
    }

    public static ShortLink createUserProfileLink(@NonNull String userId) {
        return buildShortLink("profile", userId);
    }

    public static ShortLink createGroupProfileLink(@NonNull String groupId) {
        return buildShortLink("group", groupId);
    }

    public static ShortLink createTrackLink(long trackId) {
        return buildShortLink("music", "track", Utils.getXoredIdSafe(Long.toString(trackId)));
    }

    public void copy(Context context, boolean showToast) {
        if (!TextUtils.isEmpty(this.shortLink)) {
            if (showToast) {
                Toast.makeText(context, this.shortLink, 0).show();
            }
            copyToClipboard(context);
        }
    }

    public String toString() {
        return this.shortLink;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(this.shortLink);
    }

    private void copyToClipboard(Context context) {
        ((ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText(this.shortLink, this.shortLink));
    }

    private static ShortLink buildShortLink(String... segments) {
        if (segments.length == 0) {
            throw new IllegalArgumentException("Segments can't have 0 length");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("http://ok.ru");
        for (String segment : segments) {
            if (segment == null) {
                return new ShortLink();
            }
            sb.append("/");
            sb.append(Utils.getXoredIdSafe(segment));
        }
        return new ShortLink(sb.toString());
    }
}
