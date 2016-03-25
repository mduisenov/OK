package ru.ok.android.ui.groups.data;

import java.util.Map;
import ru.ok.model.stream.DiscussionSummary;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.LikeInfoContext;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.message.FeedMessage;

public class MediaTopicFeed extends Feed {
    private final boolean canSetToStatus;

    public MediaTopicFeed(long id, int pattern, long date, FeedMessage message, FeedMessage title, LikeInfoContext likeInfo, DiscussionSummary discussionSummary, String spamId, String deleteId, int dataFlags, Map<String, BaseEntity> entitiesByRefId, boolean isPinned, boolean canSetToStatus) {
        setId(id);
        setPattern(pattern);
        setDate(date);
        setMessage(message);
        setTitle(title);
        setLikeInfo(likeInfo);
        setDiscussionSummary(discussionSummary);
        setSpamId(spamId);
        setDeleteId(deleteId);
        addDataFlag(dataFlags);
        setPinned(isPinned);
        this.canSetToStatus = canSetToStatus;
        populateEntities(entitiesByRefId);
    }

    private void populateEntities(Map<String, BaseEntity> entitiesByRefId) {
        for (String ref : entitiesByRefId.keySet()) {
            addTargetRef(ref);
        }
        resolveRefs(entitiesByRefId);
    }

    public boolean isCanSetToStatus() {
        return this.canSetToStatus;
    }
}
