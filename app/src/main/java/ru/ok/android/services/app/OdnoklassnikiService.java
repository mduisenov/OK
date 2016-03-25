package ru.ok.android.services.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.services.base.ThreadedService;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.events.EventsManager;

public final class OdnoklassnikiService extends ThreadedService {
    private final Handler handler;
    private final Messenger mMessenger;

    class IncomingHandler extends Handler {
        IncomingHandler() {
        }

        public void handleMessage(Message msg) {
            Logger.m172d("url test new message: " + msg.what);
            super.handleMessage(msg);
        }
    }

    public OdnoklassnikiService() {
        this.handler = new IncomingHandler();
        this.mMessenger = new Messenger(this.handler);
    }

    public void onCreate() {
        Logger.m172d("Service create");
        super.onCreate();
    }

    public IBinder onBind(Intent intent) {
        return this.mMessenger.getBinder();
    }

    protected void onHandleIntent(Intent intent) {
        if (intent != null && !handleC2DMCommand(intent)) {
            String commandName = intent.getStringExtra("COMMAND_NAME");
            if (!TextUtils.isEmpty(commandName)) {
                Logger.m173d("Handle %s command", commandName);
                try {
                    createProcessorByCommandName(commandName).processCommand(getBaseContext(), intent);
                } catch (Exception e) {
                    Logger.m177e("Failed to execute command " + commandName, e);
                }
            }
        }
    }

    private CommandProcessor createProcessorByCommandName(String commandName) throws Exception {
        return (CommandProcessor) Class.forName(CommandProcessor.extractProcessorName(commandName)).getConstructor(new Class[]{JsonSessionTransportProvider.class}).newInstance(new Object[]{JsonSessionTransportProvider.getInstance()});
    }

    private boolean handleC2DMCommand(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return false;
        }
        if ("getStatus".equals(intent.getAction())) {
            handleGetStatus();
            return true;
        } else if (!"ru.ok.android.action.NOTIFY".equals(intent.getAction())) {
            return false;
        } else {
            onNewActionNotification(intent);
            return true;
        }
    }

    private void handleGetStatus() {
        GlobalBus.sendMessage(Message.obtain(null, 2131624070));
        Logger.m172d("get status action send message");
    }

    private void onNewActionNotification(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        if (TextUtils.isEmpty(bundle.getString("conversation_id"))) {
            String videoId = bundle.getString("video_id");
            if (!TextUtils.isEmpty(videoId)) {
                processVideoMessageNotification(Long.valueOf(videoId).longValue());
                return;
            }
            return;
        }
        EventsManager.getInstance().updateNow();
    }

    private void processVideoMessageNotification(long videoId) {
        Bundle data = new Bundle();
        data.putLong("VIDEO_ID", videoId);
        GlobalBus.send(2131624085, new BusEvent(data));
        Logger.m172d("send message process video");
    }
}
