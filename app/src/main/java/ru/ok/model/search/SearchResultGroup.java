package ru.ok.model.search;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.model.GroupInfo;

public class SearchResultGroup extends SearchResult {
    public static final Creator<SearchResultGroup> CREATOR;
    private GroupInfo groupInfo;

    /* renamed from: ru.ok.model.search.SearchResultGroup.1 */
    static class C15881 implements Creator<SearchResultGroup> {
        C15881() {
        }

        public SearchResultGroup createFromParcel(Parcel src) {
            SearchResultGroup group = new SearchResultGroup();
            group.readFromParcel(src);
            return group;
        }

        public SearchResultGroup[] newArray(int count) {
            return new SearchResultGroup[count];
        }
    }

    public GroupInfo getGroupInfo() {
        return this.groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public SearchType getType() {
        return SearchType.GROUP;
    }

    public final void readFromParcel(Parcel src) {
        super.readFromParcel(src);
        this.groupInfo = (GroupInfo) src.readParcelable(GroupInfo.class.getClassLoader());
    }

    public final void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.groupInfo, 0);
    }

    static {
        CREATOR = new C15881();
    }
}
