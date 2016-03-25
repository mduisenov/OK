package ru.ok.android.fragments.web.hooks;

import android.net.Uri;

public class HookNativePayment extends HookBaseProcessor {
    private final HookNativePaymentListener listener;

    public interface HookNativePaymentListener {
        void onShowNativePayment();
    }

    public HookNativePayment(HookNativePaymentListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/payment";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onShowNativePayment();
        }
    }
}
