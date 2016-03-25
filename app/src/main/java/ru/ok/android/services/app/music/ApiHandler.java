package ru.ok.android.services.app.music;

import android.os.Handler;
import android.os.Message;
import ru.ok.android.music.PlayTrackInfoBigImage;
import ru.ok.model.wmf.PlayTrackInfo;

public final class ApiHandler extends Handler {
    private GetPlayTrackInfoListener listener;

    public interface GetPlayTrackInfoListener {
        void onGetPlayInfo(PlayTrackInfo playTrackInfo);

        void onGetPlayInfoError(Object obj);
    }

    public ApiHandler(GetPlayTrackInfoListener listener) {
        this.listener = listener;
    }

    public void handleMessage(Message msg) {
        if (this.listener != null) {
            switch (msg.what) {
                case 137:
                    this.listener.onGetPlayInfo(PlayTrackInfoBigImage.create((PlayTrackInfo) msg.obj));
                case 138:
                    this.listener.onGetPlayInfoError(msg.obj);
                default:
            }
        }
    }
}
