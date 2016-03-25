package ru.ok.android.utils.bus;

import android.os.Bundle;
import java.util.ArrayList;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.java.api.json.users.ComplaintType;
import ru.ok.java.api.request.groups.GroupCreateType;

public final class BusGroupsHelper {
    private static void getGroupInfo(String groupId, boolean refresh) {
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_ID", groupId);
        bundle.putBoolean("GROUP_REFRESH", refresh);
        GlobalBus.send(2131623994, new BusEvent(bundle));
    }

    public static void getGroupInfo(String groupId) {
        getGroupInfo(groupId, false);
    }

    public static void refreshGroupInfo(String groupId) {
        getGroupInfo(groupId, true);
    }

    public static void inviteFriendsToGroup(String groupId, ArrayList<String> friends) {
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_ID", groupId);
        bundle.putStringArrayList("GROUP_FRIENDS_IDS", friends);
        GlobalBus.send(2131623995, new BusEvent(bundle));
    }

    public static void complaintToGroup(String groupId, ComplaintType type) {
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_ID", groupId);
        bundle.putSerializable("GROUP_COMPLAINT_TYPE", type);
        GlobalBus.send(2131623956, new BusEvent(bundle));
    }

    public static void inviteToGroup(String groupId, boolean mayBe) {
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_ID", groupId);
        bundle.putBoolean("GROUP_MAYBE", mayBe);
        GlobalBus.send(2131623996, new BusEvent(bundle));
    }

    public static void inviteToGroup(String groupId) {
        inviteToGroup(groupId, false);
    }

    public static void leaveGroup(String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_ID", groupId);
        GlobalBus.send(2131623997, new BusEvent(bundle));
    }

    public static void subscribeToGroup(String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_ID", groupId);
        GlobalBus.send(2131624111, new BusEvent(bundle));
    }

    public static void friendsInGroup(String groupId, boolean fetchUserInfos, boolean fetchGroupInfo) {
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_ID", groupId);
        bundle.putBoolean("FETCH_USER_INFOS", fetchUserInfos);
        bundle.putBoolean("FETCH_USER_INFOS", fetchGroupInfo);
        GlobalBus.send(2131623992, new BusEvent(bundle));
    }

    public static void createGroup(GroupCreateType type, String name, String description, boolean isOpen) {
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_TYPE", type.toString());
        bundle.putString("GROUP_NAME", name);
        bundle.putString("GROUP_DESCRIPTION", description);
        bundle.putBoolean("GROUP_OPEN", isOpen);
        GlobalBus.send(2131623991, new BusEvent(bundle));
    }
}
