package ru.ok.android.services.processors.audio;

import android.content.Context;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import ru.ok.android.utils.AudioPlaybackController;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Storage.External.Application;

public class AudioRecorder {
    private static AudioRecorder instance;
    private List<Integer> audioWave;
    private boolean audioWaveChanged;
    private List<Integer> audioWaveDisplayed;
    private Handler handler;
    private long recMeasureOffset;
    private String recordedFilePath;
    private MediaRecorder recorder;
    private int recordingSample;
    private int recordingSampleIdx;
    private int skipSamplesRate;
    private long startTime;
    int timerInterval;
    private Runnable waveUpdateRunnable;

    /* renamed from: ru.ok.android.services.processors.audio.AudioRecorder.1 */
    class C04501 implements Runnable {
        C04501() {
        }

        public void run() {
            if (AudioRecorder.this.recorder == null) {
                AudioRecorder.this.handler.removeCallbacks(this);
                return;
            }
            double db;
            int maxAmplitude = Math.abs(AudioRecorder.this.recorder.getMaxAmplitude());
            if (maxAmplitude == 0) {
                db = -45.0d;
            } else {
                db = 18.0d * Math.log10(((double) maxAmplitude) / 32768.0d);
            }
            if (db < -45.0d) {
                db = -45.0d;
            }
            Logger.m172d("max atr value = " + maxAmplitude + " db " + db);
            if (AudioRecorder.this.audioWave == null || AudioRecorder.this.audioWaveDisplayed == null) {
                AudioRecorder.this.audioWave = new ArrayList(100);
                AudioRecorder.this.audioWaveDisplayed = new ArrayList(100);
            }
            int power = (int) (((45.0d + db) * 32768.0d) / 45.0d);
            AudioRecorder.this.audioWaveDisplayed.add(Integer.valueOf(power));
            if (AudioRecorder.this.audioWaveDisplayed.size() >= 2000) {
                AudioRecorder.this.audioWaveDisplayed.remove(0);
            }
            AudioRecorder.this.audioWaveChanged = true;
            AudioRecorder.this.recordingSample = Math.max(power, AudioRecorder.this.recordingSample);
            AudioRecorder.access$604(AudioRecorder.this);
            if (AudioRecorder.this.recordingSampleIdx % AudioRecorder.this.skipSamplesRate == 0) {
                AudioRecorder.this.audioWave.add(Integer.valueOf(AudioRecorder.this.recordingSample));
                AudioRecorder.this.recordingSample = 0;
                if (AudioRecorder.this.audioWave.size() >= 100) {
                    for (int i = 0; i < 50; i++) {
                        AudioRecorder.this.audioWave.set(i, Integer.valueOf(Math.max(((Integer) AudioRecorder.this.audioWave.get(i * 2)).intValue(), ((Integer) AudioRecorder.this.audioWave.get((i * 2) + 1)).intValue())));
                    }
                    AudioRecorder.this.audioWave.subList(50, AudioRecorder.this.audioWave.size()).clear();
                    AudioRecorder.access$728(AudioRecorder.this, 2);
                }
            }
            AudioRecorder.access$814(AudioRecorder.this, (long) AudioRecorder.this.timerInterval);
            AudioRecorder.this.handler.postAtTime(this, AudioRecorder.this.startTime + AudioRecorder.this.recMeasureOffset);
        }
    }

    class MediaRecorderCallback implements OnErrorListener, OnInfoListener {
        private final RecordingCallback callback;

        public MediaRecorderCallback(RecordingCallback callback) {
            this.callback = callback;
        }

        public void onError(MediaRecorder mr, int what, int extra) {
            Logger.m184w("Media recorder error: what=" + what + "; extra=" + extra);
            this.callback.onError();
        }

        public void onInfo(MediaRecorder mr, int what, int extra) {
            if (what == 800) {
                this.callback.onDone();
            }
        }
    }

    public interface RecordingCallback {
        void onDone();

        void onError();
    }

    static /* synthetic */ int access$604(AudioRecorder x0) {
        int i = x0.recordingSampleIdx + 1;
        x0.recordingSampleIdx = i;
        return i;
    }

    static /* synthetic */ int access$728(AudioRecorder x0, int x1) {
        int i = x0.skipSamplesRate * x1;
        x0.skipSamplesRate = i;
        return i;
    }

    static /* synthetic */ long access$814(AudioRecorder x0, long x1) {
        long j = x0.recMeasureOffset + x1;
        x0.recMeasureOffset = j;
        return j;
    }

    private AudioRecorder() {
        this.handler = new Handler();
        this.timerInterval = 100;
        this.audioWaveChanged = false;
        this.skipSamplesRate = 1;
        this.waveUpdateRunnable = new C04501();
    }

    public static synchronized AudioRecorder instance() {
        AudioRecorder audioRecorder;
        synchronized (AudioRecorder.class) {
            if (instance == null) {
                instance = new AudioRecorder();
            }
            audioRecorder = instance;
        }
        return audioRecorder;
    }

    public boolean startRecording(Context context, int maxDuration, RecordingCallback callback) {
        return startRecording(context, 2, maxDuration, callback) || startRecording(context, 0, maxDuration, callback);
    }

    private boolean startRecording(Context context, int audioEncoderType, int maxDuration, RecordingCallback callback) {
        stopRecording();
        if (this.audioWave != null) {
            this.audioWave.clear();
        }
        if (this.audioWaveDisplayed != null) {
            this.audioWaveDisplayed.clear();
        }
        this.recordingSampleIdx = 0;
        this.skipSamplesRate = 1;
        this.recordingSample = 0;
        AudioPlaybackController.pausePlayback();
        this.recorder = new MediaRecorder();
        if (prepareRecording(context, audioEncoderType, this.recorder, maxDuration, callback)) {
            this.timerInterval = 100;
            this.recMeasureOffset = -200;
            Logger.m172d("OkRecorder : Start recording");
            try {
                this.recorder.start();
                fixStartRecordingTime();
                this.handler.postDelayed(this.waveUpdateRunnable, (long) this.timerInterval);
                return true;
            } catch (Throwable ex) {
                Logger.m179e(ex, "Error start audio recording");
                this.recorder.release();
                this.recorder = null;
                return false;
            }
        }
        Logger.m172d("OkRecorder : Error start recording");
        this.recorder.release();
        this.recorder = null;
        return false;
    }

    public boolean stopRecording() {
        boolean z = false;
        this.handler.removeCallbacks(this.waveUpdateRunnable);
        if (this.recorder == null) {
            return false;
        }
        try {
            this.audioWaveDisplayed = null;
            Logger.m172d("OkRecorder : Stop recording");
            this.recorder.stop();
            this.recorder.reset();
            z = true;
        } catch (Throwable ex) {
            Logger.m179e(ex, "Error stop audio recording");
        } finally {
            this.recorder.release();
            this.recorder = null;
        }
        return z;
    }

    public String getRecorderFilePath() {
        return this.recordedFilePath;
    }

    private boolean prepareRecording(Context context, int audioEncoderType, MediaRecorder recorder, int maxDuration, RecordingCallback callback) {
        this.recordedFilePath = createRecorderFilePath(context);
        recorder.setAudioSource(6);
        recorder.setOutputFormat(2);
        recorder.setOutputFile(this.recordedFilePath);
        recorder.setAudioEncoder(audioEncoderType);
        recorder.setMaxDuration(maxDuration);
        MediaRecorderCallback facadeCallback = new MediaRecorderCallback(callback);
        recorder.setOnErrorListener(facadeCallback);
        recorder.setOnInfoListener(facadeCallback);
        try {
            recorder.prepare();
            Logger.m172d("OkRecorder : Prepare recording");
            return true;
        } catch (Throwable ex) {
            Logger.m179e(ex, "Prepare recorder exception:");
            return false;
        }
    }

    public boolean isRecording() {
        return this.recorder != null;
    }

    private String createRecorderFilePath(Context context) {
        File dir;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            dir = Application.getFilesDir(context);
        } else {
            dir = context.getDir("tmp_recordings", 0);
        }
        return new File(dir, UUID.randomUUID().toString() + ".m4a").getAbsolutePath();
    }

    private void fixStartRecordingTime() {
        this.startTime = SystemClock.uptimeMillis();
    }

    public byte[] getAudioWave() {
        return scaleWave(this.audioWave);
    }

    public byte[] getAudioWaveDisplayed() {
        return scaleWave(this.audioWaveDisplayed);
    }

    private byte[] scaleWave(List<Integer> wave) {
        if (wave == null || wave.isEmpty()) {
            return null;
        }
        byte[] result = new byte[wave.size()];
        int maxValue = 0;
        for (Integer val : wave) {
            if (val.intValue() > maxValue) {
                maxValue = val.intValue();
            }
        }
        if (maxValue < 16386) {
            maxValue = 16386;
        }
        int i = 0;
        for (Integer val2 : wave) {
            result[i] = (byte) ((val2.intValue() * 127) / maxValue);
            i++;
        }
        return result;
    }

    public boolean isAudioWaveChanged() {
        return this.audioWaveChanged;
    }

    public long getRecordingDuration() {
        return SystemClock.uptimeMillis() - this.startTime;
    }
}
