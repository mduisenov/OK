package ru.ok.android.fragments.web.hooks.music;

import android.net.Uri;
import java.util.List;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkArtistRadioMusicProcessor extends ShortLinkBaseProcessor {
    private final ArtistRadioMusicListener listener;

    public interface ArtistRadioMusicListener {
        void onShowArtistRadioMusic(String str);
    }

    public ShortLinkArtistRadioMusicProcessor(ArtistRadioMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/music/artistradio/";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            List<String> segs = uri.getPathSegments();
            this.listener.onShowArtistRadioMusic((String) segs.get(segs.size() - 1));
        }
    }
}
