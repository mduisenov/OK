package ru.ok.android.ui.stream.suggestions;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.onelog.search.SearchSuggestionType;
import ru.ok.onelog.search.SearchSuggestionsUsageFactory;

public class SearchSuggestionsHolder extends ViewHolder {
    private Activity activity;
    private Context context;
    View findByNameView;
    View findByPhonebookView;
    View findColleaguesView;
    View findFriendsFromSchoolView;
    private Fragment fragment;

    /* renamed from: ru.ok.android.ui.stream.suggestions.SearchSuggestionsHolder.1 */
    class C12581 implements OnClickListener {
        C12581() {
        }

        public void onClick(View view) {
            NavigationHelper.showSearchPage(SearchSuggestionsHolder.this.activity, null);
            OneLog.log(SearchSuggestionsUsageFactory.get(SearchSuggestionType.simple_search));
        }
    }

    /* renamed from: ru.ok.android.ui.stream.suggestions.SearchSuggestionsHolder.2 */
    class C12592 implements OnClickListener {
        C12592() {
        }

        public void onClick(View v) {
            NavigationHelper.showSearchClassmatesFragment(SearchSuggestionsHolder.this.activity);
            OneLog.log(SearchSuggestionsUsageFactory.get(SearchSuggestionType.search_classmates));
        }
    }

    /* renamed from: ru.ok.android.ui.stream.suggestions.SearchSuggestionsHolder.3 */
    class C12603 implements OnClickListener {
        C12603() {
        }

        public void onClick(View v) {
            NavigationHelper.showSearchColleaguesFragment(SearchSuggestionsHolder.this.activity);
            OneLog.log(SearchSuggestionsUsageFactory.get(SearchSuggestionType.search_colleagues));
        }
    }

    /* renamed from: ru.ok.android.ui.stream.suggestions.SearchSuggestionsHolder.4 */
    class C12614 implements OnClickListener {
        C12614() {
        }

        public void onClick(View v) {
            NavigationHelper.showRecommendedUsersPage(SearchSuggestionsHolder.this.activity);
            OneLog.log(SearchSuggestionsUsageFactory.get(SearchSuggestionType.search_by_phonebook));
        }
    }

    public static View createView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903499, parent, false);
    }

    public SearchSuggestionsHolder(View itemView) {
        super(itemView);
        this.findFriendsFromSchoolView = itemView.findViewById(2131625368);
        this.findColleaguesView = itemView.findViewById(2131625369);
        this.findByNameView = itemView.findViewById(2131625370);
        this.findByPhonebookView = itemView.findViewById(2131625371);
    }

    public SearchSuggestionsHolder(View view, Activity activity, Fragment fragment) {
        this(view);
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.fragment = fragment;
        attachListeners();
    }

    private void attachListeners() {
        OnClickListener clickListener = new C12581();
        this.findFriendsFromSchoolView.setOnClickListener(new C12592());
        this.findColleaguesView.setOnClickListener(new C12603());
        this.findByNameView.setOnClickListener(clickListener);
        this.findByPhonebookView.setOnClickListener(new C12614());
    }
}
