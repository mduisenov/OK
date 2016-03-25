package ru.ok.android.fragments.web.hooks.music;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkCollectionsMusicProcessor extends ShortLinkBaseProcessor {
    private final CollectionsMusicListener listener;

    public interface CollectionsMusicListener {
        void onShowCollectionsMusic();
    }

    public ShortLinkCollectionsMusicProcessor(CollectionsMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/music/collections";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onShowCollectionsMusic();
        }
    }
}
