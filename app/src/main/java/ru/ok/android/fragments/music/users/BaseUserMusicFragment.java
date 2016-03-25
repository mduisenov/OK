package ru.ok.android.fragments.music.users;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.music.MusicPlayerInActionBarFragment;
import ru.ok.android.ui.activity.ShowFragmentActivity;
import ru.ok.android.ui.custom.indicator.PagerSlidingTabStrip;
import ru.ok.android.utils.NavigationHelper.FragmentLocation;
import ru.ok.android.utils.animation.SimpleAnimatorListener;
import ru.ok.android.widget.ViewPagerDisable;

public abstract class BaseUserMusicFragment extends MusicPlayerInActionBarFragment implements OnPageChangeListener, Callback {
    protected PagerSlidingTabStrip indicator;
    protected View indicatorShadow;
    protected PagerAdapter pagerAdapter;
    protected View spaceUnderIndicator;
    protected ViewPager viewPager;

    /* renamed from: ru.ok.android.fragments.music.users.BaseUserMusicFragment.1 */
    class C03251 extends SimpleAnimatorListener {
        C03251() {
        }

        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            animation.removeAllListeners();
            BaseUserMusicFragment.this.spaceUnderIndicator.setVisibility(4);
            BaseUserMusicFragment.this.viewPager.setTranslationY(0.0f);
        }
    }

    /* renamed from: ru.ok.android.fragments.music.users.BaseUserMusicFragment.2 */
    class C03262 extends SimpleAnimatorListener {
        C03262() {
        }

        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            animation.removeAllListeners();
            BaseUserMusicFragment.this.spaceUnderIndicator.setVisibility(8);
            BaseUserMusicFragment.this.viewPager.setTranslationY(0.0f);
            BaseUserMusicFragment.this.indicator.bringToFront();
        }
    }

    protected abstract PagerAdapter createPagerAdapter();

    protected int getLayoutId() {
        return 2130903557;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(2130903557, container, false);
        setHasOptionsMenu(true);
        initPager(fragmentView);
        initSeparator(fragmentView);
        return fragmentView;
    }

    private void initSeparator(View view) {
        View separator = view.findViewById(2131624718);
        int visibility = 8;
        if (getActivity() instanceof ShowFragmentActivity) {
            visibility = ((ShowFragmentActivity) getActivity()).canShowFragmentOnLocation(FragmentLocation.right) ? 0 : 8;
        }
        separator.setVisibility(visibility);
    }

    protected boolean isShouldExpand() {
        return true;
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        setPageScrollEnabled(false);
        return false;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        setPageScrollEnabled(false);
        return false;
    }

    public void onDestroyActionMode(ActionMode mode) {
        setPageScrollEnabled(true);
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    private void initPager(View fragmentView) {
        this.viewPager = (ViewPager) fragmentView.findViewById(C0263R.id.pager);
        this.indicator = (PagerSlidingTabStrip) fragmentView.findViewById(C0263R.id.indicator);
        this.indicator.setShouldExpand(isShouldExpand());
        this.indicatorShadow = fragmentView.findViewById(2131625417);
        this.spaceUnderIndicator = fragmentView.findViewById(2131625416);
        this.pagerAdapter = createPagerAdapter();
        this.viewPager.setOffscreenPageLimit(this.pagerAdapter.getCount() - 1);
        this.viewPager.setAdapter(this.pagerAdapter);
        this.indicator.setOnPageChangeListener(this);
        this.indicator.setViewPager(this.viewPager);
    }

    public void setPageScrollEnabled(boolean enabled) {
        if (this.indicator != null) {
            int translation = -this.indicator.getHeight();
            if (enabled) {
                this.indicator.animate().translationY(0.0f);
                this.indicatorShadow.animate().translationY(0.0f);
                this.viewPager.animate().translationY((float) (-translation)).setListener(new C03251());
            } else {
                this.indicator.animate().translationY((float) translation);
                this.indicatorShadow.animate().translationY((float) translation);
                this.viewPager.animate().translationY((float) translation).setListener(new C03262());
            }
        }
        if (this.viewPager instanceof ViewPagerDisable) {
            ((ViewPagerDisable) this.viewPager).setEnableScroll(enabled);
        }
    }

    public void onPageScrolled(int i, float v, int i2) {
    }

    public void onPageSelected(int i) {
    }

    public void onPageScrollStateChanged(int i) {
    }
}
