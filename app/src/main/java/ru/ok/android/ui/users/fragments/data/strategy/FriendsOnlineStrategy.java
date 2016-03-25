package ru.ok.android.ui.users.fragments.data.strategy;

import android.content.Context;
import ru.ok.android.utils.DateFormatter;
import ru.ok.model.UserInfo;

public final class FriendsOnlineStrategy extends FriendsFilterListStrategy {
    public FriendsOnlineStrategy(Context context) {
        super(context);
    }

    public CharSequence buildInfoString(UserInfo user) {
        return DateFormatter.formatDeltaTimePast(this.context, user.lastOnline, false, false);
    }
}
