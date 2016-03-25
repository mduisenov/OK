package ru.ok.android.fragments.web.hooks.discussion;

import ru.ok.android.fragments.web.hooks.discussion.HookDiscussionInfoBaseProcessor.HookDiscussionInfoListener;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;

public final class HookDiscussionInfoMediaNewsProcessor extends HookDiscussionInfoBaseProcessor {
    public HookDiscussionInfoMediaNewsProcessor(HookDiscussionInfoListener listener) {
        super(listener);
    }

    protected Type getDiscussionType() {
        return Type.GROUP_TOPIC;
    }

    protected String getHookName() {
        return "/apphook/groupMediaNews";
    }
}
