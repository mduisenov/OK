package ru.ok.android.fragments.web.hooks;

import android.net.Uri;

public final class HookPlayTrackProcessor extends HookBaseProcessor {
    private OnPlayTrackListener listener;

    public interface OnPlayTrackListener {
        void onPlayTrack(Long l);
    }

    public HookPlayTrackProcessor(OnPlayTrackListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/musicStatusPlay";
    }

    protected void onHookExecute(Uri uri) {
        String trackIds = uri.getQueryParameter("tid");
        if (this.listener != null) {
            this.listener.onPlayTrack(Long.valueOf(Long.parseLong(trackIds)));
        }
    }
}
