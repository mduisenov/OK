package ru.ok.android.fragments.web;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import ru.ok.android.app.WebHttpLoader;
import ru.ok.android.app.WebHttpLoader.LoadUrlTaskCommon;
import ru.ok.android.app.WebHttpLoader.RequestType;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;

public class WebExternalUrlManager {
    private final Context context;

    /* renamed from: ru.ok.android.fragments.web.WebExternalUrlManager.1 */
    class C03341 extends LoadUrlTaskCommon {
        C03341(String x0, RequestType x1) {
            super(x0, x1);
        }

        public void onFailed(int errorCode) {
            Logger.m173d("fail load syslink: %s", this.url);
            Uri uri = Uri.parse(this.url);
            if (uri == null || TextUtils.isEmpty(uri.getScheme()) || !uri.getScheme().equals("market")) {
                WebExternalUrlManager.onOutLinkOpenInBrowser(WebExternalUrlManager.this.context, this.url);
            } else if (WebExternalUrlManager.this.context != null) {
                WebExternalUrlManager.onOpenInDeviceApp(WebExternalUrlManager.this.context, WebExternalUrlManager.getMarketURI(WebExternalUrlManager.this.context, this.url));
            }
        }

        public void onRedirect(String newUrl) {
            WebExternalUrlManager.this.preProcessUrl(newUrl);
        }

        public void onLoadedContent(String urlFinal) {
            WebExternalUrlManager.onOutLinkOpenInBrowser(WebExternalUrlManager.this.context, urlFinal);
        }
    }

    public WebExternalUrlManager(Context context) {
        this.context = context;
    }

    public void preProcessUrl(String url) {
        WebHttpLoader.from(this.context).postLoadUrl(new C03341(url, RequestType.HEAD));
    }

    public static void onOutLinkOpenInBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        if ("market".equals(uri.getScheme())) {
            uri = getMarketURI(context, url);
        }
        onOpenInDeviceApp(context, uri);
    }

    private static Uri getMarketURI(Context context, String marketURL) {
        PackageManager packageManager = context.getPackageManager();
        Uri marketUri = Uri.parse(marketURL);
        return new Intent("android.intent.action.VIEW").setData(marketUri).resolveActivity(packageManager) != null ? marketUri : Uri.parse("https://play.google.com/store/apps/" + marketURL.substring("market://".length()));
    }

    public static void onOpenInDeviceApp(Context context, Uri uri) {
        NavigationHelper.openInExternalApp(context, uri);
    }
}
