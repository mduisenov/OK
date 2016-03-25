package ru.ok.android.ui.stream.data;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.SharedPreferenceFormatException;
import ru.ok.android.utils.settings.Settings;

public final class StreamSettingsHelper {
    private final Context context;
    private final String lastActivityTsSettingName;
    private final String streamPositionSettingName;

    public StreamSettingsHelper(@NonNull Context context, @NonNull StreamContext streamContext) {
        this.context = context;
        this.streamPositionSettingName = streamContext.getKey() + ":stream_position";
        this.lastActivityTsSettingName = streamContext.getKey() + ":last_activity_ts";
    }

    public void setStreamPosition(@Nullable StreamListPosition position) {
        Editor editor = Settings.getEditor(this.context);
        if (position != null) {
            StreamListPositionPrefsHelper.toSharedPrefs(position, editor, this.streamPositionSettingName);
        } else {
            StreamListPositionPrefsHelper.cleanFromPrefs(editor, this.streamPositionSettingName);
        }
        editor.putLong(this.lastActivityTsSettingName, System.currentTimeMillis());
        editor.apply();
    }

    public long getLastActivityTs() {
        long j = 0;
        try {
            j = Settings.getLongValue(this.context, this.lastActivityTsSettingName, 0);
        } catch (ClassCastException e) {
            Settings.getEditor(this.context).remove(this.lastActivityTsSettingName).apply();
        }
        return j;
    }

    @Nullable
    public StreamListPosition getStreamPosition() {
        StreamListPosition streamListPosition = null;
        long lastActivityTs = getLastActivityTs();
        if (lastActivityTs > 0) {
            if (System.currentTimeMillis() - lastActivityTs > getStreamPositionTtl() * 1000) {
                Logger.m173d("Don't using saved stream position according to ttl settings: ttl=%d elapsed=%d", Long.valueOf(getStreamPositionTtl() * 1000), Long.valueOf(System.currentTimeMillis() - lastActivityTs));
                setStreamPosition(streamListPosition);
                return streamListPosition;
            }
        }
        try {
            streamListPosition = StreamListPositionPrefsHelper.fromSharedPrefs(Settings.getPreferences(this.context), this.streamPositionSettingName);
        } catch (SharedPreferenceFormatException e) {
            Editor editor = Settings.getEditor(this.context);
            StreamListPositionPrefsHelper.cleanFromPrefs(editor, this.streamPositionSettingName);
            editor.apply();
        }
        return streamListPosition;
    }

    public long getStreamPositionTtl() {
        long j = 900;
        try {
            j = Settings.getLongValueInvariable(this.context, "position_ttl", 900);
        } catch (ClassCastException e) {
            clearPositionTtl();
        }
        return j;
    }

    public void setPositionTtl(long positionTtlSec) {
        Settings.getEditorInvariable(this.context).putLong("position_ttl", positionTtlSec).apply();
    }

    public void clearPositionTtl() {
        Settings.getEditorInvariable(this.context).remove("position_ttl").apply();
    }

    public long getForceRefreshInterval() {
        long j = 3600;
        try {
            j = Settings.getLongValueInvariable(this.context, "force_refresh_interval", 3600);
        } catch (ClassCastException e) {
            clearForceRefreshInterval();
        }
        return j;
    }

    public void setForceRefreshInterval(long forceRefreshIntervalSec) {
        Settings.getEditorInvariable(this.context).putLong("force_refresh_interval", forceRefreshIntervalSec).apply();
    }

    public void clearForceRefreshInterval() {
        Settings.getEditorInvariable(this.context).remove("force_refresh_interval").apply();
    }
}
