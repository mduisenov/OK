package ru.ok.android.ui.fragments;

import android.os.Bundle;
import android.os.ResultReceiver;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.music.CustomPlayListFragment;
import ru.ok.android.fragments.music.MusicPlayerInActionBarFragment;
import ru.ok.android.model.music.MusicInfoContainer;
import ru.ok.android.music.view.FloatingPlayerButton;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.app.MusicService.InformationState;
import ru.ok.android.services.app.MusicService.MusicState;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.animation.PlayerAnimationHelper;
import ru.ok.android.utils.animation.SyncBus.MessageCallback;
import ru.ok.android.utils.bus.BusProtocol;

public class PlayerDataUpdateReceiver extends ResultReceiver implements MessageCallback {
    private FloatingPlayerButton floatingButton;
    private final MusicPlayerInActionBarFragment fragment;
    private boolean lastVisibility;
    private boolean queueRevealAnimation;

    /* renamed from: ru.ok.android.ui.fragments.PlayerDataUpdateReceiver.1 */
    static /* synthetic */ class C08041 {
        static final /* synthetic */ int[] f102xae06aa6c;

        static {
            f102xae06aa6c = new int[InformationState.values().length];
            try {
                f102xae06aa6c[InformationState.PLAY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f102xae06aa6c[InformationState.ERROR.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f102xae06aa6c[InformationState.PAUSE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public PlayerDataUpdateReceiver(MusicPlayerInActionBarFragment fragment) {
        super(null);
        this.fragment = fragment;
    }

    public boolean updateFloatingPlayerButton(FloatingPlayerButton button) {
        int i = 0;
        if (button == null) {
            return false;
        }
        boolean visible;
        this.floatingButton = button;
        if (this.lastVisibility && DeviceUtils.isSmall(this.fragment.getContext())) {
            visible = true;
        } else {
            visible = false;
        }
        FloatingPlayerButton floatingPlayerButton = this.floatingButton;
        if (!visible) {
            i = 8;
        }
        floatingPlayerButton.setVisibility(i);
        return visible;
    }

    public boolean isRevealAnimationQueued() {
        return this.queueRevealAnimation;
    }

    public void onPlayerViewCreated() {
        if (this.fragment != null && this.floatingButton != null && this.queueRevealAnimation) {
            this.floatingButton.startRevealAnimation(this.fragment.getContext(), this.fragment);
        }
    }

    public void onPlayerViewCollapsed() {
        if (this.floatingButton != null && this.fragment != null) {
            this.floatingButton.startCollapseAnimation(this.fragment.getContext());
        }
    }

    @Subscribe(on = 2131623946, to = 2131624252)
    public void onStreamMediaStatus(BusEvent event) {
        InformationState playState = getPlayState(event);
        if (this.fragment.isPlayFloatingButtonRequired()) {
            changeFloatingButtonState(playState);
            fetchAlbumImage(getMusicInfo(event));
        }
        if (!(DeviceUtils.isSmall(this.fragment.getContext()) || playState != InformationState.START || (this.fragment instanceof CustomPlayListFragment))) {
            NavigationHelper.showMusicPlayer(this.fragment.getActivity());
        }
        this.fragment.onMediaPlayerState(event);
    }

    private MusicInfoContainer getMusicInfo(BusEvent event) {
        return (MusicInfoContainer) event.bundleOutput.getParcelable(BusProtocol.PREF_MEDIA_PLAYER_STATE_MUSIC_INFO_CONTAINER);
    }

    private InformationState getPlayState(BusEvent event) {
        return (InformationState) event.bundleOutput.getSerializable(BusProtocol.PREF_MEDIA_PLAYER_STATE);
    }

    private void changeFloatingButtonState(InformationState playState) {
        switch (C08041.f102xae06aa6c[playState.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (this.floatingButton != null) {
                    this.floatingButton.setPlay();
                    this.floatingButton.setVisibility(0);
                } else if (this.fragment != null && this.fragment.isPlayFloatingButtonRequired()) {
                    this.fragment.inflateFloatingPlayerButton();
                }
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                if (this.floatingButton != null) {
                    this.floatingButton.setPause();
                }
            default:
        }
    }

    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        this.lastVisibility = ((MusicState) resultData.getSerializable("playState")) != MusicState.UNKNOWN;
        updateFloatingPlayerButtonState(resultData);
        if (this.fragment.getActivity() != null) {
            if (this.lastVisibility && !DeviceUtils.isSmall(this.fragment.getContext()) && (this.fragment instanceof MusicUsersFragment) && this.fragment.isVisible()) {
                NavigationHelper.showMusicPlayer(this.fragment.getActivity());
            }
            this.fragment.getActivity().invalidateOptionsMenu();
        }
    }

    private void updateFloatingPlayerButtonState(Bundle resultData) {
        int i = 0;
        if (this.floatingButton != null && this.fragment.getActivity() != null) {
            boolean visible;
            fetchAlbumImage((MusicInfoContainer) resultData.getParcelable("music_info"));
            if (resultData.getSerializable("playState") == MusicState.PLAYING) {
                this.floatingButton.setPlay();
            } else {
                this.floatingButton.setPause();
            }
            if (this.lastVisibility && DeviceUtils.isSmall(this.fragment.getContext())) {
                visible = true;
            } else {
                visible = false;
            }
            if (this.floatingButton != null) {
                FloatingPlayerButton floatingPlayerButton = this.floatingButton;
                if (!visible) {
                    i = 8;
                }
                floatingPlayerButton.setVisibility(i);
            }
        }
    }

    private void fetchAlbumImage(MusicInfoContainer container) {
        if (container != null && container.playTrackInfo != null && container.playTrackInfo.imageUrl != null && this.floatingButton != null) {
            this.floatingButton.setAlbumUrl(container.playTrackInfo.imageUrl);
        }
    }

    public void queueRevealAnimation() {
        this.queueRevealAnimation = true;
    }

    public void unSubscribe() {
        if (this.fragment.isPlayFloatingButtonRequired()) {
            PlayerAnimationHelper.unregisterCallback(1, this);
            PlayerAnimationHelper.unregisterCallback(2, this);
            PlayerAnimationHelper.unregisterCallback(4, this);
            this.queueRevealAnimation = false;
            if (this.floatingButton != null) {
                this.floatingButton.resetAnimation();
            }
        }
        GlobalBus.unregister(this);
    }

    public void subscribe() {
        if (this.fragment.isPlayFloatingButtonRequired()) {
            PlayerAnimationHelper.registerCallback(1, this);
            PlayerAnimationHelper.registerCallback(2, this);
            PlayerAnimationHelper.registerCallback(4, this);
        }
        GlobalBus.register(this);
    }

    public Bundle onMessage(android.os.Message message) {
        if ((this.fragment == null || DeviceUtils.isSmall(this.fragment.getContext())) && PlayerAnimationHelper.isAnimationEnabled()) {
            switch (message.what) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    onPlayerViewCreated();
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    this.queueRevealAnimation = false;
                    onPlayerViewCollapsed();
                    break;
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    this.queueRevealAnimation = false;
                    break;
                default:
                    break;
            }
        }
        return null;
    }
}
