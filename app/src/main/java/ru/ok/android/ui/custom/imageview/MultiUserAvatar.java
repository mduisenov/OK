package ru.ok.android.ui.custom.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.C0206R;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.utils.URLUtil;
import ru.ok.android.utils.clover.CloverImageView;
import ru.ok.android.utils.clover.CloverImageView.LeafInfo;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public final class MultiUserAvatar extends CloverImageView {
    private String uniqueId;
    private final List<GeneralUserInfo> userInfos;
    private final List<GeneralUserInfo> userWithoutAvatarInfos;

    public MultiUserAvatar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.userInfos = new ArrayList(4);
        this.userWithoutAvatarInfos = new ArrayList();
        int avatarSize = (int) getContext().getResources().getDimension(2131231171);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, C0206R.styleable.MultiUserAvatar, 0, 0);
        if (typedArray.hasValue(0)) {
            avatarSize = typedArray.getDimensionPixelSize(0, avatarSize);
        }
        typedArray.recycle();
        setLeafSize(avatarSize);
    }

    public void setUsers(List<? extends GeneralUserInfo> users, String hideUid, String uniqueId) {
        if (!TextUtils.equals(this.uniqueId, uniqueId) && users != null && users.size() != 0) {
            this.uniqueId = uniqueId;
            List<LeafInfo> leafInfos = getLeafInfos(users, this.userInfos, this.userWithoutAvatarInfos, hideUid);
            if (leafInfos != null && leafInfos.size() <= 4) {
                setLeaves(leafInfos);
            }
        }
    }

    public static List<LeafInfo> getLeafInfos(@NonNull List<? extends GeneralUserInfo> users, @Nullable List<GeneralUserInfo> usersCacheList, @Nullable List<GeneralUserInfo> usersWithoutAvatarsCacheList, @Nullable String hideUid) {
        List<LeafInfo> leafInfos = new ArrayList();
        if (usersCacheList == null) {
            usersCacheList = new ArrayList();
        } else {
            usersCacheList.clear();
        }
        if (usersWithoutAvatarsCacheList == null) {
            usersWithoutAvatarsCacheList = new ArrayList();
        } else {
            usersWithoutAvatarsCacheList.clear();
        }
        for (GeneralUserInfo user : users) {
            if (!(user == null || TextUtils.equals(hideUid, user.getId()))) {
                if (URLUtil.isStubUrl(user.getPicUrl())) {
                    usersWithoutAvatarsCacheList.add(user);
                } else {
                    usersCacheList.add(user);
                    if (usersCacheList.size() >= 4) {
                        break;
                    }
                }
            }
        }
        if (usersCacheList.size() < 4) {
            int rest = 4 - usersCacheList.size();
            if (usersWithoutAvatarsCacheList.size() >= rest) {
                usersWithoutAvatarsCacheList = usersWithoutAvatarsCacheList.subList(0, rest);
            }
            usersCacheList.addAll(usersWithoutAvatarsCacheList);
        }
        int count = usersCacheList.size();
        for (int i = 0; i < count; i++) {
            Uri leafUri;
            GeneralUserInfo userInfo = (GeneralUserInfo) usersCacheList.get(i);
            int placeholderResId = getDefaultAvatarId(userInfo);
            if (URLUtil.isStubUrl(userInfo.getPicUrl())) {
                leafUri = FrescoOdkl.uriFromResId(placeholderResId);
            } else {
                leafUri = Uri.parse(userInfo.getPicUrl());
            }
            leafInfos.add(new LeafInfo(leafUri, placeholderResId));
        }
        return leafInfos;
    }

    @DrawableRes
    private static int getDefaultAvatarId(GeneralUserInfo userInfo) {
        if (userInfo.getObjectType() == 0) {
            return ((UserInfo) userInfo).genderType == UserGenderType.MALE ? 2130838321 : 2130837927;
        } else {
            return 2130837663;
        }
    }
}
