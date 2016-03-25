package ru.ok.android.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import ru.mail.libverify.C0176R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.music.MusicPlayerInActionBarFragmentWithStub;
import ru.ok.android.ui.SearchQueryTextHandler;
import ru.ok.android.ui.adapters.friends.HeaderMusicAdapter;
import ru.ok.android.ui.adapters.friends.UsersMusicCardAdapter;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.AbsListItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.MusicNewInterest;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.MyMusic;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.Radio;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.controls.music.MusicControlUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.music.MusicUserInfo;

public class MusicUsersFragment extends MusicPlayerInActionBarFragmentWithStub implements LoaderCallbacks<Cursor>, OnStubButtonClickListener {
    private boolean collapseSearchOnStop;
    private UsersMusicCardAdapter cursorAdapter;
    private final SparseArray<HeaderMusicAdapter> headerMusicAdapters;
    private MenuItem itemSearch;
    private ViewGroup mMainView;
    private Messenger mMessenger;
    private LinearLayoutManager recyclerLayoutManager;
    private SearchMusicTextWatcher searchMusicTextWatcher;
    private SearchView searchView;
    private RecyclerView usersListView;

    /* renamed from: ru.ok.android.ui.fragments.MusicUsersFragment.1 */
    class C08031 extends Handler {
        C08031() {
        }

        public void handleMessage(Message msg) {
            if (MusicUsersFragment.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    class SearchMusicTextWatcher extends SearchQueryTextHandler {
        private final MusicFragmentMode mode;

        SearchMusicTextWatcher() {
            super(1000);
            this.mode = MusicUsersFragment.this.getMode();
        }

        protected void onSearchQueryChange(String s) {
            if (s.length() > 0) {
                if (DeviceUtils.isTablet(MusicUsersFragment.this.getContext())) {
                    MusicUsersFragment.this.itemSearch.setVisible(false);
                } else if (MusicUsersFragment.this.itemSearch != null) {
                    MusicUsersFragment.this.collapseSearchOnStop = true;
                }
                NavigationHelper.showSearchMusic(MusicUsersFragment.this.getActivity(), s, this.mode);
            }
        }
    }

    public MusicUsersFragment() {
        this.headerMusicAdapters = new SparseArray();
        this.mMessenger = new Messenger(new C08031());
    }

    protected int getLayoutId() {
        return 2130903340;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LocalizationManager.from(getActivity());
        this.mMainView = (ViewGroup) LocalizationManager.inflate(getActivity(), 2130903340, container, false);
        this.searchMusicTextWatcher = new SearchMusicTextWatcher();
        setHasOptionsMenu(true);
        this.usersListView = (RecyclerView) this.mMainView.findViewById(2131625126);
        this.recyclerLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.usersListView.setLayoutManager(this.recyclerLayoutManager);
        ArrayList<View> headerMusicViews = new ArrayList();
        this.headerMusicAdapters.clear();
        List<AbsListItem> list = new ArrayList();
        list.add(new MusicNewInterest());
        if (getMode() != MusicFragmentMode.MULTI_SELECTION) {
            list.add(new Radio());
        }
        list.add(new MyMusic());
        this.cursorAdapter = new UsersMusicCardAdapter(getContext(), null, true, list, getMode());
        this.emptyView = (SmartEmptyViewAnimated) this.mMainView.findViewById(C0263R.id.empty_view);
        this.emptyView.setButtonClickListener(this);
        this.usersListView.setAdapter(this.cursorAdapter);
        return this.mMainView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tryToUpdate();
        getLoaderManager().initLoader(1, null, this);
    }

    public void onStubButtonClick(Type type) {
        tryToUpdate();
    }

    protected String getTitle() {
        return LocalizationManager.from(getContext()).getString(2131166223);
    }

    public static Bundle newArguments(boolean selected, String uid, MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putString("SELECTING_UID", uid);
        args.putBoolean("SELECTED", selected);
        args.putBoolean("SELECTED", selected);
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!getMode().onCreateOptionsMenu(menu, inflater, this, true) || inflateMenuLocalized(2131689530, menu)) {
            super.onCreateOptionsMenu(menu, inflater);
            this.itemSearch = menu.findItem(2131625511);
            this.searchView = (SearchView) MenuItemCompat.getActionView(this.itemSearch);
            this.searchView.setQueryHint(getStringLocalized(2131166235));
            ImageView imageButton = (ImageView) this.searchView.findViewById(C0176R.id.search_button);
            if (imageButton != null) {
                imageButton.setImageResource(2130837592);
            }
            this.searchView.setOnQueryTextListener(this.searchMusicTextWatcher);
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
        if (getMode().onPrepareOptionsMenu(menu, this)) {
            super.onPrepareOptionsMenu(menu);
        }
    }

    public void setSelectionUser(String selectionUserUid) {
        if (this.cursorAdapter != null && !TextUtils.isEmpty(selectionUserUid)) {
            if (OdnoklassnikiApplication.getCurrentUser().uid.equals(selectionUserUid)) {
                this.cursorAdapter.setSelectionMyMusic();
            } else {
                this.cursorAdapter.setSelectionUserId(selectionUserUid);
            }
        }
    }

    protected void onHideFragment() {
        super.onHideFragment();
    }

    public void clearSelectPosition() {
        for (int i = 0; i < this.headerMusicAdapters.size(); i++) {
            HeaderMusicAdapter adapter = (HeaderMusicAdapter) this.headerMusicAdapters.valueAt(i);
            if (adapter != null) {
                adapter.setSelection(false);
            }
        }
        if (this.cursorAdapter != null) {
            this.cursorAdapter.clearSelection();
        }
    }

    protected void onInternetAvailable() {
        super.onInternetAvailable();
        tryToUpdate();
    }

    public void onStop() {
        super.onStop();
        if (this.collapseSearchOnStop && this.itemSearch != null) {
            this.collapseSearchOnStop = false;
            this.itemSearch.collapseActionView();
        }
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), OdklProvider.musicFriendsUri(), null, null, null, "friends_music.add_time DESC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        this.cursorAdapter.swapCursor(cursor);
        SmartEmptyViewAnimated smartEmptyViewAnimated = this.emptyView;
        int i = (cursor == null || cursor.getCount() == 0) ? 0 : 8;
        smartEmptyViewAnimated.setVisibility(i);
        dbLoadCompleted();
        setSelectionUser(getArguments() == null ? null : getArguments().getString("SELECTING_UID"));
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.cursorAdapter.swapCursor(null);
    }

    public void tryToUpdate() {
        showProgressStub();
        Message message = Message.obtain(null, 2131624059);
        message.replyTo = this.mMessenger;
        GlobalBus.sendMessage(message);
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 143:
                onWebLoadSuccess(Type.FRIENDS_LIST_MUSIC, ((MusicUserInfo[]) ((MusicUserInfo[]) msg.obj)).length != 0);
                return false;
            case 144:
                onWebLoadError(msg.obj);
                if (getActivity() == null) {
                    return false;
                }
                MusicControlUtils.onError(getActivity(), msg);
                return false;
            default:
                return true;
        }
    }
}
