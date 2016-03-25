package ru.ok.android.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.fragments.web.shortlinks.ShortLink;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.app.MusicService.LocalBinder;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.adapters.music.EndlessTrackAdapter;
import ru.ok.android.ui.custom.player.PlayPauseView;
import ru.ok.android.ui.custom.player.PlayPauseView.OnPlayPauseCheckedChangedListener;
import ru.ok.android.ui.dialogs.ErrorMusicDialog;
import ru.ok.android.ui.dialogs.ErrorMusicDialog.OnNextTrackListener;
import ru.ok.android.ui.dialogs.SelectAlbumArtistBase.OnSelectAlbumArtistListener;
import ru.ok.android.ui.dialogs.actions.SelectAlbumArtistActionBox;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.tabbar.manager.BaseTabbarManager;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.animation.ParallaxPageTransformer;
import ru.ok.android.utils.animation.PlayerAnimationHelper;
import ru.ok.android.utils.animation.SyncBus.MessageCallback;
import ru.ok.android.utils.controls.PlayerControl;
import ru.ok.android.utils.controls.PlayerSetter;
import ru.ok.android.utils.controls.PlayerSetter.RepeatState;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.music.MusicPlayerUtils;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.wmf.PlayTrackInfo;
import ru.ok.model.wmf.Track;

public class PlayerFragment extends BaseFragment implements OnClickListener, OnSeekBarChangeListener, OnPlayPauseCheckedChangedListener, MessageCallback, PlayerSetter {
    private EndlessTrackAdapter adapter;
    private TextView albumTextView;
    private boolean animationInProgress;
    private TextView artistTextView;
    private OnControlListener controlListener;
    private TextView endTimeTextView;
    private ErrorMusicDialog errorMusicDialog;
    private boolean isFirstStart;
    private boolean lastScrollForward;
    private TextView legalInfoTextView;
    private ViewGroup mMainView;
    private LocalBinder musicServiceBinder;
    private boolean needUpdate;
    private OnPageChangeListener onPageChangeListener;
    protected OnPlayControlListener playControlListener;
    protected PlayPauseView playPauseView;
    private PlayerControl playerControl;
    private SeekBar progress;
    private int progressSec;
    private ServiceConnection serviceConnection;
    private TextView startTimeTextView;
    private TextView trackTextView;
    private boolean tracking;
    private ViewPager viewPager;

    /* renamed from: ru.ok.android.ui.fragments.PlayerFragment.1 */
    class C08051 extends SimpleOnPageChangeListener {
        C08051() {
        }

        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            if (state == 0) {
                if (PlayerFragment.this.needUpdate) {
                    PlayerFragment.this.update();
                    PlayerFragment.this.needUpdate = false;
                }
                PlayerFragment.this.animationInProgress = false;
                return;
            }
            PlayerFragment.this.animationInProgress = true;
        }

        public void onPageSelected(int position) {
            int oldPosition = PlayerFragment.this.adapter.getCurrentPosition();
            PlayerFragment.this.adapter.setCurrentPosition(position);
            Logger.m173d("Position: %d. Adapter: %d", Integer.valueOf(position), Integer.valueOf(oldPosition));
            if (position > oldPosition) {
                PlayerFragment.this.onPageChanged(true);
            } else if (position < oldPosition) {
                PlayerFragment.this.onPageChanged(false);
            }
            Track track = PlayerFragment.this.adapter.getCurrentTrack();
            if (track != null) {
                PlayerFragment.this.setStaticTrackInfo(track);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.PlayerFragment.2 */
    class C08062 implements Runnable {
        C08062() {
        }

        public void run() {
            PlayerAnimationHelper.sendPlayerCreated();
            PlayerFragment.this.mMainView.requestFocus();
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.PlayerFragment.3 */
    class C08073 implements OnSelectAlbumArtistListener {
        C08073() {
        }

        public void onSelectAlbum() {
            if (PlayerFragment.this.playControlListener != null) {
                PlayerFragment.this.playControlListener.onSearchAlbumMusic();
            }
        }

        public void onSelectArtist() {
            if (PlayerFragment.this.playControlListener != null) {
                PlayerFragment.this.playControlListener.onSearchArtistMusic();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.PlayerFragment.4 */
    class C08084 implements ServiceConnection {
        C08084() {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerFragment.this.musicServiceBinder = (LocalBinder) service;
            PlayerFragment.this.restorePlayPauseState(PlayerFragment.this.playPauseView);
            PlayerFragment.this.callUpdate();
        }

        public void onServiceDisconnected(ComponentName name) {
            PlayerFragment.this.musicServiceBinder = null;
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.PlayerFragment.5 */
    class C08095 implements OnNextTrackListener {
        final /* synthetic */ Activity val$activity;

        C08095(Activity activity) {
            this.val$activity = activity;
        }

        public void onNextTrack() {
            if (this.val$activity != null) {
                PlayerFragment.this.errorMusicDialog = null;
                PlayerFragment.this.onPageChanged(PlayerFragment.this.lastScrollForward);
            }
        }
    }

    public interface OnControlListener {
        void onResume();

        void onStop();
    }

    public interface OnPlayControlListener {
        void onChangeTrackPosition(int i);

        void onChangeTrackSeekPosition(int i);

        void onPauseMusic();

        void onPlayMusic();

        void onRepeatUpdate();

        void onSearchAlbumMusic();

        void onSearchArtistMusic();

        void onSearchMusic();

        void onShuffleUpdate();

        void onStartSeek();
    }

    public PlayerFragment() {
        this.tracking = false;
        this.lastScrollForward = true;
        this.animationInProgress = false;
        this.needUpdate = false;
        this.onPageChangeListener = new C08051();
        this.progressSec = 0;
        this.serviceConnection = new C08084();
    }

    protected int getLayoutId() {
        return 2130903394;
    }

    public void updateActionBarState() {
        if (DeviceUtils.getType(OdnoklassnikiApplication.getContext()) == DeviceLayoutType.SMALL) {
            super.updateActionBarState();
        }
    }

    protected String getTitle() {
        return null;
    }

    private void requestPlayerState() {
        GlobalBus.send(2131624108, new BusEvent());
    }

    public void setPlay() {
        this.playPauseView.setPlay();
    }

    public void setPause() {
        this.playPauseView.setPause();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean z = true;
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            z = false;
        }
        this.isFirstStart = z;
        return inflateLayout(inflater, new FrameLayout(getActivity()));
    }

    private View inflateLayout(LayoutInflater inflater, ViewGroup container) {
        this.mMainView = (ViewGroup) inflater.inflate(2130903394, container);
        this.playPauseView = (PlayPauseView) this.mMainView.findViewById(2131625238);
        restorePlayPauseState(this.playPauseView);
        this.legalInfoTextView = (TextView) this.mMainView.findViewById(2131625241);
        this.startTimeTextView = (TextView) this.mMainView.findViewById(2131625232);
        this.endTimeTextView = (TextView) this.mMainView.findViewById(2131625233);
        this.progress = (SeekBar) this.mMainView.findViewById(2131624548);
        this.progress.bringToFront();
        if (DeviceUtils.isSmall(getContext())) {
            MusicPlayerUtils.setTouchDelegate(this.mMainView, this.progress, DimenUtils.getRealDisplayPixels(50, getContext()));
        }
        this.mMainView.findViewById(2131625239).setOnClickListener(this);
        this.mMainView.findViewById(2131625237).setOnClickListener(this);
        this.mMainView.findViewById(2131625236).setOnClickListener(this);
        this.mMainView.findViewById(2131625240).setOnClickListener(this);
        this.artistTextView = (TextView) this.mMainView.findViewById(2131625123);
        this.albumTextView = (TextView) this.mMainView.findViewById(2131624948);
        this.trackTextView = (TextView) this.mMainView.findViewById(2131625235);
        initEvents();
        this.playerControl = new PlayerControl(getActivity(), this);
        this.playerControl.registerBus();
        setOnControlListener(this.playerControl);
        this.viewPager = (ViewPager) this.mMainView.findViewById(2131625229);
        this.viewPager.setPageTransformer(true, new ParallaxPageTransformer());
        this.adapter = new EndlessTrackAdapter(getActivity());
        this.adapter.setOnClickListener(this);
        this.viewPager.setAdapter(this.adapter);
        this.viewPager.setOffscreenPageLimit(1);
        this.viewPager.setOnPageChangeListener(this.onPageChangeListener);
        this.viewPager.setCurrentItem(50000, false);
        ((BaseCompatToolbarActivity) getActivity()).getAppBarLayout().setBackgroundDrawable(getResources().getDrawable(2130837608));
        return this.mMainView;
    }

    private void restorePlayPauseState(PlayPauseView view) {
        if (this.musicServiceBinder != null && view != null && this.musicServiceBinder.isPlaying()) {
            view.setForcePlay();
        }
    }

    public void onPageChanged(boolean forward) {
        if (forward) {
            onNextTrack();
        } else {
            onPrevTrack();
        }
    }

    private void initEvents() {
        this.playPauseView.addOnPlayPauseCheckedChangedListener(this);
        this.progress.setOnSeekBarChangeListener(this);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflateMenuLocalized(2131689518, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Track track = this.musicServiceBinder != null ? this.musicServiceBinder.getCurrentTrack() : null;
        switch (item.getItemId()) {
            case 2131624717:
                if (track == null) {
                    return true;
                }
                this.playerControl.onSetStatusTrack(track);
                return true;
            case 2131625228:
                NavigationHelper.showPlayerPlayListPage(getActivity());
                return true;
            case 2131625454:
                if (track == null) {
                    return true;
                }
                ShortLink.createTrackLink(track.id).copy(getContext(), true);
                return true;
            case 2131625461:
                if (this.playControlListener == null) {
                    return true;
                }
                if (canShowGoToAlbum(track)) {
                    this.playControlListener.onSearchAlbumMusic();
                } else {
                    this.playControlListener.onSearchMusic();
                }
                StatisticManager.getInstance().addStatisticEvent("music-search_touch", new Pair[0]);
                return true;
            case 2131625506:
                if (track == null) {
                    return true;
                }
                this.playerControl.onAddTrack(track);
                return true;
            case 2131625507:
                if (this.playControlListener == null) {
                    return true;
                }
                if (canShowGoToArtist(track)) {
                    this.playControlListener.onSearchArtistMusic();
                } else {
                    this.playControlListener.onSearchMusic();
                }
                StatisticManager.getInstance().addStatisticEvent("music-search_touch", new Pair[0]);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showLoadProgress() {
    }

    public void hideLoadProgress() {
    }

    public void setOnControlListener(OnControlListener listener) {
        this.controlListener = listener;
    }

    public void setPlayControlListener(OnPlayControlListener listener) {
        this.playControlListener = listener;
    }

    public void setProgress(int value, int duration, int secPosition) {
        if (getActivity() != null && isAdded() && !this.tracking) {
            this.progress.setProgress(value);
            setTime(secPosition, duration);
        }
    }

    public void setEnabledProgress(boolean value) {
        if (getActivity() != null && isAdded()) {
            this.progress.setEnabled(value);
        }
    }

    public int getProgressInSec() {
        return this.progressSec;
    }

    public void setDownloadProgress(int value) {
        if (isAdded()) {
            this.progress.setSecondaryProgress(value);
        }
    }

    private void setStaticTrackInfo(Track track) {
        if (track != null) {
            if (this.trackTextView != null) {
                this.trackTextView.setText(track.name);
            }
            if (!(this.artistTextView == null || track.artist == null)) {
                this.artistTextView.setText(track.artist.name);
            }
            if (this.albumTextView != null && track.album != null) {
                this.albumTextView.setText(track.album.name);
            }
        }
    }

    public void setTime(int sec, int duration) {
        this.startTimeTextView.setText(DateFormatter.getTimeStringFromSec(sec));
        this.endTimeTextView.setText("-" + DateFormatter.getTimeStringFromSec(duration - sec));
        this.progressSec = sec;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if (BaseCompatToolbarActivity.isUseTabbar(activity) && DeviceUtils.getType(activity) == DeviceLayoutType.SMALL) {
            ((BaseTabbarManager) activity).showAboveTabbar();
        }
        if (this.viewPager != null && isPlayAnimation()) {
            this.viewPager.setVisibility(4);
            this.viewPager.post(new C08062());
        }
    }

    public void onResume() {
        super.onResume();
        this.playerControl.registerReceiver();
        if (this.controlListener != null) {
            this.controlListener.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        this.playerControl.unRegisterReceiver();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPlayerState();
    }

    public void onStart() {
        super.onStart();
        Context context = OdnoklassnikiApplication.getContext();
        context.bindService(new Intent(context, MusicService.class), this.serviceConnection, 64);
        PlayerAnimationHelper.registerCallback(4, this);
    }

    public void onStop() {
        super.onStop();
        OdnoklassnikiApplication.getContext().unbindService(this.serviceConnection);
        PlayerAnimationHelper.unregisterCallback(4, this);
        this.musicServiceBinder = null;
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterListeners();
    }

    private void unregisterListeners() {
        this.playerControl.unRegisterBus();
        if (this.controlListener != null) {
            this.controlListener.onStop();
        }
    }

    public void onPlayClick(PlayPauseView view) {
        if (this.playControlListener != null) {
            this.playControlListener.onPlayMusic();
        }
        StatisticManager.getInstance().addStatisticEvent("music-play_touch", new Pair[0]);
    }

    public void onPauseClick(PlayPauseView view) {
        if (this.playControlListener != null) {
            this.playControlListener.onPauseMusic();
        }
        StatisticManager.getInstance().addStatisticEvent("music-pause_touch", new Pair[0]);
    }

    public void setShuffle(boolean value) {
        if (getActivity() != null && isAdded()) {
            ImageView shuffle = (ImageView) this.mMainView.findViewById(2131625236);
            if (value) {
                shuffle.setImageResource(2130838195);
            } else {
                shuffle.setImageResource(2130838194);
            }
        }
    }

    public void setRepeat(RepeatState state) {
        if (getActivity() != null && isAdded()) {
            ImageView repeat = (ImageView) this.mMainView.findViewById(2131625240);
            if (state == RepeatState.none) {
                repeat.setImageResource(2130838185);
            } else if (state == RepeatState.repeat) {
                repeat.setImageResource(2130838187);
            } else if (state == RepeatState.repeatOne) {
                repeat.setImageResource(2130838186);
            }
        }
    }

    public void onClick(View view) {
        if (view.getId() == 2131625239) {
            this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() + 1, true);
            StatisticManager.getInstance().addStatisticEvent("music-next_touch", new Pair[0]);
        } else if (view.getId() == 2131625237) {
            if (getProgressInSec() < 5) {
                this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() - 1, true);
            } else {
                this.playControlListener.onChangeTrackPosition(0);
            }
            StatisticManager.getInstance().addStatisticEvent("music-prev_touch", new Pair[0]);
        } else if (view.getId() == 2131625236) {
            if (this.playControlListener != null) {
                this.playControlListener.onShuffleUpdate();
            }
            StatisticManager.getInstance().addStatisticEvent("music-shuffle_touch", new Pair[0]);
        } else if (view.getId() == 2131625240) {
            if (this.playControlListener != null) {
                this.playControlListener.onRepeatUpdate();
            }
            StatisticManager.getInstance().addStatisticEvent("music-repeat_touch", new Pair[0]);
        }
        if (view.getId() == 2131625403 && DeviceUtils.isTablet(getContext())) {
            View anchor = (View) view.getTag();
            if (anchor != null) {
                showArtistAlbumPopup(anchor);
            }
        }
    }

    private void showArtistAlbumPopup(View anchor) {
        SelectAlbumArtistActionBox actionBox = new SelectAlbumArtistActionBox(getActivity(), anchor);
        if (canShowGoToAlbum(this.adapter.getCurrentTrack())) {
            actionBox.addAlbumAction();
        }
        if (canShowGoToArtist(this.adapter.getCurrentTrack())) {
            actionBox.addArtistAction();
        }
        actionBox.setOnSelectAlbumArtistListener(new C08073());
        actionBox.show();
    }

    private boolean canShowGoToArtist(Track track) {
        return (track == null || track.artist == null || track.artist.id <= 0) ? false : true;
    }

    private boolean canShowGoToAlbum(Track track) {
        return (track == null || track.album == null || track.album.id <= 0) ? false : true;
    }

    private boolean canShowGoToSearchAlbum(Track track) {
        return (track == null || track.album == null || track.album.id >= 0) ? false : true;
    }

    private boolean canShowGoToSearchArtist(Track track) {
        return (track == null || track.artist == null || track.artist.id >= 0) ? false : true;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        boolean z;
        boolean z2 = true;
        super.onPrepareOptionsMenu(menu);
        Track track = this.musicServiceBinder != null ? this.musicServiceBinder.getCurrentTrack() : null;
        MenuItem item = menu.findItem(2131625507);
        if (item != null) {
            if (canShowGoToSearchArtist(track) || canShowGoToArtist(track)) {
                z = true;
            } else {
                z = false;
            }
            item.setVisible(z);
        }
        item = menu.findItem(2131625461);
        if (item != null) {
            if (canShowGoToSearchAlbum(track) || canShowGoToAlbum(track)) {
                z = true;
            } else {
                z = false;
            }
            item.setVisible(z);
        }
        item = menu.findItem(2131625506);
        if (item != null) {
            if (track == null || Settings.getPlayListType(getContext(), MusicListType.MY_MUSIC) == MusicListType.MY_MUSIC) {
                item.setVisible(false);
            } else {
                item.setVisible(true);
            }
        }
        item = menu.findItem(2131625454);
        if (item != null) {
            if (track == null) {
                z2 = false;
            }
            item.setVisible(z2);
        }
    }

    public void onNextTrack() {
        this.lastScrollForward = true;
        setProgress(0, 0, 0);
        setDownloadProgress(0);
        showLoadProgress();
        if (this.musicServiceBinder != null) {
            this.musicServiceBinder.nextBlocking();
            update();
        } else {
            this.needUpdate = true;
        }
        StatisticManager.getInstance().addStatisticEvent("music-next_touch", new Pair[0]);
    }

    public void onPrevTrack() {
        this.lastScrollForward = false;
        setProgress(0, 0, 0);
        setDownloadProgress(0);
        showLoadProgress();
        if (this.musicServiceBinder != null) {
            this.musicServiceBinder.prevBlocking();
            update();
        } else {
            this.needUpdate = true;
        }
        StatisticManager.getInstance().addStatisticEvent("music-prev_touch", new Pair[0]);
    }

    public void callUpdate() {
        if (this.musicServiceBinder == null) {
            this.needUpdate = true;
        } else if (this.animationInProgress) {
            this.needUpdate = true;
        } else {
            this.needUpdate = false;
            update();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Parcelable[] viewStates = saveViewStates();
        saveErrorDialog();
        this.playerControl.unRegisterReceiver();
        unregisterListeners();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        ViewGroup viewGroup = (ViewGroup) getView();
        viewGroup.removeAllViews();
        inflateLayout(inflater, viewGroup);
        restoreViewStates(viewStates);
        restoreErrorDialog();
        requestPlayerState();
        this.playerControl.registerReceiver();
        if (this.controlListener != null) {
            this.controlListener.onResume();
        }
    }

    private void restoreViewStates(Parcelable[] viewStates) {
        this.progress.onRestoreInstanceState(viewStates[0]);
        this.startTimeTextView.onRestoreInstanceState(viewStates[1]);
        this.startTimeTextView.setFreezesText(false);
        this.endTimeTextView.onRestoreInstanceState(viewStates[2]);
        this.endTimeTextView.setFreezesText(false);
        this.playPauseView.onRestoreInstanceState(viewStates[3]);
    }

    private Parcelable[] saveViewStates() {
        this.startTimeTextView.setFreezesText(true);
        this.endTimeTextView.setFreezesText(true);
        return new Parcelable[]{this.progress.onSaveInstanceState(), this.startTimeTextView.onSaveInstanceState(), this.endTimeTextView.onSaveInstanceState(), this.playPauseView.onSaveInstanceState()};
    }

    private void restoreErrorDialog() {
        if (this.errorMusicDialog != null) {
            this.errorMusicDialog = null;
            showErrorMusicDialog();
        }
    }

    private void saveErrorDialog() {
        if (this.errorMusicDialog != null) {
            this.errorMusicDialog.dismiss();
        }
    }

    private void update() {
        Track prev = this.musicServiceBinder.getPrevTrack();
        Track cur = this.musicServiceBinder.getCurrentTrack();
        Track next = this.musicServiceBinder.getNextTrack();
        PlayTrackInfo info = this.musicServiceBinder.getCurrentTrackInfo();
        setStaticLegalInfo(info);
        this.adapter.addTrackInfo(cur.id, info);
        setStaticTrackInfo(cur);
        this.adapter.replaceTracks(cur, prev, next);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.invalidateOptionsMenu();
        }
    }

    private boolean isPlayAnimation() {
        return this.isFirstStart && getArguments() != null && getArguments().getBoolean("argument_animate", false) && DeviceUtils.isSmall(getContext());
    }

    private void setStaticLegalInfo(PlayTrackInfo info) {
        if (this.legalInfoTextView != null && info != null) {
            this.legalInfoTextView.setText(MusicPlayerUtils.buildLegalInfo(getContext(), info));
        }
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (this.playControlListener != null) {
            this.playControlListener.onChangeTrackSeekPosition(i);
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        this.tracking = true;
        if (this.playControlListener != null) {
            this.playControlListener.onStartSeek();
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        int position = this.progress.getProgress();
        if (this.playControlListener != null) {
            this.playControlListener.onChangeTrackPosition(position);
        }
        this.tracking = false;
    }

    public void showErrorMusicDialog() {
        Context activity = getActivity();
        if (this.errorMusicDialog == null && activity != null) {
            this.errorMusicDialog = new ErrorMusicDialog(activity, LocalizationManager.getString(activity, 2131165414));
            this.errorMusicDialog.setOnNextTrackListener(new C08095(activity));
            this.errorMusicDialog.show();
        }
    }

    public void setScrollDirection(boolean forward) {
        this.lastScrollForward = forward;
    }

    public Bundle onMessage(Message message) {
        switch (message.what) {
            case MessagesProto.Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                if (!(this.viewPager == null || this.viewPager.getVisibility() == 0)) {
                    this.viewPager.setVisibility(0);
                    break;
                }
        }
        return null;
    }
}
