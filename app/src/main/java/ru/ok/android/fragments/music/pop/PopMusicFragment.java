package ru.ok.android.fragments.music.pop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.music.collections.PopMusicCollectionsFragment;
import ru.ok.android.fragments.music.users.BaseUserMusicFragment;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.localization.LocalizationManager;

public class PopMusicFragment extends BaseUserMusicFragment {

    class PopMusicPagerAdapter extends FragmentPagerAdapter {
        private MusicFragmentMode mode;
        private PopMusicCollectionsFragment musicCollectionsFragment;
        private PopTracksFragment tracksFragment;

        public PopTracksFragment getTracksFragment() {
            if (this.tracksFragment == null) {
                this.tracksFragment = (PopTracksFragment) PopTracksFragment.newInstance(this.mode);
            }
            this.tracksFragment.setActionModeCallback(PopMusicFragment.this);
            return this.tracksFragment;
        }

        public PopMusicCollectionsFragment getMusicCollectionsFragment() {
            if (this.musicCollectionsFragment == null) {
                this.musicCollectionsFragment = (PopMusicCollectionsFragment) PopMusicCollectionsFragment.newInstance(this.mode);
            }
            return this.musicCollectionsFragment;
        }

        public PopMusicPagerAdapter(MusicFragmentMode mode) {
            super(PopMusicFragment.this.getChildFragmentManager());
            this.mode = mode;
        }

        public Fragment getItem(int position) {
            switch (position) {
                case RECEIVED_VALUE:
                    return getTracksFragment();
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    return getMusicCollectionsFragment();
                default:
                    return null;
            }
        }

        public int getCount() {
            return 2;
        }

        public CharSequence getPageTitle(int position) {
            int textResId;
            switch (position) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    textResId = 2131166224;
                    break;
                default:
                    textResId = 2131166611;
                    break;
            }
            return LocalizationManager.getString(PopMusicFragment.this.getContext(), textResId);
        }

        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }
    }

    public static Bundle newArguments(MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.viewPager.setCurrentItem(0);
    }

    protected PagerAdapter createPagerAdapter() {
        return new PopMusicPagerAdapter(getMode());
    }

    protected String getTitle() {
        return getStringLocalized(2131166253);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (inflateMenuLocalized(2131689528, menu)) {
            MenuItem item = menu.findItem(2131625438);
            if (this.viewPager == null || this.viewPager.getCurrentItem() == 0) {
                item.setVisible(true);
            } else {
                item.setVisible(false);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625438:
                ((PopMusicPagerAdapter) this.pagerAdapter).getTracksFragment().showSelectedMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
