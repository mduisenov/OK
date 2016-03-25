package ru.ok.android.ui.users.fragments.profiles;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.ExternalUrlWebFragment;
import ru.ok.android.fragments.web.shortlinks.ShortLink;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.model.music.MusicInfoContainer;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.app.MusicService.InformationState;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.custom.imageview.CarouselPresentsImageView;
import ru.ok.android.ui.custom.profiles.ProfilesButton;
import ru.ok.android.ui.custom.profiles.StatusView;
import ru.ok.android.ui.custom.profiles.StatusView.OnStatusListener;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.ui.measuredobserver.MeasureObservable;
import ru.ok.android.ui.users.fragments.data.ProfileSectionsAdapter;
import ru.ok.android.ui.users.fragments.data.UserMergedPresent;
import ru.ok.android.ui.users.fragments.data.UserProfileInfo;
import ru.ok.android.ui.users.fragments.data.UserProfileInfoLoader;
import ru.ok.android.ui.users.fragments.data.UserSectionItem;
import ru.ok.android.ui.users.fragments.profiles.ProfileLoadCallBack.ProfileAccessInfo;
import ru.ok.android.ui.users.fragments.profiles.ProfileLoadCallBack.ProfileType;
import ru.ok.android.ui.users.fragments.profiles.statistics.UserProfileStatisticsManager;
import ru.ok.android.ui.users.fragments.utils.ProfileUtils;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.android.utils.bus.BusProtocol;
import ru.ok.android.utils.bus.BusUsersHelper;
import ru.ok.android.utils.indexing.Action;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.users.FriendRelativeType;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;
import ru.ok.java.api.response.users.FriendRelation;
import ru.ok.java.api.response.users.UserCounters;
import ru.ok.java.api.utils.DateUtils;
import ru.ok.model.Discussion;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.UserInfo.UserOnlineType;
import ru.ok.model.UserStatus;

public abstract class ProfileUserFragment extends ProfileBaseFragment implements LoaderCallbacks<UserProfileInfo>, OnStatusListener {
    private TextView additional;
    protected View birthdayView;
    protected ProfilesButton callButton;
    protected View dividerView;
    protected View informationLayout;
    protected ProfilesButton inviteButton;
    protected View mainView;
    private TextView name;
    private View onlineLayout;
    private TextView onlineText;
    private ImageView onlineView;
    protected View premiumView;
    protected CarouselPresentsImageView present;
    protected View privateView;
    protected UserProfileInfo profileInfo;
    private TextView relations;
    protected ProfileSectionsAdapter<UserSectionItem, UserCounters> sectionsAdapter;
    protected AdapterView<ListAdapter> sectionsList;
    protected ProfilesButton sendMessageButton;
    private ProfilesButton sendPresentButton;
    protected View serviceInvisibleView;
    protected StatusView statusView;
    protected ProfilesButton subscribeButton;

    /* renamed from: ru.ok.android.ui.users.fragments.profiles.ProfileUserFragment.1 */
    class C13441 implements OnGlobalLayoutListener {
        int measureHeight;

        C13441() {
            this.measureHeight = -1;
        }

        public void onGlobalLayout() {
            if (this.measureHeight != ProfileUserFragment.this.getView().getMeasuredHeight() && ProfileUserFragment.this.notifyProfileDataObserver.hasChanged()) {
                this.measureHeight = ProfileUserFragment.this.getView().getMeasuredHeight();
                ProfileUserFragment.this.notifyProfileDataObserver.notifyViewChange();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.profiles.ProfileUserFragment.2 */
    class C13452 implements Observer {
        C13452() {
        }

        public void update(Observable observable, Object data) {
            ProfileUserFragment.this.measureObservableHelper.onMeasure((View) data);
        }
    }

    /* renamed from: ru.ok.android.ui.users.fragments.profiles.ProfileUserFragment.3 */
    static /* synthetic */ class C13463 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$UserInfo$UserOnlineType;

        static {
            $SwitchMap$ru$ok$model$UserInfo$UserOnlineType = new int[UserOnlineType.values().length];
            try {
                $SwitchMap$ru$ok$model$UserInfo$UserOnlineType[UserOnlineType.OFFLINE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$model$UserInfo$UserOnlineType[UserOnlineType.MOBILE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$model$UserInfo$UserOnlineType[UserOnlineType.WEB.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private class DefaultInformationClickListener implements OnClickListener {
        private DefaultInformationClickListener() {
        }

        public void onClick(View v) {
            ProfileUserFragment.this.showUserAboutInfo();
            UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_NAMEZONE);
        }
    }

    private class DefaultPresentClickListener implements OnClickListener {
        private DefaultPresentClickListener() {
        }

        public void onClick(View v) {
            UserMergedPresent currentPresent = ProfileUserFragment.this.present.getCurrentVisiblePresent();
            if (!(currentPresent == null || TextUtils.isEmpty(currentPresent.presentId))) {
                ProfileUserFragment.this.showPresent(currentPresent.presentId);
            }
            UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_TOUCH_PRESENT);
        }
    }

    private class DefaultRelationClickListener implements OnClickListener {
        private DefaultRelationClickListener() {
        }

        public void onClick(View v) {
            if (v.getTag() != null && (v.getTag() instanceof FriendRelation)) {
                ProfileUserFragment.this.showRelationUser((FriendRelation) v.getTag());
            }
        }
    }

    protected abstract BaseUserProfileNavigationHandler getNavigationHandler();

    protected abstract String getUserId();

    protected abstract boolean isSendPresentVisible();

    protected abstract void onSendPresentClicked();

    protected int getLayoutId() {
        return 2130903559;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int newAlpha = 0;
        Context activity = getActivity();
        this.mainView = LocalizationManager.inflate(activity, getLayoutId(), null, false);
        this.informationLayout = this.mainView.findViewById(2131624912);
        this.present = (CarouselPresentsImageView) this.mainView.findViewById(2131624513);
        if (isShowOnlineView()) {
            ((ViewStub) this.mainView.findViewById(2131625268)).inflate();
            this.onlineLayout = this.mainView.findViewById(2131625155);
            this.onlineView = (ImageView) this.mainView.findViewById(2131625156);
            this.onlineText = (TextView) this.mainView.findViewById(2131625157);
        }
        this.name = (TextView) this.mainView.findViewById(C0263R.id.name);
        this.name.setSelected(true);
        this.additional = (TextView) this.mainView.findViewById(2131624922);
        this.relations = (TextView) this.mainView.findViewById(2131625267);
        this.inviteButton = (ProfilesButton) this.mainView.findViewById(2131624926);
        this.sendMessageButton = (ProfilesButton) this.mainView.findViewById(2131625259);
        this.sendPresentButton = (ProfilesButton) this.mainView.findViewById(2131625260);
        this.sendPresentButton.setOnClickListener(this);
        this.callButton = (ProfilesButton) this.mainView.findViewById(2131625261);
        this.subscribeButton = (ProfilesButton) this.mainView.findViewById(2131625258);
        this.privateView = this.mainView.findViewById(2131624920);
        this.premiumView = this.mainView.findViewById(2131624921);
        this.birthdayView = this.mainView.findViewById(2131625266);
        this.serviceInvisibleView = this.mainView.findViewById(2131625269);
        this.statusView = (StatusView) this.mainView.findViewById(2131625419);
        this.statusView.setOnOpenStatusListener(this);
        this.statusView.setShowMore(isMayDeleteStatus());
        this.sectionsAdapter = new ProfileSectionsAdapter(activity);
        this.sectionsAdapter.swapData(getNavigationHandler().getSectionItems());
        this.sectionsList = (AdapterView) this.mainView.findViewById(2131624914);
        if (this.sectionsList == null) {
            this.sectionsList = (AdapterView) this.mainView.findViewById(2131624918);
        }
        this.sectionsList.setAdapter(this.sectionsAdapter);
        this.sectionsList.setOnItemClickListener(getNavigationHandler());
        this.dividerView = this.mainView.findViewById(2131624602);
        this.present.setOnClickListener(new DefaultPresentClickListener());
        this.informationLayout.setOnClickListener(new DefaultInformationClickListener());
        this.relations.setOnClickListener(new DefaultRelationClickListener());
        initLoader();
        BusUsersHelper.getUserInfos(Arrays.asList(new String[]{getUserId()}), true);
        this.mainView.getViewTreeObserver().addOnGlobalLayoutListener(new C13441());
        if (this.mainView instanceof MeasureObservable) {
            ((MeasureObservable) this.mainView).addMeasureObserver(new C13452());
        }
        if (activity instanceof BaseCompatToolbarActivity) {
            if (!DeviceUtils.isSmall(activity)) {
                newAlpha = MotionEventCompat.ACTION_MASK;
            }
            BaseCompatToolbarActivity toolbarActivity = (BaseCompatToolbarActivity) activity;
            toolbarActivity.setToolbarTitleTextAlpha(newAlpha);
            toolbarActivity.setShadowAlpha(newAlpha);
        }
        return this.mainView;
    }

    protected boolean isIndexingFragment() {
        return true;
    }

    protected Action createIndexingAction() {
        if (this.profileInfo == null || this.profileInfo.userInfo == null) {
            return super.createIndexingAction();
        }
        return Action.newAction("http://schema.org/ViewAction", this.profileInfo.userInfo.getConcatName(), Uri.parse("http://ok.ru/profile/" + getUserId()), Uri.parse("android-app://ru.ok.android/odnoklassniki/ok.ru/profile/" + getUserId()));
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.notifyProfileDataObserver.profileDataChange();
    }

    public void onResume() {
        super.onResume();
        notifyUserName();
    }

    protected int getFriendsPrefixResId() {
        return 2131166242;
    }

    public void showMainPhoto() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            if (this.profileInfo == null || this.profileInfo.userInfo == null || TextUtils.isEmpty(this.profileInfo.userInfo.pid)) {
                Logger.m172d("no main photo");
            } else {
                NavigationHelper.showPhoto(activity, new PhotoOwner(getUserId(), 0), null, this.profileInfo.userInfo.pid, 7);
            }
        }
    }

    protected void logMainPhotoClick() {
        UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.ACTION_AVATAR_CLICK);
    }

    protected int getDefaultImageResId() {
        if (this.profileInfo == null) {
            return 0;
        }
        UserInfo user = this.profileInfo.userInfo;
        if (user != null) {
            return user.genderType == UserGenderType.MALE ? 2130838602 : 2130838600;
        } else {
            return 0;
        }
    }

    protected boolean isMayDeleteStatus() {
        return false;
    }

    protected void setVisibilityProfileMenu(int value) {
        this.sectionsList.setVisibility(value);
        if (this.dividerView != null) {
            this.dividerView.setVisibility(value == 0 ? 0 : 4);
        }
    }

    protected void initLoader() {
        Logger.m172d("init loader");
        getLoaderManager().initLoader(10, null, this);
    }

    protected GeneralDataLoader getLoader() {
        if (getActivity() != null) {
            return (GeneralDataLoader) getLoaderManager().getLoader(10);
        }
        return null;
    }

    public void addViewChangeObserver(Observer observer) {
        this.notifyProfileDataObserver.addObserver(observer);
    }

    public void removeViewChangeObserver(Observer observer) {
        this.notifyProfileDataObserver.deleteObserver(observer);
    }

    public void addMeasureViewChangeObserver(Observer observer) {
        this.measureObservableHelper.addObserver(observer);
    }

    public void removeMeasureViewChangeObserver(Observer observer) {
        this.measureObservableHelper.deleteObserver(observer);
    }

    public void setLoadCallBack(ProfileLoadCallBack loadCallBack) {
        this.loadCallBack = loadCallBack;
    }

    public Loader<UserProfileInfo> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                Logger.m182v("User Loader Create: " + getUserId());
                return new UserProfileInfoLoader(getActivity(), getUserId());
            default:
                return null;
        }
    }

    public void updateProfile() {
        BusUsersHelper.refreshUserInfos(Arrays.asList(new String[]{getUserId()}), true);
    }

    @Subscribe(on = 2131623946, to = 2131624252)
    public void onStreamMediaStatus(BusEvent event) {
        if (this.profileInfo != null && this.profileInfo.userInfo != null && this.profileInfo.userInfo.status != null) {
            UserStatus userStatus = this.statusView == null ? null : this.statusView.getStatus();
            MusicInfoContainer musicInfoContainer = (MusicInfoContainer) event.bundleOutput.getParcelable(BusProtocol.PREF_MEDIA_PLAYER_STATE_MUSIC_INFO_CONTAINER);
            if (musicInfoContainer != null && musicInfoContainer.track != null && userStatus != null && musicInfoContainer.track.id == userStatus.trackId) {
                InformationState playState = (InformationState) event.bundleOutput.getSerializable(BusProtocol.PREF_MEDIA_PLAYER_STATE);
                if (playState == InformationState.PAUSE) {
                    this.statusView.setPause();
                } else if (playState == InformationState.PLAY) {
                    this.statusView.setPlay();
                } else if (playState == InformationState.ERROR && getActivity() != null) {
                    this.statusView.setPause();
                    String errorMessage = event.bundleOutput.getString(BusProtocol.PREF_MEDIA_PLAYER_ERROR_MESSAGE);
                    if (errorMessage == null) {
                        if (errorMessage != null) {
                            Toast.makeText(getActivity(), errorMessage, 0).show();
                        }
                    } else if (errorMessage != null) {
                        Toast.makeText(getActivity(), errorMessage, 0).show();
                    }
                }
            }
        }
    }

    @Subscribe(on = 2131623946, to = 2131624221)
    public void onUserInfo(BusEvent e) {
        List<String> userIds = e.bundleInput.getStringArrayList("USER_IDS");
        if (userIds != null && userIds.contains(getUserId())) {
            boolean isRefresh = e.bundleInput.getBoolean("USER_REFRESH");
            if (e.resultCode != -1) {
                ErrorType errorType = getErrorType(e);
                onProfileInfoLoadError(ProfileType.USER, errorType);
                if (isRefresh) {
                    onErrorResult(errorType);
                }
            } else if (isRefresh) {
                ArrayList<UserInfo> result = e.bundleOutput.getParcelableArrayList("USERS");
                boolean isBlocked = e.bundleOutput.getBoolean("USER_BLOCKED");
                boolean isFriend = e.bundleOutput.getBoolean("USER_FRIEND");
                boolean isPrivate = false;
                if (result != null && result.size() > 0) {
                    isPrivate = ((UserInfo) result.get(0)).isPrivateProfile();
                }
                onProfileInfoRefreshFinish(new ProfileAccessInfo(isBlocked, isPrivate, isFriend));
            } else if (!isProfileInfoAvailable()) {
                GeneralDataLoader loader = getLoader();
                if (loader != null) {
                    loader.forceLoad();
                }
            }
        }
    }

    public boolean isProfileInfoAvailable() {
        return (this.profileInfo == null || this.profileInfo.userInfo == null) ? false : true;
    }

    public void onLoadFinished(Loader<UserProfileInfo> loader, UserProfileInfo profileInfo) {
        if (profileInfo == null) {
            onProfileInfoLoadError(ProfileType.USER, ErrorType.GENERAL);
        } else if (profileInfo.userInfo != null) {
            onProfileInfoLoad(profileInfo);
        } else if (this.pendingError != null) {
            onProfileInfoLoadError(ProfileType.USER, this.pendingError);
        }
    }

    protected void onProfileInfoRefreshFinish(ProfileAccessInfo info) {
        if (this.loadCallBack != null) {
            this.loadCallBack.onProfileRefresh(ProfileType.USER, info);
        }
    }

    public void onLoaderReset(Loader<UserProfileInfo> loader) {
        Logger.m183v("User Loader Reset: %s", getUserId());
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.invalidateOptionsMenu();
    }

    public void setProfileInfo(UserProfileInfo profileInfo) {
        this.profileInfo = profileInfo;
    }

    protected boolean isShowOnlineView() {
        return true;
    }

    protected void notifyUser() {
        Activity activity = getActivity();
        if (activity != null && this.profileInfo != null && this.profileInfo.userInfo != null) {
            notifyUserName();
            this.name.setText(this.profileInfo.userInfo.getAnyName());
            if (this.onlineLayout != null) {
                setOnlineType(Utils.onlineStatus(this.profileInfo.userInfo), this.profileInfo.userInfo.lastOnline);
            }
            StringBuilder sb = new StringBuilder();
            ProfileUtils.appendAgeString(activity, sb, this.profileInfo.userInfo.age);
            ProfileUtils.appendLocation(sb, this.profileInfo.userInfo.location);
            Utils.setTextViewTextWithVisibility(this.additional, sb);
            updatePrivateStatus(this.profileInfo.userInfo);
            updatePremiumStatus(this.profileInfo.userInfo.isPremiumProfile());
            updateBirthday(DateUtils.isBirthdayDate(this.profileInfo.userInfo.birthday));
            if (isShowStatus()) {
                this.statusView.setVisibility(0);
                this.statusView.setStatus(this.profileInfo.userInfo.status);
            } else {
                this.statusView.setVisibility(8);
            }
            notifyUserImage();
        }
    }

    private void notifyUserName() {
        Activity activity = getActivity();
        String userName = getUserName();
        if (activity != null && userName != null && (activity instanceof BaseCompatToolbarActivity)) {
            ((BaseCompatToolbarActivity) activity).setToolbarTitle(userName);
        }
    }

    @Nullable
    public String getUserName() {
        if (this.profileInfo == null || this.profileInfo.userInfo == null) {
            return null;
        }
        return this.profileInfo.userInfo.getAnyName();
    }

    protected boolean isShowStatus() {
        return this.profileInfo.userInfo.status != null && ((!this.profileInfo.isPrivateProfile() || this.profileInfo.isFriend()) && (this.profileInfo.relationInfo == null || !this.profileInfo.relationInfo.isBlocks));
    }

    protected void notifyUserImage() {
        if (getActivity() != null && this.profileInfo != null && this.profileInfo.userInfo != null) {
            processAvatarUrl(this.profileInfo.userInfo.bigPicUrl, this.profileInfo.userInfo.picUrl);
        }
    }

    protected void updatePrivateStatus(UserInfo info) {
        this.privateView.setVisibility(info.isShowLock() ? 0 : 8);
    }

    protected void updatePremiumStatus(boolean isPremium) {
        this.premiumView.setVisibility(isPremium ? 0 : 8);
    }

    protected void updateBirthday(boolean isBirthday) {
        this.birthdayView.setVisibility(isBirthday ? 0 : 8);
    }

    public void notifyProfileInfo() {
        if (getActivity() != null && this.profileInfo != null) {
            if (isShowPresents(this.profileInfo.presents)) {
                this.present.setPresents(this.profileInfo.presents);
                this.present.setVisibility(0);
            } else {
                this.present.setVisibility(8);
            }
            FriendRelation relationLove = getLoveRelation(this.profileInfo);
            Utils.setTextViewTextWithVisibility(this.relations, buildRelationsString(relationLove));
            this.relations.setTag(relationLove);
            this.sectionsAdapter.setCounters(this.profileInfo.counters);
            updateCanCall();
            updateRelationInfo();
            updateCanSendMessages();
            updateCanFriendInvite();
            updateSendPresent();
            updateIsSubscribeToStream();
            updateMutualFriends();
            this.notifyProfileDataObserver.profileDataChange();
        }
    }

    private void updateSendPresent() {
        this.sendPresentButton.setVisibility(isSendPresentVisible() ? 0 : 8);
    }

    protected boolean isShowPresents(List<UserMergedPresent> presents) {
        return (!this.profileInfo.isPrivateProfile() || this.profileInfo.isFriend()) && presents != null && !presents.isEmpty() && (this.profileInfo.relationInfo == null || !this.profileInfo.relationInfo.isBlocks);
    }

    protected void updateRelationInfo() {
        Logger.m172d("default update relations");
    }

    protected void updateCanSendMessages() {
        Logger.m172d("default update can send message");
    }

    protected void updateIsSubscribeToStream() {
        Logger.m172d("default update isSubscribe");
    }

    protected void updateCanCall() {
        Logger.m172d("default update can video call to user");
    }

    protected void updateCanFriendInvite() {
        Logger.m172d("default update can friend invite");
        executePendingFriendshipAction();
    }

    protected void updateMutualFriends() {
    }

    private CharSequence buildRelationsString(FriendRelation relation) {
        if (relation == null) {
            return null;
        }
        CharSequence sb = new SpannableStringBuilder();
        sb.append(relation.message);
        sb.setSpan(new StyleSpan(1), 0, sb.length(), 33);
        return sb;
    }

    private FriendRelation getLoveRelation(UserProfileInfo profileInfo) {
        List<FriendRelation> list = (List) profileInfo.relations.get(FriendRelativeType.LOVE);
        if (list == null) {
            list = (List) profileInfo.relations.get(FriendRelativeType.SPOUSE);
        }
        if (list == null) {
            list = (List) profileInfo.relations.get(FriendRelativeType.DIVORCED);
        }
        if (list == null) {
            list = (List) profileInfo.relations.get(FriendRelativeType.OPEN);
        }
        if (list == null) {
            return null;
        }
        Iterator i$ = list.iterator();
        if (i$.hasNext()) {
            return (FriendRelation) i$.next();
        }
        return null;
    }

    public void setOnlineType(UserOnlineType onlineType, long lastOnline) {
        int newVisibility = 0;
        if (onlineType == null) {
            onlineType = UserOnlineType.OFFLINE;
        }
        int textVisibility = 0;
        Drawable currentDrawable;
        switch (C13463.$SwitchMap$ru$ok$model$UserInfo$UserOnlineType[onlineType.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                newVisibility = 8;
                if (lastOnline <= 0) {
                    textVisibility = 8;
                    break;
                } else {
                    this.onlineText.setText(LocalizationManager.getString(getContext(), 2131166793) + " " + DateFormatter.getFormatStringFromDate(getContext(), lastOnline));
                    break;
                }
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                currentDrawable = getResources().getDrawable(2130838218);
                this.onlineText.setText(LocalizationManager.getString(getContext(), 2131166798));
                if (currentDrawable != null) {
                    this.onlineView.setImageDrawable(currentDrawable);
                    this.onlineView.setBackgroundDrawable(null);
                    break;
                }
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                int currentColor = getActivity().getResources().getColor(2131493207);
                Drawable sd2 = new ShapeDrawable(new OvalShape());
                sd2.getPaint().setColor(currentColor);
                sd2.getPaint().setStyle(Style.FILL_AND_STROKE);
                currentDrawable = sd2;
                this.onlineText.setText(LocalizationManager.getString(getContext(), 2131166798));
                this.onlineView.setBackgroundDrawable(currentDrawable);
                this.onlineView.setImageDrawable(null);
                break;
        }
        this.onlineText.setVisibility(textVisibility);
        this.onlineView.setVisibility(newVisibility);
    }

    public void showPresent(String presentId) {
        if (TextUtils.isEmpty(getUserId()) || getActivity() == null) {
            Logger.m172d("no show present");
        } else {
            NavigationHelper.showExternalUrlPage(getActivity(), WebUrlCreator.getMakePresentPageUrl(presentId), false);
        }
    }

    public void showUserAboutInfo() {
        if (TextUtils.isEmpty(getUserId()) || getActivity() == null) {
            Logger.m172d("no show about info");
            return;
        }
        ActivityExecutor activityExecutor = new ActivityExecutor(getActivity(), ExternalUrlWebFragment.class);
        activityExecutor.setArguments(ExternalUrlWebFragment.newArguments(WebUrlCreator.getUrl("profile/<user_id>/about", getUserId(), null)));
        activityExecutor.setAddToBackStack(false);
        activityExecutor.setNeedToolbar(true);
        activityExecutor.setSlidingMenuEnable(true);
        activityExecutor.setHideHomeButton(true);
        activityExecutor.execute();
    }

    public void showRelationUser(FriendRelation relation) {
        if (relation != null && relation != null && !TextUtils.isEmpty(relation.userId) && getActivity() != null) {
            NavigationHelper.showUserInfo(getActivity(), relation.userId);
        }
    }

    protected void createShortLink() {
        if (getActivity() != null) {
            ShortLink.createUserProfileLink(getUserId()).copy(getActivity(), true);
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

    protected void onProfileInfoLoad(UserProfileInfo profileInfo) {
        if (this.profileInfo == null) {
            setProfileInfo(profileInfo);
            restartAppIndexing();
        } else {
            setProfileInfo(profileInfo);
        }
        if (this.loadCallBack != null) {
            this.loadCallBack.onProfileInfoLoad(ProfileType.USER, createAccessInfo(profileInfo));
        }
        if (profileInfo.userInfo.isAllDataAvailable) {
            notifyUser();
            notifyProfileInfo();
        } else {
            notifyUserName();
            notifyUserImage();
        }
        Logger.m172d("User Loader loaded");
    }

    private ProfileAccessInfo createAccessInfo(UserProfileInfo profileInfo) {
        boolean z = false;
        boolean z2 = profileInfo.relationInfo != null && profileInfo.relationInfo.isBlocks;
        boolean isPrivateProfile = profileInfo.userInfo.isPrivateProfile();
        if (OdnoklassnikiApplication.getCurrentUser().uid.equals(getUserId()) || (profileInfo.relationInfo != null && profileInfo.relationInfo.isFriend)) {
            z = true;
        }
        return new ProfileAccessInfo(z2, isPrivateProfile, z);
    }

    public void onOpenStatus(UserStatus status) {
        NavigationHelper.showDiscussionCommentsFragment(getActivity(), new Discussion(status.id, Type.USER_STATUS.name()), Page.INFO, "");
    }

    public void onDeleteStatus(UserStatus status) {
        BusUsersHelper.deleteUserStatus();
    }

    @Subscribe(on = 2131623946, to = 2131624227)
    public void onUserTopicLoad(BusEvent event) {
        if (isVisible()) {
            String userId = event.bundleInput == null ? null : event.bundleInput.getString("user_id");
            if (!TextUtils.isEmpty(userId) && userId.equals(getUserId())) {
                updateProfile();
            }
        }
    }

    public void onClick(View v) {
        if (v != this.sendPresentButton) {
            super.onClick(v);
        } else if (getActivity() != null) {
            Logger.m173d("sendPresentButton clicked for userId: %s", getUserId());
            onSendPresentClicked();
        }
    }

    protected void executePendingFriendshipAction() {
        if (!"action_friendship".equals(this.pendingAction)) {
            return;
        }
        if (this.profileInfo == null) {
            Logger.m184w("null profile info");
            return;
        }
        Context context = getContext();
        if (context == null) {
            Logger.m184w("null context");
            return;
        }
        if (this.profileInfo.isSentFriendInvitation()) {
            Logger.m172d("friendship request already sent");
            showTimedToastIfVisible(LocalizationManager.getString(context, 2131165889), 1);
        } else if (this.profileInfo.canFriendInvite() && !this.profileInfo.isFriend()) {
            Logger.m173d("Sending friendship request to userId=%s", getUserId());
            BusUsersHelper.inviteFriend(userId);
        }
        this.pendingAction = null;
    }
}
