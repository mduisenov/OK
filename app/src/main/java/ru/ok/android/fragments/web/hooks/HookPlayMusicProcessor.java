package ru.ok.android.fragments.web.hooks;

import android.net.Uri;

public class HookPlayMusicProcessor extends HookBaseProcessor {
    private OnPlayMusicListener listener;

    public interface OnPlayMusicListener {
        void onPlayMusic(long j, String str, String str2);
    }

    public HookPlayMusicProcessor(OnPlayMusicListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/playMusic";
    }

    protected void onHookExecute(Uri uri) {
        String trackIds = uri.getQueryParameter("tid");
        String tracksIds = uri.getQueryParameter("tids");
        String uid = uri.getQueryParameter("uid");
        if (this.listener != null) {
            this.listener.onPlayMusic(Long.parseLong(trackIds), tracksIds, uid);
        }
    }
}
