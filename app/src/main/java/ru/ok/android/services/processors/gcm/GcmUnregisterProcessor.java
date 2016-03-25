package ru.ok.android.services.processors.gcm;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import ru.ok.android.playservices.GCMUtils;
import ru.ok.android.playservices.PlayServicesUtils;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.request.UnRegisterPushNotificationsRequest;

public class GcmUnregisterProcessor {
    public static void performUnregistering(Context context) {
        String gcmRegistrationId = GCMUtils.getRegistrationId(context);
        if (!TextUtils.isEmpty(gcmRegistrationId)) {
            GCMUtils.clearRegistrationId(context);
            if (PlayServicesUtils.isPlayServicesAvailable(context)) {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                if (gcm == null) {
                    Logger.m184w("Could not obtain GoogleCloudMessaging instance");
                } else {
                    try {
                        gcm.unregister();
                    } catch (Throwable e) {
                        Logger.m179e(e, "Failed to unregister from GCM");
                    }
                }
            }
            try {
                sendUnregisterRequest(gcmRegistrationId);
            } catch (Throwable e2) {
                Logger.m179e(e2, "Failed to unregister GCM on our server");
            }
        }
    }

    private static void sendUnregisterRequest(String registrationId) throws BaseApiException {
        JsonHttpResult response = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new UnRegisterPushNotificationsRequest(registrationId));
        Logger.m173d("response: %s", response);
    }
}
