package ru.ok.android.ui.stream.mediatopic;

public enum PollAnswerState {
    EMPTY,
    WAITING_FOR_VOTE,
    WAITING_FOR_UNVOTE,
    VOTING,
    VOTED,
    UNVOTING,
    UNVOTED,
    ERROR
}
