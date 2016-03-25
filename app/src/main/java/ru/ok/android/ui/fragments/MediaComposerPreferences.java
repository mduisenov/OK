package ru.ok.android.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerData;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.UserInfo;

public class MediaComposerPreferences {
    private final SharedPreferences prefs;

    public MediaComposerPreferences(Context context) {
        this.prefs = context.getSharedPreferences("media_composer", 0);
    }

    public void saveUserMediaTopicDraft(UserInfo userInfo, MediaComposerData content) {
        saveDraft(content, getPrefNameInContext("draft-", userInfo, MediaTopicType.USER, null));
    }

    public void saveGroupMediaTopicDarft(UserInfo userInfo, String groupId, MediaComposerData content) {
        saveDraft(content, getPrefNameInContext("draft-", userInfo, MediaTopicType.GROUP_THEME, groupId));
    }

    private void saveDraft(MediaComposerData content, String prefName) {
        try {
            this.prefs.edit().putString(prefName, IOUtils.serializableToBase64String(content)).apply();
        } catch (Throwable e) {
            Logger.m177e("Failed to save mediatopic draft: %s", e);
            Logger.m178e(e);
        }
    }

    public MediaComposerData getUserMediaTopicDraft(UserInfo userInfo) {
        return getDraft(getPrefNameInContext("draft-", userInfo, MediaTopicType.USER, null));
    }

    public MediaComposerData getGroupMediaTopicDraft(UserInfo userInfo, String groupId) {
        return getDraft(getPrefNameInContext("draft-", userInfo, MediaTopicType.GROUP_THEME, groupId));
    }

    private MediaComposerData getDraft(String prefName) {
        String contentEncoded = this.prefs.getString(prefName, null);
        if (contentEncoded != null) {
            try {
                return (MediaComposerData) IOUtils.base64SerializedToObject(contentEncoded);
            } catch (Throwable e) {
                Logger.m177e("Failed to restore mediatopic draft: %s", e);
                Logger.m178e(e);
                this.prefs.edit().remove(prefName).apply();
            }
        }
        return null;
    }

    public void deleteUserMediaTopicDraft(UserInfo userInfo) {
        deleteDraft(getPrefNameInContext("draft-", userInfo, MediaTopicType.USER, null));
    }

    public void deleteGroupMediaTopicDraft(UserInfo userInfo, String groupId) {
        deleteDraft(getPrefNameInContext("draft-", userInfo, MediaTopicType.GROUP_THEME, groupId));
    }

    private void deleteDraft(String prefName) {
        this.prefs.edit().remove(prefName).apply();
    }

    public int getLastUsedToStatus(UserInfo currentUser, MediaTopicType type, String additionalId) {
        return this.prefs.getInt(getPrefNameInContext("to-status-", currentUser, type, additionalId), 3);
    }

    public void setLastUsedToStatus(UserInfo currentUser, MediaTopicType type, String additionalId, int toStatus) {
        if (toStatus == 1 || toStatus == 2 || toStatus == 3) {
            this.prefs.edit().putInt(getPrefNameInContext("to-status-", currentUser, type, additionalId), toStatus).apply();
            return;
        }
        Logger.m185w("Illegal value toStatus=%d", Integer.valueOf(toStatus));
    }

    private static String getPrefNameInContext(String prefPrefix, UserInfo userInfo, MediaTopicType type, String additionalId) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefPrefix).append(type);
        if (userInfo != null) {
            sb.append('-').append(userInfo.uid);
        }
        if (additionalId != null) {
            sb.append('-').append(additionalId);
        }
        return sb.toString();
    }
}
