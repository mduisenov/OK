package ru.ok.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.music.tuners.MusicTunersFragment;
import ru.ok.android.fragments.music.users.MyMusicFragment;
import ru.ok.android.fragments.music.users.UserMusicFragment;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.fragments.MusicUsersFragment;
import ru.ok.android.ui.fragments.StubFragment;
import ru.ok.android.ui.utils.HomeButtonUtils;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.FragmentLocation;
import ru.ok.android.utils.localization.LocalizationManager;

public class UserMusicActivity extends OdklSubActivity {
    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        if (isShowTuner()) {
            showTuner();
        } else if (isShowTrack()) {
            showTrack();
        } else if (DeviceUtils.getType(this) == DeviceLayoutType.LARGE) {
            if (TextUtils.isEmpty(getUserId())) {
                showStub();
            } else {
                showMusicUserFragment(getUserId());
            }
            showMusicUsersFragment();
        } else if (TextUtils.isEmpty(getUserId())) {
            showMusicUsersFragment();
        } else {
            showMusicUserFragment(getUserId());
        }
    }

    private void showTrack() {
        long trackId = getIntent().getLongExtra("extra_track_id", -1);
        if (DeviceUtils.getType(this) == DeviceLayoutType.LARGE) {
            showMusicUsersFragment();
            NavigationHelper.showMusicTrack(this, trackId);
            return;
        }
        NavigationHelper.showMusicTrack(this, trackId);
    }

    private boolean isShowTrack() {
        Intent intent = getIntent();
        return intent != null && intent.hasExtra("extra_track_id");
    }

    private void showTuner() {
        if (DeviceUtils.getType(this) == DeviceLayoutType.LARGE) {
            showMusicUsersFragment();
            showTunersFragment();
            return;
        }
        showTunersFragment();
    }

    private void showTunersFragment() {
        ActivityExecutor activityExecutor = new ActivityExecutor(this, MusicTunersFragment.class);
        activityExecutor.setAddToBackStack(false);
        activityExecutor.setFragmentLocation(FragmentLocation.right);
        showFragment(activityExecutor);
    }

    private boolean isShowTuner() {
        Intent intent = getIntent();
        return !(intent == null || !intent.getBooleanExtra("extra_show_radio", false));
    }

    private void showMusicUserFragment(String usersId) {
        Class<? extends Fragment> fragmentClass;
        Bundle args;
        if (usersId.equals(OdnoklassnikiApplication.getCurrentUser().uid)) {
            fragmentClass = MyMusicFragment.class;
            args = MyMusicFragment.newArguments(MusicFragmentMode.STANDARD, getIntent().getIntExtra("extra_type", 0));
        } else {
            fragmentClass = UserMusicFragment.class;
            args = UserMusicFragment.newArguments(usersId, MusicFragmentMode.STANDARD);
        }
        ActivityExecutor activityExecutor = new ActivityExecutor(this, fragmentClass);
        activityExecutor.setAddToBackStack(false);
        activityExecutor.setFragmentLocation(FragmentLocation.right);
        activityExecutor.setNeedToolbar(true);
        activityExecutor.setSlidingMenuEnable(true);
        activityExecutor.setArguments(args);
        activityExecutor.setHideHomeButton(false);
        HomeButtonUtils.hideHomeButton(this);
        showFragment(activityExecutor);
    }

    private void showMusicUsersFragment() {
        ActivityExecutor activityExecutor = new ActivityExecutor(this, MusicUsersFragment.class);
        activityExecutor.setAddToBackStack(false);
        activityExecutor.setFragmentLocation(FragmentLocation.left);
        activityExecutor.setNeedToolbar(true);
        activityExecutor.setSlidingMenuEnable(true);
        activityExecutor.setArguments(MusicUsersFragment.newArguments(true, getUserId(), MusicFragmentMode.STANDARD));
        activityExecutor.setHideHomeButton(false);
        HomeButtonUtils.hideHomeButton(this);
        showFragment(activityExecutor);
    }

    private void showStub() {
        String text = LocalizationManager.getString(this, 2131166500);
        String title = LocalizationManager.getString(this, 2131166223);
        ActivityExecutor activityExecutor = new ActivityExecutor(this, StubFragment.class);
        activityExecutor.setAddToBackStack(true);
        activityExecutor.setFragmentLocation(FragmentLocation.right);
        activityExecutor.setNeedToolbar(true);
        activityExecutor.setSlidingMenuEnable(true);
        activityExecutor.setArguments(StubFragment.newArguments(text, title));
        activityExecutor.setHideHomeButton(false);
        showFragment(activityExecutor);
    }

    public boolean isNeedShowLeftMenu() {
        return true;
    }

    public String getUserId() {
        return getIntent().getStringExtra("extra_user_id");
    }
}
