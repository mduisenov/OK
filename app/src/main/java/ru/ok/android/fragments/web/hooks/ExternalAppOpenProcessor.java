package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import ru.ok.android.fragments.web.client.interceptor.hooks.AppHooksInterceptor.HookUrlProcessor;

public class ExternalAppOpenProcessor implements HookUrlProcessor {
    private ExternalAppOpenListener listener;

    public interface ExternalAppOpenListener {
        void onExternalOpen(Uri uri);
    }

    public ExternalAppOpenProcessor(ExternalAppOpenListener listener) {
        this.listener = listener;
    }

    public boolean handleWebHookEvent(String url) {
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        if (scheme == null || scheme.length() <= 0 || scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
            return false;
        }
        if (this.listener != null) {
            this.listener.onExternalOpen(uri);
        }
        return true;
    }
}
