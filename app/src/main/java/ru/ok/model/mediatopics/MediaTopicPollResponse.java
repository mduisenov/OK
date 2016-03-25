package ru.ok.model.mediatopics;

import java.util.ArrayList;
import java.util.List;
import ru.ok.model.stream.entities.FeedPollEntity.Answer;

public final class MediaTopicPollResponse {
    public final List<Answer> answers;
    public final boolean success;

    public MediaTopicPollResponse(boolean success) {
        this.answers = new ArrayList();
        this.success = success;
    }

    public String toString() {
        return "MediaTopicPollResponse{success=" + this.success + ", answers=" + this.answers + '}';
    }
}
