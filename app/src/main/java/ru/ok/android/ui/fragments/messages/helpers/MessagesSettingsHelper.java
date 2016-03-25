package ru.ok.android.ui.fragments.messages.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.UserInfo;

public final class MessagesSettingsHelper {
    private final SharedPreferences preferences;

    public MessagesSettingsHelper(Context context) {
        this.preferences = context.getSharedPreferences("MessagingPreferences", 0);
    }

    public void setMessageDraft(UserInfo user, String settingsName, String enteredText) {
        Settings.commitEditor(getEditor().putString(buildDraftName(user, settingsName), enteredText));
    }

    public String getMessageDraft(UserInfo user, String settingsName) {
        return this.preferences.getString(buildDraftName(user, settingsName), "");
    }

    private String buildDraftName(UserInfo user, String settingsName) {
        return String.format("draft-%s-%s", new Object[]{user.uid, settingsName});
    }

    private Editor getEditor() {
        return this.preferences.edit();
    }
}
