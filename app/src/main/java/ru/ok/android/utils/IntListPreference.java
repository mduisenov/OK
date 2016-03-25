package ru.ok.android.utils;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class IntListPreference extends ListPreference {
    public IntListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntListPreference(Context context) {
        super(context);
    }

    protected boolean persistString(String value) {
        if (value == null) {
            return false;
        }
        return persistInt(Integer.valueOf(value).intValue());
    }

    protected String getPersistedString(String defaultReturnValue) {
        if (getSharedPreferences().contains(getKey())) {
            return String.valueOf(getPersistedInt(0));
        }
        return defaultReturnValue;
    }

    public int getIntValue() {
        try {
            return Integer.parseInt(getValue());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
