package ru.ok.android.fresco;

import android.net.Uri;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;

public class OdklCacheKeyFactory extends DefaultCacheKeyFactory {
    public Uri getCacheKeySourceUri(Uri sourceUri) {
        return FrescoOdkl.getUriContentDescription(sourceUri);
    }
}
