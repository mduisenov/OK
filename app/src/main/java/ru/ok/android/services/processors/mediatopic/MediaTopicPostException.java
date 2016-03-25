package ru.ok.android.services.processors.mediatopic;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.ok.android.services.persistent.TaskServerErrorException;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;
import ru.ok.java.api.utils.ObjectUtils;

public class MediaTopicPostException extends TaskServerErrorException {
    public static final Creator<MediaTopicPostException> CREATOR;
    private static final long serialVersionUID = -7893206269501745570L;
    private int blockLimit;
    private transient int hashCode;
    private List<String> privacyRestrictionUids;

    /* renamed from: ru.ok.android.services.processors.mediatopic.MediaTopicPostException.1 */
    static class C04651 implements Creator<MediaTopicPostException> {
        C04651() {
        }

        public MediaTopicPostException createFromParcel(Parcel source) {
            return new MediaTopicPostException(source);
        }

        public MediaTopicPostException[] newArray(int size) {
            return new MediaTopicPostException[size];
        }
    }

    MediaTopicPostException(int errorCode, String message, Throwable e, List<String> privacyRestrictionUids, int blockLimit) {
        if (errorCode == 11 && !(e instanceof ImageUploadException)) {
            errorCode = 999;
        }
        super(errorCode, message, e);
        this.hashCode = 0;
        if (privacyRestrictionUids == null) {
            this.privacyRestrictionUids = Collections.emptyList();
        } else {
            this.privacyRestrictionUids = Collections.unmodifiableList(privacyRestrictionUids);
        }
        this.blockLimit = blockLimit;
    }

    MediaTopicPostException(int errorCode, String message) {
        this(errorCode, message, null, null, -1);
    }

    MediaTopicPostException(int errorCode) {
        this(errorCode, null, null, null, -1);
    }

    MediaTopicPostException(int errorCode, Throwable e) {
        this(errorCode, null, e, null, -1);
    }

    public List<String> getPrivacyRestrictionUids() {
        return this.privacyRestrictionUids;
    }

    public String toString() {
        return "MediaTopicPostException[errorCode=" + getErrorCode() + " message=" + getMessage() + " cause=" + getCause() + (!this.privacyRestrictionUids.isEmpty() ? " restricted UID=" + this.privacyRestrictionUids : "") + " blockLimit=" + this.blockLimit + "]";
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        MediaTopicPostException other = (MediaTopicPostException) o;
        if (!ObjectUtils.listsEqual(this.privacyRestrictionUids, other.privacyRestrictionUids) || this.blockLimit != other.blockLimit) {
            return false;
        }
        Throwable cause1 = getCause();
        if (!(cause1 instanceof ImageUploadException) || cause1.equals(other.getCause())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hashCode = this.hashCode;
        if (hashCode == 0) {
            hashCode = super.hashCode();
            Throwable cause = getCause();
            if (cause instanceof ImageUploadException) {
                hashCode += 1290837293 * cause.hashCode();
            }
            hashCode = (hashCode + (140872341 * ObjectUtils.collectionHashCode(this.privacyRestrictionUids))) + (424765471 * this.blockLimit);
            if (hashCode == 0) {
                hashCode = 1;
            }
            this.hashCode = hashCode;
        }
        return hashCode;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeStringList(this.privacyRestrictionUids);
        dest.writeInt(this.blockLimit);
    }

    protected MediaTopicPostException(Parcel source) {
        super(source);
        this.hashCode = 0;
        List<String> privacyRestrictedUids = new ArrayList();
        source.readStringList(privacyRestrictedUids);
        this.privacyRestrictionUids = Collections.unmodifiableList(privacyRestrictedUids);
        this.blockLimit = source.readInt();
    }

    static {
        CREATOR = new C04651();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.privacyRestrictionUids == null) {
            this.privacyRestrictionUids = Collections.emptyList();
        }
        if (this.blockLimit == 0) {
            this.blockLimit = -1;
        }
    }
}
