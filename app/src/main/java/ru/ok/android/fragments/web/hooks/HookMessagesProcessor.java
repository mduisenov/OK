package ru.ok.android.fragments.web.hooks;

import android.net.Uri;

public class HookMessagesProcessor extends HookBaseProcessor {
    private OnShowMessagesUrlLoadingListener onShowMessagesUrlLoadingListener;

    public interface OnShowMessagesUrlLoadingListener {
        void onShowMessages(String str);
    }

    public HookMessagesProcessor(OnShowMessagesUrlLoadingListener onShowMessagesUrlLoadingListener) {
        this.onShowMessagesUrlLoadingListener = onShowMessagesUrlLoadingListener;
    }

    private void notifyLoadingListenerShowMessages(String userId) {
        if (this.onShowMessagesUrlLoadingListener != null) {
            this.onShowMessagesUrlLoadingListener.onShowMessages(userId);
        }
    }

    protected String getHookName() {
        return "/apphook/messages";
    }

    protected void onHookExecute(Uri uri) {
        notifyLoadingListenerShowMessages(uri.getQueryParameter("uid"));
    }
}
