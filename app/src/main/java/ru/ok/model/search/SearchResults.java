package ru.ok.model.search;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;

public class SearchResults implements Parcelable, Cloneable {
    public static final Creator<SearchResults> CREATOR;
    private String anchor;
    private List<SearchResult> found;
    private boolean hasMore;
    private SearchContext searchContext;

    /* renamed from: ru.ok.model.search.SearchResults.1 */
    static class C15901 implements Creator<SearchResults> {
        C15901() {
        }

        public SearchResults createFromParcel(Parcel src) {
            SearchResults searchResults = new SearchResults();
            searchResults.readFromParcel(src);
            return searchResults;
        }

        public SearchResults[] newArray(int count) {
            return new SearchResults[count];
        }
    }

    public enum SearchContext {
        ALL,
        USER,
        GROUP,
        COMMUNITY,
        MUSIC,
        APP
    }

    public SearchResults() {
        this.searchContext = SearchContext.ALL;
    }

    public boolean isHasMore() {
        return this.hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public SearchContext getSearchContext() {
        return this.searchContext;
    }

    public void setSearchContext(SearchContext searchContext) {
        this.searchContext = searchContext;
    }

    public List<SearchResult> getFound() {
        if (this.found == null) {
            this.found = new ArrayList();
        }
        return this.found;
    }

    public SearchResults clone() {
        SearchResults cloned = new SearchResults();
        copyTo(cloned);
        return cloned;
    }

    public void copyTo(SearchResults dest) {
        dest.hasMore = this.hasMore;
        dest.anchor = this.anchor;
        dest.searchContext = this.searchContext;
        dest.found = new ArrayList(this.found);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (this.hasMore ? 1 : 0));
        dest.writeString(this.anchor);
        if (this.searchContext != null) {
            dest.writeInt(this.searchContext.ordinal());
        }
        dest.writeList(getFound());
    }

    public void readFromParcel(Parcel src) {
        this.hasMore = src.readByte() > null;
        this.anchor = src.readString();
        this.searchContext = SearchContext.values()[src.readInt()];
        this.found = new ArrayList();
        src.readList(this.found, SearchResult.class.getClassLoader());
    }

    static {
        CREATOR = new C15901();
    }
}
