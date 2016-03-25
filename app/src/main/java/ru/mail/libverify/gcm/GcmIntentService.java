package ru.mail.libverify.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import ru.mail.libverify.api.VerificationFactory;
import ru.mail.libverify.utils.C0204d;

public class GcmIntentService extends IntentService {
    public GcmIntentService() {
        super("GcmIntentService");
        setIntentRedelivery(true);
    }

    public void onDestroy() {
        super.onDestroy();
        C0204d.m139c("GcmIntentService", "service destroyed");
    }

    protected void onHandleIntent(Intent intent) {
        GcmRegisterService.m63a("GcmIntentService.onHandleIntent start", new Object[0]);
        if (intent == null) {
            GcmRegisterService.m63a("GcmIntentService.onHandleIntent: intent is null", new Object[0]);
            return;
        }
        try {
            Bundle extras = intent.getExtras();
            String messageType = GoogleCloudMessaging.getInstance(this).getMessageType(intent);
            if (extras == null || extras.isEmpty()) {
                GcmRegisterService.m63a("GcmIntentService.onHandleIntent: extras is empty", new Object[0]);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                GcmRegisterService.m63a("Send error: {0}", extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                GcmRegisterService.m63a("Deleted messages on server: %s", extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                CharSequence string = extras.getString("from");
                Object string2 = extras.getString("data");
                if (!TextUtils.isEmpty(string) && !TextUtils.equals(string, "297109036349")) {
                    GcmRegisterService.m63a("GcmIntentService.onHandleIntent: extras sender id %s is not equal to required sender id %s", string, "297109036349");
                    WakefulBroadcastReceiver.completeWakefulIntent(intent);
                    GcmRegisterService.m63a("GcmIntentService.onHandleIntent complete", new Object[0]);
                    return;
                } else if (TextUtils.isEmpty(string2)) {
                    GcmRegisterService.m63a("GcmIntentService.onHandleIntent: can't handle empty message", new Object[0]);
                    WakefulBroadcastReceiver.completeWakefulIntent(intent);
                    GcmRegisterService.m63a("GcmIntentService.onHandleIntent complete", new Object[0]);
                    return;
                } else {
                    VerificationFactory.getGcmApi(this).m44a(string2);
                }
            }
        } catch (Exception e) {
            GcmRegisterService.m63a("handleMessage error %s", e.toString());
        } catch (Throwable th) {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
            GcmRegisterService.m63a("GcmIntentService.onHandleIntent complete", new Object[0]);
        }
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        GcmRegisterService.m63a("GcmIntentService.onHandleIntent complete", new Object[0]);
    }
}
