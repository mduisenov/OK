package ru.ok.android.fragments.web.hooks.music;

import android.net.Uri;
import java.util.List;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkPlayListMusicProcessor extends ShortLinkBaseProcessor {
    private final PlayListMusicListener listener;

    public interface PlayListMusicListener {
        void onShowPlayListMusic(String str);
    }

    public ShortLinkPlayListMusicProcessor(PlayListMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/music/playlist/";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            List<String> segs = uri.getPathSegments();
            this.listener.onShowPlayListMusic((String) segs.get(segs.size() - 1));
        }
    }
}
