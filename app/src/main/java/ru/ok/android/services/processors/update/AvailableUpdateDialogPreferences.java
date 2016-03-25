package ru.ok.android.services.processors.update;

import android.content.Context;
import android.content.SharedPreferences;
import ru.ok.android.utils.Logger;

class AvailableUpdateDialogPreferences {
    final int shownFromVersion;
    final int shownToVersion;
    final long shownTs;

    AvailableUpdateDialogPreferences(int shownFromVersion, int shownToVersion, long shownTs) {
        this.shownFromVersion = shownFromVersion;
        this.shownToVersion = shownToVersion;
        this.shownTs = shownTs;
    }

    boolean alreadyShown(int fromVersion, int toVersion) {
        return fromVersion == this.shownFromVersion && toVersion == this.shownToVersion;
    }

    static AvailableUpdateDialogPreferences fromSharedPreferences(Context context) {
        SharedPreferences prefs = CheckUpdatePreferences.getPreferences(context);
        int fromVersion = -1;
        int toVersion = -1;
        long shownTs = 0;
        try {
            fromVersion = prefs.getInt("dialog.show.from.version", -1);
            toVersion = prefs.getInt("dialog.show.to.version", -1);
            shownTs = prefs.getLong("dialog.show.ts", shownTs);
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to read dialog shown versions from prefs: " + e);
        }
        return new AvailableUpdateDialogPreferences(fromVersion, toVersion, shownTs);
    }

    static void saveShownForVersions(Context context, int fromVersion, int toVersion, long ts) {
        CheckUpdatePreferences.getPreferences(context).edit().putInt("dialog.show.from.version", fromVersion).putInt("dialog.show.to.version", toVersion).putLong("dialog.show.ts", ts).apply();
    }
}
