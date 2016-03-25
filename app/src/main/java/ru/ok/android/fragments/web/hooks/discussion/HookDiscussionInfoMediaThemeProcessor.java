package ru.ok.android.fragments.web.hooks.discussion;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.discussion.HookDiscussionInfoBaseProcessor.HookDiscussionInfoListener;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;

public final class HookDiscussionInfoMediaThemeProcessor extends HookDiscussionInfoBaseProcessor {
    public HookDiscussionInfoMediaThemeProcessor(HookDiscussionInfoListener listener) {
        super(listener);
    }

    protected Type getDiscussionType() {
        return Type.GROUP_TOPIC;
    }

    protected String getHookName() {
        return "/apphook/groupMediaTheme";
    }

    protected boolean isUriMatches(Uri uri) {
        return uri.getPath().endsWith("/apphook/groupMediaTheme");
    }
}
