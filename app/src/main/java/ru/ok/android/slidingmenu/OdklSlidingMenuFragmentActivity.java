package ru.ok.android.slidingmenu;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ru.mail.android.mytarget.nativeads.NativeAppwallAd;
import ru.mail.android.mytarget.nativeads.NativeAppwallAd.AppwallAdListener;
import ru.mail.android.mytarget.nativeads.banners.NativeAppwallBanner;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.web.WebFragment.RootFragmentType;
import ru.ok.android.fragments.web.hooks.WebLinksProcessor;
import ru.ok.android.model.music.MusicInfoContainer;
import ru.ok.android.onelog.NewUserLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.app.MusicService.InformationState;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.target.TargetUtils;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.nativeRegistration.HelpSettingsHandler;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.bus.BusProtocol;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;
import ru.ok.android.widget.MenuView;
import ru.ok.android.widget.MenuView.MenuItem;
import ru.ok.android.widget.MenuView.ViewHolder;
import ru.ok.android.widget.menuitems.AdmanMenuItem;
import ru.ok.android.widget.menuitems.AdmanMenuItem.OnBannerClickListener;
import ru.ok.android.widget.menuitems.BannerItem;
import ru.ok.android.widget.menuitems.GridItem;
import ru.ok.android.widget.menuitems.MenuDivider;
import ru.ok.android.widget.menuitems.MoreBannersItem;
import ru.ok.android.widget.menuitems.MoreBannersItem.OnMoreBannersClickListener;
import ru.ok.android.widget.menuitems.MusicItem;
import ru.ok.android.widget.menuitems.SlidingMenuHelper;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;
import ru.ok.android.widget.menuitems.StandardItem;
import ru.ok.android.widget.menuitems.StandardItem.BubbleState;
import ru.ok.android.widget.menuitems.UserItem;
import ru.ok.model.UserInfo;
import ru.ok.model.events.DiscussionOdklEvent;
import ru.ok.model.events.OdnkEvent;
import ru.ok.model.events.OdnkEvent.EventType;
import ru.ok.model.stream.banner.PromoLink;

public abstract class OdklSlidingMenuFragmentActivity extends BaseActivity implements OnClickListener, OnBannerClickListener, OnMoreBannersClickListener {
    private final List<MenuItem> admanBannerItems;
    private int heightItem;
    private InformationState lastPlayerState;
    private MenuAdapter menuAdapter;
    private StandardItem menuItemConversation;
    private StandardItem menuItemDiscussions;
    private StandardItem menuItemFriends;
    private GridItem menuItemGrid;
    private StandardItem menuItemGroups;
    private StandardItem menuItemHolidays;
    private MusicItem menuItemMusic;
    private StandardItem menuItemPhotos;
    private StandardItem menuItemStream;
    private UserItem menuItemUser;
    private MenuView menuView;
    private final List<MenuItem> sideLink2Items;
    private final List<MenuItem> sideLinkItems;
    private final LoaderCallbacks<ArrayList<PromoLink>> sideLinkLoaderCallback;
    private SlidingMenuStrategy slidingMenuStrategy;
    protected WebLinksProcessor slidingMenuWebLinksProcessor;
    private NativeAppwallAd targetAdapter;
    private AppwallAdListener updateListener;

    /* renamed from: ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity.1 */
    class C05211 implements LoaderCallbacks<ArrayList<PromoLink>> {
        C05211() {
        }

        public Loader onCreateLoader(int id, Bundle args) {
            if (id == 2131624303) {
                return new SideLinksLoader(OdklSlidingMenuFragmentActivity.this, new int[]{3, 4});
            }
            return null;
        }

        public void onLoadFinished(Loader<ArrayList<PromoLink>> loader, ArrayList<PromoLink> data) {
            OdklSlidingMenuFragmentActivity.this.onSideLinksLoaded(data);
        }

        public void onLoaderReset(Loader loader) {
            OdklSlidingMenuFragmentActivity.this.onSideLinksLoaded(null);
        }
    }

    /* renamed from: ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity.2 */
    class C05222 implements AppwallAdListener {
        C05222() {
        }

        public void onLoad(NativeAppwallAd nativeAppwallAd) {
            MenuAdapter adapter = OdklSlidingMenuFragmentActivity.this.getMenuAdapter();
            List<MenuItem> admanItems = OdklSlidingMenuFragmentActivity.this.admanBannerItems;
            String title = nativeAppwallAd.getTitle();
            if (adapter != null && admanItems != null && !OdklSlidingMenuFragmentActivity.this.isFinishing()) {
                admanItems.clear();
                int heightItem = (int) OdklSlidingMenuFragmentActivity.this.getContext().getResources().getDimension(2131230746);
                Logger.m172d("adman : sec name >>>:" + title);
                admanItems.add(new MenuDivider(heightItem, title));
                MoreBannersItem moreBannersItem = null;
                List<NativeAppwallBanner> banners = nativeAppwallAd.getBanners();
                if (banners.size() > 2) {
                    moreBannersItem = new MoreBannersItem(OdklSlidingMenuFragmentActivity.this, heightItem, title, banners.subList(1, banners.size()));
                    moreBannersItem.setListener(OdklSlidingMenuFragmentActivity.this);
                    banners = banners.subList(0, 2);
                }
                for (NativeAppwallBanner banner : banners) {
                    AdmanMenuItem item = new AdmanMenuItem(banner, title, heightItem, nativeAppwallAd);
                    item.setClickListener(OdklSlidingMenuFragmentActivity.this);
                    admanItems.add(item);
                }
                if (moreBannersItem != null) {
                    admanItems.add(moreBannersItem);
                }
                adapter.notifyDataSetChanged();
            }
        }

        public void onNoAd(String s, NativeAppwallAd nativeAppwallAd) {
        }

        public void onClick(NativeAppwallBanner nativeAppwallBanner, NativeAppwallAd nativeAppwallAd) {
        }

        public void onDismissDialog(NativeAppwallAd nativeAppwallAd) {
        }
    }

    /* renamed from: ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity.3 */
    static /* synthetic */ class C05233 {
        static final /* synthetic */ int[] f86x48c1fed9;
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$events$OdnkEvent$EventType;

        static {
            $SwitchMap$ru$ok$model$events$OdnkEvent$EventType = new int[EventType.values().length];
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.ACTIVITIES.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.EVENTS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.MARKS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.GUESTS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.DISCUSSIONS.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.MESSAGES.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.FRIENDS.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.FRIENDS_ONLINE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.UPLOAD_PHOTO.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.GROUPS.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.HOLIDAYS.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            f86x48c1fed9 = new int[RootFragmentType.values().length];
            try {
                f86x48c1fed9[RootFragmentType.DISCUSSIONS.ordinal()] = 1;
            } catch (NoSuchFieldError e12) {
            }
            try {
                f86x48c1fed9[RootFragmentType.MESSAGES.ordinal()] = 2;
            } catch (NoSuchFieldError e13) {
            }
        }
    }

    public class MenuAdapter extends BaseAdapter implements OnClickListener {
        @NonNull
        private SubListDataSet<MenuItem> activeData;
        private final Context context;
        @Nullable
        private final SubListDataSet<MenuItem> dataCollapsed;
        @NonNull
        private final SubListDataSet<MenuItem> dataExposed;
        private Type selectedItem;

        public MenuAdapter(Context context) {
            this.dataExposed = new SubListDataSet();
            this.context = context;
            if (OdklSlidingMenuFragmentActivity.this.slidingMenuStrategy.isCollapsible()) {
                this.dataCollapsed = new SubListDataSet();
                this.activeData = this.dataCollapsed;
                return;
            }
            this.dataCollapsed = null;
            this.activeData = this.dataExposed;
        }

        public void onClick(View v) {
            ViewHolder viewHolder = (ViewHolder) v.getTag();
            if (viewHolder != null && viewHolder.position >= 0 && viewHolder.position < getCount() && OdklSlidingMenuFragmentActivity.this.menuView != null) {
                getItem(viewHolder.position).onClick(OdklSlidingMenuFragmentActivity.this.menuView, getItem(viewHolder.position));
            }
        }

        public void addItem(boolean showWhenCollapsed, MenuItem item) {
            this.dataExposed.addItem(item);
            if (showWhenCollapsed && this.dataCollapsed != null) {
                this.dataCollapsed.addItem(item);
            }
        }

        public void addSubList(boolean showWhenCollapsed, List<MenuItem> subList) {
            this.dataExposed.addSubList(subList);
            if (showWhenCollapsed && this.dataCollapsed != null) {
                this.dataCollapsed.addSubList(subList);
            }
        }

        public void setCollapsed(boolean collapsed) {
            if (this.dataCollapsed != null) {
                SubListDataSet<MenuItem> data = collapsed ? this.dataCollapsed : this.dataExposed;
                if (data != this.activeData) {
                    this.activeData = data;
                    notifyDataSetChanged();
                }
            }
        }

        public int getCount() {
            return this.activeData.getCount();
        }

        public MenuItem getItem(int position) {
            return (MenuItem) this.activeData.getItem(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public int getItemViewType(int position) {
            return getItem(position).getType();
        }

        public int getViewTypeCount() {
            return 7;
        }

        public boolean isEnabled(int position) {
            return getItem(position).getType() != 4;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).postGetView(getItem(position).getView(LocalizationManager.from(this.context), convertView, position, this.selectedItem), this, this.selectedItem);
        }

        public void setSelectedItem(Type selectedItem) {
            this.selectedItem = selectedItem;
            notifyDataSetChanged();
        }
    }

    protected abstract Type getSlidingMenuSelectedItem();

    public OdklSlidingMenuFragmentActivity() {
        this.slidingMenuStrategy = SlidingMenuStrategy.getCurrentStrategy(this);
        this.lastPlayerState = InformationState.STOP;
        this.menuAdapter = null;
        this.menuView = null;
        this.admanBannerItems = new ArrayList();
        this.sideLinkItems = new ArrayList();
        this.sideLink2Items = new ArrayList();
        this.sideLinkLoaderCallback = new C05211();
        this.updateListener = new C05222();
    }

    public void scrollMenuToTop() {
        if (this.menuView != null) {
            this.menuView.setSelection(0);
        }
    }

    public boolean isMenuIndicatorEnable() {
        if (isNeedShowLeftMenu()) {
            return this.slidingMenuStrategy.isMenuIndicatorEnable();
        }
        return false;
    }

    public void setMenuIndicatorEnable(boolean enable) {
        if (isNeedShowLeftMenu()) {
            this.slidingMenuStrategy.setMenuIndicatorEnable(enable);
        }
    }

    public static void setMenuIndicatorEnable(Activity activity, boolean enable) {
        if (activity instanceof OdklSlidingMenuFragmentActivity) {
            ((OdklSlidingMenuFragmentActivity) activity).setMenuIndicatorEnable(enable);
        }
    }

    public SlidingMenuStrategy getSlidingMenuStrategy() {
        return this.slidingMenuStrategy;
    }

    public void setContentView(int layoutResID) {
        DrawerLayout drawer = getDrawerLayout();
        if (isNeedShowLeftMenu()) {
            this.slidingMenuStrategy.setContentView(layoutResID);
            if (drawer != null) {
                drawer.setDrawerLockMode(0);
                return;
            }
            return;
        }
        super.setContentView(layoutResID);
        if (drawer != null) {
            drawer.setDrawerLockMode(1);
        }
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (isNeedShowLeftMenu() && this.slidingMenuStrategy.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isNeedShowLeftMenu()) {
            this.slidingMenuStrategy.onConfigurationChanged(newConfig);
        }
    }

    public void setContentView(View view) {
        if (isNeedShowLeftMenu()) {
            this.slidingMenuStrategy.setContentView(view);
        } else {
            super.setContentView(view);
        }
    }

    protected void odklSuperSetContentView(int res) {
        super.setContentView(res);
    }

    protected void odklSuperSetContentView(View v, LayoutParams params) {
        super.setContentView(v, params);
    }

    public void setContentView(View view, LayoutParams params) {
        if (isNeedShowLeftMenu()) {
            this.slidingMenuStrategy.setContentView(view, params);
        } else {
            super.setContentView(view, params);
        }
    }

    public void closeMenu() {
        if (isNeedShowLeftMenu()) {
            this.slidingMenuStrategy.closeMenu();
        }
    }

    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if ((fragment instanceof BaseFragment) && fragment.isVisible() && ((BaseFragment) fragment).handleBack()) {
                    return;
                }
            }
        }
        if (!onBackPressedChild()) {
            super.onBackPressed();
        }
    }

    public boolean onBackPressedChild() {
        return false;
    }

    public void openMenu() {
        if (isNeedShowLeftMenu()) {
            this.slidingMenuStrategy.openMenu();
        }
    }

    public boolean isMenuOpen() {
        if (isNeedShowLeftMenu()) {
            return this.slidingMenuStrategy.isMenuOpen();
        }
        return false;
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isNeedShowLeftMenu()) {
            this.slidingMenuStrategy.onSaveInstanceState(outState);
        }
    }

    private void setPlayState(InformationState playing, CharSequence trackInfo) {
        boolean z = true;
        if (this.menuItemMusic != null && this.menuAdapter != null) {
            this.menuItemMusic.setIsMusicInit(true);
            MusicItem musicItem = this.menuItemMusic;
            if (playing != InformationState.PAUSE) {
                z = false;
            }
            musicItem.setIsPause(z);
            this.menuItemMusic.setTrackInfo(trackInfo);
            this.menuAdapter.notifyDataSetChanged();
        }
    }

    private void setNotPlayState() {
        if (this.menuItemMusic != null && this.menuAdapter != null) {
            this.menuItemMusic.setIsMusicInit(false);
            this.menuAdapter.notifyDataSetChanged();
        }
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        this.heightItem = (int) getContext().getResources().getDimension(2131230746);
        super.onCreateLocalized(savedInstanceState);
        if (isNeedShowLeftMenu()) {
            this.slidingMenuStrategy.onCreateLocalized(savedInstanceState);
            this.targetAdapter = TargetUtils.createTargetAdapter(this);
        }
    }

    protected void onStart() {
        super.onStart();
        if (this.targetAdapter != null) {
            this.targetAdapter.setListener(this.updateListener);
            this.targetAdapter.load();
        }
    }

    protected void onStop() {
        super.onStop();
        if (this.targetAdapter != null) {
            this.targetAdapter.setListener(null);
        }
    }

    protected void setMenuView(MenuView menuView) {
        this.menuView = menuView;
    }

    protected MenuView getMenuView() {
        return this.menuView;
    }

    public MenuAdapter getMenuAdapter() {
        if (this.menuAdapter == null) {
            this.menuAdapter = this.menuView == null ? null : (MenuAdapter) this.menuView.getAdapter();
        }
        return this.menuAdapter;
    }

    protected void setMenuAdapter(MenuAdapter menuAdapter) {
        this.menuAdapter = menuAdapter;
    }

    protected void notifyCreateAdapter() {
        if (this.targetAdapter != null) {
            this.targetAdapter.load();
        }
    }

    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (isNeedShowLeftMenu()) {
            this.slidingMenuStrategy.onPostCreate(savedInstanceState);
        }
        getSupportLoaderManager().initLoader(2131624303, null, this.sideLinkLoaderCallback);
    }

    protected void onResume() {
        super.onResume();
        if (isNeedShowLeftMenu()) {
            this.slidingMenuStrategy.onResume();
        }
    }

    public void onGoingToPause() {
        if (isNeedShowLeftMenu()) {
            this.slidingMenuStrategy.onPause();
        }
    }

    protected void onPause() {
        onGoingToPause();
        super.onPause();
    }

    public View findViewById(int id) {
        if (!isNeedShowLeftMenu()) {
            return super.findViewById(id);
        }
        View v = super.findViewById(id);
        return v == null ? this.slidingMenuStrategy.findViewById(id) : v;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (isNeedShowLeftMenu()) {
            return this.slidingMenuStrategy.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event);
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624252)
    public void onStreamMediaStatus(BusEvent event) {
        InformationState playState = (InformationState) event.bundleOutput.getSerializable(BusProtocol.PREF_MEDIA_PLAYER_STATE);
        MusicInfoContainer musicInfoContainer = (MusicInfoContainer) event.bundleOutput.getParcelable(BusProtocol.PREF_MEDIA_PLAYER_STATE_MUSIC_INFO_CONTAINER);
        if (musicInfoContainer != null) {
            this.lastPlayerState = playState;
            String artistName = null;
            String trackName = null;
            if (musicInfoContainer.track != null) {
                artistName = musicInfoContainer.track.artist != null ? musicInfoContainer.track.artist.name : null;
                trackName = musicInfoContainer.track.name;
            }
            boolean urlEnable = (this.lastPlayerState == InformationState.STOP || this.lastPlayerState == InformationState.ERROR || this.lastPlayerState == InformationState.DATA_QUERY) ? false : true;
            if (urlEnable) {
                boolean artistEmpty = TextUtils.isEmpty(artistName);
                boolean trackEmpty = TextUtils.isEmpty(trackName);
                StringBuilder sb = null;
                if (!(artistEmpty && trackEmpty)) {
                    sb = new StringBuilder();
                    if (!artistEmpty) {
                        sb.append(artistName);
                    }
                    if (!(artistEmpty || trackEmpty)) {
                        sb.append(" - ");
                    }
                    if (!trackEmpty) {
                        sb.append(trackName);
                    }
                }
                setPlayState(this.lastPlayerState, sb);
                return;
            }
            setNotPlayState();
            return;
        }
        setNotPlayState();
    }

    @Subscribe(on = 2131623946, to = 2131624216)
    public final void onGetCurrentUser(BusEvent busEvent) {
        if (busEvent.resultCode == -1) {
            UserInfo user = (UserInfo) busEvent.bundleOutput.getParcelable(BusProtocol.USER);
            if (!(user == null || this.menuItemUser == null)) {
                this.menuItemUser.setCurrentUser(user);
            }
            if (user != null) {
                StatisticManager.getInstance().setUserId(user.uid);
            }
        }
    }

    public void onGetNewEvents(ArrayList<OdnkEvent> returnList) {
        super.onGetNewEvents(returnList);
        boolean needUpdateGrid = false;
        int guestsCount = 0;
        int marksCount = 0;
        int eventsCount = 0;
        boolean needUpdateFriends = false;
        int friends = 0;
        int friendsOnline = 0;
        Iterator i$ = returnList.iterator();
        while (i$.hasNext()) {
            OdnkEvent event = (OdnkEvent) i$.next();
            switch (C05233.$SwitchMap$ru$ok$model$events$OdnkEvent$EventType[event.type.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    MenuView.setCounterToStandartItem(this.menuItemStream, event.getValueInt(), 0);
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    eventsCount = event.getValueInt();
                    needUpdateGrid = true;
                    break;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    marksCount = event.getValueInt();
                    needUpdateGrid = true;
                    break;
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    guestsCount = event.getValueInt();
                    needUpdateGrid = true;
                    break;
                case Message.UUID_FIELD_NUMBER /*5*/:
                    int discussionsLikeCount = ((DiscussionOdklEvent) event).getIntValueLike();
                    MenuView.setCounterToStandartItem(this.menuItemDiscussions, event.getValueInt(), 0, ((DiscussionOdklEvent) event).getIntValueReply() > 0, discussionsLikeCount > 0);
                    break;
                case Message.REPLYTO_FIELD_NUMBER /*6*/:
                    MenuView.setCounterToStandartItem(this.menuItemConversation, event.getValueInt(), 0);
                    break;
                case Message.ATTACHES_FIELD_NUMBER /*7*/:
                    friends = event.getValueInt();
                    needUpdateFriends = true;
                    break;
                case Message.TASKID_FIELD_NUMBER /*8*/:
                    friendsOnline = event.getValueInt();
                    needUpdateFriends = true;
                    break;
                case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                    MenuView.setCounterToStandartItem(this.menuItemPhotos, event.getValueInt(), 0);
                    break;
                case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                    MenuView.setCounterToStandartItem(this.menuItemGroups, event.getValueInt(), 0);
                    break;
                case Message.EDITINFO_FIELD_NUMBER /*11*/:
                    MenuView.setCounterToStandartItem(this.menuItemHolidays, event.getValueInt(), 0);
                    break;
                default:
                    break;
            }
        }
        boolean newUser = false;
        if (needUpdateFriends) {
            if (friends == 0 && friendsOnline == 0) {
                NewUserLog.logAppLaunch();
                newUser = true;
            }
            MenuView.setCounterToStandartItem(this.menuItemFriends, friendsOnline, friends);
        }
        if (needUpdateGrid && this.menuItemGrid != null) {
            this.menuItemGrid.setCounter(eventsCount, marksCount, guestsCount);
        }
        if (this.menuItemUser != null) {
            this.menuItemUser.setNotificationsCounter((marksCount + guestsCount) + eventsCount);
            this.menuItemUser.setNewUser(newUser);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case 2131625072:
                getContext().startService(MusicService.getPrevIntent(getContext()));
                StatisticManager.getInstance().addStatisticEvent("music-prev_touch", new Pair[0]);
            case 2131625074:
                getContext().startService(MusicService.getNextIntent(getContext()));
                StatisticManager.getInstance().addStatisticEvent("music-next_touch", new Pair[0]);
            case 2131625075:
                if (this.lastPlayerState != InformationState.PAUSE) {
                    getContext().startService(MusicService.getTogglePlayIntent(getContext(), true));
                    StatisticManager.getInstance().addStatisticEvent("music-pause_touch", new Pair[0]);
                    return;
                }
                getContext().startService(MusicService.getPlayIntent(getContext()));
                StatisticManager.getInstance().addStatisticEvent("music-play_touch", new Pair[0]);
            default:
        }
    }

    public void onLocalizationChanged() {
        super.onLocalizationChanged();
        if (this.menuAdapter != null) {
            this.menuAdapter.notifyDataSetChanged();
        }
        if (isNeedShowLeftMenu()) {
            TargetUtils.setLang(Settings.getCurrentLocale(this));
            if (this.targetAdapter != null) {
                this.targetAdapter.setListener(null);
            }
            this.targetAdapter = TargetUtils.createTargetAdapter(this);
            this.targetAdapter.setListener(this.updateListener);
            this.targetAdapter.load();
            GlobalBus.send(2131623987, new BusEvent());
        }
    }

    public MenuAdapter createMenuAdapterAndInitItems() {
        BubbleState bubbleStateGreen = DeviceUtils.getType(getContext()) != DeviceLayoutType.SMALL ? BubbleState.green_tablet : BubbleState.green_phone;
        MenuAdapter adapter = new MenuAdapter(this);
        this.menuItemUser = new UserItem(getContext().getResources().getDimensionPixelSize(2131231079), this);
        this.menuItemUser.setCurrentUser(Settings.getCurrentUser(this));
        this.menuItemGrid = new GridItem(2131166602, 2131166596, 0, 2131166595, 0, 2131166594, 0, this, getContext().getResources().getDimensionPixelSize(2131231174));
        this.menuItemStream = SlidingMenuHelper.createStandardItem(this, Type.stream, this.heightItem, BubbleState.green_phone);
        this.menuItemFriends = SlidingMenuHelper.createStandardItem(this, Type.friends, this.heightItem, BubbleState.gray);
        this.menuItemConversation = SlidingMenuHelper.createStandardItem(this, Type.conversation, this.heightItem, bubbleStateGreen);
        this.menuItemDiscussions = SlidingMenuHelper.createStandardItem(this, Type.discussion, this.heightItem, bubbleStateGreen);
        this.menuItemPhotos = SlidingMenuHelper.createStandardItem(this, Type.photos, this.heightItem, BubbleState.gray);
        MenuItem menuItemVideos = SlidingMenuHelper.createStandardItem(this, Type.videos, this.heightItem, BubbleState.gray);
        this.menuItemGroups = SlidingMenuHelper.createStandardItem(this, Type.groups, this.heightItem, BubbleState.gray);
        this.menuItemMusic = new MusicItem(this, this.heightItem);
        this.menuItemMusic.setOnClickListener(this);
        MenuItem menuItemShare = SlidingMenuHelper.createStandardItem(this, Type.share, this.heightItem, BubbleState.green_tablet);
        MenuItem menuItemGames = SlidingMenuHelper.createStandardItem(this, Type.games, this.heightItem, BubbleState.gray);
        MenuItem menuItemPresents = SlidingMenuHelper.createStandardItem(this, Type.make_present, this.heightItem, BubbleState.green_tablet);
        this.menuItemHolidays = SlidingMenuHelper.createStandardItem(this, Type.holidays, this.heightItem, BubbleState.gray);
        MenuItem menuItemProgress = SlidingMenuHelper.createStandardItem(this, Type.progress, this.heightItem, BubbleState.gray);
        MenuItem menuItemOnline = SlidingMenuHelper.createStandardItem(this, Type.online, this.heightItem, BubbleState.gray);
        MenuItem menuItemProfileSettings = SlidingMenuHelper.createStandardItem(this, Type.profile_settings, this.heightItem, BubbleState.gray);
        MenuItem menuItemRecharge = SlidingMenuHelper.createStandardItem(this, Type.recharge, this.heightItem, BubbleState.gray);
        MenuItem menuItemBookmarks = SlidingMenuHelper.createStandardItem(this, Type.bookmarks, this.heightItem, BubbleState.gray);
        MenuItem menuItemForum = SlidingMenuHelper.createStandardItem(this, Type.forum, this.heightItem, BubbleState.green_tablet);
        MenuItem menuItemBlackList = SlidingMenuHelper.createStandardItem(this, Type.blacklist, this.heightItem, BubbleState.gray);
        MenuItem menuItemSettings = SlidingMenuHelper.createStandardItem(this, Type.settings, this.heightItem, BubbleState.gray);
        MenuItem menuItemHelp = SlidingMenuHelper.createStandardItem(this, HelpSettingsHandler.isFeedbackEnabled(this) ? Type.feedback : Type.faq, this.heightItem, BubbleState.gray);
        MenuItem menuItemExit = SlidingMenuHelper.createStandardItem(this, Type.exit, this.heightItem, BubbleState.gray);
        adapter.addItem(true, this.menuItemUser);
        adapter.addItem(true, this.menuItemGrid);
        adapter.addItem(false, new MenuDivider(this.heightItem));
        adapter.addSubList(true, this.sideLink2Items);
        adapter.addItem(true, this.menuItemStream);
        adapter.addItem(true, this.menuItemFriends);
        adapter.addItem(true, this.menuItemConversation);
        adapter.addItem(true, this.menuItemDiscussions);
        adapter.addItem(true, this.menuItemPhotos);
        adapter.addItem(true, menuItemVideos);
        adapter.addItem(true, this.menuItemGroups);
        adapter.addItem(true, this.menuItemMusic);
        adapter.addItem(true, menuItemShare);
        adapter.addItem(false, new MenuDivider(this.heightItem, 2131166577));
        adapter.addItem(false, menuItemGames);
        adapter.addItem(false, menuItemPresents);
        adapter.addItem(false, this.menuItemHolidays);
        adapter.addItem(false, menuItemProgress);
        adapter.addItem(false, menuItemOnline);
        adapter.addItem(false, new MenuDivider(this.heightItem, 2131166579));
        adapter.addItem(false, menuItemProfileSettings);
        adapter.addItem(false, menuItemRecharge);
        adapter.addItem(false, menuItemBookmarks);
        adapter.addItem(false, menuItemForum);
        adapter.addItem(false, menuItemBlackList);
        adapter.addItem(false, menuItemSettings);
        adapter.addItem(false, new MenuDivider(this.heightItem, 2131166578));
        adapter.addItem(false, menuItemHelp);
        adapter.addItem(false, menuItemExit);
        adapter.addSubList(false, this.sideLinkItems);
        adapter.addSubList(false, this.admanBannerItems);
        adapter.setSelectedItem(getSlidingMenuSelectedItem());
        return adapter;
    }

    public void onSlidingMenuChangedOpeningRatio(float openingRatio) {
        this.menuItemUser.onSlidingMenuChangedOpeningRatio(openingRatio);
        this.menuItemGrid.onSlidingMenuChangedOpeningRatio(openingRatio);
    }

    protected void onSideLinksLoaded(List<PromoLink> promoLinks) {
        Logger.m173d("promoLinks=%s", promoLinks);
        PromoLink sideLink = null;
        PromoLink sideLink2 = null;
        if (!(promoLinks == null || promoLinks.isEmpty())) {
            for (PromoLink promoLink : promoLinks) {
                if (promoLink.type == 3) {
                    sideLink = promoLink;
                } else if (promoLink.type == 4) {
                    sideLink2 = promoLink;
                }
            }
        }
        this.sideLinkItems.clear();
        if (sideLink != null) {
            Logger.m173d("Displaying side link: %s", sideLink);
            BannerItem sideLinkItem = new BannerItem(this.heightItem, this);
            sideLinkItem.setCurrentPromoLink(sideLink);
            this.sideLinkItems.add(new MenuDivider(this.heightItem, 2131166580));
            this.sideLinkItems.add(sideLinkItem);
        }
        this.sideLink2Items.clear();
        if (sideLink2 != null) {
            Logger.m173d("Displaying side link #2: %s", sideLink2);
            BannerItem sideLink2Item = new BannerItem(this.heightItem, this);
            sideLink2Item.setCurrentPromoLink(sideLink2);
            this.sideLink2Items.add(sideLink2Item);
        }
        MenuAdapter adapter = getMenuAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void onBannerClick(NativeAppwallBanner banner) {
        if (banner != null && !isFinishing()) {
            this.targetAdapter.handleBannerClick(banner);
        }
    }

    public void onMoreBannersClick(String section, List<NativeAppwallBanner> list) {
        NavigationHelper.showAdmanBannersFragment(this, section);
    }

    public WebLinksProcessor getSlidingMenuWebLinksProcessor() {
        if (this.slidingMenuWebLinksProcessor == null) {
            this.slidingMenuWebLinksProcessor = new WebLinksProcessor(this, true);
        }
        return this.slidingMenuWebLinksProcessor;
    }

    protected void updateSlidingMenuSelection() {
        if (this.menuAdapter != null) {
            this.menuAdapter.setSelectedItem(getSlidingMenuSelectedItem());
        }
    }
}
