package ru.ok.android.music;

import android.content.Context;
import android.os.ConditionVariable;
import android.util.Pair;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import ru.ok.android.model.cache.music.MusicBaseFileCache;
import ru.ok.android.model.cache.music.async.AsyncFileCache;
import ru.ok.android.model.cache.music.async.AsyncFileCache.ContainsKeyCallBack;
import ru.ok.android.music.data.BufferedMusicFile;
import ru.ok.android.services.app.MusicService.BufferedPlayInfo;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.Logger;
import ru.ok.model.wmf.PlayTrackInfo;
import ru.ok.model.wmf.Track;

public final class DownloadTask {
    private volatile long bufferingTrackId;
    private final Context context;
    private final AsyncFileCache fileCache;
    private Future<Pair<PlayTrackInfo, BufferedMusicFile>> future;
    private final ConditionVariable lock;
    private ExecutorService service;

    /* renamed from: ru.ok.android.music.DownloadTask.1 */
    class C03781 implements ContainsKeyCallBack {
        final /* synthetic */ PlayTrackInfo val$info;

        C03781(PlayTrackInfo playTrackInfo) {
            this.val$info = playTrackInfo;
        }

        public void onGetKeyInCacheValue(String key, boolean isCache) {
            String trackId = MusicBaseFileCache.buildFileName(this.val$info.trackId);
            String str = "DownloadTask: before cache: %b  %b";
            Object[] objArr = new Object[2];
            objArr[0] = Boolean.valueOf(!isCache);
            objArr[1] = Boolean.valueOf(key.equals(trackId));
            Logger.m173d(str, objArr);
            if (!isCache && key.equals(trackId)) {
                Pair<InputStream, Long> cachePair = null;
                try {
                    cachePair = MusicUtils.initHttpInputStream(this.val$info.getMp3ContentUrl(), 0, 5000);
                } catch (IOException e) {
                    Logger.m180e(e, "DownloadTask: error cached http: %s", e.getMessage());
                }
                if (cachePair != null) {
                    InputStream inputStream = cachePair.first;
                    Logger.m172d("DownloadTask: first start cached");
                    DownloadTask.this.future = DownloadTask.this.service.submit(new DownloadFilesRunnable(this.val$info, inputStream, ((Long) cachePair.second).longValue(), null));
                    Logger.m172d("DownloadTask: start cached");
                    return;
                }
                Logger.m172d("DownloadTask: no start cached(no create httpStream)");
            }
        }
    }

    private class DownloadFilesRunnable implements Callable<Pair<PlayTrackInfo, BufferedMusicFile>> {
        private final long expectedLength;
        private final PlayTrackInfo info;
        private final InputStream is;

        private DownloadFilesRunnable(PlayTrackInfo info, InputStream is, long expectedLength) {
            this.info = info;
            this.is = is;
            this.expectedLength = expectedLength;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.util.Pair<ru.ok.model.wmf.PlayTrackInfo, ru.ok.android.music.data.BufferedMusicFile> call() {
            /*
            r12 = this;
            r7 = "";
            ru.ok.android.utils.Logger.m172d(r7);
            r7 = ru.ok.android.music.DownloadTask.this;
            r8 = r12.info;
            r8 = r8.trackId;
            r7.bufferingTrackId = r8;
            r7 = ru.ok.android.music.DownloadTask.this;
            r2 = r7.getCacheDir();
            r3 = new java.io.File;
            r7 = "downloadingMediaNext.dat";
            r3.<init>(r2, r7);
            r6 = ru.ok.android.music.MusicUtils.initOutStream(r3);
            r7 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
            r1 = new byte[r7];
            r0 = 1;
        L_0x0026:
            r7 = r12.is;	 Catch:{ IOException -> 0x0061 }
            r5 = r7.read(r1);	 Catch:{ IOException -> 0x0061 }
            if (r5 < 0) goto L_0x0045;
        L_0x002e:
            if (r5 <= 0) goto L_0x0034;
        L_0x0030:
            r7 = 0;
            r6.write(r1, r7, r5);	 Catch:{ IOException -> 0x0061 }
        L_0x0034:
            r7 = java.lang.Thread.currentThread();	 Catch:{ IOException -> 0x0061 }
            r7 = r7.isInterrupted();	 Catch:{ IOException -> 0x0061 }
            if (r7 == 0) goto L_0x0057;
        L_0x003e:
            r7 = "DownloadTask: interrupt()";
            ru.ok.android.utils.Logger.m172d(r7);	 Catch:{ IOException -> 0x0061 }
            r0 = 0;
        L_0x0045:
            ru.ok.android.utils.IOUtils.closeSilently(r6);
        L_0x0048:
            r7 = new android.util.Pair;
            r8 = r12.info;
            r9 = new ru.ok.android.music.data.BufferedMusicFile;
            r10 = r12.expectedLength;
            r9.<init>(r3, r10, r0);
            r7.<init>(r8, r9);
            return r7;
        L_0x0057:
            r7 = ru.ok.android.music.DownloadTask.this;	 Catch:{ IOException -> 0x0061 }
            r7 = r7.lock;	 Catch:{ IOException -> 0x0061 }
            r7.block();	 Catch:{ IOException -> 0x0061 }
            goto L_0x0026;
        L_0x0061:
            r4 = move-exception;
            ru.ok.android.utils.Logger.m178e(r4);	 Catch:{ all -> 0x006a }
            r0 = 0;
            ru.ok.android.utils.IOUtils.closeSilently(r6);
            goto L_0x0048;
        L_0x006a:
            r7 = move-exception;
            ru.ok.android.utils.IOUtils.closeSilently(r6);
            throw r7;
            */
            throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.music.DownloadTask.DownloadFilesRunnable.call():android.util.Pair<ru.ok.model.wmf.PlayTrackInfo, ru.ok.android.music.data.BufferedMusicFile>");
        }
    }

    public DownloadTask(Context context, AsyncFileCache fileCache) {
        this.bufferingTrackId = -1;
        this.lock = new ConditionVariable(true);
        this.service = Executors.newSingleThreadExecutor();
        this.context = context;
        this.fileCache = fileCache;
    }

    public void startBufferingNextTrack(PlayTrackInfo info) {
        this.lock.open();
        Logger.m172d("DownloadTask: get cached data from cache");
        if (info.trackId == this.bufferingTrackId) {
            Logger.m173d("%d track is already downloading. ", Long.valueOf(info.trackId));
            return;
        }
        this.fileCache.isKeyContains(MusicBaseFileCache.buildFileName(info.trackId), new C03781(info));
    }

    public BufferedPlayInfo getNextBufferedTrack(Track requiringTrack) {
        this.lock.open();
        Logger.m172d("DownloadTask: get cached data");
        if (this.future != null) {
            try {
                this.service.shutdownNow();
                this.service = Executors.newSingleThreadExecutor();
                Pair<PlayTrackInfo, BufferedMusicFile> result = (Pair) this.future.get(1, TimeUnit.SECONDS);
                if (result == null || result.first == null || result.second == null || ((PlayTrackInfo) result.first).trackId != requiringTrack.id) {
                    this.future = null;
                    this.bufferingTrackId = -1;
                } else {
                    BufferedPlayInfo create = BufferedPlayInfo.create((PlayTrackInfo) result.first, ((BufferedMusicFile) result.second).move(new File(getCacheDir(), "bufferedMedia.dat")));
                    this.future = null;
                    this.bufferingTrackId = -1;
                    return create;
                }
            } catch (InterruptedException e) {
                this.future = null;
                this.bufferingTrackId = -1;
                return null;
            } catch (ExecutionException e2) {
                this.future = null;
                this.bufferingTrackId = -1;
                return null;
            } catch (IOException e3) {
                this.future = null;
                this.bufferingTrackId = -1;
                return null;
            } catch (TimeoutException e4) {
                this.future = null;
                this.bufferingTrackId = -1;
                return null;
            } catch (Throwable th) {
                this.future = null;
                this.bufferingTrackId = -1;
            }
        }
        return null;
    }

    private File getCacheDir() {
        File dir = FileUtils.getCacheDir(this.context, "temp-music");
        if (dir.exists() && !dir.isDirectory()) {
            dir.delete();
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public void pause() {
        this.lock.open();
    }
}
