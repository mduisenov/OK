package ru.ok.android.fragments.web.hooks;

import android.net.Uri;

public class HookLogoutProcessor extends HookBaseProcessor {
    private OnLogoutUrlLoadingListener onLogoutUrlLoadingListener;

    public interface OnLogoutUrlLoadingListener {
        void onLogoutUrlLoading();
    }

    public HookLogoutProcessor(OnLogoutUrlLoadingListener onLogoutUrlLoadingListener) {
        this.onLogoutUrlLoadingListener = onLogoutUrlLoadingListener;
    }

    protected String getHookName() {
        return "/apphook/logoff";
    }

    protected void onHookExecute(Uri uri) {
        notifyLoadingListenerOnStartLogout();
    }

    private void notifyLoadingListenerOnStartLogout() {
        if (this.onLogoutUrlLoadingListener != null) {
            this.onLogoutUrlLoadingListener.onLogoutUrlLoading();
        }
    }
}
