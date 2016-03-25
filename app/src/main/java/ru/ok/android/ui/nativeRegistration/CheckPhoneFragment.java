package ru.ok.android.ui.nativeRegistration;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import java.util.ArrayList;
import ru.mail.libverify.api.VerificationApi$VerificationStateChangedListener;
import ru.mail.libverify.api.VerificationApi.FailReason;
import ru.mail.libverify.api.VerificationApi.IvrStateListener;
import ru.mail.libverify.api.VerificationApi.VerificationSource;
import ru.mail.libverify.api.VerificationApi.VerificationState;
import ru.mail.libverify.api.VerificationApi.VerificationStateDescriptor;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.model.UserWithLogin;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.services.processors.registration.CheckPhoneProcessor;
import ru.ok.android.ui.activity.LoginScreenUtils;
import ru.ok.android.utils.LibverifyUtil;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.SmsCodeUtils;
import ru.ok.android.utils.SmsCodeUtils.SmsCodeReceiverListener;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.controls.nativeregistration.CheckPhoneControl;
import ru.ok.android.utils.controls.nativeregistration.GetExistingUserBySmsControl;
import ru.ok.android.utils.controls.nativeregistration.OnCheckPhoneListener;
import ru.ok.android.utils.controls.nativeregistration.OnGetExistingUserBySmsListener;
import ru.ok.android.utils.controls.nativeregistration.RegistrationControl;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.onelog.builtin.DurationInterval;
import ru.ok.onelog.builtin.Outcome;
import ru.ok.onelog.registration.ReceivedSmsCodeReadEventFactory;
import ru.ok.onelog.registration.RegistrationDurationFactory;
import ru.ok.onelog.registration.RegistrationDurationSource;
import ru.ok.onelog.registration.RegistrationWorkflowSource;

public class CheckPhoneFragment extends PinFragment implements VerificationApi$VerificationStateChangedListener, SmsCodeReceiverListener, OnCheckPhoneListener, OnGetExistingUserBySmsListener {
    private final String KEY_ALLOW_GET_NEW_SMS;
    private final String KEY_COUNTDOWN_VALUE;
    private final String KEY_NEW_SMS_REQUESTED;
    private final String KEY_SMS_CODE_VERIFIED;
    private boolean allowGetNewSms;
    private FrameLayout buttonContainer;
    private View cancel;
    private View countDownContainer;
    private TextView countDownText;
    private CountDownTimer countDownTimer;
    private long countdownValue;
    private String countryCode;
    private TextView feedbackButton;
    private GetExistingUserBySmsControl getExistingUserBySmsControl;
    private Button goButton;
    private boolean isAccountRecovery;
    private boolean isIvrAvailable;
    private boolean isPhoneAlreadyLogin;
    private boolean isSmsCodeAutomaticallyPlaced;
    private boolean isSmsCodeAutomaticallyReadAfterWrong;
    private boolean isSmsCodeParsingResultAlreadyLogged;
    private boolean loading;
    private boolean newSmsAlreadyRequested;
    private String phone;
    private TextView phoneTxt;
    private long pinTimeout;
    private EditText pinTxt;
    private View progress;
    private boolean smsCodeAlreadyVerified;
    private boolean smsCodeVerificationStarted;
    private long smsRequestTime;
    private State state;
    private String tempCode;
    Handler uiHandler;
    private String userId;
    private View view;

    /* renamed from: ru.ok.android.ui.nativeRegistration.CheckPhoneFragment.1 */
    class C10651 implements Runnable {
        final /* synthetic */ String val$sessionId;
        final /* synthetic */ VerificationStateDescriptor val$state;

        C10651(String str, VerificationStateDescriptor verificationStateDescriptor) {
            this.val$sessionId = str;
            this.val$state = verificationStateDescriptor;
        }

        public void run() {
            if (!TextUtils.equals(this.val$sessionId, LibverifyUtil.getSessionId(CheckPhoneFragment.this.getContext()))) {
                return;
            }
            if (this.val$state == null) {
                LibverifyUtil.resetSessionId(CheckPhoneFragment.this.getContext());
                return;
            }
            switch (C10727.f112x7fc70e95[this.val$state.getState().ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    if (this.val$state.getReason() == FailReason.INCORRECT_SMS_CODE) {
                        CheckPhoneFragment.this.changeState(State.GET_PIN);
                        CheckPhoneFragment.this.hideSpinner();
                        LibverifyUtil.getVerificationApi(CheckPhoneFragment.this.getContext()).resetVerificationCodeError(LibverifyUtil.getSessionId(CheckPhoneFragment.this.getContext()));
                        if (!CheckPhoneFragment.this.checkSmsCodeAfterError()) {
                            CheckPhoneFragment.this.showError(LocalizationManager.getString(CheckPhoneFragment.this.getContext(), 2131165844));
                        }
                    } else if (CheckPhoneFragment.this.smsCodeVerificationStarted) {
                        CheckPhoneFragment.this.showSpinner();
                        CheckPhoneFragment.this.smsCodeVerificationStarted = false;
                        LibverifyUtil.verifySmsCode(CheckPhoneFragment.this.getContext(), CheckPhoneFragment.this.pin);
                    } else if (this.val$state.getSource() != null && this.val$state.getSource() == VerificationSource.UNKNOWN) {
                        CheckPhoneFragment.this.hideSpinner();
                    }
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    if (this.val$state.getSmsCodeInfo() != null && !TextUtils.isEmpty(this.val$state.getSmsCodeInfo().receivedSmsCode) && this.val$state.getSource() != VerificationSource.USER_INPUT) {
                        CheckPhoneFragment.this.onCodeReceived(this.val$state.getSmsCodeInfo().receivedSmsCode);
                    }
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    if (this.val$state.getReason() == FailReason.OK) {
                        if (!(this.val$state.getSource() == null || this.val$state.getSource() == VerificationSource.USER_INPUT)) {
                            CheckPhoneFragment.this.onCodeReceived(this.val$state.getSmsCodeInfo().receivedSmsCode);
                        }
                        CheckPhoneFragment.this.smsCodeAlreadyVerified = true;
                        CheckPhoneFragment.this.processRegistrationWithLibVerify(this.val$sessionId, this.val$state.getToken(), CheckPhoneFragment.this.getLogin());
                        return;
                    }
                    CheckPhoneFragment.this.logWorkflowError();
                    CheckPhoneFragment.this.hideSpinner();
                    CheckPhoneFragment.this.showError(this.val$state.getReason().getDescription());
                    LibverifyUtil.cancelVerification(CheckPhoneFragment.this.getContext());
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.CheckPhoneFragment.2 */
    class C10662 extends CountDownTimer {
        C10662(long x0, long x1) {
            super(x0, x1);
        }

        public void onTick(long l) {
            CheckPhoneFragment.this.countdownValue = l;
            CheckPhoneFragment.this.countDownText.setText(CheckPhoneFragment.this.getCountDownText(l));
            CheckPhoneFragment.this.phoneTxt.requestFocus();
        }

        public void onFinish() {
            CheckPhoneFragment.this.countdownValue = 0;
            CheckPhoneFragment.this.allowGetNewSms = true;
            CheckPhoneFragment.this.countDownContainer.setVisibility(8);
            CheckPhoneFragment.this.buttonContainer.setVisibility(0);
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.CheckPhoneFragment.3 */
    class C10673 implements OnClickListener {
        C10673() {
        }

        public void onClick(View view) {
            CheckPhoneFragment.this.getActivity().onBackPressed();
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.CheckPhoneFragment.4 */
    class C10684 implements TextWatcher {
        C10684() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            CheckPhoneFragment.this.hideError();
            if (charSequence.length() == 0) {
                CheckPhoneFragment.this.changeState(State.GET_PIN);
            } else {
                CheckPhoneFragment.this.changeState(State.CHECK_PHONE);
            }
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.CheckPhoneFragment.5 */
    class C10695 implements OnClickListener {
        C10695() {
        }

        public void onClick(View view) {
            if (CheckPhoneFragment.this.state == State.CHECK_PHONE) {
                CheckPhoneFragment.this.sendConfirmation();
                return;
            }
            CheckPhoneFragment.this.pinTxt.getText().clear();
            CheckPhoneFragment.this.getNewCode();
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.CheckPhoneFragment.6 */
    class C10716 implements IvrStateListener {

        /* renamed from: ru.ok.android.ui.nativeRegistration.CheckPhoneFragment.6.1 */
        class C10701 implements Runnable {
            final /* synthetic */ FailReason val$result;

            C10701(FailReason failReason) {
                this.val$result = failReason;
            }

            public void run() {
                CheckPhoneFragment.this.hideSpinner();
                if (this.val$result != FailReason.OK) {
                    CheckPhoneFragment.this.showError(this.val$result.toString());
                }
            }
        }

        C10716() {
        }

        public void onRequestExecuted(FailReason result) {
            CheckPhoneFragment.this.uiHandler.post(new C10701(result));
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.CheckPhoneFragment.7 */
    static /* synthetic */ class C10727 {
        static final /* synthetic */ int[] f112x7fc70e95;
        static final /* synthetic */ int[] f113x6033cc23;

        static {
            f113x6033cc23 = new int[State.values().length];
            try {
                f113x6033cc23[State.CHECK_PHONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f113x6033cc23[State.GET_PIN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            f112x7fc70e95 = new int[VerificationState.values().length];
            try {
                f112x7fc70e95[VerificationState.WAITING_FOR_SMS_CODE.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f112x7fc70e95[VerificationState.VERIFYING_SMS_CODE.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f112x7fc70e95[VerificationState.FINAL.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public static class GoToCheckPhoneBundleBuilder {
        private String countryCode;
        private boolean isAccountRecovery;
        private boolean isIvrAvailable;
        private boolean isPhoneAlreadyLogin;
        private String phone;
        private long pinTimeout;
        private long smsRequestTime;
        private String userId;

        public GoToCheckPhoneBundleBuilder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public GoToCheckPhoneBundleBuilder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public GoToCheckPhoneBundleBuilder setCountryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public GoToCheckPhoneBundleBuilder setPinTimeout(long pinTimeout) {
            this.pinTimeout = pinTimeout;
            return this;
        }

        public GoToCheckPhoneBundleBuilder setIsAccountRecovery(boolean isAccountRecovery) {
            this.isAccountRecovery = isAccountRecovery;
            return this;
        }

        public GoToCheckPhoneBundleBuilder setIsPhoneAlreadyLogin(boolean isPhoneAlreadyLogin) {
            this.isPhoneAlreadyLogin = isPhoneAlreadyLogin;
            return this;
        }

        public GoToCheckPhoneBundleBuilder setSmsRequestTime(long smsRequestTime) {
            this.smsRequestTime = smsRequestTime;
            return this;
        }

        public GoToCheckPhoneBundleBuilder setIsIvrAvailable(boolean isIvrAvailable) {
            this.isIvrAvailable = isIvrAvailable;
            return this;
        }

        public Bundle build() {
            Bundle args = new Bundle();
            args.putString("uid", this.userId);
            args.putString("phone", this.phone);
            args.putString("code", this.countryCode);
            args.putBoolean("account_recovery", this.isAccountRecovery);
            args.putBoolean("phone_already_login", this.isPhoneAlreadyLogin);
            args.putBoolean("ivr_available", this.isIvrAvailable);
            args.putLong("sms_request_time", this.smsRequestTime);
            args.putLong("pinTimeout", this.pinTimeout);
            return args;
        }
    }

    enum State {
        GET_PIN,
        CHECK_PHONE
    }

    public CheckPhoneFragment() {
        this.KEY_ALLOW_GET_NEW_SMS = "allowGetNewSms";
        this.KEY_COUNTDOWN_VALUE = "countdownValue";
        this.KEY_SMS_CODE_VERIFIED = "smsCodeVerified";
        this.KEY_NEW_SMS_REQUESTED = "newSmsRequested";
        this.uiHandler = new Handler(Looper.getMainLooper());
    }

    protected RegistrationWorkflowSource getWorkflowSource() {
        if (AuthorizationPreferences.getLibVerifyEnabled()) {
            return RegistrationWorkflowSource.libv_enter_code;
        }
        return RegistrationWorkflowSource.enter_code;
    }

    public void onCodeReceived(String smsCode) {
        if (this.goButton.isClickable() || this.buttonContainer.getVisibility() == 8) {
            populateSmsCode(smsCode);
            this.isSmsCodeAutomaticallyPlaced = true;
            logSmsCodeEventDuration(RegistrationDurationSource.sms_code_reading);
            sendConfirmation();
            return;
        }
        this.tempCode = smsCode;
        this.isSmsCodeAutomaticallyReadAfterWrong = true;
    }

    public void onGetExistingUserError(String message, @NonNull ErrorType errorType) {
        logWorkflowError();
        changeState(State.GET_PIN);
        hideSpinner();
        if (errorType == ErrorType.SMS_CODE_WRONG) {
            showError(LocalizationManager.getString(getActivity(), 2131165844));
            logSmsParsingResult(false);
            return;
        }
        showError(errorType.getDefaultErrorMessage());
    }

    public void onGetExistingUserSuccessful(UserWithLogin user) {
        this.smsCodeAlreadyVerified = true;
        logSmsParsingResult(true);
        hideSpinner();
        this.communicationInterface.goToExistingUser(user, this.countryCode, this.phone, this.pin);
    }

    public void onLoginSuccessful(String url, String verificationUrl) {
        if (AuthorizationPreferences.getPasswordObligatoryBeforeProfile()) {
            storeUserName(getLogin(), true);
            this.communicationInterface.goToEnterPassword(getPin());
            return;
        }
        super.onLoginSuccessful(url, verificationUrl);
    }

    public void onStateChanged(@NonNull String sessionId, VerificationStateDescriptor state) {
        this.uiHandler.post(new C10651(sessionId, state));
    }

    @Subscribe(on = 2131623946, to = 2131624239)
    public void onRegisterWithLibVerifyResult(BusEvent event) {
        onRegisterWithLibVerifyResult(event, this.countryCode, this.phone);
    }

    public void onResume() {
        super.onResume();
        GlobalBus.register(this);
        LibverifyUtil.addVerificationStateChangedListener(getContext(), this);
    }

    public void onPause() {
        super.onPause();
        GlobalBus.unregister(this);
        LibverifyUtil.removeVerificationStateChangedListener(getContext(), this);
    }

    private void changeState(State state) {
        this.state = state;
        switch (C10727.f113x6033cc23[state.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.goButton.setText(LocalizationManager.getString(getActivity(), 2131165631));
                this.buttonContainer.setVisibility(0);
                this.countDownContainer.setVisibility(8);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                if (AuthorizationPreferences.getLibVerifyEnabled() && canUseIvrCall()) {
                    this.goButton.setText(LocalizationManager.getString(getContext(), 2131165464));
                } else {
                    this.goButton.setText(LocalizationManager.getString(getActivity(), 2131165901));
                }
                if (this.allowGetNewSms) {
                    this.buttonContainer.setVisibility(0);
                    this.countDownContainer.setVisibility(8);
                    return;
                }
                this.buttonContainer.setVisibility(8);
                this.countDownContainer.setVisibility(0);
            default:
        }
    }

    private boolean canUseIvrCall() {
        return this.newSmsAlreadyRequested && this.isIvrAvailable;
    }

    protected void showInputError() {
        Utils.setViewBackgroundWithoutResettingPadding(this.pinTxt, 2130838301);
    }

    protected void hideInputError() {
        Utils.setViewBackgroundWithoutResettingPadding(this.pinTxt, 2130838300);
    }

    private String getCountDownText(long l) {
        Context context = getActivity();
        if (AuthorizationPreferences.getLibVerifyEnabled() && canUseIvrCall()) {
            return LocalizationManager.getString(context, 2131165465, Long.valueOf(l / 1000));
        }
        return LocalizationManager.getString(context, 2131165900, Long.valueOf(l / 1000));
    }

    private void restartCountDown() {
        this.countdownValue = this.pinTimeout;
        continueCountDown();
    }

    private void continueCountDown() {
        this.allowGetNewSms = false;
        this.countDownText.setText(getCountDownText(this.countdownValue));
        if (this.countDownTimer != null) {
            this.countDownTimer.cancel();
        }
        this.countDownTimer = new C10662(this.countdownValue, 1000);
        this.countDownTimer.start();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCommunicationInterface((CommunicationInterface) getActivity());
        this.view = LocalizationManager.inflate(getActivity(), 2130903126, container, false);
        setErrorTextView((TextView) this.view.findViewById(2131625281));
        this.pinTxt = (EditText) this.view.findViewById(2131624677);
        this.phoneTxt = (TextView) this.view.findViewById(2131624676);
        this.countDownContainer = this.view.findViewById(2131624681);
        this.countDownText = (TextView) this.view.findViewById(2131624682);
        this.feedbackButton = (TextView) this.view.findViewById(2131624812);
        TextView newPhoneCodeInfoText = (TextView) this.view.findViewById(2131624683);
        this.pinTimeout = getArguments().getLong("pinTimeout");
        if (this.pinTimeout == 0) {
            this.pinTimeout = 20000;
        }
        this.isIvrAvailable = getArguments().getBoolean("ivr_available");
        this.phone = getArguments().getString("phone");
        this.countryCode = getArguments().getString("code");
        this.smsRequestTime = getArguments().getLong("sms_request_time");
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            this.phoneTxt.setText(phoneUtil.format(phoneUtil.parse(getLogin(), phoneUtil.getRegionCodeForCountryCode(Integer.parseInt(this.countryCode)).toUpperCase()), PhoneNumberFormat.INTERNATIONAL));
        } catch (Exception e) {
            this.phoneTxt.setText(getLogin());
        }
        this.isAccountRecovery = getArguments().getBoolean("account_recovery");
        newPhoneCodeInfoText.setText(LocalizationManager.getString(getActivity(), 2131166307));
        if (!this.isAccountRecovery) {
            this.userId = getArguments().getString("uid");
            this.isPhoneAlreadyLogin = getArguments().getBoolean("phone_already_login");
        }
        this.buttonContainer = (FrameLayout) this.view.findViewById(2131624678);
        setErrorTextView((TextView) this.view.findViewById(2131625281));
        this.cancel = this.view.findViewById(C0263R.id.cancel);
        prepareProgress();
        initListeners();
        if (savedInstanceState != null) {
            this.allowGetNewSms = savedInstanceState.getBoolean("allowGetNewSms", false);
            this.smsCodeAlreadyVerified = savedInstanceState.getBoolean("smsCodeVerified", false);
            this.newSmsAlreadyRequested = savedInstanceState.getBoolean("newSmsRequested", false);
            this.countdownValue = savedInstanceState.getLong("countdownValue", 0);
            if (this.countdownValue != 0) {
                continueCountDown();
            }
        } else {
            restartCountDown();
        }
        if (this.smsCodeAlreadyVerified) {
            this.pinTxt.setEnabled(false);
        }
        changeState(State.GET_PIN);
        return this.view;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("allowGetNewSms", this.allowGetNewSms);
        outState.putLong("countdownValue", this.countdownValue);
        outState.putBoolean("smsCodeVerified", this.smsCodeAlreadyVerified);
        outState.putBoolean("newSmsRequested", this.newSmsAlreadyRequested);
    }

    public void onStart() {
        super.onStart();
        if (PermissionUtils.checkAnySelfPermission(getContext(), "android.permission.RECEIVE_SMS", "android.permission.READ_SMS") == 0) {
            SmsCodeUtils.registerSmsCodeReceiver(getActivity(), this);
        }
    }

    private void prepareProgress() {
        this.goButton = (Button) this.view.findViewById(2131624679);
        this.progress = this.view.findViewById(2131624680);
    }

    public void onStop() {
        super.onStop();
        SmsCodeUtils.unregisterSmsCodeReceiver(getActivity());
    }

    private void enableButton() {
        if (!this.loading) {
            this.goButton.setAlpha(1.0f);
            this.goButton.setClickable(true);
            this.cancel.setClickable(true);
        }
    }

    private void disableButton() {
        this.goButton.setAlpha(0.4f);
        this.goButton.setClickable(false);
        this.cancel.setClickable(false);
    }

    private void initListeners() {
        this.cancel.setOnClickListener(new C10673());
        this.pinTxt.addTextChangedListener(new C10684());
        this.goButton.setOnClickListener(new C10695());
        setFeedbackButtonListener(this.feedbackButton);
    }

    private void logSmsCodeEventDuration(RegistrationDurationSource source) {
        if (this.smsRequestTime > 0) {
            OneLog.log(RegistrationDurationFactory.get(DurationInterval.valueOfMillis(SystemClock.elapsedRealtime() - this.smsRequestTime), source));
            this.smsRequestTime = 0;
        }
    }

    private void sendConfirmation() {
        showSpinner();
        this.pin = this.pinTxt.getText().toString().trim();
        if (AuthorizationPreferences.getLibVerifyEnabled()) {
            this.smsCodeVerificationStarted = true;
            LibverifyUtil.requestVerificationState(getContext(), this);
        } else if (this.isAccountRecovery) {
            if (StringUtils.isEmpty(this.pin)) {
                hideSpinner();
                showError(LocalizationManager.getString(getActivity(), 2131165788));
                return;
            }
            hideError();
            logSmsCodeEventDuration(RegistrationDurationSource.sms_code_using);
            this.getExistingUserBySmsControl = new GetExistingUserBySmsControl();
            this.getExistingUserBySmsControl.getExistingUserBySms(getLogin(), getPin(), this);
        } else if (isActivityGotLocations()) {
            hideError();
            this.communicationInterface.goToUpdateUserInfo();
        } else if (!isLoginStarted()) {
            if (StringUtils.isEmpty(this.pin)) {
                hideSpinner();
                showError(LocalizationManager.getString(getActivity(), 2131165788));
                return;
            }
            logSmsCodeEventDuration(RegistrationDurationSource.sms_code_using);
            this.checkPhoneControl = new CheckPhoneControl();
            this.checkPhoneControl.tryToCheckPhone(this.userId, getLogin(), this.pin, this);
        }
    }

    protected void showSpinner() {
        hideError();
        this.loading = true;
        this.pinTxt.setEnabled(false);
        disableButton();
        this.progress.setVisibility(0);
    }

    protected String getLogin() {
        return this.countryCode + this.phone;
    }

    protected void hideSpinner() {
        this.loading = false;
        this.pinTxt.setEnabled(true);
        enableButton();
        this.progress.setVisibility(8);
    }

    public void populateSmsCode(String message) {
        this.pinTxt.setText(message);
    }

    private void requestIvrCall() {
        if (this.isIvrAvailable) {
            LibverifyUtil.requestIvrCall(getContext(), new C10716());
        }
    }

    public void getNewCode() {
        this.isSmsCodeAutomaticallyPlaced = false;
        this.isSmsCodeParsingResultAlreadyLogged = false;
        this.isSmsCodeAutomaticallyReadAfterWrong = false;
        this.pin = null;
        this.smsRequestTime = SystemClock.elapsedRealtime();
        showSpinner();
        if (!AuthorizationPreferences.getLibVerifyEnabled()) {
            this.registrationControl = new RegistrationControl();
            this.registrationControl.tryToRegisterUser(getLogin(), this);
        } else if (canUseIvrCall()) {
            requestIvrCall();
        } else {
            this.newSmsAlreadyRequested = true;
            LibverifyUtil.requestNewSmsCode(getContext());
        }
        restartCountDown();
        changeState(State.GET_PIN);
    }

    public void onUserCreationSuccesfull(String userId, boolean isPhoneAlreadyLogin, boolean isUserExists) {
        this.registrationControl = null;
        this.userId = userId;
        this.isPhoneAlreadyLogin = isPhoneAlreadyLogin;
        this.isAccountRecovery = isUserExists;
        hideSpinner();
    }

    private void logSmsParsingResult(boolean isCodeOrPasswordCorrect) {
        if (!this.isSmsCodeParsingResultAlreadyLogged) {
            OneLog.log(ReceivedSmsCodeReadEventFactory.get(Outcome.successIf(this.isSmsCodeAutomaticallyPlaced), Outcome.successIf(isCodeOrPasswordCorrect)));
            this.isSmsCodeParsingResultAlreadyLogged = true;
        }
    }

    public void onCheckPhoneSuccessfull(Bundle bundle) {
        this.smsCodeAlreadyVerified = true;
        logSmsParsingResult(true);
        this.checkPhoneControl = null;
        ArrayList<UserWithLogin> userInfos = (ArrayList) bundle.getSerializable(CheckPhoneProcessor.KEY_USER_LIST);
        if (userInfos == null || userInfos.size() <= 0) {
            String token = bundle.getString(CheckPhoneProcessor.KEY_USER_TOKEN);
            setToken(token);
            LoginScreenUtils.performLoginByToken(token, this);
            return;
        }
        this.communicationInterface.goToUserList(this.userId, getLogin(), this.pin, userInfos, this.isPhoneAlreadyLogin);
    }

    public void onCheckPhoneError(String message, @NonNull ErrorType errorType) {
        logWorkflowError();
        this.checkPhoneControl = null;
        if (errorType == ErrorType.SMS_CODE_WRONG) {
            logSmsParsingResult(false);
            if (!checkSmsCodeAfterError()) {
                showError(LocalizationManager.getString(getActivity(), 2131165844));
            } else {
                return;
            }
        } else if (errorType == ErrorType.USER_EXISTS) {
            this.communicationInterface.goToEnterNewLogin(this.userId, getPin());
        } else {
            showError(errorType.getDefaultErrorMessage());
        }
        changeState(State.GET_PIN);
        hideSpinner();
    }

    private boolean checkSmsCodeAfterError() {
        if (!this.isSmsCodeAutomaticallyReadAfterWrong || this.isSmsCodeAutomaticallyPlaced) {
            return false;
        }
        this.isSmsCodeParsingResultAlreadyLogged = false;
        this.isSmsCodeAutomaticallyPlaced = true;
        populateSmsCode(this.tempCode);
        sendConfirmation();
        return true;
    }
}
