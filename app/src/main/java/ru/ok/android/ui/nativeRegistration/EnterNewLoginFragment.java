package ru.ok.android.ui.nativeRegistration;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.app.MyTrackerUtils;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.onelog.registration.ProfileErrorBuilder;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.ui.activity.LoginScreenUtils;
import ru.ok.android.ui.custom.text.PasswordEditText;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.controls.nativeregistration.ConfirmationControl;
import ru.ok.android.utils.controls.nativeregistration.OnConfirmationListener;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.onelog.registration.ProfileErrorEventFactory;
import ru.ok.onelog.registration.ProfileErrorSource;

public class EnterNewLoginFragment extends PinFragment implements OnConfirmationListener {
    private View createBtn;
    private TextView feedbackButton;
    private boolean loading;
    private String login;
    private TextView loginTxt;
    private PasswordEditText passwordText;
    private View progress;
    private String userId;

    /* renamed from: ru.ok.android.ui.nativeRegistration.EnterNewLoginFragment.1 */
    class C10771 implements TextWatcher {
        C10771() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EnterNewLoginFragment.this.hideError();
        }

        public void afterTextChanged(Editable s) {
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.EnterNewLoginFragment.2 */
    class C10782 implements TextWatcher {
        C10782() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            EnterNewLoginFragment.this.hideError();
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.EnterNewLoginFragment.3 */
    class C10793 implements OnClickListener {
        C10793() {
        }

        public void onClick(View view) {
            String newPassword = EnterNewLoginFragment.this.passwordText.getText();
            EnterNewLoginFragment.this.login = EnterNewLoginFragment.this.loginTxt.getText().toString().trim();
            if (EnterNewLoginFragment.this.login.contains(" ")) {
                EnterNewLoginFragment.this.showError(LocalizationManager.getString(EnterNewLoginFragment.this.getActivity(), 2131165837));
                return;
            }
            EnterNewLoginFragment.this.hideError();
            if (EnterNewLoginFragment.this.isActivityGotLocations()) {
                EnterNewLoginFragment.this.communicationInterface.goToUpdateUserInfo();
            } else if (StringUtils.isEmpty(EnterNewLoginFragment.this.login)) {
                EnterNewLoginFragment.this.showError(LocalizationManager.getString(EnterNewLoginFragment.this.getActivity(), 2131165784));
            } else {
                EnterNewLoginFragment.this.showSpinner();
                if (!EnterNewLoginFragment.this.isLoginStarted()) {
                    ProfileErrorBuilder profileErrorBuilder = new ProfileErrorBuilder();
                    if (!AuthorizationPreferences.getPasswordObligatoryBeforeProfile() || EnterNewLoginFragment.this.validatePassword(newPassword, EnterNewLoginFragment.this.passwordText, profileErrorBuilder)) {
                        EnterNewLoginFragment.this.confirmationControl = new ConfirmationControl();
                        EnterNewLoginFragment.this.confirmationControl.tryToConfirmUser(EnterNewLoginFragment.this.userId, EnterNewLoginFragment.this.login, EnterNewLoginFragment.this.pin, newPassword, EnterNewLoginFragment.this);
                        return;
                    }
                    OneLog.log(ProfileErrorEventFactory.get(profileErrorBuilder.toString(), ProfileErrorSource.registration_with_login));
                    EnterNewLoginFragment.this.hideSpinner();
                }
            }
        }
    }

    public void onUserConfirmationSuccessfull(String authToken) {
        this.confirmationControl = null;
        setToken(authToken);
        LoginScreenUtils.performLoginByToken(authToken, this);
        MyTrackerUtils.onRegistration(this.login, this.userId);
    }

    public void onUserConfirmationError(String error, @NonNull ErrorType errorType) {
        logWorkflowError();
        hideSpinner();
        this.confirmationControl = null;
        if (errorType == ErrorType.SMS_CODE_WRONG) {
            showError(LocalizationManager.getString(getActivity(), 2131165844, LocalizationManager.getString(getActivity(), 2131166328)));
            return;
        }
        showError(errorType.getDefaultErrorMessage());
    }

    protected void hideInputError() {
        Utils.setViewBackgroundWithoutResettingPadding(this.loginTxt, 2130838300);
        this.passwordText.hideValidation();
        this.passwordText.setEditTextBackground(2130838300);
    }

    protected void showInputError() {
        Utils.setViewBackgroundWithoutResettingPadding(this.loginTxt, 2130838301);
        this.passwordText.setEditTextBackground(2130838301);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCommunicationInterface((CommunicationInterface) getActivity());
        View view = LocalizationManager.inflate(getActivity(), 2130903553, container, false);
        setErrorTextView((TextView) view.findViewById(2131625281));
        this.loginTxt = (TextView) view.findViewById(2131625283);
        this.loginTxt.setPadding(0, 0, 0, 0);
        this.passwordText = (PasswordEditText) view.findViewById(2131624773);
        if (!AuthorizationPreferences.getPasswordObligatoryBeforeProfile()) {
            this.passwordText.setVisibility(8);
        } else if (AuthorizationPreferences.getPasswordValidationEnabled()) {
            this.passwordText.setValidatePassword(true, true);
        }
        this.createBtn = view.findViewById(2131625284);
        this.feedbackButton = (TextView) view.findViewById(2131624812);
        this.progress = view.findViewById(2131624680);
        this.userId = getArguments().getString("uid");
        this.pin = getArguments().getString("pin");
        initListeners();
        return view;
    }

    private void enableButton() {
        if (!this.loading) {
            this.createBtn.setAlpha(1.0f);
            this.createBtn.setClickable(true);
        }
    }

    private void disableButton() {
        this.createBtn.setAlpha(0.4f);
        this.createBtn.setClickable(false);
    }

    private void initListeners() {
        this.passwordText.setTextChangedListener(new C10771());
        this.loginTxt.addTextChangedListener(new C10782());
        this.createBtn.setOnClickListener(new C10793());
        setFeedbackButtonListener(this.feedbackButton);
    }

    protected void showSpinner() {
        this.loading = true;
        this.progress.setVisibility(0);
        disableButton();
    }

    protected String getLogin() {
        return this.login;
    }

    protected void hideSpinner() {
        this.loading = false;
        this.progress.setVisibility(8);
        enableButton();
    }
}
