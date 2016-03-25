package ru.ok.android.fragments.web.hooks.hooklinks;

import android.net.Uri;
import android.support.annotation.Nullable;
import java.util.List;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;

public class ShortLinkUserGroupsProcessor extends ShortLinkBaseProcessor {
    private final ShortLinkUserGroupsListener listener;

    public interface ShortLinkUserGroupsListener {
        void onShowUserGroups(String str);
    }

    public ShortLinkUserGroupsProcessor(ShortLinkUserGroupsListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "user_groups";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            String userId = extractUserId(uri, true);
            if (userId != null) {
                this.listener.onShowUserGroups(userId);
            }
        }
    }

    protected boolean isUriMatches(Uri uri) {
        return extractUserId(uri, false) != null;
    }

    @Nullable
    public static String extractUserId(Uri uri, boolean doXor) {
        if (uri == null) {
            return null;
        }
        List<String> path = uri.getPathSegments();
        if (path != null && path.size() == 3 && "profile".equals(path.get(0)) && "groups".equals(path.get(2))) {
            return ShortLinkUtils.extractId((String) path.get(1), doXor);
        }
        return null;
    }
}
