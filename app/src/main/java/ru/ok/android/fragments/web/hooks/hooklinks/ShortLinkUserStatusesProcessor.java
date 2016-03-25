package ru.ok.android.fragments.web.hooks.hooklinks;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Pair;
import java.util.List;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;

public class ShortLinkUserStatusesProcessor extends ShortLinkBaseProcessor {
    private final ShortLinkUserTopicsListener listener;

    public interface ShortLinkUserTopicsListener {
        void onShowUserTopic(String str, String str2);

        void onShowUserTopics(String str, String str2);
    }

    public ShortLinkUserStatusesProcessor(ShortLinkUserTopicsListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "user_topics";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            Pair<String, String> userIdFilterOrStatusIdPair = extractUserIdFilterOrStatusIdPair(uri, true);
            if (userIdFilterOrStatusIdPair != null) {
                String statusId = ShortLinkUtils.extractId((String) userIdFilterOrStatusIdPair.second, true);
                if (statusId != null) {
                    this.listener.onShowUserTopic((String) userIdFilterOrStatusIdPair.first, statusId);
                } else {
                    this.listener.onShowUserTopics((String) userIdFilterOrStatusIdPair.first, (String) userIdFilterOrStatusIdPair.second);
                }
            }
        }
    }

    protected boolean isUriMatches(Uri uri) {
        return extractUserIdFilterOrStatusIdPair(uri, false) != null;
    }

    @Nullable
    public static Pair<String, String> extractUserIdFilterOrStatusIdPair(Uri uri, boolean doXor) {
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
        String pathEnd1 = (String) path.get(size - 1);
        String pathEnd2 = (String) path.get(size - 2);
        String pathEnd3 = (String) path.get(size - 3);
        if ("profile".equals(pathEnd3) && "statuses".equals(pathEnd1)) {
            return validPairOrNull(ShortLinkUtils.extractId(pathEnd2, doXor), null);
        }
        if (size < 4) {
            return null;
        }
        if ("profile".equals((String) path.get(size - 4)) && "statuses".equals(pathEnd2)) {
            return validPairOrNull(ShortLinkUtils.extractId(pathEnd3, doXor), pathEnd1);
        }
        return null;
    }

    @Nullable
    private static Pair<String, String> validPairOrNull(String requiredId, String optionalParam) {
        if (requiredId == null) {
            return null;
        }
        return new Pair(requiredId, optionalParam);
    }
}
