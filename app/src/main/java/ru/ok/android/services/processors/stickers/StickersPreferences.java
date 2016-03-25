package ru.ok.android.services.processors.stickers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

final class StickersPreferences {
    private Editor editor;
    private final SharedPreferences preferences;

    public StickersPreferences(Context context) {
        this.preferences = context.getSharedPreferences("stickers-prefs", 0);
    }

    public int getLastSeenVersion() {
        return this.preferences.getInt("last-seen-version", 0);
    }

    public Editor getEditor() {
        if (this.editor == null) {
            this.editor = this.preferences.edit();
        }
        return this.editor;
    }

    public void setLastSeenVersion(int version) {
        getEditor().putInt("last-seen-version", version).apply();
    }

    public long getPaymentEndDate() {
        return this.preferences.getLong("key-payment-end-date", 0);
    }

    public void clear() {
        getEditor().clear().apply();
    }

    public long getLastUpdateSetTimeMs() {
        return this.preferences.getLong("last-set-update-ms", 0);
    }
}
