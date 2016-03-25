package ru.ok.android.ui.users.fragments.profiles.statistics;

import android.util.Pair;
import ru.ok.android.statistics.StatisticManager;

public class UserProfileStatisticsManager {
    public static String ACTION_ABOUT;
    public static String ACTION_AVATAR_CLICK;
    public static String ACTION_BLOCK_USER;
    public static String ACTION_CALL;
    public static String ACTION_COMPLAIN;
    public static String ACTION_DELETE_FRIENDS;
    public static String ACTION_FIND_FRIENDS;
    public static String ACTION_INVITE_TO_GROUP;
    public static String ACTION_MAKE_FRIENDS;
    public static String ACTION_MAKE_PRESENT;
    public static String ACTION_MAKE_WEB_LINK;
    public static String ACTION_NAMEZONE;
    public static String ACTION_SELECT_AVATAR;
    public static String ACTION_SEND_MESSAGE;
    public static String ACTION_SET_RELATION;
    public static String ACTION_SIGNIFICANT_OTHER;
    public static String ACTION_SUBSCRIBE_TO_USER;
    public static String ACTION_TOUCH_PRESENT;
    public static String ACTION_UNSUBSCRIBE_TO_USER;
    public static String ACTION_UPLOAD_AVATAR;
    public static String PREFIX;
    public static String PREFIX_CURRENT_USER;
    public static String SECTION_ACHIEVEMENTS;
    public static String SECTION_APPLICATIONS;
    public static String SECTION_FORUM;
    public static String SECTION_FRIENDS;
    public static String SECTION_GROUPS;
    public static String SECTION_HOLIDAYS;
    public static String SECTION_MUSIC;
    public static String SECTION_PHOTOS;
    public static String SECTION_PRESENTS;
    public static String SECTION_TOPICS;
    public static String SECTION_VIDEOS;

    static {
        PREFIX = "Profile_user_";
        PREFIX_CURRENT_USER = "Profile_current_user_";
        SECTION_PHOTOS = "section_photos";
        SECTION_FRIENDS = "section_friends";
        SECTION_GROUPS = "section_groups";
        SECTION_VIDEOS = "section_videos";
        SECTION_TOPICS = "section_topics";
        SECTION_APPLICATIONS = "section_applications";
        SECTION_PRESENTS = "section_presents";
        SECTION_HOLIDAYS = "section_holidays";
        SECTION_FORUM = "section_forum";
        SECTION_MUSIC = "section_music";
        SECTION_ACHIEVEMENTS = "secton_achievements";
        ACTION_AVATAR_CLICK = "avatar_image";
        ACTION_ABOUT = "action_about";
        ACTION_NAMEZONE = "action_about_namezone";
        ACTION_UPLOAD_AVATAR = "action_upload_avatar";
        ACTION_SELECT_AVATAR = "action_select_avatar";
        ACTION_FIND_FRIENDS = "action_find_friends";
        ACTION_SEND_MESSAGE = "action_send_message";
        ACTION_SET_RELATION = "action_set_relation";
        ACTION_INVITE_TO_GROUP = "action_invite_to_group";
        ACTION_CALL = "action_call";
        ACTION_COMPLAIN = "action_complain";
        ACTION_DELETE_FRIENDS = "action_delete_friend";
        ACTION_TOUCH_PRESENT = "action_touch_present";
        ACTION_MAKE_PRESENT = "action_make_present";
        ACTION_SIGNIFICANT_OTHER = "action_significant_other";
        ACTION_SUBSCRIBE_TO_USER = "action_subscribe_news";
        ACTION_UNSUBSCRIBE_TO_USER = "action_unsubscribe_news";
        ACTION_MAKE_FRIENDS = "action_make_friend";
        ACTION_BLOCK_USER = "action_block_user";
        ACTION_MAKE_WEB_LINK = "action_copy_link";
    }

    public static void sendStatEvent(String event) {
        StatisticManager.getInstance().addStatisticEvent(PREFIX + event, new Pair[0]);
    }

    public static void sendStatEventForCurrentUser(String event) {
        StatisticManager.getInstance().addStatisticEvent(PREFIX_CURRENT_USER + event, new Pair[0]);
    }
}
