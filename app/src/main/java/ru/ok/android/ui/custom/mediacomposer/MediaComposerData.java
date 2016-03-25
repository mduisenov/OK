package ru.ok.android.ui.custom.mediacomposer;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Serializable;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class MediaComposerData implements Parcelable, Serializable {
    public static final Creator<MediaComposerData> CREATOR;
    private static final long serialVersionUID = 1;
    public final String groupId;
    public MediaTopicMessage mediaTopicMessage;
    public MediaTopicType mediaTopicType;
    public boolean toStatus;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.MediaComposerData.1 */
    static class C06711 implements Creator<MediaComposerData> {
        C06711() {
        }

        public MediaComposerData createFromParcel(Parcel source) {
            return new MediaComposerData(null);
        }

        public MediaComposerData[] newArray(int size) {
            return new MediaComposerData[size];
        }
    }

    private MediaComposerData(MediaTopicType type, String groupId, MediaTopicMessage mediaTopicMessage, boolean toStatus) {
        if (type == null) {
            type = MediaTopicType.USER;
        }
        this.mediaTopicType = type;
        this.groupId = groupId;
        this.mediaTopicMessage = mediaTopicMessage;
        this.toStatus = toStatus;
    }

    public static MediaComposerData user(boolean toStatus) {
        return new MediaComposerData(MediaTopicType.USER, null, null, toStatus);
    }

    public static MediaComposerData group(String groupId) {
        return new MediaComposerData(MediaTopicType.GROUP_THEME, groupId, null, false);
    }

    public static MediaComposerData groupSuggested(String groupId) {
        return new MediaComposerData(MediaTopicType.GROUP_SUGGESTED, groupId, null, false);
    }

    public static MediaComposerData user(MediaTopicMessage mediaTopicMessage, boolean toStatus) {
        return new MediaComposerData(MediaTopicType.USER, null, mediaTopicMessage, toStatus);
    }

    public static MediaComposerData group(String groupId, MediaTopicMessage mediaTopicMessage) {
        return new MediaComposerData(MediaTopicType.GROUP_THEME, groupId, mediaTopicMessage, false);
    }

    public static MediaComposerData groupSuggested(String groupId, MediaTopicMessage mediaTopicMessage) {
        return new MediaComposerData(MediaTopicType.GROUP_SUGGESTED, groupId, mediaTopicMessage, false);
    }

    public boolean isValid() {
        return (this.mediaTopicMessage == null || this.mediaTopicMessage.isEmpty()) ? false : true;
    }

    public String toString() {
        return "MediaComposerData[type=" + this.mediaTopicType + " groupId=" + this.groupId + " toStatus=" + this.toStatus + " message=" + this.mediaTopicMessage + "]";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mediaTopicType.ordinal());
        dest.writeParcelable(this.mediaTopicMessage, flags);
        dest.writeInt(this.toStatus ? 1 : 0);
        dest.writeString(this.groupId);
    }

    private MediaComposerData(Parcel src) {
        this.mediaTopicType = MediaTopicType.values()[src.readInt()];
        this.mediaTopicMessage = (MediaTopicMessage) src.readParcelable(MediaComposerData.class.getClassLoader());
        this.toStatus = src.readInt() != 0;
        this.groupId = src.readString();
    }

    static {
        CREATOR = new C06711();
    }
}
