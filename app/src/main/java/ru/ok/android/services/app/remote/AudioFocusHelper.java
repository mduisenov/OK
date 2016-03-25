package ru.ok.android.services.app.remote;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import ru.ok.android.proto.MessagesProto.Message;

public class AudioFocusHelper implements OnAudioFocusChangeListener {
    AudioManager mAM;
    MusicFocusable mFocusable;

    public AudioFocusHelper(Context ctx, MusicFocusable focusable) {
        this.mAM = (AudioManager) ctx.getSystemService("audio");
        this.mFocusable = focusable;
    }

    public boolean requestFocus() {
        return 1 == this.mAM.requestAudioFocus(this, 3, 1);
    }

    public void onAudioFocusChange(int focusChange) {
        if (this.mFocusable != null) {
            switch (focusChange) {
                case -3:
                    this.mFocusable.onLostAudioFocus(true);
                case PagerAdapter.POSITION_NONE /*-2*/:
                case RecyclerView.NO_POSITION /*-1*/:
                    this.mFocusable.onLostAudioFocus(false);
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    this.mFocusable.onGainedAudioFocus();
                default:
            }
        }
    }
}
