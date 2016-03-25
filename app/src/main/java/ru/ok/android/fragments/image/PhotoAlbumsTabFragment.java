package ru.ok.android.fragments.image;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.custom.indicator.PagerSlidingTabStrip;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.image.view.AlbumFinder;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.widget.ViewPagerDisable;
import ru.ok.model.photo.PhotoAlbumInfo;

public class PhotoAlbumsTabFragment extends BaseFragment implements AlbumFinder {
    private PhotoAlbumPhotosFragment mPhotoAlbumPhotosFragment;
    private PhotoAlbumsFragment mPhotoAlbumsFragment;
    private PhotoOwner mPhotoOwner;
    private ViewPagerDisable mViewPager;
    private OnPageChangeListener pageChangeListener;

    /* renamed from: ru.ok.android.fragments.image.PhotoAlbumsTabFragment.1 */
    class C03111 implements OnPageChangeListener {
        C03111() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            if (position == 1) {
                ((BaseCompatToolbarActivity) PhotoAlbumsTabFragment.this.getContext()).getAppBarLayout().setExpanded(true, true);
            }
        }

        public void onPageScrollStateChanged(int state) {
        }
    }

    private class PhotoAlbumsPagerAdapter extends FragmentPagerAdapter {
        public PhotoAlbumsPagerAdapter() {
            super(PhotoAlbumsTabFragment.this.getChildFragmentManager());
        }

        public CharSequence getPageTitle(int position) {
            int titleResId;
            switch (position) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    titleResId = 2131165375;
                    break;
                default:
                    titleResId = 2131165380;
                    break;
            }
            return LocalizationManager.getString(PhotoAlbumsTabFragment.this.getContext(), titleResId);
        }

        public Fragment getItem(int position) {
            switch (position) {
                case RECEIVED_VALUE:
                    return PhotoAlbumsTabFragment.this.getAllPhotosFragment();
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    return PhotoAlbumsTabFragment.this.getAllAlbumsFragment();
                default:
                    return null;
            }
        }

        public int getCount() {
            return 2;
        }
    }

    public PhotoAlbumsTabFragment() {
        this.pageChangeListener = new C03111();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPhotoOwner(savedInstanceState);
    }

    protected CharSequence getTitle() {
        if (this.mPhotoOwner.getOwnerInfo() == null || !this.mPhotoOwner.isCurrentUser()) {
            return getStringLocalized(2131166597);
        }
        return getStringLocalized(2131165282);
    }

    protected CharSequence getSubtitle() {
        return PhotoAlbumsFragment.subtitleFromPhotoOwner(this.mPhotoOwner);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LocalizationManager.inflate(getContext(), getLayoutId(), container, false);
        this.mViewPager = (ViewPagerDisable) view.findViewById(2131625199);
        PagerSlidingTabStrip tabIndicator = (PagerSlidingTabStrip) view.findViewById(C0263R.id.indicator);
        PhotoAlbumsPagerAdapter pagerAdapter = new PhotoAlbumsPagerAdapter();
        this.mViewPager.setOffscreenPageLimit(1);
        this.mViewPager.setAdapter(pagerAdapter);
        this.mViewPager.setCurrentItem(0);
        this.mViewPager.setOnPageChangeListener(null);
        this.mViewPager.addOnPageChangeListener(this.pageChangeListener);
        tabIndicator.setViewPager(this.mViewPager);
        initCurrentTab(savedInstanceState);
        return view;
    }

    private void initCurrentTab(Bundle savedInstanceState) {
        int tabPosition = 0;
        if (savedInstanceState != null) {
            tabPosition = savedInstanceState.getInt("slctdtb", 0);
        } else if (getArguments().getBoolean("opndmnu")) {
            tabPosition = 1;
        }
        this.mViewPager.setCurrentItem(tabPosition);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("slctdtb", this.mViewPager.getCurrentItem());
        outState.putParcelable("pwnr", this.mPhotoOwner);
        super.onSaveInstanceState(outState);
    }

    protected int getLayoutId() {
        return 2130903376;
    }

    public static PhotoAlbumsTabFragment newInstance(PhotoOwner photoOwner, boolean openedFromMenu, boolean hideActions) {
        PhotoAlbumsTabFragment fragment = new PhotoAlbumsTabFragment();
        Bundle args = new Bundle();
        args.putParcelable("pwnr", photoOwner);
        args.putBoolean("opndmnu", openedFromMenu);
        args.putBoolean("hdactns", hideActions);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    public PhotoAlbumInfo findAlbumById(@NonNull String aid) {
        return this.mPhotoAlbumsFragment.findAlbumById(aid);
    }

    private void initPhotoOwner(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.mPhotoOwner = (PhotoOwner) savedInstanceState.getParcelable("pwnr");
        }
        if (this.mPhotoOwner == null) {
            this.mPhotoOwner = (PhotoOwner) getArguments().getParcelable("pwnr");
        }
        if (this.mPhotoOwner != null) {
            this.mPhotoOwner.tryPopulateOwner();
        }
    }

    private Fragment getAllPhotosFragment() {
        if (this.mPhotoAlbumPhotosFragment == null) {
            this.mPhotoAlbumPhotosFragment = PhotoAlbumPhotosFragment.newInstance("stream", this.mPhotoOwner, getArguments().getBoolean("hdactns"));
        }
        return this.mPhotoAlbumPhotosFragment;
    }

    private Fragment getAllAlbumsFragment() {
        if (this.mPhotoAlbumsFragment == null) {
            this.mPhotoAlbumsFragment = PhotoAlbumsFragment.newInstance(this.mPhotoOwner);
        }
        return this.mPhotoAlbumsFragment;
    }

    @Subscribe(on = 2131623946, to = 2131624178)
    public void onPhotoAlbumsEvent(BusEvent event) {
        if (getActivity() != null) {
            if (event.resultCode == -1) {
                PhotoOwner photoOwnerResp = (PhotoOwner) event.bundleOutput.getParcelable("wnrnfo");
                if (photoOwnerResp != null) {
                    this.mPhotoOwner = photoOwnerResp;
                    updateTitle();
                    return;
                }
                return;
            }
            setTitle(getStringLocalized(2131166597));
        }
    }

    protected final void updateTitle() {
        if (getActivity() != null) {
            if (this.mPhotoOwner.isCurrentUser()) {
                OdklSlidingMenuFragmentActivity.setMenuIndicatorEnable(getActivity(), true);
            } else {
                OdklSlidingMenuFragmentActivity.setMenuIndicatorEnable(getActivity(), false);
            }
            updateActionBarState();
        }
    }
}
