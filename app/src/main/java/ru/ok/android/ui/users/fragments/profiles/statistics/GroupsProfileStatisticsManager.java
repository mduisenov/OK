package ru.ok.android.ui.users.fragments.profiles.statistics;

import android.util.Pair;
import ru.ok.android.statistics.StatisticManager;

public class GroupsProfileStatisticsManager {
    public static String ACTION_AVATAR_CLICK;
    public static String ACTION_CHANGE_SETTINGS;
    public static String ACTION_COMPLAIN;
    public static String ACTION_DELETE_GROUP;
    public static String ACTION_INVITE_FRIENDS;
    public static String ACTION_JOIN_GROUP;
    public static String ACTION_MAKE_WEB_LINK;
    public static String ACTION_NAMEZONE;
    public static String ACTION_SELECT_AVATAR;
    public static String ACTION_SUBSCRIBE_TO_GROUP;
    public static String PREFIX;
    public static String SECTION_BACK_LIST;
    public static String SECTION_INVITE;
    public static String SECTION_LEAVE_GROUP;
    public static String SECTION_LINKS;
    public static String SECTION_MEMBERS;
    public static String SECTION_MODERATORS;
    public static String SECTION_PHOTO_ALBUMS;

    static {
        PREFIX = "Profile_group_";
        ACTION_JOIN_GROUP = "action_join_group";
        ACTION_INVITE_FRIENDS = "action_invite_friends";
        ACTION_CHANGE_SETTINGS = "action_change_settings";
        ACTION_DELETE_GROUP = "action_delete_group";
        ACTION_SUBSCRIBE_TO_GROUP = "action_subscribe_news";
        ACTION_MAKE_WEB_LINK = "action_copy_link";
        ACTION_AVATAR_CLICK = "avatar_image";
        ACTION_SELECT_AVATAR = "action_select_avatar";
        ACTION_NAMEZONE = "action_about_namezone";
        ACTION_COMPLAIN = "action_group_complain";
        SECTION_MEMBERS = "section_members";
        SECTION_LINKS = "section_links";
        SECTION_MODERATORS = "section_moderators";
        SECTION_BACK_LIST = "section_black_list";
        SECTION_INVITE = "section_invite_request";
        SECTION_LEAVE_GROUP = "action_leave_group";
        SECTION_PHOTO_ALBUMS = "section_photo_albums";
    }

    public static void sendStatEvent(String event) {
        StatisticManager.getInstance().addStatisticEvent(PREFIX + event, new Pair[0]);
    }
}
