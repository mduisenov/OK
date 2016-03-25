package ru.ok.android.fragments.web.hooks.discussion;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;
import ru.ok.model.Discussion;

public final class HookDiscussionCommentsProcessor extends HookBaseProcessor {
    private HookDiscussionCommentsListener listener;

    public interface HookDiscussionCommentsListener {
        void onDiscussionCommentsSelected(Discussion discussion, Uri uri);
    }

    public HookDiscussionCommentsProcessor(HookDiscussionCommentsListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/dscnComments";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onDiscussionCommentsSelected(new Discussion(uri.getQueryParameter("id"), uri.getQueryParameter("type")), uri);
        }
    }
}
