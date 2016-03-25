package ru.ok.android.ui.video.player;

import android.media.MediaCodec.CryptoException;
import android.os.SystemClock;
import com.google.android.exoplayer.MediaCodecTrackRenderer.DecoderInitializationException;
import com.google.android.exoplayer.audio.AudioTrack.InitializationException;
import com.google.android.exoplayer.audio.AudioTrack.WriteException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.video.player.ExoHandlePlayer.InfoListener;
import ru.ok.android.ui.video.player.ExoHandlePlayer.InternalErrorListener;
import ru.ok.android.ui.video.player.ExoHandlePlayer.Listener;
import ru.ok.android.utils.Logger;

public class EventLogger implements InfoListener, InternalErrorListener, Listener {
    private static final NumberFormat TIME_FORMAT;
    private final boolean isLogEnabled;
    private long[] loadStartTimeMs;
    private long sessionStartTimeMs;

    static {
        TIME_FORMAT = NumberFormat.getInstance(Locale.US);
        TIME_FORMAT.setMinimumFractionDigits(2);
        TIME_FORMAT.setMaximumFractionDigits(2);
    }

    public EventLogger() {
        this.isLogEnabled = Logger.isLoggingEnable();
        this.loadStartTimeMs = new long[5];
    }

    public void startSession() {
        this.sessionStartTimeMs = SystemClock.elapsedRealtime();
        Logger.m172d("start [0]");
    }

    public void endSession() {
        String str = "end [%s]";
        Object[] objArr = new Object[1];
        objArr[0] = this.isLogEnabled ? getSessionTimeString() : null;
        Logger.m173d(str, objArr);
    }

    public void onStateChanged(boolean playWhenReady, int state) {
        String sessionTimeString;
        String str = null;
        String str2 = "state [%s, %s, %s]";
        Object[] objArr = new Object[3];
        if (this.isLogEnabled) {
            sessionTimeString = getSessionTimeString();
        } else {
            sessionTimeString = null;
        }
        objArr[0] = sessionTimeString;
        objArr[1] = Boolean.valueOf(playWhenReady);
        if (this.isLogEnabled) {
            str = getStateString(state);
        }
        objArr[2] = str;
        Logger.m173d(str2, objArr);
    }

    public void onError(Exception e) {
        String str = "playerFailed [%s]: %s";
        Object[] objArr = new Object[2];
        objArr[0] = this.isLogEnabled ? getSessionTimeString() : null;
        objArr[1] = e;
        Logger.m180e(e, str, objArr);
    }

    public void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
        Logger.m173d("videoSizeChanged [%d, %d, %.3f]", Integer.valueOf(width), Integer.valueOf(height), Float.valueOf(pixelWidthHeightRatio));
    }

    public void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate) {
        String str = null;
        String str2 = "bandwidth [%s, %d, %s, %d]";
        Object[] objArr = new Object[4];
        objArr[0] = this.isLogEnabled ? getSessionTimeString() : null;
        objArr[1] = Long.valueOf(bytes);
        if (this.isLogEnabled) {
            str = getTimeString((long) elapsedMs);
        }
        objArr[2] = str;
        objArr[3] = Long.valueOf(bitrateEstimate);
        Logger.m173d(str2, objArr);
    }

    public void onDroppedFrames(int count, long elapsed) {
        String str = "droppedFrames [%s, %d]";
        Object[] objArr = new Object[2];
        objArr[0] = this.isLogEnabled ? getSessionTimeString() : null;
        objArr[1] = Integer.valueOf(count);
        Logger.m173d(str, objArr);
    }

    public void onLoadStarted(int sourceId, String formatId, int trigger, boolean isInitialization, int mediaStartTimeMs, int mediaEndTimeMs, long length) {
        this.loadStartTimeMs[sourceId] = SystemClock.elapsedRealtime();
        String str = "loadStart [%s, %d, %d, %d]";
        Object[] objArr = new Object[4];
        objArr[0] = this.isLogEnabled ? getSessionTimeString() : null;
        objArr[1] = Integer.valueOf(sourceId);
        objArr[2] = Integer.valueOf(mediaStartTimeMs);
        objArr[3] = Integer.valueOf(mediaEndTimeMs);
        Logger.m173d(str, objArr);
    }

    public void onLoadCompleted(int sourceId, long bytesLoaded) {
        long downloadTime = SystemClock.elapsedRealtime() - this.loadStartTimeMs[sourceId];
        String str = "loadEnd [%s, %d, %d, %d]";
        Object[] objArr = new Object[4];
        objArr[0] = this.isLogEnabled ? getSessionTimeString() : null;
        objArr[1] = Integer.valueOf(sourceId);
        objArr[2] = Long.valueOf(downloadTime);
        objArr[3] = Long.valueOf(bytesLoaded);
        Logger.m173d(str, objArr);
    }

    public void onVideoFormatEnabled(String formatId, int trigger, int mediaTimeMs) {
        String str = "videoFormat [%s, %s, %d]";
        Object[] objArr = new Object[3];
        objArr[0] = this.isLogEnabled ? getSessionTimeString() : null;
        objArr[1] = formatId;
        objArr[2] = Integer.valueOf(trigger);
        Logger.m173d(str, objArr);
    }

    public void onAudioFormatEnabled(String formatId, int trigger, int mediaTimeMs) {
        String str = "audioFormat [%s, %s, %d]";
        Object[] objArr = new Object[3];
        objArr[0] = this.isLogEnabled ? getSessionTimeString() : null;
        objArr[1] = formatId;
        objArr[2] = Integer.valueOf(trigger);
        Logger.m173d(str, objArr);
    }

    public void onUpstreamError(int sourceId, IOException e) {
        printInternalError("upstreamError", e);
    }

    public void onConsumptionError(int sourceId, IOException e) {
        printInternalError("consumptionError", e);
    }

    public void onRendererInitializationError(Exception e) {
        printInternalError("rendererInitError", e);
    }

    public void onDrmSessionManagerError(Exception e) {
        printInternalError("drmSessionManagerError", e);
    }

    public void onDecoderInitializationError(DecoderInitializationException e) {
        printInternalError("decoderInitializationError", e);
    }

    public void onAudioTrackInitializationError(InitializationException e) {
        printInternalError("audioTrackInitializationError", e);
    }

    public void onAudioTrackWriteError(WriteException e) {
        printInternalError("audioTrackWriteError", e);
    }

    public void onCryptoError(CryptoException e) {
        printInternalError("cryptoError", e);
    }

    private void printInternalError(String type, Exception e) {
        String str = "internalError [%s, %s]: %s";
        Object[] objArr = new Object[3];
        objArr[0] = this.isLogEnabled ? getSessionTimeString() : null;
        objArr[1] = type;
        objArr[2] = e;
        Logger.m180e(e, str, objArr);
    }

    private String getStateString(int state) {
        switch (state) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return "I";
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return "P";
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return "B";
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return "R";
            case Message.UUID_FIELD_NUMBER /*5*/:
                return "E";
            default:
                return "?";
        }
    }

    private String getSessionTimeString() {
        return getTimeString(SystemClock.elapsedRealtime() - this.sessionStartTimeMs);
    }

    private String getTimeString(long timeMs) {
        return TIME_FORMAT.format((double) (((float) timeMs) / 1000.0f));
    }
}
