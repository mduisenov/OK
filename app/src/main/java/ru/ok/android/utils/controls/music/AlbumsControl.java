package ru.ok.android.utils.controls.music;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.ui.fragments.handlers.AlbumsMusicViewHandler;
import ru.ok.android.ui.fragments.handlers.AlbumsMusicViewHandler.OnGetNextAlbumsListener;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.Artist;

public class AlbumsControl implements OnGetNextAlbumsListener {
    protected AlbumsMusicViewHandler albumsMusicViewHandler;
    private Artist artist;
    protected Context context;
    private final boolean isSearch;
    private Messenger mMessenger;
    private String searchText;

    /* renamed from: ru.ok.android.utils.controls.music.AlbumsControl.1 */
    class C14511 extends Handler {
        C14511() {
        }

        public void handleMessage(Message msg) {
            if (AlbumsControl.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public AlbumsControl(Context context, AlbumsMusicViewHandler handler, boolean isSearch) {
        this.searchText = "";
        this.mMessenger = new Messenger(new C14511());
        this.context = context;
        this.albumsMusicViewHandler = handler;
        this.albumsMusicViewHandler.setOnGetNextAlbumsListener(this);
        this.isSearch = isSearch;
    }

    public void setData(Album[] albums) {
        this.albumsMusicViewHandler.setData(albums);
        this.albumsMusicViewHandler.onResult(this.isSearch);
    }

    public void addData(Album[] albums) {
        this.albumsMusicViewHandler.addData(albums);
        this.albumsMusicViewHandler.onResult(this.isSearch);
    }

    public Album[] getData() {
        return this.albumsMusicViewHandler.getData();
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 194:
                Album[] albums = (Album[]) msg.obj;
                Bundle data = msg.getData();
                if (data == null || data.getInt("start_position", 0) == 0) {
                    setData(albums);
                } else {
                    addData(albums);
                }
                return false;
            case 195:
                onError(msg);
                return false;
            case 245:
                setData((Album[]) msg.obj);
                return false;
            case 246:
                onError(msg);
                return false;
            default:
                return true;
        }
    }

    private void onError(Message msg) {
        MusicControlUtils.onError(this.context, msg);
        this.albumsMusicViewHandler.onError(msg.obj);
    }

    public void tryToGetSearchAlbums(String text, int start) {
        this.searchText = text;
        Message msg = Message.obtain(null, 2131624066, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = text;
        Bundle data = new Bundle();
        data.putInt("start_position", start);
        msg.setData(data);
        GlobalBus.sendMessage(msg);
    }

    public void tryToGetSearchAlbums(String text) {
        this.albumsMusicViewHandler.showProgress();
        tryToGetSearchAlbums(text, 0);
    }

    public void tryToGetNextSearchAlbums() {
        tryToGetSearchAlbums(this.searchText, Math.max(0, getData().length - 1));
    }

    public void tryToGetArtistAlbums(Artist artist) {
        this.artist = artist;
        this.albumsMusicViewHandler.showProgress();
        Message msg = Message.obtain(null, 2131624044, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = artist;
        GlobalBus.sendMessage(msg);
    }

    public void onGetNextAlbumsList() {
        if (getData().length != 0) {
            tryToGetNextSearchAlbums();
        } else if (this.artist != null) {
            tryToGetArtistAlbums(this.artist);
        } else {
            tryToGetSearchAlbums(this.searchText);
        }
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
