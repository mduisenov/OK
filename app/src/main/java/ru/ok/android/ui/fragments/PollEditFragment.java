package ru.ok.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.custom.mediacomposer.MaxLengthFilter;
import ru.ok.android.ui.custom.mediacomposer.PollItem;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.settings.MediaComposerSettings;

public class PollEditFragment extends Fragment implements TextWatcher {
    private InputFilter[] answerLengthFilter;
    boolean betweenResumeAndPause;
    private PollEditFragmentListener listener;
    private ViewGroup mAnswers;
    private CompoundButton mCheckBoxOnlyOneAnswer;
    private EditText mEditTextPollTitle;
    private int nextBaseId;
    private final Runnable notifyValidityChangedRunnable;
    private boolean onlyOneAnswer;
    @NonNull
    private MediaComposerSettings settings;

    public interface PollEditFragmentListener {
        void onPollValidityChanged();
    }

    /* renamed from: ru.ok.android.ui.fragments.PollEditFragment.1 */
    class C08101 implements Runnable {
        C08101() {
        }

        public void run() {
            if (PollEditFragment.this.listener != null) {
                PollEditFragment.this.listener.onPollValidityChanged();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.PollEditFragment.2 */
    class C08112 implements OnCheckedChangeListener {
        C08112() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            PollEditFragment.this.onlyOneAnswer = isChecked;
            PollEditFragment.this.updateAnswers();
        }
    }

    private class ViewHolder implements TextWatcher, OnFocusChangeListener {
        final int baseId;
        final EditText editText;
        final CompoundButton radioButton;

        ViewHolder(View view, int baseId) {
            this.baseId = baseId;
            this.editText = (EditText) view.findViewById(2131625244);
            this.editText.setId(baseId + 1);
            this.radioButton = (CompoundButton) view.findViewById(2131625243);
            this.radioButton.setId(baseId + 2);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (TextUtils.isEmpty(s) && after > count) {
                PollEditFragment.this.postNotifyValidityChanged();
            }
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s) && count < before) {
                PollEditFragment.this.postNotifyValidityChanged();
            }
        }

        public void afterTextChanged(Editable s) {
            if (this.editText.isFocused()) {
                PollEditFragment.this.processItems();
            }
        }

        public void onFocusChange(View v, boolean hasFocus) {
            PollEditFragment.this.processItems();
        }
    }

    public PollEditFragment() {
        this.mEditTextPollTitle = null;
        this.mCheckBoxOnlyOneAnswer = null;
        this.mAnswers = null;
        this.nextBaseId = 10;
        this.settings = new MediaComposerSettings();
        this.notifyValidityChangedRunnable = new C08101();
        this.betweenResumeAndPause = false;
    }

    public static PollEditFragment newInstance(PollItem poll, MediaTopicType mediaTopicType) {
        Bundle args = new Bundle();
        args.putParcelable("key_poll", poll);
        args.putSerializable("mt_type", mediaTopicType);
        PollEditFragment fragment = new PollEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(PollEditFragmentListener listener) {
        this.listener = listener;
    }

    public void postNotifyValidityChanged() {
        ThreadUtil.getMainThreadHandler().post(this.notifyValidityChangedRunnable);
    }

    public boolean isValid() {
        if (this.mEditTextPollTitle == null || TextUtils.isEmpty(this.mEditTextPollTitle.getText().toString().trim()) || getAnswersCount() < 2) {
            return false;
        }
        return true;
    }

    public int getAnswersCount() {
        int count = 0;
        int childCount = this.mAnswers == null ? 0 : this.mAnswers.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (!TextUtils.isEmpty(getHolderFromView(i).editText.getText().toString().trim())) {
                count++;
            }
        }
        return count;
    }

    public PollItem getItem() {
        PollItem pollItem = new PollItem();
        PollItem pollItemEditable = (PollItem) getArguments().getParcelable("key_poll");
        if (pollItemEditable != null) {
            pollItem.setViewId(pollItemEditable.getViewId());
        }
        pollItem.setTitle(this.mEditTextPollTitle.getText().toString());
        pollItem.setMultiAnswersAllowed(!this.mCheckBoxOnlyOneAnswer.isChecked());
        for (int i = 0; i < this.mAnswers.getChildCount(); i++) {
            String answer = getHolderFromView(i).editText.getText().toString().trim();
            if (!TextUtils.isEmpty(answer)) {
                pollItem.addAnswer(answer);
            }
        }
        return pollItem;
    }

    @NonNull
    public MediaTopicType getMediaTopicType() {
        MediaTopicType type = (MediaTopicType) getArguments().getSerializable("mt_type");
        if (type == null) {
            return MediaTopicType.USER;
        }
        return type;
    }

    public void processItems() {
        int i;
        int size = this.mAnswers.getChildCount();
        List<View> listViews = new ArrayList();
        for (i = 0; i < size; i++) {
            listViews.add(this.mAnswers.getChildAt(i));
        }
        i = 0;
        while (i < size) {
            View view = (View) listViews.get(i);
            EditText editText = ((ViewHolder) view.getTag()).editText;
            if (TextUtils.isEmpty(editText.getText()) && !editText.isFocused() && (i != size - 1 || (size - 2 >= 0 && TextUtils.isEmpty(getHolderFromView(size - 2).editText.getText())))) {
                this.mAnswers.removeView(view);
            }
            i++;
        }
        size = this.mAnswers.getChildCount();
        if (size > 0 && !TextUtils.isEmpty(getHolderFromView(size - 1).editText.getText())) {
            if (size < this.settings.maxPollAnswersCount) {
                addItem("", getNextBaseId());
            } else {
                MediaComposerStats.hitLimit(getMediaTopicType(), "answer_count");
            }
        }
    }

    public ViewHolder getHolderFromView(int index) {
        return (ViewHolder) this.mAnswers.getChildAt(index).getTag();
    }

    public void addItem(String item, int baseId) {
        View view = LocalizationManager.inflate(this.mAnswers.getContext(), 2130903398, this.mAnswers, false);
        view.setId(baseId);
        ViewHolder viewHolder = new ViewHolder(view, baseId);
        viewHolder.editText.setText(item);
        viewHolder.editText.setFilters(getAnswerLengthFilter(this.settings.maxPollAnswerLength));
        viewHolder.editText.addTextChangedListener(viewHolder);
        if (this.betweenResumeAndPause) {
            viewHolder.editText.setOnFocusChangeListener(viewHolder);
        }
        updateAnswerButton(viewHolder);
        view.setTag(viewHolder);
        this.mAnswers.addView(view);
    }

    private InputFilter[] getAnswerLengthFilter(int maxAnswerLength) {
        if (this.answerLengthFilter == null) {
            this.answerLengthFilter = new InputFilter[]{new MaxLengthFilter(maxAnswerLength, "answer_length", getMediaTopicType())};
        }
        return this.answerLengthFilter;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settings = MediaComposerSettings.fromSharedPreferences(ServicesSettingsHelper.getPreferences(getActivity()));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = LocalizationManager.inflate(getActivity(), 2130903400, container, false);
        this.mEditTextPollTitle = (EditText) returnView.findViewById(2131625249);
        this.mCheckBoxOnlyOneAnswer = (CompoundButton) returnView.findViewById(2131625251);
        this.mAnswers = (ViewGroup) returnView.findViewById(2131625250);
        this.mEditTextPollTitle.setFilters(new InputFilter[]{new MaxLengthFilter(this.settings.maxPollQuestionLength, "question_length", getMediaTopicType())});
        if (savedInstanceState != null) {
            String[] answers = savedInstanceState.getStringArray("answers");
            int[] baseIds = savedInstanceState.getIntArray("item_base_ids");
            this.onlyOneAnswer = savedInstanceState.getBoolean("only_one_answer");
            if (!(answers == null || baseIds == null || answers.length != baseIds.length)) {
                int count = answers.length;
                for (int i = 0; i < count; i++) {
                    addItem(answers[i], baseIds[i]);
                    if (this.nextBaseId <= baseIds[i]) {
                        this.nextBaseId = baseIds[i] + 10;
                    }
                }
            }
        } else if (getArguments() == null || getArguments().getParcelable("key_poll") == null) {
            this.onlyOneAnswer = true;
            this.mCheckBoxOnlyOneAnswer.setChecked(this.onlyOneAnswer);
        } else {
            PollItem pollItem = (PollItem) getArguments().getParcelable("key_poll");
            this.mEditTextPollTitle.setText(pollItem.getTitle());
            this.onlyOneAnswer = !pollItem.isMultiAnswersAllowed();
            this.mCheckBoxOnlyOneAnswer.setChecked(this.onlyOneAnswer);
            if (pollItem.getAnswers() != null) {
                for (String s : pollItem.getAnswers()) {
                    addItem(s, getNextBaseId());
                }
            }
            processItems();
        }
        this.mCheckBoxOnlyOneAnswer.setOnCheckedChangeListener(new C08112());
        this.mEditTextPollTitle.addTextChangedListener(this);
        return returnView;
    }

    public void onResume() {
        super.onResume();
        int answerCount = this.mAnswers.getChildCount();
        for (int i = 0; i < answerCount; i++) {
            ViewHolder holder = (ViewHolder) this.mAnswers.getChildAt(i).getTag();
            holder.editText.setOnFocusChangeListener(holder);
        }
        this.betweenResumeAndPause = true;
    }

    public void onPause() {
        super.onPause();
        this.betweenResumeAndPause = false;
    }

    private void updateAnswers() {
        int answerCount = this.mAnswers.getChildCount();
        for (int i = 0; i < answerCount; i++) {
            updateAnswerButton((ViewHolder) this.mAnswers.getChildAt(i).getTag());
        }
    }

    private void updateAnswerButton(ViewHolder holder) {
        holder.radioButton.setButtonDrawable(this.onlyOneAnswer ? 2130837698 : 2130837785);
        if (this.onlyOneAnswer) {
            holder.radioButton.setChecked(holder.editText.isFocused());
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (after == 0) {
            if (this.mAnswers.getChildCount() == 1 && TextUtils.isEmpty(getHolderFromView(0).editText.getText())) {
                this.mAnswers.removeAllViews();
            }
        } else if (after > count && this.mAnswers.getChildCount() == 0 && this.settings.maxPollAnswersCount > 0) {
            addItem("", getNextBaseId());
        }
        if (TextUtils.isEmpty(s) && after > count) {
            postNotifyValidityChanged();
        }
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s) && count < before) {
            postNotifyValidityChanged();
        }
    }

    public void afterTextChanged(Editable s) {
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int answerCount = this.mAnswers.getChildCount();
        String[] answers = new String[answerCount];
        int[] baseIds = new int[answerCount];
        for (int i = 0; i < answerCount; i++) {
            ViewHolder viewHolder = getHolderFromView(i);
            answers[i] = viewHolder.editText.getText().toString();
            baseIds[i] = viewHolder.baseId;
        }
        outState.putStringArray("answers", answers);
        outState.putIntArray("item_base_ids", baseIds);
        outState.putBoolean("only_one_answer", this.mCheckBoxOnlyOneAnswer.isChecked());
    }

    private int getNextBaseId() {
        int id = this.nextBaseId;
        this.nextBaseId += 10;
        return id;
    }
}
