package ru.ok.android.fragments.web.hooks.photo;

import android.net.Uri;
import android.text.TextUtils;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;
import ru.ok.android.utils.Logger;

public class HookShowUserPhotoObserver extends HookBaseProcessor {
    private final OnShowUserPhotoListener onShowUserPhotoListener;

    public interface OnShowUserPhotoListener {
        void onShowUserPhoto(String str, String str2, String str3, String[] strArr);
    }

    public HookShowUserPhotoObserver(OnShowUserPhotoListener onOpenUserPhotosListener) {
        this.onShowUserPhotoListener = onOpenUserPhotosListener;
    }

    protected String getHookName() {
        return "/apphook/userPhoto";
    }

    protected boolean isUriMatches(Uri uri) {
        return uri.getPath().endsWith("/apphook/userPhoto");
    }

    protected void onHookExecute(Uri uri) {
        String pid = uri.getQueryParameter("photoId");
        String aid = uri.getQueryParameter("aid");
        String uid = uri.getQueryParameter("uid");
        String spidsParam = uri.getQueryParameter("spids");
        String[] spids = null;
        if (!TextUtils.isEmpty(spidsParam)) {
            try {
                spids = spidsParam.split(";");
                if (spids == null || spids.length < 2) {
                    spids = null;
                }
            } catch (Throwable exc) {
                Logger.m178e(exc);
            }
        }
        if (this.onShowUserPhotoListener != null) {
            this.onShowUserPhotoListener.onShowUserPhoto(aid, pid, uid, spids);
        }
    }
}
