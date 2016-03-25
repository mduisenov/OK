package ru.ok.android.utils;

import android.net.Uri;
import android.text.TextUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.request.ImageRequest;
import ru.ok.android.receivers.ConnectivityReceiver;

public final class PrefetchUtils {
    public static void prefetchUrl(Uri url) {
        prefetchUrl(url, true);
    }

    public static void prefetchUrl(String uri) {
        prefetchUrl(uri, true);
    }

    public static void prefetchUrl(String uri, boolean onWiFiOnly) {
        if (!TextUtils.isEmpty(uri)) {
            prefetchUrl(Uri.parse(uri), onWiFiOnly);
        }
    }

    public static void prefetchUrl(Uri uri, boolean onWiFiOnly) {
        if (uri != null) {
            if (!onWiFiOnly || ConnectivityReceiver.isWifi) {
                Fresco.getImagePipeline().prefetchToBitmapCache(ImageRequest.fromUri(uri), null);
            }
        }
    }
}
