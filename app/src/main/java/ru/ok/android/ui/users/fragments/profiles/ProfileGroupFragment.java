package ru.ok.android.ui.users.fragments.profiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.GeneralDataLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.google.android.gms.plus.PlusShare;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;
import ru.ok.android.C0206R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.GroupsStorageFacade;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.web.shortlinks.ShortLink;
import ru.ok.android.model.image.ImageForUpload;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.custom.profiles.ProfilesButton;
import ru.ok.android.ui.dialogs.ComplaintGroupDialog;
import ru.ok.android.ui.dialogs.ComplaintGroupDialog.OnSelectComplaintGroupDataListener;
import ru.ok.android.ui.dialogs.ConfirmationDialog;
import ru.ok.android.ui.groups.activity.SelectFriendsForGroupActivity;
import ru.ok.android.ui.groups.data.GroupProfileInfo;
import ru.ok.android.ui.groups.data.GroupProfileLoader;
import ru.ok.android.ui.groups.data.GroupSectionItem;
import ru.ok.android.ui.groups.fragments.GroupAboutFragment;
import ru.ok.android.ui.measuredobserver.MeasureObservable;
import ru.ok.android.ui.measuredobserver.MeasureObservable.MeasureObservableHelper;
import ru.ok.android.ui.users.UsersSelectionParams;
import ru.ok.android.ui.users.fragments.data.ProfileSectionsAdapter;
import ru.ok.android.ui.users.fragments.profiles.ProfileLoadCallBack.ProfileAccessInfo;
import ru.ok.android.ui.users.fragments.profiles.ProfileLoadCallBack.ProfileType;
import ru.ok.android.ui.users.fragments.profiles.statistics.GroupsProfileStatisticsManager;
import ru.ok.android.ui.users.fragments.profiles.statistics.UserProfileStatisticsManager;
import ru.ok.android.ui.users.fragments.utils.GroupProfileMenuItemsVisibilityHelper;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.android.utils.bus.BusGroupsHelper;
import ru.ok.android.utils.indexing.Action;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.exceptions.NotSessionKeyException;
import ru.ok.java.api.json.users.ComplaintType;
import ru.ok.java.api.response.groups.GroupCounters;
import ru.ok.model.GroupInfo;
import ru.ok.model.GroupType;
import ru.ok.model.GroupUserStatus;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.onelog.groups.GroupJoinClickFactory;
import ru.ok.onelog.groups.GroupJoinClickSource;

public class ProfileGroupFragment extends ProfileBaseFragment implements LoaderCallbacks<GroupProfileInfo>, OnClickListener, OnSelectComplaintGroupDataListener {
    private ProfilesButton addFriendsButton;
    private TextView additional;
    private TextView countMembersText;
    private TextView created;
    private View divider;
    private GroupProfileInfo groupProfileInfo;
    private GroupProfileNavigationHandler handler;
    private ProfilesButton happeningInvite;
    private ProfilesButton happeningMayInvite;
    private boolean hasRefreshedAfterCreate;
    private View informationLayout;
    private ProfilesButton inviteButton;
    private TextView location;
    private View mainView;
    private MeasureObservableHelper measureObservableHelper;
    private GroupProfileMenuItemsVisibilityHelper menuItemsVisibilityHelper;
    private TextView name;
    private ProfileInfoViewChangeObserver notifyProfileDataObserver;
    private OnLeaveGroupListener onLeaveGroupListener;
    private View premiumView;
    private View privateView;
    private ProfileSectionsAdapter<GroupSectionItem, GroupCounters> sectionsAdapter;
    protected AdapterView<ListAdapter> sectionsList;
    private MenuItem subscribeItem;

    public interface OnLeaveGroupListener {
        void onGroupLeave();
    }

    /* renamed from: ru.ok.android.ui.users.fragments.profiles.ProfileGroupFragment.1 */
    class C13391 implements OnClickListener {
        final /* synthetic */ Activity val$activity;

        C13391(Activity activity) {
            this.val$activity = activity;
        }

        public void onClick(View v) {
            if (ProfileGroupFragment.this.groupProfileInfo != null && ProfileGroupFragment.this.groupProfileInfo.groupInfo != null) {
                NavigationHelper.showExternalUrlPage(this.val$activity, WebUrlCreator.getUrl(GroupSectionItem.MEMBERS.getMethodName(), ProfileGroupFragment.this.groupProfileInfo.groupInfo.getId(), null), false);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.profiles.ProfileGroupFragment.2 */
    class C13402 implements OnGlobalLayoutListener {
        int measureHeight;

        C13402() {
            this.measureHeight = -1;
        }

        public void onGlobalLayout() {
            if (this.measureHeight != ProfileGroupFragment.this.getView().getMeasuredHeight() && ProfileGroupFragment.this.notifyProfileDataObserver != null && ProfileGroupFragment.this.notifyProfileDataObserver.hasChanged()) {
                this.measureHeight = ProfileGroupFragment.this.getView().getMeasuredHeight();
                ProfileGroupFragment.this.notifyProfileDataObserver.notifyViewChange();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.profiles.ProfileGroupFragment.3 */
    class C13413 implements Observer {
        C13413() {
        }

        public void update(Observable observable, Object data) {
            ProfileGroupFragment.this.measureObservableHelper.onMeasure((View) data);
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.profiles.ProfileGroupFragment.4 */
    class C13424 implements Runnable {
        C13424() {
        }

        public void run() {
            GroupsStorageFacade.updateStatusToGroup(OdnoklassnikiApplication.getCurrentUser().getId(), ProfileGroupFragment.this.getGroupId(), GroupUserStatus.PASSIVE);
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.profiles.ProfileGroupFragment.5 */
    static /* synthetic */ class C13435 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$GroupUserStatus;

        static {
            $SwitchMap$ru$ok$model$GroupUserStatus = new int[GroupUserStatus.values().length];
            try {
                $SwitchMap$ru$ok$model$GroupUserStatus[GroupUserStatus.ACTIVE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupUserStatus[GroupUserStatus.MAYBE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupUserStatus[GroupUserStatus.BLOCKED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupUserStatus[GroupUserStatus.ADMIN.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupUserStatus[GroupUserStatus.MODERATOR.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupUserStatus[GroupUserStatus.PASSIVE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$model$GroupUserStatus[GroupUserStatus.UNKNOWN.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    private class DefaultAddFriendsClickListener implements OnClickListener {
        private DefaultAddFriendsClickListener() {
        }

        public void onClick(View v) {
            ProfileGroupFragment.this.showInviteFriends();
            GroupsProfileStatisticsManager.sendStatEvent(GroupsProfileStatisticsManager.ACTION_INVITE_FRIENDS);
        }
    }

    private class DefaultInformationClickListener implements OnClickListener {
        private DefaultInformationClickListener() {
        }

        public void onClick(View v) {
            ProfileGroupFragment.this.showGroupAboutInfo();
            GroupsProfileStatisticsManager.sendStatEvent(GroupsProfileStatisticsManager.ACTION_NAMEZONE);
        }
    }

    private class DefaultInviteClickListener implements OnClickListener {
        private DefaultInviteClickListener() {
        }

        public void onClick(View v) {
            showAlert();
            OneLog.log(GroupJoinClickFactory.get(GroupJoinClickSource.group_profile));
            GroupsProfileStatisticsManager.sendStatEvent(GroupsProfileStatisticsManager.ACTION_JOIN_GROUP);
        }

        private void showAlert() {
            if (ProfileGroupFragment.this.getActivity() != null && ProfileGroupFragment.this.groupProfileInfo != null && ProfileGroupFragment.this.groupProfileInfo.groupInfo != null) {
                BusGroupsHelper.inviteToGroup(ProfileGroupFragment.this.groupProfileInfo.groupInfo.getId());
            }
        }
    }

    private class DefaultInviteHappeningClickListener implements OnClickListener {
        private DefaultInviteHappeningClickListener() {
        }

        public void onClick(View v) {
            ProfileGroupFragment.this.happeningInvite.setEnabled(false);
            BusGroupsHelper.inviteToGroup(ProfileGroupFragment.this.getGroupId());
        }
    }

    private class DefaultMayInviteHappeningClickListener implements OnClickListener {
        private DefaultMayInviteHappeningClickListener() {
        }

        public void onClick(View v) {
            ProfileGroupFragment.this.happeningMayInvite.setEnabled(false);
            BusGroupsHelper.inviteToGroup(ProfileGroupFragment.this.getGroupId(), true);
        }
    }

    public ProfileGroupFragment() {
        this.menuItemsVisibilityHelper = new GroupProfileMenuItemsVisibilityHelper();
        this.notifyProfileDataObserver = new ProfileInfoViewChangeObserver();
        this.measureObservableHelper = new MeasureObservableHelper();
    }

    public static ProfileGroupFragment newInstance(String groupId) {
        ProfileGroupFragment f = new ProfileGroupFragment();
        Bundle args = new Bundle();
        args.putString("group_id_extras", groupId);
        f.setArguments(args);
        return f;
    }

    protected int getLayoutId() {
        return 2130903232;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int newAlpha = 0;
        Context activity = getActivity();
        this.mainView = LocalizationManager.inflate(activity, getLayoutId(), null, false);
        this.informationLayout = this.mainView.findViewById(2131624912);
        this.name = (TextView) this.mainView.findViewById(C0263R.id.name);
        this.additional = (TextView) this.mainView.findViewById(2131624922);
        this.created = (TextView) this.mainView.findViewById(2131624923);
        this.location = (TextView) this.mainView.findViewById(2131624924);
        this.inviteButton = (ProfilesButton) this.mainView.findViewById(2131624926);
        this.addFriendsButton = (ProfilesButton) this.mainView.findViewById(2131624929);
        this.happeningInvite = (ProfilesButton) this.mainView.findViewById(2131624927);
        this.happeningMayInvite = (ProfilesButton) this.mainView.findViewById(2131624928);
        this.inviteButton.setOnClickListener(new DefaultInviteClickListener());
        this.addFriendsButton.setOnClickListener(new DefaultAddFriendsClickListener());
        this.happeningInvite.setOnClickListener(new DefaultInviteHappeningClickListener());
        this.happeningMayInvite.setOnClickListener(new DefaultMayInviteHappeningClickListener());
        this.informationLayout.setOnClickListener(new DefaultInformationClickListener());
        this.privateView = this.mainView.findViewById(2131624920);
        this.premiumView = this.mainView.findViewById(2131624921);
        this.countMembersText = (TextView) this.mainView.findViewById(2131624925);
        this.countMembersText.setOnClickListener(new C13391(activity));
        this.handler = new GroupProfileNavigationHandler(activity);
        this.handler.setGroupId(getGroupId());
        this.sectionsAdapter = this.handler.getSectionsAdapter();
        this.sectionsAdapter.swapData(GroupSectionItem.GENERAL_LIST);
        this.sectionsList = (AdapterView) this.mainView.findViewById(2131624914);
        if (this.sectionsList == null) {
            this.sectionsList = (AdapterView) this.mainView.findViewById(2131624918);
        }
        this.sectionsList.setAdapter(this.sectionsAdapter);
        this.sectionsList.setOnItemClickListener(this.handler);
        this.divider = this.mainView.findViewById(2131624602);
        this.mainView.getViewTreeObserver().addOnGlobalLayoutListener(new C13402());
        if (this.mainView instanceof MeasureObservable) {
            ((MeasureObservable) this.mainView).addMeasureObserver(new C13413());
        }
        if (activity instanceof BaseCompatToolbarActivity) {
            if (!DeviceUtils.isSmall(activity)) {
                newAlpha = MotionEventCompat.ACTION_MASK;
            }
            BaseCompatToolbarActivity toolbarActivity = (BaseCompatToolbarActivity) activity;
            toolbarActivity.setToolbarTitleTextAlpha(newAlpha);
            toolbarActivity.setShadowAlpha(newAlpha);
        }
        initLoader();
        return this.mainView;
    }

    public void onResume() {
        super.onResume();
        updateGroupName();
    }

    protected boolean isIndexingFragment() {
        return true;
    }

    protected Action createIndexingAction() {
        if (this.groupProfileInfo == null || this.groupProfileInfo.groupInfo == null) {
            return super.createIndexingAction();
        }
        return Action.newAction("http://schema.org/ViewAction", this.groupProfileInfo.groupInfo.getName(), Uri.parse("http://ok.ru/group/" + getGroupId()), Uri.parse("android-app://ru.ok.android/odnoklassniki/ok.ru/group/" + getGroupId()));
    }

    public void setOnLeaveGroupListener(OnLeaveGroupListener onLeaveGroupListener) {
        this.onLeaveGroupListener = onLeaveGroupListener;
    }

    public GroupProfileInfo getGroupProfileInfo() {
        return this.groupProfileInfo;
    }

    protected String getGroupId() {
        return getArguments().getString("group_id_extras");
    }

    public void setProfileInfo(GroupProfileInfo profileInfo) {
        this.groupProfileInfo = profileInfo;
    }

    public void addViewChangeObserver(Observer observer) {
        this.notifyProfileDataObserver.addObserver(observer);
    }

    public void removeViewChangeObserver(Observer observer) {
        this.notifyProfileDataObserver.deleteObserver(observer);
    }

    public void setLoadCallBack(ProfileLoadCallBack loadCallBack) {
        this.loadCallBack = loadCallBack;
    }

    protected void initLoader() {
        Logger.m172d("init loader");
        getLoaderManager().initLoader(11, null, this);
    }

    protected GeneralDataLoader getLoader() {
        if (getActivity() != null) {
            return (GeneralDataLoader) getLoaderManager().getLoader(11);
        }
        return null;
    }

    private void startProfileUpdate() {
        BusGroupsHelper.getGroupInfo(getGroupId());
        BusGroupsHelper.friendsInGroup(getGroupId(), false, false);
    }

    public Loader<GroupProfileInfo> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case Message.EDITINFO_FIELD_NUMBER /*11*/:
                Logger.m182v("User Loader Create: " + getGroupId());
                return new GroupProfileLoader(getActivity(), getGroupId());
            default:
                return null;
        }
    }

    @Subscribe(on = 2131623946, to = 2131624171)
    public void onGroupInfo(BusEvent e) {
        if (TextUtils.equals(e.bundleInput.getString("GROUP_ID"), getGroupId())) {
            boolean isRefresh = e.bundleInput.getBoolean("GROUP_REFRESH");
            if (e.resultCode != -1) {
                ErrorType errorType = getErrorType(e);
                onProfileInfoLoadError(ProfileType.GROUP, errorType);
                if (isRefresh) {
                    onErrorResult(errorType);
                }
            } else if (isRefresh) {
                boolean isPrivate = e.bundleOutput.getBoolean("GROUP_RESULT_INFO_PRIVATE");
                boolean isDisabled = e.bundleOutput.getBoolean("GROUP_RESULT_INFO_DISABLED");
                GroupUserStatus status = (GroupUserStatus) e.bundleOutput.getSerializable("GROUP_RESULT_INFO_STATUS");
                if (status == null) {
                    status = GroupUserStatus.PASSIVE;
                }
                onProfileInfoRefreshFinish(createAccessInfo(status, isPrivate, isDisabled));
            }
        }
    }

    public boolean isProfileInfoAvailable() {
        return (this.groupProfileInfo == null || this.groupProfileInfo.groupInfo == null) ? false : true;
    }

    public void onLoadFinished(Loader<GroupProfileInfo> loader, GroupProfileInfo groupInfo) {
        if (groupInfo == null) {
            onProfileInfoLoadError(ProfileType.GROUP, ErrorType.GENERAL);
        } else if (groupInfo.groupInfo != null) {
            onProfileInfoLoad(groupInfo);
        } else if (this.pendingError != null) {
            onProfileInfoLoadError(ProfileType.GROUP, this.pendingError);
        }
        if (!this.hasRefreshedAfterCreate) {
            startProfileUpdate();
            this.hasRefreshedAfterCreate = true;
        }
    }

    public void onLoaderReset(Loader<GroupProfileInfo> loader) {
        Logger.m182v("Group Loader Reset");
    }

    protected void onProfileInfoRefreshFinish(ProfileAccessInfo info) {
        if (this.loadCallBack != null) {
            this.loadCallBack.onProfileRefresh(ProfileType.GROUP, info);
        }
    }

    protected void onProfileInfoLoad(GroupProfileInfo profileInfo) {
        if (this.groupProfileInfo == null) {
            setProfileInfo(profileInfo);
            restartAppIndexing();
        } else {
            setProfileInfo(profileInfo);
        }
        notifyProfileInfo();
        this.menuItemsVisibilityHelper.setGroupProfileInfo(profileInfo);
        if (this.loadCallBack != null) {
            this.loadCallBack.onProfileInfoLoad(ProfileType.GROUP, createAccessInfo(profileInfo.userStatus, profileInfo.groupInfo.isPrivateGroup(), profileInfo.groupInfo.isDisabled()));
        }
        Logger.m172d("Group Loader loaded");
    }

    private ProfileAccessInfo createAccessInfo(GroupUserStatus status, boolean isPrivate, boolean isDisabled) {
        boolean z = false;
        boolean z2 = status == GroupUserStatus.BLOCKED;
        if (status == GroupUserStatus.ACTIVE || status == GroupUserStatus.ADMIN || status == GroupUserStatus.MODERATOR) {
            z = true;
        }
        return new ProfileAccessInfo(z2, isPrivate, z, isDisabled);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.invalidateOptionsMenu();
    }

    private void notifyProfileInfo() {
        if (getActivity() != null && this.groupProfileInfo != null) {
            boolean isAdmin;
            this.inviteButton.setText(2131166022);
            this.sectionsAdapter.setCounters(this.groupProfileInfo.counters);
            GroupUserStatus status = this.groupProfileInfo.userStatus;
            if (status == GroupUserStatus.ADMIN || status == GroupUserStatus.MODERATOR) {
                isAdmin = true;
            } else {
                isAdmin = false;
            }
            this.sectionsAdapter.swapData(isAdmin ? GroupSectionItem.ADMIN_LIST : GroupSectionItem.GENERAL_LIST);
            this.sectionsList.setVisibility(0);
            if (this.divider != null) {
                this.divider.setVisibility(0);
            }
            if (!(this.countMembersText == null || this.groupProfileInfo.counters == null || this.groupProfileInfo.groupInfo.isPrivateGroup())) {
                int count = this.groupProfileInfo.counters.members;
                if (count > 0) {
                    this.countMembersText.setText(LocalizationManager.getString(getContext(), StringUtils.plural((long) count, 2131166186, 2131166187, 2131166188), Integer.valueOf(count)));
                    this.countMembersText.setVisibility(0);
                }
            }
            updateStatus(status, this.groupProfileInfo.groupInfo.getType());
            notifyGroupInfo();
            if (this.groupProfileInfo.groupInfo.isDisabled()) {
                setEnabledSectionList(false);
                this.sectionsList.setVisibility(8);
                this.inviteButton.setVisibility(8);
            }
            this.notifyProfileDataObserver.profileDataChange();
        }
    }

    protected void notifyGroupInfo() {
        if (getActivity() != null && this.groupProfileInfo != null && this.groupProfileInfo.groupInfo != null) {
            GroupInfo groupInfo = this.groupProfileInfo.groupInfo;
            if (groupInfo.isAllDataAvailable()) {
                boolean showFriends;
                List list;
                this.name.setText(groupInfo.getName());
                Utils.setTextViewTextWithVisibility(this.additional, groupInfo.getDescription());
                updateGroupName();
                if (this.groupProfileInfo.groupInfo.getType() == GroupType.HAPPENING) {
                    if (groupInfo.getStartDate() > 0) {
                        String dateString = DateFormatter.getFormatStringFromDate(getContext(), groupInfo.getStartDate(), TimeZone.getTimeZone("Europe/Moscow").getID());
                        if (groupInfo.getEndDate() > 0) {
                            dateString = dateString + " - " + DateFormatter.getFormatStringFromDate(getContext(), groupInfo.getEndDate(), TimeZone.getTimeZone("Europe/Moscow").getID());
                        }
                        Utils.setTextViewTextWithVisibility(this.created, dateString);
                    }
                    if (groupInfo.getAddress() != null) {
                        Utils.setTextViewTextWithVisibility(this.location, groupInfo.getAddress().getStringAddress());
                    }
                }
                this.premiumView.setVisibility(this.groupProfileInfo.groupInfo.isPremium() ? 0 : 8);
                notifyPrivateGroup(this.groupProfileInfo.groupInfo.isPrivateGroup());
                processAvatarUrl(groupInfo.getBigPicUrl(), groupInfo.getPicUrl());
                if ((this.groupProfileInfo.userStatus == GroupUserStatus.PASSIVE || this.groupProfileInfo.userStatus == null) && !this.groupProfileInfo.groupInfo.isPrivateGroup()) {
                    showFriends = true;
                } else {
                    showFriends = false;
                }
                if (showFriends) {
                    list = this.groupProfileInfo.friendsInGroup;
                } else {
                    list = null;
                }
                updateFriendsBlockWithUsers(list);
                return;
            }
            processAvatarUrl(groupInfo.getBigPicUrl(), groupInfo.getPicUrl());
        }
    }

    private void updateGroupName() {
        String groupName = getGroupName();
        if (groupName != null && (getActivity() instanceof BaseCompatToolbarActivity)) {
            ((BaseCompatToolbarActivity) getActivity()).setToolbarTitle(groupName);
        }
    }

    @Nullable
    public String getGroupName() {
        if (this.groupProfileInfo == null || this.groupProfileInfo.groupInfo == null) {
            return null;
        }
        return this.groupProfileInfo.groupInfo.getName();
    }

    protected int getFriendsPrefixResId() {
        return 2131165888;
    }

    protected void logMainPhotoClick() {
        GroupsProfileStatisticsManager.sendStatEvent(GroupsProfileStatisticsManager.ACTION_AVATAR_CLICK);
    }

    protected void showMainPhoto() {
        if (getActivity() == null) {
            return;
        }
        if (this.groupProfileInfo == null || this.groupProfileInfo.groupInfo == null || TextUtils.isEmpty(this.groupProfileInfo.groupInfo.getPhotoId())) {
            Logger.m172d("no main photo");
        } else {
            NavigationHelper.showPhoto(getActivity(), new PhotoOwner(getGroupId(), 1), null, this.groupProfileInfo.groupInfo.getPhotoId(), 8);
        }
    }

    protected int getDefaultImageResId() {
        if (this.groupProfileInfo == null) {
            return 0;
        }
        GroupInfo groupInfo = this.groupProfileInfo.groupInfo;
        if (groupInfo != null) {
            return groupInfo.getType() == GroupType.HAPPENING ? 2130838599 : 2130838601;
        } else {
            return 0;
        }
    }

    private void notifyPrivateGroup(boolean isPrivate) {
        int i = 8;
        this.privateView.setVisibility(isPrivate ? 0 : 8);
        AdapterView adapterView = this.sectionsList;
        if (!(isPrivate && (this.groupProfileInfo.userStatus == null || this.groupProfileInfo.userStatus == GroupUserStatus.PASSIVE))) {
            i = 0;
        }
        adapterView.setVisibility(i);
    }

    private void updateStatus(GroupUserStatus status, GroupType type) {
        int i = 0;
        executePendingJoinGroupAction(status);
        if (status == null) {
            setInviteVisibility(0, type);
            return;
        }
        ProfilesButton profilesButton;
        ProfilesButton profilesButton2;
        int i2;
        switch (C13435.$SwitchMap$ru$ok$model$GroupUserStatus[status.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                profilesButton = this.addFriendsButton;
                if (!this.groupProfileInfo.groupInfo.isCanAddFriends()) {
                    i = 8;
                }
                profilesButton.setVisibility(i);
                setInviteVisibility(8, type);
                return;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                profilesButton2 = this.addFriendsButton;
                if (this.groupProfileInfo.groupInfo.isCanAddFriends()) {
                    i2 = 0;
                } else {
                    i2 = 8;
                }
                profilesButton2.setVisibility(i2);
                this.happeningInvite.setVisibility(0);
                this.happeningMayInvite.setVisibility(8);
                this.inviteButton.setVisibility(8);
                return;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                setEnabledSectionList(false);
                this.addFriendsButton.setVisibility(4);
                setInviteVisibility(8, type);
                return;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                profilesButton2 = this.addFriendsButton;
                if (this.groupProfileInfo.groupInfo.isCanAddFriends()) {
                    i2 = 0;
                } else {
                    i2 = 8;
                }
                profilesButton2.setVisibility(i2);
                setInviteVisibility(8, type);
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                break;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                this.addFriendsButton.setVisibility(8);
                setInviteVisibility(0, type);
                return;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                this.addFriendsButton.setVisibility(8);
                setInviteVisibility(8, type);
                return;
            default:
                return;
        }
        profilesButton = this.addFriendsButton;
        if (!this.groupProfileInfo.groupInfo.isCanAddFriends()) {
            i = 8;
        }
        profilesButton.setVisibility(i);
        setInviteVisibility(8, type);
    }

    private void setInviteVisibility(int visibility, GroupType type) {
        if (type == GroupType.HAPPENING) {
            this.happeningInvite.setVisibility(visibility);
            this.happeningMayInvite.setVisibility(visibility);
            this.inviteButton.setVisibility(8);
            return;
        }
        this.happeningInvite.setVisibility(8);
        this.happeningMayInvite.setVisibility(8);
        this.inviteButton.setVisibility(visibility);
    }

    private void executePendingJoinGroupAction(GroupUserStatus status) {
        boolean maybe = true;
        if ("action_join".equals(this.pendingAction)) {
            boolean canJoin = status == null || status == GroupUserStatus.PASSIVE || status == GroupUserStatus.MAYBE;
            if (canJoin) {
                if (status != GroupUserStatus.MAYBE) {
                    maybe = false;
                }
                BusGroupsHelper.inviteToGroup(getGroupId(), maybe);
            } else {
                Context context = getContext();
                if (context != null) {
                    showTimedToastIfVisible(LocalizationManager.getString(context, 2131165944), 1);
                }
            }
            this.pendingAction = null;
        }
    }

    protected void setEnabledSectionList(boolean enabled) {
        this.sectionsList.setEnabled(enabled);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getActivity() != null && inflateMenuLocalized(2131689491, menu)) {
            this.menuItemsVisibilityHelper.configureMenu(menu);
            this.subscribeItem = menu.findItem(2131625467);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625453:
                changeMainPhoto();
                GroupsProfileStatisticsManager.sendStatEvent(GroupsProfileStatisticsManager.ACTION_SELECT_AVATAR);
                return true;
            case 2131625454:
                createShortLink();
                GroupsProfileStatisticsManager.sendStatEvent(GroupsProfileStatisticsManager.ACTION_MAKE_WEB_LINK);
                return true;
            case 2131625464:
                BusGroupsHelper.inviteToGroup(this.groupProfileInfo.groupInfo.getId(), true);
                return true;
            case 2131625465:
                leaveGroup();
                GroupsProfileStatisticsManager.sendStatEvent(GroupsProfileStatisticsManager.ACTION_DELETE_GROUP);
                return true;
            case 2131625466:
                showGroupSettings();
                GroupsProfileStatisticsManager.sendStatEvent(GroupsProfileStatisticsManager.ACTION_CHANGE_SETTINGS);
                return true;
            case 2131625467:
                subscribeToStream();
                GroupsProfileStatisticsManager.sendStatEvent(GroupsProfileStatisticsManager.ACTION_SUBSCRIBE_TO_GROUP);
                return true;
            case 2131625468:
                complaintToGroup();
                UserProfileStatisticsManager.sendStatEvent(GroupsProfileStatisticsManager.ACTION_COMPLAIN);
                return true;
            default:
                return false;
        }
    }

    public void updateProfile() {
        BusGroupsHelper.refreshGroupInfo(getGroupId());
        BusGroupsHelper.friendsInGroup(getGroupId(), false, false);
    }

    protected void complaintToGroup() {
        if (getActivity() != null && this.groupProfileInfo != null) {
            ComplaintGroupDialog dialog = ComplaintGroupDialog.newInstance();
            dialog.setTargetFragment(this, 0);
            showDialog(dialog, "Complaint_group");
        }
    }

    protected void createShortLink() {
        if (getActivity() != null) {
            ShortLink.createGroupProfileLink(getGroupId()).copy(getActivity(), true);
        }
    }

    private void showInviteFriends() {
        Intent selectFriends = new Intent(getActivity(), SelectFriendsForGroupActivity.class);
        UsersSelectionParams selectionParams = new UsersSelectionParams(new ArrayList(), Integer.MAX_VALUE);
        selectFriends.putExtra(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, 2131166502);
        selectFriends.putExtra("GROUP_ID", getGroupId());
        selectFriends.putExtra("selection_params", selectionParams);
        selectFriends.putExtra("select_target", 3);
        startActivityForResult(selectFriends, 7);
    }

    private void showGroupAboutInfo() {
        if (getActivity() != null) {
            showDialog(GroupAboutFragment.newInstance(getGroupId()), "about_group");
        }
    }

    private void changeMainPhoto() {
        if (getActivity() != null) {
            PhotoAlbumInfo albumInfo = new PhotoAlbumInfo();
            albumInfo.setOwnerType(OwnerType.GROUP);
            albumInfo.setGroupId(getGroupId());
            albumInfo.setId("group_main");
            albumInfo.setTitle(getStringLocalized(2131165933));
            NavigationHelper.startPhotoUploadSequence(getActivity(), albumInfo, 1, 1);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624225)
    public void onImageUploaded(BusEvent event) {
        if (event.resultCode == 1) {
            ImageForUpload image = (ImageForUpload) event.bundleOutput.getParcelable("img");
            if (image != null && image.getCurrentStatus() == 5 && image.getUploadTarget() == 1 && TextUtils.equals(getGroupId(), image.getAlbumInfo().getGroupId())) {
                BusGroupsHelper.getGroupInfo(getGroupId());
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                onSelectFriendsResult(resultCode, data);
            case C0206R.styleable.Theme_checkedTextViewStyle /*102*/:
                this.subscribeItem.setEnabled(false);
                BusGroupsHelper.subscribeToGroup(getGroupId());
            default:
        }
    }

    protected void onSelectFriendsResult(int resultCode, Intent data) {
        if (resultCode == -1 && data != null && data.hasExtra("selected_ids")) {
            setSendingFriendsInvite(data.getStringArrayListExtra("selected_ids"));
        }
    }

    private void showGroupSettings() {
        try {
            NavigationHelper.showExternalUrlPage(getActivity(), WebUrlCreator.getGroupSettingsPageUrl(getGroupId(), true), false);
        } catch (NotSessionKeyException e) {
            Logger.m172d("no session key error");
        }
    }

    private void subscribeToStream() {
        if (getActivity() != null && this.groupProfileInfo != null) {
            ConfirmationDialog confirmationDialog = ConfirmationDialog.newInstance(2131166666, LocalizationManager.getString(getContext(), 2131166664) + " " + this.groupProfileInfo.groupInfo.getName() + "?", 2131166658, 2131165476, (int) C0206R.styleable.Theme_checkedTextViewStyle);
            confirmationDialog.setTargetFragment(this, C0206R.styleable.Theme_checkedTextViewStyle);
            showDialog(confirmationDialog, "subscribe_dialog");
        }
    }

    public void onSelectComplaintGroupData(ComplaintType type) {
        BusGroupsHelper.complaintToGroup(getGroupId(), type);
    }

    private void leaveGroup() {
        BusGroupsHelper.leaveGroup(getGroupId());
    }

    @Subscribe(on = 2131623946, to = 2131624173)
    public final void onInviteGroupResult(BusEvent event) {
        this.inviteButton.setEnabled(true);
        this.happeningInvite.setEnabled(true);
        this.happeningMayInvite.setEnabled(true);
        if (event.resultCode == -1) {
            FragmentActivity activity = getActivity();
            Bundle bundle = event.bundleOutput;
            if (activity != null && bundle != null && bundle.getBoolean("GROUP_INVITE_RESULT_VALUE") && this.groupProfileInfo != null && this.groupProfileInfo.groupInfo != null && this.groupProfileInfo.groupInfo.isPrivateGroup()) {
                showTimedToastIfVisible(getStringLocalized(2131165943), 0);
                return;
            }
            return;
        }
        onErrorResult(event);
    }

    @Subscribe(on = 2131623946, to = 2131624174)
    public final void onLeaveGroupResult(BusEvent event) {
        if (event.resultCode == -1) {
            Bundle bundle = event.bundleOutput;
            if (bundle != null && bundle.getBoolean("GROUP_LEAVE_RESULT_VALUE")) {
                ThreadUtil.execute(new C13424());
                if (this.onLeaveGroupListener != null) {
                    this.onLeaveGroupListener.onGroupLeave();
                }
                showTimedToastIfVisible(2131165948, 1);
                return;
            }
            return;
        }
        onErrorResult(event);
    }

    @Subscribe(on = 2131623946, to = 2131624247)
    public final void onSubscribeToStream(BusEvent event) {
        if (!getGroupId().equals(event.bundleOutput.getString("GROUP_ID"))) {
            return;
        }
        if (event.resultCode == -1) {
            Bundle bundle = event.bundleOutput;
            if (bundle == null) {
                return;
            }
            if (bundle.getBoolean("GROUP_SUBSCRIBE_RESULT_VALUE")) {
                showTimedToastIfVisible(2131166663, 1);
                return;
            } else {
                showTimedToastIfVisible(2131166662, 1);
                return;
            }
        }
        this.subscribeItem.setEnabled(true);
        onErrorResult(event);
    }

    @Subscribe(on = 2131623946, to = 2131624172)
    public final void onFriendInviteToGroup(BusEvent event) {
        if (!getGroupId().equals(event.bundleInput.getString("GROUP_ID"))) {
            return;
        }
        if (event.resultCode == -1) {
            Bundle bundle = event.bundleOutput;
            if (bundle != null && bundle.getStringArrayList("GROUP_FRIENDS_IDS") != null) {
                showTimedToastIfVisible(2131166021, 1);
                return;
            }
            return;
        }
        this.subscribeItem.setEnabled(true);
        onErrorResult(event);
    }

    @Subscribe(on = 2131623946, to = 2131624136)
    public final void onComplaintToGroup(BusEvent event) {
        if (event.resultCode == -1) {
            Bundle bundle = event.bundleOutput;
            if (bundle != null) {
                if (bundle.getBoolean("GROUP_COMPLAINT_RESULT_VALUE")) {
                    Logger.m172d("complaint to group Ok");
                } else {
                    Logger.m172d("complaint to group Fail");
                }
                showTimedToastIfVisible(2131165629, 1);
                return;
            }
            return;
        }
        onErrorResult(event);
    }

    @Subscribe(on = 2131623946, to = 2131624226)
    public void onGroupTopicLoad(BusEvent event) {
        if (isVisible()) {
            String groupId = event.bundleInput == null ? null : event.bundleInput.getString("group_id");
            if (!TextUtils.isEmpty(groupId) && groupId.equals(getGroupId())) {
                updateProfile();
            }
        }
    }

    private void setSendingFriendsInvite(ArrayList<String> selectedUids) {
        if (selectedUids.size() > 0) {
            BusGroupsHelper.inviteFriendsToGroup(getGroupId(), selectedUids);
        }
    }

    protected void showDialog(DialogFragment dialogFragment, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.add((Fragment) dialogFragment, tag);
        fragmentTransaction.commit();
    }
}
