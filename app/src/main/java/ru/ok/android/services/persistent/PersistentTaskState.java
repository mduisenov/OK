package ru.ok.android.services.persistent;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public enum PersistentTaskState implements Parcelable {
    SUBMITTED,
    PAUSED,
    EXECUTING,
    COMPLETED,
    FAILED,
    ERROR,
    WAIT_INTERNET,
    WAIT_EXTERNAL_STORAGE,
    WAIT,
    CANCELED;
    
    public static final Creator<PersistentTaskState> CREATOR;
    private static final long serialVersionUID = 1;

    /* renamed from: ru.ok.android.services.persistent.PersistentTaskState.1 */
    static class C04451 implements Creator<PersistentTaskState> {
        C04451() {
        }

        public PersistentTaskState createFromParcel(Parcel source) {
            int ordinal = source.readInt();
            if (ordinal < 0 || ordinal >= PersistentTaskState.values().length) {
                return PersistentTaskState.ERROR;
            }
            return PersistentTaskState.values()[ordinal];
        }

        public PersistentTaskState[] newArray(int size) {
            return new PersistentTaskState[size];
        }
    }

    static {
        CREATOR = new C04451();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }
}
