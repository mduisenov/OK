package ru.ok.android.app;

import android.content.Context;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;
import bo.pic.android.media.ImageLoader;
import bo.pic.android.media.MediaContentType;
import bo.pic.android.media.bitmap.SoftReferenceBitmapPool;
import bo.pic.android.media.cache.BaseDiskCache;
import bo.pic.android.media.cache.CacheKey;
import bo.pic.android.media.cache.DiskCache;
import bo.pic.android.media.cache.ImageCacheUtils;
import bo.pic.android.media.cache.MemoryCache;
import bo.pic.android.media.cache.MemoryCache.RemoveFromCacheListener;
import bo.pic.android.media.content.MediaContent;
import bo.pic.android.media.content.animation.AnimatedImageContent;
import bo.pic.android.media.download.CompositeImageDownloader;
import bo.pic.android.media.download.FileSystemImageDownloader;
import bo.pic.android.media.download.HttpAsyncClientImageDownloader;
import bo.pic.android.media.download.ImageDownloader;
import bo.pic.android.media.util.Function;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import ru.ok.android.http.ProtocolException;
import ru.ok.android.http.client.config.RequestConfig;
import ru.ok.android.http.conn.ssl.SSLConnectionSocketFactory;
import ru.ok.android.http.impl.client.LaxRedirectStrategy;
import ru.ok.android.http.impl.nio.client.CloseableHttpAsyncClient;
import ru.ok.android.http.impl.nio.client.HttpAsyncClients;
import ru.ok.android.http.impl.nio.reactor.IOReactorConfig;

public class GifAsMp4ImageLoaderHelper {
    public static final MediaContentType GIF;
    private static final ExecutorService sDiskCacheExecutor;
    private static final ExecutorService sFileSystemExecutor;
    private static ImageLoader sImageLoader;

    /* renamed from: ru.ok.android.app.GifAsMp4ImageLoaderHelper.1 */
    static class C02081 implements ThreadFactory {
        private final AtomicInteger counter;
        final /* synthetic */ String val$name;

        /* renamed from: ru.ok.android.app.GifAsMp4ImageLoaderHelper.1.1 */
        class C02071 implements Runnable {
            final /* synthetic */ Runnable val$runnable;

            C02071(Runnable runnable) {
                this.val$runnable = runnable;
            }

            public void run() {
                Process.setThreadPriority(-2);
                this.val$runnable.run();
            }
        }

        C02081(String str) {
            this.val$name = str;
            this.counter = new AtomicInteger();
        }

        public Thread newThread(@NonNull Runnable runnable) {
            Thread thread = new Thread(new C02071(runnable));
            thread.setDaemon(true);
            thread.setName("ImageLoader-" + this.val$name + "#" + this.counter.incrementAndGet());
            thread.setPriority(1);
            return thread;
        }
    }

    /* renamed from: ru.ok.android.app.GifAsMp4ImageLoaderHelper.2 */
    static class C02092 extends LaxRedirectStrategy {
        C02092() {
        }

        protected URI createLocationURI(String location) throws ProtocolException {
            return super.createLocationURI(location.replaceAll("https", "http"));
        }
    }

    private static class InternalDiskCache implements DiskCache<CacheKey<String>> {
        @NonNull
        private final Map<MediaContentType, DiskCache<String>> mCaches;

        /* renamed from: ru.ok.android.app.GifAsMp4ImageLoaderHelper.InternalDiskCache.1 */
        class C02101 implements Function<String, String> {
            C02101() {
            }

            public String apply(@NonNull String s) {
                return ImageCacheUtils.getDiskCacheKey(s);
            }
        }

        public InternalDiskCache(@NonNull Context context) {
            this.mCaches = new HashMap();
            this.mCaches.put(GifAsMp4ImageLoaderHelper.GIF, new BaseDiskCache(new File(context.getCacheDir(), "gif"), new C02101(), 15728640));
        }

        public byte[] put(@NonNull CacheKey<String> key, @NonNull byte[] value) {
            return (mCaches.get(key.type)).put(key.key, value);
        }

        @Nullable
        public byte[] get(@NonNull CacheKey<String> key) {
            return (mCaches.get(key.type)).get(key.key);
        }

        @NonNull
        public File getFile(CacheKey<String> key) {
            return (mCaches.get(key.type)).getFile(key.key);
        }
    }

    private static class InternalMemoryCache implements MemoryCache<CacheKey<String>, MediaContent> {
        @NonNull
        private final Map<MediaContentType, LruCache<String, MediaContent>> mCaches;
        @Nullable
        private RemoveFromCacheListener<MediaContent> mRemoveListener;

        private class AnimatedContentCache extends LruCache<String, AnimatedImageContent> {
            AnimatedContentCache(int maxSize) {
                super(maxSize);
            }

            protected int sizeOf(@NonNull String key, @NonNull AnimatedImageContent value) {
                return 1;
            }

            protected void entryRemoved(boolean evicted, String key, AnimatedImageContent oldValue, AnimatedImageContent newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (InternalMemoryCache.this.mRemoveListener != null) {
                    InternalMemoryCache.this.mRemoveListener.onRemoved(oldValue);
                }
            }
        }

        public InternalMemoryCache() {
            this.mCaches = new HashMap();
            this.mCaches.put(GifAsMp4ImageLoaderHelper.GIF, new AnimatedContentCache(10));
        }

        @Nullable
        public MediaContent put(@NonNull CacheKey<String> key, @NonNull MediaContent value) {
            return (mCaches.get(key.type)).put(key.key, value);
        }

        @Nullable
        public MediaContent get(@NonNull CacheKey<String> key) {
            return (mCaches.get(key.type)).get(key.key);
        }

        public void setRemoveFromCacheListener(@NonNull RemoveFromCacheListener<MediaContent> listener) {
            this.mRemoveListener = listener;
        }
    }

    static {
        GIF = new MediaContentType("GIF");
        sDiskCacheExecutor = createExecutorService("GifDiskCache", 2);
        sFileSystemExecutor = createExecutorService("GifFileSystemLoader", 2);
    }

    public static synchronized ImageLoader with(@NonNull Context context) {
        ImageLoader imageLoader;
        synchronized (GifAsMp4ImageLoaderHelper.class) {
            if (sImageLoader == null) {
                sImageLoader = createImageLoader(context.getApplicationContext());
            }
            imageLoader = sImageLoader;
        }
        return imageLoader;
    }

    @NonNull
    private static ImageLoader createImageLoader(@NonNull Context context) {
        return new ImageLoader(context, createImageDownloader(), new InternalMemoryCache(), new InternalDiskCache(context), new HashMap(), new SoftReferenceBitmapPool(), sDiskCacheExecutor);
    }

    @NonNull
    private static ImageDownloader createImageDownloader() {
        return new CompositeImageDownloader(new FileSystemImageDownloader(sFileSystemExecutor), new HttpAsyncClientImageDownloader(onCreateHttpClient()));
    }

    @NonNull
    private static ExecutorService createExecutorService(@NonNull String name, int count) {
        return Executors.newFixedThreadPool(count, new C02081(name));
    }

    @NonNull
    private static CloseableHttpAsyncClient onCreateHttpClient() {
        RequestConfig config = RequestConfig.custom().setRedirectsEnabled(true).setRelativeRedirectsAllowed(true).setMaxRedirects(30).setConnectTimeout(30000).setSocketTimeout(30000).build();
        CloseableHttpAsyncClient result = HttpAsyncClients.custom().setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).setDefaultRequestConfig(config).setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(2).build()).setMaxConnPerRoute(50).setMaxConnTotal(50).setRedirectStrategy(new C02092()).build();
        result.start();
        return result;
    }
}
