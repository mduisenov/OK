package ru.ok.android.ui.users.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.github.eterverda.sntp.SNTP;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.UsersStorageFacade;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.fragments.RefreshableRecyclerFragmentHelper;
import ru.ok.android.fragments.UsersListFragment;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.utils.users.OnlineUsersManager;
import ru.ok.android.ui.adapters.friends.UserInfosController.SelectionsMode;
import ru.ok.android.ui.adapters.friends.UsersInfoCursorAdapter;
import ru.ok.android.ui.custom.cards.IListLayoutDependentAdapter;
import ru.ok.android.ui.custom.cards.listcard.CardItem;
import ru.ok.android.ui.custom.cards.listcard.CardItem.Type;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.AbsListItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.UserCardItem.ItemRelationType;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.users.CursorSwapper;
import ru.ok.android.ui.users.fragments.data.FriendsConversationsOnlineLoader;
import ru.ok.android.utils.DimenUtils;
import ru.ok.android.utils.localization.base.LocalizedActivity;
import ru.ok.model.UserInfo.UserOnlineType;

public class OnlineUsersFragment extends UsersListFragment {
    private Adapter cardListAdapter;
    private RefreshableOnlineUsersHelper helper;
    private boolean isOnlyFriends;

    protected static class OnlineCardAdapter extends CardListAdapter implements IListLayoutDependentAdapter, CursorSwapper {
        private final List<AbsListItem> absListItems;
        private final boolean isOnlyFriends;
        private final boolean mAutoReQuery;
        protected ChangeObserver mChangeObserver;
        private Cursor mCursor;

        private class ChangeObserver extends ContentObserver {
            public ChangeObserver() {
                super(new Handler());
            }

            public boolean deliverSelfNotifications() {
                return true;
            }

            public void onChange(boolean selfChange) {
                OnlineCardAdapter.this.onContentChanged();
            }
        }

        public OnlineCardAdapter(Context context, Cursor cursor, boolean autoReQuery, boolean isOnlyFriends, List<AbsListItem> absListItems) {
            super((LocalizedActivity) context);
            this.mChangeObserver = new ChangeObserver();
            this.absListItems = absListItems;
            this.mAutoReQuery = autoReQuery;
            this.isOnlyFriends = isOnlyFriends;
            swapCursor(cursor);
        }

        private void updateListFromCursor() {
            List usersInfo = new ArrayList();
            if (this.mCursor == null) {
                setData(new ArrayList(0));
                return;
            }
            this.mCursor.moveToFirst();
            while (!this.mCursor.isAfterLast() && !this.mCursor.isBeforeFirst()) {
                usersInfo.add(UsersStorageFacade.cursor2User(this.mCursor));
                this.mCursor.moveToNext();
            }
            List<CardItem> cardItems = new ArrayList(2);
            cardItems.add(new CardItem().setAbsItemList(this.absListItems).setType(Type.list_abs));
            cardItems.add(new CardItem().setInfoList(usersInfo, this.isOnlyFriends ? ItemRelationType.friend : null));
            setData(cardItems);
        }

        public void onListLayoutChanged() {
            updateListFromCursor();
        }

        public Cursor swapCursor(Cursor newCursor) {
            if (newCursor == this.mCursor) {
                return newCursor;
            }
            Cursor oldCursor = this.mCursor;
            if (!(oldCursor == null || this.mChangeObserver == null)) {
                oldCursor.unregisterContentObserver(this.mChangeObserver);
            }
            this.mCursor = newCursor;
            if (!(newCursor == null || this.mChangeObserver == null)) {
                newCursor.registerContentObserver(this.mChangeObserver);
            }
            updateListFromCursor();
            return oldCursor;
        }

        public boolean headerIsEnable() {
            return false;
        }

        protected void onContentChanged() {
            if (this.mAutoReQuery && this.mCursor != null && !this.mCursor.isClosed()) {
                this.mCursor.requery();
                updateListFromCursor();
            }
        }
    }

    private class RefreshableOnlineUsersHelper extends RefreshableRecyclerFragmentHelper {
        RefreshableOnlineUsersHelper() {
            super(OnlineUsersFragment.this, OnlineUsersFragment.this.getActivity(), "online_update_time", 2131166277);
        }

        public void onStubButtonClick(SmartEmptyViewAnimated.Type type) {
            if (type == SmartEmptyViewAnimated.Type.NO_INTERNET) {
                OnlineUsersFragment.this.helper.onStartRefresh(true);
            }
        }

        public boolean onStartRefresh(boolean manual) {
            OnlineUsersFragment.this.emptyView.setState(State.LOADING);
            OnlineUsersManager manager = OnlineUsersManager.getInstance();
            if (manual) {
                manager.getOnlineUsersNow();
            } else if (!manager.getOnlineUsers()) {
                OnlineUsersFragment.this.helper.notifyRefreshSuccessful(null);
            }
            return true;
        }

        public void notifyRefreshFailed(ErrorType error) {
            super.notifyRefreshFailed(error);
            OnlineUsersFragment.this.emptyView.setState(State.LOADED);
            OnlineUsersFragment.this.emptyView.setType(error == ErrorType.NO_INTERNET ? SmartEmptyViewAnimated.Type.NO_INTERNET : SmartEmptyViewAnimated.Type.FRIENDS_ONLINE);
        }
    }

    public static OnlineUsersFragment newInstance(boolean doShowSelection, boolean onlyFriends, String selectedUserId, boolean addTopPadding) {
        Bundle args = new Bundle();
        UsersListFragment.initArguments(args, doShowSelection, selectedUserId, SelectionsMode.SINGLE, null);
        args.putBoolean("only_friends", onlyFriends);
        args.putBoolean("add_top_padding", addTopPadding);
        OnlineUsersFragment fragment = new OnlineUsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public boolean isOnlyFriends() {
        return this.isOnlyFriends;
    }

    boolean isAddTopPadding() {
        return getArguments().getBoolean("add_top_padding", false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainFragmentView = super.onCreateView(inflater, container, savedInstanceState);
        this.isOnlyFriends = getArguments().getBoolean("only_friends", false);
        if (isOnlyFriends()) {
            RecyclerView listView = (RecyclerView) mainFragmentView.findViewById(2131624731);
            if (isAddTopPadding()) {
                listView.setPadding(listView.getPaddingLeft(), listView.getPaddingTop() + DimenUtils.getToolbarHeight(getActivity()), listView.getPaddingRight(), listView.getPaddingBottom());
            }
            this.cardListAdapter = (Adapter) createOnlyFriendAdapter();
            listView.setAdapter(this.cardListAdapter);
        }
        return mainFragmentView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.emptyView.setType(SmartEmptyViewAnimated.Type.FRIENDS_ONLINE);
        getLoaderManager().initLoader(0, null, this);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.cardListAdapter instanceof IListLayoutDependentAdapter) {
            ((IListLayoutDependentAdapter) this.cardListAdapter).onListLayoutChanged();
        }
    }

    protected RefreshableRecyclerFragmentHelper createRefreshHelper() {
        RefreshableRecyclerFragmentHelper refreshableOnlineUsersHelper = new RefreshableOnlineUsersHelper();
        this.helper = refreshableOnlineUsersHelper;
        return refreshableOnlineUsersHelper;
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        super.onLoadFinished((Loader) cursorLoader, cursor);
        if (this.cardListAdapter != null) {
            ((CursorSwapper) this.cardListAdapter).swapCursor(cursor);
        }
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        super.onLoaderReset(cursorLoader);
        if (this.cardListAdapter != null) {
            ((CursorSwapper) this.cardListAdapter).swapCursor(null);
        }
    }

    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        if (!isOnlyFriends()) {
            return new FriendsConversationsOnlineLoader(getContext());
        }
        String sortOrder = "";
        if (isSortByLastOnline()) {
            sortOrder = "user_last_online DESC, ";
        }
        sortOrder = sortOrder + "user_n_first_name, user_n_last_name";
        return new CursorLoader(getActivity(), OdklProvider.friendsUri(), UsersStorageFacade.PROJECTION_FRIENDS, "(user_online = '" + UserOnlineType.MOBILE.name() + "' OR " + "user_online" + " = '" + UserOnlineType.WEB.name() + "') AND " + "user_id" + " <> ? " + "AND CAST((" + "user_last_online" + " + " + 1200000 + ") as INTEGER) > ?", new String[]{OdnoklassnikiApplication.getCurrentUser().uid, String.valueOf(SNTP.safeCurrentTimeMillisFromCache())}, sortOrder);
    }

    protected boolean isSortByLastOnline() {
        return true;
    }

    @Subscribe(on = 2131623946, to = 2131624165)
    public void onOnlineFriendsFetched(BusEvent event) {
        int i = 0;
        if (event.resultCode != -1) {
            this.helper.notifyRefreshFailed(ErrorType.from(event.bundleOutput));
        } else if (getActivity() != null) {
            boolean z;
            int count = event.bundleOutput.getInt("COUNT", 0);
            RefreshableOnlineUsersHelper refreshableOnlineUsersHelper = this.helper;
            if (count <= 0) {
                z = true;
            } else {
                z = false;
            }
            refreshableOnlineUsersHelper.notifyRefreshSuccessful(Boolean.valueOf(z));
            this.emptyView.setState(State.LOADED);
            this.emptyView.setType(SmartEmptyViewAnimated.Type.FRIENDS_ONLINE);
            SmartEmptyViewAnimated smartEmptyViewAnimated = this.emptyView;
            if (count > 0) {
                i = 8;
            }
            smartEmptyViewAnimated.setVisibility(i);
        }
    }

    protected UsersInfoCursorAdapter createRecyclerAdapter() {
        if (isOnlyFriends()) {
            return null;
        }
        UsersInfoCursorAdapter adapter = new UsersInfoCursorAdapter(getActivity(), null, this.doShowSelection, getSelectionsMode(), getSelectionParams(), this.selectedIds, this, this);
        adapter.setOnGoToMainPageSelectListener(this);
        adapter.setOnCallUserSelectListener(this);
        if (this.doShowSelection) {
            adapter.setSelectionUserId(getSelectedUserId());
        }
        return adapter;
    }

    protected CursorSwapper createOnlyFriendAdapter() {
        return new OnlineCardAdapter(getContext(), null, true, true, new ArrayList());
    }
}
