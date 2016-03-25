package ru.ok.android.utils.controls;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import java.util.Arrays;
import java.util.List;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.ui.fragments.handlers.MusicPlayListHandler;
import ru.ok.android.utils.controls.music.MusicListControl;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.model.wmf.Track;

public class SearchPlayListControl extends MusicListControl {
    private String searchText;
    private long serverTripTime;

    /* renamed from: ru.ok.android.utils.controls.SearchPlayListControl.1 */
    static /* synthetic */ class C14401 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$utils$controls$music$MusicListType;

        static {
            $SwitchMap$ru$ok$android$utils$controls$music$MusicListType = new int[MusicListType.values().length];
            try {
                $SwitchMap$ru$ok$android$utils$controls$music$MusicListType[MusicListType.SEARCH_MUSIC.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public SearchPlayListControl(Activity context, MusicPlayListHandler playListHandler) {
        super(context, playListHandler, MusicListType.SEARCH_PLAYLIST);
        this.searchText = "search";
        this.serverTripTime = 0;
    }

    public boolean onHandleMessage(Message msg) {
        if (!super.onHandleMessage(msg)) {
            return false;
        }
        switch (msg.what) {
            case 173:
                List<Track> tracksSearch = Arrays.asList((Track[]) msg.obj);
                Bundle dataSearch = msg.getData();
                if (dataSearch == null || dataSearch.getInt("start_position", 0) == 0) {
                    setData(tracksSearch, dataSearch.getBoolean("all_content", false));
                } else {
                    addData(tracksSearch, dataSearch.getBoolean("all_content", false));
                }
                return false;
            case 174:
                this.playListHandler.clearData();
                this.playListHandler.onError(msg.obj);
                return false;
            default:
                return true;
        }
    }

    public void tryToGetSearchMusic(String text) {
        tryToGetSearchMusic(text, 0);
    }

    public void tryToGetSearchMusic(String text, int start) {
        Message msg = Message.obtain(null, 2131624068, 0, 0);
        msg.replyTo = this.mMessenger;
        msg.obj = text;
        Bundle data = new Bundle();
        data.putInt("start_position", start);
        msg.setData(data);
        GlobalBus.sendMessage(msg);
        setPlaylistId(text);
        this.searchText = text;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void onGetNextTrackList() {
        long t = System.currentTimeMillis();
        int size = getData().size();
        if (size == 0 || (this.currentType != null && t - this.serverTripTime > 2000)) {
            switch (C14401.$SwitchMap$ru$ok$android$utils$controls$music$MusicListType[this.currentType.ordinal()]) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    showProgress();
                    tryToGetSearchMusic(this.searchText, size);
                    break;
            }
            this.serverTripTime = t;
            return;
        }
        this.playListHandler.onRefreshComplete();
    }
}
