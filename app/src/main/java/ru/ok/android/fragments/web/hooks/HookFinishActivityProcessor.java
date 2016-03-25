package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class HookFinishActivityProcessor extends HookBaseProcessor {
    private final OnFinishActivityListener listener;

    public interface OnFinishActivityListener {
        void onFinishActivity(int i);
    }

    @NonNull
    public static String makeHookUrlWithResult(int result) {
        return "_app_internal_finish_activity/" + result;
    }

    public HookFinishActivityProcessor(@Nullable OnFinishActivityListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "_app_internal_finish_activity";
    }

    protected void onHookExecute(@NonNull Uri uri) {
        if (this.listener != null) {
            this.listener.onFinishActivity(parseResult(uri));
        }
    }

    private static int parseResult(@NonNull Uri uri) {
        try {
            return Integer.parseInt(new ShortLinkParser(uri.toString(), "_app_internal_finish_activity").getValue("_app_internal_finish_activity"));
        } catch (Exception e) {
            return 0;
        }
    }
}
