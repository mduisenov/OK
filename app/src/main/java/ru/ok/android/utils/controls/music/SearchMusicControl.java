package ru.ok.android.utils.controls.music;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import java.util.Arrays;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.music.MusicFragmentMode;
import ru.ok.android.ui.fragments.handlers.SearchMusicViewHandler;
import ru.ok.android.utils.controls.SearchPlayListControl;
import ru.ok.model.wmf.Album;
import ru.ok.model.wmf.Artist;
import ru.ok.model.wmf.relevant.AlbumsRelevantAnswer;
import ru.ok.model.wmf.relevant.ArtistsRelevantAnswer;
import ru.ok.model.wmf.relevant.RelevantAnswer;
import ru.ok.model.wmf.relevant.RelevantType;

public class SearchMusicControl {
    private AlbumsControl albumsControl;
    private ArtistBestMatchControl artistBestMatchControl;
    private ArtistsControl artistsControl;
    private BestMatchListControl bestMatchControl;
    private Context context;
    private SearchMusicViewHandler handler;
    private Messenger mMessenger;
    private SearchPlayListControl playListControl;
    private String searchText;

    /* renamed from: ru.ok.android.utils.controls.music.SearchMusicControl.1 */
    class C14561 extends Handler {
        C14561() {
        }

        public void handleMessage(Message msg) {
            if (SearchMusicControl.this.onHandleMessage(msg)) {
                super.handleMessage(msg);
            }
        }
    }

    public SearchMusicControl(Activity context, SearchMusicViewHandler handler, MusicFragmentMode mode) {
        this.mMessenger = new Messenger(new C14561());
        this.context = context;
        this.handler = handler;
        this.playListControl = new SearchPlayListControl(context, handler.getMusicPlayListHandler());
        this.albumsControl = new AlbumsControl(context, handler.getAlbumsHandler(), true);
        this.artistsControl = new ArtistsControl(context, handler.getArtistsMusicViewHandler());
        this.bestMatchControl = new BestMatchListControl(context, handler.getAlbumPlayListHandler(), handler.getArtistPlayListHandler(), mode);
        this.artistBestMatchControl = new ArtistBestMatchControl(handler.getArtistPlayListHandler().getArtistBestMatchHandler());
        GlobalBus.register(this);
        GlobalBus.send(2131624108, new BusEvent());
    }

    public void setOnSelectArtistListener(OnSelectArtistListener onSelectArtistListener) {
        this.artistsControl.setSelectArtistListener(onSelectArtistListener);
    }

    public void setOnSelectAlbumForArtistListener(OnSelectAlbumsForArtistListener onSelectAlbumForArtistListener) {
        this.artistBestMatchControl.setOnSelectAlbumsForArtistListener(onSelectAlbumForArtistListener);
    }

    public void setOnSelectArtistSimilarMusicListener(OnSelectArtistSimilarMusicListener onSelectArtistSimilarMusicListener) {
        this.artistBestMatchControl.setOnSelectArtistSimilarMusicListener(onSelectArtistSimilarMusicListener);
    }

    public void setMusicPageSelectListener(MusicPageSelectListener listener) {
        this.handler.setMusicPageSelectListener(listener);
    }

    @Subscribe(on = 2131623946, to = 2131624252)
    public void onStreamMediaStatus(BusEvent event) {
        this.playListControl.onStreamMediaStatus(event);
        this.bestMatchControl.onStreamMediaStatus(event);
    }

    public void tryToGetSearchMusic(String text) {
        this.searchText = text;
        tryToGetRelevantMusic(text);
    }

    private void tryToGetRelevantMusic(String text) {
        this.handler.showSearchView();
        this.handler.getArtistPlayListHandler().showProgress();
        this.handler.getAlbumPlayListHandler().showProgress();
        this.handler.getMusicPlayListHandler().showProgress();
        this.playListControl.setSearchText(text);
        this.albumsControl.setSearchText(text);
        this.artistsControl.setSearchText(text);
        this.handler.getAlbumsHandler().showProgress();
        Message msg = Message.obtain(null, 2131624069, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = text;
        GlobalBus.sendMessage(msg);
    }

    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case 202:
                this.handler.showSearchView();
                RelevantAnswer answer = msg.obj;
                if (answer.getType() == RelevantType.ALBUMS_BEST_MATCH) {
                    this.handler.getArtistPlayListHandler().clearData();
                    this.handler.getPagerAdapter().setBestMatchAlbumsResult();
                    AlbumsRelevantAnswer albumsRelevantAnswer = (AlbumsRelevantAnswer) answer;
                    this.bestMatchControl.setData(((Album[]) albumsRelevantAnswer.getBestMatch())[0], Arrays.asList(albumsRelevantAnswer.getTracks()));
                    this.playListControl.tryToGetSearchMusic(this.searchText);
                    this.artistsControl.tryToGetSearchArtists(this.searchText);
                    this.handler.notifyData();
                    this.handler.setSelectionAlbumsPage();
                } else if (answer.getType() == RelevantType.ARTISTS_BEST_MATCH) {
                    this.handler.setSelectionArtistsPage();
                    this.handler.getAlbumPlayListHandler().clearData();
                    this.handler.getPagerAdapter().setBestMatchArtistResult();
                    ArtistsRelevantAnswer artistsRelevantAnswer = (ArtistsRelevantAnswer) answer;
                    this.bestMatchControl.setData(((Artist[]) artistsRelevantAnswer.getBestMatch())[0], Arrays.asList(artistsRelevantAnswer.getTracks()));
                    this.playListControl.tryToGetSearchMusic(this.searchText);
                    this.albumsControl.tryToGetSearchAlbums(this.searchText);
                } else if (answer.getType() == RelevantType.NO_BEST_MATCH) {
                    this.handler.getAlbumPlayListHandler().clearData();
                    this.handler.getArtistPlayListHandler().clearData();
                    this.handler.getPagerAdapter().setNoBestMatch();
                    this.playListControl.tryToGetSearchMusic(this.searchText);
                    this.albumsControl.tryToGetSearchAlbums(this.searchText);
                    this.artistsControl.tryToGetSearchArtists(this.searchText);
                    this.handler.setSelectionTracksPage();
                }
                return false;
            case 203:
                this.handler.getPagerAdapter().setNoBestMatch();
                this.handler.getMusicPlayListHandler().onError(msg.obj);
                this.handler.getAlbumsHandler().onError(msg.obj);
                this.handler.getArtistPlayListHandler().onError(msg.obj);
                this.handler.getArtistsMusicViewHandler().onError(msg.obj);
                this.handler.getAlbumPlayListHandler().onError(msg.obj);
                MusicControlUtils.onError(this.context, msg);
                this.handler.showSearchView();
                return false;
            default:
                return true;
        }
    }

    public MusicListControl getMusicListControl() {
        return this.playListControl;
    }

    public void cleanup() {
        GlobalBus.unregister(this);
    }
}
