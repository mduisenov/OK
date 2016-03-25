package ru.ok.android.ui.video.player;

import android.media.MediaCodec.CryptoException;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import com.google.android.exoplayer.DummyTrackRenderer;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.ExoPlayer.Factory;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer.EventListener;
import com.google.android.exoplayer.MediaCodecTrackRenderer.DecoderInitializationException;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioTrack.InitializationException;
import com.google.android.exoplayer.audio.AudioTrack.WriteException;
import com.google.android.exoplayer.chunk.ChunkSampleSource;
import com.google.android.exoplayer.chunk.MultiTrackChunkSource;
import com.google.android.exoplayer.drm.StreamingDrmSessionManager$EventListener;
import com.google.android.exoplayer.metadata.MetadataTrackRenderer.MetadataRenderer;
import com.google.android.exoplayer.text.TextRenderer;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter$EventListener;
import com.google.android.exoplayer.util.PlayerControl;
import com.google.android.gms.location.LocationStatusCodes;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ExoHandlePlayer implements com.google.android.exoplayer.ExoPlayer.Listener, EventListener, MediaCodecVideoTrackRenderer.EventListener, ChunkSampleSource.EventListener, StreamingDrmSessionManager$EventListener, TextRenderer, DefaultBandwidthMeter$EventListener {
    private boolean backgrounded;
    private InternalRendererBuilderCallback builderCallback;
    private Id3MetadataListener id3MetadataListener;
    private InfoListener infoListener;
    private InternalErrorListener internalErrorListener;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState;
    private final CopyOnWriteArrayList<Listener> listeners;
    private final Handler mainHandler;
    private MultiTrackChunkSource[] multiTrackSources;
    private final ExoPlayer player;
    private final PlayerControl playerControl;
    private RendererBuilder rendererBuilder;
    private int rendererBuildingState;
    private int[] selectedTracks;
    private Surface surface;
    private TextListener textListener;
    private String[][] trackNames;
    private TrackRenderer videoRenderer;
    private int videoTrackToRestore;

    public interface InfoListener {
        void onAudioFormatEnabled(String str, int i, int i2);

        void onBandwidthSample(int i, long j, long j2);

        void onDroppedFrames(int i, long j);

        void onLoadCompleted(int i, long j);

        void onLoadStarted(int i, String str, int i2, boolean z, int i3, int i4, long j);

        void onVideoFormatEnabled(String str, int i, int i2);
    }

    public interface InternalErrorListener {
        void onAudioTrackInitializationError(InitializationException initializationException);

        void onAudioTrackWriteError(WriteException writeException);

        void onConsumptionError(int i, IOException iOException);

        void onCryptoError(CryptoException cryptoException);

        void onDecoderInitializationError(DecoderInitializationException decoderInitializationException);

        void onDrmSessionManagerError(Exception exception);

        void onRendererInitializationError(Exception exception);

        void onUpstreamError(int i, IOException iOException);
    }

    public interface Listener {
        void onError(Exception exception);

        void onStateChanged(boolean z, int i);

        void onVideoSizeChanged(int i, int i2, float f);
    }

    /* renamed from: ru.ok.android.ui.video.player.ExoHandlePlayer.1 */
    class C13781 implements MetadataRenderer<Map<String, Object>> {
        C13781() {
        }

        public void onMetadata(Map<String, Object> metadata) {
            if (ExoHandlePlayer.this.id3MetadataListener != null) {
                ExoHandlePlayer.this.id3MetadataListener.onId3Metadata(metadata);
            }
        }
    }

    public interface Id3MetadataListener {
        void onId3Metadata(Map<String, Object> map);
    }

    public interface RendererBuilderCallback {
        void onRenderers(String[][] strArr, MultiTrackChunkSource[] multiTrackChunkSourceArr, TrackRenderer[] trackRendererArr);

        void onRenderersError(Exception exception);
    }

    private class InternalRendererBuilderCallback implements RendererBuilderCallback {
        private boolean canceled;

        private InternalRendererBuilderCallback() {
        }

        public void cancel() {
            this.canceled = true;
        }

        public void onRenderers(String[][] trackNames, MultiTrackChunkSource[] multiTrackSources, TrackRenderer[] renderers) {
            if (!this.canceled) {
                ExoHandlePlayer.this.onRenderers(trackNames, multiTrackSources, renderers);
            }
        }

        public void onRenderersError(Exception e) {
            if (!this.canceled) {
                ExoHandlePlayer.this.onRenderersError(e);
            }
        }
    }

    public interface RendererBuilder {
        void buildRenderers(ExoHandlePlayer exoHandlePlayer, RendererBuilderCallback rendererBuilderCallback);
    }

    public interface TextListener {
        void onText(String str);
    }

    public ExoHandlePlayer(RendererBuilder rendererBuilder) {
        this.rendererBuilder = rendererBuilder;
        this.player = Factory.newInstance(5, LocationStatusCodes.GEOFENCE_NOT_AVAILABLE, 5000);
        this.player.addListener(this);
        this.playerControl = new PlayerControl(this.player);
        this.mainHandler = new Handler();
        this.listeners = new CopyOnWriteArrayList();
        this.lastReportedPlaybackState = 1;
        this.rendererBuildingState = 1;
        this.selectedTracks = new int[5];
        this.selectedTracks[2] = -1;
    }

    public PlayerControl getPlayerControl() {
        return this.playerControl;
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void setInternalErrorListener(InternalErrorListener listener) {
        this.internalErrorListener = listener;
    }

    public void setInfoListener(InfoListener listener) {
        this.infoListener = listener;
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
        pushSurface(false);
    }

    public void blockingClearSurface() {
        this.surface = null;
        pushSurface(true);
    }

    public String[] getTracks(int type) {
        return this.trackNames == null ? null : this.trackNames[type];
    }

    public int getSelectedTrackIndex(int type) {
        return this.selectedTracks[type];
    }

    public void selectTrack(int type, int index) {
        if (this.selectedTracks[type] != index) {
            this.selectedTracks[type] = index;
            pushTrackSelection(type, true);
            if (type == 2 && index == -1 && this.textListener != null) {
                this.textListener.onText(null);
            }
        }
    }

    public void setBackgrounded(boolean backgrounded) {
        if (this.backgrounded != backgrounded) {
            this.backgrounded = backgrounded;
            if (backgrounded) {
                this.videoTrackToRestore = getSelectedTrackIndex(0);
                selectTrack(0, -1);
                blockingClearSurface();
                return;
            }
            selectTrack(0, this.videoTrackToRestore);
        }
    }

    public void prepare() {
        if (this.rendererBuildingState == 3) {
            this.player.stop();
        }
        if (this.builderCallback != null) {
            this.builderCallback.cancel();
        }
        this.rendererBuildingState = 2;
        reportPlayerState();
        this.builderCallback = new InternalRendererBuilderCallback();
        if (this.rendererBuilder != null) {
            this.rendererBuilder.buildRenderers(this, this.builderCallback);
        }
    }

    void onRenderers(String[][] trackNames, MultiTrackChunkSource[] multiTrackSources, TrackRenderer[] renderers) {
        this.builderCallback = null;
        if (trackNames == null) {
            trackNames = new String[5][];
        }
        if (multiTrackSources == null) {
            multiTrackSources = new MultiTrackChunkSource[5];
        }
        for (int i = 0; i < 5; i++) {
            if (renderers[i] == null) {
                renderers[i] = new DummyTrackRenderer();
            } else if (trackNames[i] == null) {
                trackNames[i] = new String[(multiTrackSources[i] == null ? 1 : multiTrackSources[i].getTrackCount())];
            }
        }
        this.videoRenderer = renderers[0];
        this.trackNames = trackNames;
        this.multiTrackSources = multiTrackSources;
        this.rendererBuildingState = 3;
        pushSurface(false);
        pushTrackSelection(0, true);
        pushTrackSelection(1, true);
        pushTrackSelection(2, true);
        this.player.prepare(renderers);
    }

    void onRenderersError(Exception e) {
        this.builderCallback = null;
        if (this.internalErrorListener != null) {
            this.internalErrorListener.onRendererInitializationError(e);
        }
        Iterator i$ = this.listeners.iterator();
        while (i$.hasNext()) {
            ((Listener) i$.next()).onError(e);
        }
        this.rendererBuildingState = 1;
        reportPlayerState();
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.player.setPlayWhenReady(playWhenReady);
    }

    public void seekTo(long positionMs) {
        this.player.seekTo(positionMs);
    }

    public void release() {
        if (this.builderCallback != null) {
            this.builderCallback.cancel();
            this.builderCallback = null;
        }
        this.rendererBuildingState = 1;
        this.surface = null;
        this.player.release();
    }

    public int getPlaybackState() {
        if (this.rendererBuildingState == 2) {
            return 2;
        }
        int playerState = this.player.getPlaybackState();
        if (this.rendererBuildingState == 3 && this.rendererBuildingState == 1) {
            return 2;
        }
        return playerState;
    }

    public long getCurrentPosition() {
        if (this.player != null) {
            return this.player.getCurrentPosition();
        }
        return 0;
    }

    public Looper getPlaybackLooper() {
        return this.player.getPlaybackLooper();
    }

    public Handler getMainHandler() {
        return this.mainHandler;
    }

    public void onPlayerStateChanged(boolean playWhenReady, int state) {
        reportPlayerState();
    }

    public void onPlayerError(ExoPlaybackException exception) {
        this.rendererBuildingState = 1;
        Iterator i$ = this.listeners.iterator();
        while (i$.hasNext()) {
            ((Listener) i$.next()).onError(exception);
        }
    }

    public void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
        Iterator i$ = this.listeners.iterator();
        while (i$.hasNext()) {
            ((Listener) i$.next()).onVideoSizeChanged(width, height, pixelWidthHeightRatio);
        }
    }

    public void onDroppedFrames(int count, long elapsed) {
        if (this.infoListener != null) {
            this.infoListener.onDroppedFrames(count, elapsed);
        }
    }

    public void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate) {
        if (this.infoListener != null) {
            this.infoListener.onBandwidthSample(elapsedMs, bytes, bitrateEstimate);
        }
    }

    public void onDownstreamFormatChanged(int sourceId, String formatId, int trigger, int mediaTimeMs) {
        if (this.infoListener != null) {
            if (sourceId == 0) {
                this.infoListener.onVideoFormatEnabled(formatId, trigger, mediaTimeMs);
            } else if (sourceId == 1) {
                this.infoListener.onAudioFormatEnabled(formatId, trigger, mediaTimeMs);
            }
        }
    }

    public void onDrmSessionManagerError(Exception e) {
        if (this.internalErrorListener != null) {
            this.internalErrorListener.onDrmSessionManagerError(e);
        }
    }

    public void onDecoderInitializationError(DecoderInitializationException e) {
        if (this.internalErrorListener != null) {
            this.internalErrorListener.onDecoderInitializationError(e);
        }
    }

    public void onAudioTrackInitializationError(InitializationException e) {
        if (this.internalErrorListener != null) {
            this.internalErrorListener.onAudioTrackInitializationError(e);
        }
    }

    public void onAudioTrackWriteError(WriteException e) {
        if (this.internalErrorListener != null) {
            this.internalErrorListener.onAudioTrackWriteError(e);
        }
    }

    public void onCryptoError(CryptoException e) {
        if (this.internalErrorListener != null) {
            this.internalErrorListener.onCryptoError(e);
        }
    }

    public void onUpstreamError(int sourceId, IOException e) {
        if (this.internalErrorListener != null) {
            this.internalErrorListener.onUpstreamError(sourceId, e);
        }
    }

    public void onConsumptionError(int sourceId, IOException e) {
        if (this.internalErrorListener != null) {
            this.internalErrorListener.onConsumptionError(sourceId, e);
        }
    }

    public void onText(String text) {
        processText(text);
    }

    public MetadataRenderer<Map<String, Object>> getId3MetadataRenderer() {
        return new C13781();
    }

    public void onPlayWhenReadyCommitted() {
    }

    public void onDrawnToSurface(Surface surface) {
    }

    public void onLoadStarted(int sourceId, String formatId, int trigger, boolean isInitialization, int mediaStartTimeMs, int mediaEndTimeMs, long length) {
        if (this.infoListener != null) {
            this.infoListener.onLoadStarted(sourceId, formatId, trigger, isInitialization, mediaStartTimeMs, mediaEndTimeMs, length);
        }
    }

    public void onLoadCompleted(int sourceId, long bytesLoaded) {
        if (this.infoListener != null) {
            this.infoListener.onLoadCompleted(sourceId, bytesLoaded);
        }
    }

    public void onLoadCanceled(int sourceId, long bytesLoaded) {
    }

    public void onUpstreamDiscarded(int sourceId, int mediaStartTimeMs, int mediaEndTimeMs, long bytesDiscarded) {
    }

    public void onDownstreamDiscarded(int sourceId, int mediaStartTimeMs, int mediaEndTimeMs, long bytesDiscarded) {
    }

    private void reportPlayerState() {
        boolean playWhenReady = this.player.getPlayWhenReady();
        int playbackState = getPlaybackState();
        if (this.lastReportedPlayWhenReady != playWhenReady || this.lastReportedPlaybackState != playbackState) {
            Iterator i$ = this.listeners.iterator();
            while (i$.hasNext()) {
                ((Listener) i$.next()).onStateChanged(playWhenReady, playbackState);
            }
            this.lastReportedPlayWhenReady = playWhenReady;
            this.lastReportedPlaybackState = playbackState;
        }
    }

    private void pushSurface(boolean blockForSurfacePush) {
        if (this.rendererBuildingState == 3) {
            if (blockForSurfacePush) {
                this.player.blockingSendMessage(this.videoRenderer, 1, this.surface);
            } else {
                this.player.sendMessage(this.videoRenderer, 1, this.surface);
            }
        }
    }

    private void pushTrackSelection(int type, boolean allowRendererEnable) {
        if (this.rendererBuildingState == 3) {
            int trackIndex = this.selectedTracks[type];
            if (trackIndex == -1) {
                this.player.setRendererEnabled(type, false);
            } else if (this.multiTrackSources[type] == null) {
                this.player.setRendererEnabled(type, allowRendererEnable);
            } else {
                boolean playWhenReady = this.player.getPlayWhenReady();
                this.player.setPlayWhenReady(false);
                this.player.setRendererEnabled(type, false);
                this.player.sendMessage(this.multiTrackSources[type], 1, Integer.valueOf(trackIndex));
                this.player.setRendererEnabled(type, allowRendererEnable);
                this.player.setPlayWhenReady(playWhenReady);
            }
        }
    }

    void processText(String text) {
        if (this.textListener != null && this.selectedTracks[2] != -1) {
            this.textListener.onText(text);
        }
    }
}
