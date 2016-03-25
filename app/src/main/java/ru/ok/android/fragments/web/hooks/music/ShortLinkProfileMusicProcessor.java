package ru.ok.android.fragments.web.hooks.music;

import android.net.Uri;
import java.util.List;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkBaseProcessor;

public class ShortLinkProfileMusicProcessor extends ShortLinkBaseProcessor {
    private final ProfileMusicListener listener;

    public interface ProfileMusicListener {
        void onShowProfileMusic(String str);
    }

    public ShortLinkProfileMusicProcessor(ProfileMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/music/profile/";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            List<String> segs = uri.getPathSegments();
            this.listener.onShowProfileMusic((String) segs.get(segs.size() - 1));
        }
    }
}
