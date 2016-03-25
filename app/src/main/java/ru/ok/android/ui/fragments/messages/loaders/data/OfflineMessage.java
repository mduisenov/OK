package ru.ok.android.ui.fragments.messages.loaders.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.Comparator;
import java.util.List;
import ru.ok.model.messages.MessageBase;

public final class OfflineMessage<M extends MessageBase> implements Parcelable {
    public static final Creator<OfflineMessage<? extends MessageBase>> CREATOR;
    public static final Comparator<? super OfflineMessage<? extends MessageBase>> DATE_COMPARATOR;
    public final M message;
    public final OfflineData offlineData;
    public RepliedToInfo repliedToInfo;

    /* renamed from: ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage.1 */
    static class C08871 implements Comparator<OfflineMessage<? extends MessageBase>> {
        C08871() {
        }

        public int compare(OfflineMessage<? extends MessageBase> a, OfflineMessage<? extends MessageBase> b) {
            if (a.message.date < b.message.date) {
                return -1;
            }
            return a.message.date > b.message.date ? 1 : 0;
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage.2 */
    static class C08882 implements Creator<OfflineMessage<? extends MessageBase>> {
        C08882() {
        }

        public OfflineMessage<?> createFromParcel(Parcel source) {
            return new OfflineMessage((MessageBase) source.readParcelable(MessageBase.class.getClassLoader()), (OfflineData) source.readParcelable(OfflineData.class.getClassLoader()));
        }

        public OfflineMessage<?>[] newArray(int size) {
            return new OfflineMessage[size];
        }
    }

    static {
        DATE_COMPARATOR = new C08871();
        CREATOR = new C08882();
    }

    public OfflineMessage(M message, OfflineData offlineData) {
        this.message = message;
        this.offlineData = offlineData;
    }

    public boolean equals(Object o) {
        if (o == null || o.getClass() != OfflineMessage.class) {
            return false;
        }
        OfflineMessage om = (OfflineMessage) o;
        if (TextUtils.equals(this.message.id, om.message.id) && !TextUtils.isEmpty(this.message.id)) {
            return true;
        }
        if (this.offlineData == null || om.offlineData == null || this.offlineData.databaseId != om.offlineData.databaseId) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "OfflineMessage{message=" + this.message + " " + "offline=" + this.offlineData + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.message, flags);
        dest.writeParcelable(this.offlineData, flags);
    }

    public static <M extends MessageBase> OfflineMessage<M> findFirstWithServerMessage(List<OfflineMessage<M>> messages) {
        for (int i = 0; i < messages.size(); i++) {
            OfflineMessage<M> m = (OfflineMessage) messages.get(i);
            if (isMessageFromServer(m)) {
                return m;
            }
        }
        return null;
    }

    public static <M extends MessageBase> OfflineMessage<M> findLastWithServerMessage(List<OfflineMessage<M>> messages) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            OfflineMessage<M> m = (OfflineMessage) messages.get(i);
            if (isMessageFromServer(m)) {
                return m;
            }
        }
        return null;
    }

    private static <M extends MessageBase> boolean isMessageFromServer(OfflineMessage<M> m) {
        return m.message.hasServerId();
    }
}
