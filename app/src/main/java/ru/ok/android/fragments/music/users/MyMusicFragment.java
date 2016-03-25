package ru.ok.android.fragments.music.users;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.music.ExtensionTracksFragment;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.music.collections.MyMusicCollectionsFragment;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.localization.LocalizationManager;

public class MyMusicFragment extends BaseUserMusicFragment {

    class MyMusicPagerAdapter extends FragmentPagerAdapter {
        private ExtensionTracksFragment extensionTracksFragment;
        private HistoryTracksFragment historyTracksFragment;
        private MusicFragmentMode mode;
        private MyTracksFragment tracksFragment;
        private MyMusicCollectionsFragment userMusicCollectionsFragment;

        public HistoryTracksFragment getHistoryTracksFragment() {
            if (this.historyTracksFragment == null) {
                this.historyTracksFragment = (HistoryTracksFragment) HistoryTracksFragment.newInstance(this.mode);
            }
            this.historyTracksFragment.setActionModeCallback(MyMusicFragment.this);
            return this.historyTracksFragment;
        }

        public MyTracksFragment getTracksFragment() {
            if (this.tracksFragment == null) {
                this.tracksFragment = (MyTracksFragment) MyTracksFragment.newInstance(this.mode);
            }
            this.tracksFragment.setActionModeCallback(MyMusicFragment.this);
            return this.tracksFragment;
        }

        public MyMusicCollectionsFragment getUserMusicCollectionsFragment() {
            if (this.userMusicCollectionsFragment == null) {
                this.userMusicCollectionsFragment = (MyMusicCollectionsFragment) MyMusicCollectionsFragment.newInstance(this.mode);
            }
            return this.userMusicCollectionsFragment;
        }

        public ExtensionTracksFragment getExtensionTracksFragment() {
            if (this.extensionTracksFragment == null) {
                this.extensionTracksFragment = (ExtensionTracksFragment) ExtensionTracksFragment.newInstance(this.mode);
            }
            this.extensionTracksFragment.setActionModeCallback(MyMusicFragment.this);
            return this.extensionTracksFragment;
        }

        public MyMusicPagerAdapter(MusicFragmentMode mode) {
            super(MyMusicFragment.this.getChildFragmentManager());
            this.mode = mode;
        }

        public Fragment getItem(int position) {
            switch (position) {
                case RECEIVED_VALUE:
                    return getExtensionTracksFragment();
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    return getTracksFragment();
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    return getUserMusicCollectionsFragment();
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    return getHistoryTracksFragment();
                default:
                    return null;
            }
        }

        public int getCount() {
            return 4;
        }

        public CharSequence getPageTitle(int position) {
            int textResId;
            switch (position) {
                case RECEIVED_VALUE:
                    textResId = 2131166225;
                    break;
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    textResId = 2131166611;
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    textResId = 2131166224;
                    break;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    textResId = 2131166229;
                    break;
                default:
                    textResId = 2131166611;
                    break;
            }
            return LocalizationManager.getString(MyMusicFragment.this.getContext(), textResId);
        }

        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }
    }

    protected CharSequence getTitle() {
        return getStringLocalized(2131166246);
    }

    public static Bundle newArguments(MusicFragmentMode mode, int page) {
        Bundle args = new Bundle();
        args.putParcelable("music-fragment-mode", mode);
        args.putInt("show_page", page);
        return args;
    }

    private int getPageType() {
        switch (getArguments().getInt("show_page")) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return 2;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return 3;
            default:
                return 1;
        }
    }

    protected PagerAdapter createPagerAdapter() {
        return new MyMusicPagerAdapter(getMode());
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.viewPager.setCurrentItem(getPageType());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (inflateMenuLocalized(2131689511, menu)) {
            MenuItem itemDelete = menu.findItem(C0263R.id.delete);
            MenuItem itemAdd = menu.findItem(2131625438);
            if (getMode() != MusicFragmentMode.STANDARD) {
                itemDelete.setVisible(false);
                itemAdd.setVisible(false);
            } else if (this.viewPager != null) {
                switch (this.viewPager.getCurrentItem()) {
                    case RECEIVED_VALUE:
                        itemDelete.setVisible(false);
                        itemAdd.setVisible(true);
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        itemDelete.setVisible(true);
                        itemAdd.setVisible(false);
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                        itemDelete.setVisible(false);
                        itemAdd.setVisible(true);
                    default:
                        itemDelete.setVisible(false);
                        itemAdd.setVisible(false);
                }
            } else {
                itemDelete.setVisible(true);
                itemAdd.setVisible(false);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (getActivity() == null) {
            return false;
        }
        switch (item.getItemId()) {
            case C0263R.id.delete /*2131624801*/:
                ((MyMusicPagerAdapter) this.pagerAdapter).getTracksFragment().showSelectedMode();
                return true;
            case 2131625438:
                switch (this.viewPager.getCurrentItem()) {
                    case RECEIVED_VALUE:
                        ((MyMusicPagerAdapter) this.pagerAdapter).getExtensionTracksFragment().showSelectedMode();
                        break;
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                        ((MyMusicPagerAdapter) this.pagerAdapter).getHistoryTracksFragment().showSelectedMode();
                        break;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected boolean isShouldExpand() {
        return false;
    }
}
