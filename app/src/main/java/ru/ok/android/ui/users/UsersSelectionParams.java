package ru.ok.android.ui.users;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Collection;
import java.util.Set;
import ru.ok.java.api.utils.ObjectUtils;

public class UsersSelectionParams implements Parcelable {
    public static final Creator<UsersSelectionParams> CREATOR;
    private transient int hashCode;
    protected final int maxSelectedCount;
    protected final Set<String> selectedIds;

    /* renamed from: ru.ok.android.ui.users.UsersSelectionParams.1 */
    static class C12891 implements Creator<UsersSelectionParams> {
        C12891() {
        }

        public UsersSelectionParams createFromParcel(Parcel source) {
            return new UsersSelectionParams(source);
        }

        public UsersSelectionParams[] newArray(int size) {
            return new UsersSelectionParams[size];
        }
    }

    public UsersSelectionParams() {
        this(0);
    }

    public UsersSelectionParams(int maxSelectedCount) {
        this(null, maxSelectedCount);
    }

    public UsersSelectionParams(Collection<String> selectedIds, int maxSelectedCount) {
        this.selectedIds = ObjectUtils.unmodifiableCopy(selectedIds);
        this.maxSelectedCount = maxSelectedCount;
    }

    public boolean isEnabled(String id) {
        return true;
    }

    public int getMaxSelectedCount() {
        return this.maxSelectedCount;
    }

    public Set<String> getSelectedIds() {
        return this.selectedIds;
    }

    public int describeContents() {
        return 0;
    }

    public int hashCode() {
        int hashCode = this.hashCode;
        if (hashCode == 0) {
            hashCode = (1202386039 * ObjectUtils.collectionHashCode(this.selectedIds)) + (542980553 * this.maxSelectedCount);
            if (hashCode == 0) {
                hashCode = 1;
            }
            this.hashCode = hashCode;
        }
        return hashCode;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        UsersSelectionParams other = (UsersSelectionParams) o;
        if (ObjectUtils.setsEqual(this.selectedIds, other.selectedIds) && this.maxSelectedCount == other.maxSelectedCount) {
            return true;
        }
        return false;
    }

    public void writeToParcel(Parcel dest, int flags) {
        ObjectUtils.writeSet(this.selectedIds, dest);
        dest.writeInt(this.maxSelectedCount);
    }

    UsersSelectionParams(Parcel src) {
        this.selectedIds = ObjectUtils.readUnmodifiableStringSet(src, UsersSelectionParams.class.getClassLoader());
        this.maxSelectedCount = src.readInt();
    }

    static {
        CREATOR = new C12891();
    }
}
