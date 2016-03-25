package ru.ok.android.fragments.web.hooks;

import ru.ok.android.fragments.web.client.interceptor.hooks.AppHooksInterceptor.HookUrlProcessor;

public class HookErrorObserver implements HookUrlProcessor {
    private OnErrorUrlListener onErrorUrlListener;
    private OnUserBlockedListener onErrorUserBlockedListener;

    public interface OnErrorUrlListener {
        void onErrorUrlLoad(String str);
    }

    public interface OnUserBlockedListener {
        void onErrorUserBlocked();
    }

    public HookErrorObserver(OnErrorUrlListener onErrorUrlListener, OnUserBlockedListener onUserBlockedListener) {
        this.onErrorUrlListener = onErrorUrlListener;
        this.onErrorUserBlockedListener = onUserBlockedListener;
    }

    public boolean handleWebHookEvent(String url) {
        if (!url.contains("st.cmd=error")) {
            return false;
        }
        if (url.contains("st.ecode=errors.user.blocked") && this.onErrorUserBlockedListener != null) {
            this.onErrorUserBlockedListener.onErrorUserBlocked();
        }
        if (this.onErrorUrlListener != null) {
            this.onErrorUrlListener.onErrorUrlLoad(url);
        }
        return true;
    }
}
