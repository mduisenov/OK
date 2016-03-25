package ru.ok.android.music;

import android.content.Context;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Pair;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.model.cache.music.async.MusicAsyncFileCache;
import ru.ok.android.music.AndroidAudioDevice.FinishCallBack;
import ru.ok.android.music.AndroidAudioDevice.InitializationAudioException;
import ru.ok.android.music.AndroidAudioDevice.ProgressCallBack;
import ru.ok.android.music.codec.MP3FileDecoder;
import ru.ok.android.music.data.BufferedMusicFile;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.ThreadUtil;

public class StreamMediaPlayer implements FinishCallBack, ProgressCallBack {
    private static AtomicInteger instances;
    private AndroidAudioDevice audioDevice;
    private volatile boolean autoPlay;
    private BufferedMusicFile bufferedFile;
    private long byteToSeek;
    private PlayerCallBack callBack;
    private Context context;
    private int dataPos;
    private Mp3Streamer downloadContentThread;
    private File downloadingMediaFile;
    private long duration;
    private volatile long mediaLength;
    private String mediaUrl;
    private long mp3Size;
    private MusicBuffer musicBufferExecutor;
    private long pcmSize;
    private volatile boolean skipTrack;
    private volatile long startRead;
    private volatile State state;
    private volatile long totalRead;

    /* renamed from: ru.ok.android.music.StreamMediaPlayer.1 */
    class C03791 implements Runnable {
        final /* synthetic */ int val$code;
        final /* synthetic */ long val$errorPosition;

        C03791(long j, int i) {
            this.val$errorPosition = j;
            this.val$code = i;
        }

        public void run() {
            StreamMediaPlayer.this.callBack.onDownloadError(this.val$errorPosition, this.val$code);
        }
    }

    public enum DownloadState {
        Idle,
        Prepare,
        Download,
        Wait,
        Finish,
        Error,
        Interrupt
    }

    private class UpdateFileRunnable implements Runnable {
        protected File bufferedFile;
        protected volatile boolean interrupted;
        private volatile WaitLock lock;
        private OnFinishTaskListener onFinishTaskListener;
        boolean startNow;

        public UpdateFileRunnable(boolean start) {
            this.interrupted = false;
            this.lock = new WaitLock(null);
            this.startNow = false;
            this.bufferedFile = new File(MusicUtils.getCache(StreamMediaPlayer.this.context), "music_buffer");
            this.startNow = start;
        }

        protected void onWriteSample(int size, long psmTotalSize) {
            if (this.startNow && size > 0) {
                StreamMediaPlayer.this.play();
                this.startNow = false;
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        protected void decodeStream() {
            /*
            r14 = this;
            r2 = new ru.ok.android.music.codec.MP3FileDecoder;
            r6 = r14.bufferedFile;
            r6 = r6.getAbsolutePath();
            r7 = ru.ok.android.music.StreamMediaPlayer.this;
            r8 = r7.mp3Size;
            r7 = ru.ok.android.music.StreamMediaPlayer.this;
            r7 = r7.dataPos;
            r10 = (long) r7;
            r8 = java.lang.Math.max(r8, r10);
            r2.<init>(r6, r8);
            r4 = java.lang.System.currentTimeMillis();	 Catch:{ InterruptedException -> 0x0057 }
        L_0x0020:
            r6 = r14.interrupted;	 Catch:{ InterruptedException -> 0x0057 }
            if (r6 != 0) goto L_0x00bb;
        L_0x0024:
            r6 = 6144; // 0x1800 float:8.61E-42 double:3.0355E-320;
            r3 = r2.readSamples(r6);	 Catch:{ InterruptedException -> 0x0057 }
            if (r3 <= 0) goto L_0x0061;
        L_0x002c:
            r0 = r2.getBytesArray(r3);	 Catch:{ InterruptedException -> 0x0057 }
            r6 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ InterruptedException -> 0x0057 }
            r6 = r6.audioDevice;	 Catch:{ InterruptedException -> 0x0057 }
            r7 = r2.getByteOffset();	 Catch:{ InterruptedException -> 0x0057 }
            r6.writeSamples(r0, r7, r3);	 Catch:{ InterruptedException -> 0x0057 }
            r6 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ InterruptedException -> 0x0057 }
            r7 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ InterruptedException -> 0x0057 }
            r8 = r7.pcmSize;	 Catch:{ InterruptedException -> 0x0057 }
            r10 = (long) r3;	 Catch:{ InterruptedException -> 0x0057 }
            r8 = r8 + r10;
            r6.pcmSize = r8;	 Catch:{ InterruptedException -> 0x0057 }
            r6 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ InterruptedException -> 0x0057 }
            r6 = r6.pcmSize;	 Catch:{ InterruptedException -> 0x0057 }
            r14.onWriteSample(r3, r6);	 Catch:{ InterruptedException -> 0x0057 }
            r14.checkPaused();	 Catch:{ InterruptedException -> 0x0057 }
            goto L_0x0020;
        L_0x0057:
            r1 = move-exception;
            r6 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x00d0 }
            r6.interrupt();	 Catch:{ all -> 0x00d0 }
            r2.dispose();
        L_0x0060:
            return;
        L_0x0061:
            r6 = "EOF buffer. URL: %s Duration: %d Actual duration: %d Read ratio: %f Play ratio: %f";
            r7 = 5;
            r7 = new java.lang.Object[r7];	 Catch:{ InterruptedException -> 0x0057 }
            r8 = 0;
            r9 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ InterruptedException -> 0x0057 }
            r9 = r9.mediaUrl;	 Catch:{ InterruptedException -> 0x0057 }
            r7[r8] = r9;	 Catch:{ InterruptedException -> 0x0057 }
            r8 = 1;
            r9 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ InterruptedException -> 0x0057 }
            r10 = r9.duration;	 Catch:{ InterruptedException -> 0x0057 }
            r9 = java.lang.Long.valueOf(r10);	 Catch:{ InterruptedException -> 0x0057 }
            r7[r8] = r9;	 Catch:{ InterruptedException -> 0x0057 }
            r8 = 2;
            r10 = java.lang.System.currentTimeMillis();	 Catch:{ InterruptedException -> 0x0057 }
            r10 = r10 - r4;
            r12 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            r10 = r10 / r12;
            r9 = java.lang.Long.valueOf(r10);	 Catch:{ InterruptedException -> 0x0057 }
            r7[r8] = r9;	 Catch:{ InterruptedException -> 0x0057 }
            r8 = 3;
            r9 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ InterruptedException -> 0x0057 }
            r10 = r9.totalRead;	 Catch:{ InterruptedException -> 0x0057 }
            r9 = (float) r10;	 Catch:{ InterruptedException -> 0x0057 }
            r10 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ InterruptedException -> 0x0057 }
            r10 = r10.mediaLength;	 Catch:{ InterruptedException -> 0x0057 }
            r10 = (float) r10;	 Catch:{ InterruptedException -> 0x0057 }
            r9 = r9 / r10;
            r9 = java.lang.Float.valueOf(r9);	 Catch:{ InterruptedException -> 0x0057 }
            r7[r8] = r9;	 Catch:{ InterruptedException -> 0x0057 }
            r8 = 4;
            r9 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ InterruptedException -> 0x0057 }
            r10 = r9.pcmSize;	 Catch:{ InterruptedException -> 0x0057 }
            r9 = (float) r10;	 Catch:{ InterruptedException -> 0x0057 }
            r10 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ InterruptedException -> 0x0057 }
            r10 = r10.mediaLength;	 Catch:{ InterruptedException -> 0x0057 }
            r10 = (float) r10;	 Catch:{ InterruptedException -> 0x0057 }
            r9 = r9 / r10;
            r9 = java.lang.Float.valueOf(r9);	 Catch:{ InterruptedException -> 0x0057 }
            r7[r8] = r9;	 Catch:{ InterruptedException -> 0x0057 }
            ru.ok.android.utils.Logger.m173d(r6, r7);	 Catch:{ InterruptedException -> 0x0057 }
        L_0x00bb:
            r6 = r14.interrupted;	 Catch:{ InterruptedException -> 0x0057 }
            if (r6 != 0) goto L_0x00cc;
        L_0x00bf:
            r6 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ InterruptedException -> 0x0057 }
            r7 = r14.bufferedFile;	 Catch:{ InterruptedException -> 0x0057 }
            r8 = r7.length();	 Catch:{ InterruptedException -> 0x0057 }
            r7 = (int) r8;	 Catch:{ InterruptedException -> 0x0057 }
            r8 = (long) r7;	 Catch:{ InterruptedException -> 0x0057 }
            r6.mp3Size = r8;	 Catch:{ InterruptedException -> 0x0057 }
        L_0x00cc:
            r2.dispose();
            goto L_0x0060;
        L_0x00d0:
            r6 = move-exception;
            r2.dispose();
            throw r6;
            */
            throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.music.StreamMediaPlayer.UpdateFileRunnable.decodeStream():void");
        }

        private void checkPaused() throws InterruptedException {
            if (!StreamMediaPlayer.this.audioDevice.isPlaying()) {
                this.lock.lockThread();
            }
        }

        public boolean isWaitingPlay() {
            return this.lock.isWaiting();
        }

        public void resumeThread() {
            this.lock.unLockThread();
        }

        public void run() {
            if (this.bufferedFile.exists()) {
                if (!this.interrupted) {
                    decodeStream();
                }
                Logger.m173d("Buffering frame mp3 sise = %d, interrupted: %s", Long.valueOf(StreamMediaPlayer.this.mp3Size), Boolean.valueOf(this.interrupted));
            }
            notifyFinish();
        }

        public synchronized void interruptTask() {
            this.interrupted = true;
            if (isWaitingPlay()) {
                resumeThread();
            }
        }

        private void notifyFinish() {
            if (this.onFinishTaskListener != null) {
                this.onFinishTaskListener.onFinishTask(this);
            }
        }

        public void setOnFinishTaskListener(OnFinishTaskListener listener) {
            this.onFinishTaskListener = listener;
        }
    }

    private class FinishUpdateFileRunnable extends UpdateFileRunnable {
        public FinishUpdateFileRunnable(boolean start) {
            super(start);
        }

        protected void decodeStream() {
            super.decodeStream();
            if (!this.interrupted) {
                if (StreamMediaPlayer.this.skipTrack) {
                    StreamMediaPlayer.this.onFinish(StreamMediaPlayer.this.audioDevice, 0);
                } else {
                    setFinishEvent();
                }
            }
        }

        private void setFinishEvent() {
            StreamMediaPlayer.this.audioDevice.setFinishCallBackPosition(((int) ((StreamMediaPlayer.this.pcmSize * PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / ((long) StreamMediaPlayer.this.audioDevice.getSampleRate()))) - 1);
        }
    }

    class Mp3Info {
        int bitRate;
        int channelsCount;
        int channelsFormat;
        int frameSize;
        int sampleRate;

        Mp3Info(int sampleRate, int bitRate, int channelsFormat, int channelsCount, int frameSize) {
            this.sampleRate = 44100;
            this.sampleRate = sampleRate;
            this.bitRate = bitRate;
            this.channelsFormat = channelsFormat;
            this.channelsCount = channelsCount;
            this.frameSize = frameSize;
        }
    }

    class Mp3Streamer extends Thread {
        private final BufferedMusicFile bufferedFile;
        private final int instance;
        private final Object lock;
        private final String mediaUrl;
        private final long startOffset;
        private volatile DownloadState streamState;

        Mp3Streamer(String mediaUrl, BufferedMusicFile buffer, long startOffset) {
            super("StreamMediaPlayer-download");
            this.lock = new Object();
            this.streamState = DownloadState.Idle;
            this.startOffset = startOffset;
            this.mediaUrl = mediaUrl;
            this.bufferedFile = buffer;
            this.instance = StreamMediaPlayer.instances.getAndIncrement();
        }

        public void run() {
            downloadAudioData(this.mediaUrl, this.bufferedFile, this.startOffset);
        }

        private boolean setLock() {
            try {
                Logger.m173d("(%d)", Integer.valueOf(this.instance));
                synchronized (this.lock) {
                    setStreamState(DownloadState.Wait);
                    this.lock.wait();
                }
                return true;
            } catch (InterruptedException e) {
                return false;
            }
        }

        public void resumeDownload() {
            Logger.m173d("(%d)", Integer.valueOf(this.instance));
            synchronized (this.lock) {
                this.lock.notify();
            }
        }

        public boolean isWait() {
            return this.streamState == DownloadState.Wait;
        }

        public void shutdown() {
            setStreamState(DownloadState.Interrupt);
        }

        public boolean isCompleted() {
            return this.streamState == DownloadState.Finish || this.streamState == DownloadState.Error || this.streamState == DownloadState.Error;
        }

        protected void setStreamState(DownloadState streamState) {
            synchronized (Mp3Streamer.class) {
                Logger.m173d("(%d) current state: %s, new state: %s", Integer.valueOf(this.instance), this.streamState, streamState);
                if (this.streamState != DownloadState.Interrupt || streamState == DownloadState.Error) {
                    this.streamState = streamState;
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void downloadAudioData(java.lang.String r32, ru.ok.android.music.data.BufferedMusicFile r33, long r34) {
            /*
            r31 = this;
            r5 = "(%d) url: %d, file: %s, offset: %d";
            r6 = 4;
            r6 = new java.lang.Object[r6];
            r7 = 0;
            r0 = r31;
            r8 = r0.instance;
            r8 = java.lang.Integer.valueOf(r8);
            r6[r7] = r8;
            r7 = 1;
            r6[r7] = r32;
            r7 = 2;
            r6[r7] = r33;
            r7 = 3;
            r8 = java.lang.Long.valueOf(r34);
            r6[r7] = r8;
            ru.ok.android.utils.Logger.m173d(r5, r6);
            r5 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Prepare;
            r0 = r31;
            r0.setStreamState(r5);
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;
            r6 = 0;
            r5.totalRead = r6;
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;
            r6 = 0;
            r5.mp3Size = r6;
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;
            r6 = 0;
            r5.pcmSize = r6;
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;
            r6 = 0;
            r5.dataPos = r6;
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;
            r6 = 0;
            r5.skipTrack = r6;
            r5 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
            r0 = new byte[r5];
            r17 = r0;
            r5 = 10;
            r0 = new byte[r5];
            r24 = r0;
            r21 = -1;
            r10 = new java.util.concurrent.atomic.AtomicReference;
            r5 = java.lang.Boolean.FALSE;
            r10.<init>(r5);
            r27 = 0;
            r4 = new ru.ok.android.music.DownloadAbortCondition;
            r4.<init>();
        L_0x006f:
            if (r27 != 0) goto L_0x00aa;
        L_0x0071:
            r4.newAttempt();	 Catch:{ IOException -> 0x008a }
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x008a }
            r11 = r4.getReadTimeout();	 Catch:{ IOException -> 0x008a }
            r6 = r32;
            r7 = r33;
            r8 = r34;
            r27 = r5.initInputStream(r6, r7, r8, r10, r11);	 Catch:{ IOException -> 0x008a }
            r4.reset();	 Catch:{ IOException -> 0x008a }
            goto L_0x006f;
        L_0x008a:
            r23 = move-exception;
            r5 = r4.isTrue();
            if (r5 == 0) goto L_0x0098;
        L_0x0091:
            r5 = 3;
            r0 = r31;
            r0.unRecoverableError(r5);
        L_0x0097:
            return;
        L_0x0098:
            r5 = "Early connection exception. Lock until connection available.";
            ru.ok.android.utils.Logger.m172d(r5);
            r0 = r23;
            r5 = r0 instanceof java.net.ConnectException;
            if (r5 == 0) goto L_0x006f;
        L_0x00a4:
            r5 = r31.setLock();
            if (r5 != 0) goto L_0x006f;
        L_0x00aa:
            if (r27 == 0) goto L_0x013a;
        L_0x00ac:
            r0 = r27;
            r0 = r0.first;
            r29 = r0;
            r29 = (java.io.InputStream) r29;
            r0 = r31;
            r6 = ru.ok.android.music.StreamMediaPlayer.this;
            r0 = r27;
            r5 = r0.second;
            r5 = (java.lang.Long) r5;
            r8 = r5.longValue();
            r8 = r8 + r34;
            r6.mediaLength = r8;
            r5 = "(%d) stream opened: ";
            r6 = 3;
            r6 = new java.lang.Object[r6];
            r7 = 0;
            r0 = r31;
            r8 = r0.instance;
            r8 = java.lang.Integer.valueOf(r8);
            r6[r7] = r8;
            r7 = 1;
            r6[r7] = r29;
            r7 = 2;
            r0 = r31;
            r8 = ru.ok.android.music.StreamMediaPlayer.this;
            r8 = r8.mediaLength;
            r8 = java.lang.Long.valueOf(r8);
            r6[r7] = r8;
            ru.ok.android.utils.Logger.m173d(r5, r6);
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;
            r0 = r34;
            r5.startRead = r0;
            r5 = "(%d) open output stream...";
            r6 = 1;
            r6 = new java.lang.Object[r6];
            r7 = 0;
            r0 = r31;
            r8 = r0.instance;
            r8 = java.lang.Integer.valueOf(r8);
            r6[r7] = r8;
            ru.ok.android.utils.Logger.m173d(r5, r6);
            r6 = ru.ok.android.music.StreamMediaPlayer.Mp3Streamer.class;
            monitor-enter(r6);
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x0156 }
            r7 = "music_buffer";
            r26 = r5.initOutStream(r7);	 Catch:{ all -> 0x0156 }
            monitor-exit(r6);	 Catch:{ all -> 0x0156 }
            if (r26 != 0) goto L_0x0159;
        L_0x011b:
            r5 = "(%d) output stream not opened";
            r6 = 1;
            r6 = new java.lang.Object[r6];
            r7 = 0;
            r0 = r31;
            r8 = r0.instance;
            r8 = java.lang.Integer.valueOf(r8);
            r6[r7] = r8;
            ru.ok.android.utils.Logger.m185w(r5, r6);
            r5 = 1;
            r0 = r31;
            r0.unRecoverableError(r5);
            ru.ok.android.utils.IOUtils.closeSilently(r29);
            goto L_0x0097;
        L_0x013a:
            r5 = "(%d) failed to open stream";
            r6 = 1;
            r6 = new java.lang.Object[r6];
            r7 = 0;
            r0 = r31;
            r8 = r0.instance;
            r8 = java.lang.Integer.valueOf(r8);
            r6[r7] = r8;
            ru.ok.android.utils.Logger.m185w(r5, r6);
            r5 = 1;
            r0 = r31;
            r0.unRecoverableError(r5);
            goto L_0x0097;
        L_0x0156:
            r5 = move-exception;
            monitor-exit(r6);	 Catch:{ all -> 0x0156 }
            throw r5;
        L_0x0159:
            r5 = "(%d) output stream opened";
            r6 = 1;
            r6 = new java.lang.Object[r6];
            r7 = 0;
            r0 = r31;
            r8 = r0.instance;
            r8 = java.lang.Integer.valueOf(r8);
            r6[r7] = r8;
            ru.ok.android.utils.Logger.m173d(r5, r6);
            r12 = new java.io.BufferedInputStream;
            r0 = r29;
            r12.<init>(r0);
            r18 = 0;
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x03bd }
            r6 = ru.ok.android.music.StreamMediaPlayer.State.Buffering;	 Catch:{ all -> 0x03bd }
            r5.setState(r6);	 Catch:{ all -> 0x03bd }
            r19 = r18;
        L_0x0181:
            r0 = r31;
            r5 = r0.streamState;	 Catch:{ all -> 0x04ed }
            r6 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Interrupt;	 Catch:{ all -> 0x04ed }
            if (r5 == r6) goto L_0x0215;
        L_0x0189:
            r0 = r31;
            r5 = r0.streamState;	 Catch:{ all -> 0x04ed }
            r6 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Error;	 Catch:{ all -> 0x04ed }
            if (r5 == r6) goto L_0x0215;
        L_0x0191:
            r0 = r31;
            r5 = r0.streamState;	 Catch:{ all -> 0x04ed }
            r6 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Finish;	 Catch:{ all -> 0x04ed }
            if (r5 == r6) goto L_0x0215;
        L_0x0199:
            r0 = r31;
            r5 = r0.streamState;	 Catch:{ all -> 0x04ed }
            r6 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Download;	 Catch:{ all -> 0x04ed }
            if (r5 == r6) goto L_0x01a8;
        L_0x01a1:
            r5 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Download;	 Catch:{ all -> 0x04ed }
            r0 = r31;
            r0.setStreamState(r5);	 Catch:{ all -> 0x04ed }
        L_0x01a8:
            r28 = 0;
            if (r12 == 0) goto L_0x01b8;
        L_0x01ac:
            r5 = 0;
            r6 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
            r0 = r17;
            r28 = r12.read(r0, r5, r6);	 Catch:{ IOException -> 0x01f5 }
            r4.reset();	 Catch:{ IOException -> 0x01f5 }
        L_0x01b8:
            if (r28 < 0) goto L_0x0406;
        L_0x01ba:
            r5 = -1;
            r0 = r21;
            if (r0 != r5) goto L_0x02e6;
        L_0x01bf:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x04ed }
            r6 = r5.totalRead;	 Catch:{ all -> 0x04ed }
            r0 = (int) r6;	 Catch:{ all -> 0x04ed }
            r25 = r0;
        L_0x01ca:
            r5 = 10;
            r0 = r25;
            if (r0 >= r5) goto L_0x02b1;
        L_0x01d0:
            r0 = r25;
            r6 = (long) r0;	 Catch:{ all -> 0x04ed }
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x04ed }
            r8 = r5.totalRead;	 Catch:{ all -> 0x04ed }
            r0 = r28;
            r14 = (long) r0;	 Catch:{ all -> 0x04ed }
            r8 = r8 + r14;
            r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r5 >= 0) goto L_0x02b1;
        L_0x01e3:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x04ed }
            r6 = r5.totalRead;	 Catch:{ all -> 0x04ed }
            r5 = (int) r6;	 Catch:{ all -> 0x04ed }
            r5 = r25 - r5;
            r5 = r17[r5];	 Catch:{ all -> 0x04ed }
            r24[r25] = r5;	 Catch:{ all -> 0x04ed }
            r25 = r25 + 1;
            goto L_0x01ca;
        L_0x01f5:
            r23 = move-exception;
            r4.newAttempt();	 Catch:{ IOException -> 0x0208 }
            r16 = r4.getReadTimeout();	 Catch:{ IOException -> 0x0208 }
            r11 = r31;
            r13 = r33;
            r14 = r34;
            r12 = r11.reopenInputStream(r12, r13, r14, r16);	 Catch:{ IOException -> 0x0208 }
            goto L_0x01b8;
        L_0x0208:
            r20 = move-exception;
            r5 = r4.isTrue();	 Catch:{ all -> 0x04ed }
            if (r5 == 0) goto L_0x029d;
        L_0x020f:
            r5 = 3;
            r0 = r31;
            r0.unRecoverableError(r5);	 Catch:{ all -> 0x04ed }
        L_0x0215:
            ru.ok.android.utils.IOUtils.closeSilently(r12);
            ru.ok.android.utils.IOUtils.closeSilently(r26);
            ru.ok.android.utils.IOUtils.closeSilently(r29);
            r0 = r31;
            r5 = r0.streamState;
            r6 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Finish;
            if (r5 != r6) goto L_0x0287;
        L_0x0226:
            r5 = "(%d) state is Finish";
            r6 = 1;
            r6 = new java.lang.Object[r6];
            r7 = 0;
            r0 = r31;
            r8 = r0.instance;
            r8 = java.lang.Integer.valueOf(r8);
            r6[r7] = r8;
            ru.ok.android.utils.Logger.m173d(r5, r6);
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;
            r6 = r5.totalRead;
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;
            r8 = r5.mediaLength;
            r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r5 != 0) goto L_0x0497;
        L_0x024e:
            r5 = r10.get();
            r6 = java.lang.Boolean.TRUE;
            if (r5 == r6) goto L_0x0287;
        L_0x0256:
            r6 = ru.ok.android.music.StreamMediaPlayer.Mp3Streamer.class;
            monitor-enter(r6);
            r0 = r31;
            r5 = r0.streamState;	 Catch:{ all -> 0x0494 }
            r7 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Interrupt;	 Catch:{ all -> 0x0494 }
            if (r5 == r7) goto L_0x0286;
        L_0x0261:
            r5 = "(%d) Save file to cache";
            r7 = 1;
            r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0494 }
            r8 = 0;
            r0 = r31;
            r9 = r0.instance;	 Catch:{ all -> 0x0494 }
            r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0494 }
            r7[r8] = r9;	 Catch:{ all -> 0x0494 }
            ru.ok.android.utils.Logger.m173d(r5, r7);	 Catch:{ all -> 0x0494 }
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x0494 }
            r0 = r31;
            r7 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x0494 }
            r7 = r7.downloadingMediaFile;	 Catch:{ all -> 0x0494 }
            r0 = r32;
            r5.notifySaveInBuffer(r0, r7);	 Catch:{ all -> 0x0494 }
        L_0x0286:
            monitor-exit(r6);	 Catch:{ all -> 0x0494 }
        L_0x0287:
            r5 = "(%d) Done";
            r6 = 1;
            r6 = new java.lang.Object[r6];
            r7 = 0;
            r0 = r31;
            r8 = r0.instance;
            r8 = java.lang.Integer.valueOf(r8);
            r6[r7] = r8;
            ru.ok.android.utils.Logger.m173d(r5, r6);
            goto L_0x0097;
        L_0x029d:
            r5 = "Connection exception. Lock until connection available.";
            ru.ok.android.utils.Logger.m172d(r5);	 Catch:{ all -> 0x04ed }
            r0 = r23;
            r5 = r0 instanceof java.net.ConnectException;	 Catch:{ all -> 0x04ed }
            if (r5 == 0) goto L_0x01b8;
        L_0x02a9:
            r5 = r31.setLock();	 Catch:{ all -> 0x04ed }
            if (r5 == 0) goto L_0x01b8;
        L_0x02af:
            goto L_0x0181;
        L_0x02b1:
            r5 = 10;
            r0 = r25;
            if (r0 != r5) goto L_0x02e6;
        L_0x02b7:
            r5 = 0;
            r5 = r24[r5];	 Catch:{ all -> 0x04ed }
            r6 = 73;
            if (r5 != r6) goto L_0x038c;
        L_0x02be:
            r5 = 1;
            r5 = r24[r5];	 Catch:{ all -> 0x04ed }
            r6 = 68;
            if (r5 != r6) goto L_0x038c;
        L_0x02c5:
            r5 = 2;
            r5 = r24[r5];	 Catch:{ all -> 0x04ed }
            r6 = 51;
            if (r5 != r6) goto L_0x038c;
        L_0x02cc:
            r5 = 6;
            r5 = r24[r5];	 Catch:{ all -> 0x04ed }
            r5 = r5 << 21;
            r5 = r5 + 10;
            r6 = 7;
            r6 = r24[r6];	 Catch:{ all -> 0x04ed }
            r6 = r6 << 14;
            r5 = r5 | r6;
            r6 = 8;
            r6 = r24[r6];	 Catch:{ all -> 0x04ed }
            r6 = r6 << 7;
            r5 = r5 | r6;
            r6 = 9;
            r6 = r24[r6];	 Catch:{ all -> 0x04ed }
            r21 = r5 | r6;
        L_0x02e6:
            r0 = r31;
            r5 = r0.streamState;	 Catch:{ IOException -> 0x04f2 }
            r6 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Interrupt;	 Catch:{ IOException -> 0x04f2 }
            if (r5 == r6) goto L_0x04f7;
        L_0x02ee:
            r0 = r31;
            r5 = r0.streamState;	 Catch:{ IOException -> 0x04f2 }
            r6 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Error;	 Catch:{ IOException -> 0x04f2 }
            if (r5 == r6) goto L_0x04f7;
        L_0x02f6:
            r5 = 0;
            r0 = r26;
            r1 = r17;
            r2 = r28;
            r0.write(r1, r5, r2);	 Catch:{ IOException -> 0x04f2 }
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x04f2 }
            r0 = r28;
            r6 = (long) r0;	 Catch:{ IOException -> 0x04f2 }
            ru.ok.android.music.StreamMediaPlayer.access$314(r5, r6);	 Catch:{ IOException -> 0x04f2 }
            r18 = r19 + 1;
            r5 = r19 % 20;
            if (r5 != 0) goto L_0x033a;
        L_0x0310:
            r5 = "(%d) %d / %d transferred";
            r6 = 3;
            r6 = new java.lang.Object[r6];	 Catch:{ IOException -> 0x03a2 }
            r7 = 0;
            r0 = r31;
            r8 = r0.instance;	 Catch:{ IOException -> 0x03a2 }
            r8 = java.lang.Integer.valueOf(r8);	 Catch:{ IOException -> 0x03a2 }
            r6[r7] = r8;	 Catch:{ IOException -> 0x03a2 }
            r7 = 1;
            r8 = java.lang.Integer.valueOf(r28);	 Catch:{ IOException -> 0x03a2 }
            r6[r7] = r8;	 Catch:{ IOException -> 0x03a2 }
            r7 = 2;
            r0 = r31;
            r8 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x03a2 }
            r8 = r8.totalRead;	 Catch:{ IOException -> 0x03a2 }
            r8 = java.lang.Long.valueOf(r8);	 Catch:{ IOException -> 0x03a2 }
            r6[r7] = r8;	 Catch:{ IOException -> 0x03a2 }
            ru.ok.android.utils.Logger.m173d(r5, r6);	 Catch:{ IOException -> 0x03a2 }
        L_0x033a:
            r0 = r31;
            r5 = r0.streamState;	 Catch:{ IOException -> 0x03a2 }
            r6 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Interrupt;	 Catch:{ IOException -> 0x03a2 }
            if (r5 == r6) goto L_0x0381;
        L_0x0342:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x03a2 }
            r5 = r5.state;	 Catch:{ IOException -> 0x03a2 }
            r6 = ru.ok.android.music.StreamMediaPlayer.State.Buffering;	 Catch:{ IOException -> 0x03a2 }
            if (r5 != r6) goto L_0x03c8;
        L_0x034e:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x03a2 }
            r6 = r5.totalRead;	 Catch:{ IOException -> 0x03a2 }
            r5 = 245760; // 0x3c000 float:3.44383E-40 double:1.214216E-318;
            r5 = r5 + r21;
            r8 = (long) r5;	 Catch:{ IOException -> 0x03a2 }
            r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r5 <= 0) goto L_0x0381;
        L_0x0360:
            r0 = r31;
            r6 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x03a2 }
            if (r21 <= 0) goto L_0x0390;
        L_0x0366:
            r5 = r21;
        L_0x0368:
            r5 = r6.onBuffering(r5);	 Catch:{ IOException -> 0x03a2 }
            if (r5 == 0) goto L_0x0392;
        L_0x036e:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x03a2 }
            r5 = r5.musicBufferExecutor;	 Catch:{ IOException -> 0x03a2 }
            r0 = r31;
            r6 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x03a2 }
            r6 = r6.autoPlay;	 Catch:{ IOException -> 0x03a2 }
            r5.addTask(r6);	 Catch:{ IOException -> 0x03a2 }
        L_0x0381:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x03bd }
            r5.fireDataLoadUpdate();	 Catch:{ all -> 0x03bd }
        L_0x0388:
            r19 = r18;
            goto L_0x0181;
        L_0x038c:
            r21 = 0;
            goto L_0x02e6;
        L_0x0390:
            r5 = 0;
            goto L_0x0368;
        L_0x0392:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x03a2 }
            r6 = new java.lang.Exception;	 Catch:{ IOException -> 0x03a2 }
            r7 = "buffered error";
            r6.<init>(r7);	 Catch:{ IOException -> 0x03a2 }
            r5.notifyError(r6);	 Catch:{ IOException -> 0x03a2 }
            goto L_0x0381;
        L_0x03a2:
            r22 = move-exception;
        L_0x03a3:
            r5 = "Failed to save audio to file.";
            r0 = r22;
            ru.ok.android.utils.Logger.m179e(r0, r5);	 Catch:{ all -> 0x03bd }
            r5 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Error;	 Catch:{ all -> 0x03bd }
            r0 = r31;
            r0.setStreamState(r5);	 Catch:{ all -> 0x03bd }
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x03bd }
            r6 = 0;
            r8 = 2;
            r5.notifyDownloadError(r6, r8);	 Catch:{ all -> 0x03bd }
            goto L_0x0381;
        L_0x03bd:
            r5 = move-exception;
        L_0x03be:
            ru.ok.android.utils.IOUtils.closeSilently(r12);
            ru.ok.android.utils.IOUtils.closeSilently(r26);
            ru.ok.android.utils.IOUtils.closeSilently(r29);
            throw r5;
        L_0x03c8:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x03a2 }
            r5 = r5.musicBufferExecutor;	 Catch:{ IOException -> 0x03a2 }
            r5 = r5.isFree();	 Catch:{ IOException -> 0x03a2 }
            if (r5 == 0) goto L_0x0381;
        L_0x03d6:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x03a2 }
            r5 = r5.isSeeking();	 Catch:{ IOException -> 0x03a2 }
            if (r5 != 0) goto L_0x0381;
        L_0x03e0:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x03a2 }
            r6 = r5.totalRead;	 Catch:{ IOException -> 0x03a2 }
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x03a2 }
            r8 = r5.mp3Size;	 Catch:{ IOException -> 0x03a2 }
            r6 = r6 - r8;
            r8 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
            r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r5 <= 0) goto L_0x0381;
        L_0x03f8:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ IOException -> 0x03a2 }
            r5 = r5.musicBufferExecutor;	 Catch:{ IOException -> 0x03a2 }
            r6 = 0;
            r5.addTask(r6);	 Catch:{ IOException -> 0x03a2 }
            goto L_0x0381;
        L_0x0406:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x04ed }
            r6 = r5.totalRead;	 Catch:{ all -> 0x04ed }
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x04ed }
            r8 = r5.mediaLength;	 Catch:{ all -> 0x04ed }
            r8 = r8 - r34;
            r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r5 != 0) goto L_0x0452;
        L_0x041c:
            r5 = "(%d) downloading finished!";
            r6 = 1;
            r6 = new java.lang.Object[r6];	 Catch:{ all -> 0x04ed }
            r7 = 0;
            r0 = r31;
            r8 = r0.instance;	 Catch:{ all -> 0x04ed }
            r8 = java.lang.Integer.valueOf(r8);	 Catch:{ all -> 0x04ed }
            r6[r7] = r8;	 Catch:{ all -> 0x04ed }
            ru.ok.android.utils.Logger.m173d(r5, r6);	 Catch:{ all -> 0x04ed }
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x04ed }
            r5 = r5.musicBufferExecutor;	 Catch:{ all -> 0x04ed }
            r6 = 0;
            r5.addFinishTask(r6);	 Catch:{ all -> 0x04ed }
            r5 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Finish;	 Catch:{ all -> 0x04ed }
            r0 = r31;
            r0.setStreamState(r5);	 Catch:{ all -> 0x04ed }
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x04ed }
            r0 = r32;
            r1 = r34;
            r5.notifyDataFullyLoaded(r0, r1);	 Catch:{ all -> 0x04ed }
            r18 = r19;
            goto L_0x0388;
        L_0x0452:
            r5 = "(%d) got EOF from server before mediaLength is reached. Total read: %d";
            r6 = 2;
            r6 = new java.lang.Object[r6];	 Catch:{ all -> 0x04ed }
            r7 = 0;
            r0 = r31;
            r8 = r0.instance;	 Catch:{ all -> 0x04ed }
            r8 = java.lang.Integer.valueOf(r8);	 Catch:{ all -> 0x04ed }
            r6[r7] = r8;	 Catch:{ all -> 0x04ed }
            r7 = 1;
            r0 = r31;
            r8 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x04ed }
            r8 = r8.totalRead;	 Catch:{ all -> 0x04ed }
            r8 = java.lang.Long.valueOf(r8);	 Catch:{ all -> 0x04ed }
            r6[r7] = r8;	 Catch:{ all -> 0x04ed }
            ru.ok.android.utils.Logger.m173d(r5, r6);	 Catch:{ all -> 0x04ed }
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x04ed }
            r6 = 1;
            r5.skipTrack = r6;	 Catch:{ all -> 0x04ed }
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;	 Catch:{ all -> 0x04ed }
            r5 = r5.musicBufferExecutor;	 Catch:{ all -> 0x04ed }
            r6 = 0;
            r5.addFinishTask(r6);	 Catch:{ all -> 0x04ed }
            r5 = ru.ok.android.music.StreamMediaPlayer.DownloadState.Finish;	 Catch:{ all -> 0x04ed }
            r0 = r31;
            r0.setStreamState(r5);	 Catch:{ all -> 0x04ed }
            r18 = r19;
            goto L_0x0388;
        L_0x0494:
            r5 = move-exception;
            monitor-exit(r6);	 Catch:{ all -> 0x0494 }
            throw r5;
        L_0x0497:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;
            r6 = r5.totalRead;
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;
            r8 = r5.startRead;
            r6 = r6 + r8;
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;
            r8 = r5.mediaLength;
            r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r5 == 0) goto L_0x04e4;
        L_0x04b4:
            r5 = "Download has been finished but track length: %d != mediaLength: %d. ";
            r6 = 2;
            r6 = new java.lang.Object[r6];
            r7 = 0;
            r0 = r31;
            r8 = ru.ok.android.music.StreamMediaPlayer.this;
            r8 = r8.totalRead;
            r0 = r31;
            r11 = ru.ok.android.music.StreamMediaPlayer.this;
            r14 = r11.startRead;
            r8 = r8 + r14;
            r8 = java.lang.Long.valueOf(r8);
            r6[r7] = r8;
            r7 = 1;
            r0 = r31;
            r8 = ru.ok.android.music.StreamMediaPlayer.this;
            r8 = r8.mediaLength;
            r8 = java.lang.Long.valueOf(r8);
            r6[r7] = r8;
            ru.ok.android.utils.Logger.m177e(r5, r6);
        L_0x04e4:
            r0 = r31;
            r5 = ru.ok.android.music.StreamMediaPlayer.this;
            r5.fireDataLoadUpdate();
            goto L_0x0287;
        L_0x04ed:
            r5 = move-exception;
            r18 = r19;
            goto L_0x03be;
        L_0x04f2:
            r22 = move-exception;
            r18 = r19;
            goto L_0x03a3;
        L_0x04f7:
            r18 = r19;
            goto L_0x0381;
            */
            throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.music.StreamMediaPlayer.Mp3Streamer.downloadAudioData(java.lang.String, ru.ok.android.music.data.BufferedMusicFile, long):void");
        }

        private void unRecoverableError(int code) {
            StreamMediaPlayer.this.setState(State.Error);
            setStreamState(DownloadState.Error);
            StreamMediaPlayer.this.notifyDownloadError(0, code);
        }

        private InputStream reopenInputStream(InputStream stream, BufferedMusicFile buffer, long offset, int readTimeout) throws IOException {
            if (!NetUtils.isConnectionAvailable(StreamMediaPlayer.this.context, false)) {
                StreamMediaPlayer.this.notifyDownloadError(StreamMediaPlayer.this.totalRead, 0);
                if (!setLock()) {
                    Logger.m172d("Connection e. Lock interrupted.");
                }
            }
            Logger.m172d("Connection e. Availible");
            setStreamState(DownloadState.Prepare);
            IOUtils.closeSilently((Closeable) stream);
            Pair<InputStream, Long> pair = StreamMediaPlayer.this.initInputStream(this.mediaUrl, buffer, StreamMediaPlayer.this.totalRead + offset, null, readTimeout);
            if (pair != null) {
                stream = pair.first;
                StreamMediaPlayer.this.mediaLength = (((Long) pair.second).longValue() + offset) + StreamMediaPlayer.this.totalRead;
                return stream;
            }
            setStreamState(DownloadState.Error);
            StreamMediaPlayer.this.notifyDownloadError(StreamMediaPlayer.this.totalRead, 1);
            return stream;
        }
    }

    public interface OnFinishTaskListener {
        void onFinishTask(UpdateFileRunnable updateFileRunnable);
    }

    private class MusicBuffer implements OnFinishTaskListener {
        private ExecutorService executorTransfer;
        private List<UpdateFileRunnable> tasks;

        private MusicBuffer() {
            this.tasks = new LinkedList();
        }

        private ExecutorService getExecutor() {
            if (this.executorTransfer == null || this.executorTransfer.isShutdown()) {
                this.executorTransfer = Executors.newSingleThreadExecutor();
            }
            return this.executorTransfer;
        }

        protected synchronized void addTask(UpdateFileRunnable runnable) {
            runnable.setOnFinishTaskListener(this);
            this.tasks.add(runnable);
            getExecutor().submit(runnable);
        }

        public UpdateFileRunnable addTask(boolean start) {
            UpdateFileRunnable runnable = new UpdateFileRunnable(start);
            addTask(runnable);
            return runnable;
        }

        public UpdateFileRunnable addFinishTask(boolean start) {
            UpdateFileRunnable runnable = new FinishUpdateFileRunnable(start);
            addTask(runnable);
            return runnable;
        }

        public synchronized void clean() {
            for (UpdateFileRunnable task : this.tasks) {
                task.interruptTask();
            }
            if (this.executorTransfer != null) {
                this.executorTransfer.shutdown();
            }
            this.tasks.clear();
        }

        protected synchronized boolean remove(UpdateFileRunnable task) {
            return this.tasks.remove(task);
        }

        public synchronized void resume() {
            for (UpdateFileRunnable task : this.tasks) {
                if (task.isWaitingPlay()) {
                    task.resumeThread();
                }
            }
        }

        public void onFinishTask(UpdateFileRunnable task) {
            remove(task);
        }

        public synchronized boolean isFree() {
            return this.tasks.size() == 0;
        }
    }

    public interface PlayerCallBack {
        void onBuffering();

        void onDownLoadProgress(int i);

        void onDownloadError(long j, int i);

        void onDownloadFinish(String str, long j);

        void onError(Exception exception, boolean z);

        void onPause();

        void onPlay();

        void onPlayEnd();

        void onProgress(int i);

        void onSaveInBuffer(String str, File file);

        void onStart();

        void onStop();
    }

    public enum State {
        Idle,
        Start,
        Buffering,
        Buffered,
        Playing,
        Seeking,
        Paused,
        Stop,
        Error
    }

    private class WaitLock {
        private final Object lock;
        private volatile boolean lockValue;

        private WaitLock() {
            this.lock = new Object();
        }

        public void lockThread() throws InterruptedException {
            synchronized (this.lock) {
                this.lockValue = true;
                this.lock.wait();
            }
        }

        public void unLockThread() {
            synchronized (this.lock) {
                this.lock.notify();
                this.lockValue = false;
            }
        }

        public boolean isWaiting() {
            return this.lockValue;
        }
    }

    static /* synthetic */ long access$314(StreamMediaPlayer x0, long x1) {
        long j = x0.totalRead + x1;
        x0.totalRead = j;
        return j;
    }

    public StreamMediaPlayer(Context context, PlayerCallBack callBack) {
        this.autoPlay = true;
        this.mp3Size = 0;
        this.pcmSize = 0;
        this.state = State.Idle;
        this.context = context;
        this.callBack = callBack;
        this.musicBufferExecutor = new MusicBuffer();
    }

    private void notifyStop() {
        if (this.callBack != null) {
            this.callBack.onStop();
        }
    }

    private void notifyPause() {
        if (this.callBack != null) {
            this.callBack.onPause();
        }
    }

    private void notifyPlay() {
        if (this.callBack != null) {
            this.callBack.onPlay();
        }
    }

    private void notifyError(Exception errorEx) {
        notifyError(errorEx, true);
    }

    private void notifyError(Exception errorEx, boolean isVisibleErrorForUser) {
        if (this.callBack != null) {
            this.callBack.onError(errorEx, isVisibleErrorForUser);
        }
    }

    private void notifyBufferingFinish() {
        if (this.callBack != null) {
            this.callBack.onBuffering();
        }
    }

    private void notifyDataFullyLoaded(String url, long offset) {
        if (this.callBack != null) {
            this.callBack.onDownloadFinish(url, offset);
        }
    }

    private void notifySaveInBuffer(String url, File bufferedFile) {
        if (this.callBack != null) {
            this.callBack.onSaveInBuffer(url, bufferedFile);
        }
    }

    private void notifyDownloadError(long errorPosition, int code) {
        if (this.callBack != null) {
            ThreadUtil.executeOnMain(new C03791(errorPosition, code));
        }
    }

    private void notifyPlayEnd() {
        if (this.callBack != null) {
            this.callBack.onPlayEnd();
        }
    }

    private void notifyProgress(int value) {
        if (this.callBack != null) {
            this.callBack.onProgress(value);
        }
    }

    private synchronized void setState(State state) {
        Logger.m172d("Stream Player set state: " + state.toString());
        this.state = state;
    }

    public void stop(boolean notify) {
        if (this.audioDevice != null && this.audioDevice.stop()) {
            setState(State.Stop);
            if (notify) {
                notifyStop();
            }
        }
    }

    public void repeat() {
        seekTo(0, true);
    }

    public synchronized boolean play() {
        boolean z;
        if (this.audioDevice == null || !this.audioDevice.play()) {
            setState(State.Error);
            notifyError(new Exception("On play error"));
            z = false;
        } else {
            setState(State.Playing);
            this.musicBufferExecutor.resume();
            notifyPlay();
            z = true;
        }
        return z;
    }

    public synchronized boolean pause() {
        boolean z;
        if (this.audioDevice == null || !this.audioDevice.pause()) {
            setState(State.Error);
            notifyError(new Exception("On play error"));
            z = false;
        } else {
            setState(State.Paused);
            notifyPause();
            z = true;
        }
        return z;
    }

    public void release() {
        if (this.audioDevice != null) {
            this.audioDevice.release();
        }
    }

    public boolean isPlaying() {
        if (this.audioDevice != null && this.audioDevice.isPlaying() && this.state == State.Playing) {
            return true;
        }
        return false;
    }

    public boolean isPause() {
        if (this.audioDevice != null && this.audioDevice.isPausing() && this.state == State.Paused) {
            return true;
        }
        return false;
    }

    public boolean isSeeking() {
        if (this.audioDevice != null && this.state == State.Seeking) {
            return true;
        }
        return false;
    }

    public void seekTo(int position) {
        boolean z = true;
        boolean value;
        if (isPlaying() || this.state == State.Buffering || this.state == State.Buffered || this.state == State.Playing || this.state == State.Start) {
            value = true;
        } else {
            value = false;
        }
        if (!(this.autoPlay && value)) {
            z = false;
        }
        seekTo(position, z);
    }

    public void seekTo(int position, boolean autoPlayValue) {
        if (autoPlayValue && position == 100) {
            stop(true);
            notifyPlayEnd();
        }
        setState(State.Seeking);
        if (this.audioDevice != null && this.audioDevice.isPlaying()) {
            this.audioDevice.stop();
        }
        long valueSeek = position == 0 ? 0 : (long) ((((double) this.mediaLength) / 100.0d) * ((double) position));
        if (valueSeek < this.startRead || valueSeek > this.totalRead + this.startRead || this.duration <= 0) {
            if (this.downloadContentThread != null && this.downloadContentThread.isAlive()) {
                this.downloadContentThread.shutdown();
            }
            this.musicBufferExecutor.clean();
            this.pcmSize = 0;
            seekInToNoBufferedPart(autoPlayValue, valueSeek);
            return;
        }
        this.musicBufferExecutor.clean();
        this.pcmSize = 0;
        if (reCreateAudioDevice()) {
            seekInToBufferedPart(autoPlayValue, valueSeek);
        } else {
            notifyError(new Exception("no create audio device error"));
        }
    }

    private boolean reCreateAudioDevice() {
        try {
            if (this.audioDevice != null) {
                this.audioDevice.clearFinishCallBack();
                this.audioDevice.release();
                this.audioDevice = new AndroidAudioDevice(this.audioDevice.getSampleRate(), this.audioDevice.getChannelsConfiguration(), this.audioDevice.getChannelsCount());
            } else {
                this.audioDevice = new AndroidAudioDevice();
            }
            this.audioDevice.setUpdateProgressCallBack(this);
            this.audioDevice.setFinishCallBack(this);
            this.audioDevice.setProgressPeriod(this.audioDevice.getSampleRate() / 2);
            return true;
        } catch (InitializationAudioException ex) {
            Logger.m173d("create audio device error: %s", ex.getMessage());
            return false;
        }
    }

    private void seekInToNoBufferedPart(boolean autoPlayValue, long valueSeek) {
        try {
            this.autoPlay = autoPlayValue;
            interrupt();
            if (this.mediaUrl != null) {
                startStreaming(this.mediaUrl, this.bufferedFile, this.mediaLength, this.duration, valueSeek);
            } else {
                notifyError(new NullPointerException("media url is null"));
            }
        } catch (IOException e) {
            notifyError(e);
        }
    }

    private void seekInToBufferedPart(boolean autoPlayValue, long valueSeek) {
        this.mp3Size = valueSeek - this.startRead;
        this.byteToSeek = valueSeek - this.startRead;
        if (this.downloadContentThread == null || !this.downloadContentThread.isCompleted()) {
            this.musicBufferExecutor.addTask(autoPlayValue);
        } else {
            this.musicBufferExecutor.addFinishTask(autoPlayValue);
        }
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public void streamingMusic(String urlMp3, BufferedMusicFile file, long mediaLengthInKb, long mediaLengthInSeconds) throws IOException {
        setState(State.Start);
        this.callBack.onStart();
        startStreaming(urlMp3, file, mediaLengthInKb * PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID, mediaLengthInSeconds, 0);
    }

    private void startStreaming(String urlMp3, BufferedMusicFile file, long mediaLength, long mediaLengthInSeconds, long startOffset) throws IOException {
        this.mediaLength = mediaLength;
        this.duration = mediaLengthInSeconds;
        this.mediaUrl = urlMp3;
        this.bufferedFile = file;
        this.byteToSeek = 0;
        this.musicBufferExecutor.clean();
        clearState();
        if (this.downloadContentThread != null && this.downloadContentThread.isAlive()) {
            this.downloadContentThread.shutdown();
        }
        this.downloadContentThread = new Mp3Streamer(urlMp3, file, startOffset);
        this.downloadContentThread.start();
    }

    public void notifyDownload() {
        if (this.downloadContentThread != null && this.downloadContentThread.isWait()) {
            this.downloadContentThread.resumeDownload();
        }
    }

    static {
        instances = new AtomicInteger();
    }

    private synchronized boolean onBuffering(int dataOffset) {
        boolean z = false;
        synchronized (this) {
            setState(State.Buffered);
            this.dataPos = dataOffset;
            notifyBufferingFinish();
            if (this.audioDevice != null) {
                this.audioDevice.release();
            }
            Mp3Info info = getFormatAudio(this.downloadingMediaFile.getAbsolutePath());
            if (info != null) {
                try {
                    if (info.sampleRate > 0) {
                        this.audioDevice = new AndroidAudioDevice(info.sampleRate, info.channelsFormat, info.channelsCount);
                        this.audioDevice.setUpdateProgressCallBack(this);
                        this.audioDevice.setFinishCallBack(this);
                        this.audioDevice.setProgressPeriod(this.audioDevice.getSampleRate() / 2);
                        z = true;
                    }
                } catch (InitializationAudioException ex) {
                    Logger.m173d("init audio dev ice error: %s", ex.getMessage());
                }
            }
            this.audioDevice = new AndroidAudioDevice();
            this.audioDevice.setUpdateProgressCallBack(this);
            this.audioDevice.setFinishCallBack(this);
            this.audioDevice.setProgressPeriod(this.audioDevice.getSampleRate() / 2);
            z = true;
        }
        return z;
    }

    private Mp3Info getFormatAudio(String pathMp3) {
        try {
            MP3FileDecoder fileDecoder = new MP3FileDecoder(pathMp3, (long) this.dataPos);
            fileDecoder.readSamples(6144);
            int sampleRate = fileDecoder.getAudioSampleRate(44100);
            int bitRate = fileDecoder.getAudioBitRate(50000);
            int channelsFormat = fileDecoder.getChannelsFormat(12);
            int channelsCount = fileDecoder.getAudioChannelsCount(2);
            int size = fileDecoder.getPcmSize();
            fileDecoder.dispose();
            Logger.m173d("start decode data - Rate: %d,Format: %d,Count: %d", Integer.valueOf(sampleRate), Integer.valueOf(channelsFormat), Integer.valueOf(channelsCount));
            return new Mp3Info(sampleRate, bitRate, channelsFormat, channelsCount, size);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Pair<InputStream, Long> initInputStream(String mediaUrl, BufferedMusicFile fileBuffer, long offsetIn, AtomicReference<Boolean> isFromCache, int readTimeout) throws IOException {
        Pair<InputStream, Long> pair = MusicUtils.initCacheInputStream(MusicAsyncFileCache.getInstance(), mediaUrl, offsetIn);
        if (pair != null) {
            if (isFromCache != null) {
                isFromCache.set(Boolean.TRUE);
            }
            return pair;
        }
        if (fileBuffer != null) {
            if (fileBuffer.isComplete()) {
                pair = MusicUtils.initFileInputStream(fileBuffer.getFile(), offsetIn);
            } else {
                pair = MusicUtils.initFileAndHttpInputStream(fileBuffer, mediaUrl, offsetIn, readTimeout);
            }
        }
        if (pair != null) {
            return pair;
        }
        return MusicUtils.initHttpInputStream(mediaUrl, offsetIn, readTimeout);
    }

    private OutputStream initOutStream(String name) {
        this.downloadingMediaFile = new File(MusicUtils.getCache(this.context), name);
        return MusicUtils.initOutStream(this.downloadingMediaFile);
    }

    private void fireDataLoadUpdate() {
        int loadProgress = getDataLoadProgress();
        if (this.callBack != null) {
            this.callBack.onDownLoadProgress(loadProgress);
        }
    }

    public int getDataLoadProgress() {
        if (Thread.currentThread().isInterrupted()) {
            return 0;
        }
        return (int) (100.0f * (((float) (this.totalRead + this.startRead)) / ((float) this.mediaLength)));
    }

    private int getProgressValue(int position) {
        if (this.duration <= 0 || this.mediaLength <= 0 || this.state == State.Stop) {
            return 0;
        }
        double kbInSec = (double) (this.mediaLength / this.duration);
        double startInSec = ((double) this.startRead) / kbInSec;
        double seekInSec = ((double) this.byteToSeek) / kbInSec;
        int value = (((int) seekInSec) + position) + ((int) startInSec);
        Logger.m172d("value progress: " + value + " " + position + " " + startInSec + " " + seekInSec);
        return value + 1;
    }

    public int getProgressValue() {
        Logger.m172d("Stream Player get state: " + this.state.toString());
        if (this.audioDevice == null || this.state == State.Stop) {
            Logger.m172d("AudioDevice no init or NULL");
            return 0;
        }
        try {
            Logger.m173d("value progress: %d", Integer.valueOf(getProgressValue(this.audioDevice.getPosition())));
            return getProgressValue(this.audioDevice.getPosition());
        } catch (IllegalStateException e) {
            notifyError(new Exception("get progress error AudioDevice is IllegalStateException"));
            return 0;
        }
    }

    public synchronized void interrupt() {
        if (this.downloadContentThread != null && this.downloadContentThread.isAlive()) {
            this.downloadContentThread.shutdown();
        }
        setState(State.Idle);
        clearState();
    }

    private void clearState() {
        if (this.audioDevice != null && this.audioDevice.stop()) {
            this.audioDevice.clearFinishCallBack();
            this.audioDevice.clearBuffer();
        }
        this.musicBufferExecutor.clean();
        this.totalRead = 0;
    }

    public void onFinish(AndroidAudioDevice device, int position) {
        stop(true);
        notifyPlayEnd();
    }

    public void onProgress(AndroidAudioDevice device, int position) {
        notifyProgress(getProgressValue(position));
    }

    public boolean isDownloadComplete() {
        return this.downloadContentThread != null && this.downloadContentThread.isCompleted();
    }
}
