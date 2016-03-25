package ru.ok.android.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.music.MusicPlayerInActionBarFragment;
import ru.ok.android.fragments.music.TrackSelectionControlImpl;
import ru.ok.android.ui.SearchQueryTextHandler;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.adapters.CheckChangeAdapter.OnCheckStateChangeListener;
import ru.ok.android.ui.fragments.handlers.SearchMusicViewHandler;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.DeviceUtils.DeviceLayoutType;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.controls.music.MusicAddActionModeCallBack;
import ru.ok.android.utils.controls.music.MusicMultiDeleteAddControl;
import ru.ok.android.utils.controls.music.MusicMultiDeleteAddControl.OnAddTrackListener;
import ru.ok.android.utils.controls.music.MusicPageSelectListener;
import ru.ok.android.utils.controls.music.OnSelectAlbumsForArtistListener;
import ru.ok.android.utils.controls.music.OnSelectArtistListener;
import ru.ok.android.utils.controls.music.OnSelectArtistSimilarMusicListener;
import ru.ok.android.utils.controls.music.SearchMusicControl;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.Track;

@SameFragmentWithoutBundleEqualCheck
public final class SearchMusicFragment extends MusicPlayerInActionBarFragment implements OnItemLongClickListener, OnCheckStateChangeListener, OnAddTrackListener, MusicPageSelectListener, OnSelectAlbumsForArtistListener, OnSelectArtistListener, OnSelectArtistSimilarMusicListener {
    private ActionMode actionMode;
    private SearchMusicActionModeCallBack actionModeCallback;
    private boolean addVisible;
    private boolean first;
    private SearchMusicViewHandler handler;
    private MenuItem itemAdd;
    private MusicFragmentMode mode;
    private MusicMultiDeleteAddControl multiDeleteAddControl;
    private SearchMusicControl searchMusicControl;
    private SearchMusicTextWatcher textWatcher;

    /* renamed from: ru.ok.android.ui.fragments.SearchMusicFragment.1 */
    class C08131 implements OnActionExpandListener {
        C08131() {
        }

        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        public boolean onMenuItemActionCollapse(MenuItem item) {
            FragmentManager manager = SearchMusicFragment.this.getFragmentManager();
            if (manager == null || manager.getBackStackEntryCount() <= 0 || !SearchMusicFragment.this.isResumed()) {
                NavigationHelper.finishActivity(SearchMusicFragment.this.getActivity());
            } else {
                manager.popBackStackImmediate();
            }
            return false;
        }
    }

    class SearchMusicActionModeCallBack extends MusicAddActionModeCallBack {
        SearchMusicActionModeCallBack(Context context) {
            super(context);
        }

        protected void onClickItemActionMode() {
            SearchMusicFragment.this.multiDeleteAddControl.addTracks(SearchMusicFragment.this.searchMusicControl.getMusicListControl().getSelectedData());
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            SearchMusicFragment.this.searchMusicControl.getMusicListControl().switchToSelectionMode(new TrackSelectionControlImpl());
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {
            super.onDestroyActionMode(mode);
            SearchMusicFragment.this.searchMusicControl.getMusicListControl().switchToStandardMode();
        }
    }

    class SearchMusicTextWatcher extends SearchQueryTextHandler {
        SearchMusicTextWatcher() {
            super(1000);
        }

        protected void onSearchQueryChange(String s) {
            if (!TextUtils.isEmpty(s)) {
                SearchMusicFragment.this.getArguments().putString("START_TEXT", s);
                SearchMusicFragment.this.searchMusicControl.tryToGetSearchMusic(s);
            }
        }
    }

    public SearchMusicFragment() {
        this.textWatcher = new SearchMusicTextWatcher();
        this.addVisible = false;
        this.first = true;
    }

    protected CharSequence getTitle() {
        return LocalizationManager.getString(getActivity(), 2131166479);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        this.mode = getMode();
        this.handler = new SearchMusicViewHandler(this.mode, getActivity());
        View view = this.handler.createView(inflater, container, savedInstanceState);
        this.handler.setOnItemLongClickListener(this);
        this.searchMusicControl = new SearchMusicControl(getActivity(), this.handler, this.mode);
        this.searchMusicControl.setOnSelectArtistListener(this);
        this.searchMusicControl.setOnSelectArtistSimilarMusicListener(this);
        this.searchMusicControl.setOnSelectAlbumForArtistListener(this);
        this.searchMusicControl.setMusicPageSelectListener(this);
        this.searchMusicControl.getMusicListControl().setOnCheckStateChangeListener(this);
        this.multiDeleteAddControl = new MusicMultiDeleteAddControl();
        this.multiDeleteAddControl.setAddTrackListener(this);
        this.actionModeCallback = new SearchMusicActionModeCallBack(getActivity());
        this.textWatcher.onSearchQueryChange(getStartSearchText());
        this.first = true;
        return view;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.handler.onDestroyView();
        this.searchMusicControl.cleanup();
    }

    protected int getLayoutId() {
        return 2130903427;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getMode().onCreateOptionsMenu(menu, inflater, this, true)) {
            int menuResourceId;
            if (DeviceUtils.getType(OdnoklassnikiApplication.getContext()) == DeviceLayoutType.LARGE) {
                menuResourceId = 2131689525;
            } else {
                menuResourceId = 2131689524;
            }
            if (inflateMenuLocalized(menuResourceId, menu)) {
                this.itemAdd = menu.findItem(2131625438);
                updateAddItemVisibility();
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateAddItemVisibility() {
        if (this.itemAdd != null) {
            this.itemAdd.setVisible(this.addVisible);
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem searchItem = menu.findItem(2131625511);
        if (searchItem != null) {
            MenuItemCompat.expandActionView(searchItem);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchItem.expandActionView();
            searchView.setQueryHint(getStringLocalized(2131166235));
            searchView.setOnQueryTextListener(this.textWatcher);
            searchView.setQuery(getStartSearchText(), false);
            if (!this.first) {
                searchView.clearFocus();
            }
            this.first = false;
            MenuItemCompat.setOnActionExpandListener(searchItem, new C08131());
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2131625438:
                showSelectedMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Bundle newArguments(String startText, MusicFragmentMode mode) {
        Bundle args = new Bundle();
        args.putString("START_TEXT", startText);
        args.putParcelable("music-fragment-mode", mode);
        return args;
    }

    private String getStartSearchText() {
        return getArguments().getString("START_TEXT");
    }

    public void onSelectArtist(Artist artist) {
        Logger.m173d("artist=%s", artist);
        NavigationHelper.showArtistPage(getActivity(), artist, this.mode);
    }

    public void onShowArtistSimilarPage(Artist artist) {
        Logger.m173d("artist=%s", artist);
        NavigationHelper.showArtistSimilarPage(getActivity(), artist, this.mode);
    }

    public void onSelectAlbumsForArtist(Artist artist) {
        Logger.m173d("artist=%s", artist);
        NavigationHelper.showAlbumsPage(getActivity(), artist, this.mode, true);
    }

    private void showSelectedMode() {
        if (getActivity() != null) {
            this.actionMode = ((BaseCompatToolbarActivity) getActivity()).getSupportToolbar().startActionMode(this.actionModeCallback);
        }
    }

    private void hideSelectedMode() {
        if (this.actionMode != null) {
            this.actionMode.finish();
        }
    }

    public void onCheckStateChange(boolean checkState) {
        if (this.actionModeCallback.getItem() != null) {
            this.actionModeCallback.getItem().setEnabled(checkState);
        }
    }

    public void onPause() {
        super.onPause();
        hideSelectedMode();
    }

    public void onAddTracksSuccessful(Track[] tracks) {
        Toast.makeText(getActivity(), getStringLocalized(2131165342), 0).show();
        hideSelectedMode();
    }

    public void onAddTracksFailed() {
        Toast.makeText(getActivity(), getStringLocalized(2131165797), 0).show();
        hideSelectedMode();
    }

    public void onSelectMultiAddDeletePage() {
        this.addVisible = true;
        updateAddItemVisibility();
    }

    public void onSelectSimplePage() {
        this.addVisible = false;
        updateAddItemVisibility();
        hideSelectedMode();
    }

    protected void onHideFragment() {
        super.onHideFragment();
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        showSelectedMode();
        return true;
    }
}
