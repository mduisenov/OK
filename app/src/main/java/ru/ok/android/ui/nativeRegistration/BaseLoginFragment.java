package ru.ok.android.ui.nativeRegistration;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import ru.ok.android.http.util.TextUtils;
import ru.ok.android.ui.activity.LoginScreenUtils;
import ru.ok.android.ui.custom.text.PasswordEditText;
import ru.ok.android.utils.controls.authorization.OnLoginListener;
import ru.ok.android.utils.localization.LocalizationManager;

public abstract class BaseLoginFragment extends BaseFragment implements OnLoginListener {
    protected String login;
    protected boolean loginFromRegistration;
    protected View needHelpButton;
    private NeedHelpDialog needHelpDialog;
    protected PasswordEditText passwordText;

    /* renamed from: ru.ok.android.ui.nativeRegistration.BaseLoginFragment.1 */
    class C10631 implements TextWatcher {
        C10631() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            BaseLoginFragment.this.hideError();
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.BaseLoginFragment.2 */
    class C10642 implements OnClickListener {
        C10642() {
        }

        public void onClick(View view) {
            if (BaseLoginFragment.this.needHelpDialog == null) {
                BaseLoginFragment.this.needHelpDialog = new NeedHelpDialog();
            }
            if (!BaseLoginFragment.this.needHelpDialog.isAdded()) {
                BaseLoginFragment.this.needHelpDialog.show(BaseLoginFragment.this.getActivity().getSupportFragmentManager(), null);
            }
        }
    }

    public BaseLoginFragment() {
        this.loginFromRegistration = false;
    }

    protected String getLogin() {
        return this.login;
    }

    protected void initListeners() {
        this.passwordText.setTextChangedListener(new C10631());
        this.needHelpButton.setOnClickListener(new C10642());
    }

    public void onLoginSuccessful(String url, String verificationUrl) {
        hideSpinner();
        storeUserName(getLogin(), this.loginFromRegistration);
        this.communicationInterface.goToOdklActivity();
    }

    public void onLoginError(String message, int type, int errorCode) {
        showDefaultErrorMessage(message, type, errorCode);
        hideSpinner();
    }

    protected void performLoginByPassword(String login, String password) {
        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(password)) {
            showError(LocalizationManager.getString(getActivity(), 2131165806));
            hideSpinner();
            return;
        }
        this.passwordText.hideValidation();
        LoginScreenUtils.performLoginByPassword(login, password, this);
    }
}
