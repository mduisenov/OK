package ru.ok.android.ui.fragments.handlers;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.services.transport.exception.NoConnectionException;
import ru.ok.android.ui.adapters.music.artists.ArtistsAdapter;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController.OnItemClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.ui.utils.EmptyViewRecyclerDataObserver;
import ru.ok.android.utils.HideKeyboardRecyclerScrollHelper;
import ru.ok.android.utils.controls.music.OnSelectArtistListener;
import ru.ok.model.wmf.Artist;

public class ArtistsMusicViewHandler extends RefreshViewHandler implements OnItemClickListener, OnStubButtonClickListener {
    private ArtistsAdapter adapter;
    private RecyclerView artistsListView;
    private Context context;
    private SmartEmptyViewAnimated emptyView;
    private OnGetNextArtistsListener onGetNextArtistsListener;
    private OnSelectArtistListener onSelectArtistListener;
    private LinearLayoutManager recyclerLayoutManager;

    public interface OnGetNextArtistsListener {
        void onGetNextArtistsList();
    }

    public ArtistsMusicViewHandler(Context context) {
        this.context = context;
    }

    public void setOnSelectArtistListenerListener(OnSelectArtistListener artistListener) {
        this.onSelectArtistListener = artistListener;
    }

    public void setOnGetNextArtistsListener(OnGetNextArtistsListener onGetNextArtistsListener) {
        this.onGetNextArtistsListener = onGetNextArtistsListener;
    }

    protected int getLayoutId() {
        return 2130903104;
    }

    protected void onViewCreated(LayoutInflater inflater, View view) {
        super.onViewCreated(inflater, view);
        this.artistsListView = (RecyclerView) view.findViewById(2131624622);
        this.recyclerLayoutManager = new LinearLayoutManager(this.context, 1, false);
        this.artistsListView.setLayoutManager(this.recyclerLayoutManager);
        this.artistsListView.addOnScrollListener(new HideKeyboardRecyclerScrollHelper(this.context, view));
        this.emptyView = (SmartEmptyViewAnimated) view.findViewById(C0263R.id.empty_view);
        this.emptyView.setButtonClickListener(this);
    }

    public void onDestroyView() {
    }

    public void onError(Object description) {
        SmartEmptyViewAnimated smartEmptyViewAnimated = this.emptyView;
        int i = (this.adapter == null || this.adapter.getItemCount() == 0) ? 0 : 8;
        smartEmptyViewAnimated.setVisibility(i);
        this.emptyView.setType(description instanceof NoConnectionException ? Type.NO_INTERNET : Type.ERROR);
        this.emptyView.setState(State.LOADED);
        onLoadComplete();
    }

    private void onResult() {
        SmartEmptyViewAnimated smartEmptyViewAnimated = this.emptyView;
        int i = (this.adapter == null || this.adapter.getItemCount() == 0) ? 0 : 8;
        smartEmptyViewAnimated.setVisibility(i);
        this.emptyView.setType(Type.SEARCH);
        this.emptyView.setState(State.LOADED);
        onLoadComplete();
    }

    public void onLoadComplete() {
        this.refreshProvider.refreshCompleted();
    }

    public void clearData() {
        if (this.adapter != null) {
            this.adapter.clear();
            this.adapter.notifyDataSetChanged();
        }
    }

    public void setData(Artist[] artists) {
        if (artists == null || artists.length == 0) {
            clearData();
        } else if (this.adapter == null) {
            this.adapter = initArtistsAdapter(this.context, artists);
            this.artistsListView.setAdapter(this.adapter);
        } else {
            this.adapter.setArtists(artists);
            this.adapter.notifyDataSetChanged();
        }
        onResult();
    }

    public void addData(Artist[] artists) {
        if (artists != null) {
            if (this.adapter == null) {
                this.adapter = initArtistsAdapter(this.context, artists);
                this.artistsListView.setAdapter(this.adapter);
            } else {
                this.adapter.addArtists(artists);
            }
        }
        onResult();
    }

    private ArtistsAdapter initArtistsAdapter(Context context, Artist[] artists) {
        ArtistsAdapter adapter = new ArtistsAdapter(context, artists);
        adapter.registerAdapterDataObserver(new EmptyViewRecyclerDataObserver(this.emptyView, adapter));
        adapter.getItemClickListenerController().addItemClickListener(this);
        return adapter;
    }

    public Artist[] getData() {
        if (this.adapter == null) {
            return new Artist[0];
        }
        return this.adapter.getArtists();
    }

    public void onRefresh() {
        if (this.onGetNextArtistsListener != null) {
            this.onGetNextArtistsListener.onGetNextArtistsList();
        }
    }

    public void onItemClick(View view, int position) {
        Artist selectArtist = (Artist) this.adapter.getItem(position);
        if (selectArtist != null && this.onSelectArtistListener != null) {
            this.onSelectArtistListener.onSelectArtist(selectArtist);
        }
    }

    public void onStubButtonClick(Type type) {
        this.emptyView.setState(State.LOADING);
        onRefresh();
    }
}
