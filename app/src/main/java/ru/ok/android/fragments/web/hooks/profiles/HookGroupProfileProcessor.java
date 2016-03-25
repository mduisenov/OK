package ru.ok.android.fragments.web.hooks.profiles;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;

public final class HookGroupProfileProcessor extends HookBaseProcessor {
    private final HookGroupProfileListener listener;

    public interface HookGroupProfileListener {
        void onGroupProfileSelected(String str);
    }

    public HookGroupProfileProcessor(HookGroupProfileListener listener) {
        this.listener = listener;
    }

    protected boolean isUriMatches(Uri uri) {
        return uri.getPath().contains(new StringBuilder().append(getHookName()).append("/").toString()) || uri.getPath().contains(getHookName() + ";") || uri.getPath().equals(getHookName());
    }

    protected String getHookName() {
        return "/apphook/group";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            this.listener.onGroupProfileSelected(uri.getQueryParameter("gid"));
        }
    }
}
