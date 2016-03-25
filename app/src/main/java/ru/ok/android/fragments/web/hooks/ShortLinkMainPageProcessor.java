package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import ru.ok.android.fragments.web.client.WebClientUtils;
import ru.ok.android.fragments.web.client.interceptor.shortlinks.ShortLinksBridge;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public final class ShortLinkMainPageProcessor extends ShortLinkBaseProcessor {
    private final ShortLinksBridge shortLinksBridge;

    public ShortLinkMainPageProcessor(ShortLinksBridge shortLinksBridge) {
        this.shortLinksBridge = shortLinksBridge;
    }

    protected boolean isUriMatches(Uri uri) {
        return WebClientUtils.isOkHost(uri) && uri.getPathSegments() != null && uri.getPathSegments().isEmpty();
    }

    protected String getHookName() {
        return null;
    }

    protected void onHookExecute(Uri uri) {
        this.shortLinksBridge.onShowMainPage();
    }
}
