package ru.ok.android.widget.menuitems;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Pair;
import ru.ok.android.C0206R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;
import ru.ok.android.fragments.web.hooks.WebLinksProcessor;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.onelog.ProfileLog;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.banners.BannerLinksUtils;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.Source;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.controls.authorization.AuthorizationControl;
import ru.ok.android.widget.MenuView;
import ru.ok.android.widget.MenuView.MenuItem;
import ru.ok.android.widget.menuitems.StandardItem.BubbleState;
import ru.ok.model.Discussion;
import ru.ok.model.stream.banner.Banner;
import ru.ok.model.stream.banner.PromoLink;
import ru.ok.onelog.groups.GroupsPageOpenFactory;
import ru.ok.onelog.groups.GroupsPageOpenSource;

public final class SlidingMenuHelper {

    /* renamed from: ru.ok.android.widget.menuitems.SlidingMenuHelper.1 */
    static class C14991 implements Runnable {
        final /* synthetic */ OdklSlidingMenuFragmentActivity val$activity;
        final /* synthetic */ MenuView val$menu;
        final /* synthetic */ MenuItem val$menuItem;
        final /* synthetic */ Type val$type;
        final /* synthetic */ WebLinksProcessor val$webLinksProcessor;

        C14991(Type type, MenuItem menuItem, OdklSlidingMenuFragmentActivity odklSlidingMenuFragmentActivity, WebLinksProcessor webLinksProcessor, MenuView menuView) {
            this.val$type = type;
            this.val$menuItem = menuItem;
            this.val$activity = odklSlidingMenuFragmentActivity;
            this.val$webLinksProcessor = webLinksProcessor;
            this.val$menu = menuView;
        }

        public void run() {
            StatisticManager.getInstance().addStatisticEvent("left_menu-item_clicked", Pair.create("source", this.val$type.name()));
            Logger.m173d("Sliding menu item selected: %s", this.val$type);
            switch (C15002.$SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[this.val$type.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    if (this.val$menuItem instanceof BannerItem) {
                        BannerItem bannerItem = this.val$menuItem;
                        Banner banner = bannerItem.getBanner();
                        PromoLink promoLink = bannerItem.getPromoLink();
                        BannerLinksUtils.processBannerClick(banner, this.val$activity, this.val$webLinksProcessor);
                        Utils.sendPixels(promoLink, 2, this.val$activity);
                    }
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    NavigationHelper.showFriends(this.val$activity, true);
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    NavigationHelper.showDiscussionPage(this.val$activity);
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    NavigationHelper.showConversationsPage(this.val$activity);
                case Message.UUID_FIELD_NUMBER /*5*/:
                    NavigationHelper.showGroupsFromMenu(this.val$activity);
                    OneLog.log(GroupsPageOpenFactory.get(GroupsPageOpenSource.sliding_menu));
                case Message.REPLYTO_FIELD_NUMBER /*6*/:
                    if (this.val$menu != null) {
                        this.val$menu.open();
                    }
                case Message.ATTACHES_FIELD_NUMBER /*7*/:
                    NavigationHelper.showSettings(this.val$activity, false);
                case Message.TASKID_FIELD_NUMBER /*8*/:
                    NavigationHelper.showWebSettings(this.val$activity);
                case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                    if ((this.val$menuItem instanceof UserItem) && ((UserItem) this.val$menuItem).isNewUser()) {
                        ProfileLog.logProfileSidebarOpenNewUser();
                    }
                    ProfileLog.logProfileSidebarOpen();
                    NavigationHelper.showCurrentUser(this.val$activity, true);
                case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                    StatisticManager.getInstance().addStatisticEvent("menu-logout", new Pair[0]);
                    AuthorizationControl.getInstance().showLogoutDialog(this.val$activity);
                case Message.EDITINFO_FIELD_NUMBER /*11*/:
                    NavigationHelper.showFeedPage(this.val$activity, Source.sliding_menu);
                case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                    NavigationHelper.showFeedbackPage(this.val$activity, true);
                case Conversation.OWNERID_FIELD_NUMBER /*13*/:
                    NavigationHelper.showFaqPage(this.val$activity, true);
                case C0206R.styleable.Toolbar_titleMarginEnd /*14*/:
                    if (OdnoklassnikiApplication.getCurrentUser() != null) {
                        NavigationHelper.showUserPhotoAlbums(this.val$activity, OdnoklassnikiApplication.getCurrentUser().uid, true);
                    }
                case C0206R.styleable.Toolbar_titleMarginTop /*15*/:
                    NavigationHelper.showPayment(this.val$activity, true);
                case C0206R.styleable.Toolbar_titleMarginBottom /*16*/:
                    NavigationHelper.showDiscussionCommentsFragment(this.val$activity, new Discussion(OdnoklassnikiApplication.getCurrentUser().uid, ru.ok.java.api.response.discussion.info.DiscussionGeneralInfo.Type.USER_FORUM.name()), Page.MESSAGES, null);
                case C0206R.styleable.Toolbar_maxButtonHeight /*17*/:
                    NavigationHelper.showUserTopics(this.val$activity, OdnoklassnikiApplication.getCurrentUser().getId());
                case C0206R.styleable.Toolbar_collapseIcon /*18*/:
                    NavigationHelper.showVideos(this.val$activity, true);
                default:
                    String url = SlidingMenuHelper.getUrl(this.val$type);
                    if (!TextUtils.isEmpty(url)) {
                        NavigationHelper.showExternalUrlPage(this.val$activity, url, true, this.val$type);
                    }
            }
        }
    }

    /* renamed from: ru.ok.android.widget.menuitems.SlidingMenuHelper.2 */
    static /* synthetic */ class C15002 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type;

        static {
            $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type = new int[Type.values().length];
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.banner.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.friends.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.discussion.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.conversation.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.groups.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.menu.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.settings.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.profile_settings.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.user.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.exit.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.stream.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.feedback.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.faq.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.photos.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.recharge.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.forum.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.share.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$ru$ok$android$widget$menuitems$SlidingMenuHelper$Type[Type.videos.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
        }
    }

    public enum Type {
        friends(2131166584, 2130838427, "profile/<user_id>/friends"),
        discussion(2131166576, 2130838412),
        conversation(2131166575, 2130838451),
        photos(2131166597, 2130838464, "profile/<user_id>/photos"),
        videos(2131166606, 2130838493, "video"),
        user_videos(2131166606, 2130838493, "profile/<user_id>/video"),
        mygroups(2131166586, 2130838433, "profile/<user_id>/groups/my"),
        groups(2131166586, 2130838433),
        holidays(2131166589, 2130838442, "profile/<user_id>/friend_holidays"),
        events(2131166581, 2130838415, "events"),
        stream(2131166605, 2130838421),
        exit(2131166582, 2130838418),
        share(2131166604, 2130838481, "profile/<user_id>/statuses"),
        games(2131166585, 2130838430, "games", false),
        forum(2131166583, 2130838424, "profile/<user_id>/forum"),
        bookmarks(2131166574, 2130838409, "bookmarks"),
        myholidays(2131166589, 2130838442, "profile/<user_id>/holidays"),
        friend_holidays(2131166589, 2130838442, "profile/<user_id>/holidays"),
        friend_presents(2131166599, 2130838467, "profile/<user_id>/gifts"),
        my_presents(2131166599, 2130838467, "gifts/my"),
        make_present(2131166599, 2130838467, "gifts"),
        blacklist(2131166573, 2130838406, "blacklist"),
        feedback(2131166588, 2130838439),
        faq(2131165851, 2130838439),
        settings(2131166603, 2130838478),
        profile_settings(2131166410, 2130838494),
        menu(2131166590, 2130838446),
        search(2131166602, 2130838418, "api/search"),
        music(2131166592, 2130838455),
        grid(2131166592, 2130838455),
        pymk(2131166592, 2130838455, "profile/<user_id>/pymk"),
        user(2131166592, 2130838455),
        banner(2131166592, 2130838455),
        online(2131166593, 2130838490, "online"),
        progress(2131166600, 2130838403, "profile/<user_id>/achievements"),
        recharge(2131166601, 2130838461, "online"),
        photos_albums(2131166597, 2130838464, "profile/<user_id>/photos"),
        more(2131166591, 2130838439),
        notifications(2131166596, 2130838458),
        guests(2131166594, 2130838436),
        marks(2131166595, 2130838472);
        
        private final int iconRes;
        private boolean isNeedTabBar;
        private final String methodName;
        private final int nameRes;

        private Type(int nameRes, int iconRes) {
            this(r7, r8, nameRes, iconRes, "");
        }

        private Type(int nameRes, int iconRes, String methodName) {
            this.isNeedTabBar = true;
            this.nameRes = nameRes;
            this.iconRes = iconRes;
            this.methodName = methodName;
        }

        private Type(int nameRes, int iconRes, String methodName, boolean isNeedTabBar) {
            this(r1, r2, nameRes, iconRes, methodName);
            this.isNeedTabBar = isNeedTabBar;
        }

        public String getMethodName() {
            return this.methodName;
        }

        public int getNameResId() {
            return this.nameRes;
        }

        public boolean isNeedTabBar() {
            return this.isNeedTabBar;
        }
    }

    public static boolean processClickItemAndReturnNeededCloseMenu(OdklSlidingMenuFragmentActivity activity, WebLinksProcessor webLinksProcessor, Type type, MenuView menu, MenuItem menuItem) {
        activity.getSlidingMenuStrategy().processRunnableClick(new C14991(type, menuItem, activity, webLinksProcessor, menu));
        return type != Type.exit;
    }

    public static StandardItem createStandardItem(OdklSlidingMenuFragmentActivity activity, Type type, int height, BubbleState bubleState) {
        return new StandardItem(activity, type.iconRes, type.nameRes, type, height, bubleState);
    }

    public static String getUrl(Type type) {
        String methodName = type.getMethodName();
        String uid = JsonSessionTransportProvider.getInstance().getStateHolder().getUserId();
        if (TextUtils.isEmpty(uid)) {
            return null;
        }
        return ShortLinkUtils.getUrl(methodName, uid, "<user_id>");
    }

    public static void clickMenu(Activity activity) {
        StatisticManager.getInstance().addStatisticEvent("left_menu-open_but_toolbar", new Pair[0]);
        if (activity instanceof OdklSlidingMenuFragmentActivity) {
            OdklSlidingMenuFragmentActivity activityCast = (OdklSlidingMenuFragmentActivity) activity;
            if (activityCast.isMenuOpen()) {
                activityCast.closeMenu();
            } else {
                activityCast.openMenu();
            }
        }
    }

    public static void closeMenu(Activity activity) {
        if (activity instanceof OdklSlidingMenuFragmentActivity) {
            ((OdklSlidingMenuFragmentActivity) activity).closeMenu();
        }
    }

    public static void openMenu(Activity activity) {
        if (activity instanceof OdklSlidingMenuFragmentActivity) {
            ((OdklSlidingMenuFragmentActivity) activity).openMenu();
        }
    }
}
