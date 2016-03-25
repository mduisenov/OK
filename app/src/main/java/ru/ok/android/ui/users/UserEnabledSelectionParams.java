package ru.ok.android.ui.users;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Collection;
import java.util.Set;
import ru.ok.java.api.utils.ObjectUtils;

public class UserEnabledSelectionParams extends UsersSelectionParams {
    public static final Creator<UserEnabledSelectionParams> CREATOR;
    protected final Set<String> enabledIds;
    private transient int hashCode;

    /* renamed from: ru.ok.android.ui.users.UserEnabledSelectionParams.1 */
    static class C12881 implements Creator<UserEnabledSelectionParams> {
        C12881() {
        }

        public UserEnabledSelectionParams createFromParcel(Parcel source) {
            return new UserEnabledSelectionParams(source);
        }

        public UserEnabledSelectionParams[] newArray(int size) {
            return new UserEnabledSelectionParams[size];
        }
    }

    public UserEnabledSelectionParams(Collection<String> selectedIds, Collection<String> enabledIds, int maxSelectedCount) {
        super(selectedIds, maxSelectedCount);
        this.enabledIds = ObjectUtils.unmodifiableCopy(enabledIds);
    }

    public UserEnabledSelectionParams(UsersSelectionParams params, Collection<String> enabledIds) {
        this(params.selectedIds, enabledIds, params.maxSelectedCount);
    }

    public boolean isEnabled(String id) {
        return this.enabledIds != null && this.enabledIds.contains(id);
    }

    public int hashCode() {
        int hashCode = this.hashCode;
        if (hashCode == 0) {
            hashCode = super.hashCode() + (1628628761 * ObjectUtils.collectionHashCode(this.enabledIds));
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
        return ObjectUtils.setsEqual(this.enabledIds, ((UserEnabledSelectionParams) o).enabledIds);
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        ObjectUtils.writeSet(this.enabledIds, dest);
    }

    UserEnabledSelectionParams(Parcel src) {
        super(src);
        this.enabledIds = ObjectUtils.readUnmodifiableStringSet(src, UserEnabledSelectionParams.class.getClassLoader());
    }

    static {
        CREATOR = new C12881();
    }
}
