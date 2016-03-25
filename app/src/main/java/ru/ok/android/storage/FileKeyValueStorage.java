package ru.ok.android.storage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.jivesoftware.smack.util.StringUtils;
import ru.ok.android.graylog.GrayLog;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.log.FilePathPrettyPrinter;

public class FileKeyValueStorage<V> {
    private static final ReentrantReadWriteLock storageLock;
    @NonNull
    private final File baseDir;
    @NonNull
    private final Context context;
    private final Map<String, ReadWriteLock> fileLocks;
    @NonNull
    private final ISerializer<V> serializer;

    public FileKeyValueStorage(@NonNull Context context, @NonNull File baseDir, @NonNull ISerializer<V> serializer) {
        this.fileLocks = new HashMap();
        this.context = context.getApplicationContext();
        this.baseDir = baseDir;
        this.serializer = serializer;
    }

    static {
        storageLock = new ReentrantReadWriteLock();
    }

    public void put(@NonNull String key, @NonNull V value) throws StorageException {
        long startTime = System.currentTimeMillis();
        Logger.m173d("put >>> key=%s value=%s", key, value);
        Lock lock = obtainLock(key).writeLock();
        storageLock.readLock().lock();
        try {
            lock.lock();
            putLocked(key, value);
            lock.unlock();
            storageLock.readLock().unlock();
            Logger.m173d("put <<< %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
        } catch (Throwable th) {
            storageLock.readLock().unlock();
        }
    }

    public void remove(@NonNull String key) throws StorageException {
        long startTime = System.currentTimeMillis();
        Logger.m173d("remove >>> key=%s", key);
        Lock lock = obtainLock(key).writeLock();
        storageLock.readLock().lock();
        try {
            lock.lock();
            removeLocked(key);
            lock.unlock();
            storageLock.readLock().unlock();
            Logger.m173d("remove <<< %d ms", Long.valueOf(System.currentTimeMillis() - startTime));
        } catch (Throwable th) {
            storageLock.readLock().unlock();
        }
    }

    @Nullable
    public V get(@NonNull String key) throws StorageException {
        long startTime = System.currentTimeMillis();
        Logger.m173d("get >>> key=%s", key);
        Lock lock = obtainLock(key).readLock();
        storageLock.readLock().lock();
        try {
            lock.lock();
            V value = getLocked(key);
            lock.unlock();
            storageLock.readLock().unlock();
            Logger.m173d("get <<< value=%s, %dms", value, Long.valueOf(System.currentTimeMillis() - startTime));
            return value;
        } catch (Throwable th) {
            storageLock.readLock().unlock();
        }
    }

    private void putLocked(@NonNull String key, @NonNull V value) throws StorageException {
        Throwable e;
        String message;
        Throwable th;
        Closeable out = null;
        File file = null;
        try {
            file = getFile(key);
            FileUtils.mkdirsChecked(file.getParentFile());
            Closeable out2 = new BufferedOutputStream(new FileOutputStream(file, false));
            try {
                this.serializer.write(value, out2);
                IOUtils.closeSilently(out2);
            } catch (IOException e2) {
                e = e2;
                out = out2;
                try {
                    Logger.m180e(e, "putLocked: %s", e);
                    if (file != null) {
                        try {
                            file.delete();
                        } catch (Throwable th2) {
                        }
                    }
                    message = "stream_cache_write\nKey: " + key + "\nPath:\n " + FilePathPrettyPrinter.getFileModifiersWithParents(file);
                    GrayLog.log(message, e);
                    Logger.m179e(e, message);
                    throw new StorageException("Failed to store value for key=" + key, e);
                } catch (Throwable th3) {
                    th = th3;
                    IOUtils.closeSilently(out);
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                out = out2;
                IOUtils.closeSilently(out);
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
            Logger.m180e(e, "putLocked: %s", e);
            if (file != null) {
                file.delete();
            }
            message = "stream_cache_write\nKey: " + key + "\nPath:\n " + FilePathPrettyPrinter.getFileModifiersWithParents(file);
            GrayLog.log(message, e);
            Logger.m179e(e, message);
            throw new StorageException("Failed to store value for key=" + key, e);
        }
    }

    private void removeLocked(@NonNull String key) throws StorageException {
        try {
            File file = getFile(key);
            if (!file.exists()) {
                Logger.m185w("removeLocked: file does not exist for key=%s: %s", key, file);
            } else if (file.delete()) {
                Logger.m173d("removeLocked: deleted key=%s file=%s", key, file);
            } else {
                throw new StorageException("Failed to delete value for key=" + key + ", file=" + file);
            }
        } catch (IOException e) {
            Logger.m180e(e, "removeLocked: %s", e);
            throw new StorageException("Failed to delete value for key=" + key, e);
        }
    }

    @Nullable
    private V getLocked(@NonNull String key) throws StorageException {
        FileNotFoundException e;
        Throwable th;
        IOException e2;
        Closeable in = null;
        File file = null;
        try {
            file = getFile(key);
            Closeable in2 = new BufferedInputStream(new FileInputStream(file));
            try {
                V read = this.serializer.read(in2);
                IOUtils.closeSilently(in2);
                in = in2;
                return read;
            } catch (FileNotFoundException e3) {
                e = e3;
                in = in2;
                try {
                    Logger.m185w("readLocked: %s", e);
                    IOUtils.closeSilently(in);
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    IOUtils.closeSilently(in);
                    throw th;
                }
            } catch (IOException e4) {
                e2 = e4;
                in = in2;
                Logger.m180e(e2, "readLocked: failed to read value for key=%s", key);
                if (file != null) {
                    try {
                        Logger.m173d("readLocked: delete broken file: %s", file.getPath());
                        file.delete();
                    } catch (Throwable th3) {
                    }
                }
                GrayLog.log("stream_cache_read\nKey: " + key, e2);
                throw new StorageException("Failed to read value for key: " + key, e2);
            } catch (Throwable th4) {
                th = th4;
                in = in2;
                IOUtils.closeSilently(in);
                throw th;
            }
        } catch (FileNotFoundException e5) {
            e = e5;
            Logger.m185w("readLocked: %s", e);
            IOUtils.closeSilently(in);
            return null;
        } catch (IOException e6) {
            e2 = e6;
            Logger.m180e(e2, "readLocked: failed to read value for key=%s", key);
            if (file != null) {
                Logger.m173d("readLocked: delete broken file: %s", file.getPath());
                file.delete();
            }
            GrayLog.log("stream_cache_read\nKey: " + key, e2);
            throw new StorageException("Failed to read value for key: " + key, e2);
        }
    }

    private File getFile(String key) throws IOException {
        return new File(this.baseDir, URLEncoder.encode(key, StringUtils.UTF8));
    }

    private ReadWriteLock obtainLock(@NonNull String key) {
        ReadWriteLock lock;
        synchronized (this.fileLocks) {
            lock = (ReadWriteLock) this.fileLocks.get(key);
            if (lock == null) {
                lock = new ReentrantReadWriteLock();
                this.fileLocks.put(key, lock);
            }
        }
        return lock;
    }
}
