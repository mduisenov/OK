package ru.ok.android.services.processors.stream;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.File;
import ru.ok.android.storage.FileKeyValueStorage;
import ru.ok.android.storage.StorageException;
import ru.ok.android.utils.FileUtils;
import ru.ok.model.stream.Holidays;

public class HolidaysFileCache {
    private String basePath;
    private Context context;
    private String currentUserId;
    private FileKeyValueStorage<Holidays> storage;

    public HolidaysFileCache(@NonNull Context context, @NonNull String currentUserId, @NonNull String basePath) {
        this.basePath = basePath;
        this.context = context;
        this.currentUserId = currentUserId;
    }

    public void replace(@Nullable Holidays holidays) throws StorageException {
        getStorage().put("key", holidays);
    }

    @Nullable
    public Holidays get() throws StorageException {
        return (Holidays) getStorage().get("key");
    }

    @NonNull
    private FileKeyValueStorage<Holidays> getStorage() throws StorageException {
        if (this.storage == null) {
            synchronized (this) {
                if (this.storage == null) {
                    this.storage = new FileKeyValueStorage(this.context, getCacheDir(this.basePath), new StreamHolidaySerializer());
                }
            }
        }
        return this.storage;
    }

    @NonNull
    private File getCacheDir(@NonNull String basePath) throws StorageException {
        File cacheDir = new File(new File(this.context.getCacheDir(), basePath), FileUtils.id2filename(this.currentUserId));
        if (!cacheDir.exists() || cacheDir.isDirectory()) {
            return cacheDir;
        }
        throw new StorageException("Path name exists and is not a directory: " + cacheDir);
    }
}
