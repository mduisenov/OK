package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkGuestsProcessor extends ShortLinkBaseProcessor {
    private final HookGuestsListener listener;

    public interface HookGuestsListener {
        void onShowGuests();
    }

    public ShortLinkGuestsProcessor(HookGuestsListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/guests";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onShowGuests();
        }
    }
}
