package ru.ok.android.ui.users.fragments.data;

import ru.ok.android.ui.base.profile.ProfileSectionItem;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;
import ru.ok.java.api.response.users.UserCounters;

public enum UserSectionItem implements ProfileSectionItem<UserCounters> {
    FRIENDS(Type.friends) {
        public int getCount(UserCounters counters) {
            return counters.friends;
        }
    },
    PHOTOS(Type.photos) {
        public int getCount(UserCounters counters) {
            return counters.photosInPhotoAlbums + counters.photosPersonal;
        }
    },
    GROUPS(Type.groups) {
        public int getCount(UserCounters counters) {
            return counters.groups;
        }
    },
    MY_GROUPS(Type.mygroups) {
        public int getCount(UserCounters counters) {
            return counters.groups;
        }
    },
    NOTES(Type.share) {
        public int getCount(UserCounters counters) {
            return counters.statuses;
        }
    },
    MUSIC(Type.music),
    VIDEOS(Type.user_videos),
    HOLIDAYS(Type.holidays),
    FRIEND_HOLIDAYS(Type.friend_holidays),
    MY_HOLIDAYS(Type.myholidays),
    GAMES(Type.games) {
        public int getCount(UserCounters counters) {
            return 0;
        }
    },
    PRESENTS(Type.friend_presents) {
        public int getCount(UserCounters counters) {
            return counters.presents;
        }
    },
    MY_PRESENTS(Type.my_presents) {
        public int getCount(UserCounters counters) {
            return counters.presents;
        }
    },
    ACHIEVEMENTS(Type.progress),
    FORUM(Type.forum);
    
    private final Type type;

    private UserSectionItem(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public int getCount(UserCounters counters) {
        return 0;
    }

    public int getNameResourceId() {
        return this.type.getNameResId();
    }
}
