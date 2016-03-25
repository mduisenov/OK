package ru.ok.android.ui.dialogs.highlight;

import android.content.Context;
import ru.ok.android.utils.settings.Settings;

public class HighlightsStateStore {
    public static final boolean needsToShowHighlight(Context context, String key) {
        return !Settings.getBoolValueInvariable(context, key, false);
    }

    public static final void setHighlightShownValue(Context context, String key, boolean value) {
        Settings.storeBoolValueInvariable(context, key, value);
    }

    public static final void markHighlightAsShown(Context context, String key) {
        setHighlightShownValue(context, key, true);
    }
}
