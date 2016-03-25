package ru.ok.android.utils;

import android.content.Context;
import android.os.Handler;
import ru.mail.libverify.api.UncaughtExceptionListener;
import ru.mail.libverify.api.VerificationApi;
import ru.mail.libverify.api.VerificationApi$VerificationStateChangedListener;
import ru.mail.libverify.api.VerificationApi.IvrStateListener;
import ru.mail.libverify.api.VerificationFactory;
import ru.mail.libverify.utils.LogReceiver;
import ru.ok.android.utils.settings.Settings;

public class LibverifyUtil {
    private static String APP_KEY;
    private static String APP_NAME;
    public static String VERIFICATION_SERVICE;
    private static VerificationApi verificationApi;

    /* renamed from: ru.ok.android.utils.LibverifyUtil.1 */
    static class C14211 implements LogReceiver {
        C14211() {
        }

        public void m170v(String tag, String message) {
            Logger.m183v(tag, message);
        }

        public void m171v(String tag, String message, Throwable exception) {
            Logger.m183v(tag, message, exception);
        }

        public void m168e(String tag, String message) {
            Logger.m177e(tag, message);
        }

        public void m169e(String tag, String message, Throwable exception) {
            Logger.m177e(tag, message, exception);
        }

        public void m166d(String tag, String message) {
            Logger.m173d(tag, message);
        }

        public void m167d(String tag, String message, Throwable exception) {
            Logger.m173d(tag, message, exception);
        }
    }

    /* renamed from: ru.ok.android.utils.LibverifyUtil.2 */
    static class C14222 implements UncaughtExceptionListener {
        C14222() {
        }

        public void uncaughtException(Thread thread, Throwable ex) {
            Logger.m178e(ex);
        }
    }

    /* renamed from: ru.ok.android.utils.LibverifyUtil.3 */
    static class C14233 implements Runnable {
        final /* synthetic */ Context val$context;

        C14233(Context context) {
            this.val$context = context;
        }

        public void run() {
            LibverifyUtil.resetSessionId(this.val$context);
            Settings.setLibVerifyCompleted(this.val$context, false);
            LibverifyUtil.getVerificationApi(this.val$context).signOut(false);
        }
    }

    static {
        APP_KEY = "VPkLHkj0hepfixj4";
        APP_NAME = "ODKL";
        VERIFICATION_SERVICE = "odkl_registration";
    }

    public static String restartVerification(Context context, String phoneNumber) {
        cancelVerification(context);
        return startVerification(context, phoneNumber);
    }

    public static void resetSessionId(Context context) {
        Settings.storeLibverifySession(context, null);
    }

    public static String getSessionId(Context context) {
        return Settings.getLibverifySession(context);
    }

    public static void cancelVerification(Context context) {
        String sessionId = getSessionId(context);
        if (!StringUtils.isEmpty(sessionId)) {
            getVerificationApi(context).cancelVerification(sessionId);
            resetSessionId(context);
        }
    }

    public static void completeVerification(Context context) {
        String sessionId = getSessionId(context);
        if (!StringUtils.isEmpty(sessionId)) {
            getVerificationApi(context).completeVerification(sessionId);
            Settings.setLibVerifyCompleted(context, true);
        }
    }

    private static String startVerification(Context context, String phoneNumber) {
        return startVerification(context, phoneNumber, getVerificationApi(context));
    }

    private static String startVerification(Context context, String phoneNumber, VerificationApi verificationApi) {
        String verificationId = verificationApi.startVerification(VERIFICATION_SERVICE, phoneNumber, null, null);
        Settings.storeLibverifySession(context, verificationId);
        return verificationId;
    }

    public static synchronized VerificationApi getVerificationApi(Context context) {
        VerificationApi verificationApi;
        synchronized (LibverifyUtil.class) {
            if (verificationApi == null) {
                verificationApi = VerificationFactory.getInstance(context, APP_NAME, APP_KEY, new C14211(), new C14222());
            }
            verificationApi = verificationApi;
        }
        return verificationApi;
    }

    public static void requestVerificationState(Context context, VerificationApi$VerificationStateChangedListener listener) {
        getVerificationApi(context).requestVerificationState(getSessionId(context), listener);
    }

    public static void requestIvrCall(Context context, IvrStateListener listener) {
        getVerificationApi(context).requestIvrPhoneCall(getSessionId(context), listener);
    }

    public static void requestNewSmsCode(Context context) {
        getVerificationApi(context).requestNewSmsCode(getSessionId(context));
    }

    public static void verifySmsCode(Context context, String smsCode) {
        getVerificationApi(context).verifySmsCode(getSessionId(context), smsCode);
    }

    public static void addVerificationStateChangedListener(Context context, VerificationApi$VerificationStateChangedListener verificationStateChangedListener) {
        getVerificationApi(context).addVerificationStateChangedListener(verificationStateChangedListener);
    }

    public static void removeVerificationStateChangedListener(Context context, VerificationApi$VerificationStateChangedListener verificationStateChangedListener) {
        getVerificationApi(context).removeVerificationStateChangedListener(verificationStateChangedListener);
    }

    public static void logoutDevice(Context context, Handler handler) {
        if (Settings.getLibVerifyCompleted(context)) {
            handler.post(new C14233(context));
        }
    }
}
