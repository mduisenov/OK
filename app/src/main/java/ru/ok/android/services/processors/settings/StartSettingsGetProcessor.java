package ru.ok.android.services.processors.settings;

import android.content.Context;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.TamTamSettingsProcessor;
import ru.ok.android.services.processors.base.CommandProcessor;
import ru.ok.android.services.processors.messaging.MessagesSettingsHandler;
import ru.ok.android.services.processors.stickers.StickersSettingsHandler;
import ru.ok.android.services.processors.update.CheckUpdateProcessor;
import ru.ok.android.services.processors.video.check.VideoSettingsGetProcessor;
import ru.ok.android.services.processors.xmpp.XmppSettingsHandler;
import ru.ok.android.ui.dialogs.rate.RateDialogSettingsHandler;
import ru.ok.android.ui.nativeRegistration.HelpSettingsHandler;
import ru.ok.android.ui.stream.data.StreamPmsSettingsHandler;
import ru.ok.android.utils.Logger;

public final class StartSettingsGetProcessor {
    private static final List<SettingHandler> settingsKeys;
    private final Context context;

    public interface SettingHandler {
        String getSettingsKey();

        void handleResult(JSONObject jSONObject);

        boolean isSettingsTimeRequestValid();
    }

    static {
        settingsKeys = new ArrayList();
    }

    public StartSettingsGetProcessor(Context context) {
        this.context = context.getApplicationContext();
        settingsKeys.add(new VideoSettingsGetProcessor(context));
        settingsKeys.add(new CheckUpdateProcessor(context));
        settingsKeys.add(new TamTamSettingsProcessor(context));
        settingsKeys.add(new RateDialogSettingsHandler(context));
        settingsKeys.add(new StreamPmsSettingsHandler(context));
        settingsKeys.add(new HelpSettingsHandler(context));
        settingsKeys.add(new EmptyStreamSettingsGetProcessor(context));
        settingsKeys.add(new XmppSettingsHandler(context));
        settingsKeys.add(new StickersSettingsHandler(context));
        settingsKeys.add(new PhotoRollSettingsHandler(context));
        settingsKeys.add(new GrayLogSettingsHandler(context));
        settingsKeys.add(new MessagesSettingsHandler(context));
    }

    @Subscribe(on = 2131623944, to = 2131624102)
    public void getStartSettings(BusEvent event) {
        int resultCode;
        Logger.m172d("");
        Bundle outBundle = new Bundle();
        try {
            List<SettingHandler> requestSettings = getCurrentTimeSettings(settingsKeys);
            List<String> names = new ArrayList();
            for (SettingHandler setting : requestSettings) {
                names.add(setting.getSettingsKey());
            }
            if (names.size() > 0) {
                JSONObject json = SettingsGetProcessor.performSettingGetJsonRequest(this.context, (String[]) names.toArray(new String[names.size()]));
                for (SettingHandler settings : requestSettings) {
                    settings.handleResult(json);
                }
            }
            resultCode = -1;
            Logger.m172d("get start settings ok");
        } catch (Throwable e) {
            Logger.m186w(e, "Failed to get start settings: " + e);
            CommandProcessor.fillErrorBundle(outBundle, e);
            resultCode = -2;
        }
        GlobalBus.send(2131624244, new BusEvent(event.bundleInput, outBundle, resultCode));
    }

    private static List<SettingHandler> getCurrentTimeSettings(List<SettingHandler> settings) {
        ArrayList<SettingHandler> useSettingsList = new ArrayList();
        for (SettingHandler setting : settings) {
            if (setting.isSettingsTimeRequestValid()) {
                useSettingsList.add(setting);
            }
        }
        return useSettingsList;
    }
}
