package ru.ok.android.ui.groups.loaders;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import ru.ok.android.services.processors.SearchQuickProcessor;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.model.GroupInfo;
import ru.ok.model.search.SearchResult;
import ru.ok.model.search.SearchResult.SearchScope;
import ru.ok.model.search.SearchResultGroup;
import ru.ok.model.search.SearchResults;
import ru.ok.model.search.SearchResults.SearchContext;
import ru.ok.model.search.SearchType;

public class GroupsSearchLoader extends BaseGroupsPageLoader {
    private String query;
    private Comparator<? super SearchResult> scopeComparator;

    /* renamed from: ru.ok.android.ui.groups.loaders.GroupsSearchLoader.1 */
    class C09411 implements Comparator<SearchResult> {
        C09411() {
        }

        public int compare(SearchResult lhs, SearchResult rhs) {
            boolean lhOwn;
            boolean rhOwn;
            if (lhs.getScope() == SearchScope.OWN) {
                lhOwn = true;
            } else {
                lhOwn = false;
            }
            if (rhs.getScope() == SearchScope.OWN) {
                rhOwn = true;
            } else {
                rhOwn = false;
            }
            if (lhOwn == rhOwn) {
                return 0;
            }
            if (lhOwn) {
                return -1;
            }
            return 1;
        }
    }

    public static class HeaderFakeGroupInfo extends GroupInfo {
        public static final Creator<HeaderFakeGroupInfo> CREATOR;
        public static HeaderFakeGroupInfo OWN;
        public static HeaderFakeGroupInfo PORTAL;
        public final int headerType;

        /* renamed from: ru.ok.android.ui.groups.loaders.GroupsSearchLoader.HeaderFakeGroupInfo.1 */
        static class C09421 implements Creator<HeaderFakeGroupInfo> {
            C09421() {
            }

            public HeaderFakeGroupInfo createFromParcel(Parcel source) {
                int i = 1;
                if (source.readInt() != 1) {
                    i = 0;
                }
                return new HeaderFakeGroupInfo(i);
            }

            public HeaderFakeGroupInfo[] newArray(int count) {
                return new HeaderFakeGroupInfo[count];
            }
        }

        public HeaderFakeGroupInfo(int headerType) {
            this.headerType = headerType;
        }

        public static HeaderFakeGroupInfo getOwnGroupsHeader() {
            if (OWN == null) {
                OWN = new HeaderFakeGroupInfo(1);
            }
            return OWN;
        }

        public static HeaderFakeGroupInfo getPortalGroupsHeader() {
            if (PORTAL == null) {
                PORTAL = new HeaderFakeGroupInfo(0);
            }
            return PORTAL;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.headerType);
        }

        static {
            CREATOR = new C09421();
        }
    }

    public GroupsSearchLoader(Context context, String query, String anchor, PagingDirection direction, int count) {
        super(context, anchor, direction, count);
        this.scopeComparator = new C09411();
        this.query = query;
    }

    public GroupsLoaderResult loadInBackground() {
        try {
            SearchResults searchResults = SearchQuickProcessor.performSearch(this.query, new SearchType[]{SearchType.GROUP}, SearchContext.GROUP, this.anchor, this.direction, this.count);
            List<GroupInfo> groupInfos = null;
            List<SearchResult> found = searchResults.getFound();
            if (!(found == null || found.isEmpty())) {
                Collections.sort(found, this.scopeComparator);
                groupInfos = new ArrayList();
                boolean headerProcessingCompleted = false;
                boolean previousScopeOwn = false;
                int size = found.size();
                for (int i = 0; i < size; i++) {
                    SearchResult searchResult = (SearchResult) found.get(i);
                    if (searchResult.getType() == SearchType.GROUP) {
                        if (!headerProcessingCompleted) {
                            boolean currentScopeOwn = searchResult.getScope() == SearchScope.OWN;
                            if (i == 0) {
                                if (currentScopeOwn) {
                                    groupInfos.add(HeaderFakeGroupInfo.getOwnGroupsHeader());
                                } else {
                                    headerProcessingCompleted = true;
                                }
                            } else if (!currentScopeOwn && previousScopeOwn) {
                                groupInfos.add(HeaderFakeGroupInfo.getPortalGroupsHeader());
                                headerProcessingCompleted = true;
                            }
                            previousScopeOwn = currentScopeOwn;
                        }
                        groupInfos.add(((SearchResultGroup) searchResult).getGroupInfo());
                    }
                }
            }
            return new GroupsLoaderResult(getLoadParams(), true, null, groupInfos, null, searchResults.getAnchor(), searchResults.isHasMore());
        } catch (Exception e) {
            return new GroupsLoaderResult(getLoadParams(), false, ErrorType.fromException(e));
        }
    }

    @NonNull
    private GroupsLoaderLoadParams getLoadParams() {
        return new GroupsLoaderLoadParams(this.anchor, this.direction, null);
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
