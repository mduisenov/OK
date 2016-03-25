package ru.ok.android.app;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.ViewConfiguration;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import io.github.eterverda.sntp.SNTP;
import io.github.eterverda.sntp.android.AndroidSNTPCacheFactory;
import io.github.eterverda.sntp.android.AndroidSNTPClientFactory;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.HashMap;
import ru.mail.android.mytarget.Tracer;
import ru.ok.android.app.helper.AccountsHelper;
import ru.ok.android.app.helper.ServiceHelper;
import ru.ok.android.bus.Bus;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.GlobalBusRegistrar;
import ru.ok.android.db.DataBaseHelper;
import ru.ok.android.db.DbFailureActivity;
import ru.ok.android.db.access.AuthorizedUsersStorageFacade;
import ru.ok.android.emoji.smiles.SmilesCallback;
import ru.ok.android.emoji.smiles.SmilesManager;
import ru.ok.android.fragments.web.WebBaseFragment;
import ru.ok.android.fresco.OdklCacheKeyFactory;
import ru.ok.android.fresco.OdklLoggableNetworkFetcher;
import ru.ok.android.graylog.GrayLog;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.onelog.AppLaunchMonitor;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.onelog.api.ApiRequestsReporter.ConnectionClassChangeListener;
import ru.ok.android.services.app.MusicService.Starter;
import ru.ok.android.services.messages.MessagesService;
import ru.ok.android.services.persistent.PersistentTaskService;
import ru.ok.android.services.processors.GetRecommendedFriendsProcessor;
import ru.ok.android.services.processors.GetStatusProcessor;
import ru.ok.android.services.processors.GuestProcessor;
import ru.ok.android.services.processors.PymkProcessor;
import ru.ok.android.services.processors.SearchQuickProcessor;
import ru.ok.android.services.processors.SetStatusProcessor;
import ru.ok.android.services.processors.calls.GetVideoCallParamsProcessor;
import ru.ok.android.services.processors.discussions.DiscussionAddProcessor;
import ru.ok.android.services.processors.discussions.DiscussionChunksProcessor;
import ru.ok.android.services.processors.discussions.DiscussionProcessor;
import ru.ok.android.services.processors.discussions.DiscussionsMarkAsReadProcessor;
import ru.ok.android.services.processors.discussions.MarkAsReadDiscussionsProcessor;
import ru.ok.android.services.processors.events.GetEventsProcessor;
import ru.ok.android.services.processors.friends.FriendsFilterProcessor;
import ru.ok.android.services.processors.friends.GetFriendsProcessor;
import ru.ok.android.services.processors.friends.MutualFriendsProcessor;
import ru.ok.android.services.processors.gcm.GcmRegisterProcessor;
import ru.ok.android.services.processors.general.LikeProcessor;
import ru.ok.android.services.processors.general.RemoveOldDataProcessor;
import ru.ok.android.services.processors.general.RingtoneProcessor;
import ru.ok.android.services.processors.geo.ComplaintPlaceProcessor;
import ru.ok.android.services.processors.geo.GetCategoriesProcessor;
import ru.ok.android.services.processors.geo.GetPlacesProcessor;
import ru.ok.android.services.processors.geo.ReverseGeocodeProcessor;
import ru.ok.android.services.processors.geo.ValidatePlaceProcessor;
import ru.ok.android.services.processors.groups.GroupsProcessor;
import ru.ok.android.services.processors.login.ExpireSessionProcessor;
import ru.ok.android.services.processors.login.LogoutAllProcessor;
import ru.ok.android.services.processors.mediatopic.MediaTopicEditTextProcessor;
import ru.ok.android.services.processors.mediatopic.MediaTopicGetByPhotoProcessor;
import ru.ok.android.services.processors.mediatopic.MediatopicProcessor;
import ru.ok.android.services.processors.messaging.ConversationsProcessor;
import ru.ok.android.services.processors.messaging.MessagesChunksProcessor;
import ru.ok.android.services.processors.messaging.MessagesProcessor;
import ru.ok.android.services.processors.music.AddTrackProcessor;
import ru.ok.android.services.processors.music.DeleteTrackProcessor;
import ru.ok.android.services.processors.music.GetAlbumInfoProcessor;
import ru.ok.android.services.processors.music.GetAlbumTracksProcessor;
import ru.ok.android.services.processors.music.GetAlbumsForArtistProcessor;
import ru.ok.android.services.processors.music.GetArtistInfoProcessor;
import ru.ok.android.services.processors.music.GetArtistSimilarTracksProcessor;
import ru.ok.android.services.processors.music.GetArtistTrackProcessor;
import ru.ok.android.services.processors.music.GetCollectionInfoProcessor;
import ru.ok.android.services.processors.music.GetCollectionTracksProcessor;
import ru.ok.android.services.processors.music.GetCustomTrackProcessor;
import ru.ok.android.services.processors.music.GetHistoryMusicProcessor;
import ru.ok.android.services.processors.music.GetMyFriendsProcessor;
import ru.ok.android.services.processors.music.GetMyMusicCollectionsProcessor;
import ru.ok.android.services.processors.music.GetMyMusicProcessor;
import ru.ok.android.services.processors.music.GetPlayListInfoProcessor;
import ru.ok.android.services.processors.music.GetPlayTrackInfoProcessor;
import ru.ok.android.services.processors.music.GetPopCollectionTracksProcessor;
import ru.ok.android.services.processors.music.GetPopMusicCollectionsProcessor;
import ru.ok.android.services.processors.music.GetPopMusicProcessor;
import ru.ok.android.services.processors.music.GetRelevantProcessor;
import ru.ok.android.services.processors.music.GetSearchAlbumsProcessor;
import ru.ok.android.services.processors.music.GetSearchArtistsProcessor;
import ru.ok.android.services.processors.music.GetSearchMusicProcessor;
import ru.ok.android.services.processors.music.GetTunerTracksProcessor;
import ru.ok.android.services.processors.music.GetTunersProcessor;
import ru.ok.android.services.processors.music.GetUserMusicCollectionsProcessor;
import ru.ok.android.services.processors.music.GetUserMusicProcessor;
import ru.ok.android.services.processors.music.Play30MusicProcessor;
import ru.ok.android.services.processors.music.SetMusicStatusProcessor;
import ru.ok.android.services.processors.music.StatusPlayMusicProcessor;
import ru.ok.android.services.processors.music.SubscribeMusicCollectionProcessor;
import ru.ok.android.services.processors.music.UnSubscribeMusicCollectionProcessor;
import ru.ok.android.services.processors.notification.NotificationProcessor;
import ru.ok.android.services.processors.photo.CreatePhotoAlbumProcessor;
import ru.ok.android.services.processors.photo.DeletePhotoAlbumProcessor;
import ru.ok.android.services.processors.photo.DeletePhotoProcessor;
import ru.ok.android.services.processors.photo.EditPhotoAlbumProcessor;
import ru.ok.android.services.processors.photo.EditPhotoProcessor;
import ru.ok.android.services.processors.photo.GetPhotoAlbumProcessor;
import ru.ok.android.services.processors.photo.GetPhotoAlbumsProcessor;
import ru.ok.android.services.processors.photo.GetPhotoInfoProcessor;
import ru.ok.android.services.processors.photo.GetPhotoTagsProcessor;
import ru.ok.android.services.processors.photo.GetPhotosProcessor;
import ru.ok.android.services.processors.photo.ImageUploadNotificationProcessor;
import ru.ok.android.services.processors.photo.LikePhotoAlbumProcessor;
import ru.ok.android.services.processors.photo.LikePhotoProcessor;
import ru.ok.android.services.processors.photo.MarkPhotoProcessor;
import ru.ok.android.services.processors.photo.MarkPhotoSpamProcessor;
import ru.ok.android.services.processors.photo.SetAlbumMainPhotoProcessor;
import ru.ok.android.services.processors.photo.SetMainPhotoProcessor;
import ru.ok.android.services.processors.photo.upload.ImageUploadProcessor;
import ru.ok.android.services.processors.photo.upload.StoreLastSuccessfulImageUploadTimeProcessor;
import ru.ok.android.services.processors.photo.view.DeleteUserPhotoTagProcessor;
import ru.ok.android.services.processors.photo.view.GetAlbumInfoBatchProcessor;
import ru.ok.android.services.processors.photo.view.GetFullPhotoInfoProcessor;
import ru.ok.android.services.processors.photo.view.GetPhotoAlbumsBatchProcessor;
import ru.ok.android.services.processors.photo.view.GetViewInfoBatchProcessor;
import ru.ok.android.services.processors.poll.AppPollProcessor;
import ru.ok.android.services.processors.presents.GetPresentsProcessor;
import ru.ok.android.services.processors.presents.ReceivePresentProcessor;
import ru.ok.android.services.processors.presents.SendPresentProcessor;
import ru.ok.android.services.processors.registration.AuthorizationSettingsProcessor;
import ru.ok.android.services.processors.registration.ChangePasswordProcessor;
import ru.ok.android.services.processors.registration.RegisterWithLibVerifyProcessor;
import ru.ok.android.services.processors.settings.MediaComposerSettingsProcessor;
import ru.ok.android.services.processors.settings.ServicesSettingsProcessor;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor;
import ru.ok.android.services.processors.stickers.StickersProcessor;
import ru.ok.android.services.processors.stream.GetStreamProcessor;
import ru.ok.android.services.processors.stream.StreamMiscProcessor;
import ru.ok.android.services.processors.users.CurrentUserInfoProcessor;
import ru.ok.android.services.processors.users.UsersProcessor;
import ru.ok.android.services.processors.video.GetSimilarMoviesProcessor;
import ru.ok.android.services.processors.video.VideoLikeProcessor;
import ru.ok.android.services.processors.video.VideoProcessor;
import ru.ok.android.services.processors.xmpp.XmppSettingsPreferences;
import ru.ok.android.services.transport.AuthSessionDataStore;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.statistics.flurry.FlurryStatisticAgent;
import ru.ok.android.target.TargetUtils;
import ru.ok.android.ui.video.player.cast.VideoCastFragment;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.EmailExceptionHandler;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.image.SmilesLoader;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.ApiLibConfig;
import ru.ok.java.api.ServiceStateHolder;
import ru.ok.java.api.utils.Configuration;
import ru.ok.java.api.utils.Constants.Api;
import ru.ok.model.UserInfo;

public class OdnoklassnikiApplication extends MultiDexApplication implements ActivityLifecycleCallbacks {
    private static Context context;
    private static UserInfo currentUser;
    public static long userUpdateTime;
    private boolean dbFailureNotificationDisplayed;
    private volatile DataBaseHelper dbHelper;
    private HashMap<String, Typeface> fonts;
    private final BroadcastReceiver localeChangedReceiver;
    private LocalizationManager localizationManager;
    private ServiceHelper serviceHelper;
    private WebHttpLoader webHttpLoader;

    /* renamed from: ru.ok.android.app.OdnoklassnikiApplication.1 */
    class C02141 extends BroadcastReceiver {
        C02141() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ru.ok.android.utils.localization.LOCALE_CHANGED")) {
                WebBaseFragment.webCache.onLocaleChanged();
            }
        }
    }

    /* renamed from: ru.ok.android.app.OdnoklassnikiApplication.2 */
    class C02152 implements SmilesCallback {
        C02152() {
        }

        public Drawable getDrawableByUrl(String url, int width) {
            return SmilesLoader.getInstance().getDrawableByUrl(url, width);
        }

        public void executeRunnable(Runnable runnable, boolean shortOperation) {
            GlobalBus.post(runnable, shortOperation ? 2131623945 : 2131623944);
        }

        public void logEvent(String eventName, String... params) {
            Pair<String, String>[] pairs = null;
            if (params != null && params.length > 0) {
                int count = params.length / 2;
                pairs = new Pair[count];
                for (int i = 0; i < count; i++) {
                    pairs[i] = new Pair(params[(i * 2) + 0], params[(i * 2) + 1]);
                }
            }
            StatisticManager.getInstance().addStatisticEvent(eventName, pairs);
        }

        public String getTranslatedString(int resourceId) {
            return LocalizationManager.getString(OdnoklassnikiApplication.this, resourceId);
        }
    }

    /* renamed from: ru.ok.android.app.OdnoklassnikiApplication.3 */
    class C02163 implements Runnable {
        C02163() {
        }

        public void run() {
            SNTP.safeCurrentTimeMillis();
        }
    }

    /* renamed from: ru.ok.android.app.OdnoklassnikiApplication.4 */
    static class C02174 implements Runnable {
        final /* synthetic */ String val$login;
        final /* synthetic */ UserInfo val$user;

        C02174(UserInfo userInfo, String str) {
            this.val$user = userInfo;
            this.val$login = str;
        }

        public void run() {
            AuthorizedUsersStorageFacade.updateUserInfoWithLogin(this.val$user, this.val$login);
        }
    }

    public OdnoklassnikiApplication() {
        this.localeChangedReceiver = new C02141();
        this.dbFailureNotificationDisplayed = false;
    }

    static {
        ApiLibConfig.DEBUG = false;
        ApiLibConfig.TEST_MODE = false;
        userUpdateTime = 0;
    }

    public static Context getContext() {
        return context;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        context = this;
    }

    public void registerProcessors() {
        Bus bus = GlobalBus.getInstance();
        GlobalBusRegistrar reg = GlobalBusRegistrar.INSTANCE;
        reg.register(bus, new Starter());
        reg.register(bus, new AddTrackProcessor());
        reg.register(bus, new DeleteTrackProcessor());
        reg.register(bus, new GetSearchMusicProcessor());
        reg.register(bus, new UsersProcessor());
        reg.register(bus, new SetStatusProcessor());
        reg.register(bus, new GetStatusProcessor());
        reg.register(bus, new ImageUploadProcessor());
        reg.register(bus, new GcmRegisterProcessor());
        reg.register(bus, new CurrentUserInfoProcessor());
        reg.register(bus, new GetFriendsProcessor());
        reg.register(bus, new GetEventsProcessor());
        reg.register(bus, new GetPhotoAlbumsBatchProcessor());
        reg.register(bus, new GetMyMusicProcessor());
        reg.register(bus, new GetPlayTrackInfoProcessor());
        reg.register(bus, new GetMyFriendsProcessor());
        reg.register(bus, new GetUserMusicProcessor());
        reg.register(bus, new SetMusicStatusProcessor());
        reg.register(bus, new CreatePhotoAlbumProcessor());
        reg.register(bus, new GetVideoCallParamsProcessor());
        reg.register(bus, new MarkAsReadDiscussionsProcessor());
        reg.register(bus, new Play30MusicProcessor());
        reg.register(bus, new GetSearchAlbumsProcessor());
        reg.register(bus, new GetSearchArtistsProcessor());
        reg.register(bus, new GetRelevantProcessor());
        reg.register(bus, new GetAlbumTracksProcessor());
        reg.register(bus, new GetArtistTrackProcessor());
        reg.register(bus, new GetUserMusicCollectionsProcessor());
        reg.register(bus, new GetMyMusicCollectionsProcessor());
        reg.register(bus, new GetCollectionTracksProcessor());
        reg.register(bus, new GetPopMusicProcessor());
        reg.register(bus, new GetPopMusicCollectionsProcessor());
        reg.register(bus, new GetPopCollectionTracksProcessor());
        reg.register(bus, new GetArtistSimilarTracksProcessor());
        reg.register(bus, new GetAlbumsForArtistProcessor());
        reg.register(bus, new GetHistoryMusicProcessor());
        reg.register(bus, new GetAlbumInfoProcessor());
        reg.register(bus, new GetArtistInfoProcessor());
        reg.register(bus, new GetPhotoInfoProcessor());
        reg.register(bus, new GetViewInfoBatchProcessor());
        reg.register(bus, new GetPhotosProcessor());
        reg.register(bus, new GetPhotoTagsProcessor());
        reg.register(bus, new MarkPhotoProcessor());
        reg.register(bus, new GetAlbumInfoBatchProcessor());
        reg.register(bus, new LikePhotoProcessor());
        reg.register(bus, new GetFullPhotoInfoProcessor());
        reg.register(bus, new DeletePhotoAlbumProcessor());
        reg.register(bus, new LikePhotoAlbumProcessor());
        reg.register(bus, new SetAlbumMainPhotoProcessor());
        reg.register(bus, new SetMainPhotoProcessor());
        reg.register(bus, new MarkPhotoSpamProcessor());
        reg.register(bus, new GetPhotoAlbumsProcessor());
        reg.register(bus, new GetCollectionInfoProcessor());
        reg.register(bus, new GetPlayListInfoProcessor());
        reg.register(bus, new DeletePhotoProcessor());
        reg.register(bus, new EditPhotoAlbumProcessor());
        reg.register(bus, new ConversationsProcessor());
        reg.register(bus, new MessagesProcessor());
        reg.register(bus, new MessagesChunksProcessor());
        reg.register(bus, new DiscussionProcessor());
        reg.register(bus, new DiscussionChunksProcessor());
        reg.register(bus, new DiscussionAddProcessor());
        reg.register(bus, new DiscussionsMarkAsReadProcessor());
        reg.register(bus, new EditPhotoProcessor());
        reg.register(bus, new GroupsProcessor());
        reg.register(bus, new FriendsFilterProcessor(this));
        reg.register(bus, new GetPhotoAlbumProcessor());
        reg.register(bus, new ServicesSettingsProcessor(this));
        reg.register(bus, new SubscribeMusicCollectionProcessor());
        reg.register(bus, new UnSubscribeMusicCollectionProcessor());
        reg.register(bus, new DeleteUserPhotoTagProcessor());
        reg.register(bus, new SearchQuickProcessor());
        reg.register(bus, new GetTunersProcessor());
        reg.register(bus, new GetTunerTracksProcessor());
        reg.register(bus, new StatusPlayMusicProcessor());
        reg.register(bus, new GuestProcessor());
        reg.register(bus, new GetStreamProcessor());
        reg.register(bus, new StreamMiscProcessor());
        reg.register(bus, new PymkProcessor());
        reg.register(bus, new LikeProcessor());
        reg.register(bus, new RingtoneProcessor());
        reg.register(bus, new MediatopicProcessor());
        reg.register(bus, new MediaTopicGetByPhotoProcessor());
        reg.register(bus, new MutualFriendsProcessor());
        reg.register(bus, new GetPlacesProcessor());
        reg.register(bus, new ComplaintPlaceProcessor());
        reg.register(bus, new ReverseGeocodeProcessor());
        reg.register(bus, new ValidatePlaceProcessor());
        reg.register(bus, new GetCategoriesProcessor());
        reg.register(bus, new VideoProcessor());
        reg.register(bus, new GetCustomTrackProcessor());
        reg.register(bus, new RemoveOldDataProcessor());
        reg.register(bus, new ExpireSessionProcessor());
        reg.register(bus, new AuthorizationSettingsProcessor(this));
        reg.register(bus, new ChangePasswordProcessor());
        reg.register(bus, new RegisterWithLibVerifyProcessor());
        reg.register(bus, new VideoLikeProcessor());
        reg.register(bus, new MediaComposerSettingsProcessor(this));
        reg.register(bus, new LogoutAllProcessor());
        reg.register(bus, new StartSettingsGetProcessor(this));
        reg.register(bus, new NotificationProcessor());
        reg.register(bus, new SendPresentProcessor());
        reg.register(bus, new ReceivePresentProcessor());
        reg.register(bus, new GetPresentsProcessor());
        reg.register(bus, new GetSimilarMoviesProcessor());
        reg.register(bus, new GetRecommendedFriendsProcessor());
        reg.register(bus, new ImageUploadNotificationProcessor());
        reg.register(bus, new AppPollProcessor());
        reg.register(bus, new MediaTopicEditTextProcessor());
        reg.register(bus, new StickersProcessor(this));
        reg.register(bus, new StoreLastSuccessfulImageUploadTimeProcessor());
    }

    public void onCreate() {
        super.onCreate();
        registerProcessors();
        initConfiguration();
        configureLogger();
        configureOneLog();
        initReceivers();
        initDatabase();
        initAsyncTask();
        onVersionCodeChanged();
        initActionBarDots();
        initAPI();
        initSNTP();
        initLocalizationManager();
        initExceptionHandlers();
        initBus();
        initAccounts();
        initSmiles();
        initCast();
        tryRestartPersistentTasks(this);
        initAdman(this, getCurrentUser());
        registerActivityLifecycleCallbacks(this);
        registerActivityLifecycleCallbacks(AppLaunchMonitor.getInstance());
        FlurryStatisticAgent.initFlurry(this);
        reportDeviceParams();
        initConnectionClassLogger();
        initFresco();
        SpritesHelper.initialize(this);
    }

    private void initConnectionClassLogger() {
        ConnectionClassManager.getInstance().register(new ConnectionClassChangeListener());
    }

    private void initFresco() {
        Fresco.initialize(getApplicationContext(), ImagePipelineConfig.newBuilder(context).setCacheKeyFactory(new OdklCacheKeyFactory()).setDownsampleEnabled(true).setNetworkFetcher(new OdklLoggableNetworkFetcher()).build());
    }

    private void initSmiles() {
        SmilesManager.setSmilesCallback(new C02152());
    }

    private void initAccounts() {
        if (Settings.hasLoginData(this)) {
            if (!AccountsHelper.hasAccountForCurrentUser(this)) {
                AccountsHelper.registerAccountForUser(this, getCurrentUser());
            }
            AccountsHelper.storeAuthenticationToken(this, JsonSessionTransportProvider.getInstance().getStateHolder());
        }
        AccountsHelper.requestSyncIfNeeded(this);
    }

    private void initConfiguration() {
        Configuration.init(this);
    }

    private void initReceivers() {
        LocalBroadcastManager.getInstance(this).registerReceiver(this.localeChangedReceiver, new IntentFilter("ru.ok.android.utils.localization.LOCALE_CHANGED"));
    }

    private void initCast() {
        VideoCastManager.initialize(this, "559D7832", VideoCastFragment.class, null).enableFeatures(31);
    }

    private void initBus() {
        GlobalBus.send(2131624101, new BusEvent());
        GlobalBus.send(2131624098, new BusEvent());
        GlobalBus.send(2131624097, new BusEvent());
        GlobalBus.send(2131623982, new BusEvent());
        GlobalBus.send(2131624052, new BusEvent());
        GlobalBus.send(2131624102, new BusEvent());
        MessagesService.sendActionSendAll(this);
        ServiceHelper.from().sendUndeliveredDiscussionComments();
    }

    private void initExceptionHandlers() {
        Thread.setDefaultUncaughtExceptionHandler(new EmailExceptionHandler(this));
        Thread.setDefaultUncaughtExceptionHandler(new AppCrashHandler(this));
        Thread.setDefaultUncaughtExceptionHandler(new VersionCodeCrushHandler(this));
    }

    private void initAPI() {
        String strCid = Settings.getStrValue(getContext(), "CID");
        if (TextUtils.isEmpty(strCid)) {
            strCid = "";
        }
        Api.CID_VALUE = strCid;
    }

    private void initSNTP() {
        SNTP.setClient(AndroidSNTPClientFactory.create());
        SNTP.setCache(AndroidSNTPCacheFactory.create(this));
        ThreadUtil.execute(new C02163());
    }

    private void initLocalizationManager() {
        this.localizationManager = new LocalizationManager(this);
        this.localizationManager.updateLocaleIfNeeded();
    }

    private void initActionBarDots() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
        }
    }

    private void onVersionCodeChanged() {
        try {
            int storedVersionCode = Settings.getIntValueInvariable(this, "version_code_key", 0);
            int versionCode = Utils.getVersionCode(this);
            if (versionCode > storedVersionCode) {
                configureCookies(versionCode);
                XmppSettingsPreferences.resetLastCheckDate(getContext());
            }
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }

    private void configureCookies(int versionCode) {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeSessionCookie();
        Settings.storeIntValueInvariable(this, "version_code_key", versionCode);
        cookieSyncManager.sync();
    }

    private void initAsyncTask() {
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
        }
    }

    private void initDatabase() {
        this.dbHelper = new DataBaseHelper(this);
    }

    public static void initAdman(Context context, UserInfo user) {
        Tracer.enabled = false;
        String lang = Settings.getCurrentLocale(context);
        String uid = (user == null || TextUtils.isEmpty(user.uid)) ? null : user.uid;
        Logger.m172d("Calling to AdmanTracker.init...");
        TargetUtils.initTarget(uid, lang, false);
        MyTrackerUtils.initMyTracker(context, user, lang);
    }

    private static void updateTargetOkUser(@NonNull String uid) {
        String admanUid;
        String lang = Settings.getCurrentLocale(getContext());
        if (uid == null) {
            admanUid = "";
        } else {
            admanUid = uid;
        }
        TargetUtils.initTarget(admanUid, lang, true);
    }

    private void reportDeviceParams() {
        Pair<String, String> pairType = new Pair("device_type", DeviceUtils.getType(this) != DeviceLayoutType.SMALL ? "tablet" : "phone");
        StatisticManager.getInstance().addStatisticEvent("device_2", pairType);
    }

    public LocalizationManager getLocalizationManager() {
        return this.localizationManager;
    }

    public static void onLoggedInUserId(String userId) {
        Logger.m173d("%s", userId);
        if (!TextUtils.equals(getCurrentUser().uid, userId)) {
            setCurrentUser(new UserInfo(userId));
        }
    }

    public static void setCurrentUser(UserInfo user) {
        String userId = null;
        Logger.m173d("%s", user);
        String previousUid = currentUser == null ? null : currentUser.uid;
        if (user != null) {
            userId = user.uid;
        }
        currentUser = user;
        userUpdateTime = user == null ? 0 : System.currentTimeMillis();
        Context context = getContext();
        if (context != null) {
            Settings.storeCurrentUserValue(context, user);
            updateUserInfoWithLoginAsync();
            if (!TextUtils.equals(previousUid, userId)) {
                tryRestartPersistentTasks(context);
            }
        }
        if (!TextUtils.equals(previousUid, userId)) {
            updateTargetOkUser(userId);
        }
        MyTrackerUtils.onCurrentUserChanged(user);
    }

    public static UserInfo getCurrentUser() {
        if (currentUser == null) {
            currentUser = Settings.getCurrentUser(getContext());
        }
        return currentUser;
    }

    public static void updateUserInfoWithLoginAsync() {
        UserInfo currentUser = getCurrentUser();
        if (!TextUtils.isEmpty(currentUser.uid)) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute(new C02174(new UserInfo(currentUser), Settings.getUserName(getContext())));
        }
    }

    public ServiceHelper getServiceHelper() {
        if (this.serviceHelper == null) {
            this.serviceHelper = new ServiceHelper(this);
        }
        return this.serviceHelper;
    }

    public WebHttpLoader getWebHttpLoader() {
        if (this.webHttpLoader == null) {
            this.webHttpLoader = new WebHttpLoader(getContext());
        }
        return this.webHttpLoader;
    }

    public void onTerminate() {
        super.onTerminate();
        if (this.webHttpLoader != null) {
            this.webHttpLoader.dispose();
        }
    }

    private static void tryRestartPersistentTasks(Context context) {
        UserInfo currentUser = getCurrentUser();
        if (currentUser == null || TextUtils.isEmpty(currentUser.uid)) {
            Logger.m184w("Not currently logged in, not re-starting persistent task queue");
            return;
        }
        Logger.m172d("Restarting persistent task queue");
        PersistentTaskService.restart(context, currentUser.uid);
    }

    public Typeface getFontFromAssets(String filename) throws FileNotFoundException {
        if (filename == null) {
            throw new FileNotFoundException("Font filename is null.");
        }
        HashMap<String, Typeface> fonts = this.fonts;
        if (fonts == null) {
            fonts = new HashMap();
            this.fonts = fonts;
        }
        Typeface font = (Typeface) fonts.get(filename);
        if (font == null && !fonts.containsKey(filename)) {
            try {
                font = Typeface.createFromAsset(getAssets(), filename);
            } catch (Throwable e) {
                Logger.m177e("Failed to load font from asset: %s", filename);
                Logger.m178e(e);
            }
            fonts.put(filename, font);
        }
        if (font != null) {
            return font;
        }
        throw new FileNotFoundException("Failed to load font from asset: " + filename);
    }

    private void configureLogger() {
        Logger.setLoggingEnabled(false);
        Logger.setLogToFile(false, this);
    }

    private void configureOneLog() {
        ServiceStateHolder data = AuthSessionDataStore.getDefault(this);
        OneLog.attachBaseContext(this);
        OneLog.attachBaseUrl(Uri.parse(data.getBaseUrl()));
        OneLog.attachApplicationKey(data.getAppKey(), data.getSecretAppKey());
        OneLog.attachSessionKey(data.getSessionKey(), data.getSecretSessionKey());
        GrayLog.attachBaseContext(this);
        GrayLog.attachBaseUrl(Uri.parse(data.getBaseUrl()));
        GrayLog.attachApplicationKey(data.getAppKey(), data.getSecretAppKey());
        GrayLog.attachSessionKey(data.getSessionKey(), data.getSecretSessionKey());
    }

    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    public void onActivityStarted(Activity activity) {
        MyTrackerUtils.onActivityStarted(activity);
    }

    public void onActivityResumed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
        MyTrackerUtils.onActivityStopper(activity);
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    @Nullable
    public static SQLiteDatabase getDatabase(Context context) {
        return ((OdnoklassnikiApplication) context.getApplicationContext()).getDatabase();
    }

    private SQLiteOpenHelper getDBHelper() {
        return this.dbHelper;
    }

    @Nullable
    private SQLiteDatabase getDatabase() {
        try {
            return getDBHelper().getWritableDatabase();
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to open DB");
            showDBFailureNotification();
            DataBaseHelper.reportDBFailure(getContext(), "OdklApplication failed to open DB", e);
            return null;
        }
    }

    public void showDBFailureNotification() {
        if (!this.dbFailureNotificationDisplayed) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (nm != null) {
                LocalizationManager localizationManager = LocalizationManager.from(this);
                if (localizationManager != null) {
                    Intent intent = new Intent(this, DbFailureActivity.class);
                    intent.addFlags(268435456);
                    AppLaunchLog.fillLocalDbFailure(intent);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 2131624284, intent, 0);
                    Builder builder = new Builder(this);
                    builder.setContentTitle(localizationManager.getString(2131165661));
                    BigTextStyle style = new BigTextStyle();
                    style.bigText(localizationManager.getString(2131165660));
                    builder.setStyle(style);
                    builder.setContentText(localizationManager.getString(2131165660));
                    builder.setSmallIcon(2130838516);
                    builder.setContentIntent(pendingIntent);
                    nm.notify(2131624270, builder.build());
                    this.dbFailureNotificationDisplayed = true;
                }
            }
        }
    }
}
