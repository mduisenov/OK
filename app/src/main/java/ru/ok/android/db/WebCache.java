package ru.ok.android.db;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import ru.ok.android.utils.Logger;

public class WebCache {
    private WebCacheDb webCacheDb;

    public static class UrlTitleInfo {
        public boolean subtitleExist;
        public String title;

        public UrlTitleInfo(String title, boolean subtitleExist) {
            this.title = title;
            this.subtitleExist = subtitleExist;
        }
    }

    public WebCache(Context context) {
        this.webCacheDb = new WebCacheDb(context);
    }

    public void saveTitle(String url, String title) {
        if (title == null) {
            Logger.m184w("WebCache not save null title");
        } else {
            this.webCacheDb.insertTitle(getUrlId(url), title);
        }
    }

    public UrlTitleInfo getTitle(String url) {
        return this.webCacheDb.queryTitle(getUrlId(url));
    }

    public void cleanTitles() {
        this.webCacheDb.deleteAllFromTitles();
    }

    public void onLocaleChanged() {
        cleanTitles();
    }

    private static String getUrlId(String url) {
        Uri uri = Uri.parse(url);
        String stCmd = uri.getQueryParameter("st.cmd");
        return !TextUtils.isEmpty(stCmd) ? stCmd : uri.getPath();
    }

    public void saveSubtitleExist(String url, boolean exist) {
        this.webCacheDb.insertSubtitleExist(getUrlId(url), exist);
    }
}
