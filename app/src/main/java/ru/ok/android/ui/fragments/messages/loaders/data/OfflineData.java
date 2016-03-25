package ru.ok.android.ui.fragments.messages.loaders.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import ru.ok.android.db.base.OfflineTable.Status;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;

public final class OfflineData implements Parcelable {
    public static final Creator<OfflineData> CREATOR;
    public final int databaseId;
    public final ErrorType errorType;
    public Status status;

    /* renamed from: ru.ok.android.ui.fragments.messages.loaders.data.OfflineData.1 */
    static class C08861 implements Creator<OfflineData> {
        C08861() {
        }

        public OfflineData createFromParcel(Parcel source) {
            return new OfflineData(source.readInt(), Status.valueOf(source.readString()), ErrorType.safeValueOf(source.readString()));
        }

        public OfflineData[] newArray(int size) {
            return new OfflineData[size];
        }
    }

    public OfflineData(int databaseId, Status status, ErrorType errorType) {
        this.databaseId = databaseId;
        this.status = status;
        this.errorType = errorType;
    }

    public String toString() {
        return "OfflineData{databaseId=" + this.databaseId + ", status=" + this.status + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.databaseId);
        dest.writeString(this.status.name());
        dest.writeString(this.errorType != null ? this.errorType.name() : null);
    }

    static {
        CREATOR = new C08861();
    }
}
