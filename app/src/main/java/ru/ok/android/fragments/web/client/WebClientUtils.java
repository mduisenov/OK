package ru.ok.android.fragments.web.client;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

public final class WebClientUtils {
    public static final Pair<String, String>[] validHosts;

    static {
        validHosts = new Pair[]{new Pair("m.odnoklassniki.ru", "www.ok.ru"), new Pair("m.odnoklassniki.ru", "www.odnoklassniki.ru"), new Pair("mtest.odnoklassniki.ru", "test.odnoklassniki.ru"), new Pair("mtest.odnoklassniki.ru", "test.ok.ru"), new Pair("m.odnoklassniki.ru", "ok.ru"), new Pair("m.odnoklassniki.ru", "odnoklassniki.ru"), new Pair("mtest2.odnoklassniki.ru", "test2.odnoklassniki.ru"), new Pair("mtest2.odnoklassniki.ru", "test2.ok.ru"), new Pair("m.odnoklassniki.ru", "m.ok.ru"), new Pair("connect.odnoklassniki.ru", "connect.ok.ru"), new Pair("mtest2.ok.ru", "test.ok.ru")};
    }

    public static boolean isOkHost(Uri uri) {
        String host = uri.getHost();
        for (Pair<String, String> pair : validHosts) {
            if (((String) pair.first).equals(host) || ((String) pair.second).equals(host)) {
                return true;
            }
        }
        return false;
    }

    public static Uri createValidShortLink(Uri uri) {
        for (Pair<String, String> pair : validHosts) {
            if (((String) pair.second).equals(uri.getHost())) {
                return Uri.parse("http://" + ((String) pair.first)).buildUpon().path(uri.getPath()).scheme(uri.getScheme()).build();
            }
        }
        return uri;
    }

    public static boolean isShortLink(Uri uri) {
        return isOkHost(uri) && TextUtils.isEmpty(uri.getQuery());
    }
}
