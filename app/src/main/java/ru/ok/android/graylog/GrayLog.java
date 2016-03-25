package ru.ok.android.graylog;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.concurrent.atomic.AtomicReference;
import ru.ok.android.onelog.api.ApiConfig;

public final class GrayLog {
    private static final AtomicReference<ApiConfig> API_CONFIG;
    private static final AtomicReference<Uploader> UPLOADER;
    private static volatile Context context;
    private static volatile boolean enabled;

    static {
        API_CONFIG = new AtomicReference(ApiConfig.EMPTY);
        UPLOADER = new AtomicReference();
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

    public static void setEnabled(boolean enabled) {
        enabled = enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void log(@Nullable CharSequence message) {
        if (enabled) {
            post(1, 0, message, null);
        }
    }

    public static void log(@Nullable CharSequence message, @Nullable Throwable caught) {
        if (enabled) {
            post(1, 0, message, caught);
        }
    }

    private static void post(int code, long time, @Nullable CharSequence message, @Nullable Throwable caught) {
        try {
            String placeThread = Thread.currentThread().getName();
            StackTraceElement[] rawStack = Thread.currentThread().getStackTrace();
            StackTraceElement[] placeStack = new StackTraceElement[(rawStack.length - 3)];
            System.arraycopy(rawStack, 3, placeStack, 0, placeStack.length);
            obtainUploader().post(new Item(code, time, message, caught, placeThread, placeStack));
        } catch (Throwable ignore) {
            Log.e("gray-log", "Couldn't post", ignore);
        }
    }

    private static Uploader obtainUploader() {
        Uploader oldUploader = (Uploader) UPLOADER.get();
        if (oldUploader != null) {
            return oldUploader;
        }
        Uploader newUploader = new Uploader(context);
        if (UPLOADER.compareAndSet(null, newUploader)) {
            return newUploader;
        }
        return (Uploader) UPLOADER.get();
    }
}
