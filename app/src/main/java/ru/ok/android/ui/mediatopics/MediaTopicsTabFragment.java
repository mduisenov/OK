package ru.ok.android.ui.mediatopics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.custom.indicator.PagerSlidingTabStrip;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.groups.fragments.PagerSelectedListener;
import ru.ok.android.utils.localization.LocalizationManager;

public abstract class MediaTopicsTabFragment extends BaseFragment {
    protected String defaultSelectedFilter;
    protected String groupId;
    protected PagerSlidingTabStrip indicator;
    protected GroupTopicsPagerAdapter pagerAdapter;
    protected View shadow;
    protected String userId;
    protected ViewPager viewPager;

    public static class FilterPage {
        public final String filter;
        public final int pageTitleResId;

        public FilterPage(String filter, int pageTitleResId) {
            this.filter = filter;
            this.pageTitleResId = pageTitleResId;
        }
    }

    protected class GroupTopicsPagerAdapter extends FragmentPagerAdapter {
        private final MediaTopicsListFragment[] fragments;

        public GroupTopicsPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new MediaTopicsListFragment[MediaTopicsTabFragment.this.getPagesMaxCount()];
        }

        public CharSequence getPageTitle(int position) {
            int retResId = ((FilterPage) MediaTopicsTabFragment.this.getPages().get(position)).pageTitleResId;
            return retResId != 0 ? LocalizationManager.getString(MediaTopicsTabFragment.this.getContext(), retResId) : "";
        }

        public MediaTopicsListFragment getItem(int position) {
            return MediaTopicsTabFragment.this.getPageFragment((FilterPage) MediaTopicsTabFragment.this.getPages().get(position));
        }

        public Object instantiateItem(ViewGroup container, int position) {
            MediaTopicsListFragment fragment = (MediaTopicsListFragment) super.instantiateItem(container, position);
            this.fragments[position] = fragment;
            return fragment;
        }

        public int getCount() {
            return MediaTopicsTabFragment.this.getPagesCount();
        }

        public MediaTopicsListFragment[] getFragments() {
            return this.fragments;
        }
    }

    private class PageSelectedFixPageChangeListener implements OnPageChangeListener {
        private boolean dontLoadList;
        private boolean initialSelection;
        private int positionCurrent;

        /* renamed from: ru.ok.android.ui.mediatopics.MediaTopicsTabFragment.PageSelectedFixPageChangeListener.1 */
        class C10391 implements Runnable {
            C10391() {
            }

            public void run() {
                if (!PageSelectedFixPageChangeListener.this.dontLoadList) {
                    PageSelectedFixPageChangeListener.this.selectCurrentPage();
                }
            }
        }

        private PageSelectedFixPageChangeListener() {
            this.initialSelection = false;
        }

        private void selectCurrentPage() {
            MediaTopicsTabFragment.this.onPageSelected(this.positionCurrent);
            PagerSelectedListener selectedPageListener = null;
            for (int i = 0; i < MediaTopicsTabFragment.this.pagerAdapter.getFragments().length; i++) {
                Fragment fragment = MediaTopicsTabFragment.this.pagerAdapter.getFragments()[i];
                if (fragment != null && (fragment instanceof PagerSelectedListener)) {
                    PagerSelectedListener listener = (PagerSelectedListener) fragment;
                    if (i == this.positionCurrent) {
                        selectedPageListener = listener;
                    } else {
                        listener.onPageNotSelected();
                    }
                }
            }
            if (selectedPageListener != null) {
                selectedPageListener.onPageSelected();
            }
        }

        private void fireFragmentsPageScrolled(int position, float positionOffset) {
            Fragment fragment = MediaTopicsTabFragment.this.pagerAdapter.getFragments()[this.positionCurrent];
            if (fragment != null && (fragment instanceof PagerSelectedListener)) {
                ((PagerSelectedListener) fragment).onPageScrolledOffset((((float) position) + positionOffset) - ((float) this.positionCurrent));
            }
        }

        public void onPageScrollStateChanged(int state) {
            if (state == 0) {
                MediaTopicsTabFragment.this.viewPager.postDelayed(new C10391(), 200);
            }
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            boolean z = true;
            this.positionCurrent = position;
            if (this.initialSelection) {
                if (positionOffset == 0.0f && positionOffsetPixels == 0) {
                    z = false;
                }
                this.dontLoadList = z;
                fireFragmentsPageScrolled(position, positionOffset);
                return;
            }
            this.initialSelection = true;
            selectCurrentPage();
        }

        public void onPageSelected(int position) {
        }
    }

    protected abstract MediaTopicsListFragment getPageFragment(FilterPage filterPage);

    protected abstract List<FilterPage> getPages();

    public static Bundle newArguments(String groupId, String userId, String defaultSelectedFilter) {
        Bundle bundle = new Bundle();
        bundle.putString("group_id", groupId);
        bundle.putString("user_id", userId);
        bundle.putString("filter", defaultSelectedFilter);
        return bundle;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.groupId = getArguments().getString("group_id");
        this.userId = getArguments().getString("user_id");
        this.defaultSelectedFilter = getArguments().getString("filter");
        this.pagerAdapter = new GroupTopicsPagerAdapter(getChildFragmentManager());
    }

    protected int getLayoutId() {
        return 2130903309;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(getLayoutId(), container, false);
        initPager(fragmentView);
        return fragmentView;
    }

    private void initPager(View fragmentView) {
        this.viewPager = (ViewPager) fragmentView.findViewById(C0263R.id.pager);
        this.viewPager.setOffscreenPageLimit(getPages().size());
        this.viewPager.setAdapter(this.pagerAdapter);
        this.indicator = (PagerSlidingTabStrip) fragmentView.findViewById(C0263R.id.indicator);
        this.shadow = fragmentView.findViewById(2131624847);
        this.indicator.setViewPager(this.viewPager);
        this.indicator.setOnPageChangeListener(new PageSelectedFixPageChangeListener());
        if (this.defaultSelectedFilter != null) {
            int index = getFilterPageIndex(this.defaultSelectedFilter);
            if (index != -1) {
                this.viewPager.setCurrentItem(index);
            }
        }
    }

    private int getFilterPageIndex(@NonNull String filter) {
        List<FilterPage> pages = getPages();
        int size = pages.size();
        for (int i = 0; i < size; i++) {
            if (filter.equals(((FilterPage) pages.get(i)).filter)) {
                return i;
            }
        }
        return -1;
    }

    protected int getPagesCount() {
        return getPages().size();
    }

    protected int getPagesMaxCount() {
        return getPagesCount();
    }

    protected void onPageSelected(int position) {
        MediaTopicsListFragment mediaTopicsListFragment = this.pagerAdapter.getFragments()[position];
        if (mediaTopicsListFragment != null && mediaTopicsListFragment.isEmpty()) {
            appBarExpand();
        }
    }

    public void refresh() {
        for (MediaTopicsListFragment mediaTopicsListFragment : this.pagerAdapter.getFragments()) {
            if (mediaTopicsListFragment != null) {
                mediaTopicsListFragment.setSwipeRefreshRefreshing(true);
                mediaTopicsListFragment.onRefresh();
            }
        }
    }
}
