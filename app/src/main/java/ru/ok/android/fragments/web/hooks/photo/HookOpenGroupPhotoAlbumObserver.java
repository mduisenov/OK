package ru.ok.android.fragments.web.hooks.photo;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;

public class HookOpenGroupPhotoAlbumObserver extends HookBaseProcessor {
    private OnOpenGroupPhotoAlbumListener openGroupPhotoAlbumListener;

    public interface OnOpenGroupPhotoAlbumListener {
        void onOpenGroupPhotoAlbum(String str, String str2);
    }

    public HookOpenGroupPhotoAlbumObserver(OnOpenGroupPhotoAlbumListener listener) {
        this.openGroupPhotoAlbumListener = listener;
    }

    protected String getHookName() {
        return "/apphook/groupPhotoAlbum";
    }

    protected void onHookExecute(Uri uri) {
        String gid = uri.getQueryParameter("gid");
        String aid = uri.getQueryParameter("aid");
        if (this.openGroupPhotoAlbumListener != null) {
            this.openGroupPhotoAlbumListener.onOpenGroupPhotoAlbum(gid, aid);
        }
    }
}
