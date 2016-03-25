package ru.ok.android.fragments.web.hooks.hooklinks;

import android.net.Uri;
import android.util.Pair;
import java.util.List;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkGroupThemesProcessor.ShortLinkGroupThemesListener;

public class ShortLinkGroupThemesTagProcessor extends ShortLinkBaseProcessor {
    private final ShortLinkGroupThemesListener listener;

    public ShortLinkGroupThemesTagProcessor(ShortLinkGroupThemesListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "group_topics_tag";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            Pair<String, Long> groupTagPair = extractGroupIdTagId(uri, true);
            if (groupTagPair != null) {
                this.listener.onShowGroupThemes((String) groupTagPair.first, (Long) groupTagPair.second, null);
            }
        }
    }

    protected boolean isUriMatches(Uri uri) {
        return extractGroupIdTagId(uri, false) != null;
    }

    public static Pair<String, Long> extractGroupIdTagId(Uri uri, boolean doXor) {
        if (uri == null) {
            return null;
        }
        List<String> path = uri.getPathSegments();
        if (path == null) {
            return null;
        }
        int size = path.size();
        if (size < 4 || !"group".equals(path.get(size - 4)) || !"topics".equals(path.get(size - 2))) {
            return null;
        }
        String groupIdStr = ShortLinkUtils.extractId((String) path.get(size - 3), doXor);
        if (groupIdStr != null) {
            return new Pair(groupIdStr, ShortLinkUtils.parseLongNullOnFail((String) path.get(size - 1)));
        }
        return null;
    }
}
