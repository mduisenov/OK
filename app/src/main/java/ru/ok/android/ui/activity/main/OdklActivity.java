package ru.ok.android.ui.activity.main;

import android.app.Activity;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.benchmark.BenchmarkUtils;
import ru.ok.android.billing.BillingHelper;
import ru.ok.android.billing.BillingHelper.DestroyHelper;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.ConversationsFriendsFragment;
import ru.ok.android.fragments.discussions.DiscussionsWebFragment;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.music.collections.MusicCollectionFragment;
import ru.ok.android.fragments.web.WebFragment;
import ru.ok.android.fragments.web.WebFragment.RootFragmentType;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;
import ru.ok.android.model.music.MusicInfoContainer;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.playservices.PlayServicesUtils;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.app.MusicService.InformationState;
import ru.ok.android.services.processors.update.AvailableUpdateDialogControl;
import ru.ok.android.ui.PopupDialogsSyncUtils;
import ru.ok.android.ui.activity.ShowFragmentActivity;
import ru.ok.android.ui.activity.main.ActivityExecutor.SoftInputType;
import ru.ok.android.ui.dialogs.rate.RateDialog;
import ru.ok.android.ui.fragments.MusicUsersFragment;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.ui.fragments.messages.MessagesFragment;
import ru.ok.android.ui.image.ImageUploadStatusActivity;
import ru.ok.android.ui.stream.StreamListFragment;
import ru.ok.android.ui.tabbar.OdklTabbar;
import ru.ok.android.ui.utils.HomeButtonUtils;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.FragmentLocation;
import ru.ok.android.utils.NavigationHelper.Source;
import ru.ok.android.utils.NavigationHelper.Tag;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.bus.BusProtocol;
import ru.ok.android.utils.controls.WhatNewControl;
import ru.ok.android.utils.controls.authorization.OnLoginListener;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.android.utils.settings.Settings;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;
import ru.ok.model.Discussion;
import ru.ok.model.UserInfo;

public final class OdklActivity extends ShowFragmentActivity implements OnLoginListener {
    private Type selectedType;

    /* renamed from: ru.ok.android.ui.activity.main.OdklActivity.1 */
    class C05751 implements Runnable {
        C05751() {
        }

        public void run() {
            Bundle bundle = new Bundle();
            bundle.putInt("impldract", 7);
            GlobalBus.send(2131624084, new BusEvent(bundle));
        }
    }

    /* renamed from: ru.ok.android.ui.activity.main.OdklActivity.2 */
    class C05762 implements OnCancelListener {
        C05762() {
        }

        public void onCancel(DialogInterface dialog) {
            Bundle bundle = new Bundle();
            bundle.putInt("impldract", 2);
            GlobalBus.send(2131624084, new BusEvent(bundle));
        }
    }

    /* renamed from: ru.ok.android.ui.activity.main.OdklActivity.3 */
    class C05773 implements OnClickListener {
        C05773() {
        }

        public void onClick(DialogInterface dialog, int arg1) {
            dialog.cancel();
        }
    }

    /* renamed from: ru.ok.android.ui.activity.main.OdklActivity.4 */
    class C05784 implements OnClickListener {
        C05784() {
        }

        public void onClick(DialogInterface dialog, int which) {
            OdklActivity.this.startActivity(new Intent(OdklActivity.this, ImageUploadStatusActivity.class));
            dialog.cancel();
        }
    }

    /* renamed from: ru.ok.android.ui.activity.main.OdklActivity.5 */
    static /* synthetic */ class C05795 {
        static final /* synthetic */ int[] f89x48c1fed9;
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$utils$NavigationHelper$Tag;

        static {
            f89x48c1fed9 = new int[RootFragmentType.values().length];
            try {
                f89x48c1fed9[RootFragmentType.DISCUSSIONS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f89x48c1fed9[RootFragmentType.FEED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f89x48c1fed9[RootFragmentType.MARKS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f89x48c1fed9[RootFragmentType.MESSAGES.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            $SwitchMap$ru$ok$android$utils$NavigationHelper$Tag = new int[Tag.values().length];
            try {
                $SwitchMap$ru$ok$android$utils$NavigationHelper$Tag[Tag.conversation.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$NavigationHelper$Tag[Tag.feed.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$NavigationHelper$Tag[Tag.discussion.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$NavigationHelper$Tag[Tag.music.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    public static String getStrTagByTag(Tag tag) {
        switch (C05795.$SwitchMap$ru$ok$android$utils$NavigationHelper$Tag[tag.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return "CONVERSATION_TAG";
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return "FEED_TAG";
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return "DISCUSSION_TAG";
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return "MUSIC_TAG";
            default:
                return "";
        }
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        if (getIntent() == null || getIntent().getData() != null || (getIntent().getFlags() & 4194304) == 0) {
            PopupDialogsSyncUtils.onOdklActivityCreate();
            getWindow().setSoftInputMode(32);
            super.onCreateLocalized(savedInstanceState);
            setContentView(2130903355);
            showTabbar(false);
            if (savedInstanceState == null && !startLoginIfNeeded()) {
                onIntent(getIntent(), false, Tag.feed);
                PlayServicesUtils.showRecoveryDialog(this, 0);
            }
            HomeButtonUtils.hideHomeButton(this);
            if (savedInstanceState == null && BillingHelper.getNotConsumeTransactionCount() != 0) {
                DestroyHelper destroyHelper = new DestroyHelper();
                destroyHelper.billingHelper = BillingHelper.create(this, destroyHelper);
            }
            updateOrientationConfig();
            return;
        }
        Logger.m184w("Intent has FLAG_ACTIVITY_BROUGHT_TO_FRONT flag");
        finish();
    }

    private void updateOrientationConfig() {
        View rightSmall = findViewById(2131625152);
        if (rightSmall != null) {
            rightSmall.setVisibility(getResources().getConfiguration().orientation == 1 ? 8 : 0);
        }
    }

    protected void onNewIntent(Intent intent) {
        if (Logger.isLoggingEnable()) {
            if (intent == null) {
                Logger.m172d("null intent");
            } else {
                Logger.m173d("action=%s", intent.getAction());
                Logger.m173d("data=%s", intent.getData());
                Logger.m173d("flags=0x%s", Integer.toHexString(intent.getFlags()));
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    for (String key : extras.keySet()) {
                        Logger.m173d("extra %s=%s", key, extras.get(key));
                    }
                }
            }
        }
        String s = intent.getStringExtra("extra_need_screen");
        Tag tagNew = TextUtils.isEmpty(s) ? Tag.feed : Tag.valueOf(s);
        s = getIntent().getStringExtra("extra_need_screen");
        Tag tagOld = TextUtils.isEmpty(s) ? Tag.feed : Tag.valueOf(s);
        String sourceStr = intent.getStringExtra("extra_navigation_source");
        Source source = TextUtils.isEmpty(sourceStr) ? null : Source.valueOf(sourceStr);
        if (!(tagNew != tagOld || tagNew == null || "android.intent.action.MAIN".equals(intent.getAction()))) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(getStrTagByTag(tagNew));
            if (fragment instanceof WebFragment) {
                WebFragment webFragment = (WebFragment) fragment;
                if (DeviceUtils.getType(getContext()) == DeviceLayoutType.SMALL && webFragment.isAdded() && webFragment.isVisible()) {
                    webFragment.reloadUrl();
                }
            } else if (source == Source.tab_bar && (fragment instanceof StreamListFragment) && fragment.isAdded() && fragment.isVisible()) {
                if (((Tag) intent.getSerializableExtra("current_tag")) == Tag.feed) {
                    Logger.m172d("Refresh stream");
                    ((StreamListFragment) fragment).refresh();
                }
            }
        }
        if (!(getIntent() == null || intent == null)) {
            if (!intent.getBooleanExtra("FORCE_PROCESS_INTENT", false) && TextUtils.equals(intent.getAction(), getIntent().getAction()) && Utils.equalBundles(intent.getExtras(), getIntent().getExtras()) && Utils.equalsUri(intent.getData(), getIntent().getData())) {
                return;
            }
        }
        super.onNewIntent(intent);
        onIntent(intent, false, null);
    }

    private boolean showIfNeedConversationAndReturnResultOperation(Intent intent, boolean onActivityResult) {
        if (!"ru.ok.android.ui.OdklActivity.SHOW_MESSAGES".equals(getIntent().getAction())) {
            return false;
        }
        String conversationId = intent.getStringExtra("CONVERSATION_ID");
        String userId = intent.getStringExtra("uid");
        hideAll(onActivityResult);
        showConversation(conversationId, userId, onActivityResult);
        return true;
    }

    private boolean showIfNeedDiscussionAndReturnResultOperation(Intent intent, boolean onActivityResult) {
        if (!"ru.ok.android.ui.OdklActivity.SHOW_DISCUSSIONS".equals(getIntent().getAction())) {
            return false;
        }
        hideAll(onActivityResult);
        showDiscussion(onActivityResult);
        NavigationHelper.showDiscussionCommentsFragment(this, (Discussion) intent.getParcelableExtra("extra_discussion"), Page.byIndex(intent.getIntExtra("extra_discussion_page", 0)), "");
        return true;
    }

    private boolean handleIfShowMyVideosAction(Intent intent, boolean onActivityResult) {
        if (!"ru.ok.android.ui.OdklActivity.SHOW_MY_VIDEOS".equals(getIntent().getAction())) {
            return false;
        }
        hideAll(onActivityResult);
        showFeedPage(onActivityResult, true);
        NavigationHelper.showExternalUrlPage((Activity) this, ShortLinkUtils.getUrlByPath("video/myVideo"), false, Type.videos);
        AppLaunchLog.localVideoUploadComplete();
        return true;
    }

    private boolean handleIfShowMyNotesAction(boolean onActivityResult) {
        if (!"ru.ok.android.ui.OdklActivity.SHOW_MY_NOTES".equals(getIntent().getAction())) {
            return false;
        }
        hideAll(onActivityResult);
        NavigationHelper.showUserTopics(this, OdnoklassnikiApplication.getCurrentUser().getId());
        return true;
    }

    public boolean onBackPressedChild() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            return false;
        }
        Fragment fragmentFeed = getSupportFragmentManager().findFragmentByTag("FEED_TAG");
        if (fragmentFeed != null && fragmentFeed.isVisible()) {
            return false;
        }
        NavigationHelper.showFeedPage(this, Source.back);
        return true;
    }

    private boolean showIfNeedPlayerAndReturnResultOperation(Intent intent, boolean onActivityResult) {
        Bundle bundle = intent.getExtras();
        if (bundle == null || !bundle.getBoolean("FROM_PLAYER", false)) {
            return false;
        }
        hideAll(onActivityResult);
        showMusicPage(onActivityResult, null, (MusicFragmentMode) intent.getParcelableExtra("music-fragment-mode"), true);
        AppLaunchLog.localMusicPlayer();
        NavigationHelper.showMusicPlayer(this);
        return true;
    }

    private void updateTabbarState(Class aClass) {
        OdklTabbar tabbar = getTabbarView();
        if (tabbar != null) {
            if (DiscussionsWebFragment.class == aClass) {
                tabbar.onShowDiscussionPage();
            } else if (MusicUsersFragment.class == aClass) {
                tabbar.onShowMusicPage();
            } else if (ConversationsFriendsFragment.class == aClass || MessagesFragment.class == aClass) {
                tabbar.onShowConversations();
            } else if (StreamListFragment.class == aClass) {
                tabbar.onShowFeedPage();
            }
        }
    }

    private void updateSelectedType(Class aClass) {
        if (DiscussionsWebFragment.class == aClass) {
            this.selectedType = Type.discussion;
        } else if (MusicUsersFragment.class == aClass) {
            this.selectedType = Type.music;
        } else if (ConversationsFriendsFragment.class == aClass || MessagesFragment.class == aClass) {
            this.selectedType = Type.conversation;
        } else if (StreamListFragment.class == aClass) {
            this.selectedType = Type.stream;
        }
    }

    private void onIntent(Intent intent, boolean onActivityResult, Tag defTag) {
        String str = "action=%s onActivityResult=%s defTag=%s";
        Object[] objArr = new Object[3];
        objArr[0] = intent == null ? "null" : intent.getAction();
        objArr[1] = Boolean.valueOf(onActivityResult);
        objArr[2] = defTag;
        Logger.m173d(str, objArr);
        if (!showIfNeedConversationAndReturnResultOperation(intent, onActivityResult) && !showIfNeedDiscussionAndReturnResultOperation(intent, onActivityResult) && !showIfNeedPlayerAndReturnResultOperation(intent, onActivityResult) && !handleIfShowMyNotesAction(onActivityResult) && !handleIfShowMyVideosAction(intent, onActivityResult)) {
            String s = intent.getStringExtra("extra_need_screen");
            Tag tag = TextUtils.isEmpty(s) ? defTag : Tag.valueOf(s);
            if (tag != null) {
                hideAll(onActivityResult);
                switchFragmentByTag(intent, onActivityResult, tag);
                updateSlidingMenuSelection();
            }
        }
    }

    private void switchFragmentByTag(Intent intent, boolean onActivityResult, Tag tag) {
        switch (C05795.$SwitchMap$ru$ok$android$utils$NavigationHelper$Tag[tag.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (intent.getBooleanExtra("source-shortcut", false)) {
                    AppLaunchLog.launchMessagingShortcut();
                    intent.removeExtra("source-shortcut");
                }
                showConversation(null, null, false);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                showFeedPage(onActivityResult, true);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                showDiscussion(onActivityResult);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                showMusicPage(onActivityResult, null, (MusicFragmentMode) intent.getParcelableExtra("music-fragment-mode"), true);
            default:
        }
    }

    @Nullable
    public Fragment showFragment(ActivityExecutor activityExecutor) {
        int mode = 16;
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                Fragment fragment2;
                if (!(fragment2 == null || fragment2.isHidden() || !fragment2.isAdded())) {
                    SoftInputType softInputType = ActivityExecutor.getSoftInputTypeFromFragment(fragment2);
                    if (softInputType != null) {
                        mode = softInputType == SoftInputType.RESIZE ? 16 : 32;
                    }
                }
                if (mode == 32) {
                    break;
                }
            }
        }
        if (mode == 16) {
            if (activityExecutor.getSoftInputType() == SoftInputType.RESIZE) {
                getWindow().setSoftInputMode(16);
            } else {
                getWindow().setSoftInputMode(32);
            }
        }
        Class<? extends Fragment> fragmentClass = activityExecutor.getFragmentClass();
        updateTabbarState(fragmentClass);
        FragmentLocation location = activityExecutor.getFragmentLocation();
        if (location == FragmentLocation.center || location == FragmentLocation.left) {
            updateSelectedType(fragmentClass);
        }
        if (MessagesFragment.class.isAssignableFrom(fragmentClass)) {
            fragment2 = getSupportFragmentManager().findFragmentByTag("CONVERSATION_TAG");
            Bundle arguments = activityExecutor.getArguments();
            if (fragment2 == null || fragment2.isHidden()) {
                startActivity(NavigationHelper.createIntentForShowMessagesForUser(this, arguments.getString("USER_ID")));
                return fragment2;
            } else if (fragment2 != null) {
                ((ConversationsFriendsFragment) fragment2).setSelectedUser(arguments.getString("USER_ID"), arguments.getString("CONVERSATION_ID"));
            }
        } else {
            if (MusicCollectionFragment.class.isAssignableFrom(fragmentClass)) {
                fragment2 = getSupportFragmentManager().findFragmentByTag("MUSIC_TAG");
                if (DeviceUtils.getType(this) == DeviceLayoutType.LARGE && (fragment2 == null || fragment2.isHidden())) {
                    hideAll(false);
                    Intent intent = new Intent(getIntent());
                    intent.putExtra("extra_need_screen", Tag.music);
                    new Instrumentation().callActivityOnNewIntent(this, intent);
                    showMusicPage(false, null, null, false);
                    activityExecutor.setAddToBackStack(false);
                }
            } else {
                if (StreamListFragment.class.isAssignableFrom(fragmentClass)) {
                    Logger.m173d("We want to show stream fragment... Can show friends? %s", Boolean.valueOf(canShowFragmentOnLocation(FragmentLocation.right_small)));
                    if (canShowFragmentOnLocation(FragmentLocation.right_small)) {
                        NavigationHelper.showStreamFriends(this);
                    }
                }
            }
        }
        return super.showFragment(activityExecutor);
    }

    private void showFeedPage(boolean onActivityResult, boolean addAdvertisingInfo) {
        Logger.m173d("onActivityResult=%s addAdvertisingInfo=%s", Boolean.valueOf(onActivityResult), Boolean.valueOf(addAdvertisingInfo));
        showFragment(new ActivityExecutor(this, StreamListFragment.class).setFragmentLocation(FragmentLocation.center).setActivityResult(onActivityResult).setTag("FEED_TAG").setSoftInputType(SoftInputType.PAN).setAddToBackStack(false));
    }

    private void onStreamMediaStatusLast(BusEvent event) {
        if (event == null) {
            Logger.m184w("Null music service status event.");
            return;
        }
        InformationState playState = (InformationState) event.bundleOutput.getSerializable(BusProtocol.PREF_MEDIA_PLAYER_STATE);
        if (((MusicInfoContainer) event.bundleOutput.getParcelable(BusProtocol.PREF_MEDIA_PLAYER_STATE_MUSIC_INFO_CONTAINER)) != null) {
            boolean urlEnable = (playState == InformationState.STOP || playState == InformationState.ERROR || playState == InformationState.DATA_QUERY) ? false : true;
            if (urlEnable) {
                NavigationHelper.showMusicPlayer(this);
            }
        }
    }

    private void showMusicPage(boolean onActivityResult, UserInfo userInfo, MusicFragmentMode mode, boolean needShowStab) {
        boolean doHighlightSelection;
        String str = null;
        if (DeviceUtils.getType(this) != DeviceLayoutType.SMALL) {
            onStreamMediaStatusLast(MusicService.getLastState());
        }
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("MUSIC_TAG");
        if (fragment != null) {
            if (userInfo == null) {
                ((MusicUsersFragment) fragment).clearSelectPosition();
            } else {
                ((MusicUsersFragment) fragment).setSelectionUser(userInfo.uid);
            }
        }
        if (mode == null) {
            mode = MusicFragmentMode.STANDARD;
        }
        if (DeviceUtils.getType(this) == DeviceLayoutType.LARGE) {
            doHighlightSelection = true;
        } else {
            doHighlightSelection = false;
        }
        showFragment(new ActivityExecutor(this, MusicUsersFragment.class).setArguments(MusicUsersFragment.newArguments(doHighlightSelection, userInfo == null ? null : userInfo.uid, mode)).setFragmentLocation(FragmentLocation.left).setActivityResult(onActivityResult).setTag("MUSIC_TAG").setSoftInputType(SoftInputType.PAN).setAddToBackStack(false));
        if (needShowStab && DeviceUtils.getType(this) == DeviceLayoutType.LARGE && canShowFragmentOnLocation(FragmentLocation.right)) {
            if (userInfo != null) {
                str = userInfo.uid;
            }
            NavigationHelper.showUserMusicPage(this, str, mode);
        }
    }

    public void showConversation(String conversationId, String userId, boolean onActivityResult) {
        boolean isDeviceTypeLarge;
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("CONVERSATION_TAG");
        if (fragment != null) {
            ((ConversationsFriendsFragment) fragment).setSelectedUser(userId, conversationId);
        }
        if (DeviceUtils.getType(this) == DeviceLayoutType.LARGE) {
            isDeviceTypeLarge = true;
        } else {
            isDeviceTypeLarge = false;
        }
        showFragment(new ActivityExecutor(this, ConversationsFriendsFragment.class).setArguments(ConversationsFriendsFragment.newArguments(conversationId, userId, true, true, isDeviceTypeLarge)).setFragmentLocation(FragmentLocation.left).setActivityResult(onActivityResult).setAddToBackStack(false).setTag("CONVERSATION_TAG"));
        if (isDeviceTypeLarge || !TextUtils.isEmpty(conversationId) || !TextUtils.isEmpty(userId)) {
            if (TextUtils.isEmpty(conversationId)) {
                NavigationHelper.showMessagesForUser(this, userId);
            } else {
                NavigationHelper.showMessagesForConversation(this, conversationId, userId);
            }
        }
    }

    public void showDiscussion(boolean onActivityResult) {
        showFragment(new ActivityExecutor(this, DiscussionsWebFragment.class).setFragmentLocation(FragmentLocation.center).setActivityResult(onActivityResult).setTag("DISCUSSION_TAG").setAddToBackStack(false));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625472:
                startLogReport();
                return true;
            case 2131625473:
                BenchmarkUtils.logAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startLogReport() {
        try {
            startActivity(new Intent(this, Class.forName("ru.ok.android.logreport.LogReportActivity")));
        } catch (Throwable e) {
            Logger.m177e("Log report activity not found: %s", e);
            Logger.m178e(e);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624225)
    public void onImageUploaderEvent(BusEvent event) {
        if (event.resultCode == 4) {
            int upldStatus = event.bundleOutput.getInt("upldrsts");
            if (upldStatus == 6) {
                showDialog(3);
            } else if (upldStatus != 0) {
            }
        }
    }

    protected void onResume() {
        super.onResume();
        EventsManager.getInstance().updateIfMoreOneMinuteAfterLastUpdate();
        if (!(!Settings.hasLoginData(this) || WhatNewControl.testVersion(this) || AvailableUpdateDialogControl.showAvailableUpdateDialog(this))) {
            RateDialog.showDialogIfNeeded(this, getSupportFragmentManager());
        }
        ThreadUtil.queueOnMain(new C05751());
        if (getIntent().getBooleanExtra("key_need_check_login", false)) {
            startLoginIfNeeded();
        }
    }

    public void onLoginSuccessful(String url, String verificationUrl) {
        super.onLoginSuccessful(url, verificationUrl);
        Intent intent = getIntent();
        EventsManager.getInstance().updateNow();
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return new Builder(this).setTitle(2131166469).setMessage(2131166468).setPositiveButton(2131166732, new C05784()).setNegativeButton(2131166034, new C05773()).setOnCancelListener(new C05762()).create();
            default:
                return super.onCreateDialog(id);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getTabbarView() != null) {
            KeyBoardUtils.hideKeyBoard(this, getTabbarView().getWindowToken());
        }
        updateOrientationConfig();
    }

    protected Type getSlidingMenuSelectedItem() {
        return this.selectedType;
    }

    public boolean isUseTabbar() {
        return true;
    }
}
