package ru.ok.android.fragments.web.hooks.photo;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;

public class HookOpenGroupPhotosObserver extends HookBaseProcessor {
    private final OnOpenGroupPhotosListener onOpenGroupPhotosListener;

    public interface OnOpenGroupPhotosListener {
        void onOpenGroupPhotos(String str);
    }

    public HookOpenGroupPhotosObserver(OnOpenGroupPhotosListener onOpenGroupPhotosListener) {
        this.onOpenGroupPhotosListener = onOpenGroupPhotosListener;
    }

    protected String getHookName() {
        return "/apphook/groupPhotos";
    }

    protected void onHookExecute(Uri uri) {
        String gid = uri.getQueryParameter("gid");
        if (this.onOpenGroupPhotosListener != null) {
            this.onOpenGroupPhotosListener.onOpenGroupPhotos(gid);
        }
    }
}
