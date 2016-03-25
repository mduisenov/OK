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
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.model.UserWithLogin;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.onelog.registration.ProfileErrorBuilder;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.ui.adapters.ScrollLoadBlocker;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.ui.custom.text.PasswordEditText;
import ru.ok.android.utils.LibverifyUtil;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.controls.authorization.AuthorizationControl;
import ru.ok.android.utils.controls.authorization.OnLoginListener;
import ru.ok.android.utils.controls.nativeregistration.OnRecoverUserBySmsListener;
import ru.ok.android.utils.controls.nativeregistration.OnRegainUserListener;
import ru.ok.android.utils.controls.nativeregistration.PrepareProfileActivityControl;
import ru.ok.android.utils.controls.nativeregistration.RecoverUserBySmsControl;
import ru.ok.android.utils.controls.nativeregistration.RegainUserControl;
import ru.ok.android.utils.controls.nativeregistration.RegistrationConstants.EnterPasswordReason;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.onelog.registration.ProfileErrorEventFactory;
import ru.ok.onelog.registration.ProfileErrorSource;
import ru.ok.onelog.registration.RegistrationWorkflowSource;

public class EnterPasswordFragment extends PinFragment implements OnRecoverUserBySmsListener, OnRegainUserListener {
    private EnterPasswordReason enterPasswordReason;
    private boolean isPasswordAlreadyRecovered;
    private boolean isPasswordChanged;
    private TextView loginView;
    private String password;
    private PasswordEditText passwordText;
    private View progressView;
    private View recoverBtn;
    private RecoverUserBySmsControl recoverUserBySmsControl;
    private String uid;
    private UserWithLogin user;

    /* renamed from: ru.ok.android.ui.nativeRegistration.EnterPasswordFragment.1 */
    class C10801 implements TextWatcher {
        C10801() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EnterPasswordFragment.this.hideError();
        }

        public void afterTextChanged(Editable s) {
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.EnterPasswordFragment.2 */
    class C10812 implements OnClickListener {
        C10812() {
        }

        public void onClick(View v) {
            EnterPasswordFragment.this.hideError();
            EnterPasswordFragment.this.showSpinner();
            if (EnterPasswordFragment.this.isActivityGotLocations()) {
                EnterPasswordFragment.this.communicationInterface.goToUpdateUserInfo();
            } else if (EnterPasswordFragment.this.isPasswordChanged) {
                EnterPasswordFragment.this.execPrepareProfileActivityProcessor();
            } else if (EnterPasswordFragment.this.isPasswordAlreadyRecovered) {
                AuthorizationControl.getInstance().login(EnterPasswordFragment.this.getLogin(), EnterPasswordFragment.this.password, EnterPasswordFragment.this);
            } else if (!EnterPasswordFragment.this.isLoginStarted()) {
                ProfileErrorBuilder profileErrorBuilder = new ProfileErrorBuilder();
                EnterPasswordFragment.this.password = EnterPasswordFragment.this.passwordText.getText();
                if (EnterPasswordFragment.this.validatePassword(EnterPasswordFragment.this.password, EnterPasswordFragment.this.passwordText, profileErrorBuilder)) {
                    EnterPasswordFragment.this.changePassword();
                    return;
                }
                EnterPasswordFragment.this.logPasswordErrorIfOccur(profileErrorBuilder);
                EnterPasswordFragment.this.hideSpinner();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.EnterPasswordFragment.3 */
    static /* synthetic */ class C10823 {
        static final /* synthetic */ int[] f114x4fbfdfb4;

        static {
            f114x4fbfdfb4 = new int[EnterPasswordReason.values().length];
            try {
                f114x4fbfdfb4[EnterPasswordReason.RECOVER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f114x4fbfdfb4[EnterPasswordReason.REGAIN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCommunicationInterface((CommunicationInterface) getActivity());
        View view = LocalizationManager.inflate(getActivity(), 2130903178, container, false);
        setErrorTextView((TextView) view.findViewById(2131625281));
        View logoView = view.findViewById(2131624803);
        this.progressView = view.findViewById(2131624548);
        this.recoverBtn = view.findViewById(2131624804);
        this.loginView = (TextView) view.findViewById(2131624676);
        AvatarImageView avatar = (AvatarImageView) view.findViewById(2131624657);
        this.passwordText = (PasswordEditText) view.findViewById(2131624773);
        if (AuthorizationPreferences.getPasswordValidationEnabled()) {
            this.passwordText.setValidatePassword(true, true);
        }
        Bundle arguments = getArguments();
        this.enterPasswordReason = (EnterPasswordReason) arguments.getSerializable("enter_password_reason");
        this.pin = arguments.getString("pin");
        this.user = (UserWithLogin) arguments.getParcelable("user_info");
        this.uid = arguments.getString("uid");
        if (this.enterPasswordReason != EnterPasswordReason.RECOVER) {
            avatar.setVisibility(8);
            this.loginView.setVisibility(8);
            logoView.setVisibility(0);
        } else {
            logoView.setVisibility(8);
            ImageViewManager.getInstance().displayImage(this.user.picUrl, avatar, this.user.genderType == UserGenderType.MALE, ScrollLoadBlocker.forIdleAndTouchIdle());
            buildUserName();
        }
        setFeedbackButtonListener((TextView) view.findViewById(2131624812));
        initListeners();
        return view;
    }

    private void buildUserName() {
        String userName = this.user.getAnyName().trim();
        if (StringUtils.isEmpty(userName)) {
            this.loginView.setText(this.user.login);
        } else {
            this.loginView.setText(userName);
        }
    }

    private void initListeners() {
        this.passwordText.setTextChangedListener(new C10801());
        this.recoverBtn.setOnClickListener(new C10812());
    }

    private void changePassword() {
        if (this.enterPasswordReason == EnterPasswordReason.REGAIN) {
            this.regainUserControl = new RegainUserControl();
            this.regainUserControl.tryToRegainUser(this.uid, this.user.uid, this.pin, this.password, this);
        } else if (this.enterPasswordReason == EnterPasswordReason.CHANGE_AFTER_REGISTRATION) {
            Bundle bundle = new Bundle();
            bundle.putString("old_passwrod", getPin());
            bundle.putString("new_passwrod", this.password);
            GlobalBus.send(2131623949, new BusEvent(bundle));
        } else {
            this.recoverUserBySmsControl = new RecoverUserBySmsControl();
            this.recoverUserBySmsControl.tryToRecoverUserBySms(this.user.uid, this.pin, this.password, this);
        }
    }

    private void logPasswordErrorIfOccur(ProfileErrorBuilder profileErrorBuilder) {
        if (profileErrorBuilder.hasError()) {
            ProfileErrorSource profileErrorSource;
            if (this.enterPasswordReason == EnterPasswordReason.REGAIN) {
                profileErrorSource = ProfileErrorSource.reactivation;
            } else if (this.enterPasswordReason == EnterPasswordReason.CHANGE_AFTER_REGISTRATION) {
                profileErrorSource = ProfileErrorSource.registration;
            } else {
                profileErrorSource = ProfileErrorSource.recovery;
            }
            OneLog.log(ProfileErrorEventFactory.get(profileErrorBuilder.toString(), profileErrorSource));
        }
    }

    public void onResume() {
        super.onResume();
        GlobalBus.register(this);
    }

    public void onPause() {
        super.onPause();
        GlobalBus.unregister(this);
    }

    protected void hideSpinner() {
        this.recoverBtn.setAlpha(1.0f);
        this.recoverBtn.setClickable(true);
        this.progressView.setVisibility(8);
    }

    protected void showSpinner() {
        this.recoverBtn.setAlpha(0.4f);
        this.recoverBtn.setClickable(false);
        this.progressView.setVisibility(0);
    }

    protected String getLogin() {
        if (this.user == null) {
            return null;
        }
        return this.user.login;
    }

    public void onLoginSuccessful(String url, String verificationUrl) {
        hideSpinner();
        storeUserName(getLogin(), false);
        logWorkflowSuccess();
        this.communicationInterface.goToOdklActivity();
    }

    protected void showInputError() {
        this.passwordText.hideValidation();
        this.passwordText.setEditTextBackground(2130838301);
    }

    protected void hideInputError() {
        this.passwordText.setEditTextBackground(2130838300);
    }

    public void onRecoverPasswordSuccessful() {
        this.isPasswordAlreadyRecovered = true;
        AuthorizationControl.getInstance().login(getLogin(), this.password, (OnLoginListener) this);
    }

    public void onRecoverPasswordError(String error, @NonNull ErrorType errorType) {
        logWorkflowError();
        hideSpinner();
        if (errorType == ErrorType.SMS_ACTIVATION_EXPIRED) {
            showError(2131166438);
        } else {
            showError(errorType.getDefaultErrorMessage());
        }
    }

    @Subscribe(on = 2131623946, to = 2131624129)
    public void onChangePasswordResult(BusEvent event) {
        if (event.resultCode == -1) {
            this.isPasswordChanged = true;
            this.pin = this.password;
            if (this.enterPasswordReason == EnterPasswordReason.CHANGE_AFTER_REGISTRATION) {
                execPrepareProfileActivityProcessor();
                return;
            }
            LibverifyUtil.completeVerification(getContext());
            logWorkflowSuccess();
            this.communicationInterface.goToOdklActivity();
            return;
        }
        logWorkflowError();
        showError(ErrorType.from(event.bundleOutput).getDefaultErrorMessage());
        hideSpinner();
    }

    private void execPrepareProfileActivityProcessor() {
        this.prepareProfileActivityControl = new PrepareProfileActivityControl();
        this.prepareProfileActivityControl.prepareProfileActivity(this);
    }

    protected RegistrationWorkflowSource getWorkflowSource() {
        switch (C10823.f114x4fbfdfb4[this.enterPasswordReason.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return RegistrationWorkflowSource.enter_password_recovery;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return RegistrationWorkflowSource.enter_password_react;
            default:
                return RegistrationWorkflowSource.enter_password_reg;
        }
    }
}
