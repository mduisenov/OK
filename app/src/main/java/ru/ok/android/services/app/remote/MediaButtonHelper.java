package ru.ok.android.services.app.remote;

import android.content.ComponentName;
import android.media.AudioManager;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MediaButtonHelper {
    static Method sMethodRegisterMediaButtonEventReceiver;
    static Method sMethodUnregisterMediaButtonEventReceiver;

    static {
        initializeStaticCompatMethods();
    }

    static void initializeStaticCompatMethods() {
        try {
            sMethodRegisterMediaButtonEventReceiver = AudioManager.class.getMethod("registerMediaButtonEventReceiver", new Class[]{ComponentName.class});
            sMethodUnregisterMediaButtonEventReceiver = AudioManager.class.getMethod("unregisterMediaButtonEventReceiver", new Class[]{ComponentName.class});
        } catch (NoSuchMethodException e) {
        }
    }

    public static void registerMediaButtonEventReceiverCompat(AudioManager audioManager, ComponentName receiver) {
        if (sMethodRegisterMediaButtonEventReceiver != null) {
            try {
                sMethodRegisterMediaButtonEventReceiver.invoke(audioManager, new Object[]{receiver});
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw ((RuntimeException) cause);
                } else if (cause instanceof Error) {
                    throw ((Error) cause);
                } else {
                    throw new RuntimeException(e);
                }
            } catch (IllegalAccessException e2) {
                Log.e("MediaButtonHelper", "IllegalAccessException invoking registerMediaButtonEventReceiver.");
                e2.printStackTrace();
            }
        }
    }

    public static void unregisterMediaButtonEventReceiverCompat(AudioManager audioManager, ComponentName receiver) {
        if (sMethodUnregisterMediaButtonEventReceiver != null) {
            try {
                sMethodUnregisterMediaButtonEventReceiver.invoke(audioManager, new Object[]{receiver});
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw ((RuntimeException) cause);
                } else if (cause instanceof Error) {
                    throw ((Error) cause);
                } else {
                    throw new RuntimeException(e);
                }
            } catch (IllegalAccessException e2) {
                Log.e("MediaButtonHelper", "IllegalAccessException invoking unregisterMediaButtonEventReceiver.");
                e2.printStackTrace();
            }
        }
    }
}
