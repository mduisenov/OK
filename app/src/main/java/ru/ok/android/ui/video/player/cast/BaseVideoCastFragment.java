package ru.ok.android.ui.video.player.cast;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.CastException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.player.OnVideoCastControllerListener;
import com.google.android.libraries.cast.companionlibrary.cast.player.VideoCastController;
import com.google.android.libraries.cast.companionlibrary.utils.Utils;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.utils.Logger;

public abstract class BaseVideoCastFragment extends BaseFragment implements OnVideoCastControllerListener, VideoCastController {
    private static Handler handler;
    private MyCastConsumer castConsumer;
    private VideoCastManager castManager;
    private boolean isFresh;
    private int playbackState;
    private Timer seekBarTimer;
    private MediaInfo selectedMedia;

    private class MyCastConsumer extends VideoCastConsumerImpl {
        private MyCastConsumer() {
        }

        public void onDisconnected() {
            long position;
            try {
                position = BaseVideoCastFragment.this.castManager.getCurrentMediaPosition();
            } catch (Exception e) {
                position = 0;
            }
            BaseVideoCastFragment.this.onCastDisconnect(position);
        }

        public void onApplicationDisconnected(int errorCode) {
            BaseVideoCastFragment.this.showError(2131165484);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onRemoteMediaPlayerMetadataUpdated() {
            /*
            r4 = this;
            r1 = ru.ok.android.ui.video.player.cast.BaseVideoCastFragment.this;	 Catch:{ TransientNetworkDisconnectionException -> 0x0028, NoConnectionException -> 0x001a }
            r2 = ru.ok.android.ui.video.player.cast.BaseVideoCastFragment.this;	 Catch:{ TransientNetworkDisconnectionException -> 0x0028, NoConnectionException -> 0x001a }
            r2 = r2.castManager;	 Catch:{ TransientNetworkDisconnectionException -> 0x0028, NoConnectionException -> 0x001a }
            r2 = r2.getRemoteMediaInformation();	 Catch:{ TransientNetworkDisconnectionException -> 0x0028, NoConnectionException -> 0x001a }
            r1.selectedMedia = r2;	 Catch:{ TransientNetworkDisconnectionException -> 0x0028, NoConnectionException -> 0x001a }
            r1 = ru.ok.android.ui.video.player.cast.BaseVideoCastFragment.this;	 Catch:{ TransientNetworkDisconnectionException -> 0x0028, NoConnectionException -> 0x001a }
            r1.updateClosedCaptionState();	 Catch:{ TransientNetworkDisconnectionException -> 0x0028, NoConnectionException -> 0x001a }
            r1 = ru.ok.android.ui.video.player.cast.BaseVideoCastFragment.this;	 Catch:{ TransientNetworkDisconnectionException -> 0x0028, NoConnectionException -> 0x001a }
            r1.updateMetadata();	 Catch:{ TransientNetworkDisconnectionException -> 0x0028, NoConnectionException -> 0x001a }
        L_0x0019:
            return;
        L_0x001a:
            r0 = move-exception;
        L_0x001b:
            r1 = "Failed to update the metadata due to network issues";
            r2 = 1;
            r2 = new java.lang.Object[r2];
            r3 = 0;
            r2[r3] = r0;
            ru.ok.android.utils.Logger.m177e(r1, r2);
            goto L_0x0019;
        L_0x0028:
            r0 = move-exception;
            goto L_0x001b;
            */
            throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.video.player.cast.BaseVideoCastFragment.MyCastConsumer.onRemoteMediaPlayerMetadataUpdated():void");
        }

        public void onFailed(int resourceId, int statusCode) {
            Logger.m172d("onFailed(): " + BaseVideoCastFragment.this.getString(resourceId) + ", status code: " + statusCode);
            if (statusCode == 2100 || statusCode == 2102) {
                Utils.showToast(BaseVideoCastFragment.this.getActivity(), resourceId);
            }
        }

        public void onRemoteMediaPlayerStatusUpdated() {
            BaseVideoCastFragment.this.updatePlayerStatus();
        }

        public void onConnectionSuspended(int cause) {
            BaseVideoCastFragment.this.updateControllersStatus(false);
        }

        public void onConnectivityRecovered() {
            BaseVideoCastFragment.this.updateControllersStatus(true);
        }
    }

    private class UpdateSeekBarTask extends TimerTask {

        /* renamed from: ru.ok.android.ui.video.player.cast.BaseVideoCastFragment.UpdateSeekBarTask.1 */
        class C14061 implements Runnable {
            C14061() {
            }

            public void run() {
                IOException e;
                if (BaseVideoCastFragment.this.playbackState != 4 && BaseVideoCastFragment.this.castManager.isConnected()) {
                    try {
                        long duration = BaseVideoCastFragment.this.castManager.getMediaDuration();
                        if (duration > 0) {
                            try {
                                BaseVideoCastFragment.this.updateSeekbar((int) BaseVideoCastFragment.this.castManager.getCurrentMediaPosition(), (int) duration);
                            } catch (Exception e2) {
                                Logger.m173d("Failed to get current media position", e2);
                            }
                        }
                    } catch (TransientNetworkDisconnectionException e3) {
                        e = e3;
                        Logger.m173d("Failed to update the progress bar due to network issues", e);
                    } catch (NoConnectionException e4) {
                        e = e4;
                        Logger.m173d("Failed to update the progress bar due to network issues", e);
                    }
                }
            }
        }

        private UpdateSeekBarTask() {
        }

        public void run() {
            BaseVideoCastFragment.handler.post(new C14061());
        }
    }

    public BaseVideoCastFragment() {
        this.isFresh = true;
    }

    static {
        handler = new Handler();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.castManager = VideoCastManager.getInstance();
        this.castManager.addTracksSelectedListener(this);
        this.castConsumer = new MyCastConsumer();
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle mediaWrapper = getArguments().getBundle("media");
        this.isFresh = false;
        if (mediaWrapper != null) {
            onReady(Utils.bundleToMediaInfo(mediaWrapper), getArguments().getBoolean("shouldStart"), getArguments().getInt("startPoint", 0), getCustomData());
            return;
        }
        showError(2131166816);
    }

    private JSONObject getCustomData() {
        String customDataStr = getArguments().getString("customData");
        if (!TextUtils.isEmpty(customDataStr)) {
            try {
                return new JSONObject(customDataStr);
            } catch (JSONException e) {
                Logger.m177e("Failed to unMarshalize custom data string: customData=" + customDataStr, e);
            }
        }
        return null;
    }

    protected void showError(int resId) {
        Logger.m172d("error");
    }

    protected String getDeviceName() {
        if (this.castManager != null) {
            String deviceName = this.castManager.getDeviceName();
            if (!TextUtils.isEmpty(deviceName)) {
                return deviceName;
            }
        }
        return "";
    }

    protected void onCastDisconnect(long position) {
        Logger.m172d("Device disconnect position :" + position);
    }

    private void onReady(MediaInfo mediaInfo, boolean shouldStartPlayback, int startPoint, JSONObject customData) {
        this.selectedMedia = mediaInfo;
        updateClosedCaptionState();
        try {
            setStreamType(this.selectedMedia.getStreamType());
            if (shouldStartPlayback) {
                this.playbackState = 4;
                setPlaybackStatus(this.playbackState);
                this.castManager.loadMedia(this.selectedMedia, true, startPoint, customData);
            } else {
                if (this.castManager.isRemoteMediaPlaying()) {
                    this.playbackState = 2;
                } else {
                    this.playbackState = 3;
                }
                setPlaybackStatus(this.playbackState);
            }
        } catch (Exception e) {
            Logger.m177e("Failed to get playback and media information", e);
            showError(2131165484);
        }
        updateMetadata();
        restartTrickPlayTimer();
    }

    private void updateClosedCaptionState() {
        int state = 3;
        if (this.castManager.isFeatureEnabled(16) && this.selectedMedia != null && this.castManager.getTracksPreferenceManager().isCaptionEnabled()) {
            List<MediaTrack> tracks = this.selectedMedia.getMediaTracks();
            state = (tracks == null || tracks.isEmpty()) ? 2 : 1;
        }
        setClosedCaptionState(state);
    }

    private void stopTrickPlayTimer() {
        Logger.m172d("Stopped TrickPlay Timer");
        if (this.seekBarTimer != null) {
            this.seekBarTimer.cancel();
        }
    }

    private void restartTrickPlayTimer() {
        stopTrickPlayTimer();
        this.seekBarTimer = new Timer();
        this.seekBarTimer.scheduleAtFixedRate(new UpdateSeekBarTask(), 100, 1000);
        Logger.m172d("Restarted TrickPlay Timer");
    }

    private void updateMetadata() {
        boolean isLive = true;
        if (this.selectedMedia != null) {
            showImage(Utils.getImageUri(this.selectedMedia, 1));
        }
        if (this.selectedMedia != null) {
            MediaMetadata mm = this.selectedMedia.getMetadata();
            setTitle(mm.getString("com.google.android.gms.cast.metadata.TITLE") != null ? mm.getString("com.google.android.gms.cast.metadata.TITLE") : "");
            if (this.selectedMedia.getStreamType() != 2) {
                isLive = false;
            }
            adjustControllersForLiveStream(isLive);
        }
    }

    private void updatePlayerStatus() {
        IOException e;
        int mediaStatus = this.castManager.getPlaybackStatus();
        Logger.m172d("updatePlayerStatus(), state: " + mediaStatus);
        if (this.selectedMedia != null) {
            setStreamType(this.selectedMedia.getStreamType());
            if (mediaStatus == 4) {
                setSubTitle(getString(C0158R.string.ccl_loading));
            } else {
                setSubTitle(getString(C0158R.string.ccl_casting_to_device, this.castManager.getDeviceName()));
            }
            switch (mediaStatus) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    switch (this.castManager.getIdleReason()) {
                        case Message.TEXT_FIELD_NUMBER /*1*/:
                            if (!this.isFresh) {
                                try {
                                    if (this.castManager.isRemoteStreamLive() && this.playbackState != 1) {
                                        this.playbackState = 1;
                                        setPlaybackStatus(this.playbackState);
                                    }
                                } catch (TransientNetworkDisconnectionException e2) {
                                    e = e2;
                                    Logger.m177e("Failed to determine if stream is live", e);
                                    onVideoFinish();
                                } catch (NoConnectionException e3) {
                                    e = e3;
                                    Logger.m177e("Failed to determine if stream is live", e);
                                    onVideoFinish();
                                }
                                onVideoFinish();
                            }
                        case Message.AUTHORID_FIELD_NUMBER /*2*/:
                            try {
                                if (this.castManager.isRemoteStreamLive() && this.playbackState != 1) {
                                    this.playbackState = 1;
                                    setPlaybackStatus(this.playbackState);
                                }
                            } catch (TransientNetworkDisconnectionException e4) {
                                e = e4;
                                Logger.m177e("Failed to determine if stream is live", e);
                            } catch (NoConnectionException e5) {
                                e = e5;
                                Logger.m177e("Failed to determine if stream is live", e);
                            }
                        default:
                    }
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    this.isFresh = false;
                    if (this.playbackState != 2) {
                        this.playbackState = 2;
                        setPlaybackStatus(this.playbackState);
                    }
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    this.isFresh = false;
                    if (this.playbackState != 3) {
                        this.playbackState = 3;
                        setPlaybackStatus(this.playbackState);
                    }
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    this.isFresh = false;
                    if (this.playbackState != 4) {
                        this.playbackState = 4;
                        setPlaybackStatus(this.playbackState);
                    }
                default:
            }
        }
    }

    protected void onVideoFinish() {
        Logger.m172d("video finish");
    }

    public void onDestroy() {
        Logger.m172d("onDestroy()");
        stopTrickPlayTimer();
        this.castManager.removeTracksSelectedListener(this);
        this.castManager = null;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    public void onResume() {
        IOException e;
        super.onResume();
        this.castManager.addVideoCastConsumer(this.castConsumer);
        this.castManager.incrementUiCounter();
        try {
            if ((this.castManager.isRemoteMediaPaused() || this.castManager.isRemoteMediaPlaying()) && this.castManager.getRemoteMediaInformation() != null && this.selectedMedia.getContentId().equals(this.castManager.getRemoteMediaInformation().getContentId())) {
                this.isFresh = false;
            }
            if (!this.castManager.isConnecting()) {
                boolean shouldFinish = !this.castManager.isConnected() || (this.castManager.getPlaybackStatus() == 1 && this.castManager.getIdleReason() == 1);
                if (shouldFinish && !this.isFresh) {
                    if (this.playbackState != 4) {
                        showError(2131165484);
                        return;
                    }
                    return;
                }
            }
            if (!this.isFresh) {
                updatePlayerStatus();
                this.selectedMedia = this.castManager.getRemoteMediaInformation();
                updateClosedCaptionState();
                updateMetadata();
            }
        } catch (TransientNetworkDisconnectionException e2) {
            e = e2;
            Logger.m177e("Failed to get media information or status of media playback", e);
        } catch (NoConnectionException e3) {
            e = e3;
            Logger.m177e("Failed to get media information or status of media playback", e);
        }
    }

    public void onPause() {
        this.castManager.removeVideoCastConsumer(this.castConsumer);
        this.castManager.decrementUiCounter();
        this.isFresh = false;
        super.onPause();
    }

    protected void showImage(Uri uri) {
        Logger.m172d("show image" + uri);
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        try {
            if (this.playbackState == 2) {
                this.playbackState = 4;
                setPlaybackStatus(this.playbackState);
                this.castManager.play(seekBar.getProgress());
            } else if (this.playbackState == 3) {
                this.castManager.seek(seekBar.getProgress());
            }
            restartTrickPlayTimer();
        } catch (Exception e) {
            Logger.m177e("Failed to complete seek", e);
            showError(2131165484);
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        stopTrickPlayTimer();
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    public void onPlayPauseClicked(View v) throws CastException, TransientNetworkDisconnectionException, NoConnectionException {
        Logger.m172d("Play or Pause clicked, isConnected returning: " + this.castManager.isConnected());
        togglePlayback();
    }

    private void togglePlayback() throws CastException, TransientNetworkDisconnectionException, NoConnectionException {
        switch (this.playbackState) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (this.selectedMedia.getStreamType() == 2 && this.castManager.getIdleReason() == 2) {
                    this.castManager.play();
                } else {
                    this.castManager.loadMedia(this.selectedMedia, true, 0);
                }
                this.playbackState = 4;
                restartTrickPlayTimer();
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                this.castManager.pause();
                this.playbackState = 4;
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.castManager.play();
                this.playbackState = 4;
                restartTrickPlayTimer();
                break;
        }
        setPlaybackStatus(this.playbackState);
    }

    public void onConfigurationChanged() {
        if (this.selectedMedia != null) {
            updateMetadata();
            updatePlayerStatus();
            updateControllersStatus(this.castManager.isConnected());
        }
    }

    public void onTracksSelected(List<MediaTrack> tracks) {
        long[] tracksArray;
        if (tracks.isEmpty()) {
            tracksArray = new long[0];
        } else {
            tracksArray = new long[tracks.size()];
            for (int i = 0; i < tracks.size(); i++) {
                tracksArray[i] = ((MediaTrack) tracks.get(i)).getId();
            }
        }
        this.castManager.setActiveTrackIds(tracksArray);
        if (tracks.size() > 0) {
            this.castManager.setTextTrackStyle(this.castManager.getTracksPreferenceManager().getTextTrackStyle());
        }
    }
}
