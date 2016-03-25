package ru.ok.android.ui.nativeRegistration;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.util.ArrayList;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.model.UpdateProfileFieldsFlags;
import ru.ok.android.model.UserWithLogin;
import ru.ok.android.onelog.registration.ProfileErrorBuilder;
import ru.ok.android.services.GoogleInfoService;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.services.processors.registration.Location;
import ru.ok.android.services.processors.registration.RegisterWithLibVerifyProcessor;
import ru.ok.android.ui.activity.LoginScreenUtils;
import ru.ok.android.ui.custom.text.PasswordEditText;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.controls.authorization.AuthorizationControl;
import ru.ok.android.utils.controls.authorization.OnLoginListener;
import ru.ok.android.utils.controls.nativeregistration.OnRegainUserListener;
import ru.ok.android.utils.controls.nativeregistration.PrepareProfileActivityControl;
import ru.ok.android.utils.controls.nativeregistration.PrepareProfileActivityListener;
import ru.ok.android.utils.controls.nativeregistration.RegistrationConstants;
import ru.ok.android.utils.localization.LocalizationManager;

public abstract class PinFragment extends BaseFragment implements OnLoginListener, OnRegainUserListener, PrepareProfileActivityListener {
    private boolean activityGotLocations;
    protected String pin;
    private String token;

    protected void setToken(String token) {
        this.token = token;
    }

    protected String getToken() {
        return this.token;
    }

    protected boolean isLoginStarted() {
        if (getToken() == null) {
            return false;
        }
        AuthorizationControl.getInstance().login(getToken(), true, (OnLoginListener) this);
        return true;
    }

    protected boolean isActivityGotLocations() {
        return this.activityGotLocations;
    }

    public void onRegainUserSuccessfull(String token) {
        setToken(token);
        AuthorizationControl.getInstance().login(token, true, (OnLoginListener) this);
    }

    public void onRegainUserError(String error, @NonNull ErrorType errorType) {
        logWorkflowError();
        hideSpinner();
        this.regainUserControl = null;
        showError(errorType.getDefaultErrorMessage());
    }

    public void onLoginSuccessful(String url, String verificationUrl) {
        storeUserName(getLogin(), true);
        this.prepareProfileActivityControl = new PrepareProfileActivityControl();
        this.prepareProfileActivityControl.prepareProfileActivity(this);
    }

    public void onPrepareProfileActivitySuccess(ArrayList<Location> locations, UpdateProfileFieldsFlags updateProfileFieldsFlags) {
        this.prepareProfileActivityControl = null;
        if (AuthorizationPreferences.getGoogleInfoThroughOAuth()) {
            this.activityGotLocations = true;
            this.communicationInterface.goToUpdateUserInfo(getPin(), locations, updateProfileFieldsFlags);
        } else {
            this.communicationInterface.goToUpdateUserInfo(getPin(), locations, GoogleInfoService.getUserInfo(), updateProfileFieldsFlags);
        }
        hideSpinner();
    }

    public void onPrepareProfileActivityError(String error, @NonNull ErrorType errorType) {
        logWorkflowError();
        this.prepareProfileActivityControl = null;
        showError(errorType.getDefaultErrorMessage());
        hideSpinner();
    }

    protected String getPin() {
        return this.pin;
    }

    public void onLoginError(String message, int type, int errorCode) {
        logWorkflowError();
        showDefaultErrorMessage(message, type, errorCode);
        hideSpinner();
    }

    protected boolean validatePassword(String password, PasswordEditText passwordEditText, ProfileErrorBuilder profileErrorBuilder) {
        if (passwordEditText.validatePassword()) {
            passwordEditText.hideValidation();
            if (TextUtils.isEmpty(password)) {
                showError(LocalizationManager.getString(getActivity(), 2131165788));
                profileErrorBuilder.setPasswordEmpty(true);
            } else if (password.contains(" ")) {
                showError(LocalizationManager.getString(getActivity(), 2131165838));
                profileErrorBuilder.setPasswordInvalid(true);
            }
        } else if (StringUtils.isEmpty(passwordEditText.getText())) {
            profileErrorBuilder.setPasswordEmpty(true);
        } else {
            profileErrorBuilder.setPasswordInvalid(true);
        }
        if (profileErrorBuilder.hasError()) {
            return false;
        }
        return true;
    }

    protected void onRegisterWithLibVerifyResult(BusEvent event, String countryCode, String phone) {
        if (event.resultCode == -1) {
            Bundle bundleOutput = event.bundleOutput;
            this.pin = bundleOutput.getString("pin");
            if (bundleOutput.getBoolean("account_recovery")) {
                this.communicationInterface.goToExistingUser((UserWithLogin) bundleOutput.getParcelable("user_info"), countryCode, phone, this.pin);
                return;
            }
            String uid = bundleOutput.getString("uid");
            boolean isPhoneAlreadyLogin = bundleOutput.getBoolean("phone_already_login");
            ArrayList<UserWithLogin> usersToRegain = bundleOutput.getParcelableArrayList("user_list");
            if (usersToRegain != null && usersToRegain.size() > 0) {
                this.communicationInterface.goToUserList(uid, getLogin(), this.pin, usersToRegain, isPhoneAlreadyLogin);
                return;
            } else if (isPhoneAlreadyLogin) {
                this.communicationInterface.goToEnterNewLogin(uid, this.pin);
                return;
            } else {
                String token = bundleOutput.getString(RegistrationConstants.KEY_TOKEN);
                setToken(token);
                LoginScreenUtils.performLoginByToken(token, this);
                return;
            }
        }
        logWorkflowError();
        hideSpinner();
        showError(ErrorType.from(event.bundleOutput).getDefaultErrorMessage());
    }

    protected void processRegistrationWithLibVerify(String sessionId, String token, String phoneNumber) {
        if (!isLoginStarted()) {
            RegisterWithLibVerifyProcessor.process(sessionId, token, phoneNumber);
        }
    }
}
