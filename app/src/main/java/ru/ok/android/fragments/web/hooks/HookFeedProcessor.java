package ru.ok.android.fragments.web.hooks;

import android.net.Uri;

public final class HookFeedProcessor extends HookBaseProcessor {
    private final HookFeedListener listener;

    public interface HookFeedListener {
        void onShowFeed();
    }

    public HookFeedProcessor(HookFeedListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/feed";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onShowFeed();
        }
    }
}
