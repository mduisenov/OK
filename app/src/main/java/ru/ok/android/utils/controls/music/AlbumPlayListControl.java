package ru.ok.android.utils.controls.music;

import android.app.Activity;
import android.os.Message;
import android.widget.AdapterView;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.ui.fragments.handlers.AlbumPlayListHandler;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.AlbumInfo;
import ru.ok.model.wmf.Track;

public class AlbumPlayListControl extends MusicListControl {
    private Album album;
    private long albumId;
    private final MusicFragmentMode mode;

    public AlbumPlayListControl(Activity context, AlbumPlayListHandler playListHandler, MusicFragmentMode mode) {
        super(context, playListHandler, MusicListType.ALBUM);
        playListHandler.setNoneRefresh();
        this.mode = mode;
    }

    public void setData(Album album, Collection<? extends Track> tracks) {
        ((AlbumPlayListHandler) this.playListHandler).setData(album, tracks);
        this.album = album;
    }

    public void onSelectTrack(AdapterView<?> adapterView, int position, List<? extends Track> playList) {
        if (position == 0) {
            onSelectAlbum();
        } else {
            super.onSelectTrack(adapterView, position, playList);
        }
    }

    private void onSelectAlbum() {
        if (this.album != null) {
            NavigationHelper.showAlbumPage((Activity) this.context, this.album, this.mode);
        }
    }

    public void tryToGetAlbumInfo(long albumId) {
        this.albumId = albumId;
        showProgress();
        Message msg = Message.obtain(null, 2131624042, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = Long.valueOf(albumId);
        GlobalBus.sendMessage(msg);
    }

    public void onGetNextTrackList() {
        tryToGetAlbumInfo(this.albumId);
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 253:
                AlbumInfo info = msg.obj;
                setData(info.album, Arrays.asList(info.tracks));
                this.playListHandler.onResult();
                return false;
            case 254:
                this.playListHandler.onError(msg.obj);
                onError(msg);
                return false;
            default:
                return super.onHandleMessage(msg);
        }
    }
}
