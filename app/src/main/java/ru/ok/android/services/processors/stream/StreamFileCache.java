package ru.ok.android.services.processors.stream;

import android.content.Context;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.storage.FileKeyValueStorage;
import ru.ok.android.storage.IStreamMetaStorage;
import ru.ok.android.storage.StorageException;
import ru.ok.android.storage.Storages;
import ru.ok.android.storage.sqlite.SqliteStreamMetaStorage;
import ru.ok.android.ui.stream.data.StreamContext;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.StreamPage;
import ru.ok.model.stream.StreamPageKey;

public final class StreamFileCache {
    private final String basePath;
    private final Map<StreamContext, FileKeyValueStorage<StreamPage>> caches;
    private final Context context;
    File currentUserDir;
    private final String currentUserId;
    private final IStreamMetaStorage metaStorage;
    private final StreamSerializer serializer;
    private final Storages storages;

    public StreamFileCache(Context context, String currentUserId, String basePath, Storages storages) {
        this.caches = new HashMap();
        this.serializer = new StreamSerializer();
        this.context = context.getApplicationContext();
        this.currentUserId = currentUserId;
        this.basePath = basePath;
        this.metaStorage = new SqliteStreamMetaStorage(context, currentUserId);
        this.storages = storages;
    }

    public void put(StreamContext streamContext, StreamPageKey key, StreamPage page, long ts) throws StorageException {
        getCache(streamContext).put(key.getKey(), page);
        this.metaStorage.put(streamContext, key, ts);
    }

    public void remove(StreamContext streamContext, StreamPageKey key) throws StorageException {
        getCache(streamContext).remove(key.getKey());
        this.metaStorage.remove(streamContext, key);
    }

    public StreamPage get(StreamContext streamContext, StreamPageKey key) throws StorageException {
        this.storages.waitStreamCacheInitialized();
        return (StreamPage) getCache(streamContext).get(key.getKey());
    }

    private FileKeyValueStorage<StreamPage> getCache(StreamContext streamContext) throws StorageException {
        FileKeyValueStorage<StreamPage> cache;
        synchronized (this.caches) {
            cache = (FileKeyValueStorage) this.caches.get(streamContext);
            if (cache == null) {
                cache = new FileKeyValueStorage(this.context, getCacheDir(streamContext), this.serializer);
                this.caches.put(streamContext, cache);
            }
        }
        return cache;
    }

    private File getCacheDir(StreamContext streamContext) throws StorageException {
        File cacheDir;
        if (this.currentUserDir == null) {
            this.currentUserDir = new File(new File(this.context.getCacheDir(), this.basePath), FileUtils.id2filename(this.currentUserId));
        }
        switch (streamContext.type) {
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                cacheDir = new File(this.currentUserDir, "user" + File.separator + FileUtils.id2filename(streamContext.id));
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                cacheDir = new File(this.currentUserDir, "group" + File.separator + FileUtils.id2filename(streamContext.id));
                break;
            default:
                cacheDir = new File(this.currentUserDir, "common");
                break;
        }
        if (!cacheDir.exists() || cacheDir.isDirectory()) {
            return cacheDir;
        }
        throw new StorageException("Path name exists and is not a directory: " + cacheDir);
    }

    public void trim(long trimLimitTs) throws StorageException {
        long startTime = System.currentTimeMillis();
        Logger.m173d("trim >>> trimLimitTs=%d", Long.valueOf(trimLimitTs));
        HashMap<StreamContext, ArrayList<StreamPageKey>> keysToDelete = this.metaStorage.getOlder(trimLimitTs);
        int count = 0;
        if (keysToDelete == null) {
            Logger.m172d("trim: nothing to delete");
        } else {
            this.metaStorage.remove(keysToDelete);
            for (Entry<StreamContext, ArrayList<StreamPageKey>> entry : keysToDelete.entrySet()) {
                StreamContext context = (StreamContext) entry.getKey();
                ArrayList<StreamPageKey> keys = (ArrayList) entry.getValue();
                for (int i = keys.size() - 1; i >= 0; i--) {
                    Logger.m173d("trim: delete context=%s key=%s", context, (StreamPageKey) keys.get(i));
                    remove(context, key);
                    count++;
                }
            }
        }
        Logger.m173d("trim <<< deleted %d pages in %d ms", Integer.valueOf(count), Long.valueOf(System.currentTimeMillis() - startTime));
    }
}
