package ru.ok.android.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import ru.ok.android.C0206R;

public class MediaButtonIntentReceiver extends BroadcastReceiver {
    private MusicManager manager;

    public MediaButtonIntentReceiver(MusicManager manager) {
        this.manager = manager;
    }

    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.MEDIA_BUTTON".equals(intent.getAction())) {
            KeyEvent event = (KeyEvent) intent.getParcelableExtra("android.intent.extra.KEY_EVENT");
            if (event != null) {
                if (event.getAction() == 1) {
                    switch (event.getKeyCode()) {
                        case C0206R.styleable.Theme_colorControlActivated /*85*/:
                            if (!this.manager.isPlaying()) {
                                this.manager.play();
                                break;
                            } else {
                                this.manager.pause();
                                break;
                            }
                        case C0206R.styleable.Theme_colorControlHighlight /*86*/:
                            this.manager.pause();
                            break;
                        case C0206R.styleable.Theme_colorButtonNormal /*87*/:
                            this.manager.next();
                            break;
                        case C0206R.styleable.Theme_colorSwitchThumbNormal /*88*/:
                            this.manager.prev();
                            break;
                    }
                }
                abortBroadcast();
            }
        }
    }
}
