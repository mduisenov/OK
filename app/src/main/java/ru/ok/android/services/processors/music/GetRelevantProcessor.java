package ru.ok.android.services.processors.music;

import android.os.Message;
import android.os.Messenger;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.wmf.http.HttpGetRelevantRequest;
import ru.ok.java.api.wmf.json.JsonGetRelevantParser;
import ru.ok.model.wmf.relevant.RelevantAnswer;

public final class GetRelevantProcessor {
    @Subscribe(on = 2131623944, to = 2131624069)
    public void getRelevant(BusEvent event) {
        Message msg = GlobalBus.eventToMessage(event);
        Logger.m172d("visit get relevant processor");
        getRelevant(msg.replyTo, msg.obj);
    }

    private void getRelevant(Messenger replayTo, String text) {
        try {
            RelevantAnswer relevantAnswer = getRelevantValue(text);
            Message mes = Message.obtain(null, 202, 0, 0);
            mes.obj = relevantAnswer;
            Messages.safeSendMessage(mes, replayTo);
            Logger.m172d("Get relevant");
        } catch (Exception e) {
            Logger.m172d("Error get relevant " + e.getMessage());
            Message msg = Message.obtain(null, 203, 0, 0);
            msg.obj = e;
            Messages.safeSendMessage(msg, replayTo);
        }
    }

    private RelevantAnswer getRelevantValue(String text) throws Exception {
        return new JsonGetRelevantParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetRelevantRequest(text, ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }
}
