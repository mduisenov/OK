package ru.ok.android.model.cache.music.async;

import android.content.Context;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.cache.music.MusicBaseFileCache;
import ru.ok.android.model.cache.music.async.AsyncFileCache.CacheDataCallBack;
import ru.ok.android.model.cache.music.async.AsyncFileCache.ContainsKeyCallBack;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.model.wmf.PlayTrackInfo;

public final class MusicAsyncFileCache extends MusicBaseFileCache implements AsyncFileCache {
    private static MusicAsyncFileCache instance;
    ExecutorService cacheExecutor;

    /* renamed from: ru.ok.android.model.cache.music.async.MusicAsyncFileCache.1 */
    class C03501 implements Runnable {
        final /* synthetic */ CacheDataCallBack val$callBack;
        final /* synthetic */ PlayTrackInfo val$info;
        final /* synthetic */ InputStream val$is;

        C03501(PlayTrackInfo playTrackInfo, InputStream inputStream, CacheDataCallBack cacheDataCallBack) {
            this.val$info = playTrackInfo;
            this.val$is = inputStream;
            this.val$callBack = cacheDataCallBack;
        }

        public void run() {
            if (MusicAsyncFileCache.this.dataCache(this.val$info, this.val$is)) {
                Logger.m173d("Track %d saved successfully!", Long.valueOf(this.val$info.trackId));
                if (this.val$callBack != null) {
                    this.val$callBack.onCacheDataSuccessful(this.val$info, this.val$is);
                    return;
                }
                return;
            }
            Logger.m173d("Track %d failed to save!", Long.valueOf(this.val$info.trackId));
            if (this.val$callBack != null) {
                this.val$callBack.onCacheDataFail(this.val$info, this.val$is);
            }
        }
    }

    final class ContainsRunnable implements Runnable {
        private final ContainsKeyCallBack callBack;
        private final String key;

        ContainsRunnable(String key, ContainsKeyCallBack callBack) {
            this.key = key;
            this.callBack = callBack;
        }

        public void run() {
            this.callBack.onGetKeyInCacheValue(this.key, MusicAsyncFileCache.this.isKeyContains(this.key));
        }
    }

    public static MusicAsyncFileCache getInstance() {
        if (instance == null) {
            instance = new MusicAsyncFileCache(OdnoklassnikiApplication.getContext());
        }
        return instance;
    }

    private ExecutorService getCacheExecutor() {
        if (this.cacheExecutor == null) {
            this.cacheExecutor = Executors.newFixedThreadPool(3);
        }
        return this.cacheExecutor;
    }

    private MusicAsyncFileCache(Context context) {
        super(context);
    }

    public Future isKeyContains(String key, ContainsKeyCallBack callBack) {
        return getCacheExecutor().submit(new ContainsRunnable(key, callBack));
    }

    public void cacheData(PlayTrackInfo info, InputStream is, CacheDataCallBack callBack) {
        Runnable task = new C03501(info, is, callBack);
        if (ThreadUtil.isMainThread()) {
            getCacheExecutor().submit(task);
        } else {
            task.run();
        }
    }
}
