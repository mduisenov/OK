package ru.ok.android.ui.fragments.messages.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.ui.fragments.messages.helpers.ConversationParticipantsUtils;
import ru.ok.android.utils.URLUtil;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public final class MultipleAvatarsView extends BaseMultipleUrlImageView {
    private List<AvatarImageView> avatarImageViews;
    private final List<UserInfo> users;

    public MultipleAvatarsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.users = new ArrayList();
    }

    protected void processConfig() {
        int nonCurrentUsers = ConversationParticipantsUtils.computeNonCurrentUsers(this.users);
        if (this.multiple.booleanValue()) {
            processMultiple(this.users, nonCurrentUsers);
        } else {
            processSingleUser(this.users);
        }
    }

    protected void configureForMode(boolean isMultipleLayout) {
        if (isMultipleLayout) {
            for (int i = 0; i < 4; i++) {
                addView(popUrlImageView());
            }
            return;
        }
        addView(popAvatarImageView());
    }

    protected void clearView(View child) {
        if (child instanceof AvatarImageView) {
            AvatarImageView avatar = (AvatarImageView) child;
            avatar.getImage().setUrl(null);
            getAvatarImageViews().add(avatar);
        } else if (child instanceof UrlImageView) {
            super.clearView(child);
        }
    }

    private List<AvatarImageView> getAvatarImageViews() {
        if (this.avatarImageViews == null) {
            this.avatarImageViews = new ArrayList();
        }
        return this.avatarImageViews;
    }

    private void processMultiple(List<UserInfo> users, int nonCurrentUsers) {
        UserInfo user;
        String currentUid = OdnoklassnikiApplication.getCurrentUser().uid;
        int usersWithAvatars = 0;
        for (UserInfo user2 : users) {
            if (!TextUtils.equals(currentUid, user2.uid)) {
                if (!URLUtil.isStubUrl(user2.picUrl)) {
                    usersWithAvatars++;
                }
            }
        }
        boolean currentUserHasAvatar = !URLUtil.isStubUrl(OdnoklassnikiApplication.getCurrentUser().picUrl);
        int usersWithoutAvatarsToUse = Math.max(0, 4 - usersWithAvatars);
        boolean skipCurrentUser = usersWithAvatars >= 4 || (!currentUserHasAvatar && nonCurrentUsers >= 4);
        int childIndex = 0;
        for (int i = 0; i < users.size() && childIndex < getChildCount(); i++) {
            user2 = (UserInfo) users.get(i);
            boolean isCurrentUser = TextUtils.equals(user2.uid, currentUid);
            if (!skipCurrentUser || !isCurrentUser) {
                if (!(!URLUtil.isStubUrl(user2.picUrl))) {
                    if (isCurrentUser || usersWithoutAvatarsToUse > 0) {
                        usersWithAvatars--;
                    }
                }
                int childIndex2 = childIndex + 1;
                UrlImageView child = (UrlImageView) getChildAt(childIndex);
                child.setVisibility(0);
                String picUrl = user2.picUrl;
                if (URLUtil.isStubUrl(picUrl)) {
                    picUrl = null;
                }
                ImageViewManager.getInstance().displayImage(picUrl, child, user2.genderType == UserGenderType.MALE ? 2130838321 : 2130837927, this.blocker);
                childIndex = childIndex2;
            }
        }
        while (childIndex < getChildCount()) {
            getChildAt(childIndex).setVisibility(4);
            childIndex++;
        }
    }

    private void processSingleUser(List<UserInfo> users) {
        boolean z = false;
        UserInfo nonCurrentUser = ConversationParticipantsUtils.findNonCurrentUser(users);
        AvatarImageView child = (AvatarImageView) getChildAt(0);
        child.setUser(nonCurrentUser);
        ImageViewManager instance = ImageViewManager.getInstance();
        String str = nonCurrentUser != null ? nonCurrentUser.picUrl : null;
        if (nonCurrentUser != null && nonCurrentUser.genderType == UserGenderType.MALE) {
            z = true;
        }
        instance.displayImage(str, child, z, this.blocker);
    }

    private AvatarImageView popAvatarImageView() {
        if (this.avatarImageViews != null && !this.avatarImageViews.isEmpty()) {
            return (AvatarImageView) this.avatarImageViews.remove(this.avatarImageViews.size() - 1);
        }
        AvatarImageView result = new AvatarImageView(getContext());
        result.setClickable(false);
        return result;
    }

    public List<UserInfo> getUsers() {
        return this.users;
    }
}
