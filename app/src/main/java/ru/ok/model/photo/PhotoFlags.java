package ru.ok.model.photo;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Serializable;
import java.util.Arrays;

public class PhotoFlags implements Parcelable, Serializable {
    public static final Creator<PhotoFlags> CREATOR;
    private static final PhotoFlags DEFAULT_FLAGS;
    private static final long serialVersionUID = 1;
    final int flags;

    /* renamed from: ru.ok.model.photo.PhotoFlags.1 */
    static class C15591 implements Creator<PhotoFlags> {
        C15591() {
        }

        public PhotoFlags createFromParcel(Parcel source) {
            return new PhotoFlags(source.readInt());
        }

        public PhotoFlags[] newArray(int size) {
            return new PhotoFlags[size];
        }
    }

    static {
        DEFAULT_FLAGS = new PhotoFlags(0);
        CREATOR = new C15591();
    }

    public PhotoFlags(int flags) {
        this.flags = flags;
    }

    public boolean hasFlags(int flagsMask) {
        return (this.flags & flagsMask) == flagsMask;
    }

    public static PhotoFlags create(String[] flags) {
        if (flags == null) {
            return DEFAULT_FLAGS;
        }
        int mask = 0;
        for (APIFlags apiFlag : APIFlags.values()) {
            if (Arrays.binarySearch(flags, apiFlag.value) >= 0) {
                mask |= apiFlag.mask;
            }
        }
        return new PhotoFlags(mask);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.flags);
    }
}
