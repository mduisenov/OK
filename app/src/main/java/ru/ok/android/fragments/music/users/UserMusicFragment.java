package ru.ok.android.fragments.music.users;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.Arrays;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.db.provider.OdklContract.Users;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.music.collections.UserMusicCollectionsFragment;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.bus.BusUsersHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;

public class UserMusicFragment extends BaseUserMusicFragment implements LoaderCallbacks<Cursor> {
    private UserInfo user;

    class UserMusicPagerAdapter extends FragmentPagerAdapter {
        private MusicFragmentMode mode;
        private UserTracksFragment tracksFragment;
        private String userId;
        private UserMusicCollectionsFragment userMusicCollectionsFragment;

        public UserTracksFragment getTracksFragment() {
            if (this.tracksFragment == null) {
                this.tracksFragment = (UserTracksFragment) UserTracksFragment.newInstance(this.userId, this.mode);
            }
            this.tracksFragment.setActionModeCallback(UserMusicFragment.this);
            return this.tracksFragment;
        }

        public UserMusicCollectionsFragment getUserMusicCollectionsFragment() {
            if (this.userMusicCollectionsFragment == null) {
                this.userMusicCollectionsFragment = (UserMusicCollectionsFragment) UserMusicCollectionsFragment.newInstance(this.userId, this.mode);
            }
            return this.userMusicCollectionsFragment;
        }

        public UserMusicPagerAdapter(String userId, MusicFragmentMode mode) {
            super(UserMusicFragment.this.getChildFragmentManager());
            this.userId = userId;
            this.mode = mode;
        }

        public Fragment getItem(int position) {
            switch (position) {
                case RECEIVED_VALUE:
                    return getTracksFragment();
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    return getUserMusicCollectionsFragment();
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
                case RECEIVED_VALUE:
                    textResId = 2131166611;
                    break;
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    textResId = 2131166224;
                    break;
                default:
                    textResId = 2131166611;
                    break;
            }
            return LocalizationManager.getString(UserMusicFragment.this.getContext(), textResId);
        }

        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }
    }

    public static Bundle newArguments(String userId, MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    private String getUserId() {
        return getArguments().getString("USER_ID");
    }

    protected CharSequence getTitle() {
        if (this.user != null) {
            return this.user.getConcatName();
        }
        return getStringLocalized(2131165394);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.viewPager.setCurrentItem(0);
        getLoaderManager().initLoader(0, null, this);
        if (!TextUtils.isEmpty(getUserId())) {
            BusUsersHelper.getUserInfos(Arrays.asList(new String[]{getUserId()}), false);
        }
    }

    protected PagerAdapter createPagerAdapter() {
        return new UserMusicPagerAdapter(getUserId(), getMode());
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), Users.getUri(getUserId()), null, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            UserInfo user = UsersStorageFacade.cursor2User(cursor);
            if (user != null) {
                this.user = user;
                updateActionBarState();
            }
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (inflateMenuLocalized(2131689528, menu)) {
            MenuItem item = menu.findItem(2131625438);
            if ((this.viewPager == null || this.viewPager.getCurrentItem() == 0) && getMode() == MusicFragmentMode.STANDARD) {
                item.setVisible(true);
            } else {
                item.setVisible(false);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625438:
                ((UserMusicPagerAdapter) this.pagerAdapter).getTracksFragment().showSelectedMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onPageScrollStateChanged(int i) {
        super.onPageScrollStateChanged(i);
        switch (this.viewPager.getCurrentItem()) {
            case RECEIVED_VALUE:
                ((UserMusicPagerAdapter) this.pagerAdapter).getTracksFragment().hideSelectedMode();
            default:
        }
    }
}
