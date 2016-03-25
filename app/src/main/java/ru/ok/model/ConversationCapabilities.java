package ru.ok.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.jivesoftware.smackx.caps.EntityCapsManager;
import ru.ok.android.utils.Logger;

public final class ConversationCapabilities implements Parcelable {
    public static final Creator<ConversationCapabilities> CREATOR;
    public static final ConversationCapabilities DEFAULT_CAPABILITIES;
    public final boolean canDelete;
    public final boolean canPost;
    public final boolean canSendAudio;
    public final boolean canSendVideo;
    public final boolean cantPostBecauseOnlyFriendsAllowed;

    /* renamed from: ru.ok.model.ConversationCapabilities.1 */
    static class C15091 implements Creator<ConversationCapabilities> {
        C15091() {
        }

        public ConversationCapabilities createFromParcel(Parcel source) {
            boolean z = true;
            boolean z2 = source.readInt() > 0;
            boolean z3 = source.readInt() > 0;
            boolean z4 = source.readInt() > 0;
            boolean z5 = source.readInt() > 0;
            if (source.readInt() <= 0) {
                z = false;
            }
            return new ConversationCapabilities(z2, z3, z4, z5, z);
        }

        public ConversationCapabilities[] newArray(int size) {
            return new ConversationCapabilities[size];
        }
    }

    static {
        DEFAULT_CAPABILITIES = new ConversationCapabilities(true, true, false, true, true);
        CREATOR = new C15091();
    }

    public ConversationCapabilities(boolean canDelete, boolean canPost, boolean cantPostBecauseOnlyFriendsAllowed, boolean canSendAudio, boolean canSendVideo) {
        this.canDelete = canDelete;
        this.canPost = canPost;
        this.cantPostBecauseOnlyFriendsAllowed = cantPostBecauseOnlyFriendsAllowed;
        this.canSendAudio = canSendAudio;
        this.canSendVideo = canSendVideo;
    }

    public static ConversationCapabilities create(String[] chunks) {
        boolean z = true;
        if (chunks.length <= 0) {
            return DEFAULT_CAPABILITIES;
        }
        boolean z2 = Arrays.binarySearch(chunks, Logger.METHOD_D) >= 0;
        boolean z3 = Arrays.binarySearch(chunks, EntityCapsManager.ELEMENT) >= 0;
        boolean z4 = Arrays.binarySearch(chunks, "foc") >= 0;
        boolean z5 = Arrays.binarySearch(chunks, "auu") >= 0;
        if (Arrays.binarySearch(chunks, "vdu") < 0) {
            z = false;
        }
        return new ConversationCapabilities(z2, z3, z4, z5, z);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        dest.writeInt(this.canDelete ? 1 : 0);
        if (this.canPost) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (this.cantPostBecauseOnlyFriendsAllowed) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (this.canSendAudio) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (!this.canSendVideo) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }
}
