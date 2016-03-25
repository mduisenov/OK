package ru.ok.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class ConversationParticipant implements Parcelable {
    public static final Creator<ConversationParticipant> CREATOR;
    public final ConversationParticipantCapabilities capabilities;
    public final String id;
    public long lastViewTime;

    /* renamed from: ru.ok.model.ConversationParticipant.1 */
    static class C15101 implements Creator<ConversationParticipant> {
        C15101() {
        }

        public ConversationParticipant createFromParcel(Parcel parcel) {
            return new ConversationParticipant(parcel.readString(), parcel.readLong(), (ConversationParticipantCapabilities) parcel.readParcelable(ConversationParticipant.class.getClassLoader()));
        }

        public ConversationParticipant[] newArray(int size) {
            return new ConversationParticipant[size];
        }
    }

    public ConversationParticipant(String id, long lastViewTime, ConversationParticipantCapabilities capabilities) {
        this.id = id;
        this.lastViewTime = lastViewTime;
        this.capabilities = capabilities;
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConversationParticipant that = (ConversationParticipant) o;
        if (this.id != null) {
            if (this.id.equals(that.id)) {
                return true;
            }
        } else if (that.id == null) {
            return true;
        }
        return false;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.id);
        parcel.writeLong(this.lastViewTime);
        parcel.writeParcelable(this.capabilities, flags);
    }

    static {
        CREATOR = new C15101();
    }
}
