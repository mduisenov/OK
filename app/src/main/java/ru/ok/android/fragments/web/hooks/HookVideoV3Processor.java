package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import com.google.android.gms.plus.PlusShare;

public class HookVideoV3Processor extends HookBaseProcessor {
    private final Listener listener;

    public interface Listener {
        void onShowVideoV3(String str, String str2, boolean z);
    }

    public HookVideoV3Processor(Listener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/v3video";
    }

    protected void onHookExecute(Uri uri) {
        this.listener.onShowVideoV3(uri.getQueryParameter("id"), uri.getQueryParameter(PlusShare.KEY_CALL_TO_ACTION_URL), "1".equals(uri.getQueryParameter("blocked")));
    }
}
