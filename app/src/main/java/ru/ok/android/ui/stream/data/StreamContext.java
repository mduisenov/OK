package ru.ok.android.ui.stream.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.proto.MessagesProto.Message;

public final class StreamContext implements Parcelable {
    public static final Creator<StreamContext> CREATOR;
    public final String id;
    public final String logContext;
    public final int type;

    /* renamed from: ru.ok.android.ui.stream.data.StreamContext.1 */
    static class C12301 implements Creator<StreamContext> {
        C12301() {
        }

        public StreamContext createFromParcel(Parcel source) {
            return new StreamContext(source);
        }

        public StreamContext[] newArray(int size) {
            return new StreamContext[size];
        }
    }

    public static boolean isValidType(int type) {
        return type == 1 || type == 2 || type == 3;
    }

    public StreamContext(int type, String id) {
        this.type = type;
        this.id = id;
        if (type == 2) {
            if (TextUtils.equals(id, OdnoklassnikiApplication.getCurrentUser().getId())) {
                this.logContext = "my-feed";
            } else {
                this.logContext = "user-feed";
            }
        } else if (type == 3) {
            this.logContext = "group-feed";
        } else {
            this.logContext = "main-feed";
        }
    }

    public static StreamContext stream() {
        return new StreamContext(1, null);
    }

    public static StreamContext userProfile(String uid) {
        return new StreamContext(2, uid);
    }

    public static StreamContext groupProfile(String gid) {
        return new StreamContext(3, gid);
    }

    public String getKey() {
        switch (this.type) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return "stream";
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return "user:" + this.id;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return "group:" + this.id;
            default:
                return "unknown-" + this.type + ":" + this.id;
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StreamContext)) {
            return false;
        }
        StreamContext other = (StreamContext) o;
        if (other.type == this.type && TextUtils.equals(other.id, this.id)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (this.id == null ? 0 : 72983461 * this.id.hashCode()) + (213452353 * this.type);
    }

    public String toString() {
        return getKey();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.id);
        dest.writeString(this.logContext);
    }

    StreamContext(Parcel src) {
        this.type = src.readInt();
        this.id = src.readString();
        this.logContext = src.readString();
    }

    static {
        CREATOR = new C12301();
    }
}
