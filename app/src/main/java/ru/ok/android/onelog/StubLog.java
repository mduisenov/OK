package ru.ok.android.onelog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ru.ok.android.C0206R;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.utils.Logger;
import ru.ok.onelog.stub.StubAction;
import ru.ok.onelog.stub.StubFactory;
import ru.ok.onelog.stub.StubSource;

public final class StubLog {

    /* renamed from: ru.ok.android.onelog.StubLog.1 */
    static /* synthetic */ class C03831 {
        static final /* synthetic */ int[] f70x5605461;

        static {
            f70x5605461 = new int[Type.values().length];
            try {
                f70x5605461[Type.FRIENDS_LIST.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f70x5605461[Type.FRIENDS_LIST_CONVERSATIONS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f70x5605461[Type.FRIENDS_LIST_NO_BUTTON.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f70x5605461[Type.FRIENDS_LIST_USER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f70x5605461[Type.FRIENDS_LIST_MUSIC.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f70x5605461[Type.FRIENDS_ONLINE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f70x5605461[Type.SEARCH.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f70x5605461[Type.SEARCH_GLOBAL.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f70x5605461[Type.CONVERSATIONS_LIST.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                f70x5605461[Type.GROUP_TOPICS_LIST.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                f70x5605461[Type.MY_TOPICS_LIST.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                f70x5605461[Type.GROUPS_LIST.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                f70x5605461[Type.USER_TOPICS_LIST.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                f70x5605461[Type.STREAM.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                f70x5605461[Type.STREAM_PROFILE.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                f70x5605461[Type.NO_INTERNET.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                f70x5605461[Type.ERROR.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                f70x5605461[Type.EMPTY.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
            try {
                f70x5605461[Type.RESTRICTED.ordinal()] = 19;
            } catch (NoSuchFieldError e19) {
            }
            try {
                f70x5605461[Type.RESTRICTED_ACCESS_FOR_FRIENDS.ordinal()] = 20;
            } catch (NoSuchFieldError e20) {
            }
            try {
                f70x5605461[Type.RESTRICTED_YOU_ARE_IN_BLACK_LIST.ordinal()] = 21;
            } catch (NoSuchFieldError e21) {
            }
            try {
                f70x5605461[Type.USER_BLOCKED.ordinal()] = 22;
            } catch (NoSuchFieldError e22) {
            }
            try {
                f70x5605461[Type.GROUP_BLOCKED.ordinal()] = 23;
            } catch (NoSuchFieldError e23) {
            }
            try {
                f70x5605461[Type.NOTIFICATIONS.ordinal()] = 24;
            } catch (NoSuchFieldError e24) {
            }
            try {
                f70x5605461[Type.GUESTS.ordinal()] = 25;
            } catch (NoSuchFieldError e25) {
            }
            try {
                f70x5605461[Type.MUSIC.ordinal()] = 26;
            } catch (NoSuchFieldError e26) {
            }
            try {
                f70x5605461[Type.MUSIC_TUNERS.ordinal()] = 27;
            } catch (NoSuchFieldError e27) {
            }
            try {
                f70x5605461[Type.MUSIC_EXTENSION_TRACKS.ordinal()] = 28;
            } catch (NoSuchFieldError e28) {
            }
            try {
                f70x5605461[Type.MUSIC_HISTORY_TRACKS.ordinal()] = 29;
            } catch (NoSuchFieldError e29) {
            }
            try {
                f70x5605461[Type.MUSIC_MY_COLLECTIONS.ordinal()] = 30;
            } catch (NoSuchFieldError e30) {
            }
            try {
                f70x5605461[Type.MUSIC_USER_COLLECTIONS.ordinal()] = 31;
            } catch (NoSuchFieldError e31) {
            }
            try {
                f70x5605461[Type.MUSIC_MY_TRACKS.ordinal()] = 32;
            } catch (NoSuchFieldError e32) {
            }
            try {
                f70x5605461[Type.MUSIC_USER_TRACKS.ordinal()] = 33;
            } catch (NoSuchFieldError e33) {
            }
            try {
                f70x5605461[Type.PHOTO_LOAD_FAIL.ordinal()] = 34;
            } catch (NoSuchFieldError e34) {
            }
            try {
                f70x5605461[Type.ALBUM_LOAD_FAIL.ordinal()] = 35;
            } catch (NoSuchFieldError e35) {
            }
            try {
                f70x5605461[Type.PHOTOS.ordinal()] = 36;
            } catch (NoSuchFieldError e36) {
            }
            try {
                f70x5605461[Type.ALBUMS.ordinal()] = 37;
            } catch (NoSuchFieldError e37) {
            }
            try {
                f70x5605461[Type.FRIEND_PRESENTS.ordinal()] = 38;
            } catch (NoSuchFieldError e38) {
            }
            try {
                f70x5605461[Type.MY_SENT_PRESENTS.ordinal()] = 39;
            } catch (NoSuchFieldError e39) {
            }
            try {
                f70x5605461[Type.MY_RECEIVED_PRESENTS.ordinal()] = 40;
            } catch (NoSuchFieldError e40) {
            }
        }
    }

    public static void logStubShow(@NonNull Type type) {
        logStub(StubAction.show, type);
    }

    public static void logStubClick(@NonNull Type type) {
        logStub(StubAction.click, type);
    }

    public static void logStubClickButton(@NonNull Type type) {
        logStub(StubAction.click_button, type);
    }

    private static void logStub(@NonNull StubAction action, @NonNull Type type) {
        StubSource stubSource = getSourceFromType(type);
        if (stubSource != null && stubSource != StubSource.empty) {
            OneLog.log(StubFactory.get(action, stubSource));
        }
    }

    @Nullable
    private static StubSource getSourceFromType(@NonNull Type type) {
        switch (C03831.f70x5605461[type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return StubSource.friends_list;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return StubSource.friends_list_conversations;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return StubSource.friends_list_no_button;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return StubSource.friends_list_user;
            case Message.UUID_FIELD_NUMBER /*5*/:
                return StubSource.friends_list_music;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return StubSource.friends_online;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                return StubSource.search;
            case Message.TASKID_FIELD_NUMBER /*8*/:
                return StubSource.search_global;
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                return StubSource.conversations_list;
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                return StubSource.groups_topic_list;
            case Message.EDITINFO_FIELD_NUMBER /*11*/:
                return StubSource.my_topics_list;
            case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                return StubSource.groups_topic_list;
            case Conversation.OWNERID_FIELD_NUMBER /*13*/:
                return StubSource.user_topics_list;
            case C0206R.styleable.Toolbar_titleMarginEnd /*14*/:
                return StubSource.stream;
            case C0206R.styleable.Toolbar_titleMarginTop /*15*/:
                return StubSource.stream_profile;
            case C0206R.styleable.Toolbar_titleMarginBottom /*16*/:
                return StubSource.no_internet_with_refresh;
            case C0206R.styleable.Toolbar_maxButtonHeight /*17*/:
                return StubSource.error;
            case C0206R.styleable.Toolbar_collapseIcon /*18*/:
                return StubSource.empty;
            case C0206R.styleable.Toolbar_collapseContentDescription /*19*/:
                return StubSource.restricted;
            case C0206R.styleable.Toolbar_navigationIcon /*20*/:
                return StubSource.restricted_access_for_friends;
            case C0206R.styleable.Toolbar_navigationContentDescription /*21*/:
                return StubSource.restricted_you_are_in_black_list;
            case C0206R.styleable.Toolbar_logoDescription /*22*/:
                return StubSource.user_blocked;
            case C0206R.styleable.Toolbar_titleTextColor /*23*/:
                return StubSource.group_blocked;
            case C0206R.styleable.Toolbar_subtitleTextColor /*24*/:
                return StubSource.notifications;
            case C0206R.styleable.Theme_actionMenuTextAppearance /*25*/:
                return StubSource.guests;
            case C0206R.styleable.Theme_actionMenuTextColor /*26*/:
                return StubSource.music_empty;
            case C0206R.styleable.Theme_actionModeStyle /*27*/:
                return StubSource.music_tuners;
            case C0206R.styleable.Theme_actionModeCloseButtonStyle /*28*/:
                return StubSource.music_extension_tracks;
            case C0206R.styleable.Theme_actionModeBackground /*29*/:
                return StubSource.music_history_tracks;
            case C0206R.styleable.Theme_actionModeSplitBackground /*30*/:
                return StubSource.music_my_collections;
            case C0206R.styleable.Theme_actionModeCloseDrawable /*31*/:
                return StubSource.music_user_collections;
            case C0206R.styleable.Theme_actionModeCutDrawable /*32*/:
                return StubSource.music_my_tracks;
            case C0206R.styleable.Theme_actionModeCopyDrawable /*33*/:
                return StubSource.music_user_tracks;
            case C0206R.styleable.Theme_actionModePasteDrawable /*34*/:
                return StubSource.photo_load_fail;
            case C0206R.styleable.Theme_actionModeSelectAllDrawable /*35*/:
                return StubSource.album_load_fail;
            case C0206R.styleable.Theme_actionModeShareDrawable /*36*/:
                return StubSource.photos;
            case C0206R.styleable.Theme_actionModeFindDrawable /*37*/:
                return StubSource.albums;
            case C0206R.styleable.Theme_actionModeWebSearchDrawable /*38*/:
                return StubSource.friends_presents;
            case C0206R.styleable.Theme_actionModePopupWindowStyle /*39*/:
                return StubSource.my_sent_presents;
            case C0206R.styleable.Theme_textAppearanceLargePopupMenu /*40*/:
                return StubSource.my_received_presents;
            default:
                Logger.m185w("Unsupported stub view type! Add it to one-log spec! Type: %s", type);
                return null;
        }
    }
}
