package ru.ok.android.onelog;

import ru.ok.onelog.profile.ProfileAction;
import ru.ok.onelog.profile.ProfileFactory;

public final class ProfileLog {
    public static void logProfileSidebarOpen() {
        OneLog.log(ProfileFactory.get(ProfileAction.enter_from_side_bar));
    }

    public static void logProfileSidebarOpenNewUser() {
        OneLog.log(ProfileFactory.get(ProfileAction.new_user_enter_from_side_bar));
    }
}
