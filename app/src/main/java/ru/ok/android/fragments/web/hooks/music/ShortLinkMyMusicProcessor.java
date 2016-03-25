package ru.ok.android.fragments.web.hooks.music;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkMyMusicProcessor extends ShortLinkBaseProcessor {
    private final MyMusicListener listener;

    public interface MyMusicListener {
        void onShowMyMusic();
    }

    public ShortLinkMyMusicProcessor(MyMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/music/my";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onShowMyMusic();
        }
    }
}
