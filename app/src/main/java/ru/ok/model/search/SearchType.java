package ru.ok.model.search;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public enum SearchType implements Parcelable {
    ALL,
    USER,
    GROUP,
    COMMUNITY,
    MUSIC,
    APP;
    
    public static final Creator<SearchType> CREATOR;

    /* renamed from: ru.ok.model.search.SearchType.1 */
    static class C15911 implements Creator<SearchType> {
        C15911() {
        }

        public SearchType createFromParcel(Parcel src) {
            return SearchType.values()[src.readInt()];
        }

        public SearchType[] newArray(int count) {
            return new SearchType[count];
        }
    }

    static {
        CREATOR = new C15911();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ordinal());
    }
}
