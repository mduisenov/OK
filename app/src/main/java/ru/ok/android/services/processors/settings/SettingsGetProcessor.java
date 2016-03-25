package ru.ok.android.services.processors.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import java.util.Arrays;
import org.json.JSONObject;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.SettingsGetRequest;
import ru.ok.java.api.request.SettingsGetRequestNoSession;

public final class SettingsGetProcessor {
    @NonNull
    public static JSONObject performSettingGetJsonRequest(Context context, String[] settingNames) throws BaseApiException {
        BaseRequest request;
        String str = ">>> settingsNames=%s";
        Object[] objArr = new Object[1];
        objArr[0] = Logger.isLoggingEnable() ? Arrays.toString(settingNames) : null;
        Logger.m173d(str, objArr);
        if (Settings.hasLoginData(context)) {
            Logger.m172d("has login data, performing settings.get request with session");
            request = new SettingsGetRequest(settingNames, 182);
        } else {
            Logger.m172d("has NO login data, performing settings.get request without session");
            request = new SettingsGetRequestNoSession(settingNames, 182);
        }
        try {
            return JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request).getResultAsObject();
        } catch (Throwable e) {
            Logger.m186w(e, "<<< Not a valid json object in response: " + e);
            throw new ResultParsingException(e);
        }
    }
}
