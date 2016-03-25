package ru.ok.android.services.transport;

import android.content.Context;
import android.os.Parcel;
import android.text.TextUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.remote.Base64New;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.ServiceStateHolder;

class LegacyAuthSessionDataStore {
    static boolean legacyReadFromPreference(Context context, ServiceStateHolder holder) {
        String str = true;
        Logger.m172d("Reading service state from preferences...");
        String string = Settings.getStrValue(context, "pref_state_holder_new");
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        Parcel parcel = Parcel.obtain();
        try {
            byte[] bs = Base64New.decode(string, 0);
            parcel.unmarshall(bs, 0, bs.length);
            parcel.setDataPosition(0);
            holder.legacyReadFromBundle(parcel.readBundle(), Settings.getToken(context));
            Logger.m173d("Read from preferences: %s", holder);
            return str;
        } catch (Throwable e) {
            str = "Failed to read legacy service state from preferences";
            Logger.m179e(e, str);
            return false;
        } finally {
            parcel.recycle();
        }
    }

    static void clearLegacyStorage(Context context) {
        Settings.clearSettingByKey(context, "pref_state_holder_new");
    }
}
