package ru.ok.android.fragments.web.hooks;

import android.net.Uri;

public class HookPauseTrackProcessor extends HookBaseProcessor {
    private OnPauseMusicListener listener;

    public interface OnPauseMusicListener {
        void onPauseMusic();
    }

    public HookPauseTrackProcessor(OnPauseMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/musicStatusPause";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onPauseMusic();
        }
    }
}
