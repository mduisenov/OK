package ru.ok.android.fragments.web.hooks.discussion;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkDiscussionProcessor extends ShortLinkBaseProcessor {
    private final DiscussionListener listener;

    public interface DiscussionListener {
        void onShowDiscussion();
    }

    public ShortLinkDiscussionProcessor(DiscussionListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/discussions";
    }

    protected boolean isUriMatches(Uri uri) {
        return uri.getPath().endsWith(getHookName());
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onShowDiscussion();
        }
    }
}
