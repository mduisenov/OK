package ru.ok.android.ui.stream.list;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.stream.Feed;
import ru.ok.model.stream.FeedUtils;
import ru.ok.model.stream.entities.FeedAchievementTypeEntity;
import ru.ok.model.stream.entities.FeedHolidayEntity;
import ru.ok.model.stream.entities.FeedMusicTrackEntity;
import ru.ok.model.stream.entities.FeedPresentEntity;
import ru.ok.model.stream.entities.FeedPresentTypeEntity;
import ru.ok.model.stream.entities.IPresentEntity;
import ru.ok.model.stream.message.FeedMessage;

public final class PresentInfo {
    public final FeedAchievementTypeEntity achievementType;
    public final Feed feed;
    public final FeedHolidayEntity holiday;
    public final boolean isBadge;
    public final boolean isBig;
    public final boolean isMusic;
    public final String presentImageUrl;
    public final FeedPresentTypeEntity presentType;
    public final FeedMessage receiverLabel;
    public final ArrayList<GeneralUserInfo> receivers;
    public final FeedMessage senderLabel;
    public final ArrayList<GeneralUserInfo> senders;
    public final List<FeedMusicTrackEntity> tracks;

    public PresentInfo(IPresentEntity present, Feed feed, FeedPresentEntity presentEntity) {
        boolean z = true;
        this.senders = new ArrayList();
        this.receivers = new ArrayList();
        if (present instanceof FeedPresentTypeEntity) {
            this.presentType = (FeedPresentTypeEntity) present;
            this.achievementType = null;
        } else if (present instanceof FeedAchievementTypeEntity) {
            this.achievementType = (FeedAchievementTypeEntity) present;
            this.presentType = null;
        } else {
            this.presentType = null;
            this.achievementType = null;
        }
        this.feed = feed;
        this.holiday = FeedUtils.findFirstHoliday(feed);
        this.isBig = present.isBig();
        this.presentImageUrl = present.getLargestPicUrl();
        if (presentEntity != null) {
            this.tracks = presentEntity.getTracks();
            this.senderLabel = presentEntity.getSenderLabel();
            this.receiverLabel = presentEntity.getReceiverLabel();
            boolean z2 = (presentEntity.getReceiver() == null || presentEntity.getSender() == null || !TextUtils.equals(presentEntity.getReceiver().getId(), presentEntity.getSender().getId())) ? false : true;
            this.isBadge = z2;
        } else {
            this.tracks = null;
            this.senderLabel = null;
            this.receiverLabel = null;
            this.isBadge = false;
        }
        if (this.tracks == null || this.tracks.isEmpty()) {
            z = false;
        }
        this.isMusic = z;
    }

    public String getPresentTypeId() {
        if (this.presentType != null) {
            return this.presentType.getId();
        }
        if (this.achievementType != null) {
            return this.achievementType.getId();
        }
        return null;
    }
}
