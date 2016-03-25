package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import android.text.TextUtils;

public class HookSyslinkProcessor extends HookBaseProcessor {
    private OnSyslinkListener listener;

    public interface OnSyslinkListener {
        void onLoadSysLink(String str);
    }

    public HookSyslinkProcessor(OnSyslinkListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/syslink";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            String urlRedirect = uri.getPath().split("/apphook/syslink/")[1];
            if (urlRedirect != null && !TextUtils.isEmpty(urlRedirect)) {
                this.listener.onLoadSysLink(urlRedirect);
            }
        }
    }
}
