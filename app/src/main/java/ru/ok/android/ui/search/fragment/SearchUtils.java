package ru.ok.android.ui.search.fragment;

import java.util.Arrays;
import ru.ok.model.search.SearchType;

final class SearchUtils {
    public static boolean typesMatch(SearchType[] ltypes, SearchType[] rtypes) {
        if (ltypes == rtypes) {
            return true;
        }
        if (ltypes == null || rtypes == null || ltypes.length != rtypes.length) {
            return false;
        }
        Arrays.sort(ltypes);
        Arrays.sort(rtypes);
        for (int i = 0; i < ltypes.length; i++) {
            SearchType e1 = ltypes[i];
            SearchType e2 = rtypes[i];
            if (e1 == null) {
                if (e2 != null) {
                    return false;
                }
            } else if (!e1.equals(e2)) {
                return false;
            }
        }
        return true;
    }
}
