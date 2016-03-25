package ru.ok.android.ui.users.fragments.data.strategy;

import android.content.Context;
import java.util.Set;
import ru.ok.android.ui.users.fragments.data.RelativeUtils;
import ru.ok.android.ui.users.fragments.data.UserInfoExtended;
import ru.ok.android.utils.DateFormatter;
import ru.ok.java.api.request.relatives.RelativesType;
import ru.ok.model.UserInfo;

public class FriendsFilterListStrategy extends FriendsFilterBaseStrategy<UserInfo> {
    public FriendsFilterListStrategy(Context context) {
        super(context);
    }

    public UserInfo getItem(int position) {
        return ((UserInfoExtended) this.filteredUsers.get(position)).user;
    }

    public int getItemsCount() {
        return this.filteredUsers.size();
    }

    public CharSequence buildInfoString(UserInfo user) {
        CharSequence result = null;
        if (this.relationType == RelativesType.RELATIVE) {
            result = buildSubRelativeString(user);
        }
        if (result == null) {
            return DateFormatter.formatDeltaTimePast(this.context, user.lastOnline, false, false);
        }
        return result;
    }

    public String getItemHeader(int position) {
        return ((UserInfoExtended) this.filteredUsers.get(position)).firstLetter;
    }

    private StringBuilder buildSubRelativeString(UserInfo user) {
        Set<RelativesType> subRelativeTypes = (Set) this.subRelations.get(user.uid);
        if (subRelativeTypes == null) {
            return null;
        }
        StringBuilder sb = null;
        for (RelativesType type : subRelativeTypes) {
            int resourceId = RelativeUtils.getRelativeTextResourceId(type, user);
            if (resourceId > 0) {
                if (sb == null) {
                    sb = new StringBuilder();
                }
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(this.lm.getString(resourceId));
            }
        }
        return sb;
    }
}
