package ru.ok.android.fragments.web.hooks.music;

import android.net.Uri;
import java.util.List;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;
import ru.ok.android.utils.Logger;

public class ShortLinkMusicTrackProcessor extends ShortLinkBaseProcessor {
    private final MusicListener listener;

    public interface MusicListener {
        void onShowMusicTrack(long j);
    }

    public ShortLinkMusicTrackProcessor(MusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/music/track";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            List<String> segs = uri.getPathSegments();
            try {
                this.listener.onShowMusicTrack(Long.parseLong((String) segs.get(segs.size() - 1)));
            } catch (NumberFormatException e) {
                Logger.m185w("Cannot parse track ID: %s", strLong);
            }
        }
    }
}
