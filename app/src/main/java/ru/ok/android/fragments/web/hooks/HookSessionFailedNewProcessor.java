package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookSessionFailedProcessor.OnSessionFailedListener;

public class HookSessionFailedNewProcessor extends HookSessionFailedProcessor {
    protected String getHookName() {
        return "/apphook/login";
    }

    public HookSessionFailedNewProcessor(OnSessionFailedListener onSessionFailedListener) {
        super(onSessionFailedListener);
    }

    protected String getReloginUrl(Uri uri) {
        return uri.getQueryParameter("redirect");
    }
}
