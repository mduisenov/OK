package ru.ok.android.ui.fragments.handlers;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.services.transport.exception.NoConnectionException;
import ru.ok.android.ui.adapters.music.AlbumsAdapter;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;
import ru.ok.android.utils.HideKeyboardScrollHelper;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.model.wmf.Album;

public class AlbumsMusicViewHandler extends RefreshViewHandler implements OnItemClickListener, OnStubButtonClickListener {
    private AlbumsAdapter adapter;
    private ListView albumsListView;
    private Activity context;
    private SmartEmptyViewAnimated emptyView;
    private final MusicFragmentMode mode;
    private OnGetNextAlbumsListener onGetNextAlbumsListener;

    public interface OnGetNextAlbumsListener {
        void onGetNextAlbumsList();
    }

    public AlbumsMusicViewHandler(Activity context, MusicFragmentMode mode) {
        this.context = context;
        this.mode = mode;
    }

    protected int getLayoutId() {
        return 2130903097;
    }

    protected void onViewCreated(LayoutInflater inflater, View view) {
        super.onViewCreated(inflater, view);
        this.albumsListView = (ListView) view.findViewById(2131624612);
        this.albumsListView.setAdapter(new AlbumsAdapter(this.context, new Album[0]));
        this.albumsListView.setOnItemClickListener(this);
        this.albumsListView.setOnScrollListener(new HideKeyboardScrollHelper(this.context, view));
        this.emptyView = (SmartEmptyViewAnimated) view.findViewById(C0263R.id.empty_view);
        this.emptyView.setButtonClickListener(this);
        this.albumsListView.setEmptyView(this.emptyView);
    }

    public void onDestroyView() {
    }

    public void showProgress() {
        this.emptyView.setState(State.LOADING);
        this.emptyView.setVisibility(0);
    }

    public void onResult(boolean isSearch) {
        SmartEmptyViewAnimated smartEmptyViewAnimated = this.emptyView;
        int i = (this.adapter == null || this.adapter.getCount() == 0) ? 0 : 8;
        smartEmptyViewAnimated.setVisibility(i);
        this.emptyView.setType(isSearch ? Type.SEARCH : Type.MUSIC);
        this.emptyView.setState(State.LOADED);
        onLoadComplete();
    }

    public void onError(Object description) {
        SmartEmptyViewAnimated smartEmptyViewAnimated = this.emptyView;
        int i = (this.adapter == null || this.adapter.getCount() == 0) ? 0 : 8;
        smartEmptyViewAnimated.setVisibility(i);
        this.emptyView.setType(description instanceof NoConnectionException ? Type.NO_INTERNET : Type.ERROR);
        this.emptyView.setState(State.LOADED);
        onLoadComplete();
    }

    public void onLoadComplete() {
        this.refreshProvider.refreshCompleted();
    }

    public void setOnGetNextAlbumsListener(OnGetNextAlbumsListener onGetNextAlbumsListener) {
        this.onGetNextAlbumsListener = onGetNextAlbumsListener;
    }

    public void clearData() {
        if (this.adapter != null) {
            this.adapter.clear();
        }
    }

    public void setData(Album[] albums) {
        if (albums == null || albums.length == 0) {
            clearData();
        } else if (this.adapter == null) {
            this.adapter = new AlbumsAdapter(this.context, albums);
            this.albumsListView.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();
        } else {
            this.adapter.setAlbums(albums);
            this.adapter.notifyDataSetChanged();
        }
    }

    public void addData(Album[] albums) {
        if (albums == null) {
            return;
        }
        if (this.adapter == null) {
            this.adapter = new AlbumsAdapter(this.context, albums);
            this.albumsListView.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();
            return;
        }
        this.adapter.addAlbums(albums);
        this.adapter.notifyDataSetChanged();
    }

    public Album[] getData() {
        if (this.adapter == null) {
            return new Album[0];
        }
        return this.adapter.getAlbums();
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Album selectAlbum = (Album) adapterView.getItemAtPosition(position);
        if (selectAlbum != null) {
            NavigationHelper.showAlbumPage(this.context, selectAlbum, this.mode);
        }
    }

    public void onRefresh() {
        if (this.onGetNextAlbumsListener != null) {
            this.onGetNextAlbumsListener.onGetNextAlbumsList();
        }
    }

    public void onStubButtonClick(Type type) {
        this.emptyView.setState(State.LOADING);
        onRefresh();
    }
}
