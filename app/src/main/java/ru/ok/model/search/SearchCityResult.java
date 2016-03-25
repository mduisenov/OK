package ru.ok.model.search;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;

public class SearchCityResult implements Parcelable {
    public static final Creator<SearchCityResult> CREATOR;
    public long id;
    public int matchLength;
    public int matchStart;
    public String name;
    public ArrayList<String> parents;

    /* renamed from: ru.ok.model.search.SearchCityResult.1 */
    static class C15791 implements Creator<SearchCityResult> {
        C15791() {
        }

        public SearchCityResult createFromParcel(Parcel in) {
            return new SearchCityResult(in);
        }

        public SearchCityResult[] newArray(int size) {
            return new SearchCityResult[size];
        }
    }

    public SearchCityResult(long id, String name, int matchLength, int matchStart, ArrayList<String> parents) {
        this.id = id;
        this.name = name;
        this.matchLength = matchLength;
        this.matchStart = matchStart;
        this.parents = parents;
    }

    protected SearchCityResult(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.matchLength = in.readInt();
        this.matchStart = in.readInt();
        this.parents = in.createStringArrayList();
    }

    public String getCitySummary() {
        StringBuilder result = new StringBuilder(this.name);
        if (this.parents != null && this.parents.size() > 0) {
            result.append(", ").append((String) this.parents.get(this.parents.size() - 1));
        }
        return result.toString();
    }

    static {
        CREATOR = new C15791();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.matchLength);
        dest.writeInt(this.matchStart);
        dest.writeStringList(this.parents);
    }

    public String toString() {
        return getCitySummary();
    }
}
