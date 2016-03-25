package ru.ok.android.ui.search.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.search.fragment.SearchFragment;
import ru.ok.android.ui.search.util.FragmentStatePagerAdapterExposed;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.search.SearchResults.SearchContext;
import ru.ok.model.search.SearchType;

public class SearchPagerAdapter extends FragmentStatePagerAdapterExposed {
    public static final int[] PAGES;

    static {
        PAGES = new int[]{0, 1, 2};
    }

    public SearchPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    public int getCount() {
        return PAGES.length;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case RECEIVED_VALUE:
                return SearchFragment.newInstance(true, SearchContext.ALL, SearchType.ALL);
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return SearchFragment.newInstance(false, SearchContext.ALL, SearchType.USER);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return SearchFragment.newInstance(false, SearchContext.ALL, SearchType.GROUP, SearchType.COMMUNITY);
            default:
                return null;
        }
    }

    public CharSequence getPageTitle(int position) {
        Context context = OdnoklassnikiApplication.getContext();
        switch (position) {
            case RECEIVED_VALUE:
                return LocalizationManager.getString(context, 2131166486);
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return LocalizationManager.getString(context, 2131166488);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return LocalizationManager.getString(context, 2131166487);
            default:
                return null;
        }
    }

    public void searchOnPosition(int position, String query, boolean hideResutsWithAnimation) {
        Logger.m173d("Search at position %d requested for query \"%s\"", Integer.valueOf(position), query);
        SearchFragment fragment = (SearchFragment) getFragmentAtPosition(position);
        if (fragment != null) {
            Logger.m173d("Found fragment at position %d", Integer.valueOf(position));
            fragment.search(query, false, hideResutsWithAnimation);
        }
    }
}
