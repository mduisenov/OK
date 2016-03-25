package ru.ok.android.services.processors.settings;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import org.json.JSONObject;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.SystemSettingsRequest;
import ru.ok.java.api.request.SystemSettingsRequestNoSession;

@Deprecated
public final class ServicesSettingsProcessor {
    private final Context context;

    public ServicesSettingsProcessor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Subscribe(on = 2131623944, to = 2131624101)
    public void getServicesSettings(BusEvent event) {
        if (ServicesSettingsHelper.isTimeToRegularUpdate()) {
            syncSettings();
        }
    }

    public void syncSettings() {
        try {
            BaseRequest request;
            if (Settings.hasLoginData(this.context)) {
                Logger.m172d("has login data, performing settings.get request with session");
                request = new SystemSettingsRequest();
            } else {
                Logger.m172d("has NO login data, performing settings.get request without session");
                request = new SystemSettingsRequestNoSession();
            }
            JSONObject settingsJson = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request).getResultAsObject();
            Editor editor = ServicesSettingsHelper.getPreferences(OdnoklassnikiApplication.getContext()).edit();
            ServicesSettingsParser.parse(settingsJson, editor);
            editor.apply();
        } catch (Throwable e) {
            Logger.m178e(e);
        }
    }
}
