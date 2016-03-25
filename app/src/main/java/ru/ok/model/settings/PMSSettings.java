package ru.ok.model.settings;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import ru.ok.android.utils.Logger;

public abstract class PMSSettings implements Parcelable {
    static int readIntPref(SharedPreferences prefs, String key, int defaultValue, Editor[] outCleanupEditor) {
        try {
            defaultValue = prefs.getInt(key, defaultValue);
        } catch (ClassCastException e) {
            catchException(prefs, key, outCleanupEditor, e);
        }
        return defaultValue;
    }

    @SuppressLint({"CommitPrefEdits"})
    static boolean readBooleanPref(SharedPreferences prefs, String key, boolean defaultValue, Editor[] outCleanupEditor) {
        try {
            defaultValue = prefs.getBoolean(key, defaultValue);
        } catch (ClassCastException e) {
            catchException(prefs, key, outCleanupEditor, e);
        }
        return defaultValue;
    }

    @SuppressLint({"CommitPrefEdits"})
    private static void catchException(SharedPreferences prefs, String key, Editor[] outCleanupEditor, ClassCastException e) {
        Logger.m187w(e, "Stored preference has invalid type: key=%s", key);
        if (outCleanupEditor != null) {
            Editor editor = outCleanupEditor[0];
            if (editor == null) {
                editor = prefs.edit();
                outCleanupEditor[0] = editor;
            }
            editor.remove(key);
        }
    }
}
