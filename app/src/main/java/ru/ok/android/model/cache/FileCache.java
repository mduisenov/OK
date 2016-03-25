package ru.ok.android.model.cache;

import android.content.Context;
import com.jakewharton.disklrucache.DiskLruCache;
import java.io.File;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.Logger;

public abstract class FileCache {
    private static volatile int tmpFileCount;
    protected final Context _context;
    private volatile DiskLruCache diskLruCache;

    protected abstract String getCacheFolderName();

    protected abstract int getFileCacheVersion();

    protected abstract int getValueCount();

    public FileCache(Context context) {
        this._context = context;
    }

    protected long getCacheSize(File cacheDir) {
        return 20971520;
    }

    protected DiskLruCache getDiskLruCache() {
        if (this.diskLruCache == null) {
            synchronized (this) {
                if (this.diskLruCache == null) {
                    try {
                        File cacheDir = getCacheDir();
                        long cacheSize = getCacheSize(cacheDir);
                        if (cacheSize <= 0) {
                            Logger.m185w("Wrong available cache size: %d bytes, use default: %d", Long.valueOf(cacheSize), Integer.valueOf(20971520));
                            cacheSize = 1;
                        } else {
                            Logger.m173d("Using cache size: %d bytes", Long.valueOf(cacheSize));
                        }
                        this.diskLruCache = DiskLruCache.open(cacheDir, getFileCacheVersion(), getValueCount(), cacheSize);
                    } catch (Exception e) {
                        Logger.m180e(e, "Failed to open LRU cache: %s", e);
                    }
                }
            }
        }
        return this.diskLruCache;
    }

    protected File getCacheDir() {
        return FileUtils.getCacheDir(this._context, getCacheFolderName());
    }

    public long getSize() {
        DiskLruCache lruCache = getDiskLruCache();
        if (lruCache != null) {
            return lruCache.size();
        }
        return -1;
    }

    static {
        tmpFileCount = 0;
    }
}
