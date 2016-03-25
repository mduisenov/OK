package ru.ok.android.ui.custom.prefs;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class ConfirmPreference extends DialogPreference {
    private ConfirmPreferenceListener listener;

    public interface ConfirmPreferenceListener {
        void onConfirmed();
    }

    public ConfirmPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ConfirmPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListener(ConfirmPreferenceListener listener) {
        this.listener = listener;
    }

    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);
    }

    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult && this.listener != null) {
            this.listener.onConfirmed();
        }
    }
}
