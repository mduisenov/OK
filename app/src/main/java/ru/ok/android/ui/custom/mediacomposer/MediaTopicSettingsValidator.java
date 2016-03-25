package ru.ok.android.ui.custom.mediacomposer;

import android.support.annotation.NonNull;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.settings.MediaComposerSettings;

public class MediaTopicSettingsValidator {
    public static int checkIsValid(@NonNull MediaTopicMessage mediaTopic, @NonNull MediaTopicType type, @NonNull MediaComposerSettings settings) {
        int itemCount = mediaTopic.getItemsCount();
        if (itemCount > (type == MediaTopicType.USER ? settings.maxBlockCount : settings.maxGroupBlockCount)) {
            MediaComposerStats.hitLimit(type, "block_count");
            return 2131166099;
        }
        for (int i = 0; i < itemCount; i++) {
            MediaItem item = mediaTopic.getItem(i);
            if (item instanceof TextItem) {
                if (((TextItem) item).getTextLength() > settings.maxTextLength) {
                    MediaComposerStats.hitLimit(type, "text_length");
                    return 2131166101;
                }
            } else if (item instanceof PollItem) {
                PollItem poll = (PollItem) item;
                if (poll.answers.size() > settings.maxPollAnswersCount) {
                    MediaComposerStats.hitLimit(type, "answer_count");
                    return 2131166097;
                } else if (poll.getTitle().length() > settings.maxPollQuestionLength) {
                    MediaComposerStats.hitLimit(type, "question_length");
                    return 2131166100;
                } else {
                    for (String answer : poll.answers) {
                        if (answer.length() > settings.maxPollAnswerLength) {
                            MediaComposerStats.hitLimit(type, "answer_length");
                            return 2131166098;
                        }
                    }
                    continue;
                }
            } else {
                continue;
            }
        }
        return 0;
    }
}
