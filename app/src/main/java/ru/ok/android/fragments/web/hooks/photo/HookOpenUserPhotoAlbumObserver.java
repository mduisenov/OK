package ru.ok.android.fragments.web.hooks.photo;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;

public class HookOpenUserPhotoAlbumObserver extends HookBaseProcessor {
    private final OnOpenUserPhotoAlbumListener onOpenUserPhotoListener;

    public interface OnOpenUserPhotoAlbumListener {
        void onOpenUserPhotoAlbum(String str, String str2);
    }

    public HookOpenUserPhotoAlbumObserver(OnOpenUserPhotoAlbumListener onOpenUserPhotosListener) {
        this.onOpenUserPhotoListener = onOpenUserPhotosListener;
    }

    protected String getHookName() {
        return "/apphook/userPhotoAlbum";
    }

    protected void onHookExecute(Uri uri) {
        String uid = uri.getQueryParameter("uid");
        String aid = uri.getQueryParameter("aid");
        if (this.onOpenUserPhotoListener != null) {
            this.onOpenUserPhotoListener.onOpenUserPhotoAlbum(uid, aid);
        }
    }
}
