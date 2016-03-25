package ru.ok.android.ui.nativeRegistration;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import ru.ok.android.app.MyTrackerUtils;
import ru.ok.android.ui.activity.LoginScreenUtils;
import ru.ok.android.ui.custom.text.PasswordEditText;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.controls.authorization.AuthorizationControl;
import ru.ok.android.utils.controls.authorization.OnLoginListener;
import ru.ok.android.utils.controls.authorization.OnLoginUidListener;
import ru.ok.android.utils.localization.LocalizationManager;

public class NewLoginFragment extends BaseLoginFragment implements OnClickListener, OnEditorActionListener {
    private String login;
    private View loginButton;
    private AutoCompleteTextView loginText;
    private View progress;

    /* renamed from: ru.ok.android.ui.nativeRegistration.NewLoginFragment.1 */
    class C10991 implements OnLoginUidListener {
        C10991() {
        }

        public void onLoginUid(String uid) {
            MyTrackerUtils.onRegistration(NewLoginFragment.this.login, uid);
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.NewLoginFragment.2 */
    class C11002 implements TextWatcher {
        C11002() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            NewLoginFragment.this.hideError();
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean z;
        setCommunicationInterface((CommunicationInterface) getActivity());
        View view = LocalizationManager.inflate(getActivity(), 2130903343, container, false);
        setErrorTextView((TextView) view.findViewById(2131625281));
        this.needHelpButton = view.findViewById(2131625131);
        this.passwordText = (PasswordEditText) view.findViewById(2131624773);
        this.passwordText.getEditText().setOnEditorActionListener(this);
        this.loginText = (AutoCompleteTextView) view.findViewById(2131625132);
        this.loginText.setPadding(0, 0, 0, 0);
        Context activity = getActivity();
        AutoCompleteTextView autoCompleteTextView = this.loginText;
        if (savedInstanceState == null) {
            z = true;
        } else {
            z = false;
        }
        if (LoginScreenUtils.setupLoginAutocompletion(activity, autoCompleteTextView, z)) {
            this.passwordText.requestFocus();
        } else {
            this.loginText.requestFocus();
        }
        this.loginButton = view.findViewById(2131625018);
        this.progress = view.findViewById(2131624680);
        initListeners();
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.loginFromRegistration = arguments.getBoolean("login_from_web_registration", false);
            if (this.loginFromRegistration) {
                showSpinner();
                this.passwordText.clearText();
                this.login = arguments.getString("user");
                this.loginText.setText(this.login);
                AuthorizationControl.getInstance().login(arguments.getString("token"), true, (OnLoginListener) this, new C10991());
            }
        }
        return view;
    }

    protected void initListeners() {
        super.initListeners();
        this.loginButton.setOnClickListener(this);
        this.loginText.addTextChangedListener(new C11002());
    }

    public void onLoginError(String message, int type, int errorCode) {
        showDefaultErrorMessage(message, type, errorCode);
        hideSpinner();
    }

    private void onLoginClick() {
        LoginScreenUtils.preprocessLoginPassword(this.loginText, this.passwordText.getEditText());
        String password = this.passwordText.getText().trim();
        hideError();
        showSpinner();
        this.login = this.loginText.getText().toString().trim();
        performLoginByPassword(this.login, password);
    }

    protected void hideSpinner() {
        this.loginButton.setClickable(true);
        this.loginButton.setAlpha(1.0f);
        this.progress.setVisibility(4);
    }

    protected void showSpinner() {
        this.loginButton.setClickable(false);
        this.loginButton.setAlpha(0.4f);
        this.progress.setVisibility(0);
    }

    protected String getLogin() {
        return this.login;
    }

    protected void hideInputError() {
        this.passwordText.setEditTextBackground(2130838300);
        Utils.setViewBackgroundWithoutResettingPadding(this.loginText, 2130838300);
    }

    protected void showInputError() {
        Utils.setViewBackgroundWithoutResettingPadding(this.passwordText.getEditText(), 2130838301);
        Utils.setViewBackgroundWithoutResettingPadding(this.loginText, 2130838301);
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        onLoginClick();
        return true;
    }

    public void onClick(View v) {
        if (v.getId() == 2131625018) {
            onLoginClick();
        }
    }
}
