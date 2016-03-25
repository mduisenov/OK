package ru.ok.android.ui.polls.fragment;

import android.app.Activity;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.ok.android.ui.custom.RoundedColorWithStrokeDrawable;
import ru.ok.android.ui.custom.radio.AppPollRadioView;
import ru.ok.android.ui.custom.radio.AppPollRadioView.OnCheckedChangedListener;
import ru.ok.model.poll.AppPollAnswer;
import ru.ok.model.poll.RatingPollQuestion;

public class RatingStepAppPollFragment extends Fragment implements FragmentWithAnswer {
    private RatingStepInteractionListener listener;
    private RatingPollQuestion question;
    private LinearLayout radioLayout;

    public interface RatingStepInteractionListener {
        void onAnswer(String str);
    }

    /* renamed from: ru.ok.android.ui.polls.fragment.RatingStepAppPollFragment.1 */
    class C11371 implements OnClickListener {
        C11371() {
        }

        public void onClick(View v) {
            if (v instanceof Checkable) {
                ((Checkable) v).toggle();
                if (v instanceof AppPollRadioView) {
                    RatingStepAppPollFragment.this.listener.onAnswer(((AppPollRadioView) v).getText());
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.polls.fragment.RatingStepAppPollFragment.2 */
    class C11382 implements OnCheckedChangedListener {
        C11382() {
        }

        public void onCheckedChanged(View v, boolean checked) {
            for (int i = 0; i < RatingStepAppPollFragment.this.radioLayout.getChildCount(); i++) {
                View child = RatingStepAppPollFragment.this.radioLayout.getChildAt(i);
                if ((child instanceof Checkable) && checked) {
                    if (child == v) {
                        ((Checkable) child).setChecked(true);
                    } else {
                        ((Checkable) child).setChecked(false);
                    }
                }
            }
        }
    }

    public static RatingStepAppPollFragment newInstance(RatingPollQuestion question) {
        RatingStepAppPollFragment fragment = new RatingStepAppPollFragment();
        Bundle args = new Bundle();
        args.putParcelable("arg_rating_question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    private StateListDrawable getStateListDrawable(int leftColor, int rightColor, int checkedColor, int borderSize, String text) {
        int textColor = getResources().getColor(2131492906);
        int textColorChecked = getResources().getColor(2131492907);
        int textSize = getResources().getDimensionPixelSize(2131230877);
        StateListDrawable stateListDrawable = new StateListDrawable();
        int[] iArr = new int[]{16842912};
        stateListDrawable.addState(iArr, new RoundedColorWithStrokeDrawable(checkedColor, text, textColorChecked, textSize));
        iArr = new int[]{-16842912};
        stateListDrawable.addState(iArr, new RoundedColorWithStrokeDrawable(leftColor, rightColor, borderSize, text, textColor, textSize));
        return stateListDrawable;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.question = (RatingPollQuestion) getArguments().getParcelable("arg_rating_question");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(2130903191, container, false);
        TextView textView = (TextView) v.findViewById(2131624614);
        textView.setText(this.question.getTitle());
        this.radioLayout = (LinearLayout) v.findViewById(2131624826);
        AppPollRadioView radio1 = (AppPollRadioView) v.findViewById(2131624827);
        AppPollRadioView radio2 = (AppPollRadioView) v.findViewById(2131624828);
        AppPollRadioView radio3 = (AppPollRadioView) v.findViewById(2131624829);
        AppPollRadioView radio4 = (AppPollRadioView) v.findViewById(2131624830);
        AppPollRadioView radio5 = (AppPollRadioView) v.findViewById(2131624831);
        int green = getResources().getColor(2131492904);
        int red = getResources().getColor(2131492905);
        int borderSize = getResources().getDimensionPixelSize(2131230876);
        radio1.setImageDrawable(getStateListDrawable(red, red, red, borderSize, radio1.getText()));
        AppPollRadioView appPollRadioView = radio2;
        appPollRadioView.setImageDrawable(getStateListDrawable(red, red, red, borderSize, radio2.getText()));
        appPollRadioView = radio3;
        appPollRadioView.setImageDrawable(getStateListDrawable(red, green, red, borderSize, radio3.getText()));
        appPollRadioView = radio4;
        appPollRadioView.setImageDrawable(getStateListDrawable(green, green, green, borderSize, radio4.getText()));
        appPollRadioView = radio5;
        appPollRadioView.setImageDrawable(getStateListDrawable(green, green, green, borderSize, radio5.getText()));
        OnClickListener listener = new C11371();
        OnCheckedChangedListener checkedChangedListener = new C11382();
        radio1.setOnClickListener(listener);
        radio2.setOnClickListener(listener);
        radio3.setOnClickListener(listener);
        radio4.setOnClickListener(listener);
        radio5.setOnClickListener(listener);
        radio1.setOnCheckedChangeListener(checkedChangedListener);
        radio2.setOnCheckedChangeListener(checkedChangedListener);
        radio3.setOnCheckedChangeListener(checkedChangedListener);
        radio4.setOnCheckedChangeListener(checkedChangedListener);
        radio5.setOnCheckedChangeListener(checkedChangedListener);
        return v;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.listener = (RatingStepInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    public AppPollAnswer getAnswer() {
        for (int i = 0; i < this.radioLayout.getChildCount(); i++) {
            View v = this.radioLayout.getChildAt(i);
            if (v instanceof AppPollRadioView) {
                AppPollRadioView radio = (AppPollRadioView) v;
                if (radio.isChecked()) {
                    return new AppPollAnswer(radio.getText(), radio.getText(), this.question.getStep());
                }
            }
        }
        return null;
    }
}
