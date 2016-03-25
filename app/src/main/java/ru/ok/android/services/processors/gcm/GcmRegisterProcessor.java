package ru.ok.android.services.processors.gcm;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.playservices.GCMUtils;
import ru.ok.android.playservices.PlayServicesUtils;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.request.RegisterPushNotificationRequest;

public final class GcmRegisterProcessor {
    @Subscribe(on = 2131623944, to = 2131623982)
    public void gcmRegister(BusEvent event) {
        Context context = OdnoklassnikiApplication.getContext();
        if (!hasSessionData()) {
            Logger.m184w("Has no session data");
        } else if (!PlayServicesUtils.isPlayServicesAvailable(context)) {
            Logger.m184w("Play Services not available");
        } else if (TextUtils.isEmpty(GCMUtils.getRegistrationId(context))) {
            performGCMRegistration(context);
        }
    }

    private static void performGCMRegistration(Context context) {
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            if (gcm == null) {
                Logger.m184w("Could not obtain GoogleCloudMessaging instance");
                return;
            }
            String registrationId = gcm.register("1079260813460");
            Logger.m173d("Register for push with id: '%s'", registrationId);
            sendRegistrationIdToServer(registrationId);
            if (hasSessionData()) {
                Logger.m172d("Store GCM key to settings");
                GCMUtils.storeRegistrationId(context, registrationId);
            }
            Logger.m172d("GCM successfully registered");
        } catch (Throwable ex) {
            Logger.m178e(ex);
        }
    }

    private static void sendRegistrationIdToServer(String registrationId) throws BaseApiException {
        JsonHttpResult response = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new RegisterPushNotificationRequest(registrationId));
        Logger.m173d("response: %s", response);
    }

    static boolean hasSessionData() {
        return !TextUtils.isEmpty(JsonSessionTransportProvider.getInstance().getStateHolder().getAuthenticationToken());
    }
}
