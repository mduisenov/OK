package ru.ok.android.ui.users.fragments.profiles;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;
import ru.ok.android.model.image.ImageForUpload;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.app.IntentUtils;
import ru.ok.android.ui.activity.main.ActivityExecutor.SoftInputType;
import ru.ok.android.ui.dialogs.ProgressDialogFragment;
import ru.ok.android.ui.users.fragments.data.UserMergedPresent;
import ru.ok.android.ui.users.fragments.data.UserProfileInfo;
import ru.ok.android.ui.users.fragments.profiles.statistics.UserProfileStatisticsManager;
import ru.ok.android.ui.users.fragments.utils.UserProfileMenuItemsVisibilityHelper;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.bus.BusUsersHelper;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.search.SearchType;

public class CurrentProfileUserFragment extends ProfileUserFragment {
    private CurrentUserNavigationHandlerBaseUser handler;
    private final UserProfileMenuItemsVisibilityHelper menuHelper;
    private boolean shouldHideProgress;
    private boolean shouldShowProgress;

    /* renamed from: ru.ok.android.ui.users.fragments.profiles.CurrentProfileUserFragment.1 */
    class C13351 implements OnClickListener {
        final /* synthetic */ PhotoAlbumInfo val$albumInfo;

        C13351(PhotoAlbumInfo photoAlbumInfo) {
            this.val$albumInfo = photoAlbumInfo;
        }

        public void onClick(DialogInterface dialog, int which) {
            if (CurrentProfileUserFragment.this.getActivity() != null) {
                switch (which) {
                    case RECEIVED_VALUE:
                        CurrentProfileUserFragment.this.startActivityForResult(IntentUtils.createIntentForPickPersonalPhoto(CurrentProfileUserFragment.this.getActivity()), 12345);
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        NavigationHelper.startPhotoUploadSequence(CurrentProfileUserFragment.this.getActivity(), this.val$albumInfo, 1, 2);
                    default:
                }
            }
        }
    }

    public CurrentProfileUserFragment() {
        this.menuHelper = new UserProfileMenuItemsVisibilityHelper();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.handler = new CurrentUserNavigationHandlerBaseUser(getActivity());
    }

    protected BaseUserProfileNavigationHandler getNavigationHandler() {
        return this.handler;
    }

    protected void onProfileInfoLoad(UserProfileInfo profileInfo) {
        super.onProfileInfoLoad(profileInfo);
        setVisibilityProfileMenu(0);
        if (profileInfo != null && profileInfo.userInfo != null) {
            this.menuHelper.setUserProfileInfo(profileInfo);
            this.menuHelper.setRelationInfo(profileInfo.relationInfo);
            this.menuHelper.updateVisibility();
        }
    }

    protected String getUserId() {
        return OdnoklassnikiApplication.getCurrentUser().getId();
    }

    public void notifyUser() {
        super.notifyUser();
        if (this.profileInfo.userInfo.hasServiceInvisible) {
            setServiceInvisibleUser();
        }
    }

    protected void setServiceInvisibleUser() {
        if (this.profileInfo != null && this.profileInfo.userInfo != null) {
            this.serviceInvisibleView.setVisibility(0);
        }
    }

    protected boolean isShowStatus() {
        return this.profileInfo.userInfo.status != null;
    }

    protected boolean isShowPresents(List<UserMergedPresent> presents) {
        return (presents == null || presents.isEmpty()) ? false : true;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        if (getActivity() != null) {
            super.onPrepareOptionsMenu(menu);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getActivity() != null && inflateMenuLocalized(2131689483, menu)) {
            this.menuHelper.configureMenu(menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625391:
                findFriends(MenuItemCompat.getActionView(item));
                UserProfileStatisticsManager.sendStatEventForCurrentUser(UserProfileStatisticsManager.ACTION_FIND_FRIENDS);
                return true;
            case 2131625453:
                changeMainPhoto();
                UserProfileStatisticsManager.sendStatEventForCurrentUser(UserProfileStatisticsManager.ACTION_AVATAR_CLICK);
                return true;
            case 2131625454:
                createShortLink();
                UserProfileStatisticsManager.sendStatEventForCurrentUser(UserProfileStatisticsManager.ACTION_MAKE_WEB_LINK);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void findFriends(View view) {
        if (getActivity() != null) {
            NavigationHelper.showSearchPage(getActivity(), view, "", SearchType.USER);
        }
    }

    protected boolean isSendPresentVisible() {
        return true;
    }

    protected boolean isShowOnlineView() {
        return false;
    }

    protected boolean isMayDeleteStatus() {
        return true;
    }

    private void changeMainPhoto() {
        if (getActivity() != null) {
            PhotoAlbumInfo albumInfo = new PhotoAlbumInfo();
            albumInfo.setOwnerType(OwnerType.USER);
            Builder builder = new Builder(getActivity());
            builder.setTitle(getStringLocalized(2131165424));
            builder.setItems(getStringArrayLocalized(2131558427), new C13351(albumInfo));
            builder.show();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.shouldShowProgress) {
            showProgressDialog();
            this.shouldShowProgress = false;
        }
        if (this.shouldHideProgress) {
            hideProgressDialog();
            this.shouldHideProgress = false;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12345 && resultCode == -1) {
            PhotoInfo photoInfo = (PhotoInfo) data.getParcelableExtra("photo");
            if (photoInfo != null) {
                Bundle bundleInput = new Bundle();
                bundleInput.putString("pid", photoInfo.getId());
                GlobalBus.send(2131624115, new BusEvent(bundleInput));
                this.shouldHideProgress = false;
                if (isResumed()) {
                    showProgressDialog();
                } else {
                    this.shouldShowProgress = true;
                }
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624259)
    public void onMainPhotoSet(BusEvent event) {
        this.shouldShowProgress = false;
        if (isResumed()) {
            hideProgressDialog();
        } else {
            this.shouldHideProgress = true;
        }
        if (event.resultCode == -2) {
            Toast.makeText(getActivity(), 2131166544, 1).show();
            return;
        }
        resetAvatar();
        UserProfileStatisticsManager.sendStatEventForCurrentUser(UserProfileStatisticsManager.ACTION_SELECT_AVATAR);
    }

    @Subscribe(on = 2131623946, to = 2131624225)
    public void onImageUploaded(BusEvent event) {
        if (event.resultCode == 1) {
            ImageForUpload image = (ImageForUpload) event.bundleOutput.getParcelable("img");
            if (image != null && image.getCurrentStatus() == 5 && image.getUploadTarget() == 2) {
                resetAvatar();
                UserProfileStatisticsManager.sendStatEventForCurrentUser(UserProfileStatisticsManager.ACTION_UPLOAD_AVATAR);
            }
        }
    }

    private void resetAvatar() {
        BusUsersHelper.getUserInfos(Arrays.asList(new String[]{getUserId()}), true);
    }

    private void showProgressDialog() {
        ProgressDialogFragment.createInstance(getStringLocalized(2131166864), true).show(getChildFragmentManager(), "progress-dialog");
    }

    protected final void hideProgressDialog() {
        DialogFragment fragment = (DialogFragment) getChildFragmentManager().findFragmentByTag("progress-dialog");
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    protected void onSendPresentClicked() {
        if (!TextUtils.isEmpty(getUserId())) {
            NavigationHelper.showExternalUrlPage(getActivity(), ShortLinkUtils.getUrlByPath("gifts"), SoftInputType.RESIZE);
            UserProfileStatisticsManager.sendStatEventForCurrentUser(UserProfileStatisticsManager.ACTION_MAKE_PRESENT);
        }
    }
}
