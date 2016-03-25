package ru.ok.android.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.fragments.base.BaseFragment;

public final class StubFragment extends BaseFragment {
    private TextView text;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(2130903525, container, false);
        this.text = (TextView) view.findViewById(C0263R.id.text);
        this.text.setText(getText());
        return view;
    }

    public static Bundle newArguments(String text, String title) {
        Bundle args = new Bundle();
        args.putString("TEXT", text);
        args.putString("TITLE", title);
        return args;
    }

    public String getText() {
        return getArguments().getString("TEXT");
    }

    protected String getTitle() {
        return getArguments().getString("TITLE");
    }

    protected int getLayoutId() {
        return 2130903525;
    }
}
