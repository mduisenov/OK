package ru.ok.android.ui.groups.fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.C0206R;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.TagsSpinnerAdapter;
import ru.ok.android.fragments.TagsSpinnerAdapter.GroupTagSelectListener;
import ru.ok.android.fragments.TagsSpinnerAdapter.SpinnerTextCountItem;
import ru.ok.android.fragments.TagsSpinnerAdapter.SubtitleProvider;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.ui.groups.data.GroupCountersLoader;
import ru.ok.android.ui.groups.data.GroupTagsLoader;
import ru.ok.android.ui.mediatopics.MediaTopicsListFragment;
import ru.ok.android.ui.mediatopics.MediaTopicsTabFragment;
import ru.ok.android.ui.mediatopics.MediaTopicsTabFragment.FilterPage;
import ru.ok.android.ui.mediatopics.MediaTopicsTabFragmentWithComposer;
import ru.ok.android.ui.stream.StreamGroupFragment;
import ru.ok.android.ui.utils.FabHelper;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.animation.SimpleAnimatorListener;
import ru.ok.android.utils.bus.BusGroupsHelper;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.java.api.response.groups.GroupCounters;
import ru.ok.model.groups.GroupTag;

public class GroupTopicsFragment extends MediaTopicsTabFragmentWithComposer implements GroupTagSelectListener, SubtitleProvider {
    private static final FilterPage FILTER_PAGE_GROUP_THEMES_ALL;
    private static final FilterPage FILTER_PAGE_GROUP_THEMES_SUGGESTED;
    private static final List<FilterPage> PAGES;
    private static GroupCounters groupCounters;
    private int currentPage;
    private String groupName;
    private Long stateTagId;
    private boolean stateTagsExist;
    private TagsSpinnerAdapter tagsSpinnerAdapter;
    private boolean userCanPinTopic;
    private boolean userCanPostTopic;
    private boolean userCanSuggestTopic;

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupTopicsFragment.1 */
    class C09211 implements OnClickListener {
        final /* synthetic */ FloatingActionButton val$fab;

        C09211(FloatingActionButton floatingActionButton) {
            this.val$fab = floatingActionButton;
        }

        public void onClick(View v) {
            StreamGroupFragment.createMediaTopic(this.val$fab.getContext(), GroupTopicsFragment.this.groupId, GroupTopicsFragment.this.userCanPostTopic, GroupTopicsFragment.this.userCanSuggestTopic);
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupTopicsFragment.2 */
    class C09222 implements AnimatorUpdateListener {
        C09222() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            float translation = ((Float) animation.getAnimatedValue()).floatValue();
            GroupTopicsFragment.this.indicator.setTranslationY(translation);
            GroupTopicsFragment.this.shadow.setTranslationY(translation);
            ((MarginLayoutParams) GroupTopicsFragment.this.viewPager.getLayoutParams()).topMargin = (int) Math.floor((double) (((float) GroupTopicsFragment.this.indicator.getMeasuredHeight()) + translation));
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupTopicsFragment.3 */
    class C09233 implements AnimatorUpdateListener {
        C09233() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            float translation = ((Float) animation.getAnimatedValue()).floatValue();
            GroupTopicsFragment.this.indicator.setTranslationY(translation);
            GroupTopicsFragment.this.shadow.setTranslationY(translation);
            ((MarginLayoutParams) GroupTopicsFragment.this.viewPager.getLayoutParams()).topMargin = (int) Math.floor((double) (((float) GroupTopicsFragment.this.indicator.getMeasuredHeight()) + translation));
        }
    }

    /* renamed from: ru.ok.android.ui.groups.fragments.GroupTopicsFragment.4 */
    class C09244 extends SimpleAnimatorListener {
        C09244() {
        }

        public void onAnimationEnd(Animator animation) {
            GroupTopicsFragment.this.indicator.setVisibility(8);
            GroupTopicsFragment.this.applyIndicatorVisiblityChange();
        }
    }

    private class GroupCountersLoaderCallback implements LoaderCallbacks<GroupCounters> {
        private GroupCountersLoaderCallback() {
        }

        public Loader<GroupCounters> onCreateLoader(int id, Bundle args) {
            return new GroupCountersLoader(GroupTopicsFragment.this.getContext(), GroupTopicsFragment.this.groupId);
        }

        public void onLoadFinished(Loader<GroupCounters> loader, GroupCounters groupCounters) {
            Logger.m173d("Group counters %s", groupCounters);
            if (groupCounters != null) {
                GroupTopicsFragment.groupCounters = groupCounters;
                if (GroupTopicsFragment.this.tagsSpinnerAdapter.getCount() > 0) {
                    GroupTopicsFragment.this.tagsSpinnerAdapter.getItem(0).dropDownCount = Integer.toString(groupCounters.themes);
                    GroupTopicsFragment.this.tagsSpinnerAdapter.notifyDataSetChanged();
                }
            }
        }

        public void onLoaderReset(Loader<GroupCounters> loader) {
        }
    }

    private class GroupTagsLoaderCallback implements LoaderCallbacks<GroupTagsLoaderResult> {
        private GroupTagsLoaderCallback() {
        }

        public Loader<GroupTagsLoaderResult> onCreateLoader(int id, Bundle args) {
            return new GroupTagsLoader(GroupTopicsFragment.this.getContext(), GroupTopicsFragment.this.groupId);
        }

        public void onLoadFinished(Loader<GroupTagsLoaderResult> loader, GroupTagsLoaderResult result) {
            GroupTopicsFragment.this.stateTagsExist = false;
            if (result.isSuccess) {
                boolean z;
                List<GroupTag> groupTags = result.groupTags;
                GroupTopicsFragment groupTopicsFragment = GroupTopicsFragment.this;
                if (groupTags == null || groupTags.isEmpty()) {
                    z = false;
                } else {
                    z = true;
                }
                groupTopicsFragment.stateTagsExist = z;
                Logger.m173d("Group tags %s", groupTags);
            } else {
                Logger.m185w("Failed load group tags for groupId %s", GroupTopicsFragment.this.groupId);
            }
            if (GroupTopicsFragment.this.stateTagsExist) {
                GroupTopicsFragment.this.initActionBarListMode(true);
                Pair<List<SpinnerTextCountItem>, Integer> p = getTagsSpinnerItems(result.groupTags);
                GroupTopicsFragment.this.tagsSpinnerAdapter.setData((List) p.first);
                if (((Integer) p.second).intValue() != -1) {
                    GroupTopicsFragment.this.actionBarListModeSelectIndex(((Integer) p.second).intValue());
                }
                GroupTopicsFragment.this.tagsSpinnerAdapter.notifyDataSetChanged();
                return;
            }
            GroupTopicsFragment.this.initActionBarStandardMode(true);
        }

        private Pair<List<SpinnerTextCountItem>, Integer> getTagsSpinnerItems(List<GroupTag> groupTags) {
            List<SpinnerTextCountItem> items = new ArrayList();
            items.add(new SpinnerTextCountItem(GroupTopicsFragment.this.getStringLocalized(2131165958), GroupTopicsFragment.this.getStringLocalized(2131165959), null, GroupTopicsFragment.groupCounters != null ? Integer.toString(GroupTopicsFragment.groupCounters.themes) : "", null));
            int selectedIndex = -1;
            if (groupTags != null) {
                for (int i = 0; i < groupTags.size(); i++) {
                    GroupTag groupTag = (GroupTag) groupTags.get(i);
                    if (GroupTopicsFragment.this.stateTagId != null && GroupTopicsFragment.this.stateTagId.longValue() == groupTag.tagId) {
                        selectedIndex = i;
                    }
                    items.add(new SpinnerTextCountItem(groupTag.tag, Long.toString(groupTag.topicsCount), Long.valueOf(groupTag.tagId)));
                }
            }
            return new Pair(items, Integer.valueOf(selectedIndex + 1));
        }

        public void onLoaderReset(Loader<GroupTagsLoaderResult> loader) {
        }
    }

    public static class GroupTagsLoaderResult {
        public final List<GroupTag> groupTags;
        public boolean isSuccess;

        public GroupTagsLoaderResult(boolean isSuccess, List<GroupTag> groupTags) {
            this.isSuccess = isSuccess;
            this.groupTags = groupTags;
        }
    }

    public GroupTopicsFragment() {
        this.currentPage = 0;
    }

    public static Bundle newArgumentsGroupTag(String groupId, Long tagId) {
        Bundle bundle = MediaTopicsTabFragment.newArguments(groupId, null, null);
        if (tagId != null) {
            bundle.putLong("tag_id", tagId.longValue());
        }
        return bundle;
    }

    public static Bundle newArgumentsGroupFilter(String groupId, String filter) {
        return MediaTopicsTabFragment.newArguments(groupId, null, filter);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().get("tag_id") != null) {
            this.stateTagId = Long.valueOf(getArguments().getLong("tag_id"));
        }
        this.tagsSpinnerAdapter = new TagsSpinnerAdapter(getActivity());
        this.tagsSpinnerAdapter.setGroupTagsSelectListener(this);
        this.tagsSpinnerAdapter.setSubtitleProvider(this);
        if (savedInstanceState != null) {
            this.groupName = savedInstanceState.getString("key_groupname");
            if (this.groupName != null) {
                processGroupName();
            }
        }
        BusGroupsHelper.getGroupInfo(this.groupId);
    }

    @Subscribe(on = 2131623946, to = 2131624171)
    public void onGroupInfo(BusEvent e) {
        if (TextUtils.equals(this.groupId, e.bundleInput.getString("GROUP_ID")) && e.resultCode == -1) {
            this.groupName = e.bundleOutput.getString("GROUP_RESULT_INFO_NAME");
            processGroupName();
            this.userCanPostTopic = e.bundleOutput.getBoolean("CAN_POST_MEDIATOPIC", false);
            this.userCanSuggestTopic = e.bundleOutput.getBoolean("CAN_SUGGEST_MEDIATOPIC", false);
            updateMediaPostPanel(getView());
            this.userCanPinTopic = e.bundleOutput.getBoolean("CAN_PIN_MEDIATOPIC", false);
            if (this.userCanPinTopic) {
                notifyPagesCanPinTopic();
            }
            if (this.userCanSuggestTopic && this.viewPager != null && this.viewPager.getAdapter() != null) {
                this.viewPager.getAdapter().notifyDataSetChanged();
                if (this.defaultSelectedFilter != null && this.defaultSelectedFilter.equals(FILTER_PAGE_GROUP_THEMES_SUGGESTED.filter)) {
                    selectFilterGroupSuggested();
                }
            }
        }
    }

    private void notifyPagesCanPinTopic() {
        for (Fragment fragment : this.pagerAdapter.getFragments()) {
            if (fragment != null && (fragment instanceof GroupTopicsListFragment)) {
                ((GroupTopicsListFragment) fragment).setCanPinTopic(true);
            }
        }
    }

    protected void initFab(FloatingActionButton fab) {
        fab.setOnClickListener(new C09211(fab));
    }

    private void initActionBarListMode(boolean setCallback) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
            actionBar.setSubtitle(null);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(1);
            if (setCallback) {
                actionBar.setListNavigationCallbacks(this.tagsSpinnerAdapter, this.tagsSpinnerAdapter);
            }
        }
    }

    private void actionBarListModeSelectIndex(int index) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSelectedNavigationItem(index);
        }
    }

    private void initActionBarStandardMode(boolean setCallback) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(0);
            if (setCallback) {
                actionBar.setListNavigationCallbacks(null, null);
            }
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(getTitle());
            actionBar.setSubtitle(getSubtitle());
        }
    }

    private void processGroupName() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && actionBar.getNavigationMode() == 0) {
            actionBar.setSubtitle(getSubtitle());
        }
        this.tagsSpinnerAdapter.notifyDataSetChanged();
    }

    protected CharSequence getTitle() {
        return (this.currentPage == 0 && this.stateTagsExist) ? null : getStringLocalized(2131165954);
    }

    protected CharSequence getSubtitle() {
        return this.groupName;
    }

    public CharSequence getSpinnerSubtitle() {
        return getSubtitle();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(202, getArguments(), new GroupCountersLoaderCallback());
        getLoaderManager().initLoader(C0206R.styleable.Theme_checkedTextViewStyle, getArguments(), new GroupTagsLoaderCallback());
        getLoaderManager().getLoader(C0206R.styleable.Theme_checkedTextViewStyle).forceLoad();
    }

    static {
        FILTER_PAGE_GROUP_THEMES_ALL = new FilterPage("GROUP_THEMES", 2131165956);
        FILTER_PAGE_GROUP_THEMES_SUGGESTED = new FilterPage("GROUP_SUGGESTED", 2131165957);
        PAGES = Arrays.asList(new FilterPage[]{FILTER_PAGE_GROUP_THEMES_ALL, new FilterPage("GROUP_ACTUAL", 2131165955), FILTER_PAGE_GROUP_THEMES_SUGGESTED});
    }

    protected int getPagesCount() {
        return this.userCanSuggestTopic ? 3 : 2;
    }

    protected int getPagesMaxCount() {
        return PAGES.size();
    }

    protected List<FilterPage> getPages() {
        return PAGES;
    }

    protected MediaTopicsListFragment getPageFragment(FilterPage filterPage) {
        MediaTopicsListFragment fragment = new GroupTopicsListFragment();
        fragment.setArguments(GroupTopicsListFragment.newArguments(null, this.groupId, filterPage.filter, this.stateTagId, this.userCanPinTopic));
        return fragment;
    }

    public boolean onGroupTagSelected(int position, Long tagId) {
        for (Fragment fragment : this.pagerAdapter.getFragments()) {
            if (fragment != null && (fragment instanceof GroupTopicsListFragment)) {
                ((GroupTopicsListFragment) fragment).setGroupTagId(tagId);
            }
        }
        if (position == 0) {
            showIndicator();
        } else {
            hideIndicator();
        }
        if (!(tagId == null || this.viewPager.getCurrentItem() == 0)) {
            this.viewPager.setCurrentItem(0);
        }
        return true;
    }

    private void showIndicator() {
        if (this.indicator.getVisibility() != 0) {
            this.indicator.setVisibility(0);
            this.indicator.bringToFront();
            applyIndicatorVisiblityChange();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{this.indicator.getTranslationY(), 0.0f}).setDuration(150);
            valueAnimator.addUpdateListener(new C09222());
            valueAnimator.start();
        }
    }

    private void hideIndicator() {
        if (this.indicator.getVisibility() == 0) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, (float) (-this.indicator.getMeasuredHeight())}).setDuration(150);
            valueAnimator.addUpdateListener(new C09233());
            valueAnimator.addListener(new C09244());
            valueAnimator.start();
        }
    }

    private void applyIndicatorVisiblityChange() {
        View scrollTopView = getCoordinatorManager().getFabById(2131624644);
        if (scrollTopView != null) {
            FabHelper.updateScrollTopAnchoring(getCoordinatorManager().coordinatorLayout, scrollTopView);
            getCoordinatorManager().forceCoordinatorLayoutPrepareChildren();
        }
    }

    protected void onPageSelected(int pagePosition) {
        this.currentPage = pagePosition;
        if (!this.stateTagsExist) {
            return;
        }
        if (pagePosition == 0) {
            initActionBarListMode(false);
        } else {
            initActionBarStandardMode(false);
        }
    }

    protected boolean isMediaPostPanelRequired() {
        return this.userCanPostTopic || this.userCanSuggestTopic;
    }

    @Subscribe(on = 2131623946, to = 2131624226)
    public void onGroupTopicLoad(BusEvent event) {
        if (isVisible() && event.bundleInput != null) {
            String topicGroupId = event.bundleInput.getString("group_id");
            if (!TextUtils.isEmpty(topicGroupId) && topicGroupId.equals(this.groupId)) {
                refresh();
                int typeOrdinal = event.bundleInput.getInt("mediatopic_type", -1);
                if (MediaTopicType.GROUP_SUGGESTED == (typeOrdinal == -1 ? MediaTopicType.GROUP_THEME : MediaTopicType.values()[typeOrdinal])) {
                    selectFilterGroupSuggested();
                } else {
                    selectFilterAllPage();
                }
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624019)
    public void onMediaTopicPin(BusEvent event) {
        boolean requestedPinOn = event.bundleInput.getBoolean("pin_on");
        if (event.resultCode == -1) {
            TimeToast.show(getContext(), requestedPinOn ? 2131166152 : 2131166185, 0);
            if (this.currentPage != 0) {
                this.viewPager.setCurrentItem(0);
            }
            refresh();
            return;
        }
        TimeToast.show(getContext(), requestedPinOn ? 2131166151 : 2131166184, 1);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key_groupname", this.groupName);
    }

    protected void selectFilterGroupSuggested() {
        if (this.viewPager != null && 2 < this.pagerAdapter.getCount() && this.viewPager.getCurrentItem() != 2) {
            this.viewPager.setCurrentItem(2);
        }
    }
}
