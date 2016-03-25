package ru.ok.android.services.processors.general;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import java.io.File;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.utils.AndroidResourceUris;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.settings.Settings;

public final class RingtoneProcessor {
    @Subscribe(on = 2131623944, to = 2131624098)
    public void extractRingtones(BusEvent event) {
        Context context = OdnoklassnikiApplication.getContext();
        for (File externalFilesDir : ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_NOTIFICATIONS)) {
            if (externalFilesDir != null) {
                File ringtoneFile = new File(externalFilesDir, "odkl.ogg");
                if (ringtoneFile.exists()) {
                    Logger.m173d("Odkl ringtone exists: %s", ringtoneFile);
                    initializeIncomingRingtonePreference(context, true);
                    if (!ringtoneFile.delete()) {
                        Logger.m173d("Cannot delete existent ringtone file: %s", ringtoneFile);
                    }
                    MediaScannerConnection.scanFile(context, new String[]{externalFilesDir.getAbsolutePath()}, new String[]{"audio/*"}, null);
                    return;
                }
            }
        }
        initializeIncomingRingtonePreference(context, false);
    }

    private static void initializeIncomingRingtonePreference(Context context, boolean migrating) {
        if (!Settings.getBoolValueInvariable(context, "wnotifications_ringtone_initialized", false)) {
            String ringtoneKey = context.getString(2131166288);
            Uri ringtoneUri = migrating ? getMigratedIncomingRingtoneUri(context) : getDefaultIncomingRingtoneUri(context);
            String ringtoneString = ringtoneUri != null ? ringtoneUri.toString() : null;
            Settings.getEditorInvariable(context).putBoolean("wnotifications_ringtone_initialized", true).remove("wnotifications_ringtone").putString(ringtoneKey, ringtoneString).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit().remove("wnotifications_ringtone").putString(ringtoneKey, ringtoneString).apply();
        }
    }

    private static Uri getMigratedIncomingRingtoneUri(Context context) {
        String oldRingtoneString = Settings.getStrValueInvariable(context, "wnotifications_ringtone", null);
        if (TextUtils.isEmpty(oldRingtoneString)) {
            return null;
        }
        Uri oldRingtoneUri = Uri.parse(oldRingtoneString);
        Ringtone ringtone = RingtoneManager.getRingtone(context, oldRingtoneUri);
        if (ringtone == null) {
            return getDefaultIncomingRingtoneUri(context);
        }
        String ringtoneTitle = ringtone.getTitle(context);
        if (ringtoneTitle.equals(context.getResources().getString(2131165393)) || ringtoneTitle.equals("Odnoklassniki")) {
            return getDefaultIncomingRingtoneUri(context);
        }
        return oldRingtoneUri;
    }

    public static Uri getDefaultIncomingRingtoneUri(Context context) {
        return AndroidResourceUris.getSymbolicAndroidResourceUri(context, 2131099654);
    }
}
