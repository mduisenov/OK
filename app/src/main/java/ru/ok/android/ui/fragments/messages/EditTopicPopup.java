package ru.ok.android.ui.fragments.messages;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import com.afollestad.materialdialogs.MaterialDialog;
import java.lang.reflect.Field;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.ServicesSettingsHelper;

public final class EditTopicPopup extends BaseFragment implements TextWatcher, OnClickListener {
    private ProgressBar progress;
    private EditText topicView;

    public static EditTopicPopup newInstance(String initialTopic) {
        Bundle args = new Bundle();
        String str = "initial_topic";
        if (initialTopic == null) {
            initialTopic = "";
        }
        args.putString(str, initialTopic);
        EditTopicPopup result = new EditTopicPopup();
        result.setArguments(args);
        return result;
    }

    private String getInitialTopic() {
        return getArguments().getString("initial_topic");
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LocalizationManager.inflate(getActivity(), 2130903155, null, false);
        this.topicView = (EditText) rootView.findViewById(C0263R.id.text);
        this.topicView.setText(getInitialTopic());
        this.topicView.addTextChangedListener(this);
        this.topicView.setHint(getStringLocalized(2131166543));
        this.topicView.setSelection(this.topicView.length());
        this.progress = (ProgressBar) rootView.findViewById(2131624548);
        return new Builder(getActivity()).setView(rootView).setTitle(getStringLocalized(2131165564)).setPositiveButton(getStringLocalized(2131166474), null).create();
    }

    public void onStart() {
        super.onStart();
        onTextChanged(this.topicView.getText(), 0, 0, 0);
        KeyBoardUtils.showKeyBoard(getContext(), this.topicView);
        View button = getPositiveButton();
        if (button != null) {
            button.setOnClickListener(this);
        }
    }

    private View getPositiveButton() {
        try {
            Field positiveButton = MaterialDialog.class.getDeclaredField("positiveButton");
            positiveButton.setAccessible(true);
            return (View) positiveButton.get(getDialog());
        } catch (Throwable e) {
            Logger.m178e(e);
            return null;
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateSaveButtonAndProgress(!TextUtils.equals(s.toString().trim(), getInitialTopic()), true);
    }

    public void afterTextChanged(Editable s) {
    }

    public static String extractTopic(Intent data) {
        return data.getStringExtra("topic");
    }

    protected int getLayoutId() {
        return 0;
    }

    public void updateSaveButtonAndProgress(boolean buttonEnabled, boolean buttonOnly) {
        View button = getPositiveButton();
        if (button != null) {
            button.setEnabled(buttonEnabled);
        }
        if (!buttonOnly) {
            this.topicView.setEnabled(buttonEnabled);
            this.progress.setVisibility(buttonEnabled ? 8 : 0);
        }
    }

    public void onClick(View view) {
        Intent data = new Intent();
        String theme = this.topicView.getText().toString().trim();
        int maxLength = ServicesSettingsHelper.getServicesSettings().getMultichatMaxThemeLength();
        if (maxLength > 0 && theme.length() > maxLength) {
            theme = theme.substring(0, maxLength);
        }
        data.putExtra("topic", theme);
        getTargetFragment().onActivityResult(getTargetRequestCode(), -1, data);
        updateSaveButtonAndProgress(false, false);
    }
}
