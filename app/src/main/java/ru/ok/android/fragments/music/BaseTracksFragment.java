package ru.ok.android.fragments.music;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.RecyclerViewCheckable;
import ru.ok.android.ui.activity.compat.BaseCompatToolbarActivity;
import ru.ok.android.ui.adapters.friends.BaseCursorRecyclerAdapter;
import ru.ok.android.ui.adapters.music.DotsCursorAdapter.OnDotsClickListener;
import ru.ok.android.ui.adapters.music.playlist.PlayListCursorAdapter;
import ru.ok.android.ui.adapters.music.playlist.PlayListCursorAdapter.OnCheckedChangeListener;
import ru.ok.android.ui.adapters.music.playlist.PlayListCursorAdapter.PlayListAdapterCallback;
import ru.ok.android.ui.adapters.music.playlist.ViewHolder.AnimateType;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController.OnItemClickListener;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController.OnItemLongClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.dialogs.ChangeTrackStateBase.OnChangeTrackStateListener;
import ru.ok.android.ui.dialogs.actions.ChangeTrackActionBox;
import ru.ok.android.ui.swiperefresh.SwipeUpRefreshLayout;
import ru.ok.android.ui.utils.EmptyViewRecyclerDataObserver;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.HideKeyboardRecyclerScrollHelper;
import ru.ok.android.utils.bus.BusProtocol;
import ru.ok.android.utils.controls.music.ChangeTrackControl;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.music.MusicPlayerUtils;
import ru.ok.android.utils.refresh.RefreshProvider;
import ru.ok.android.utils.refresh.SwipeUpRefreshProvider;
import ru.ok.model.wmf.Track;

public abstract class BaseTracksFragment extends MusicPlayerInActionBarFragmentWithStub implements LoaderCallbacks<Cursor>, Callback, OnDotsClickListener<Track>, OnCheckedChangeListener, PlayListAdapterCallback, OnItemClickListener, OnItemLongClickListener, OnChangeTrackStateListener {
    protected ActionMode actionMode;
    protected PlayListCursorAdapter adapter;
    private ChangeTrackActionBox changeTrackActionBox;
    private ChangeTrackControl changeTrackControl;
    private Callback externalActionModeCallback;
    protected MenuItem item;
    protected RecyclerViewCheckable listView;
    protected View mMainView;
    protected LinearLayoutManager recyclerLayoutManager;
    protected RefreshProvider refreshProvider;
    private TrackSelectionControl trackSelectionControl;

    protected abstract MusicFragmentMode getMode();

    protected abstract MusicListType getType();

    protected abstract void requestTracks();

    public BaseTracksFragment() {
        this.changeTrackActionBox = null;
    }

    protected int getLayoutId() {
        return 2130903392;
    }

    protected boolean isRefreshEnabled() {
        return false;
    }

    protected boolean isRefreshing() {
        return false;
    }

    public Adapter createWrapperAdapter(BaseCursorRecyclerAdapter adapter) {
        return adapter;
    }

    public void postListViewInit(RecyclerView listView) {
    }

    public boolean isPlayFloatingButtonRequired() {
        return false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof TrackSelectionControl) {
            this.trackSelectionControl = (TrackSelectionControl) getActivity();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        this.changeTrackControl = new ChangeTrackControl(getContext());
        this.mMainView = LocalizationManager.inflate(getContext(), 2130903392, null, false);
        this.refreshProvider = new SwipeUpRefreshProvider((SwipeUpRefreshLayout) this.mMainView.findViewById(2131624611));
        this.listView = (RecyclerViewCheckable) this.mMainView.findViewById(2131625228);
        this.recyclerLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.listView.setLayoutManager(this.recyclerLayoutManager);
        this.listView.addOnScrollListener(new HideKeyboardRecyclerScrollHelper(getContext(), this.mMainView));
        this.emptyView = (SmartEmptyViewAnimated) this.mMainView.findViewById(C0263R.id.empty_view);
        this.emptyView.setButtonClickListener(this);
        showProgressStub();
        this.adapter = new PlayListCursorAdapter(getContext(), this, getMode());
        this.adapter.registerAdapterDataObserver(new EmptyViewRecyclerDataObserver(this.emptyView, this.adapter));
        this.adapter.setOnCheckedChangeListener(this);
        this.adapter.setOnDotsClickListener(this);
        this.listView.setAdapter(createWrapperAdapter(this.adapter));
        this.listView.setChoiceMode(2);
        this.adapter.getItemClickListenerController().addItemClickListener(this);
        this.adapter.getItemClickListenerController().addItemLongClickListener(this);
        this.refreshProvider.setRefreshEnabled(isRefreshEnabled());
        this.refreshProvider.setRefreshing(isRefreshing());
        postListViewInit(this.listView);
        return this.mMainView;
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.adapter.swapCursor(null);
    }

    public void showPlay(int playPosition, AnimateType animate) {
        hidePlay(animate);
        this.adapter.setPlayingTrackPosition(playPosition);
        ViewHolder recyclerViewHolder = this.listView.findViewHolderForAdapterPosition(getListPosition4DataPosition(playPosition));
        if (recyclerViewHolder instanceof ru.ok.android.ui.adapters.music.playlist.ViewHolder) {
            ((ru.ok.android.ui.adapters.music.playlist.ViewHolder) recyclerViewHolder).setPlayState(animate);
        }
    }

    public void hidePlay(AnimateType animate) {
        int firstVisible = this.recyclerLayoutManager.findFirstVisibleItemPosition();
        if (firstVisible != -1) {
            int lastVisible = this.recyclerLayoutManager.findLastVisibleItemPosition();
            for (int i = firstVisible; i <= lastVisible; i++) {
                ViewHolder recyclerViewHolder = this.listView.findViewHolderForLayoutPosition(i);
                if (recyclerViewHolder instanceof ru.ok.android.ui.adapters.music.playlist.ViewHolder) {
                    ru.ok.android.ui.adapters.music.playlist.ViewHolder holder = (ru.ok.android.ui.adapters.music.playlist.ViewHolder) recyclerViewHolder;
                    holder.setUnPlayState(animate);
                    this.adapter.updateInCacheValue(holder);
                }
            }
            this.adapter.hidePlayingTrack();
        }
    }

    public void onStubButtonClick(Type type) {
        requestTracks();
    }

    public void onMediaPlayerState(BusEvent event) {
        if (isFragmentVisible()) {
            AnimateType animateType = (getActivity() == null || !isVisible()) ? AnimateType.NONE : event.bundleOutput.getBoolean(BusProtocol.PREF_IS_UPDATE_MEDIA_PLAYER_STATE, false) ? AnimateType.TRANSLATE : AnimateType.FADE;
            int playPosition = event.bundleOutput.getInt("playlist_track_position", -1);
            if (MusicPlayerUtils.isShowPlay(event.bundleOutput, getType(), getPlaylistId())) {
                showPlay(playPosition, animateType);
            } else {
                hidePlay(animateType);
            }
        }
    }

    public void onItemClick(View view, int position) {
        if (this.item != null || getMode() == MusicFragmentMode.MULTI_SELECTION) {
            this.listView.setItemChecked(position, !this.listView.isItemChecked(position));
            this.adapter.notifyDataSetChanged();
            return;
        }
        onSelectPosition(position, MusicStorageFacade.getTracksFromCursor(this.adapter.getCursor()));
    }

    public boolean onItemLongClick(View view, int position) {
        if (getMode() != MusicFragmentMode.STANDARD) {
            return false;
        }
        showSelectedMode();
        return true;
    }

    public void onDotsClick(Track track, View view) {
        onDotsClickToTrack(track, view);
    }

    protected int getListPosition4DataPosition(int dataPosition) {
        return dataPosition;
    }

    protected void onDotsClickToTrack(Track track, View view) {
        StatisticManager.getInstance().addStatisticEvent("music-add_swipe", new Pair[0]);
        this.changeTrackActionBox = null;
        if (getType() == MusicListType.MY_MUSIC) {
            this.changeTrackActionBox = ChangeTrackActionBox.createDeleteTrackBox(getContext(), track, view);
        } else {
            this.changeTrackActionBox = ChangeTrackActionBox.createAddTrackBox(getContext(), track, view);
        }
        this.changeTrackActionBox.setOnChangeTrackStateListener(this);
        this.changeTrackActionBox.show();
    }

    public void onSelectPosition(int position, ArrayList<Track> tracks) {
        MusicService.startPlayMusic(OdnoklassnikiApplication.getContext(), position, tracks, getType(), getPlaylistId());
    }

    protected String getPlaylistId() {
        return "none";
    }

    public void onSetStatusTrack(Track track) {
        this.changeTrackControl.setStatusTrack(track);
    }

    public void onAddTrack(Track track) {
        this.changeTrackControl.addTrack(track);
    }

    public void onDeleteTrack(Track track) {
        this.changeTrackControl.deleteTrack(track);
    }

    public void setSelectionMode(MusicFragmentMode mode) {
        SparseBooleanArray selectedPositions = this.listView.getCheckedItemPositions();
        for (int i = 0; i < selectedPositions.size(); i++) {
            if (selectedPositions.get(selectedPositions.keyAt(i))) {
                this.listView.setItemChecked(i, false);
            }
        }
        this.adapter.setSelectedMode(mode);
        this.adapter.notifyDataSetChanged();
    }

    public List<Track> getSelectionTracks() {
        List<Track> trackList = new ArrayList();
        SparseBooleanArray selectedPositions = this.listView.getCheckedItemPositions();
        for (int i = 0; i < selectedPositions.size(); i++) {
            int position = selectedPositions.keyAt(i);
            if (selectedPositions.get(position)) {
                Cursor cursor = (Cursor) this.adapter.getItem(position);
                if (cursor != null) {
                    trackList.add(MusicStorageFacade.cursor2Track(cursor));
                }
            }
        }
        return trackList;
    }

    public void setActionModeCallback(Callback externalActionModeCallback) {
        this.externalActionModeCallback = externalActionModeCallback;
    }

    public void showSelectedMode() {
        if (getActivity() != null) {
            this.actionMode = ((BaseCompatToolbarActivity) getActivity()).getSupportToolbar().startActionMode(this);
        }
    }

    public void hideSelectedMode() {
        if (this.actionMode != null) {
            this.actionMode.finish();
        }
    }

    public void onCheckedChange(boolean value, int dataPosition) {
        if (getMode() == MusicFragmentMode.MULTI_SELECTION && this.trackSelectionControl != null) {
            Track track = MusicStorageFacade.cursor2Track((Cursor) this.adapter.getItem(dataPosition));
            if (track != null) {
                this.trackSelectionControl.setTrackSelection(track, value);
            }
        }
    }

    public void onResume() {
        super.onResume();
        GlobalBus.send(2131624108, new BusEvent());
    }

    public void onPause() {
        super.onPause();
        if (DeviceUtils.isTablet(getContext())) {
            hideSelectedMode();
        }
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return this.externalActionModeCallback != null && this.externalActionModeCallback.onCreateActionMode(mode, menu);
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        setSelectionMode(MusicFragmentMode.MULTI_SELECTION);
        if (this.externalActionModeCallback != null) {
            this.externalActionModeCallback.onPrepareActionMode(mode, menu);
        }
        return true;
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (this.externalActionModeCallback != null) {
            this.externalActionModeCallback.onActionItemClicked(mode, item);
        }
        return false;
    }

    public void onDestroyActionMode(ActionMode mode) {
        this.item = null;
        setSelectionMode(MusicFragmentMode.STANDARD);
        if (this.externalActionModeCallback != null) {
            this.externalActionModeCallback.onDestroyActionMode(mode);
        }
    }

    public boolean isDataPositionChecked(int position) {
        return this.listView.getCheckedItemPositions().get(position);
    }
}
