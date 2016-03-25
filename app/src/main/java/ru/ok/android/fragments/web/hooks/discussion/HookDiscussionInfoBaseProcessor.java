package ru.ok.android.fragments.web.hooks.discussion;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;
import ru.ok.model.Discussion;

public abstract class HookDiscussionInfoBaseProcessor extends HookBaseProcessor {
    private final HookDiscussionInfoListener listener;

    public interface HookDiscussionInfoListener {
        void onDiscussionInfoSelected(Discussion discussion);
    }

    protected abstract Type getDiscussionType();

    public HookDiscussionInfoBaseProcessor(HookDiscussionInfoListener listener) {
        this.listener = listener;
    }

    protected final void onHookExecute(Uri uri) {
        this.listener.onDiscussionInfoSelected(new Discussion(uri.getQueryParameter("tid"), getDiscussionType().name()));
    }
}
