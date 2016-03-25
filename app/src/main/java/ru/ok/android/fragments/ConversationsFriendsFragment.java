package ru.ok.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.UsersListFragment.UsersListFragmentListener;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.custom.indicator.PagerSlidingTabStrip;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.messaging.fragments.ConversationsFragment;
import ru.ok.android.ui.messaging.fragments.ConversationsFragment.ConversationsFragmentListener;
import ru.ok.android.ui.messaging.fragments.MessagingPromptController;
import ru.ok.android.ui.users.UsersSelectionParams;
import ru.ok.android.ui.users.fragments.FriendsListFilteredFragment;
import ru.ok.android.ui.users.fragments.OnlineUsersFragment;
import ru.ok.android.ui.utils.FabHelper;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.ServicesSettingsHelper;

public final class ConversationsFriendsFragment extends BaseFragment {
    private boolean argShowConversations;
    private boolean argShowOnlineFriends;
    private boolean argShowSelection;
    private ConversationsFragment conversationsFragment;
    private FloatingActionButton createChatFab;
    private FriendsListFilteredFragment friendsListFragment;
    private ConversationsFriendsFragmentListener listener;
    private OnlineUsersFragment onlineUsersFragment;
    private PagerAdapter pagerAdapter;
    private List<Integer> pages;
    private ViewPager viewPager;

    /* renamed from: ru.ok.android.fragments.ConversationsFriendsFragment.1 */
    class C02721 implements OnClickListener {
        C02721() {
        }

        public void onClick(View v) {
            ConversationsFriendsFragment.createMultichat(ConversationsFriendsFragment.this);
        }
    }

    public interface ConversationsFriendsFragmentListener {
        void onConversationSelected(String str, String str2);
    }

    private class ConversationsPagerAdapter extends FragmentPagerAdapter {
        private final List<Integer> pages;

        /* renamed from: ru.ok.android.fragments.ConversationsFriendsFragment.ConversationsPagerAdapter.1 */
        class C02731 implements UsersListFragmentListener {
            C02731() {
            }

            public void onUserSelected(String uid) {
                Logger.m173d("from FriendsFragment uid=%s", uid);
                ConversationsFriendsFragment.this.onSelectedUser(null, uid, 1);
            }

            public void onListCrated(RecyclerView listView) {
            }
        }

        /* renamed from: ru.ok.android.fragments.ConversationsFriendsFragment.ConversationsPagerAdapter.2 */
        class C02742 implements ConversationsFragmentListener {
            C02742() {
            }

            public void onConversationSelected(String conversationId, String userId) {
                Logger.m173d("from ConversationsFragment conversationId=%s userId=%s", conversationId, userId);
                ConversationsFriendsFragment.this.onSelectedUser(conversationId, userId, 0);
            }

            public void onUpdatedConversationsCounter(int counter) {
                Logger.m173d("from ConversationsFragment counter=%d", Integer.valueOf(counter));
                if (ConversationsFriendsFragment.this.getActivity() != null) {
                    EventsManager.getInstance().updateConversationsCounter(counter);
                }
            }

            public void onListCrated(RecyclerView recyclerView) {
            }
        }

        /* renamed from: ru.ok.android.fragments.ConversationsFriendsFragment.ConversationsPagerAdapter.3 */
        class C02753 implements UsersListFragmentListener {
            C02753() {
            }

            public void onUserSelected(String uid) {
                Logger.m173d("from OnlineUsersFragment uid=%s", uid);
                ConversationsFriendsFragment.this.onSelectedUser(null, uid, 2);
            }

            public void onListCrated(RecyclerView listView) {
            }
        }

        public ConversationsPagerAdapter(List<Integer> pages) {
            super(ConversationsFriendsFragment.this.getChildFragmentManager());
            this.pages = pages;
        }

        public CharSequence getPageTitle(int position) {
            int titleResId;
            switch (((Integer) this.pages.get(position)).intValue()) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    titleResId = 2131165377;
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    titleResId = 2131166316;
                    break;
                default:
                    titleResId = 2131165376;
                    break;
            }
            return LocalizationManager.getString(ConversationsFriendsFragment.this.getContext(), titleResId);
        }

        public Object instantiateItem(ViewGroup container, int position) {
            Object fragment = super.instantiateItem(container, position);
            switch (ConversationsFriendsFragment.this.getPageTypeByPosition(((Integer) this.pages.get(position)).intValue())) {
                case RECEIVED_VALUE:
                    ConversationsFriendsFragment.this.conversationsFragment = (ConversationsFragment) fragment;
                    ConversationsFriendsFragment.this.conversationsFragment.setListener(new C02742());
                    break;
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    ConversationsFriendsFragment.this.friendsListFragment = (FriendsListFilteredFragment) fragment;
                    ConversationsFriendsFragment.this.friendsListFragment.setListener(new C02731());
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    ConversationsFriendsFragment.this.onlineUsersFragment = (OnlineUsersFragment) fragment;
                    ConversationsFriendsFragment.this.onlineUsersFragment.setListener(new C02753());
                    break;
            }
            return fragment;
        }

        public Fragment getItem(int position) {
            boolean z = true;
            boolean z2;
            switch (ConversationsFriendsFragment.this.getPageTypeByPosition(((Integer) this.pages.get(position)).intValue())) {
                case RECEIVED_VALUE:
                    boolean access$500 = ConversationsFriendsFragment.this.isShowSelection();
                    String access$600 = ConversationsFriendsFragment.this.getSelectedUserId();
                    String access$700 = ConversationsFriendsFragment.this.getSelectedConversationId();
                    if (ConversationsFriendsFragment.this.listener != null) {
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    return ConversationsFragment.newInstance(access$500, access$600, access$700, z2);
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    return FriendsListFilteredFragment.newInstance(ConversationsFriendsFragment.this.isShowSelection(), ConversationsFriendsFragment.this.getSelectedUserId());
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    z2 = ConversationsFriendsFragment.this.isShowSelection();
                    if (ConversationsFriendsFragment.this.argShowConversations) {
                        z = false;
                    }
                    return OnlineUsersFragment.newInstance(z2, z, ConversationsFriendsFragment.this.getSelectedUserId(), false);
                default:
                    return null;
            }
        }

        public int getCount() {
            return this.pages.size();
        }
    }

    protected String getTitle() {
        return getStringLocalized(2131165638);
    }

    public void onResume() {
        super.onResume();
        if (isFragmentVisible()) {
            onOpenFragment();
        }
    }

    public void onPause() {
        super.onPause();
        if (isFragmentVisible()) {
            onHideFragment();
        }
    }

    protected int getLayoutId() {
        return 2130903136;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.m172d("");
        View fragmentView = inflater.inflate(2130903136, container, false);
        setHasOptionsMenu(true);
        initArguments();
        initPager(fragmentView);
        setSelectedUser(getSelectedUserId(), getSelectedConversationId());
        this.createChatFab = FabHelper.createChatFab(getContext(), getCoordinatorManager().coordinatorLayout);
        this.createChatFab.setOnClickListener(new C02721());
        return fragmentView;
    }

    private String getSelectedUserId() {
        return getArguments().getString("ConversationsFriendsFragment.selectionUid");
    }

    private String getSelectedConversationId() {
        return getArguments().getString("ConversationsFriendsFragment.selectionConversationId");
    }

    private void initArguments() {
        Bundle args = getArguments();
        if (args != null) {
            this.argShowSelection = args.getBoolean("SELECTION", false);
            this.argShowConversations = args.getBoolean("show_conversations", true);
            this.argShowOnlineFriends = args.getBoolean("show_online_friends", true);
        } else {
            this.argShowSelection = false;
            this.argShowConversations = false;
            this.argShowOnlineFriends = true;
        }
        Logger.m173d("argShowSelection=%s argShowConversations=%s argShowOnlineFriends", Boolean.valueOf(this.argShowSelection), Boolean.valueOf(this.argShowConversations), Boolean.valueOf(this.argShowOnlineFriends));
    }

    public void setListener(ConversationsFriendsFragmentListener listener) {
        this.listener = listener;
    }

    private int getPageTypeByPosition(int position) {
        return ((Integer) getPages().get(position)).intValue();
    }

    private int getPageIndex(int page) {
        List<Integer> pages = getPages();
        for (int i = 0; i < pages.size(); i++) {
            if (((Integer) pages.get(i)).intValue() == page) {
                return i;
            }
        }
        return 0;
    }

    private List<Integer> getPages() {
        if (this.pages == null) {
            this.pages = new ArrayList();
            if (this.argShowConversations) {
                this.pages.add(Integer.valueOf(0));
            }
            this.pages.add(Integer.valueOf(1));
            if (this.argShowOnlineFriends) {
                this.pages.add(Integer.valueOf(2));
            }
        }
        return this.pages;
    }

    private void initPager(View fragmentView) {
        this.viewPager = (ViewPager) fragmentView.findViewById(C0263R.id.pager);
        PagerSlidingTabStrip indicator = (PagerSlidingTabStrip) fragmentView.findViewById(C0263R.id.indicator);
        List<Integer> pages = getPages();
        this.pagerAdapter = new ConversationsPagerAdapter(pages);
        this.viewPager.setOffscreenPageLimit(pages.size() - 1);
        this.viewPager.setAdapter(this.pagerAdapter);
        this.viewPager.setCurrentItem(getPageIndex(0));
        this.viewPager.setOnPageChangeListener(null);
        indicator.setViewPager(this.viewPager);
    }

    public static Bundle newArguments(String preSelectedConversationId, String preselectedUserId, boolean showConversation, boolean showOnlineFriends, boolean extraSelection) {
        Bundle bundle = new Bundle();
        bundle.putString("ConversationsFriendsFragment.selectionConversationId", preSelectedConversationId);
        bundle.putString("ConversationsFriendsFragment.selectionUid", preselectedUserId);
        bundle.putBoolean("SELECTION", extraSelection);
        bundle.putBoolean("show_conversations", showConversation);
        bundle.putBoolean("show_online_friends", showOnlineFriends);
        return bundle;
    }

    protected void onShowFragment() {
        Logger.m172d("");
        super.onShowFragment();
        onOpenFragment();
    }

    private void onOpenFragment() {
        Logger.m172d("");
        if (NetUtils.isConnectionAvailable(getActivity(), false)) {
            refresh(true);
        }
    }

    protected void ensureFab() {
        super.ensureFab();
        getCoordinatorManager().ensureFab(this.createChatFab);
    }

    protected void removeFab() {
        super.removeFab();
        getCoordinatorManager().remove(this.createChatFab);
    }

    protected void onInternetAvailable() {
        Logger.m172d("");
        super.onInternetAvailable();
        if (isFragmentVisible()) {
            refresh();
        }
    }

    public void refresh() {
        refresh(false);
    }

    public void refresh(boolean manual) {
        if (this.conversationsFragment != null) {
            Logger.m172d("start refreshing conversations...");
            this.conversationsFragment.onRefresh();
        }
        if (this.onlineUsersFragment != null) {
            Logger.m172d("start refreshing online users...");
            this.onlineUsersFragment.startRefresh(manual);
        }
        if (this.friendsListFragment != null) {
            Logger.m172d("start refreshing friends...");
            this.friendsListFragment.startRefresh(manual);
        }
    }

    private boolean isShowSelection() {
        return this.argShowSelection;
    }

    public void setSelectedUser(String userId, String conversationId) {
        getArguments().putString("ConversationsFriendsFragment.selectionUid", userId);
        getArguments().putString("ConversationsFriendsFragment.selectionConversationId", conversationId);
        Logger.m173d("uid = %s, conversationId = %s", userId, conversationId);
        if (this.conversationsFragment != null) {
            if (!TextUtils.isEmpty(conversationId)) {
                this.conversationsFragment.setSelectedConversation(conversationId);
            } else if (TextUtils.isEmpty(userId)) {
                this.conversationsFragment.clearSelection();
            } else {
                this.conversationsFragment.setSelectedUser(userId);
            }
        }
        if (this.friendsListFragment != null) {
            this.friendsListFragment.setSelectedUser(userId);
        }
        if (this.onlineUsersFragment != null) {
            this.onlineUsersFragment.setSelectedUser(userId);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (inflateMenuLocalized(2131689482, menu)) {
            MenuItem createShortcutItem = menu.findItem(2131625452);
            boolean z = !isShowSelection() || TextUtils.isEmpty(getSelectedConversationId());
            createShortcutItem.setVisible(z);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean z = false;
        switch (item.getItemId()) {
            case 2131625451:
                StatisticManager.getInstance().addStatisticEvent("conversations-search-filter", new Pair[0]);
                if (this.listener != null) {
                    z = true;
                }
                NavigationHelper.showFilterableUsers(this, 1, z);
                return true;
            case 2131625452:
                MessagingPromptController.installShortcut(getContext());
                MessagingPromptController.logAdd("menu");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void createMultichat(Fragment fragment) {
        StatisticManager.getInstance().addStatisticEvent("multichat-create-chat", new Pair[0]);
        NavigationHelper.selectFriendsFilteredForChat(fragment, new UsersSelectionParams(ServicesSettingsHelper.getServicesSettings().getMultichatMaxParticipantsCount()), 2, 0);
    }

    private void onSelectedUser(String conversationId, String userId, int fragmentSource) {
        getArguments().putString("ConversationsFriendsFragment.selectionUid", userId);
        getArguments().putString("ConversationsFriendsFragment.selectionConversationId", conversationId);
        if (this.listener != null) {
            this.listener.onConversationSelected(conversationId, userId);
            return;
        }
        if (fragmentSource == 1 && this.friendsListFragment != null) {
            this.friendsListFragment.setSelectedUser(userId);
            NavigationHelper.showMessagesForUser(getActivity(), userId);
        } else if (fragmentSource == 0 && this.conversationsFragment != null) {
            this.conversationsFragment.setSelectedConversation(conversationId);
            NavigationHelper.showMessagesForConversation(getActivity(), conversationId, userId);
        } else if (fragmentSource == 2 && this.onlineUsersFragment != null) {
            this.onlineUsersFragment.setSelectedUser(userId);
            NavigationHelper.showMessagesForUser(getActivity(), userId);
        }
        getActivity().supportInvalidateOptionsMenu();
    }

    public void onLocalizationChanged() {
        super.onLocalizationChanged();
        if (getActivity() != null && this.viewPager != null) {
            this.viewPager.getAdapter().notifyDataSetChanged();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624138)
    public void onConversationDeleted(BusEvent event) {
        if (event.resultCode == -1) {
            if (this.friendsListFragment != null) {
                this.friendsListFragment.setSelectedUser(null);
            }
            if (this.onlineUsersFragment != null) {
                this.onlineUsersFragment.setSelectedUser(null);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int i = 0;
        switch (requestCode) {
            case RECEIVED_VALUE:
                if (resultCode == -1) {
                    String conversationId = data.getStringExtra("conversation_id");
                    String userId = data.getStringExtra("user_id");
                    if (TextUtils.isEmpty(conversationId)) {
                        i = 1;
                    }
                    onSelectedUser(conversationId, userId, i);
                }
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (resultCode == -1) {
                    onSelectedUser(null, data.getStringExtra("user_id"), 0);
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
