package ru.ok.android.ui.nativeRegistration;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.BadTokenException;
import android.widget.TextView;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.fragments.registr.NotLoggedInWebFragment.Page;
import ru.ok.android.onelog.registration.RegistrationWorkflowLogHelper;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.ui.activity.LoginScreenUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.nativeregistration.CheckPhoneControl;
import ru.ok.android.utils.controls.nativeregistration.ConfirmationControl;
import ru.ok.android.utils.controls.nativeregistration.OnRegistrationListener;
import ru.ok.android.utils.controls.nativeregistration.PrepareProfileActivityControl;
import ru.ok.android.utils.controls.nativeregistration.RegainUserControl;
import ru.ok.android.utils.controls.nativeregistration.RegistrationControl;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;
import ru.ok.onelog.builtin.Outcome;
import ru.ok.onelog.registration.RegistrationWorkflowSource;

public abstract class BaseFragment extends Fragment implements OnRegistrationListener {
    protected CheckPhoneControl checkPhoneControl;
    protected CommunicationInterface communicationInterface;
    protected ConfirmationControl confirmationControl;
    private TextView errorTextView;
    private boolean isFeedbackEnabled;
    protected PrepareProfileActivityControl prepareProfileActivityControl;
    protected RegainUserControl regainUserControl;
    protected RegistrationControl registrationControl;

    /* renamed from: ru.ok.android.ui.nativeRegistration.BaseFragment.1 */
    class C10621 implements OnClickListener {
        C10621() {
        }

        public void onClick(View view) {
            if (BaseFragment.this.isFeedbackEnabled) {
                BaseFragment.this.communicationInterface.goToFeedback();
            } else {
                BaseFragment.this.communicationInterface.goToFaq();
            }
        }
    }

    protected abstract String getLogin();

    protected abstract void hideSpinner();

    protected abstract void showSpinner();

    public void setCommunicationInterface(CommunicationInterface communicationInterface) {
        this.communicationInterface = communicationInterface;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.isFeedbackEnabled = HelpSettingsHandler.isFeedbackEnabled(getActivity());
    }

    public void onDestroy() {
        super.onDestroy();
        this.confirmationControl = null;
        this.regainUserControl = null;
        this.prepareProfileActivityControl = null;
        this.checkPhoneControl = null;
        this.regainUserControl = null;
    }

    protected void setErrorTextView(TextView textView) {
        this.errorTextView = textView;
    }

    protected void showDefaultErrorMessage(String message, int type, int errorCode) {
        boolean z = true;
        if (type == 10) {
            if (errorCode == 401) {
                OdnoklassnikiApplication.setCurrentUser(null);
                try {
                    if (message.equals("AUTH_LOGIN : BLOCKED")) {
                        showError(LocalizationManager.getString(getActivity(), 2131166784));
                        Logger.m172d("On user block");
                        return;
                    }
                    showError(LocalizationManager.getString(getActivity(), 2131166052));
                    return;
                } catch (BadTokenException e) {
                    return;
                }
            }
            if (message != null) {
                showError(ErrorType.fromServerException(errorCode, message, true).getDefaultErrorMessage());
            } else {
                showError(ErrorType.fromServerException(errorCode, message, true).getDefaultErrorMessage());
            }
        } else if (type == 9) {
            if (errorCode != 555) {
                z = false;
            }
            notifyTransportError(z);
        } else {
            if (message != null) {
                showError(ErrorType.fromServerException(errorCode, message, true).getDefaultErrorMessage());
            } else {
                showError(ErrorType.fromServerException(errorCode, message, true).getDefaultErrorMessage());
            }
        }
    }

    protected void showError(String error) {
        showInputError();
        this.errorTextView.setText(error);
        this.errorTextView.setVisibility(0);
    }

    protected void showError(int id) {
        showError(LocalizationManager.getString(getActivity(), id));
    }

    protected void showInputError() {
    }

    protected void hideError() {
        hideInputError();
        this.errorTextView.setVisibility(8);
    }

    protected void hideInputError() {
    }

    private void notifyTransportError(boolean noValidDate) {
        if (noValidDate) {
            try {
                showError(LocalizationManager.getString(getActivity(), 2131166620));
                return;
            } catch (BadTokenException e) {
                return;
            }
        }
        showError(LocalizationManager.getString(getActivity(), 2131166735));
    }

    protected void storeUserName(String userName, boolean isNewRegistration) {
        Context context = getActivity();
        if (context != null) {
            LoginScreenUtils.addSuccessfulLoginName(context, userName);
            Settings.storeStrValue(context, "login", userName);
        }
        String value = isNewRegistration ? "new_user" : "new_login";
        StatisticManager.getInstance().addStatisticEvent("registration", new Pair("login_type", value));
    }

    public void onUserCreationSuccesfull(String userId, boolean isPhoneAlreadyLogin, boolean isUserExists) {
    }

    public void onUserCreationError(String message, @NonNull ErrorType errorType) {
        logWorkflowError();
        this.regainUserControl = null;
        hideSpinner();
        showError(errorType.getDefaultErrorMessage());
    }

    protected void setFeedbackButtonListener(TextView button) {
        button.setText(LocalizationManager.getString(button.getContext(), (this.isFeedbackEnabled ? Page.FeedBack : Page.Faq).titleResId));
        button.setOnClickListener(new C10621());
    }

    protected RegistrationWorkflowSource getWorkflowSource() {
        return RegistrationWorkflowLogHelper.getWorkflowSource(getClass());
    }

    protected void logWorkflow(Outcome outcome) {
        RegistrationWorkflowLogHelper.log(getWorkflowSource(), outcome);
    }

    protected void logWorkflowError() {
        logWorkflow(Outcome.failure);
    }

    protected void logWorkflowSuccess() {
        logWorkflow(Outcome.success);
    }
}
