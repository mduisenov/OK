package ru.ok.android.onelog;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import ru.ok.android.onelog.api.ApiConfig;
import ru.ok.onelog.Item;

public final class OneLog implements Appender {
    private static final AtomicReference<ApiConfig> API_CONFIG;
    private static final ConcurrentHashMap<String, OneLog> INSTANCES;
    private static volatile Context context;
    private static OneLog lastInstance;
    private final File appendFile;
    private final String collector;
    private final File uploadFile;
    private final Lock uploadLock;
    private final AtomicReference<Uploader> uploader;
    private final AtomicReference<Worker> worker;

    static {
        API_CONFIG = new AtomicReference(ApiConfig.EMPTY);
        INSTANCES = new ConcurrentHashMap();
    }

    private OneLog(@NonNull String collector) {
        this.worker = new AtomicReference();
        this.uploader = new AtomicReference();
        this.collector = collector;
        File collectorDir = new File(new File(context.getFilesDir(), "onelog"), collector);
        this.appendFile = new File(collectorDir, "append");
        this.uploadFile = new File(collectorDir, "upload");
        this.uploadLock = new ReentrantLock();
    }

    public static void attachBaseContext(@NonNull Context baseContext) {
        Context appContext = baseContext.getApplicationContext();
        if (context == null) {
            context = appContext;
        } else if (!context.equals(appContext)) {
            throw new IllegalStateException("Different context already attached");
        }
    }

    public static void attachBaseUrl(Uri uri) {
        ApiConfig config;
        do {
            config = (ApiConfig) API_CONFIG.get();
        } while (!API_CONFIG.compareAndSet(config, config.withUri(uri)));
    }

    public static void attachApplicationKey(String appKey, String appSecret) {
        ApiConfig config;
        do {
            config = (ApiConfig) API_CONFIG.get();
        } while (!API_CONFIG.compareAndSet(config, config.withApplication(appKey, appSecret)));
    }

    public static void attachSessionKey(String sessionKey, String sessionSecret) {
        ApiConfig config;
        do {
            config = (ApiConfig) API_CONFIG.get();
        } while (!API_CONFIG.compareAndSet(config, config.withSession(sessionKey, sessionSecret)));
    }

    static ApiConfig getApiConfig() {
        return (ApiConfig) API_CONFIG.get();
    }

    public static OneLog getInstance(@NonNull String collector) {
        if (context == null) {
            throw new IllegalStateException("Application context not attached to OneLog");
        }
        OneLog cachedInstance = lastInstance;
        if (cachedInstance != null && collector.equals(cachedInstance.collector)) {
            return cachedInstance;
        }
        OneLog oldInstance = (OneLog) INSTANCES.get(collector);
        if (oldInstance != null) {
            lastInstance = oldInstance;
            return oldInstance;
        }
        OneLog newInstance = new OneLog(collector);
        OneLog fastInstance = (OneLog) INSTANCES.putIfAbsent(collector, newInstance);
        if (fastInstance != null) {
            lastInstance = fastInstance;
            return fastInstance;
        }
        lastInstance = newInstance;
        return newInstance;
    }

    public static void log(@NonNull Item item) {
        getInstance(ItemExt.nonNullCollector(item)).append(item);
    }

    public static void priorityLog(@NonNull Item item) {
        OneLog instance = getInstance(ItemExt.nonNullCollector(item));
        instance.append(item);
        instance.upload();
    }

    public void append(@NonNull Item item) {
        String itemCollector = item.collector();
        if (itemCollector == null || itemCollector.equals(this.collector)) {
            getWorker().append(item);
            return;
        }
        throw new IllegalArgumentException("Unexpected collector " + itemCollector);
    }

    public void flush() {
        getWorker().flush();
    }

    private void upload() {
        getWorker().upload();
    }

    void grab() throws IOException {
        try {
            this.uploadLock.lock();
            getWorker().grab(this.uploadFile);
        } finally {
            this.uploadLock.unlock();
        }
    }

    Worker getWorker() {
        Worker oldWorker = (Worker) this.worker.get();
        if (oldWorker != null) {
            return oldWorker;
        }
        Worker newYorker = new Worker(context, this.appendFile, new ReentrantLock(), this.collector);
        if (this.worker.compareAndSet(null, newYorker)) {
            return newYorker;
        }
        return (Worker) this.worker.get();
    }

    Uploader getUploader() {
        Uploader oldUploader = (Uploader) this.uploader.get();
        if (oldUploader != null) {
            return oldUploader;
        }
        Uploader newUploader = new Uploader(context, this.uploadFile, this.uploadLock, this.collector);
        if (this.uploader.compareAndSet(null, newUploader)) {
            return newUploader;
        }
        return (Uploader) this.uploader.get();
    }
}
