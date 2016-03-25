package ru.ok.android.ui.tabbar.actions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.app.MusicService;
import ru.ok.android.services.app.MusicService.MusicState;
import ru.ok.android.ui.tabbar.actions.ResetNotificationsAction.OnActionListener;

public final class MusicPageAction extends ResetNotificationsAction {
    private Context context;
    private DataUpdateReceiver dataUpdateReceiver;
    private MusicState state;

    public class DataUpdateReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                Object obj = -1;
                switch (action.hashCode()) {
                    case -1078324299:
                        if (action.equals("ru.odnoklassniki.android.music.play.state")) {
                            obj = 6;
                            break;
                        }
                        break;
                    case -671440279:
                        if (action.equals("pause_play_track")) {
                            obj = 4;
                            break;
                        }
                        break;
                    case -511711316:
                        if (action.equals("finish_play_track")) {
                            obj = 3;
                            break;
                        }
                        break;
                    case -427044444:
                        if (action.equals("repeat_play_track")) {
                            obj = 1;
                            break;
                        }
                        break;
                    case 126552189:
                        if (action.equals("start_play_track")) {
                            obj = null;
                            break;
                        }
                        break;
                    case 546691175:
                        if (action.equals("refresh_player_progress")) {
                            obj = 5;
                            break;
                        }
                        break;
                    case 1926801536:
                        if (action.equals("play_track")) {
                            obj = 2;
                            break;
                        }
                        break;
                }
                switch (obj) {
                    case RECEIVED_VALUE:
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        MusicPageAction.this.showBubble();
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                    case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                        MusicPageAction.this.hideBubble();
                    case Message.UUID_FIELD_NUMBER /*5*/:
                        if (!MusicPageAction.this.isShowBubble()) {
                            MusicPageAction.this.showBubble();
                        }
                    case Message.REPLYTO_FIELD_NUMBER /*6*/:
                        setState(intent);
                    default:
                }
            }
        }

        private void setState(Intent intent) {
            MusicPageAction.this.state = (MusicState) intent.getSerializableExtra("playState");
            if (MusicPageAction.this.state == MusicState.PLAYING) {
                MusicPageAction.this.showBubble();
            } else {
                MusicPageAction.this.hideBubble();
            }
        }
    }

    public MusicPageAction(Context context, OnActionListener listener) {
        super(listener);
        this.state = MusicState.UNKNOWN;
        this.context = context;
    }

    public int getDrawable() {
        return 2130838269;
    }

    public int getTextRes() {
        return 2131166223;
    }

    public void registerReceiver() {
        if (this.dataUpdateReceiver == null) {
            this.dataUpdateReceiver = new DataUpdateReceiver();
        }
        registerActionsReceiver("refresh_player_progress", "start_play_track", "finish_play_track", "repeat_play_track", "play_track", "pause_play_track", "ru.odnoklassniki.android.music.play.state");
        updateCurrentState();
    }

    private void registerActionsReceiver(String... actions) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this.context);
        for (String action : actions) {
            broadcastManager.registerReceiver(this.dataUpdateReceiver, new IntentFilter(action));
        }
    }

    private void updateCurrentState() {
        this.context.startService(MusicService.getStateIntent(this.context));
    }

    public void unRegisterReceiver() {
        if (this.dataUpdateReceiver != null) {
            LocalBroadcastManager.getInstance(this.context).unregisterReceiver(this.dataUpdateReceiver);
        }
    }

    public void showBubble() {
        this.bubble.setImage(2130838252);
        this.bubble.setVisibility(0);
    }

    public void hideBubble() {
        this.bubble.hideImage();
        this.bubble.setVisibility(8);
    }

    private boolean isShowBubble() {
        return this.bubble.isShown();
    }
}
