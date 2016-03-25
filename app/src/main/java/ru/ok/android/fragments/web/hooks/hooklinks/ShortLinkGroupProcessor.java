package ru.ok.android.fragments.web.hooks.hooklinks;

import android.net.Uri;
import java.util.List;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;

public class ShortLinkGroupProcessor extends ShortLinkBaseProcessor {
    private final ShortLinkGroupListener listener;

    public interface ShortLinkGroupListener {
        void onShowGroup(String str);
    }

    protected String getHookName() {
        return "group";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            String groupId = extractGroupId(uri, true);
            if (groupId != null) {
                this.listener.onShowGroup(groupId);
            }
        }
    }

    protected boolean isUriMatches(Uri uri) {
        return extractGroupId(uri, false) != null;
    }

    public static String extractGroupId(Uri uri, boolean doXor) {
        if (uri == null) {
            return null;
        }
        List<String> path = uri.getPathSegments();
        if (path == null || path.size() < 2 || !"group".equals(path.get(path.size() - 2))) {
            return null;
        }
        return ShortLinkUtils.extractId((String) path.get(path.size() - 1), doXor);
    }
}
