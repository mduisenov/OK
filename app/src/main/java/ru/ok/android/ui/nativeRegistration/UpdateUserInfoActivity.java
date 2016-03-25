package ru.ok.android.ui.nativeRegistration;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import java.util.HashSet;
import ru.ok.android.fragments.AvatarUploadFragment;
import ru.ok.android.fragments.AvatarUploadFragment.OnAvatarUploadListener;
import ru.ok.android.model.UpdateProfileFieldsFlags;
import ru.ok.android.model.image.ImageForUpload;
import ru.ok.android.onelog.registration.RegistrationWorkflowLogHelper;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.services.processors.registration.Location;
import ru.ok.android.ui.activity.BaseNoToolbarActivity;
import ru.ok.android.ui.activity.main.OdklActivity;
import ru.ok.android.ui.nativeRegistration.UpdateUserInfoFragment.OnUpdateUserInfoListener;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.bus.BusUsersHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.onelog.builtin.Outcome;

public class UpdateUserInfoActivity extends BaseNoToolbarActivity implements OnGlobalLayoutListener, OnAvatarUploadListener, OnUpdateUserInfoListener {
    private FrameLayout fragmentContainer;
    private HashSet<Class> loggedSteps;
    private ViewGroup mainContainer;
    private UpdateProfileFieldsFlags updateProfileFieldsFlags;
    private ImageForUpload uploadedImage;

    /* renamed from: ru.ok.android.ui.nativeRegistration.UpdateUserInfoActivity.1 */
    class C11121 implements OnClickListener {
        C11121() {
        }

        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case PagerAdapter.POSITION_NONE /*-2*/:
                    UpdateUserInfoActivity.this.goToOdklActivity();
                    break;
                case RecyclerView.NO_POSITION /*-1*/:
                    UpdateUserInfoActivity.this.goToRecommendedFriends();
                    break;
            }
            dialog.dismiss();
        }
    }

    public UpdateUserInfoActivity() {
        this.loggedSteps = new HashSet();
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        if (savedInstanceState != null) {
            this.uploadedImage = (ImageForUpload) savedInstanceState.getParcelable("pic");
            this.loggedSteps = (HashSet) savedInstanceState.getSerializable("loggedSteps");
        }
        setContentView(2130903092);
        Intent intent = getIntent();
        Location location = (Location) intent.getParcelableExtra("country");
        UserInfo userInfo = (UserInfo) intent.getParcelableExtra("user_info");
        String password = intent.getStringExtra("password");
        this.updateProfileFieldsFlags = (UpdateProfileFieldsFlags) intent.getParcelableExtra("update_profile_fields_flags");
        this.mainContainer = (ViewGroup) findViewById(2131624583);
        this.fragmentContainer = (FrameLayout) findViewById(2131624585);
        this.mainContainer.getViewTreeObserver().addOnGlobalLayoutListener(this);
        getSupportFragmentManager().beginTransaction().add(2131624585, UpdateUserInfoFragment.newInstance(location, userInfo, this.updateProfileFieldsFlags, password)).commit();
    }

    private void goToPageAfterRegistration() {
        logRegistrationWorkflow();
        BusUsersHelper.refreshCurrentUserInfo(true);
        if (AuthorizationPreferences.getRecommendedFriendsByPhonebookEnabled()) {
            if (PermissionUtils.checkSelfPermission(getContext(), "android.permission.READ_CONTACTS") != 0) {
                askToFindFriends();
                return;
            } else {
                goToRecommendedFriends();
                return;
            }
        }
        goToOdklActivity();
    }

    private void askToFindFriends() {
        OnClickListener listener = new C11121();
        Builder builder = new Builder(getContext());
        builder.setNegativeButton(LocalizationManager.getString(getContext(), 2131166257), listener).setMessage(LocalizationManager.getString(getContext(), 2131165409)).setPositiveButton(LocalizationManager.getString(getContext(), 2131166881), listener);
        builder.create().show();
    }

    private void goToRecommendedFriends() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("KEY_BACK_TO_PREVIOUS_ACTIVITY", this.updateProfileFieldsFlags.isBackButtonDisabled);
        NavigationHelper.showRecommendedUsersPage(this, bundle);
        if (!this.updateProfileFieldsFlags.isBackButtonDisabled) {
            finish();
        }
    }

    private void goToOdklActivity() {
        Intent intent = new Intent(this, OdklActivity.class);
        intent.setFlags(268468224);
        startActivity(intent);
        finish();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("pic", this.uploadedImage);
        outState.putSerializable("loggedSteps", this.loggedSteps);
    }

    public void onInfoUpdated(UserInfo userInfo) {
        logRegistrationWorkflow();
        if (this.updateProfileFieldsFlags.isAvatarSeparately) {
            Fragment fragment = new AvatarUploadFragment();
            Bundle args = new Bundle();
            args.putParcelable("pic", this.uploadedImage);
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(2131624585, fragment).addToBackStack(null).commitAllowingStateLoss();
            return;
        }
        goToPageAfterRegistration();
    }

    public void onAvatarUploadFragmentClose() {
        goToPageAfterRegistration();
    }

    public void onAvatarUploaded(ImageForUpload image) {
        this.uploadedImage = image;
    }

    public void onAvatarRemoved(ImageForUpload image) {
        this.uploadedImage = null;
    }

    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0 || !this.updateProfileFieldsFlags.isBackButtonDisabled) {
            super.onBackPressed();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setMainContainerSize();
    }

    private void setMainContainerSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int maxWidth = (int) getResources().getDimension(2131231205);
        if (maxWidth > size.x) {
            this.mainContainer.getLayoutParams().width = -1;
        } else {
            this.mainContainer.getLayoutParams().width = maxWidth;
        }
    }

    public void onGlobalLayout() {
        if (this.mainContainer.getHeight() != 0) {
            setMainContainerSize();
            this.mainContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    private void logRegistrationWorkflow() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Class currentStep = ((Fragment) fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount())).getClass();
        if (!this.loggedSteps.contains(currentStep)) {
            this.loggedSteps.add(currentStep);
            RegistrationWorkflowLogHelper.log(currentStep, Outcome.success);
        }
    }
}
