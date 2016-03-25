package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.storage.Storages;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.mediatopic.PollAnswerState;
import ru.ok.android.ui.stream.view.PollAnswerLayout;
import ru.ok.model.stream.entities.FeedPollEntity;
import ru.ok.model.stream.entities.FeedPollEntity.Answer;

public class StreamPollAnswerItem extends StreamItem {
    private Answer answer;
    private final boolean hasSelf;
    private final boolean isMultiAnswerAllowed;
    private final int maxVotesCount;
    public final FeedPollEntity poll;

    public static class PollAnswerViewHolder extends ViewHolder {
        final PollAnswerLayout pollAnswer;

        public PollAnswerViewHolder(View view) {
            super(view);
            this.pollAnswer = (PollAnswerLayout) view.findViewById(2131625365);
        }
    }

    protected StreamPollAnswerItem(FeedWithState feed, FeedPollEntity poll, Answer answer, int maxVotesCount, boolean isMultiAnswerAllowed, boolean hasSelf) {
        super(18, 1, 3, feed);
        this.poll = poll;
        this.answer = answer;
        this.maxVotesCount = maxVotesCount;
        this.isMultiAnswerAllowed = isMultiAnswerAllowed;
        this.hasSelf = hasSelf;
    }

    public Answer getAnswer() {
        return this.answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903494, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        bindAnswerView(holder);
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public void bindAnswerView(ViewHolder holder) {
        if (holder instanceof PollAnswerViewHolder) {
            int votesDelta;
            PollAnswerLayout answerView = ((PollAnswerViewHolder) holder).pollAnswer;
            this.answer = Storages.getInstance(answerView.getContext(), OdnoklassnikiApplication.getCurrentUser().getId()).getMtPollsManager().getAnswerInfo(this.poll, this.answer);
            boolean isSelected = this.answer.isSelf();
            PollAnswerState answerState = isSelected ? PollAnswerState.VOTED : PollAnswerState.EMPTY;
            if (isSelected || this.hasSelf) {
                votesDelta = 0;
            } else {
                votesDelta = 1;
            }
            answerView.setAnswerInfo(this.answer.text, this.answer.getVotesCount(), this.maxVotesCount + votesDelta, this.isMultiAnswerAllowed, isSelected, answerState);
            answerView.setTag(2131624335, this);
            answerView.setTag(2131624349, holder);
            answerView.setClickable(true);
        }
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        PollAnswerViewHolder holder = new PollAnswerViewHolder(view);
        holder.pollAnswer.setOnClickListener(streamItemViewController.getPollAnswerClickListener());
        return holder;
    }

    boolean sharePressedState() {
        return false;
    }
}
