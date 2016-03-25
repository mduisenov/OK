package ru.ok.android.ui.image;

import android.net.Uri;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.utils.LruCache;

public final class PreviewUriCache {
    private static final PreviewUriCache instance;
    private static final LruCache<Uri, Uri> uriCache;

    static {
        instance = new PreviewUriCache();
        uriCache = new LruCache(50);
    }

    private PreviewUriCache() {
    }

    public static PreviewUriCache getInstance() {
        return instance;
    }

    public void put(Uri key, Uri value) {
        uriCache.put(FrescoOdkl.getUriContentDescription(key), value);
    }

    public Uri get(Uri key) {
        return (Uri) uriCache.get(FrescoOdkl.getUriContentDescription(key));
    }
}
