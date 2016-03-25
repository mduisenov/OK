package ru.ok.android.ui.search.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import ru.ok.android.services.processors.SearchQuickProcessor;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.search.SearchCityResult;
import ru.ok.model.search.SearchResult;
import ru.ok.model.search.SearchResultCommunity;
import ru.ok.model.search.SearchResultCommunity.CommunityType;
import ru.ok.model.search.SearchResults.SearchContext;
import ru.ok.model.search.SearchType;

public class CommunityAutoCompleteAdapter extends SearchBaseAdapter<SearchResultCommunity> {
    SearchCityResult city;
    CommunityType communityType;
    Context context;

    public CommunityAutoCompleteAdapter(Context context, CommunityType communityType) {
        this.communityType = CommunityType.UNKNOWN;
        this.context = context;
        this.communityType = communityType;
    }

    public void setCity(SearchCityResult city) {
        this.city = city;
    }

    public long getItemId(int position) {
        return 0;
    }

    protected ArrayList<SearchResultCommunity> performFiltering(CharSequence constraint) throws Exception {
        ArrayList<SearchResultCommunity> result = new ArrayList();
        if (constraint != null) {
            StringBuilder query = new StringBuilder();
            if (this.city != null) {
                query.append(this.city.name).append(" ");
            }
            query.append(constraint);
            for (SearchResult searchResult : SearchQuickProcessor.performSearch(query.toString(), new SearchType[]{SearchType.COMMUNITY}, SearchContext.COMMUNITY, null, null, 10).getFound()) {
                if (searchResult instanceof SearchResultCommunity) {
                    SearchResultCommunity searchResultCommunity = (SearchResultCommunity) searchResult;
                    if (searchResultCommunity.getCommunityType() == this.communityType) {
                        result.add(searchResultCommunity);
                    }
                }
            }
        }
        return result;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        SearchResultCommunity community = (SearchResultCommunity) getItem(position);
        if (convertView == null) {
            textView = (TextView) LocalizationManager.inflate(this.context, 2130903425, parent, false);
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(community.getText());
        return textView;
    }
}
