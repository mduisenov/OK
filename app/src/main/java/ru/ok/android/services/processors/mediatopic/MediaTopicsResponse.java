package ru.ok.android.services.processors.mediatopic;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import ru.ok.model.mediatopics.MediatopicWithEntityBuilders;

public class MediaTopicsResponse implements Parcelable {
    public static final Creator<MediaTopicsResponse> CREATOR;
    public final String anchor;
    public final String filter;
    public final boolean hasMore;
    public final boolean inconsistent;
    public final MediatopicWithEntityBuilders mediaTopicWithEntityBuilders;

    /* renamed from: ru.ok.android.services.processors.mediatopic.MediaTopicsResponse.1 */
    static class C04691 implements Creator<MediaTopicsResponse> {
        C04691() {
        }

        public MediaTopicsResponse createFromParcel(Parcel source) {
            return new MediaTopicsResponse(source);
        }

        public MediaTopicsResponse[] newArray(int size) {
            return new MediaTopicsResponse[size];
        }
    }

    public MediaTopicsResponse(MediatopicWithEntityBuilders mediatopicWithEntityBuilders, String filter, String anchor, boolean hasMore, boolean inconsistent) {
        this.mediaTopicWithEntityBuilders = mediatopicWithEntityBuilders;
        this.anchor = anchor;
        this.filter = filter;
        this.hasMore = hasMore;
        this.inconsistent = inconsistent;
    }

    public MediaTopicsResponse(Parcel src) {
        boolean z;
        boolean z2 = true;
        this.mediaTopicWithEntityBuilders = (MediatopicWithEntityBuilders) src.readParcelable(MediatopicWithEntityBuilders.class.getClassLoader());
        this.anchor = src.readString();
        this.filter = src.readString();
        if (src.readByte() != null) {
            z = true;
        } else {
            z = false;
        }
        this.hasMore = z;
        if (src.readByte() == null) {
            z2 = false;
        }
        this.inconsistent = z2;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        dest.writeParcelable(this.mediaTopicWithEntityBuilders, 0);
        dest.writeString(this.anchor);
        dest.writeString(this.filter);
        if (this.hasMore) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeByte((byte) i);
        if (!this.inconsistent) {
            i2 = 0;
        }
        dest.writeByte((byte) i2);
    }

    static {
        CREATOR = new C04691();
    }
}
