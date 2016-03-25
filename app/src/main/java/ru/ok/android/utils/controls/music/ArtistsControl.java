package ru.ok.android.utils.controls.music;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.ui.fragments.handlers.ArtistsMusicViewHandler;
import ru.ok.android.ui.fragments.handlers.ArtistsMusicViewHandler.OnGetNextArtistsListener;
import ru.ok.model.wmf.Artist;

public class ArtistsControl implements OnGetNextArtistsListener {
    protected ArtistsMusicViewHandler artistsMusicViewHandler;
    protected Context context;
    private Messenger mMessenger;
    private String searchText;

    /* renamed from: ru.ok.android.utils.controls.music.ArtistsControl.1 */
    class C14521 extends Handler {
        C14521() {
        }

        public void handleMessage(Message msg) {
            if (ArtistsControl.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public ArtistsControl(Context context, ArtistsMusicViewHandler artistsMusicViewHandler) {
        this.searchText = "";
        this.mMessenger = new Messenger(new C14521());
        this.context = context;
        this.artistsMusicViewHandler = artistsMusicViewHandler;
        this.artistsMusicViewHandler.setOnGetNextArtistsListener(this);
    }

    public void setSelectArtistListener(OnSelectArtistListener onSelectArtistListener) {
        this.artistsMusicViewHandler.setOnSelectArtistListenerListener(onSelectArtistListener);
    }

    public void setData(Artist[] artists) {
        this.artistsMusicViewHandler.setData(artists);
    }

    public void addData(Artist[] artists) {
        this.artistsMusicViewHandler.addData(artists);
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 198:
                Artist[] artists = (Artist[]) msg.obj;
                Bundle data = msg.getData();
                if (data == null || data.getInt("start_position", 0) == 0) {
                    setData(artists);
                } else {
                    addData(artists);
                }
                return false;
            case 199:
                this.artistsMusicViewHandler.onError(msg.obj);
                return false;
            default:
                return true;
        }
    }

    public void tryToGetSearchArtists(String text, int start) {
        Message msg = Message.obtain(null, 2131624067, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = text;
        Bundle data = new Bundle();
        data.putInt("start_position", start);
        msg.setData(data);
        GlobalBus.sendMessage(msg);
        this.searchText = text;
    }

    public void tryToGetSearchArtists(String text) {
        tryToGetSearchArtists(text, 0);
    }

    public void tryToGetNextSearchArtists() {
        tryToGetSearchArtists(this.searchText, Math.max(0, this.artistsMusicViewHandler.getData().length - 1));
    }

    public void onGetNextArtistsList() {
        tryToGetNextSearchArtists();
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
