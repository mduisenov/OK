package ru.ok.android.fragments.web.client.interceptor.shortlinks;

import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.fragments.web.client.WebClientUtils;
import ru.ok.android.fragments.web.client.interceptor.UrlInterceptor;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;
import ru.ok.android.utils.Logger;

public final class ShortLinkInterceptor implements UrlInterceptor {
    private final List<ShortLinkBaseProcessor> shortLinksProcessors;

    public ShortLinkInterceptor() {
        this.shortLinksProcessors = new ArrayList();
    }

    protected void addShortLinkProcessor(ShortLinkBaseProcessor... processors) {
        for (ShortLinkBaseProcessor processor : processors) {
            this.shortLinksProcessors.add(processor);
        }
    }

    public boolean handleUrl(String url) {
        if (WebClientUtils.isShortLink(Uri.parse(url))) {
            return processShortLinks(url);
        }
        Logger.m173d("not short link: %s", url);
        return false;
    }

    private boolean processShortLinks(String url) {
        for (ShortLinkBaseProcessor shortLink : this.shortLinksProcessors) {
            if (shortLink.handleWebHookEvent(url)) {
                Logger.m173d("shortlink %s handled by %s", url, (ShortLinkBaseProcessor) i$.next());
                return true;
            }
        }
        Logger.m185w("unhandled shortlink: %s", url);
        return false;
    }
}
