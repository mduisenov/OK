package ru.ok.android.fragments.web.hooks.hooklinks;

import android.net.Uri;
import ru.ok.android.fragments.web.hooks.HookBaseProcessor;
import ru.ok.android.utils.Logger;

public abstract class ShortLinkBaseProcessor extends HookBaseProcessor {
    public boolean handleWebHookEvent(String url) {
        Uri uri = Uri.parse(url);
        if (!isUriMatches(uri)) {
            return false;
        }
        Logger.m173d("Short link execute url = %s", url);
        onHookExecute(uri);
        return true;
    }
}
