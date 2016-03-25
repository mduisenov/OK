package ru.ok.android.fragments.web.hooks.photo;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;

public class HookGroupAvatarObserver extends HookBaseProcessor {
    private OnGroupAvatarListener onGroupAvatarListener;

    public interface OnGroupAvatarListener {
        void onGroupAvatarClicked(String str, String str2);
    }

    public HookGroupAvatarObserver(OnGroupAvatarListener listener) {
        this.onGroupAvatarListener = listener;
    }

    protected String getHookName() {
        return "/apphook/groupAvatar";
    }

    protected void onHookExecute(Uri uri) {
        String gid = uri.getQueryParameter("gid");
        String pid = uri.getQueryParameter("photoId");
        if (this.onGroupAvatarListener != null) {
            this.onGroupAvatarListener.onGroupAvatarClicked(gid, pid);
        }
    }
}
