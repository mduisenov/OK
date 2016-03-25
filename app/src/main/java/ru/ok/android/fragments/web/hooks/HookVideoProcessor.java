package ru.ok.android.fragments.web.hooks;

import android.net.Uri;

public class HookVideoProcessor extends HookBaseProcessor {
    private final HookVideoListener listener;

    public interface HookVideoListener {
        void onShowVideo(String str);
    }

    public HookVideoProcessor(HookVideoListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/video";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            String[] urls = uri.toString().split("/apphook/video/");
            if (urls.length > 1) {
                String videoUrl = urls[1];
                if (this.listener != null) {
                    this.listener.onShowVideo(videoUrl);
                }
            }
        }
    }
}
