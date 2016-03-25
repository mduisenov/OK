package ru.ok.android.ui.users.fragments.profiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import ru.ok.android.C0206R;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.SetRelationWebFragment;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;
import ru.ok.android.ui.activity.main.ActivityExecutor.SoftInputType;
import ru.ok.android.ui.activity.main.OdklSubActivity;
import ru.ok.android.ui.custom.imageview.CarouselPresentsImageView;
import ru.ok.android.ui.custom.profiles.ProfilesButton;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.ui.dialogs.BlockUserFragmentDialog;
import ru.ok.android.ui.dialogs.ComplaintUserDialog;
import ru.ok.android.ui.dialogs.ConfirmationDialog;
import ru.ok.android.ui.dialogs.DeleteFriendFragmentDialog;
import ru.ok.android.ui.users.fragments.data.UserProfileInfo;
import ru.ok.android.ui.users.fragments.profiles.statistics.UserProfileStatisticsManager;
import ru.ok.android.ui.users.fragments.utils.UserProfileMenuItemsVisibilityHelper;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.android.utils.bus.BusUsersHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.utils.DateUtils;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public final class ExternalProfileUserFragment extends ProfileUserFragment {
    private UserProfileNavigationHandler handler;
    private final UserProfileMenuItemsVisibilityHelper menuItemsVisibilityHelper;
    private MenuItem relationItem;

    private final class DefaultInviteClickListener implements OnClickListener {
        private DefaultInviteClickListener() {
        }

        public void onClick(View v) {
            ExternalProfileUserFragment.this.inviteToFriends();
            UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_MAKE_FRIENDS);
        }
    }

    public class DefaultOnCallHandler implements OnClickListener {
        public void onClick(View v) {
            if (ExternalProfileUserFragment.this.getActivity() != null) {
                NavigationHelper.onCallUser(ExternalProfileUserFragment.this.getActivity(), ExternalProfileUserFragment.this.getUserId());
            }
            UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_CALL);
        }
    }

    public class DefaultOnSendMessageHandler implements OnClickListener {
        public void onClick(View v) {
            if (ExternalProfileUserFragment.this.getActivity() != null && ExternalProfileUserFragment.this.profileInfo != null && ExternalProfileUserFragment.this.profileInfo.userInfo != null) {
                NavigationHelper.showMessagesForUser(ExternalProfileUserFragment.this.getActivity(), ExternalProfileUserFragment.this.profileInfo.userInfo.getId());
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_SEND_MESSAGE);
            }
        }
    }

    public class DefaultOnSubscribeHandler implements OnClickListener {
        public void onClick(View v) {
            if (ExternalProfileUserFragment.this.getActivity() != null) {
                ExternalProfileUserFragment.this.subscribeToStream();
            }
            UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_SUBSCRIBE_TO_USER);
        }
    }

    public static ExternalProfileUserFragment newInstance(String userId) {
        ExternalProfileUserFragment f = new ExternalProfileUserFragment();
        Bundle args = new Bundle();
        args.putString("user_id_extras", userId);
        f.setArguments(args);
        return f;
    }

    public ExternalProfileUserFragment() {
        this.menuItemsVisibilityHelper = new UserProfileMenuItemsVisibilityHelper();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.handler = new UserProfileNavigationHandler(getActivity());
        this.handler.setUserId(getUserId());
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.inviteButton.setOnClickListener(new DefaultInviteClickListener());
        this.sendMessageButton.setOnClickListener(new DefaultOnSendMessageHandler());
        this.callButton.setOnClickListener(new DefaultOnCallHandler());
        this.subscribeButton.setOnClickListener(new DefaultOnSubscribeHandler());
    }

    protected void onProfileInfoLoad(UserProfileInfo profileInfo) {
        super.onProfileInfoLoad(profileInfo);
        if (profileInfo.userInfo != null) {
            this.menuItemsVisibilityHelper.setUserProfileInfo(profileInfo);
            this.menuItemsVisibilityHelper.updateVisibility();
        }
    }

    public void notifyUser() {
        super.notifyUser();
        if (this.relationItem != null && this.profileInfo != null && this.profileInfo.userInfo != null && this.profileInfo.userInfo.genderType == UserGenderType.FEMALE) {
            this.relationItem.setTitle(2131166547);
        }
    }

    protected void updatePrivateStatus(UserInfo info) {
        int visibility;
        boolean isProfileOpened;
        int i = 0;
        super.updatePrivateStatus(info);
        if (this.profileInfo == null || !info.isPrivateProfile() || this.profileInfo.relationInfo.isFriend) {
            visibility = 0;
        } else {
            visibility = 8;
        }
        if (visibility == 0 && this.profileInfo != null) {
            this.sectionsAdapter.setCounters(this.profileInfo.counters);
        }
        setVisibilityProfileMenu(visibility);
        this.inviteButton.setVisibility(visibility);
        this.sendMessageButton.setVisibility(visibility);
        this.callButton.setVisibility(visibility);
        if (this.profileInfo == null || this.profileInfo.userInfo == null || (this.profileInfo.userInfo.isPrivateProfile() && !this.profileInfo.isFriend())) {
            isProfileOpened = false;
        } else {
            isProfileOpened = true;
        }
        this.informationLayout.setClickable(isProfileOpened);
        CarouselPresentsImageView carouselPresentsImageView = this.present;
        if (!isProfileOpened) {
            i = 8;
        }
        carouselPresentsImageView.setVisibility(i);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getActivity() != null && inflateMenuLocalized(2131689529, menu)) {
            this.menuItemsVisibilityHelper.configureMenu(menu);
            this.relationItem = menu.findItem(2131625515);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Activity activity = getActivity();
        if (activity == null) {
            return true;
        }
        switch (item.getItemId()) {
            case 2131624538:
                NavigationHelper.showMessagesForUser(activity, getUserId());
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_SEND_MESSAGE);
                return true;
            case 2131624693:
                inviteToFriends();
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_MAKE_FRIENDS);
                return true;
            case 2131625454:
                createShortLink();
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_MAKE_WEB_LINK);
                return true;
            case 2131625468:
                showDialog(ComplaintUserDialog.newInstance(getUserId()), "Complaint");
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_COMPLAIN);
                return true;
            case 2131625513:
                if (this.profileInfo == null || this.profileInfo.userInfo == null) {
                    return true;
                }
                showDialog(DeleteFriendFragmentDialog.newInstance(this.profileInfo.userInfo.getId()), "dialog_friend_delete_tag");
                return true;
            case 2131625514:
                if (!TextUtils.isEmpty(getUserId())) {
                    NavigationHelper.showExternalUrlPage(activity, WebUrlCreator.getInviteGroupsPageUrl(getUserId()), false);
                }
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_INVITE_TO_GROUP);
                return true;
            case 2131625515:
                if (!TextUtils.isEmpty(getUserId())) {
                    String uid = getUserId();
                    OdklSubActivity.startActivityShowFragment(activity, SetRelationWebFragment.class, SetRelationWebFragment.newArguments(uid, uid), false);
                }
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_SET_RELATION);
                return true;
            case 2131625516:
                if (this.profileInfo == null || this.profileInfo.userInfo == null) {
                    return true;
                }
                showDialog(BlockUserFragmentDialog.newInstance(this.profileInfo.userInfo.getId()), "dialog_friend_block_tag");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void inviteToFriends() {
        BusUsersHelper.inviteFriend(getUserId());
        this.inviteButton.setText(2131166538);
    }

    private void subscribeToStream() {
        ConfirmationDialog confirmationDialog = ConfirmationDialog.newInstance(2131166666, LocalizationManager.getString(getContext(), 2131166665) + " " + this.profileInfo.userInfo.getConcatName() + "?", 2131166658, 2131165476, (int) C0206R.styleable.Theme_checkboxStyle);
        confirmationDialog.setTargetFragment(this, C0206R.styleable.Theme_checkboxStyle);
        showDialog(confirmationDialog, "subscribe_dialog");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case C0206R.styleable.Theme_checkboxStyle /*101*/:
                if (resultCode == -1) {
                    this.subscribeButton.setEnabled(false);
                    BusUsersHelper.subscribeToUser(getUserId());
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void updateRelationInfo() {
        if (this.profileInfo.isUserBlock()) {
            this.sectionsList.setVisibility(8);
            if (this.dividerView != null) {
                this.dividerView.setVisibility(4);
            }
            this.inviteButton.setVisibility(8);
            this.sendMessageButton.setVisibility(8);
            this.callButton.setVisibility(8);
        } else if (this.profileInfo.isSentFriendInvitation()) {
            ProfilesButton profilesButton = this.inviteButton;
            int i = (this.profileInfo.userInfo == null || this.profileInfo.userInfo.genderType != UserGenderType.MALE) ? 2131166016 : 2131166015;
            profilesButton.setText(i);
            this.inviteButton.setEnabled(false);
        } else {
            this.inviteButton.setText(2131166011);
            this.inviteButton.setEnabled(true);
        }
    }

    protected void updateCanSendMessages() {
        this.sendMessageButton.setVisibility(this.profileInfo.canSendMessages() ? 0 : 8);
    }

    protected boolean isSendPresentVisible() {
        return (this.profileInfo == null || this.profileInfo.isUserBlock() || (this.profileInfo.userInfo.isPrivateProfile() && !this.profileInfo.isFriend())) ? false : true;
    }

    protected void updateIsSubscribeToStream() {
        super.updateIsSubscribeToStream();
        if (!this.profileInfo.isPremiumProfile() || this.profileInfo.isFriend()) {
            this.subscribeButton.setVisibility(8);
            return;
        }
        this.subscribeButton.setVisibility(0);
        if (this.profileInfo.isStreamSubscribe) {
            this.subscribeButton.setEnabled(false);
            this.subscribeButton.setText(2131166661);
            return;
        }
        this.subscribeButton.setEnabled(true);
        this.subscribeButton.setText(2131166658);
    }

    protected void updateCanCall() {
        super.updateCanCall();
        ProfilesButton profilesButton = this.callButton;
        int i = (this.profileInfo.isCallAvailable() && this.profileInfo.isFriend()) ? 0 : 8;
        profilesButton.setVisibility(i);
    }

    protected void updateCanFriendInvite() {
        super.updateCanFriendInvite();
        ProfilesButton profilesButton = this.inviteButton;
        int i = ((!this.profileInfo.canFriendInvite() && !this.profileInfo.isSentFriendInvitation()) || this.profileInfo.isFriend() || this.profileInfo.isPremiumProfile()) ? 8 : 0;
        profilesButton.setVisibility(i);
    }

    protected void updateMutualFriends() {
        boolean mutualFriendsBlockVisible;
        int i = 1;
        super.updateMutualFriends();
        Logger.m172d("");
        if (this.profileInfo.isFriend() || this.profileInfo.userInfo.isPrivateProfile()) {
            mutualFriendsBlockVisible = false;
        } else {
            mutualFriendsBlockVisible = true;
        }
        if (this.profileInfo.isUserBlock()) {
            i = 0;
        }
        updateFriendsBlockWithUsers(mutualFriendsBlockVisible & i ? this.profileInfo.mutualFriends : null);
    }

    protected BaseUserProfileNavigationHandler getNavigationHandler() {
        return this.handler;
    }

    protected String getUserId() {
        return getArguments().getString("user_id_extras");
    }

    @Subscribe(on = 2131623946, to = 2131624266)
    public final void onInvite(BusEvent event) {
        Context activity = getActivity();
        if (activity != null && TextUtils.equals(event.bundleInput.getString("USER_ID"), getUserId())) {
            if (event.resultCode == -1) {
                TimeToast.show(activity, 2131166014, 1);
                return;
            }
            Logger.m172d("error invite user");
            TimeToast.show(activity, 2131166012, 1);
            setSendingInviteError();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624265)
    public final void onDeleteFriend(BusEvent event) {
        if (TextUtils.equals(event.bundleInput.getString("USER_ID"), getUserId())) {
            Context context = getContext();
            if (context == null) {
                return;
            }
            if (event.resultCode == -1) {
                TimeToast.show(context, 2131165681, 1);
                return;
            }
            Logger.m172d("error delete friend");
            TimeToast.show(context, 2131165680, 1);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624137)
    public final void onComplaintToUser(BusEvent event) {
        if (event.resultCode == -1) {
            Bundle bundle = event.bundleOutput;
            if (bundle == null) {
                return;
            }
            if (bundle.getBoolean("KEY_USER_COMPLAINT_RESULT_VALUE")) {
                Logger.m172d("complaint to user Ok");
                if (bundle.getBoolean("USERS_ADD_TO_BLACKLIST")) {
                    showTimedToastIfVisible(2131165447, 1);
                    return;
                } else {
                    showTimedToastIfVisible(2131165629, 1);
                    return;
                }
            }
            Logger.m172d("complaint to user Fail");
            return;
        }
        onErrorResult(event);
    }

    @Subscribe(on = 2131623946, to = 2131624255)
    public final void onSubscribeToStream(BusEvent event) {
        if (!getUserId().equals(event.bundleOutput.getString("USER_ID"))) {
            return;
        }
        if (event.resultCode == -1) {
            showTimedToastIfVisible(2131166668, 1);
            this.subscribeButton.setText(2131166661);
            return;
        }
        this.subscribeButton.setEnabled(true);
        showTimedToastIfVisible(2131166667, 1);
    }

    public void setSendingInviteError() {
        this.inviteButton.setText(2131165791);
    }

    protected void onSendPresentClicked() {
        if (this.profileInfo != null && this.profileInfo.userInfo != null && !TextUtils.isEmpty(getUserId())) {
            String url;
            if (DateUtils.isBirthdayDate(this.profileInfo.userInfo.birthday)) {
                url = WebUrlCreator.getMakePresentPageUrl(getUserId(), null, "birthday");
            } else {
                url = ShortLinkUtils.getUrl("profile/<user_id>/sendPresent", getUserId(), "<user_id>");
            }
            NavigationHelper.showExternalUrlPage(getActivity(), url, SoftInputType.RESIZE);
            UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_MAKE_PRESENT);
        }
    }
}
