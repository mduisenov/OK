package ru.ok.android.ui.groups.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import ru.ok.android.ui.base.profile.ProfileSectionItem;
import ru.ok.java.api.response.groups.GroupCounters;

public enum GroupSectionItem implements ProfileSectionItem<GroupCounters> {
    THEMES(2131166722, 2130838484, "group/<user_id>/topics") {
        public int getCount(GroupCounters counters) {
            return counters.themes;
        }
    },
    PHOTO_ALBUMS(2131166598, 2130838464, null) {
        public int getCount(GroupCounters counters) {
            return counters.photoAlbums;
        }
    },
    VIDEOS(2131166606, 2130838493, "group/<user_id>/video") {
        public int getCount(GroupCounters counters) {
            return counters.videos;
        }
    },
    MEMBERS(2131166189, 2130838433, "group/<user_id>/members") {
        public int getCount(GroupCounters counters) {
            return counters.members;
        }
    },
    LINKS(2131166042, 2130838445, "group/<user_id>/links") {
        public int getCount(GroupCounters counters) {
            return counters.links;
        }
    };
    
    public static final List<GroupSectionItem> ADMIN_LIST;
    public static final List<GroupSectionItem> GENERAL_LIST;
    private final int iconResourceId;
    private final String methodName;
    private final int nameResourceId;

    static {
        GENERAL_LIST = Collections.unmodifiableList(new ArrayList(Arrays.asList(values())));
        ADMIN_LIST = Arrays.asList(values());
    }

    private GroupSectionItem(int nameResourceId, int iconResourceId, String methodName) {
        this.nameResourceId = nameResourceId;
        this.iconResourceId = iconResourceId;
        this.methodName = methodName;
    }

    public int getCount(GroupCounters counters) {
        return 0;
    }

    public int getNameResourceId() {
        return this.nameResourceId;
    }

    public String getMethodName() {
        return this.methodName;
    }
}
