package ru.ok.android.ui.users.fragments.profiles;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.List;
import ru.ok.android.C0206R;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.activity.UserMusicActivity;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.ui.presents.helpers.PresentSettingsHelper;
import ru.ok.android.ui.users.fragments.data.UserSectionItem;
import ru.ok.android.ui.users.fragments.profiles.statistics.UserProfileStatisticsManager;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.WebUrlCreator;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;
import ru.ok.model.Discussion;
import ru.ok.onelog.groups.GroupsPageOpenFactory;
import ru.ok.onelog.groups.GroupsPageOpenSource;

public abstract class BaseUserProfileNavigationHandler implements OnItemClickListener {
    protected final Activity activity;
    private final List<UserSectionItem> items;

    /* renamed from: ru.ok.android.ui.users.fragments.profiles.BaseUserProfileNavigationHandler.1 */
    static /* synthetic */ class C13341 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type;

        static {
            $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type = new int[Type.values().length];
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.photos.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.music.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.friends.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.forum.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.share.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.games.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.groups.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.mygroups.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.user_videos.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.friend_holidays.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.myholidays.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.holidays.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.my_presents.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.friend_presents.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.progress.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
        }
    }

    protected abstract List<UserSectionItem> createItems();

    protected abstract String getUserId();

    BaseUserProfileNavigationHandler(Activity activity) {
        this.items = createItems();
        this.activity = activity;
    }

    public List<UserSectionItem> getSectionItems() {
        return this.items;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        onSectionClicked((UserSectionItem) this.items.get(position));
    }

    protected void onSectionClicked(UserSectionItem item) {
        switch (C13341.$SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[item.getType().ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                onPhotoItemSelect();
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.SECTION_PHOTOS);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                onMusicItemSelect();
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.SECTION_MUSIC);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                onFriendsItemSelect();
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.SECTION_FRIENDS);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                onForumItemSelect();
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.SECTION_FORUM);
            case Message.UUID_FIELD_NUMBER /*5*/:
                onTopicsItemSelect();
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.SECTION_TOPICS);
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                onBaseWebItemSelect(item.getType());
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.SECTION_APPLICATIONS);
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                NavigationHelper.showUserGroups(this.activity, getUserId());
                OneLog.log(GroupsPageOpenFactory.get(GroupsPageOpenSource.user_profile));
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.SECTION_GROUPS);
            case Message.TASKID_FIELD_NUMBER /*8*/:
                NavigationHelper.showGroups(this.activity);
                OneLog.log(GroupsPageOpenFactory.get(GroupsPageOpenSource.user_profile));
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.SECTION_GROUPS);
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                onBaseWebItemSelect(item.getType());
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.SECTION_VIDEOS);
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
            case Message.EDITINFO_FIELD_NUMBER /*11*/:
            case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                onBaseWebItemSelect(item.getType());
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.SECTION_HOLIDAYS);
            case Conversation.OWNERID_FIELD_NUMBER /*13*/:
            case C0206R.styleable.Toolbar_titleMarginEnd /*14*/:
                if (PresentSettingsHelper.getSettings().nativeMySentAndReceivedEnabled) {
                    NavigationHelper.showUserReceivedPresents(this.activity, getUserId(), null);
                } else {
                    onBaseWebItemSelect(item.getType());
                }
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.SECTION_PRESENTS);
            case C0206R.styleable.Toolbar_titleMarginTop /*15*/:
                onBaseWebItemSelect(item.getType());
                UserProfileStatisticsManager.sendStatEvent(UserProfileStatisticsManager.SECTION_ACHIEVEMENTS);
            default:
        }
    }

    protected void onBaseWebItemSelect(Type itemType) {
        NavigationHelper.showExternalUrlPage(this.activity, WebUrlCreator.getUrl(itemType.getMethodName(), getUserId(), null), false, itemType.isNeedTabBar());
    }

    protected void onPhotoItemSelect() {
        NavigationHelper.showUserPhotoAlbums(this.activity, getUserId(), false);
    }

    protected void onTopicsItemSelect() {
        NavigationHelper.showUserTopics(this.activity, getUserId());
    }

    protected void onMusicItemSelect() {
        Intent intent = new Intent(this.activity, UserMusicActivity.class);
        intent.putExtra("extra_user_id", getUserId());
        this.activity.startActivity(intent);
    }

    protected void onFriendsItemSelect() {
        NavigationHelper.showExternalUrlPage(this.activity, WebUrlCreator.getUrl(Type.friends.getMethodName(), getUserId(), null), false);
    }

    protected void onForumItemSelect() {
        NavigationHelper.showDiscussionCommentsFragment(this.activity, new Discussion(getUserId(), "USER_FORUM"), Page.MESSAGES, null);
    }
}
