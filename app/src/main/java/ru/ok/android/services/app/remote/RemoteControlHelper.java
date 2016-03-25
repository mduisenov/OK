package ru.ok.android.services.app.remote;

import android.media.AudioManager;
import android.util.Log;
import java.lang.reflect.Method;

public class RemoteControlHelper {
    private static boolean sHasRemoteControlAPIs;
    private static Method sRegisterRemoteControlClientMethod;
    private static Method sUnregisterRemoteControlClientMethod;

    static {
        sHasRemoteControlAPIs = false;
        try {
            Class sRemoteControlClientClass = RemoteControlClientCompat.getActualRemoteControlClientClass(RemoteControlHelper.class.getClassLoader());
            sRegisterRemoteControlClientMethod = AudioManager.class.getMethod("registerRemoteControlClient", new Class[]{sRemoteControlClientClass});
            sUnregisterRemoteControlClientMethod = AudioManager.class.getMethod("unregisterRemoteControlClient", new Class[]{sRemoteControlClientClass});
            sHasRemoteControlAPIs = true;
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e2) {
        } catch (IllegalArgumentException e3) {
        } catch (SecurityException e4) {
        }
    }

    public static void registerRemoteControlClient(AudioManager audioManager, RemoteControlClientCompat remoteControlClient) {
        if (sHasRemoteControlAPIs) {
            try {
                sRegisterRemoteControlClientMethod.invoke(audioManager, new Object[]{remoteControlClient.getActualRemoteControlClientObject()});
            } catch (Exception e) {
                Log.e("RemoteControlHelper", e.getMessage(), e);
            }
        }
    }
}
