package ru.ok.android.fragments.web.hooks.music;

import android.net.Uri;
import java.util.List;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkMusicAlbumProcessor extends ShortLinkBaseProcessor {
    private final AlbumMusicListener listener;

    public interface AlbumMusicListener {
        void onShowAlbumMusic(String str);
    }

    public ShortLinkMusicAlbumProcessor(AlbumMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/music/album/";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            List<String> segs = uri.getPathSegments();
            this.listener.onShowAlbumMusic((String) segs.get(segs.size() - 1));
        }
    }
}
