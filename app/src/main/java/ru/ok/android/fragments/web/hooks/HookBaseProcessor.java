package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import ru.ok.android.fragments.web.client.interceptor.hooks.AppHooksInterceptor.HookUrlProcessor;
import ru.ok.android.utils.Logger;

public abstract class HookBaseProcessor implements HookUrlProcessor {
    protected abstract String getHookName();

    protected abstract void onHookExecute(Uri uri);

    public boolean handleWebHookEvent(String url) {
        Uri uri = Uri.parse(url);
        if (uri.getPath() == null) {
            Logger.m176e("Strange url: " + uri);
            return false;
        } else if (!isUriMatches(uri)) {
            return false;
        } else {
            Logger.m173d("Hook execute url = %s, processor = %s", url, this);
            onHookExecute(uri);
            return true;
        }
    }

    protected boolean isUriMatches(Uri uri) {
        return uri.getPath().contains(getHookName());
    }
}
