package ru.ok.android.fragments.web.hooks.hooklinks;

import android.net.Uri;
import android.support.annotation.Nullable;
import ru.ok.android.fragments.web.hooks.ShortLinkParser;

public class ShortLinkMyReceivedPresents extends ShortLinkBaseProcessor {
    private final ShortLinkMyReceivedPresentsListener listener;

    public interface ShortLinkMyReceivedPresentsListener {
        void onShowMyReceivedPresents(@Nullable String str);
    }

    public ShortLinkMyReceivedPresents(ShortLinkMyReceivedPresentsListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "gifts/received";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onShowMyReceivedPresents(new ShortLinkParser(uri.toString(), getHookName()).getValue("tkn"));
        }
    }
}
