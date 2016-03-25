package ru.ok.android.model.cache.music;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import ru.ok.android.model.cache.FileCache;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Storage;
import ru.ok.model.wmf.PlayTrackInfo;

public abstract class MusicBaseFileCache extends FileCache {
    private Gson gson;

    /* renamed from: ru.ok.android.model.cache.music.MusicBaseFileCache.1 */
    class C03491 implements InstanceCreator<PlayTrackInfo> {
        C03491() {
        }

        public PlayTrackInfo createInstance(Type type) {
            return new PlayTrackInfo(0, null, null, 0, 0, null, null);
        }
    }

    public static String buildFileName(String url) {
        return Uri.parse(url).getQueryParameter("fid");
    }

    public static String buildFileName(long trackId) {
        return String.valueOf(trackId);
    }

    protected MusicBaseFileCache(Context context) {
        super(context);
    }

    protected String getCacheFolderName() {
        return "music";
    }

    protected int getValueCount() {
        return 2;
    }

    protected long getCacheSize(File cacheDir) {
        return Math.min((Storage.getAvailableSize(cacheDir) + FileUtils.folderSize(cacheDir)) / 2, 8589934592L);
    }

    protected int getFileCacheVersion() {
        return AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR;
    }

    public Snapshot getInputSnapshot(String key) {
        Snapshot snapshot = null;
        try {
            DiskLruCache cache = getDiskLruCache();
            if (cache != null) {
                snapshot = cache.get(key);
            }
        } catch (Throwable e) {
            Logger.m178e(e);
        }
        return snapshot;
    }

    public PlayTrackInfo getPlayInfo(String key) {
        Snapshot snapshot = getInputSnapshot(key);
        PlayTrackInfo info = null;
        if (snapshot != null) {
            Closeable is = snapshot.getInputStream(1);
            Closeable buffer = new ByteArrayOutputStream();
            try {
                info = (PlayTrackInfo) getGson().fromJson(new InputStreamReader(is), PlayTrackInfo.class);
            } catch (Throwable e) {
                Logger.m178e(e);
            } finally {
                snapshot.close();
                IOUtils.closeSilently(buffer);
                IOUtils.closeSilently(is);
            }
        }
        return info;
    }

    public boolean isKeyContains(String key) {
        Snapshot snapshot = getInputSnapshot(key);
        if (snapshot == null) {
            return false;
        }
        snapshot.close();
        return true;
    }

    public synchronized boolean dataCache(PlayTrackInfo info, InputStream is) {
        Closeable osContent;
        Closeable osMetadata;
        boolean z = false;
        synchronized (this) {
            Editor editor = null;
            if (!TextUtils.isEmpty(info.contentUrl)) {
                try {
                    String fileName = buildFileName(info.contentUrl);
                    DiskLruCache diskLruCache = getDiskLruCache();
                    if (diskLruCache != null) {
                        editor = diskLruCache.edit(fileName);
                        if (editor != null) {
                            osContent = editor.newOutputStream(0);
                            osMetadata = editor.newOutputStream(1);
                            cacheContentStream(is, osContent);
                            cacheMetaInfo(info, osMetadata);
                            IOUtils.closeSilently(osContent);
                            IOUtils.closeSilently(osMetadata);
                            diskLruCache.flush();
                            editor.commit();
                            z = true;
                        }
                    }
                } catch (Throwable e) {
                    Logger.m178e(e);
                    abortEditor(editor);
                } catch (Throwable e2) {
                    Logger.m178e(e2);
                    abortEditor(editor);
                } catch (Throwable th) {
                    IOUtils.closeSilently(osContent);
                    IOUtils.closeSilently(osMetadata);
                }
            }
        }
        return z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void cacheContentStream(java.io.InputStream r4, java.io.OutputStream r5) throws java.io.IOException, java.lang.InterruptedException {
        /*
        r0 = new java.io.BufferedOutputStream;
        r0.<init>(r5);
        r3 = 10240; // 0x2800 float:1.4349E-41 double:5.059E-320;
        r1 = new byte[r3];
        r3 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r3 = new byte[r3];	 Catch:{ all -> 0x0026 }
        r0.write(r3);	 Catch:{ all -> 0x0026 }
    L_0x0010:
        r2 = r4.read(r1);	 Catch:{ all -> 0x0026 }
        if (r2 < 0) goto L_0x0030;
    L_0x0016:
        r3 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0026 }
        r3 = r3.isInterrupted();	 Catch:{ all -> 0x0026 }
        if (r3 == 0) goto L_0x002b;
    L_0x0020:
        r3 = new java.lang.InterruptedException;	 Catch:{ all -> 0x0026 }
        r3.<init>();	 Catch:{ all -> 0x0026 }
        throw r3;	 Catch:{ all -> 0x0026 }
    L_0x0026:
        r3 = move-exception;
        ru.ok.android.utils.IOUtils.closeSilently(r0);
        throw r3;
    L_0x002b:
        r3 = 0;
        r0.write(r1, r3, r2);	 Catch:{ all -> 0x0026 }
        goto L_0x0010;
    L_0x0030:
        ru.ok.android.utils.IOUtils.closeSilently(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.model.cache.music.MusicBaseFileCache.cacheContentStream(java.io.InputStream, java.io.OutputStream):void");
    }

    private void cacheMetaInfo(PlayTrackInfo info, OutputStream os) throws IOException {
        os.write(getGson().toJson((Object) info).getBytes());
    }

    private static void abortEditor(Editor editor) {
        if (editor != null) {
            try {
                editor.abort();
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }
    }

    private Gson getGson() {
        if (this.gson == null) {
            this.gson = new GsonBuilder().registerTypeAdapter(PlayTrackInfo.class, new C03491()).create();
        }
        return this.gson;
    }
}
