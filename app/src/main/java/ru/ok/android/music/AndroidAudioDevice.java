package ru.ok.android.music;

import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.os.Build.VERSION;
import com.google.android.gms.location.LocationStatusCodes;
import ru.ok.android.utils.Logger;

public class AndroidAudioDevice implements OnPlaybackPositionUpdateListener {
    private FinishCallBack finishCallBack;
    private int prevPosition;
    private ProgressCallBack progressCallBack;
    private volatile AudioTrack track;

    public interface FinishCallBack {
        void onFinish(AndroidAudioDevice androidAudioDevice, int i);
    }

    public class InitializationAudioException extends Exception {
        InitializationAudioException() {
        }

        InitializationAudioException(String msg) {
            super(msg);
        }
    }

    public interface ProgressCallBack {
        void onProgress(AndroidAudioDevice androidAudioDevice, int i);
    }

    public AndroidAudioDevice() throws InitializationAudioException {
        this.track = createTrack();
    }

    public AndroidAudioDevice(int rate, int channelsFormat, int channelsCount) throws InitializationAudioException {
        this.track = createTrack(rate, channelsFormat, channelsCount);
    }

    private AudioTrack createTrack() throws InitializationAudioException {
        return createTrack(44100, 12, 2);
    }

    private AudioTrack createTrack(int sampleRate, int channelsFormat, int channelsCount) throws InitializationAudioException {
        int minSize = AudioTrack.getMinBufferSize(sampleRate, channelsFormat, 2);
        if (minSize == -2 || minSize == -1) {
            throw new InitializationAudioException("GetMinBufferSize error: " + minSize);
        }
        int i;
        if (VERSION.SDK_INT < 16) {
            minSize *= 10;
        }
        if (4 == channelsFormat) {
            i = 1;
        } else {
            i = 2;
        }
        AudioTrack track = new AudioTrack(3, sampleRate, channelsFormat, 2, Math.max(minSize, Math.min(((i * 2) * sampleRate) / 2, 204800)), 1);
        if (track.getState() != 1) {
            Logger.m172d("Unable to create audio track.");
            track.release();
            throw new InitializationAudioException();
        }
        track.setPlaybackRate((sampleRate / 2) * channelsCount);
        this.prevPosition = -1;
        Logger.m173d("create audio track sampleRate: %d, chFromat: %d, chCount: %d buffer %d", Integer.valueOf(sampleRate), Integer.valueOf(channelsFormat), Integer.valueOf(channelsCount), Integer.valueOf(bufferSize));
        return track;
    }

    public void writeSamples(byte[] samples, int offset, int size) {
        if (this.track != null && this.track.getPlayState() == 3) {
            this.track.write(samples, offset, size);
        }
    }

    public void setFinishCallBack(FinishCallBack finishCallBack) {
        this.finishCallBack = finishCallBack;
    }

    public void setUpdateProgressCallBack(ProgressCallBack progressCallBack) {
        this.prevPosition = -1;
        this.progressCallBack = progressCallBack;
    }

    public void setFinishCallBackPosition(int position) {
        if (this.track != null) {
            this.track.setNotificationMarkerPosition(position);
            this.track.setPlaybackPositionUpdateListener(this);
        }
    }

    public void setProgressPeriod(int period) {
        if (this.track != null) {
            this.track.setPositionNotificationPeriod(period);
            this.track.setPlaybackPositionUpdateListener(this);
        }
    }

    public void clearFinishCallBack() {
        this.finishCallBack = null;
    }

    public int getPosition() {
        if (this.track == null || this.track.getState() != 1) {
            return 0;
        }
        if (Logger.isLoggingEnable()) {
            Logger.m172d("Audio track size = " + this.track.getNotificationMarkerPosition() + "  " + ((this.track.getPlaybackHeadPosition() / this.track.getSampleRate()) * LocationStatusCodes.GEOFENCE_NOT_AVAILABLE) + "  " + this.track.getPlaybackHeadPosition());
        }
        return this.track.getPlaybackHeadPosition() / this.track.getSampleRate();
    }

    public int getSampleRate() {
        if (this.track == null) {
            return 44100;
        }
        return this.track.getSampleRate();
    }

    public int getChannelsConfiguration() {
        if (this.track == null) {
            return 1;
        }
        return this.track.getChannelConfiguration();
    }

    public int getChannelsCount() {
        return this.track.getChannelCount();
    }

    public boolean pause() {
        if (this.track == null || this.track.getState() == 0) {
            return false;
        }
        this.track.pause();
        return true;
    }

    public boolean stop() {
        if (this.track == null || this.track.getState() == 0) {
            return false;
        }
        try {
            this.track.stop();
            return true;
        } catch (Throwable e) {
            Logger.m178e(e);
            return false;
        }
    }

    public void release() {
        if (this.track != null) {
            this.track.release();
        }
    }

    public boolean play() {
        if (this.track == null || this.track.getState() == 0) {
            return false;
        }
        try {
            this.track.play();
            return true;
        } catch (Throwable e) {
            Logger.m178e(e);
            return false;
        }
    }

    public boolean isPlaying() {
        return this.track != null && this.track.getPlayState() == 3;
    }

    public boolean isPausing() {
        return this.track != null && this.track.getPlayState() == 2;
    }

    public void clearBuffer() {
        if (this.track != null) {
            this.track.flush();
        }
    }

    public void onMarkerReached(AudioTrack audioTrack) {
        if (this.finishCallBack != null) {
            this.finishCallBack.onFinish(this, this.track == null ? 0 : this.track.getNotificationMarkerPosition());
        }
    }

    public void onPeriodicNotification(AudioTrack audioTrack) {
        int position = getPosition();
        if (this.progressCallBack != null && position != this.prevPosition) {
            Logger.m173d("progress: %d", Integer.valueOf(position));
            this.prevPosition = position;
            this.progressCallBack.onProgress(this, position);
        }
    }
}
