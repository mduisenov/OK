package ru.ok.android.ui.fragments.handlers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.fragments.music.TrackSelectionControl;
import ru.ok.android.fragments.music.TrackSelectionControl.TrackSelectionListener;
import ru.ok.android.services.transport.exception.NoConnectionException;
import ru.ok.android.ui.adapters.CheckChangeAdapter.OnCheckStateChangeListener;
import ru.ok.android.ui.adapters.music.DotsCursorAdapter.OnDotsClickListener;
import ru.ok.android.ui.adapters.music.playlist.PlayListAdapter;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.utils.HideKeyboardScrollHelper;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.model.wmf.Track;

public abstract class BaseMusicPlayListHandler extends RefreshViewHandler implements OnItemClickListener, TrackSelectionListener, OnDotsClickListener<Track>, OnStubButtonClickListener {
    protected PlayListAdapter adapter;
    private final Context context;
    private SmartEmptyViewAnimated emptyView;
    private OnGetNextTracksListener listenerNextTracks;
    private OnItemLongClickListener longClickListener;
    protected MusicFragmentMode mMode;
    private List<OnDotsClickListener<Track>> onDotsClickTrackListeners;
    private List<OnSelectTrackListener> onSelectTrackListeners;
    protected ListView playListView;
    protected TrackSelectionControl trackSelectionControl;

    public interface OnSelectTrackListener {
        void onSelectTrack(AdapterView<?> adapterView, int i, List<? extends Track> list);
    }

    public interface OnGetNextTracksListener {
        void onGetNextTrackList();
    }

    protected abstract ListAdapter createWrapperAdapter(PlayListAdapter playListAdapter);

    protected BaseMusicPlayListHandler(MusicFragmentMode mode, Context context) {
        this.onSelectTrackListeners = new ArrayList();
        this.onDotsClickTrackListeners = new ArrayList();
        this.context = context;
        this.mMode = mode;
        if (mode == MusicFragmentMode.MULTI_SELECTION && (context instanceof TrackSelectionControl)) {
            this.trackSelectionControl = (TrackSelectionControl) context;
        } else {
            this.trackSelectionControl = null;
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (this.playListView != null) {
            this.playListView.setOnItemLongClickListener(listener);
        } else {
            this.longClickListener = listener;
        }
    }

    protected int getLayoutId() {
        return 2130903393;
    }

    protected void onViewCreated(LayoutInflater inflater, View view) {
        super.onViewCreated(inflater, view);
        this.playListView = preparePlayList(view.getContext(), view);
        this.playListView.setEmptyView(this.emptyView);
        this.playListView.setChoiceMode(2);
        PlayListAdapter createDataAdapter = createDataAdapter(this.mMode);
        this.adapter = createDataAdapter;
        postListViewInited(createWrapperAdapter(createDataAdapter), inflater);
        this.emptyView = (SmartEmptyViewAnimated) view.findViewById(C0263R.id.empty_view);
        this.emptyView.setButtonClickListener(this);
        startListenTrackSelection();
    }

    public ListView preparePlayList(Context context, View mMainView) {
        ListView playListView = (ListView) mMainView.findViewById(2131625228);
        playListView.setOnItemClickListener(this);
        playListView.setOnScrollListener(new HideKeyboardScrollHelper(context, mMainView));
        playListView.setFooterDividersEnabled(true);
        if (this.longClickListener != null) {
            playListView.setOnItemLongClickListener(this.longClickListener);
        }
        return playListView;
    }

    public void onDestroyView() {
        stopListenTrackSelection();
    }

    protected void stopListenTrackSelection() {
        if (this.mMode == MusicFragmentMode.MULTI_SELECTION && this.trackSelectionControl != null) {
            Logger.m173d("[%s] stop listening track selection", getClass().getSimpleName());
            this.trackSelectionControl.removeTrackSelectionListener(this);
        }
    }

    protected void startListenTrackSelection() {
        Logger.m172d("");
        if (this.mMode == MusicFragmentMode.MULTI_SELECTION && this.trackSelectionControl != null) {
            Logger.m173d("[%s] start listening track selection", getClass().getSimpleName());
            this.trackSelectionControl.addTrackSelectionListener(this);
        }
    }

    public void onTrackSelectionChanged(Track track, boolean isSelected) {
        Logger.m173d("[%s] %s, isSelectionMode=%s", getClass().getSimpleName(), track, Boolean.valueOf(isSelected));
        this.adapter.notifyDataSetChanged();
    }

    private PlayListAdapter createDataAdapter(MusicFragmentMode mode) {
        PlayListAdapter adapter = new PlayListAdapter(this.context);
        adapter.setMode(mode, mode == MusicFragmentMode.MULTI_SELECTION ? getTrackSelectionControl() : null);
        adapter.setOnDotsClickListener(this);
        return adapter;
    }

    public void clearData() {
        if (this.adapter != null) {
            this.adapter.clearData();
        }
    }

    public void setOnCheckStateChangeListener(OnCheckStateChangeListener listener) {
        if (this.adapter != null) {
            this.adapter.setCheckStateChangeListener(listener);
        }
    }

    public void setOnGetNextTracksListener(OnGetNextTracksListener listener) {
        this.listenerNextTracks = listener;
    }

    public void onLoadComplete() {
        this.refreshProvider.refreshCompleted();
    }

    public Collection<? extends Track> getData() {
        return this.adapter.getData();
    }

    public Track[] getSelectedData() {
        if (this.mMode == MusicFragmentMode.MULTI_SELECTION) {
            TrackSelectionControl trackSelectionControl = getTrackSelectionControl();
            if (trackSelectionControl != null) {
                return trackSelectionControl.getSelectedTracks();
            }
        }
        return new Track[0];
    }

    public boolean switchToSelectionMode(TrackSelectionControl trackSelectionControl) {
        this.mMode = MusicFragmentMode.MULTI_SELECTION;
        this.trackSelectionControl = trackSelectionControl;
        if (this.adapter == null || this.adapter.getCount() <= 0) {
            return false;
        }
        this.adapter.setMode(MusicFragmentMode.MULTI_SELECTION, trackSelectionControl);
        return true;
    }

    public void switchToStandardMode() {
        this.mMode = MusicFragmentMode.STANDARD;
        this.trackSelectionControl = null;
        if (this.adapter != null) {
            this.adapter.setMode(MusicFragmentMode.STANDARD, null);
            this.adapter.clear();
        }
    }

    public void onRefreshComplete() {
        this.refreshProvider.refreshCompleted();
    }

    public boolean deleteTrack(Track track) {
        return this.adapter.removeItem(track);
    }

    public void addSelectTrackListener(OnSelectTrackListener listener) {
        this.onSelectTrackListeners.add(listener);
    }

    public void addOnDotsClickTrackListener(OnDotsClickListener<Track> listener) {
        this.onDotsClickTrackListeners.add(listener);
    }

    public void scrollToPosition(int position) {
        Utils.scrollToPosition(this.playListView, position);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        int headers = ((ListView) adapterView).getHeaderViewsCount();
        if (position >= headers && this.adapter.getItem(position - headers) != this.adapter.getPlayingTrack()) {
            notifySelectTrack(position);
        }
    }

    public void onDotsClick(Track o, View view) {
        for (OnDotsClickListener<Track> listener : this.onDotsClickTrackListeners) {
            listener.onDotsClick(o, view);
        }
    }

    protected void notifySelectTrack(int position) {
        if (this.mMode != MusicFragmentMode.MULTI_SELECTION) {
            for (OnSelectTrackListener listener : this.onSelectTrackListeners) {
                listener.onSelectTrack(this.playListView, transformViewPositionToDataPosition(position), this.adapter.getData());
            }
            return;
        }
        this.adapter.notifyDataSetChanged();
    }

    public void showProgress() {
        this.emptyView.setState(State.LOADING);
        this.emptyView.setVisibility(0);
    }

    public void onResult() {
        this.emptyView.setVisibility(getData().isEmpty() ? 0 : 8);
        this.emptyView.setType(Type.SEARCH);
        this.emptyView.setState(State.LOADED);
        onLoadComplete();
    }

    public void onError(Object description) {
        this.emptyView.setVisibility(getData().isEmpty() ? 0 : 8);
        this.emptyView.setType(description instanceof NoConnectionException ? Type.NO_INTERNET : Type.ERROR);
        this.emptyView.setState(State.LOADED);
        onLoadComplete();
    }

    public void onStubButtonClick(Type type) {
        onRefresh();
    }

    public void showPlay(Track track) {
        this.adapter.showPlayingTrack(track);
    }

    public int getPlayPosition() {
        return this.adapter.getPlayingPosition();
    }

    public void hidePlay() {
        this.adapter.hidePlayPosition();
    }

    public void setNoneRefresh() {
        this.refreshProvider.setRefreshEnabled(false);
    }

    public void onRefresh() {
        if (this.listenerNextTracks != null) {
            this.listenerNextTracks.onGetNextTrackList();
        }
    }

    public void setData(Collection<? extends Track> tracks) {
        if (tracks == null || tracks.isEmpty()) {
            clearData();
        } else {
            this.adapter.setData(tracks);
        }
    }

    public void addData(Collection<? extends Track> tracks) {
        if (tracks != null) {
            this.adapter.addTracks(tracks);
        } else {
            setData(tracks);
        }
    }

    protected TrackSelectionControl getTrackSelectionControl() {
        return this.trackSelectionControl;
    }

    protected int transformViewPositionToDataPosition(int position) {
        return position;
    }

    protected void postListViewInited(ListAdapter adapter, LayoutInflater inflater) {
        this.playListView.setAdapter(adapter);
    }
}
