package ru.ok.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import ru.ok.android.utils.Logger;

public final class ConversationParticipantCapabilities implements Parcelable {
    public static final Creator<ConversationParticipantCapabilities> CREATOR;
    private static final ConversationParticipantCapabilities DEFAULT_CAPABILITIES;
    public final boolean canKick;

    /* renamed from: ru.ok.model.ConversationParticipantCapabilities.1 */
    static class C15111 implements Creator<ConversationParticipantCapabilities> {
        C15111() {
        }

        public ConversationParticipantCapabilities createFromParcel(Parcel parcel) {
            return new ConversationParticipantCapabilities(parcel.readInt() > 0);
        }

        public ConversationParticipantCapabilities[] newArray(int size) {
            return new ConversationParticipantCapabilities[size];
        }
    }

    static {
        DEFAULT_CAPABILITIES = new ConversationParticipantCapabilities(false);
        CREATOR = new C15111();
    }

    public ConversationParticipantCapabilities(boolean canKick) {
        this.canKick = canKick;
    }

    public static ConversationParticipantCapabilities create(String[] flags) {
        if (flags == null) {
            return DEFAULT_CAPABILITIES;
        }
        return new ConversationParticipantCapabilities(Arrays.binarySearch(flags, Logger.METHOD_D) >= 0);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.canKick ? 1 : 0);
    }
}
