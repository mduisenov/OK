package ru.ok.android.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.videochat.VideochatController;
import ru.ok.android.videochat.VideochatController.VideoChatStateListener;

public class VideoChatBroadcastReceiver extends BroadcastReceiver {
    private MusicManager manager;
    final VideoChatStateListener myVideoChatListener;

    /* renamed from: ru.ok.android.music.VideoChatBroadcastReceiver.1 */
    class C03801 implements VideoChatStateListener {
        private boolean isStoping;

        C03801() {
            this.isStoping = false;
        }

        public void onCallStateChanged(int state) {
            switch (state) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    if (VideoChatBroadcastReceiver.this.manager.isPlaying()) {
                        this.isStoping = true;
                        VideoChatBroadcastReceiver.this.manager.pause();
                    }
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    if (VideoChatBroadcastReceiver.this.manager.isPlaying()) {
                        this.isStoping = true;
                        VideoChatBroadcastReceiver.this.manager.pause();
                    }
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    if (this.isStoping) {
                        VideoChatBroadcastReceiver.this.manager.play();
                        this.isStoping = false;
                    }
                default:
            }
        }
    }

    public VideoChatBroadcastReceiver(MusicManager manager) {
        this.myVideoChatListener = new C03801();
        this.manager = manager;
        VideochatController.instance().listen(this.myVideoChatListener);
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("ru.odnoklassniki.android.videochat.END_CALL")) {
            this.myVideoChatListener.onCallStateChanged(4);
        } else if (intent.getAction().equals("ru.odnoklassniki.android.videochat.STOP_CALL")) {
            this.myVideoChatListener.onCallStateChanged(4);
        }
    }
}
