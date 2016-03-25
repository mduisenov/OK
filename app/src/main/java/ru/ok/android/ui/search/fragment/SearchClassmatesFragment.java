package ru.ok.android.ui.search.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.plus.PlusShare;
import java.util.ArrayList;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.indicator.PagerSlidingTabStrip;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.ViewPagerDisable;

public class SearchClassmatesFragment extends BaseFragment {
    private PagerSlidingTabStrip indicator;
    private FragmentPagerAdapter pagerAdapter;
    private ViewPagerDisable viewPager;

    class CommunitiesPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<SearchByCommunityFragment> fragments;
        private String title;

        private SearchByCommunityFragment createFragment(int type) {
            SearchByCommunityFragment fragment = new SearchByCommunityFragment();
            Bundle args = new Bundle();
            args.putInt("type", type);
            if (this.title != null) {
                args.putString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, this.title);
            }
            fragment.setArguments(args);
            return fragment;
        }

        public CommunitiesPagerAdapter(FragmentManager fm) {
            super(fm);
            this.title = LocalizationManager.getString(SearchClassmatesFragment.this.getContext(), 2131166491);
            this.fragments = new ArrayList();
            this.fragments.add(createFragment(0));
            this.fragments.add(createFragment(1));
            this.fragments.add(createFragment(2));
        }

        public Fragment getItem(int position) {
            return (Fragment) this.fragments.get(position);
        }

        public int getCount() {
            return this.fragments.size();
        }

        public CharSequence getPageTitle(int position) {
            return ((SearchByCommunityFragment) getItem(position)).getPageTitle();
        }
    }

    protected CharSequence getTitle() {
        return LocalizationManager.getString(getContext(), 2131166491);
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LocalizationManager.inflate(getContext(), getLayoutId(), container, false);
        this.viewPager = (ViewPagerDisable) view.findViewById(2131625229);
        this.indicator = (PagerSlidingTabStrip) view.findViewById(C0263R.id.indicator);
        this.pagerAdapter = new CommunitiesPagerAdapter(getFragmentManager());
        this.viewPager.setOffscreenPageLimit(3);
        this.viewPager.setAdapter(this.pagerAdapter);
        this.viewPager.setCurrentItem(0);
        this.indicator.setViewPager(this.viewPager);
        return view;
    }

    protected int getLayoutId() {
        return 2130903424;
    }
}
