package ru.ok.android.ui.stream;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.Arrays;
import java.util.Collection;
import ru.ok.android.fragments.web.shortlinks.ShortLink;
import ru.ok.android.ui.stream.data.StreamContext;
import ru.ok.android.ui.users.fragments.profiles.ProfileUserFragment;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.UserInfo;

public abstract class BaseStreamUserFragment extends BaseProfilesStreamListFragment<ProfileUserFragment> {
    public abstract ProfileUserFragment createProfileFragment();

    public abstract String getUserId();

    protected String getTitle() {
        if (this.profileFragment != null) {
            String userName = ((ProfileUserFragment) this.profileFragment).getUserName();
            if (userName != null) {
                return userName;
            }
        }
        return "";
    }

    protected void addProfileFragment(ProfileUserFragment profileUserFragment, int containerViewId) {
        ((ProfileUserFragment) this.profileFragment).setLoadCallBack(this);
        super.addProfileFragment(profileUserFragment, containerViewId);
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.profileViewChangeObserver != null) {
            ((ProfileUserFragment) this.profileFragment).removeViewChangeObserver(this.profileViewChangeObserver);
        }
        if (this.profileViewMeasureObserver != null) {
            ((ProfileUserFragment) this.profileFragment).removeMeasureViewChangeObserver(this.profileViewMeasureObserver);
        }
    }

    protected int getLayoutId() {
        return 2130903412;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ((ProfileUserFragment) this.profileFragment).onActivityResult(requestCode, resultCode, data);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (this.profileFragment != null) {
            ((ProfileUserFragment) this.profileFragment).onCreateOptionsMenu(menu, inflater);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (this.profileFragment != null) {
            ((ProfileUserFragment) this.profileFragment).onPrepareOptionsMenu(menu);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.profileFragment != null && ((ProfileUserFragment) this.profileFragment).onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() != 2131625454) {
            return super.onOptionsItemSelected(item);
        }
        if (getActivity() != null) {
            ShortLink.createUserProfileLink(getUserId()).copy(getActivity(), true);
        }
        return true;
    }

    protected StreamContext createStreamContext() {
        return StreamContext.userProfile(getUserId());
    }

    protected Collection<? extends GeneralUserInfo> getFilteredUsers() {
        return Arrays.asList(new UserInfo[]{new UserInfo(getUserId())});
    }
}
