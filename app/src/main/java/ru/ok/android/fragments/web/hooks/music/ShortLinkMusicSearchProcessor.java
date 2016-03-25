package ru.ok.android.fragments.web.hooks.music;

import android.net.Uri;
import java.util.List;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkMusicSearchProcessor extends ShortLinkBaseProcessor {
    private final SearchMusicListener listener;

    public interface SearchMusicListener {
        void onShowSearchMusic(String str);
    }

    public ShortLinkMusicSearchProcessor(SearchMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/music/search/";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            List<String> segs = uri.getPathSegments();
            this.listener.onShowSearchMusic(((String) segs.get(segs.size() - 1)).replaceAll("\\+", " "));
        }
    }
}
