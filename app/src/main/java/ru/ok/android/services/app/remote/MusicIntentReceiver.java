package ru.ok.android.services.app.remote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import ru.ok.android.C0206R;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.utils.Logger;

public final class MusicIntentReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Logger.m173d("%s", intent);
        if (intent.getAction().equals("android.intent.action.MEDIA_BUTTON")) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get("android.intent.extra.KEY_EVENT");
            if (keyEvent.getAction() == 0 && PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(2131166226), true)) {
                switch (keyEvent.getKeyCode()) {
                    case C0206R.styleable.Theme_panelMenuListTheme /*79*/:
                    case C0206R.styleable.Theme_colorControlActivated /*85*/:
                        context.startService(MusicService.getTogglePlayIntent(context));
                    case C0206R.styleable.Theme_colorControlHighlight /*86*/:
                        context.startService(MusicService.getStopIntent(context));
                    case C0206R.styleable.Theme_colorButtonNormal /*87*/:
                        context.startService(MusicService.getNextIntent(context));
                    case C0206R.styleable.Theme_colorSwitchThumbNormal /*88*/:
                        context.startService(MusicService.getPrevIntent(context));
                    case 126:
                        context.startService(MusicService.getPlayIntent(context));
                    case 127:
                        context.startService(MusicService.getTogglePlayIntent(context));
                    default:
                }
            }
        }
    }
}
