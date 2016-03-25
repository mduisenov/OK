package ru.ok.android.utils.bus;

public class BusProtocol {
    public static String PREF_IS_UPDATE_MEDIA_PLAYER_STATE;
    public static String PREF_MEDIA_PLAYER_DURATION;
    public static String PREF_MEDIA_PLAYER_ERROR_MESSAGE;
    public static String PREF_MEDIA_PLAYER_PROGRESS;
    public static String PREF_MEDIA_PLAYER_STATE;
    public static String PREF_MEDIA_PLAYER_STATE_MUSIC_INFO_CONTAINER;
    public static String PREF_PLAY_INFO_ERROR_KEY;
    public static String USER;

    static {
        PREF_MEDIA_PLAYER_STATE_MUSIC_INFO_CONTAINER = "pref_media_player_state_music_info_container";
        PREF_MEDIA_PLAYER_STATE = "pref_media_player_state";
        PREF_MEDIA_PLAYER_ERROR_MESSAGE = "pref_media_player_error_message";
        PREF_PLAY_INFO_ERROR_KEY = "pref_music_key_error";
        PREF_MEDIA_PLAYER_PROGRESS = "pref_media_player_progress";
        PREF_MEDIA_PLAYER_DURATION = "pref_media_player_duration";
        PREF_IS_UPDATE_MEDIA_PLAYER_STATE = "is_update_pref_media_player_state";
        USER = "user";
    }
}
