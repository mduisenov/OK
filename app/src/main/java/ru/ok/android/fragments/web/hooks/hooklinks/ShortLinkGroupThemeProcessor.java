package ru.ok.android.fragments.web.hooks.hooklinks;

import android.net.Uri;
import java.util.List;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;
import ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type;
import ru.ok.model.GroupDiscussion;

public class ShortLinkGroupThemeProcessor extends ShortLinkBaseProcessor {
    private final ShortLinkGroupThemeListener listener;

    public interface ShortLinkGroupThemeListener {
        void onShowGroupTheme(GroupDiscussion groupDiscussion);
    }

    protected String getHookName() {
        return "group_topic";
    }

    protected void onHookExecute(Uri uri) {
        if (this.listener != null) {
            GroupDiscussion groupDiscussion = extractGroupDiscussion(uri, true);
            if (groupDiscussion != null) {
                this.listener.onShowGroupTheme(groupDiscussion);
            }
        }
    }

    protected boolean isUriMatches(Uri uri) {
        return extractGroupDiscussion(uri, false) != null;
    }

    public static GroupDiscussion extractGroupDiscussion(Uri uri, boolean doXor) {
        if (uri == null) {
            return null;
        }
        List<String> path = uri.getPathSegments();
        int size = path.size();
        if (path == null || size < 4 || !"group".equals(path.get(size - 4)) || !"topic".equals(path.get(size - 2))) {
            return null;
        }
        String groupIdStr = (String) path.get(size - 3);
        String topicIdStr = (String) path.get(size - 1);
        if (groupIdStr == null || topicIdStr == null) {
            return null;
        }
        String groupId = ShortLinkUtils.extractId(groupIdStr, doXor);
        if (groupId == null) {
            return null;
        }
        String topicId = ShortLinkUtils.extractId(topicIdStr, doXor);
        if (topicId != null) {
            return new GroupDiscussion(topicId, Type.GROUP_TOPIC.name(), groupId);
        }
        return null;
    }
}
