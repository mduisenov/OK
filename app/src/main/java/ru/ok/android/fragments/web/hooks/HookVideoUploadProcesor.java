package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import android.text.TextUtils;

public class HookVideoUploadProcesor extends HookBaseProcessor {
    private final HookVideoUploadListener listener;

    public interface HookVideoUploadListener {
        void onUploadVideo(String str);
    }

    public HookVideoUploadProcesor(HookVideoUploadListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/uploadVideo";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            String paramGroupID = uri.getQueryParameter("groupId");
            if (TextUtils.isEmpty(paramGroupID)) {
                paramGroupID = null;
            }
            this.listener.onUploadVideo(paramGroupID);
        }
    }
}
