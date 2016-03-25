package ru.ok.android.ui.tabbar;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import ru.ok.android.fragments.ConversationsFriendsFragment;
import ru.ok.android.slidingmenu.OdklSlidingMenuFragmentActivity;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.ShowFragmentActivity;
import ru.ok.android.ui.tabbar.actions.ConversationPageAction;
import ru.ok.android.ui.tabbar.actions.DiscussionPageAction;
import ru.ok.android.ui.tabbar.actions.FeedPageAction;
import ru.ok.android.ui.tabbar.actions.MenuTabbarAction;
import ru.ok.android.ui.tabbar.actions.MusicPageAction;
import ru.ok.android.ui.tabbar.actions.ResetNotificationsAction.OnActionListener;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NavigationHelper.Source;
import ru.ok.android.utils.NavigationHelper.Tag;
import ru.ok.android.widget.menuitems.SlidingMenuHelper;

public final class OdklTabbar extends Tabbar implements OnActionListener {
    private ConversationPageAction conversationAction;
    private DiscussionPageAction discussionsAction;
    private FeedPageAction feedAction;
    private MenuTabbarAction menuTabbarAction;
    private MusicPageAction musicAction;

    public OdklTabbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void buildActions() {
        this.conversationAction = new ConversationPageAction(this);
        this.discussionsAction = new DiscussionPageAction(this);
        this.feedAction = new FeedPageAction(this);
        this.menuTabbarAction = new MenuTabbarAction(this);
        this.musicAction = new MusicPageAction(getContext(), this);
        if (getContext() instanceof OdklSlidingMenuFragmentActivity) {
            ((OdklSlidingMenuFragmentActivity) getContext()).getSlidingMenuStrategy().buildTabbarActions(this);
            return;
        }
        addAction(this.menuTabbarAction);
        addAction(this.feedAction);
        addAction(this.discussionsAction);
        addAction(this.conversationAction);
        addAction(this.musicAction);
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        ((View) getParent()).setVisibility(visibility);
    }

    public MenuTabbarAction getMenuTabbarAction() {
        return this.menuTabbarAction;
    }

    public MusicPageAction getMusicAction() {
        return this.musicAction;
    }

    public FeedPageAction getFeedAction() {
        return this.feedAction;
    }

    public DiscussionPageAction getDiscussionsAction() {
        return this.discussionsAction;
    }

    public ConversationPageAction getConversationAction() {
        return this.conversationAction;
    }

    public void onResetNotification(Action predAction, Action currentAction) {
        if ((predAction instanceof DiscussionPageAction) && PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(getResources().getString(2131165714), 0) != 0) {
            predAction.hideBubble();
        }
    }

    public boolean onPerformAction(Action action) {
        Logger.m173d("action=%s", action);
        if (action != null) {
            String source = null;
            Activity activity = (Activity) getContext();
            if (action == this.conversationAction) {
                NavigationHelper.showConversationsPage(activity);
                updateIfExistConversation();
                source = "conversations";
            } else if (action == this.feedAction) {
                NavigationHelper.showFeedPage(activity, Source.tab_bar, getCurrentAction() == this.feedAction ? Tag.feed : null);
                source = "stream";
            } else if (action == this.discussionsAction) {
                NavigationHelper.showDiscussionPage(activity);
                source = "discussions";
            } else if (action == this.musicAction) {
                NavigationHelper.showMusicPage(activity);
                source = "music";
            } else if (action == this.menuTabbarAction) {
                SlidingMenuHelper.clickMenu(activity);
            }
            if (source != null) {
                StatisticManager.getInstance().addStatisticEvent("tabbar-clicked", Pair.create("source", source));
            }
        }
        return true;
    }

    private void updateIfExistConversation() {
        if (getContext() instanceof ShowFragmentActivity) {
            ConversationsFriendsFragment fragment = (ConversationsFriendsFragment) ((ShowFragmentActivity) getContext()).getSupportFragmentManager().findFragmentByTag("CONVERSATION_TAG");
            if (fragment != null && fragment.isAdded() && fragment.isVisible()) {
                fragment.refresh();
            }
        }
    }

    public void updateConversationsCounter(int count) {
        this.conversationAction.showBubble(count);
    }

    public void onShowConversations() {
        onSelectAction(this.conversationAction);
    }

    public void onShowFeedPage() {
        onSelectAction(this.feedAction);
    }

    public void onShowDiscussionPage() {
        onSelectAction(this.discussionsAction);
    }

    public void onShowMusicPage() {
        onSelectAction(this.musicAction);
    }

    public void onResume() {
        this.musicAction.registerReceiver();
    }

    public void onPause() {
        this.musicAction.unRegisterReceiver();
    }

    public void processDiscussionsEvents(int fDiscussionsReplyCount, int fDiscussionsLikeCount, int fDiscussionsCount) {
        if (fDiscussionsReplyCount > 0) {
            this.discussionsAction.showReplyBubble();
        } else if (fDiscussionsLikeCount > 0) {
            this.discussionsAction.showLikeBubble();
        } else if (fDiscussionsCount > 0) {
            this.discussionsAction.hideBubble();
            this.discussionsAction.showBubble(fDiscussionsCount);
        } else {
            this.discussionsAction.hideBubble();
        }
    }

    private void processAction(Action action, int count) {
        if (action != null) {
            if (count > 0) {
                action.showBubble(count);
            } else {
                action.hideBubble();
            }
        }
    }

    public void processMenuAction(int marksCount, int guestsCount, int eventCount) {
        processAction(this.menuTabbarAction, (marksCount + guestsCount) + eventCount);
    }

    public void processFeedEvent(int eventsCount) {
        processAction(this.feedAction, eventsCount);
    }
}
