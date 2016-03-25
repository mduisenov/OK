package ru.ok.android.fragments.web.hooks.music;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkRadioMusicProcessor extends ShortLinkBaseProcessor {
    private final HookRadioMusicListener listener;

    public interface HookRadioMusicListener {
        void onShowRadioMusic();
    }

    public ShortLinkRadioMusicProcessor(HookRadioMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/music/radio";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onShowRadioMusic();
        }
    }
}
