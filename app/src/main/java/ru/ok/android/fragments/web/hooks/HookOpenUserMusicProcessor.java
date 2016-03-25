package ru.ok.android.fragments.web.hooks;

import android.net.Uri;

public class HookOpenUserMusicProcessor extends HookBaseProcessor {
    private final HookOpenMusicListener listener;

    public interface HookOpenMusicListener {
        void onOpenMusic(String str);
    }

    public HookOpenUserMusicProcessor(HookOpenMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/userMusic";
    }

    protected void onHookExecute(Uri uri) {
        String uid = uri.getQueryParameter("uid");
        if (this.listener != null) {
            this.listener.onOpenMusic(uid);
        }
    }
}
