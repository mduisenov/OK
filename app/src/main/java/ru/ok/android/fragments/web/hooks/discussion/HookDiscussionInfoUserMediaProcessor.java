package ru.ok.android.fragments.web.hooks.discussion;

import ru.ok.android.fragments.web.hooks.discussion.HookDiscussionInfoBaseProcessor.HookDiscussionInfoListener;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;

public final class HookDiscussionInfoUserMediaProcessor extends HookDiscussionInfoBaseProcessor {
    public HookDiscussionInfoUserMediaProcessor(HookDiscussionInfoListener listener) {
        super(listener);
    }

    protected Type getDiscussionType() {
        return Type.USER_STATUS;
    }

    protected String getHookName() {
        return "/apphook/userMediaTheme";
    }
}
