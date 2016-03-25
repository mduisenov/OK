package ru.ok.android.ui.messaging.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.ui.custom.layout.FlowLayout;
import ru.ok.android.utils.DateFormatter;
import ru.ok.android.utils.URLUtil;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public final class ReadStatusView extends LinearLayout {
    private static final int AVATAR_IMAGE_VIEW_HORIZONTAL_MARGIN;
    private static final int AVATAR_SIZE;
    private final Map<String, UserInfo> allUserInfosLocal;
    private FlowLayout avatarsContainer;
    private HandleBlocker handleBlocker;
    boolean isMultichat;
    private TextView readText;
    private final List<String> userIdsLocal;

    static {
        AVATAR_SIZE = (int) Utils.dipToPixels(20.0f);
        AVATAR_IMAGE_VIEW_HORIZONTAL_MARGIN = (int) Utils.dipToPixels(2.0f);
    }

    public ReadStatusView(Context context, HandleBlocker handleBlocker) {
        super(context);
        this.allUserInfosLocal = new HashMap();
        this.userIdsLocal = new ArrayList();
        this.isMultichat = false;
        this.handleBlocker = handleBlocker;
        init(context);
    }

    private void init(Context context) {
        LocalizationManager.inflate(context, 2130903414, (ViewGroup) this, true);
        setPadding(0, (int) Utils.dipToPixels(14.0f), 0, 0);
        setClipChildren(false);
        setClipToPadding(false);
        setLayoutParams(new LayoutParams(-1, -2));
        setGravity(17);
        this.avatarsContainer = (FlowLayout) findViewById(2131624728);
        this.readText = (TextView) findViewById(2131625279);
    }

    public void setUsers(List<String> userIds, Set<UserInfo> allUserInfos, boolean isMultichat, long lastViewTimeForDialog) {
        Context context = getContext();
        this.isMultichat = isMultichat;
        this.userIdsLocal.clear();
        for (String userId : userIds) {
            this.userIdsLocal.add(userId);
        }
        if (isMultichat || lastViewTimeForDialog <= 0) {
            this.readText.setVisibility(8);
            this.avatarsContainer.setVisibility(0);
            this.allUserInfosLocal.clear();
            if (allUserInfos != null) {
                for (UserInfo user : allUserInfos) {
                    this.allUserInfosLocal.put(user.uid, user);
                }
            }
            int cnt = 0;
            while (cnt < this.userIdsLocal.size()) {
                View viewToReuse = this.avatarsContainer.getChildAt(cnt);
                if (viewToReuse != null) {
                    viewToReuse.setVisibility(0);
                    updateAvatar((String) this.userIdsLocal.get(cnt), (AvatarImageView) viewToReuse);
                } else {
                    AvatarImageView avatarImageView = new AvatarImageView(context);
                    updateAvatar((String) this.userIdsLocal.get(cnt), avatarImageView);
                    FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(AVATAR_SIZE, AVATAR_SIZE);
                    params.setMargins(AVATAR_IMAGE_VIEW_HORIZONTAL_MARGIN, AVATAR_IMAGE_VIEW_HORIZONTAL_MARGIN, AVATAR_IMAGE_VIEW_HORIZONTAL_MARGIN, AVATAR_IMAGE_VIEW_HORIZONTAL_MARGIN);
                    this.avatarsContainer.addView(avatarImageView, params);
                }
                cnt++;
            }
            while (cnt < this.avatarsContainer.getChildCount()) {
                this.avatarsContainer.getChildAt(cnt).setVisibility(8);
                cnt++;
            }
            return;
        }
        this.readText.setText(LocalizationManager.getString(context, 2131166430, DateFormatter.getPhotoTimeString(context, lastViewTimeForDialog)));
        this.readText.setVisibility(0);
        this.avatarsContainer.setVisibility(8);
    }

    private UserInfo getUser(String userId) {
        UserInfo result = (UserInfo) this.allUserInfosLocal.get(userId);
        if (result != null) {
            return result;
        }
        UserInfo currentUser = OdnoklassnikiApplication.getCurrentUser();
        if (TextUtils.equals(userId, currentUser.uid)) {
            return currentUser;
        }
        return null;
    }

    private void updateAvatar(String userId, AvatarImageView avatarImageView) {
        if (avatarImageView == null) {
            return;
        }
        if (avatarImageView == null || avatarImageView.user == null || !TextUtils.equals(avatarImageView.user.getId(), userId)) {
            String url = null;
            boolean isMan = true;
            UserInfo userInfo = getUser(userId);
            if (userInfo != null) {
                url = userInfo.picUrl;
                isMan = userInfo.genderType == UserGenderType.MALE;
            }
            if (URLUtil.isStubUrl(url)) {
                url = null;
            }
            ImageViewManager.getInstance().displayImage(url, avatarImageView, isMan, this.handleBlocker);
        }
    }
}
