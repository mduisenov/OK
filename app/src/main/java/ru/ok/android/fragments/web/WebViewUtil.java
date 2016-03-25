package ru.ok.android.fragments.web;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Pair;
import android.webkit.WebSettings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.utils.Constants.Api;

public class WebViewUtil {
    private static List<String> okCookieDomainUrls;
    private static String tempUserAgent;
    private static String webViewUserAgent;

    public static Pair<String, String> parseMimeTypeAndEncoding(String contentType) {
        if (contentType == null) {
            return new Pair(null, null);
        }
        int separatorIdx = contentType.indexOf(59);
        if (separatorIdx == -1) {
            return new Pair(contentType, null);
        }
        String mimeType = contentType.substring(0, separatorIdx);
        String charset = null;
        String parameter = contentType.substring(separatorIdx + 1);
        if (parameter.startsWith("charset=")) {
            charset = parameter.substring(8);
        }
        return new Pair(mimeType, charset);
    }

    public static String getOkAppUserAgentPart() {
        return "OkApp";
    }

    public static String getWebViewUserAgent(Context context) {
        if (!TextUtils.isEmpty(webViewUserAgent)) {
            return webViewUserAgent;
        }
        if (VERSION.SDK_INT >= 17) {
            String defaultUserAgent = getDefaultUserAgent(context);
            if (!TextUtils.isEmpty(defaultUserAgent)) {
                webViewUserAgent = defaultUserAgent + " " + getOkAppUserAgentPart();
                Logger.m173d("Using default User-Agent: %s", webViewUserAgent);
                return webViewUserAgent;
            }
        }
        String savedUserAgent = Settings.getStrValueInvariable(context, "web_view_user_agent", null);
        if (!TextUtils.isEmpty(savedUserAgent)) {
            return savedUserAgent;
        }
        if (TextUtils.isEmpty(tempUserAgent)) {
            tempUserAgent = createUserAgent(context);
            Logger.m173d("Using temp User-Agent: %s", tempUserAgent);
        }
        return tempUserAgent;
    }

    @TargetApi(17)
    private static String getDefaultUserAgent(Context context) {
        try {
            return WebSettings.getDefaultUserAgent(context);
        } catch (Exception e) {
            Logger.m180e(e, "Failed to get default User-Agent: %s", e);
            return null;
        }
    }

    static void setWebViewUserAgent(Context context, String userAgent) {
        Logger.m173d("User-Agent: %s", userAgent);
        if (VERSION.SDK_INT < 17) {
            webViewUserAgent = userAgent;
            Settings.getEditorInvariable(context).putString("web_view_user_agent", userAgent).apply();
        }
    }

    private static String createUserAgent(Context context) {
        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();
        String variant = locale.getVariant();
        if (TextUtils.isEmpty(variant)) {
            variant = lang;
        }
        return "Mozilla/5.0 (Linux; U; Android " + VERSION.RELEASE + "; " + lang + "-" + variant + "; " + Build.MANUFACTURER + " " + Build.MODEL + " Build/" + Build.ID + ") AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 " + (DeviceUtils.getType(context) != DeviceLayoutType.SMALL ? "" : "Mobile ") + "Safari/534.30 " + getOkAppUserAgentPart();
    }

    public static List<String> getOkCookieDomainUrls() {
        if (okCookieDomainUrls == null) {
            String webBaseUrl = ConfigurationPreferences.getInstance().getWebServer();
            List<String> domainUrls = new ArrayList(Api.COOKIE_APPCAPS_DOMAIN_URLS.length + 1);
            for (String url : Api.COOKIE_APPCAPS_DOMAIN_URLS) {
                domainUrls.add(url);
            }
            if (!(TextUtils.isEmpty(webBaseUrl) || domainUrls.contains(webBaseUrl))) {
                domainUrls.add(webBaseUrl);
            }
            okCookieDomainUrls = Collections.unmodifiableList(domainUrls);
        }
        return okCookieDomainUrls;
    }
}
