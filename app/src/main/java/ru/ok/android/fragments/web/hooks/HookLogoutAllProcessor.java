package ru.ok.android.fragments.web.hooks;

import android.net.Uri;

public class HookLogoutAllProcessor extends HookBaseProcessor {
    private OnLogoutAllListener listener;

    public interface OnLogoutAllListener {
        void onLogoutAll();
    }

    public HookLogoutAllProcessor(OnLogoutAllListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/logoutAll";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onLogoutAll();
        }
    }
}
