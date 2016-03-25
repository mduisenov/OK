package ru.ok.android.fragments.web.hooks.music;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkHistoryMusicProcessor extends ShortLinkBaseProcessor {
    private final HistoryMusicListener listener;

    public interface HistoryMusicListener {
        void onShowHistoryMusic();
    }

    public ShortLinkHistoryMusicProcessor(HistoryMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/music/history";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onShowHistoryMusic();
        }
    }
}
