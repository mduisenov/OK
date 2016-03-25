package ru.ok.android.utils.bus;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.java.api.json.users.ComplaintType;

public final class BusUsersHelper {
    public static void getUserInfos(Collection<String> userIds, boolean withRelations) {
        getUserInfos(userIds, withRelations, false);
    }

    public static void refreshCurrentUserInfo(boolean withRelations) {
        getUserInfos(Arrays.asList(new String[]{OdnoklassnikiApplication.getCurrentUser().getId()}), withRelations);
    }

    public static void refreshUserInfos(Collection<String> userIds, boolean withRelations) {
        getUserInfos(userIds, withRelations, true);
    }

    public static void getUserInfos(Collection<String> userIds, boolean withRelations, boolean refresh) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("USER_IDS", new ArrayList(userIds));
        bundle.putBoolean("WITH_RELATIONS", withRelations);
        bundle.putBoolean("USER_REFRESH", refresh);
        GlobalBus.send(2131624074, new BusEvent(bundle));
    }

    public static void inviteFriend(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", userId);
        GlobalBus.send(2131624122, new BusEvent(bundle));
    }

    public static void deleteFriend(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", userId);
        GlobalBus.send(2131624121, new BusEvent(bundle));
    }

    public static void complaintToUser(String userId, ComplaintType type, boolean isAddToBlackList) {
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", userId);
        bundle.putSerializable("USERS_COMPLAINT_TYPE", type);
        bundle.putBoolean("USERS_ADD_TO_BLACKLIST", isAddToBlackList);
        GlobalBus.send(2131623957, new BusEvent(bundle));
    }

    public static void subscribeToUser(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", userId);
        GlobalBus.send(2131624111, new BusEvent(bundle));
    }

    public static void deleteUserStatus() {
        GlobalBus.send(2131624039, new BusEvent(new Bundle()));
    }
}
