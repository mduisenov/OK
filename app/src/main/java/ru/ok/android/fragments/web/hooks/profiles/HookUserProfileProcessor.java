package ru.ok.android.fragments.web.hooks.profiles;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;

public class HookUserProfileProcessor extends HookBaseProcessor {
    private final HookUserProfileListener listener;

    public interface HookUserProfileListener {
        void onUserProfileSelected(String str);
    }

    public HookUserProfileProcessor(HookUserProfileListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/user";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onUserProfileSelected(getUidFromQueryParam(uri));
        }
    }

    protected boolean isUriMatches(Uri uri) {
        return uri.getPath().contains(new StringBuilder().append(getHookName()).append("/").toString()) || uri.getPath().equals(getHookName());
    }

    protected String getUidFromQueryParam(Uri uri) {
        return uri == null ? null : uri.getQueryParameter("uid");
    }
}
