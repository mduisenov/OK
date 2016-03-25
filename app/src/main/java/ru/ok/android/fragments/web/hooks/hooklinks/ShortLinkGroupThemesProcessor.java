package ru.ok.android.fragments.web.hooks.hooklinks;

import android.net.Uri;
import android.support.v4.util.Pair;
import java.util.List;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;

public class ShortLinkGroupThemesProcessor extends ShortLinkBaseProcessor {
    private final ShortLinkGroupThemesListener listener;

    public interface ShortLinkGroupThemesListener {
        void onShowGroupThemes(String str, Long l, String str2);
    }

    public ShortLinkGroupThemesProcessor(ShortLinkGroupThemesListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "group_topics";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            Pair<String, String> pairGroupTopics = extractGroupIdTopic(uri, true);
            if (pairGroupTopics != null) {
                this.listener.onShowGroupThemes((String) pairGroupTopics.first, null, (String) pairGroupTopics.second);
            }
        }
    }

    protected boolean isUriMatches(Uri uri) {
        return extractGroupIdTopic(uri, false) != null;
    }

    public static Pair<String, String> extractGroupIdTopic(Uri uri, boolean doXor) {
        if (uri == null) {
            return null;
        }
        List<String> path = uri.getPathSegments();
        if (path == null) {
            return null;
        }
        int size = path.size();
        if (size < 3) {
            return null;
        }
        String lastPathSegment = (String) path.get(size - 1);
        if (!"group".equals(path.get(size - 3))) {
            return null;
        }
        if ("topics".equals(lastPathSegment) || "suggested".equals(lastPathSegment) || "actualtopics".equals(lastPathSegment)) {
            return new Pair(ShortLinkUtils.extractId((String) path.get(size - 2), doXor), lastPathSegment);
        }
        return null;
    }
}
