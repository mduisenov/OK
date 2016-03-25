package ru.ok.android.ui.polls.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.services.processors.poll.AppPollPreferences;

public class FinalStepAppPollFragment extends Fragment {
    public static FinalStepAppPollFragment newInstance() {
        FinalStepAppPollFragment fragment = new FinalStepAppPollFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = (TextView) inflater.inflate(2130903198, container, false);
        textView.setText(AppPollPreferences.getTextByKey(getActivity(), "app_poll_final"));
        return textView;
    }
}
