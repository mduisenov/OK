package ru.ok.android.services.persistent;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public class TaskException extends Exception implements Parcelable {
    public static final Creator<TaskException> CREATOR;
    private static final long serialVersionUID = 1;
    private final int errorCode;

    /* renamed from: ru.ok.android.services.persistent.TaskException.1 */
    static class C04461 implements Creator<TaskException> {
        C04461() {
        }

        public TaskException createFromParcel(Parcel source) {
            return null;
        }

        public TaskException[] newArray(int size) {
            return new TaskException[0];
        }
    }

    public TaskException(int errorCode, String detailMessage, Throwable cause) {
        super(detailMessage, cause);
        this.errorCode = errorCode;
    }

    protected TaskException(Parcel src) {
        super(src.readString(), getThrowable(src));
        this.errorCode = src.readInt();
    }

    private static Throwable getThrowable(Parcel parcel) {
        byte b = parcel.readByte();
        if (b == 2) {
            return new Throwable(parcel.readString());
        }
        if (b == 1) {
            return (TaskException) parcel.readParcelable(TaskException.class.getClassLoader());
        }
        return null;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        TaskException other = (TaskException) o;
        if (this.errorCode != other.errorCode || !TextUtils.equals(getMessage(), other.getMessage())) {
            return false;
        }
        boolean z;
        Throwable cause1 = getCause();
        Throwable cause2 = other.getCause();
        boolean z2 = cause1 != null;
        if (cause2 != null) {
            z = true;
        } else {
            z = false;
        }
        if (z2 != z) {
            return false;
        }
        if (cause1 != null) {
            if (cause1.getClass() != cause2.getClass()) {
                return false;
            }
            if (!(cause1 instanceof TaskException)) {
                return TextUtils.equals(cause1.getMessage(), cause2.getMessage());
            }
            if (!cause1.equals(cause2)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int i = 0;
        String message = getMessage();
        Throwable cause = getCause();
        int hashCode = (cause == null ? 0 : 512848417 * cause.getClass().getName().hashCode()) + ((this.errorCode * 134514637) + (message == null ? 0 : 324061753 * message.hashCode()));
        if (cause instanceof TaskException) {
            i = 897331163 * cause.hashCode();
        }
        return i + hashCode;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMessage());
        if (getCause() == null) {
            dest.writeByte((byte) 0);
        } else if (getCause() instanceof Parcelable) {
            dest.writeByte((byte) 1);
            dest.writeParcelable((Parcelable) getCause(), flags);
        } else {
            dest.writeByte((byte) 2);
            dest.writeString(getCause().getMessage());
        }
        dest.writeInt(this.errorCode);
    }

    static {
        CREATOR = new C04461();
    }
}
