package ru.ok.android.fragments.web.hooks;

import android.net.Uri;

public class HookUploadPhotoProcessor extends HookBaseProcessor {
    private final HookUploadPhotoListener uploadPhotoListener;

    public interface HookUploadPhotoListener {
        void onChooserUploadPhoto(String str, String str2, String str3);
    }

    public HookUploadPhotoProcessor(HookUploadPhotoListener uploadPhotoListener) {
        this.uploadPhotoListener = uploadPhotoListener;
    }

    protected String getHookName() {
        return "/apphook/uploadPhoto";
    }

    protected void onHookExecute(Uri uri) {
        this.uploadPhotoListener.onChooserUploadPhoto(uri.getQueryParameter("aid"), uri.getQueryParameter("gid"), uri.getQueryParameter("name"));
    }
}
