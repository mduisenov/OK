package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import android.text.TextUtils;

public class HookVideoV2Processor extends HookBaseProcessor {
    private final HookVideoV2Listener listener;

    public interface HookVideoV2Listener {
        void onShowVideoV2(String str);
    }

    public HookVideoV2Processor(HookVideoV2Listener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/v2video";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            String videoId = uri.getQueryParameter("id");
            if (!TextUtils.isEmpty(videoId)) {
                this.listener.onShowVideoV2(String.valueOf(Long.parseLong(videoId) ^ 265224201205L));
            }
        }
    }
}
