package ru.mail.libverify.sms;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import ru.mail.libverify.api.C0181o;
import ru.mail.libverify.api.VerificationFactory;
import ru.mail.libverify.utils.C0204d;
import ru.mail.libverify.utils.C0205j;

public class SmsHandlingService extends IntentService {
    public SmsHandlingService() {
        super("SmsHandlingService");
    }

    private static String m81a(SmsMessage[] smsMessageArr) {
        StringBuilder stringBuilder = new StringBuilder(160);
        for (SmsMessage messageBody : smsMessageArr) {
            stringBuilder.append(messageBody.getMessageBody());
        }
        return stringBuilder.toString();
    }

    public void onDestroy() {
        super.onDestroy();
        C0204d.m139c("SmsHandlingService", "service destroyed");
    }

    protected void onHandleIntent(Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            C0196d.m100b("Incoming intent is null", new Object[0]);
            return;
        }
        try {
            if (intent.hasExtra("pdus")) {
                C0196d.m89a("Incoming sms dump %s", C0205j.m144a(intent.getExtras()));
                Object[] objArr = (Object[]) intent.getSerializableExtra("pdus");
                int length = objArr.length;
                SmsMessage[] smsMessageArr = new SmsMessage[length];
                for (int i = 0; i < length; i++) {
                    smsMessageArr[i] = SmsMessage.createFromPdu((byte[]) objArr[i]);
                }
                if (length <= 0) {
                    C0196d.m100b("received message is empty", new Object[0]);
                } else {
                    CharSequence displayOriginatingAddress = smsMessageArr[0].getDisplayOriginatingAddress();
                    Object a = m81a(smsMessageArr);
                    if (TextUtils.isEmpty(displayOriginatingAddress) || TextUtils.isEmpty(a)) {
                        C0196d.m100b("received ether message or phoneNumber is empty", new Object[0]);
                    } else {
                        C0196d.m89a("received message", new Object[0]);
                        C0181o smsApi = VerificationFactory.getSmsApi();
                        if (smsApi != null) {
                            smsApi.m53d(a);
                        }
                    }
                }
            } else {
                C0196d.m89a("intent don't have pdus", new Object[0]);
            }
        } catch (Throwable th) {
            C0196d.m89a("releasing wakelock", new Object[0]);
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
        C0196d.m89a("releasing wakelock", new Object[0]);
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }
}
