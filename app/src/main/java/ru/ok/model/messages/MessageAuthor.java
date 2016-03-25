package ru.ok.model.messages;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class MessageAuthor implements Parcelable {
    public static final Creator<MessageAuthor> CREATOR;
    private final String _id;
    private final String _type;

    /* renamed from: ru.ok.model.messages.MessageAuthor.1 */
    static class C15411 implements Creator<MessageAuthor> {
        C15411() {
        }

        public MessageAuthor createFromParcel(Parcel source) {
            return new MessageAuthor(source.readString(), source.readString());
        }

        public MessageAuthor[] newArray(int size) {
            return new MessageAuthor[size];
        }
    }

    public MessageAuthor(String id, String type) {
        this._id = id;
        this._type = type;
    }

    public String getId() {
        return this._id;
    }

    public String getType() {
        return this._type;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this._type);
    }

    static {
        CREATOR = new C15411();
    }
}
