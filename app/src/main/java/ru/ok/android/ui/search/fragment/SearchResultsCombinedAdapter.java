package ru.ok.android.ui.search.fragment;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import ru.ok.model.search.SearchResult;
import ru.ok.model.search.SearchResult.SearchScope;
import ru.ok.model.search.SearchType;

public class SearchResultsCombinedAdapter extends SearchResultsAdapter {

    /* renamed from: ru.ok.android.ui.search.fragment.SearchResultsCombinedAdapter.1 */
    class C11981 implements Comparator<SearchResultsGroup> {
        final /* synthetic */ SearchType val$relevantType;

        C11981(SearchType searchType) {
            this.val$relevantType = searchType;
        }

        public int compare(SearchResultsGroup lhs, SearchResultsGroup rhs) {
            if (lhs.type != rhs.type) {
                if (lhs.type == this.val$relevantType) {
                    return -1;
                }
                if (rhs.type == this.val$relevantType) {
                    return 1;
                }
            } else if (lhs.scope != rhs.scope) {
                if (lhs.scope == SearchScope.OWN) {
                    return -1;
                }
                if (rhs.scope == SearchScope.OWN) {
                    return 1;
                }
            }
            return 0;
        }
    }

    public SearchResultsCombinedAdapter(Context context, List<SearchResult> searchResults) {
        super(context, searchResults);
    }

    protected boolean shouldLoadMore(int position) {
        return false;
    }

    protected void splitInGroups(ArrayList<SearchResult> results, ArrayList<SearchResultsGroup> groups) {
        HashMap<String, SearchResultsGroup> groupsMap = new HashMap();
        Iterator i$ = results.iterator();
        while (i$.hasNext()) {
            SearchResult result = (SearchResult) i$.next();
            SearchResultsGroup group = getGroup(groupsMap, result.getType(), result.getScope());
            if (group.results.size() >= 10) {
                group.expandable = true;
            } else {
                group.results.add(result);
            }
        }
        groups.addAll(groupsMap.values());
        Collections.sort(groups, new C11981(((SearchResult) results.get(0)).getType()));
    }

    private SearchResultsGroup getGroup(HashMap<String, SearchResultsGroup> groupsMap, SearchType type, SearchScope scope) {
        String key = type.name() + (scope == SearchScope.OWN ? "own" : "portal");
        SearchResultsGroup group = (SearchResultsGroup) groupsMap.get(key);
        if (group != null) {
            return group;
        }
        group = new SearchResultsGroup();
        group.scope = scope;
        group.type = type;
        groupsMap.put(key, group);
        return group;
    }
}
