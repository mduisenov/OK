package ru.ok.android.ui.custom.mediacomposer;

import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class OkApiMediaTopicValidator implements MediaTopicValidator {
    public boolean canPost(MediaTopicMessage mediaTopic, MediaTopicType type, boolean toStatus) {
        if (mediaTopic == null || type == null) {
            return false;
        }
        if (mediaTopic.getWithPlace() != null) {
            return true;
        }
        if (mediaTopic.isItemsEmpty()) {
            return false;
        }
        if (!toStatus || type == MediaTopicType.USER) {
            return true;
        }
        return false;
    }
}
