package ru.ok.android.fragments.web.hooks.music;

import android.net.Uri;
import java.util.List;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkMusicArtistProcessor extends ShortLinkBaseProcessor {
    private final ArtistMusicListener listener;

    public interface ArtistMusicListener {
        void onShowArtistMusic(String str);
    }

    public ShortLinkMusicArtistProcessor(ArtistMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/music/artist/";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            List<String> segs = uri.getPathSegments();
            this.listener.onShowArtistMusic((String) segs.get(segs.size() - 1));
        }
    }
}
