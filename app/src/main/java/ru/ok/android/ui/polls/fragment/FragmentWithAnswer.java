package ru.ok.android.ui.polls.fragment;

import android.support.annotation.Nullable;
import ru.ok.model.poll.AppPollAnswer;

public interface FragmentWithAnswer {
    @Nullable
    AppPollAnswer getAnswer();
}
