package ru.ok.android.fragments.web.hooks.hooklinks;

import android.net.Uri;
import android.support.annotation.Nullable;
import ru.ok.android.fragments.web.hooks.ShortLinkParser;

public class ShortLinkMySentPresents extends ShortLinkBaseProcessor {
    private final ShortLinkMySentPresentsListener listener;

    public interface ShortLinkMySentPresentsListener {
        void onShowMySentPresents(@Nullable String str);
    }

    public ShortLinkMySentPresents(ShortLinkMySentPresentsListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "gifts/sent";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onShowMySentPresents(new ShortLinkParser(uri.toString(), getHookName()).getValue("tkn"));
        }
    }
}
