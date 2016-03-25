package ru.mail.libverify.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import java.util.Locale;
import ru.mail.libverify.api.VerificationFactory;
import ru.mail.libverify.utils.C0204d;
import ru.ok.android.proto.MessagesProto.Message;

public class NotificationService extends IntentService {
    public NotificationService() {
        super("NotificationService");
    }

    public void onDestroy() {
        super.onDestroy();
        C0204d.m139c("NotificationService", "service destroyed");
    }

    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                C0204d.m129a("NotificationService", "Wrong action type detected");
                return;
            }
            String stringExtra = intent.getStringExtra("notification_id");
            C0204d.m141c("NotificationService", "received extra %s from notification %s", action, stringExtra);
            int i = -1;
            switch (action.hashCode()) {
                case -964594249:
                    if (action.equals("action_confirm")) {
                        i = 2;
                        break;
                    }
                    break;
                case 1064330403:
                    if (action.equals("action_cancel")) {
                        i = 1;
                        break;
                    }
                    break;
                case 1096596436:
                    if (action.equals("action_delete")) {
                        i = 0;
                        break;
                    }
                    break;
            }
            switch (i) {
                case RECEIVED_VALUE:
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    VerificationFactory.getNotificationApi(this).m51c(stringExtra);
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    VerificationFactory.getNotificationApi(this).m50b(stringExtra);
                default:
                    throw new IllegalArgumentException(String.format(Locale.US, "Wrong action type %s for NotificationService detected", new Object[]{action}));
            }
        }
    }
}
