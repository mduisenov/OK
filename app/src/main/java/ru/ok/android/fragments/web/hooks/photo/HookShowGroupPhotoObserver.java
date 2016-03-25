package ru.ok.android.fragments.web.hooks.photo;

import android.net.Uri;
import android.text.TextUtils;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;
import ru.ok.android.utils.Logger;

public class HookShowGroupPhotoObserver extends HookBaseProcessor {
    private OnShowGroupPhotoListener onShowGroupPhotoListener;

    public interface OnShowGroupPhotoListener {
        void onShowGroupPhoto(String str, String str2, String str3, String[] strArr);
    }

    public HookShowGroupPhotoObserver(OnShowGroupPhotoListener onShowGroupPhotoListener) {
        this.onShowGroupPhotoListener = onShowGroupPhotoListener;
    }

    protected String getHookName() {
        return "/apphook/groupPhoto";
    }

    protected boolean isUriMatches(Uri uri) {
        return uri.getPath().endsWith("/apphook/groupPhoto");
    }

    protected void onHookExecute(Uri uri) {
        String pid = uri.getQueryParameter("photoId");
        String aid = uri.getQueryParameter("aid");
        String gid = uri.getQueryParameter("gid");
        String spidsParam = uri.getQueryParameter("spids");
        String[] spids = null;
        if (!TextUtils.isEmpty(spidsParam)) {
            try {
                spids = spidsParam.split(";");
                if (spids.length < 2) {
                    spids = null;
                }
            } catch (Throwable exc) {
                Logger.m178e(exc);
            }
        }
        if (this.onShowGroupPhotoListener != null) {
            this.onShowGroupPhotoListener.onShowGroupPhoto(aid, pid, gid, spids);
        }
    }
}
