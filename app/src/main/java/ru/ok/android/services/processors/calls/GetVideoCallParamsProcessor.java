package ru.ok.android.services.processors.calls;

import android.os.Message;
import android.os.Messenger;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.calls.JsonGetVideoCallParamsParser;
import ru.ok.java.api.request.calls.GetVideoChatCallParamsRequest;
import ru.ok.model.call.VideoCallInfo;

public final class GetVideoCallParamsProcessor {
    @Subscribe(on = 2131623944, to = 2131624048)
    public void getVideoCallParams(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit on get video call processor");
        getCall(msg.replyTo, msg.obj);
    }

    private void getCall(Messenger replyTo, String uid) {
        Message msg;
        try {
            VideoCallInfo sign = getVideoCall(uid);
            msg = Message.obtain(null, 177, 0, 0);
            msg.obj = sign;
        } catch (Exception e) {
            Logger.m173d("call get error %s", e);
            msg = Message.obtain(null, 178, 0, 0);
            msg.obj = e;
        }
        Messages.safeSendMessage(msg, replyTo);
    }

    private VideoCallInfo getVideoCall(String uid) throws Exception {
        return new JsonGetVideoCallParamsParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new GetVideoChatCallParamsRequest(uid))).parse();
    }
}
