package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import ru.ok.android.utils.Logger;

public class HookPaymentDone extends HookBaseProcessor {
    private final HookPaymentDoneListener listener;

    public interface HookPaymentDoneListener {
        void onPaymentDone(int i);
    }

    public HookPaymentDone(HookPaymentDoneListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/paymentDone";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            int serviceId = 0;
            try {
                serviceId = Integer.parseInt(uri.getQueryParameter("srv_id"));
            } catch (Exception ex) {
                Logger.m180e(ex, "Uri: %s", uri);
            }
            this.listener.onPaymentDone(serviceId);
        }
    }
}
