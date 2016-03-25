package ru.ok.model.poll;

import android.os.Parcelable;
import java.util.List;

public interface PollStep extends Parcelable {
    PollQuestion getQuestion(List<AppPollAnswer> list);
}
