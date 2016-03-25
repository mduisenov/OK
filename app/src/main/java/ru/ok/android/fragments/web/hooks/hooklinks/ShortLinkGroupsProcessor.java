package ru.ok.android.fragments.web.hooks.hooklinks;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import java.util.List;

public class ShortLinkGroupsProcessor extends ShortLinkBaseProcessor {
    private final ShortLinkGroupsListener listener;

    public interface ShortLinkGroupsListener {
        void onShowGroups(@Nullable String str);
    }

    public ShortLinkGroupsProcessor(ShortLinkGroupsListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "groups";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            Pair<Boolean, String> p = matchGroups(uri);
            if (p != null && ((Boolean) p.first).booleanValue()) {
                this.listener.onShowGroups((String) p.second);
            }
        }
    }

    protected boolean isUriMatches(Uri uri) {
        Pair<Boolean, String> p = matchGroups(uri);
        return p != null && ((Boolean) p.first).booleanValue();
    }

    public Pair<Boolean, String> matchGroups(Uri uri) {
        Object obj = null;
        if (uri == null) {
            return null;
        }
        List<String> path = uri.getPathSegments();
        int size = path.size();
        if ((size != 1 && size != 2) || !"groups".equals(path.get(0))) {
            return null;
        }
        Boolean valueOf = Boolean.valueOf(true);
        if (size != 1) {
            String str = (String) path.get(1);
        }
        return new Pair(valueOf, obj);
    }
}
