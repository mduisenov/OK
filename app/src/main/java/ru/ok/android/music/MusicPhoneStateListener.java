package ru.ok.android.music;

import android.telephony.PhoneStateListener;
import ru.ok.android.proto.MessagesProto.Message;

public class MusicPhoneStateListener extends PhoneStateListener {
    private boolean isStoping;
    private MusicManager manager;

    public MusicPhoneStateListener(MusicManager manager) {
        this.isStoping = false;
        this.manager = manager;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case RECEIVED_VALUE:
                if (this.isStoping) {
                    this.manager.play();
                    this.isStoping = false;
                }
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (this.manager.isPlaying()) {
                    this.isStoping = true;
                    this.manager.pause();
                }
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (this.manager.isPlaying()) {
                    this.isStoping = true;
                    this.manager.pause();
                }
            default:
        }
    }
}
