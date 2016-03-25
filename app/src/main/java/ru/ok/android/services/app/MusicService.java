package ru.ok.android.services.app;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.jivesoftware.smack.packet.Stanza;
import ru.ok.android.C0206R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.music.AsyncStorageOperations;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.fresco.FrescoOdkl.BitmapProcessor;
import ru.ok.android.model.cache.music.MusicBaseFileCache;
import ru.ok.android.model.cache.music.async.AsyncFileCache.CacheDataCallBack;
import ru.ok.android.model.cache.music.async.MusicAsyncFileCache;
import ru.ok.android.model.music.MusicInfoContainer;
import ru.ok.android.music.ConnectionReceiver;
import ru.ok.android.music.CursorPlayList;
import ru.ok.android.music.CursorPlayList.CursorIsNullException;
import ru.ok.android.music.DownloadTask;
import ru.ok.android.music.HeadsetIntentReceiver;
import ru.ok.android.music.MediaButtonIntentReceiver;
import ru.ok.android.music.MusicManager;
import ru.ok.android.music.MusicPhoneStateListener;
import ru.ok.android.music.StreamMediaPlayer;
import ru.ok.android.music.StreamMediaPlayer.PlayerCallBack;
import ru.ok.android.music.VideoChatBroadcastReceiver;
import ru.ok.android.music.data.BufferedMusicFile;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.app.music.ApiHandler;
import ru.ok.android.services.app.music.ApiHandler.GetPlayTrackInfoListener;
import ru.ok.android.services.app.notification.MusicNotificationHelper;
import ru.ok.android.services.app.remote.AudioFocusHelper;
import ru.ok.android.services.app.remote.MediaButtonHelper;
import ru.ok.android.services.app.remote.MusicFocusable;
import ru.ok.android.services.app.remote.MusicIntentReceiver;
import ru.ok.android.services.app.remote.RemoteControlClientCompat;
import ru.ok.android.services.app.remote.RemoteControlClientCompat.MetadataEditorCompat;
import ru.ok.android.services.app.remote.RemoteControlHelper;
import ru.ok.android.services.transport.exception.NetworkException;
import ru.ok.android.services.transport.exception.NoConnectionException;
import ru.ok.android.services.transport.exception.ServerNotFoundException;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.custom.PlayerImageView;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.Tag;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.bus.BusProtocol;
import ru.ok.android.utils.controls.PlayerSetter.RepeatState;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.music.MusicPlayerUtils;
import ru.ok.android.utils.settings.Settings;
import ru.ok.android.widget.music.MusicBaseWidget;
import ru.ok.java.api.exceptions.ServerReturnErrorException;
import ru.ok.model.wmf.PlayTrackInfo;
import ru.ok.model.wmf.Track;

public final class MusicService extends Service implements MusicManager, PlayerCallBack, GetPlayTrackInfoListener, MusicFocusable {
    private static BusEvent lastState;
    private static Track[] tracksToPlay;
    private ConnectionReceiver connectionReceiver;
    private Track currentTrack;
    private BufferedPlayInfo currentTrackInfo;
    private int downloadValue;
    private BroadcastReceiver endCallReceiver;
    private volatile String errorMessage;
    private Handler handlerApi;
    private final Handler handlerApiNext;
    private boolean isPlay30Send;
    private LocalBinder localBinder;
    private final Messenger mApiMessenger;
    private final Messenger mApiMessengerNext;
    private AudioFocus mAudioFocus;
    private AudioFocusHelper mAudioFocusHelper;
    private AudioManager mAudioManager;
    private HeadsetIntentReceiver mHeadsetReceiver;
    private MediaButtonIntentReceiver mMediaButtonIntentReceiver;
    private ComponentName mMediaButtonReceiverComponent;
    private RemoteControlClientCompat mRemoteControlClientCompat;
    private StreamMediaPlayer mStreamMediaPlayer;
    private PhoneStateListener myPhoneListener;
    private final DownloadTask nextDownloadTask;
    private Handler observerHandler;
    private CursorPlayList playList;
    private volatile InformationState playState;
    private PlaylistChangedObserver playlistChangedObserver;
    private String playlistKey;
    private RepeatState repeat;
    private final Handler streamPlayHandler;

    /* renamed from: ru.ok.android.services.app.MusicService.10 */
    static /* synthetic */ class AnonymousClass10 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$utils$controls$PlayerSetter$RepeatState;

        static {
            $SwitchMap$ru$ok$android$utils$controls$PlayerSetter$RepeatState = new int[RepeatState.values().length];
            try {
                $SwitchMap$ru$ok$android$utils$controls$PlayerSetter$RepeatState[RepeatState.repeatOne.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$utils$controls$PlayerSetter$RepeatState[RepeatState.repeat.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    /* renamed from: ru.ok.android.services.app.MusicService.1 */
    class C04111 extends Handler {
        C04111() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECEIVED_VALUE:
                    MusicService.this.startStreamTrack(msg.obj);
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /* renamed from: ru.ok.android.services.app.MusicService.2 */
    class C04122 implements BitmapProcessor {
        final /* synthetic */ boolean val$isPlaying;
        final /* synthetic */ Track val$track;

        C04122(Track track, boolean z) {
            this.val$track = track;
            this.val$isPlaying = z;
        }

        public void processBitmap(@Nullable Bitmap bitmap) {
            if (bitmap == null || !bitmap.isRecycled()) {
                MusicService.this.showNotification(this.val$track, this.val$isPlaying, bitmap);
            } else {
                MusicService.this.showNotification(this.val$track, this.val$isPlaying, null);
            }
            if (VERSION.SDK_INT < 14) {
                return;
            }
            if (this.val$isPlaying) {
                MusicService.this.updateLockScreenControl(this.val$track, bitmap, 3);
            } else {
                MusicService.this.updateLockScreenControl(this.val$track, bitmap, 2);
            }
        }
    }

    /* renamed from: ru.ok.android.services.app.MusicService.3 */
    class C04133 implements BitmapProcessor {
        final /* synthetic */ boolean val$isPlaying;
        final /* synthetic */ Track val$track;

        C04133(Track track, boolean z) {
            this.val$track = track;
            this.val$isPlaying = z;
        }

        public void processBitmap(@Nullable Bitmap bitmap) {
            MusicBaseWidget.updateAllWidgetsByMusicInfo(MusicService.this.getBaseContext(), bitmap, this.val$track, this.val$isPlaying);
            if (bitmap == null) {
                MusicService.this.updateCurrentTrackBitmap(this.val$isPlaying);
            }
        }
    }

    /* renamed from: ru.ok.android.services.app.MusicService.4 */
    class C04154 extends BaseBitmapDataSubscriber {
        final /* synthetic */ long val$currentTrackId;
        final /* synthetic */ boolean val$isPlaying;

        /* renamed from: ru.ok.android.services.app.MusicService.4.1 */
        class C04141 implements Runnable {
            C04141() {
            }

            public void run() {
                if (MusicService.this.currentTrack != null && MusicService.this.currentTrack.id == C04154.this.val$currentTrackId) {
                    MusicService.this.updateRemoteControls(MusicService.this.currentTrack, C04154.this.val$isPlaying);
                    MusicService.this.sendStatus();
                }
            }
        }

        C04154(long j, boolean z) {
            this.val$currentTrackId = j;
            this.val$isPlaying = z;
        }

        protected void onNewResultImpl(@Nullable Bitmap bitmap) {
            ThreadUtil.executeOnMain(new C04141());
        }

        protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
        }
    }

    /* renamed from: ru.ok.android.services.app.MusicService.5 */
    class C04165 implements BitmapProcessor {
        C04165() {
        }

        public void processBitmap(@Nullable Bitmap bitmap) {
            MusicBaseWidget.updateAllWidgetsByMusicInfo(MusicService.this.getBaseContext(), bitmap, MusicService.this.playList == null ? null : MusicService.this.playList.getTrack(), false);
        }
    }

    /* renamed from: ru.ok.android.services.app.MusicService.6 */
    class C04176 implements BitmapProcessor {
        final /* synthetic */ Intent val$intent;

        C04176(Intent intent) {
            this.val$intent = intent;
        }

        public void processBitmap(@Nullable Bitmap bitmap) {
            boolean isPlaying = MusicService.this.getMediaPlayer().isPlaying();
            int appWidgetId = this.val$intent.getIntExtra("ru.ok.android.widget.music.EXTRA_WIDGET_ID", -1);
            if (appWidgetId >= 0) {
                MusicBaseWidget.updateWidget(MusicService.this.getBaseContext(), (Class) this.val$intent.getSerializableExtra("ru.ok.android.widget.music.EXTRA_WIDGET_CLASS"), appWidgetId, bitmap, MusicService.this.playList != null ? MusicService.this.playList.getTrack() : null, isPlaying);
                if (bitmap == null && MusicService.this.currentTrackInfo != null) {
                    MusicService.this.updateCurrentTrackBitmap(isPlaying);
                }
            }
        }
    }

    /* renamed from: ru.ok.android.services.app.MusicService.7 */
    class C04187 implements CacheDataCallBack {
        C04187() {
        }

        public void onCacheDataSuccessful(PlayTrackInfo info, InputStream is) {
            IOUtils.closeSilently((Closeable) is);
        }

        public void onCacheDataFail(PlayTrackInfo info, InputStream is) {
            IOUtils.closeSilently((Closeable) is);
        }
    }

    /* renamed from: ru.ok.android.services.app.MusicService.8 */
    class C04198 implements GetPlayTrackInfoListener {
        C04198() {
        }

        public void onGetPlayInfo(PlayTrackInfo info) {
            if (MusicService.this.isCachedMusic()) {
                MusicService.this.nextDownloadTask.startBufferingNextTrack(info);
            }
        }

        public void onGetPlayInfoError(Object errorObject) {
        }
    }

    abstract class BaseAsyncTask extends AsyncTask<Void, Void, Boolean> {
        int startSearchPosition;

        protected abstract boolean step();

        BaseAsyncTask() {
        }

        public void onPreExecute() {
            super.onPreExecute();
            this.startSearchPosition = MusicService.this.getPlayList().getPosition();
        }

        public Boolean doInBackground(Void... params) {
            if (Settings.isPlayOnlyCache(MusicService.this.getApplicationContext())) {
                while (step()) {
                    if (MusicService.this.getPlayList().getPosition() == this.startSearchPosition) {
                        return Boolean.valueOf(false);
                    }
                    Track track = MusicService.this.getPlayList().getTrack();
                    if (track != null) {
                        if (MusicAsyncFileCache.getInstance().isKeyContains(String.valueOf(track.id))) {
                        }
                    }
                    return Boolean.valueOf(true);
                }
                MusicService.this.getPlayList().moveToPosition(this.startSearchPosition);
                return Boolean.valueOf(false);
            } else if (step()) {
                return Boolean.valueOf(true);
            } else {
                return Boolean.valueOf(false);
            }
        }

        public void onPostExecute(Boolean isPlay) {
            super.onPostExecute(isPlay);
            if (isPlay.booleanValue()) {
                MusicService.this.startPlay();
            }
        }

        public void executeOnCurrentThread() {
            onPreExecute();
            boolean result = doInBackground(new Void[0]).booleanValue();
            if (isCancelled()) {
                onCancelled(Boolean.valueOf(result));
            } else {
                onPostExecute(Boolean.valueOf(result));
            }
        }
    }

    /* renamed from: ru.ok.android.services.app.MusicService.9 */
    class C04209 extends BaseAsyncTask {
        C04209() {
            super();
        }

        protected boolean step() {
            if (MusicService.this.getPlayList().moveToNext()) {
                return true;
            }
            return false;
        }
    }

    enum AudioFocus {
        NoFocusNoDuck,
        NoFocusCanDuck,
        Focused
    }

    public static final class BufferedPlayInfo {
        public final BufferedMusicFile buffer;
        public final PlayTrackInfo info;

        private BufferedPlayInfo(PlayTrackInfo info, BufferedMusicFile buffer) {
            this.info = info;
            this.buffer = buffer;
        }

        public static BufferedPlayInfo create(PlayTrackInfo info) {
            return new BufferedPlayInfo(info, null);
        }

        public static BufferedPlayInfo create(PlayTrackInfo info, BufferedMusicFile file) {
            return new BufferedPlayInfo(info, file);
        }
    }

    public enum InformationState {
        DATA_QUERY,
        START,
        BUFFERED,
        PLAY,
        PAUSE,
        STOP,
        ERROR
    }

    public class LocalBinder extends Binder {
        public void nextBlocking() {
            MusicService.this.moveCommandBlocking(true);
        }

        public void prevBlocking() {
            MusicService.this.moveCommandBlocking(false);
        }

        public Track getNextTrack() {
            return MusicService.this.getNeighborTrack(true);
        }

        public Track getPrevTrack() {
            return MusicService.this.getNeighborTrack(false);
        }

        public Track getCurrentTrack() {
            CursorPlayList playList = MusicService.this.getPlayList();
            if (playList == null) {
                return null;
            }
            Track track = playList.getTrack();
            if (track != null) {
                return track;
            }
            return MusicService.this.currentTrack;
        }

        public PlayTrackInfo getCurrentTrackInfo() {
            if (MusicService.this.currentTrackInfo != null) {
                return MusicService.this.currentTrackInfo.info;
            }
            return null;
        }

        public boolean isPlaying() {
            return MusicService.this.getState() == MusicState.PLAYING;
        }
    }

    public enum MusicState {
        PLAYING,
        PAUSING,
        UNKNOWN
    }

    class NextTask extends BaseAsyncTask {
        NextTask() {
            super();
        }

        protected boolean step() {
            return MusicService.this.moveToNext();
        }
    }

    private class PlaylistChangedObserver extends ContentObserver {
        public PlaylistChangedObserver() {
            super(MusicService.this.observerHandler);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MusicService.this.playList != null) {
                MusicService.this.playList.onPlaylistChanged();
            }
        }
    }

    class PrevTask extends BaseAsyncTask {
        PrevTask() {
            super();
        }

        protected boolean step() {
            return MusicService.this.moveToPrev();
        }
    }

    public static final class Starter {
        @Subscribe(to = 2131624108)
        public void state(BusEvent event) {
            startMusicService("ru.ok.android.music.STATE_STREAM_MEDIA_PLAYER");
        }

        @Subscribe(to = 2131624086)
        public void pause(BusEvent event) {
            startMusicService("ru.ok.android.music.PAUSE");
        }

        private static void startMusicService(String action) {
            Context context = OdnoklassnikiApplication.getContext();
            context.startService(new Intent(context, MusicService.class).setAction(action));
        }
    }

    public MusicService() {
        this.mAudioFocus = AudioFocus.NoFocusNoDuck;
        this.mAudioFocusHelper = null;
        this.repeat = RepeatState.none;
        this.observerHandler = new Handler();
        this.isPlay30Send = false;
        this.localBinder = new LocalBinder();
        this.playState = InformationState.STOP;
        this.errorMessage = null;
        this.streamPlayHandler = new C04111();
        this.nextDownloadTask = new DownloadTask(this, MusicAsyncFileCache.getInstance());
        this.currentTrackInfo = null;
        this.currentTrack = null;
        this.handlerApi = new ApiHandler(this);
        this.mApiMessenger = new Messenger(this.handlerApi);
        this.downloadValue = 0;
        this.handlerApiNext = new ApiHandler(new C04198());
        this.mApiMessengerNext = new Messenger(this.handlerApiNext);
    }

    public static void finishPlayMusic(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.FINISH");
        context.startService(intent);
    }

    public static InformationState getInformationState(BusEvent event) {
        InformationState state = (InformationState) event.bundleOutput.getSerializable(BusProtocol.PREF_MEDIA_PLAYER_STATE);
        return state != null ? state : InformationState.STOP;
    }

    static {
        lastState = null;
    }

    public static BusEvent getLastState() {
        return lastState;
    }

    public InformationState getInformationState() {
        return this.playState;
    }

    public void setPlayState(InformationState playState) {
        setPlayState(playState, true);
    }

    public void setPlayState(InformationState playState, boolean needSendInfoAboutTrack) {
        setPlayState(playState, needSendInfoAboutTrack, false);
    }

    private void setPlayState(InformationState playState, boolean needInfo, boolean silent) {
        if (this.playState != playState) {
            this.playState = playState;
            if (!silent) {
                GlobalBus.send(2131624252, createStateEvent(needInfo, true));
            }
        }
    }

    public void sendStatus() {
        GlobalBus.send(2131624252, createStateEvent(true, false));
    }

    public BusEvent createStateEvent(boolean needSendInfoAboutTrack, boolean isNewState) {
        MusicInfoContainer musicInfoContainer;
        Bundle bundle = new Bundle();
        Track prev = getNeighborTrack(false);
        Track next = getNeighborTrack(true);
        if (!needSendInfoAboutTrack || (this.currentTrackInfo == null && this.currentTrack == null)) {
            musicInfoContainer = null;
        } else {
            musicInfoContainer = new MusicInfoContainer(this.currentTrack, this.currentTrackInfo != null ? this.currentTrackInfo.info : null, prev, next, isShufflePlayList());
        }
        bundle.putParcelable(BusProtocol.PREF_MEDIA_PLAYER_STATE_MUSIC_INFO_CONTAINER, musicInfoContainer);
        InformationState state = getInformationState();
        bundle.putSerializable(BusProtocol.PREF_MEDIA_PLAYER_STATE, state);
        bundle.putBoolean(BusProtocol.PREF_IS_UPDATE_MEDIA_PLAYER_STATE, isNewState);
        if (state == InformationState.ERROR && this.errorMessage != null) {
            bundle.putString(BusProtocol.PREF_MEDIA_PLAYER_ERROR_MESSAGE, this.errorMessage);
        }
        bundle.putString("playlist_key", this.playlistKey);
        bundle.putInt("playlist_track_position", getShuffleAwarePosition());
        BusEvent busEvent = new BusEvent(null, bundle);
        lastState = busEvent;
        return busEvent;
    }

    private int getShuffleAwarePosition() {
        CursorPlayList playList = getPlayList();
        if (playList == null) {
            return -1;
        }
        if (tracksToPlay != null && playList.isShuffle()) {
            long id = playList.getTrack().id;
            for (int pos = 0; pos < tracksToPlay.length; pos++) {
                if (tracksToPlay[pos].id == id) {
                    return pos;
                }
            }
        }
        return playList.getPosition();
    }

    private Track getNeighborTrack(boolean forward) {
        Track track = null;
        if (!(this.playList == null || this.currentTrack == null)) {
            CursorPlayList playList = getPlayList();
            if (playList != null) {
                track = null;
                if (forward) {
                    if (playList.moveToNext()) {
                        track = playList.getTrack();
                    }
                    playList.moveToPrev();
                } else {
                    if (playList.moveToPrev()) {
                        track = playList.getTrack();
                    }
                    playList.moveToNext();
                }
            }
        }
        return track;
    }

    public IBinder onBind(Intent intent) {
        return this.localBinder;
    }

    public boolean isShufflePlayList() {
        return this.playList == null ? false : this.playList.isShuffle();
    }

    public void onCreate() {
        super.onCreate();
        initService();
    }

    public void onDestroy() {
        unRegisterReceiver();
        clear();
        super.onDestroy();
    }

    void tryToGetAudioFocus() {
        if (this.mAudioFocus != AudioFocus.Focused && this.mAudioFocusHelper != null && this.mAudioFocusHelper.requestFocus()) {
            this.mAudioFocus = AudioFocus.Focused;
        }
    }

    public void onGainedAudioFocus() {
    }

    public void onLostAudioFocus(boolean canDuck) {
    }

    private void updateRemoteControls(Track track, boolean isPlaying) {
        if (track == null) {
            hideNotification(true);
            return;
        }
        if (this.currentTrackInfo != null && PlayerImageView.isStubImageUrl(this.currentTrackInfo.info.imageUrl)) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), 2130838690);
        } else if (!(this.currentTrackInfo == null || this.currentTrackInfo.info == null || TextUtils.isEmpty(this.currentTrackInfo.info.imageUrl))) {
            FrescoOdkl.getBitmapOnUiThread(ImageRequest.fromUri(this.currentTrackInfo.info.imageUrl), new C04122(track, isPlaying));
        }
        updateWidgets(track, isPlaying);
    }

    private void showNotification(Track track, boolean isPlaying, Bitmap bitmap) {
        startForeground(155, MusicNotificationHelper.createNotification(this, track, isPlaying, bitmap));
    }

    private void updateLockScreenControl(Track track, Bitmap bitmap, int state) {
        tryToGetAudioFocus();
        if (this.mRemoteControlClientCompat == null) {
            Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
            intent.setComponent(this.mMediaButtonReceiverComponent);
            this.mRemoteControlClientCompat = new RemoteControlClientCompat(PendingIntent.getBroadcast(this, 0, intent, 0));
            RemoteControlHelper.registerRemoteControlClient(this.mAudioManager, this.mRemoteControlClientCompat);
        }
        this.mRemoteControlClientCompat.setPlaybackState(state);
        this.mRemoteControlClientCompat.setTransportControlFlags(181);
        String artistName = track.artist == null ? "" : track.artist.name;
        String albumName = track.album == null ? "" : track.album.name;
        MetadataEditorCompat editorCompat = this.mRemoteControlClientCompat.editMetadata(true);
        editorCompat.putString(2, artistName).putString(1, albumName).putString(7, track.name).putLong(9, (long) track.duration);
        if (!(bitmap == null || bitmap.isRecycled())) {
            editorCompat.putBitmap(100, bitmap.copy(bitmap.getConfig(), true));
        }
        editorCompat.apply();
    }

    private void hideLockScreenControl() {
        if (VERSION.SDK_INT >= 14 && this.mRemoteControlClientCompat != null) {
            this.mRemoteControlClientCompat.setPlaybackState(1);
        }
    }

    private void updateWidgets(Track track, boolean isPlaying) {
        if (this.currentTrackInfo != null) {
            FrescoOdkl.getBitmapOnUiThread(ImageRequest.fromUri(this.currentTrackInfo.info.imageUrl), new C04133(track, isPlaying));
        }
    }

    private void updateCurrentTrackBitmap(boolean isPlaying) {
        if (this.currentTrackInfo != null && this.currentTrack != null) {
            long currentTrackId = this.currentTrack.id;
            ImageRequest request = ImageRequest.fromUri(this.currentTrackInfo.info.imageUrl);
            if (request != null) {
                Fresco.getImagePipeline().fetchDecodedImage(request, null).subscribe(new C04154(currentTrackId, isPlaying), ThreadUtil.getSingleThreadExecutor());
            }
        }
    }

    private void hideNotification(boolean pauseWidgets) {
        stopForeground(true);
        updateWidgetForStop(pauseWidgets);
    }

    private void updateWidgetForStop(boolean pauseWidgets) {
        if (pauseWidgets && this.currentTrackInfo != null) {
            FrescoOdkl.getBitmapOnUiThread(ImageRequest.fromUri(this.currentTrackInfo.info.imageUrl), new C04165());
        }
    }

    public boolean pause() {
        if (!getMediaPlayer().isPlaying()) {
            return true;
        }
        getMediaPlayer().setAutoPlay(false);
        return getMediaPlayer().pause();
    }

    public boolean play() {
        if (getMediaPlayer().isPlaying()) {
            return true;
        }
        if (getInformationState() == InformationState.DATA_QUERY) {
            tryGetPlayInfo();
            return true;
        }
        getMediaPlayer().setAutoPlay(true);
        return getMediaPlayer().play();
    }

    public boolean isPlaying() {
        return getMediaPlayer().isPlaying();
    }

    private void clearData() {
        this.currentTrackInfo = null;
        this.currentTrack = null;
    }

    public void repeat() {
        int valueRepeat = 0;
        if (this.repeat == RepeatState.repeat) {
            this.repeat = RepeatState.repeatOne;
            valueRepeat = 2;
        } else if (this.repeat == RepeatState.repeatOne) {
            this.repeat = RepeatState.none;
            valueRepeat = 0;
        } else if (this.repeat == RepeatState.none) {
            this.repeat = RepeatState.repeat;
            valueRepeat = 1;
        }
        Intent intentRepeat = new Intent("set_repeat");
        intentRepeat.putExtra("play_repeat_update", valueRepeat);
        sendBroadcast(intentRepeat);
    }

    private boolean moveToPrev() {
        if (getPlayList().isFirstTrack()) {
            getPlayList().moveToLast();
            return true;
        } else if (getPlayList().moveToPrev()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean moveToNext() {
        if (getPlayList().isLastTrack() || getPlayList().isAfterLastTrack()) {
            getPlayList().moveToFirst();
            return true;
        } else if (getPlayList().moveToNext()) {
            return true;
        } else {
            return false;
        }
    }

    public void prev() {
        new PrevTask().execute(new Void[0]);
    }

    public void next() {
        new NextTask().execute(new Void[0]);
    }

    private void prevBlocking() {
        new PrevTask().executeOnCurrentThread();
    }

    private void nextBlocking() {
        new NextTask().executeOnCurrentThread();
    }

    public void startPlay() {
        CursorPlayList playlist = getPlayList();
        Track track = playlist.getTrack();
        if (track != null) {
            startPlay(track, playlist.isFirstTrack(), playlist.isLastTrack());
        }
    }

    private void startPlay(Track track, boolean isFirst, boolean isLast) {
        BufferedPlayInfo nextTrackInfo = this.nextDownloadTask.getNextBufferedTrack(track);
        clearData();
        getMediaPlayer().interrupt();
        BufferedPlayInfo info = null;
        if (nextTrackInfo != null && track.id == nextTrackInfo.info.trackId) {
            info = nextTrackInfo;
        } else if (MusicAsyncFileCache.getInstance().isKeyContains(MusicBaseFileCache.buildFileName(track.id))) {
            PlayTrackInfo playInfo = MusicAsyncFileCache.getInstance().getPlayInfo(MusicBaseFileCache.buildFileName(track.id));
            if (playInfo != null) {
                info = BufferedPlayInfo.create(playInfo);
            }
        }
        if (info != null) {
            stream(info, track, isFirst, isLast);
        } else {
            tryGetPlayInfo(track.id);
        }
    }

    public void registerReceiver() {
        getBaseContext().registerReceiver(this.endCallReceiver, new IntentFilter("ru.odnoklassniki.android.videochat.END_CALL"));
        getBaseContext().registerReceiver(this.endCallReceiver, new IntentFilter("ru.odnoklassniki.android.videochat.STOP_CALL"));
        registerReceiver(this.mHeadsetReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
        registerReceiver(this.mMediaButtonIntentReceiver, new IntentFilter("android.intent.action.MEDIA_BUTTON"));
        registerReceiver(this.connectionReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        MediaButtonHelper.registerMediaButtonEventReceiverCompat(this.mAudioManager, this.mMediaButtonReceiverComponent);
    }

    private void unRegisterReceiver() {
        if (this.endCallReceiver != null) {
            getBaseContext().unregisterReceiver(this.endCallReceiver);
        }
        if (this.mMediaButtonIntentReceiver != null) {
            getBaseContext().unregisterReceiver(this.mMediaButtonIntentReceiver);
        }
        if (this.mHeadsetReceiver != null) {
            getBaseContext().unregisterReceiver(this.mHeadsetReceiver);
        }
        if (this.connectionReceiver != null) {
            getBaseContext().unregisterReceiver(this.connectionReceiver);
        }
        if (this.playlistChangedObserver != null) {
            getContentResolver().unregisterContentObserver(this.playlistChangedObserver);
        }
        MediaButtonHelper.unregisterMediaButtonEventReceiverCompat(this.mAudioManager, this.mMediaButtonReceiverComponent);
    }

    private void stream(BufferedPlayInfo info, Track track, boolean isFirst, boolean isLast) {
        if (info == null || track == null) {
            onError(new Exception("No valid track"), true);
            return;
        }
        this.currentTrack = track;
        this.currentTrackInfo = info;
        this.mStreamMediaPlayer.setAutoPlay(true);
        streamDelay(info, 500);
    }

    private void streamDelay(BufferedPlayInfo bufferedTrackInfo, int delay) {
        this.streamPlayHandler.removeMessages(0);
        if (delay > 0) {
            Message msg = Message.obtain();
            msg.obj = bufferedTrackInfo;
            this.streamPlayHandler.sendMessageDelayed(msg, (long) delay);
            return;
        }
        startStreamTrack(bufferedTrackInfo);
    }

    private void startStreamTrack(BufferedPlayInfo bufferedTrackInfo) {
        try {
            this.mStreamMediaPlayer.setAutoPlay(true);
            this.mStreamMediaPlayer.streamingMusic(bufferedTrackInfo.info.getMp3ContentUrl(), bufferedTrackInfo.buffer, bufferedTrackInfo.info.size, bufferedTrackInfo.info.duration);
            StatisticManager.getInstance().addStatisticEvent("music_play_start", new Pair[0]);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), LocalizationManager.getString(getBaseContext(), 2131165830), 0).show();
        }
    }

    @Deprecated
    public static Intent newPlayIntent(Context context, int playPosition, Track[] tracks, MusicListType type, boolean clearOnlyCache, boolean seekToStart) {
        ArrayList arrayList = new ArrayList();
        for (Track track : tracks) {
            arrayList.add(track);
        }
        return newPlayIntent(context, playPosition, arrayList, type, clearOnlyCache, seekToStart);
    }

    private static Intent newPlayIntent(Context context, int playPosition, ArrayList<Track> arrayList, MusicListType type, boolean clearOnlyCache, boolean seekToStart) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.PLAY_START");
        Bundle data = new Bundle();
        data.putSerializable("playlist_type", type);
        data.putInt("play_position", playPosition);
        data.putBoolean("clear_only_cache", clearOnlyCache);
        data.putBoolean("seek_to_start", seekToStart);
        intent.putExtras(data);
        return intent;
    }

    public static void startPlayMusic(Context context, int position, ArrayList<Track> tracks, MusicListType type) {
        startPlayMusic(context, position, tracks, type, null);
    }

    public static void startPlayMusic(Context context, int position, ArrayList<Track> tracks, MusicListType type, @Nullable String playlistId) {
        Intent intent = newPlayIntent(context, position, (ArrayList) tracks, type, false, false);
        if (type != MusicListType.PLAYLIST) {
            tracksToPlay = (Track[]) tracks.toArray(new Track[tracks.size()]);
        }
        if (!TextUtils.isEmpty(playlistId)) {
            intent.putExtra("playlist_id", playlistId);
        }
        CursorPlayList.saveType(context, type);
        Settings.setPlayOnlyCache(context, false);
        context.startService(intent);
    }

    public static Intent getNextIntent(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.PLAY_NEXT");
        return intent;
    }

    public static Intent getPrevIntent(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.PLAY_PREV");
        return intent;
    }

    public static Intent getTogglePlayIntent(Context context, boolean hideNotification) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.TOGGLE_PLAY");
        Bundle data = new Bundle();
        data.putBoolean("ru.ok.android.music.HIDE_NOTIFICATION", hideNotification);
        intent.putExtras(data);
        return intent;
    }

    public static Intent getTogglePlayIntent(Context context) {
        return getTogglePlayIntent(context, true);
    }

    public static Intent getSeekIntent(Context context, int position) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.SEEK");
        Bundle data = new Bundle();
        data.putInt("ru.ok.android.music.SEEK_POSITION", position);
        intent.putExtras(data);
        return intent;
    }

    public static Intent getPlayIntent(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.PLAY");
        return intent;
    }

    public static Intent getStopIntent(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.STOP");
        return intent;
    }

    public static Intent getShuffleIntent(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.SHUFFLE");
        return intent;
    }

    public static Intent getRepeatIntent(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.REPEAT");
        return intent;
    }

    public static Intent getStateStreamMediaPlayerIntent(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.STATE_STREAM_MEDIA_PLAYER");
        return intent;
    }

    public static Intent getStateIntent(Context context) {
        return getStateIntent(context, null);
    }

    public static Intent getHideNotificationIntent(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.NOTIFICATION_HIDE");
        return intent;
    }

    public static Intent getStateIntent(Context context, ResultReceiver receiver) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction("ru.ok.android.music.STATE");
        if (receiver != null) {
            intent.putExtra("ru.ok.android.music.RESULT", receiver);
        }
        return intent;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals("ru.ok.android.music.PLAY_START")) {
                    startCommand(intent);
                } else if (action.equals("ru.ok.android.music.PLAY")) {
                    playCommand();
                } else if (action.equals("ru.ok.android.music.PAUSE")) {
                    pauseCommand();
                } else if (action.equals("ru.ok.android.music.STOP")) {
                    stopCommand();
                } else if (action.equals("ru.ok.android.music.FINISH")) {
                    finishCommand();
                } else if (action.equals("ru.ok.android.music.PLAY_NEXT")) {
                    moveCommand(true);
                } else if (action.equals("ru.ok.android.music.PLAY_PREV")) {
                    moveCommand(false);
                } else if (action.equals("ru.ok.android.music.TOGGLE_PLAY")) {
                    pausePlayCommand(intent);
                } else if (action.equals("ru.ok.android.music.REPEAT")) {
                    repeatCommand();
                } else if (action.equals("ru.ok.android.music.SHUFFLE")) {
                    shuffleCommand();
                } else if (action.equals("ru.ok.android.music.NOTIFICATION_HIDE")) {
                    hideNotificationCommand();
                } else if (action.equals("ru.ok.android.music.SEEK")) {
                    seekCommand(intent);
                } else if (action.equals("ru.ok.android.music.UPDATE_WIDGET")) {
                    updateWidgetCommand(intent);
                } else if (action.equals("ru.ok.android.music.STATE")) {
                    stateCommand(intent);
                } else if (action.equals("ru.ok.android.music.STATE_STREAM_MEDIA_PLAYER")) {
                    sendStatus();
                }
                logCommand(intent);
            }
        }
        return 2;
    }

    private void finishCommand() {
        clear();
        stopSelf();
    }

    private void logCommand(Intent intent) {
        String logEvent = intent.getStringExtra("ru.ok.android.music.LOG_EVENT");
        if (!TextUtils.isEmpty(logEvent)) {
            StatisticManager.getInstance().addStatisticEvent(logEvent, new Pair[0]);
        }
    }

    private void seekCommand(Intent intent) {
        getMediaPlayer().seekTo(intent.getIntExtra("ru.ok.android.music.SEEK_POSITION", 0));
        if (!getMediaPlayer().isDownloadComplete()) {
            this.nextDownloadTask.pause();
        }
        sendBroadcast(new Intent("set_seek_finish"));
    }

    private void shuffleCommand() {
        try {
            getPlayList().setShuffle(!getPlayList().isShuffle());
            Intent intentRequest = new Intent("set_shuffle");
            intentRequest.putExtra("play_shuffle_update", getPlayList().isShuffle());
            sendBroadcast(intentRequest);
        } catch (CursorIsNullException e) {
            onError(e, true);
        }
    }

    private void repeatCommand() {
        repeat();
    }

    private void pausePlayCommand(Intent intent) {
        showOdklWithMusic();
        if (this.mStreamMediaPlayer != null) {
            if (getMediaPlayer().isPlaying()) {
                pauseCommand(intent);
                if (intent.getBooleanExtra("ru.ok.android.STOP_FOREGRAUND", false)) {
                    stopForeground(true);
                }
            } else {
                playCommand();
            }
            if (intent.getBooleanExtra("ru.ok.android.STOP_FOREGRAUND", false)) {
                updateWidgets(getPlayList().getTrack(), getMediaPlayer().isPlaying());
            }
        }
    }

    private void hideNotificationCommand() {
        pause();
        hideNotification(true);
    }

    private void pauseCommand(Intent intent) {
        showOdklWithMusic();
        if (this.mStreamMediaPlayer != null) {
            boolean isHideNotification = intent.getBooleanExtra("ru.ok.android.music.HIDE_NOTIFICATION", false);
            if (pause() && isHideNotification) {
                hideNotification(true);
            }
        }
    }

    private void moveCommand(boolean next) {
        getMediaPlayer().stop(false);
        if (next) {
            next();
        } else {
            prev();
        }
        showOdklWithMusic();
    }

    private void moveCommandBlocking(boolean next) {
        getMediaPlayer().stop(false);
        if (next) {
            nextBlocking();
        } else {
            prevBlocking();
        }
        showOdklWithMusic();
    }

    private void stopCommand() {
        showOdklWithMusic();
        getMediaPlayer().stop(true);
        updateRemoteControls(getPlayList().getTrack(), false);
        stopForeground(true);
    }

    private void clear() {
        pause();
        hideNotification(true);
        hideLockScreenControl();
        getMediaPlayer().release();
    }

    private void updateWidgetCommand(Intent intent) {
        if (this.currentTrackInfo != null) {
            FrescoOdkl.getBitmapOnUiThread(ImageRequest.fromUri(this.currentTrackInfo.info.imageUrl), new C04176(intent));
        }
    }

    private void startCommand(Intent intent) {
        MusicListType type = (MusicListType) intent.getExtras().getSerializable("playlist_type");
        if (type != null) {
            int playPosition = intent.getIntExtra("play_position", 0);
            if (tracksToPlay != null) {
                if (tracksToPlay.length >= playPosition) {
                    this.currentTrack = tracksToPlay[playPosition];
                } else {
                    return;
                }
            }
            if (type != MusicListType.PLAYLIST) {
                this.playlistKey = MusicPlayerUtils.buildPlaylistKey(type, intent.getStringExtra("playlist_id"));
            }
            setPlayState(InformationState.START);
            if (createTempPlaylist(type, tracksToPlay, playPosition)) {
                savePlaylistToDb();
                if (!(this.playList.isShuffle() ? this.playList.moveToTrack(this.currentTrack.id) : this.playList.moveToPosition(playPosition))) {
                    return;
                }
                if (this.currentTrackInfo == null || this.currentTrack.id != this.currentTrackInfo.info.trackId) {
                    sendShuffleIntent();
                    startPlay();
                } else if (intent.getBooleanExtra("seek_to_start", false) || !getMediaPlayer().isPause()) {
                    getMediaPlayer().seekTo(0, true);
                } else {
                    getMediaPlayer().play();
                }
            }
        }
    }

    private boolean createTempPlaylist(MusicListType type, Track[] tracks, int playPosition) {
        try {
            if (this.playList != null && type != MusicListType.PLAYLIST) {
                CursorPlayList oldPlayList = this.playList;
                this.playList = new CursorPlayList(getBaseContext(), tracks, playPosition);
                closePlaylist(oldPlayList);
                return true;
            } else if (this.playList != null) {
                return true;
            } else {
                this.playList = new CursorPlayList(getBaseContext(), tracks, playPosition);
                return true;
            }
        } catch (CursorIsNullException e) {
            onError(e, true);
            return false;
        }
    }

    private void savePlaylistToDb() {
        if (this.playlistChangedObserver != null) {
            getContentResolver().unregisterContentObserver(this.playlistChangedObserver);
        }
        if (tracksToPlay != null) {
            int token = CursorPlayList.generateToken();
            this.playlistChangedObserver = new PlaylistChangedObserver();
            getContentResolver().registerContentObserver(ContentUris.withAppendedId(OdklProvider.playListUri(), (long) token), true, this.playlistChangedObserver);
            AsyncStorageOperations.savePlaylist(getBaseContext(), tracksToPlay, token);
        }
    }

    private void sendShuffleIntent() {
        Intent intentSh = new Intent("set_shuffle");
        intentSh.putExtra("play_shuffle_update", isShufflePlayList());
        sendBroadcast(intentSh);
    }

    private void closePlaylist(CursorPlayList playList) {
        if (playList != null) {
            playList.close();
        }
    }

    private void stateCommand(Intent intentIn) {
        boolean z;
        Intent result = new Intent("ru.odnoklassniki.android.music.play.state");
        result.putExtra("playState", getState());
        result.putExtra("downloadState", this.mStreamMediaPlayer == null ? 0 : this.mStreamMediaPlayer.getDataLoadProgress());
        String str = "shuffleState";
        if (this.playList == null) {
            z = false;
        } else {
            z = this.playList.isShuffle();
        }
        result.putExtra(str, z);
        result.putExtra("repeatState", this.repeat);
        Track trackParcelable = null;
        PlayTrackInfo playTrackInfoParcelable = null;
        if (!(this.playList == null || getState() == MusicState.UNKNOWN)) {
            Track currentTrack = this.playList.getTrack();
            if (currentTrack != null) {
                trackParcelable = currentTrack;
            }
        }
        if (this.currentTrackInfo != null) {
            playTrackInfoParcelable = this.currentTrackInfo.info;
        }
        result.putExtra("music_info", new MusicInfoContainer(trackParcelable, playTrackInfoParcelable, getNeighborTrack(false), getNeighborTrack(true), isShufflePlayList()));
        result.putExtra("play_progress_update_sec", this.mStreamMediaPlayer == null ? 0 : this.mStreamMediaPlayer.getProgressValue());
        str = "play_track_duration";
        int i = (getState() == MusicState.UNKNOWN || getPlayList().getTrack() == null) ? 0 : this.playList.getTrack().duration;
        result.putExtra(str, i);
        result.putExtra("playlist_key", this.playlistKey);
        result.putExtra("playlist_track_position", getShuffleAwarePosition());
        if (intentIn.hasExtra("ru.ok.android.music.RESULT")) {
            ((ResultReceiver) intentIn.getParcelableExtra("ru.ok.android.music.RESULT")).send(0, result.getExtras());
        }
        sendBroadcast(result);
    }

    public void sendBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }

    private MusicState getState() {
        if (this.mStreamMediaPlayer == null) {
            return MusicState.UNKNOWN;
        }
        if (this.mStreamMediaPlayer.isPlaying()) {
            return MusicState.PLAYING;
        }
        if (this.mStreamMediaPlayer.isPause()) {
            return MusicState.PAUSING;
        }
        return MusicState.UNKNOWN;
    }

    private void playCommand() {
        if (getInformationState() == InformationState.ERROR) {
            sendStatus();
        } else if (getInformationState() != InformationState.STOP || this.currentTrack == null || this.currentTrackInfo == null) {
            play();
        } else {
            streamDelay(this.currentTrackInfo, 0);
        }
    }

    public void pauseCommand() {
        pause();
    }

    private void showOdklWithMusic() {
        if (this.playList == null) {
            Intent intent = NavigationHelper.createIntentForTag(this, Tag.music);
            intent.addFlags(268435456);
            startActivity(intent);
        }
    }

    private void initService() {
        this.mAudioManager = (AudioManager) getSystemService("audio");
        if (VERSION.SDK_INT >= 8) {
            this.mAudioFocusHelper = new AudioFocusHelper(getApplicationContext(), this);
        } else {
            this.mAudioFocus = AudioFocus.Focused;
        }
        this.myPhoneListener = new MusicPhoneStateListener(this);
        ((TelephonyManager) getSystemService("phone")).listen(this.myPhoneListener, 32);
        this.mHeadsetReceiver = new HeadsetIntentReceiver(this);
        this.mMediaButtonIntentReceiver = new MediaButtonIntentReceiver(this);
        this.endCallReceiver = new VideoChatBroadcastReceiver(this);
        this.connectionReceiver = new ConnectionReceiver(this);
        this.mMediaButtonReceiverComponent = new ComponentName(getApplicationContext(), MusicIntentReceiver.class);
        registerReceiver();
    }

    public StreamMediaPlayer getMediaPlayer() {
        Logger.m172d("init player");
        if (this.mStreamMediaPlayer == null) {
            this.mStreamMediaPlayer = new StreamMediaPlayer(this, this);
        }
        return this.mStreamMediaPlayer;
    }

    private void tryGetPlayInfo(long trackId) {
        if (trackId == 0) {
            Track track;
            CursorPlayList playList = getPlayList();
            if (playList != null) {
                track = playList.getTrack();
            } else {
                track = null;
            }
            if (track != null) {
                trackId = track.id;
            } else {
                trackId = 0;
            }
            this.currentTrack = track;
        }
        setPlayState(InformationState.DATA_QUERY, true);
        Message msg = Message.obtain(null, 2131624061, 0, 0);
        msg.replyTo = this.mApiMessenger;
        Bundle bundleSend = new Bundle();
        bundleSend.putLong("tid", trackId);
        msg.setData(bundleSend);
        GlobalBus.sendMessage(msg);
    }

    private void tryGetPlayInfo() {
        tryGetPlayInfo(0);
    }

    public void onProgress(int sec) {
        if (getPlayList().getTrack() != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(BusProtocol.PREF_MEDIA_PLAYER_PROGRESS, sec);
            bundle.putInt(BusProtocol.PREF_MEDIA_PLAYER_DURATION, getPlayList().getTrack().duration);
            GlobalBus.send(2131624251, new BusEvent(null, bundle));
        }
        if (sec == 30 && !this.isPlay30Send) {
            this.isPlay30Send = true;
            sendPlay30(getPlayList().getTrack());
        }
    }

    private void sendPlay30(Track track) {
        Message msg = Message.obtain(null, 2131624080, 0, 0);
        msg.replyTo = this.mApiMessenger;
        msg.obj = track;
        GlobalBus.sendMessage(msg);
    }

    public void onDownLoadProgress(int value) {
        if (value != this.downloadValue) {
            this.downloadValue = value;
            Bundle bundle = new Bundle();
            bundle.putInt("play_progress_update", value);
            GlobalBus.send(2131624250, new BusEvent(null, bundle));
        }
    }

    public void onDownloadFinish(String url, long offset) {
        onDownLoadProgress(100);
        if (isCachedMusic() && isCacheNextMusic() && !Settings.isPlayOnlyCache(getApplicationContext())) {
            getNextTrackInfo();
        }
    }

    public void onSaveInBuffer(String url, File bufferedFile) {
        String mp3ContentUrl = null;
        if (bufferedFile != null) {
            try {
                if (isCachedMusic()) {
                    BufferedPlayInfo currentTrackInfo = this.currentTrackInfo;
                    PlayTrackInfo info = currentTrackInfo == null ? null : currentTrackInfo.info;
                    if (info != null) {
                        mp3ContentUrl = info.getMp3ContentUrl();
                    }
                    if (TextUtils.equals(mp3ContentUrl, url)) {
                        cacheData(info, bufferedFile);
                    }
                }
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }
    }

    public void onDownloadError(long errorPosition, int code) {
        if (errorPosition == 0) {
            if (code == 2) {
                this.errorMessage = LocalizationManager.getString((Context) this, 2131166237);
            }
            if (code == 3) {
                this.errorMessage = LocalizationManager.getString((Context) this, 2131166236);
                Toast.makeText(getBaseContext(), this.errorMessage, 1).show();
            }
            setPlayState(InformationState.ERROR);
            hideNotification(true);
            hideLockScreenControl();
        }
    }

    public void notifyConnectionAvailable() {
        getMediaPlayer().notifyDownload();
    }

    private void cacheData(PlayTrackInfo info, File bufferedDataFile) {
        try {
            MusicAsyncFileCache.getInstance().cacheData(info, new BufferedInputStream(new FileInputStream(bufferedDataFile)), new C04187());
        } catch (FileNotFoundException e) {
            Logger.m176e("Music file not found");
        }
    }

    private void getNextTrackInfo() {
        long j = 0;
        if (this.playList.moveToNext()) {
            Message msg = Message.obtain(null, 2131624061, 0, 0);
            msg.replyTo = this.mApiMessengerNext;
            Bundle bundleSend = new Bundle();
            String str = "tid";
            if (!(getPlayList() == null || getPlayList().getTrack() == null)) {
                j = getPlayList().getTrack().id;
            }
            bundleSend.putLong(str, j);
            msg.setData(bundleSend);
            GlobalBus.sendMessage(msg);
        }
        this.playList.moveToPrev();
    }

    private boolean isCachedMusic() {
        switch (PreferenceManager.getDefaultSharedPreferences(this).getInt(getString(2131165457), 0)) {
            case RECEIVED_VALUE:
                return true;
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                if (CursorPlayList.getType(getApplicationContext()) != MusicListType.MY_MUSIC) {
                    return false;
                }
                return true;
            case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                return false;
            default:
                return false;
        }
    }

    private boolean isCacheNextMusic() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(2131165455), true);
    }

    public void onStart() {
        this.isPlay30Send = false;
        if (this.currentTrackInfo != null) {
            broadcastIntentPlayStart(this.currentTrackInfo.info, this.currentTrack);
        }
        this.playList.saveCurrentTrackId();
    }

    private CursorPlayList getPlayList() {
        if (this.playList == null) {
            try {
                this.playList = new CursorPlayList(getApplicationContext());
                this.playList.moveToFirst();
            } catch (CursorIsNullException e) {
                onError(e, true);
            }
        }
        return this.playList;
    }

    public void broadcastIntentPlayStart(PlayTrackInfo playInfo, Track track) {
        Intent intent = new Intent("start_play_track");
        intent.putExtra("music_info", new MusicInfoContainer(track, playInfo, getNeighborTrack(false), getNeighborTrack(true), getPlayList().isShuffle()));
        intent.putExtra("mp3_position", getShuffleAwarePosition());
        sendBroadcast(intent);
    }

    public void onBuffering() {
        setPlayState(InformationState.BUFFERED);
        sendBroadcast(new Intent("buffering_play_track"));
    }

    public void onPause() {
        setPlayState(InformationState.PAUSE);
        sendBroadcast(new Intent("pause_play_track"));
        CursorPlayList pl = getPlayList();
        if (pl != null && pl.getTrack() != null) {
            updateRemoteControls(pl.getTrack(), false);
        }
    }

    public void onPlay() {
        setPlayState(InformationState.PLAY);
        if (this.playList != null) {
            CursorPlayList pl = getPlayList();
            if (pl != null && pl.getTrack() != null) {
                Track track = pl.getTrack();
                Intent intent = new Intent("play_track");
                intent.putExtra("music_info", new MusicInfoContainer(track, null, getNeighborTrack(false), getNeighborTrack(true), isShufflePlayList()));
                sendBroadcast(intent);
                updateRemoteControls(track, true);
            }
        }
    }

    public void onStop() {
        setPlayState(InformationState.STOP, false);
    }

    public void onError(Exception e, boolean isVisibleErrorForUser) {
        if (isVisibleErrorForUser) {
            setPlayState(InformationState.ERROR, false);
            hideNotification(true);
            hideLockScreenControl();
        }
        logErrorStatistic(e);
    }

    private void logErrorStatistic(Exception e) {
        Pair<String, String>[] params;
        if (e == null || TextUtils.isEmpty(e.getMessage())) {
            params = null;
        } else {
            params = new Pair[]{new Pair(Stanza.TEXT, e.getMessage())};
            Logger.m173d("on Music Error: %s", e.getMessage());
        }
        StatisticManager.getInstance().addStatisticEvent("music-error", params);
    }

    private void sendFinishPlayTrack() {
        sendBroadcast(new Intent("finish_play_track"));
    }

    private void onRepeat() {
        Track track = getPlayList().getTrack();
        MusicInfoContainer musicInfoContainer = new MusicInfoContainer(track, null, getNeighborTrack(false), getNeighborTrack(true), isShufflePlayList());
        if (track != null) {
            Intent intentRepeat = new Intent("repeat_play_track");
            intentRepeat.putExtra("music_info", musicInfoContainer);
            sendBroadcast(intentRepeat);
        }
        updateRemoteControls(track, true);
    }

    public void onPlayEnd() {
        switch (AnonymousClass10.$SwitchMap$ru$ok$android$utils$controls$PlayerSetter$RepeatState[this.repeat.ordinal()]) {
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                getMediaPlayer().repeat();
                onRepeat();
            case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                hideLockScreenControl();
                sendFinishPlayTrack();
                next();
            default:
                hideLockScreenControl();
                sendFinishPlayTrack();
                if (getPlayList().isLastTrack()) {
                    hideNotification(false);
                } else {
                    new C04209().execute(new Void[0]);
                }
        }
    }

    public void onGetPlayInfo(PlayTrackInfo info) {
        stream(BufferedPlayInfo.create(info), getPlayList().getTrack(), getPlayList().isFirstTrack(), getPlayList().isLastTrack());
    }

    public void onGetPlayInfoError(Object errorObject) {
        hideNotification(true);
        if (errorObject instanceof ServerReturnErrorException) {
            ServerReturnErrorException ex = (ServerReturnErrorException) errorObject;
            if (ex.getErrorCode() == C0206R.styleable.Theme_radioButtonStyle || ex.getMessage().equals("error.copyright.restriction")) {
                sendError(C0206R.styleable.Theme_radioButtonStyle);
            } else {
                sendError(C0206R.styleable.Theme_editTextStyle);
            }
        } else if (errorObject instanceof NoConnectionException) {
            sendError(404);
            TimeToast.show(getBaseContext(), 2131166735, 0);
        } else if (errorObject instanceof ServerNotFoundException) {
            sendError(404);
            TimeToast.show(getBaseContext(), 2131166539, 0);
        } else if (errorObject instanceof NetworkException) {
            sendError(404);
            TimeToast.show(getBaseContext(), 2131165836, 0);
        } else {
            sendError(404);
        }
    }

    private void sendError(int error) {
        MusicInfoContainer musicInfoContainer;
        Bundle bundle = new Bundle();
        if (this.currentTrackInfo == null && this.currentTrack == null) {
            musicInfoContainer = null;
        } else {
            musicInfoContainer = new MusicInfoContainer(this.currentTrack, this.currentTrackInfo != null ? this.currentTrackInfo.info : null, getNeighborTrack(false), getNeighborTrack(true), isShufflePlayList());
        }
        bundle.putParcelable(BusProtocol.PREF_MEDIA_PLAYER_STATE_MUSIC_INFO_CONTAINER, musicInfoContainer);
        bundle.putInt(BusProtocol.PREF_PLAY_INFO_ERROR_KEY, error);
        GlobalBus.send(2131624243, new BusEvent(null, bundle));
    }
}
