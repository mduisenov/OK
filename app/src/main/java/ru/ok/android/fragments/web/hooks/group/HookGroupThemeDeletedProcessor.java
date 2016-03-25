package ru.ok.android.fragments.web.hooks.group;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;

public class HookGroupThemeDeletedProcessor extends HookBaseProcessor {
    private final HookGroupThemeDeletedListener listener;

    public interface HookGroupThemeDeletedListener {
        void onGroupThemeDeletedClick(String str, String str2);
    }

    public HookGroupThemeDeletedProcessor(HookGroupThemeDeletedListener listener) {
        this.listener = listener;
    }

    protected final void onHookExecute(Uri uri) {
        String topicId = uri.getQueryParameter("tid");
        this.listener.onGroupThemeDeletedClick(uri.getQueryParameter("gid"), topicId);
    }

    protected String getHookName() {
        return "/apphook/groupMediaThemeDeleted";
    }
}
