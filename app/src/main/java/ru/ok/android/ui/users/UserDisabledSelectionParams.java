package ru.ok.android.ui.users;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import ru.ok.java.api.utils.ObjectUtils;

public class UserDisabledSelectionParams extends UsersSelectionParams {
    public static final Creator<UserDisabledSelectionParams> CREATOR;
    protected final Set<String> disabledIds;
    private transient int hashCode;

    /* renamed from: ru.ok.android.ui.users.UserDisabledSelectionParams.1 */
    static class C12871 implements Creator<UserDisabledSelectionParams> {
        C12871() {
        }

        public UserDisabledSelectionParams createFromParcel(Parcel source) {
            return new UserDisabledSelectionParams(source);
        }

        public UserDisabledSelectionParams[] newArray(int size) {
            return new UserDisabledSelectionParams[size];
        }
    }

    public UserDisabledSelectionParams(Collection<String> selectedIds, Collection<String> disabledIds, int maxSelectedCount) {
        super(selectedIds, maxSelectedCount);
        this.disabledIds = ObjectUtils.unmodifiableCopy(disabledIds);
    }

    public ArrayList<String> getDisabledIds(ArrayList<String> out) {
        if (out == null) {
            return new ArrayList(this.disabledIds);
        }
        out.addAll(this.disabledIds);
        return out;
    }

    public boolean isEnabled(String id) {
        return this.disabledIds == null || !this.disabledIds.contains(id);
    }

    public int hashCode() {
        int hashCode = this.hashCode;
        if (hashCode == 0) {
            hashCode = super.hashCode() + (2074924151 * ObjectUtils.collectionHashCode(this.disabledIds));
            if (hashCode == 0) {
                hashCode = 1;
            }
            this.hashCode = hashCode;
        }
        return hashCode;
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        return ObjectUtils.setsEqual(this.disabledIds, ((UserDisabledSelectionParams) o).disabledIds);
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        ObjectUtils.writeSet(this.disabledIds, dest);
    }

    UserDisabledSelectionParams(Parcel src) {
        super(src);
        this.disabledIds = ObjectUtils.readUnmodifiableStringSet(src, UserDisabledSelectionParams.class.getClassLoader());
    }

    static {
        CREATOR = new C12871();
    }
}
