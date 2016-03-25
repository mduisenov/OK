package ru.ok.android.model.pagination.impl;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import ru.ok.android.model.pagination.PageAnchor;
import ru.ok.java.api.request.paging.PagingAnchor;

public class ItemIdPageAnchor implements PageAnchor {
    public static final Creator<ItemIdPageAnchor> CREATOR;
    private final String firstItemId;
    private final String lastItemId;

    /* renamed from: ru.ok.android.model.pagination.impl.ItemIdPageAnchor.1 */
    static class C03751 implements Creator<ItemIdPageAnchor> {
        C03751() {
        }

        public ItemIdPageAnchor createFromParcel(Parcel source) {
            return new ItemIdPageAnchor(null);
        }

        public ItemIdPageAnchor[] newArray(int size) {
            return new ItemIdPageAnchor[size];
        }
    }

    public ItemIdPageAnchor(@NonNull String firstItemId, @NonNull String lastItemId) {
        this.firstItemId = PagingAnchor.buildAnchor(firstItemId);
        this.lastItemId = PagingAnchor.buildAnchor(lastItemId);
    }

    private ItemIdPageAnchor(Parcel source) {
        this.firstItemId = source.readString();
        this.lastItemId = source.readString();
    }

    @NonNull
    public String getBackwardAnchor() {
        return this.firstItemId;
    }

    @NonNull
    public String getForwardAnchor() {
        return this.lastItemId;
    }

    public String toString() {
        return "ItemIdPageAnchor{firstItemId='" + this.firstItemId + '\'' + ", lastItemId='" + this.lastItemId + '\'' + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstItemId);
        dest.writeString(this.lastItemId);
    }

    static {
        CREATOR = new C03751();
    }
}
