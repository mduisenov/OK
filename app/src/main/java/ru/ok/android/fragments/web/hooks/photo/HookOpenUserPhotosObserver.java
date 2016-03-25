package ru.ok.android.fragments.web.hooks.photo;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;

public class HookOpenUserPhotosObserver extends HookBaseProcessor {
    private final OnOpenUserPhotosListener onOpenUserPhotosListener;

    public interface OnOpenUserPhotosListener {
        void onOpenUserPhotos(String str);
    }

    public HookOpenUserPhotosObserver(OnOpenUserPhotosListener onOpenUserPhotosListener) {
        this.onOpenUserPhotosListener = onOpenUserPhotosListener;
    }

    protected String getHookName() {
        return "/apphook/userPhotos";
    }

    protected void onHookExecute(Uri uri) {
        String uid = uri.getQueryParameter("uid");
        if (this.onOpenUserPhotosListener != null) {
            this.onOpenUserPhotosListener.onOpenUserPhotos(uid);
        }
    }
}
