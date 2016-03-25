package ru.ok.android.ui.fragments.messages.loaders.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import ru.ok.model.messages.MessageBase;

public final class RepliedToInfo<M extends MessageBase> implements Parcelable {
    public static final Creator<RepliedToInfo> CREATOR;
    public OfflineMessage<M> offlineMessage;
    public Status status;

    /* renamed from: ru.ok.android.ui.fragments.messages.loaders.data.RepliedToInfo.1 */
    static class C08891 implements Creator<RepliedToInfo> {
        C08891() {
        }

        public RepliedToInfo createFromParcel(Parcel source) {
            return new RepliedToInfo((OfflineMessage) source.readParcelable(OfflineMessage.class.getClassLoader()), Status.valueOf(source.readString()));
        }

        public RepliedToInfo[] newArray(int size) {
            return new RepliedToInfo[size];
        }
    }

    public enum Status {
        COLLAPSED,
        LOADING,
        EXPANDED
    }

    public RepliedToInfo(OfflineMessage<M> offlineMessage, Status status) {
        this.offlineMessage = offlineMessage;
        this.status = status;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.offlineMessage, flags);
        dest.writeString(this.status.name());
    }

    static {
        CREATOR = new C08891();
    }
}
