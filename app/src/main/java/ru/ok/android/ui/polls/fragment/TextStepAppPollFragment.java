package ru.ok.android.ui.polls.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.model.poll.AppPollAnswer;
import ru.ok.model.poll.TextPollQuestion;

public class TextStepAppPollFragment extends Fragment implements FragmentWithAnswer {
    private EditText editText;
    private StepTextInteractionListener listener;
    private TextPollQuestion question;

    public interface StepTextInteractionListener {
        void onAnswer(String str);
    }

    /* renamed from: ru.ok.android.ui.polls.fragment.TextStepAppPollFragment.1 */
    class C11441 implements TextWatcher {
        final /* synthetic */ TextView val$countTextView;

        C11441(TextView textView) {
            this.val$countTextView = textView;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            this.val$countTextView.setText(Integer.toString(TextStepAppPollFragment.this.editText.getText().length()));
        }
    }

    public static TextStepAppPollFragment newInstance(TextPollQuestion question) {
        TextStepAppPollFragment fragment = new TextStepAppPollFragment();
        Bundle args = new Bundle();
        args.putParcelable("arg_text_question", question);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.question = (TextPollQuestion) getArguments().getParcelable("arg_text_question");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(2130903193, container, false);
        ((TextView) v.findViewById(2131624614)).setText(this.question.getTitle());
        this.editText = (EditText) v.findViewById(2131624823);
        this.editText.addTextChangedListener(new C11441((TextView) v.findViewById(2131624832)));
        KeyBoardUtils.showKeyBoard(getActivity(), this.editText);
        return v;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.listener.onAnswer("1");
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.listener = (StepTextInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    public AppPollAnswer getAnswer() {
        return new AppPollAnswer("1", this.editText.getText().toString(), "1", this.question.getStep());
    }
}
