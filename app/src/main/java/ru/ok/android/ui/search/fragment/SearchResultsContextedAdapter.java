package ru.ok.android.ui.search.fragment;

import android.content.Context;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ru.ok.model.search.SearchResult;
import ru.ok.model.search.SearchResult.SearchScope;

public class SearchResultsContextedAdapter extends SearchResultsAdapter {
    public SearchResultsContextedAdapter(Context context, List<SearchResult> searchResults) {
        super(context, searchResults);
    }

    protected void splitInGroups(ArrayList<SearchResult> results, ArrayList<SearchResultsGroup> groups) {
        SearchResultsGroup scopeMyGroup = null;
        SearchResultsGroup scopePortalGroup = null;
        Iterator i$ = results.iterator();
        while (i$.hasNext()) {
            SearchResultsGroup groupToAdd;
            SearchResult result = (SearchResult) i$.next();
            if (result.getScope() == SearchScope.OWN) {
                if (scopeMyGroup == null) {
                    scopeMyGroup = new SearchResultsGroup();
                    scopeMyGroup.scope = SearchScope.OWN;
                    scopeMyGroup.type = result.getType();
                    groups.add(0, scopeMyGroup);
                }
                groupToAdd = scopeMyGroup;
            } else {
                if (scopePortalGroup == null) {
                    scopePortalGroup = new SearchResultsGroup();
                    scopePortalGroup.scope = SearchScope.PORTAL;
                    scopePortalGroup.type = result.getType();
                    groups.add(scopePortalGroup);
                }
                groupToAdd = scopePortalGroup;
            }
            groupToAdd.results.add(result);
        }
    }
}
