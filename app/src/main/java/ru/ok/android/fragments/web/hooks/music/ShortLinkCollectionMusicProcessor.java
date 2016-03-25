package ru.ok.android.fragments.web.hooks.music;

import android.net.Uri;
import java.util.List;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkCollectionMusicProcessor extends ShortLinkBaseProcessor {
    private final CollectionMusicListener listener;

    public interface CollectionMusicListener {
        void onShowCollectionMusic(String str);
    }

    public ShortLinkCollectionMusicProcessor(CollectionMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/music/collection/";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            List<String> segs = uri.getPathSegments();
            this.listener.onShowCollectionMusic((String) segs.get(segs.size() - 1));
        }
    }
}
