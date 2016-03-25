package ru.ok.android.utils;

import android.net.Uri;
import android.text.TextUtils;

public class URLUtil {
    public static boolean isStubUrl(String url) {
        return TextUtils.isEmpty(url) || url.contains("/res/stub_");
    }

    public static String prepareDisplayableLink(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        try {
            String link = Uri.parse(url).buildUpon().clearQuery().path(null).fragment(null).build().toString();
            int schemeSepPos = link.indexOf("://");
            if (schemeSepPos >= 0) {
                return link.substring(schemeSepPos + 3);
            }
            return link;
        } catch (Exception e) {
            Logger.m180e(e, "Failed to parse URL: %s", url);
            return url;
        }
    }
}
