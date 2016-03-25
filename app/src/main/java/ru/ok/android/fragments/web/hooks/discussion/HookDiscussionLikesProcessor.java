package ru.ok.android.fragments.web.hooks.discussion;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;
import ru.ok.model.Discussion;

public final class HookDiscussionLikesProcessor extends HookBaseProcessor {
    private HookDiscussionLikesListener listener;

    public interface HookDiscussionLikesListener {
        void onDiscussionLikesSelected(Discussion discussion);
    }

    public HookDiscussionLikesProcessor(HookDiscussionLikesListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/dscnLikes";
    }

    protected void onHookExecute(Uri uri) {
        this.listener.onDiscussionLikesSelected(new Discussion(uri.getQueryParameter("id"), uri.getQueryParameter("type")));
    }
}
