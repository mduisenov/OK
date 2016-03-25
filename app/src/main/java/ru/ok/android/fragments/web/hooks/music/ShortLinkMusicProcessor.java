package ru.ok.android.fragments.web.hooks.music;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkMusicProcessor extends ShortLinkBaseProcessor {
    private final MusicListener listener;

    public interface MusicListener {
        void onShowMusic();
    }

    public ShortLinkMusicProcessor(MusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/music";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onShowMusic();
        }
    }
}
