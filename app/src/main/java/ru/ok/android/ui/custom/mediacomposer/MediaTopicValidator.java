package ru.ok.android.ui.custom.mediacomposer;

import ru.ok.java.api.request.mediatopic.MediaTopicType;

public interface MediaTopicValidator {
    boolean canPost(MediaTopicMessage mediaTopicMessage, MediaTopicType mediaTopicType, boolean z);
}
