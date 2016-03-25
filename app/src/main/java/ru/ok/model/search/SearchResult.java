package ru.ok.model.search;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class SearchResult implements Parcelable {
    private SearchScope scope;
    private String text;
    private String typeMsg;

    public enum SearchScope {
        UNKNOWN,
        OWN,
        FRIENDS,
        PORTAL
    }

    public abstract SearchType getType();

    public SearchResult() {
        this.scope = SearchScope.UNKNOWN;
    }

    public void setTypeMsg(String typeMsg) {
        this.typeMsg = typeMsg;
    }

    public SearchScope getScope() {
        return this.scope;
    }

    public void setScope(SearchScope scope) {
        this.scope = scope;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.scope.ordinal());
        dest.writeString(this.typeMsg);
        dest.writeString(this.text);
    }

    public void readFromParcel(Parcel src) {
        this.scope = SearchScope.values()[src.readInt()];
        this.typeMsg = src.readString();
        this.text = src.readString();
    }

    public String toString() {
        return getText();
    }
}
