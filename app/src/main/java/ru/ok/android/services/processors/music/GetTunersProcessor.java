package ru.ok.android.services.processors.music;

import android.os.Message;
import android.os.Messenger;
import ru.ok.android.C0206R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.access.music.MusicStorageFacade;
import ru.ok.android.services.app.Messages;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.wmf.http.HttpGetMyTunersRequest;
import ru.ok.java.api.wmf.json.JsonTunersParser;
import ru.ok.model.wmf.Tuner;

public final class GetTunersProcessor {
    @Subscribe(on = 2131623944, to = 2131624072)
    public void getTuners(BusEvent event) {
        Messenger replyTo = GlobalBus.eventToMessage(event).replyTo;
        try {
            Tuner[] tuners = getTuners();
            Logger.m173d("Error get tuners %d", Integer.valueOf(tuners.length));
            updateDB(tuners);
            Message mes = Message.obtain(null, C0206R.styleable.Theme_spinnerStyle, 0, 0);
            mes.obj = tuners;
            Messages.safeSendMessage(mes, replyTo);
        } catch (Exception e) {
            Logger.m172d("Error get tuners " + e.getMessage());
            Message msgError = Message.obtain(null, C0206R.styleable.Theme_ratingBarStyle, 0, 0);
            msgError.obj = e;
            Messages.safeSendMessage(msgError, replyTo);
        }
    }

    private Tuner[] getTuners() throws Exception {
        return new JsonTunersParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new HttpGetMyTunersRequest(Settings.getCurrentLocale(OdnoklassnikiApplication.getContext()), ConfigurationPreferences.getInstance().getWmfServer()))).parse();
    }

    private void updateDB(Tuner[] tuners) {
        MusicStorageFacade.insertTuners(OdnoklassnikiApplication.getContext(), tuners);
    }
}
