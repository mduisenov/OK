package ru.ok.android.ui.fragments.messages.view;

import android.content.Context;
import android.util.AttributeSet;
import java.util.List;
import ru.ok.model.MutualFriendsPreviewInfo;
import ru.ok.model.UserInfo;

public class PymkMutualFriendsView extends ParticipantsPreviewView {
    protected Integer totalCount;
    private String uid;

    public PymkMutualFriendsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.totalCount = null;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return this.uid;
    }

    public void setParticipants(List<UserInfo> participants, boolean showCount) {
        if (this.totalCount == null) {
            super.setParticipants(participants, showCount);
            return;
        }
        super.setParticipants(participants, false);
        if (this.totalCount.intValue() > this.maxAvatars) {
            this.count.setVisibility(0);
            this.count.setText(String.valueOf(this.totalCount));
        }
    }

    public void setParticipants(List<UserInfo> participants, int totalCount) {
        setTotalCount(Integer.valueOf(totalCount));
        setParticipants((List) participants, true);
    }

    public void setParticipants(MutualFriendsPreviewInfo mutualFriendsPreviewInfo) {
        setParticipants(mutualFriendsPreviewInfo.users, mutualFriendsPreviewInfo.totalCount);
    }

    public Integer getTotalCount() {
        return this.totalCount;
    }
}
