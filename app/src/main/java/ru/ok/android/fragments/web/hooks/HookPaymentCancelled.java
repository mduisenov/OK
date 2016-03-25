package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import ru.ok.android.utils.Logger;

public class HookPaymentCancelled extends HookBaseProcessor {
    private final HookPaymentCancelledListener listener;

    public interface HookPaymentCancelledListener {
        void onPaymentCancelled(int i);
    }

    public HookPaymentCancelled(HookPaymentCancelledListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/paymentCancel";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            int serviceId = 0;
            try {
                serviceId = Integer.parseInt(uri.getQueryParameter("srv_id"));
            } catch (Exception ex) {
                Logger.m180e(ex, "Uri: %s", uri);
            }
            this.listener.onPaymentCancelled(serviceId);
        }
    }
}
