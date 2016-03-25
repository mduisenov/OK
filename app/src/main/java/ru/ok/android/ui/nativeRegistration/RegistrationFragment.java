package ru.ok.android.ui.nativeRegistration;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import ru.mail.libverify.api.VerificationApi$PhoneCheckListener;
import ru.mail.libverify.api.VerificationApi$VerificationStateChangedListener;
import ru.mail.libverify.api.VerificationApi.FailReason;
import ru.mail.libverify.api.VerificationApi.PhoneAccountSearchItem;
import ru.mail.libverify.api.VerificationApi.PhoneAccountSearchListener;
import ru.mail.libverify.api.VerificationApi.PhoneCheckResult;
import ru.mail.libverify.api.VerificationApi.PhoneNumberCheckSession;
import ru.mail.libverify.api.VerificationApi.VerificationState;
import ru.mail.libverify.api.VerificationApi.VerificationStateDescriptor;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.GoogleInfoService;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.services.utils.users.LocationUtils;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.activity.main.ActivityExecutor;
import ru.ok.android.ui.nativeRegistration.CountryCodeListFragment.OnCountrySelectionListener;
import ru.ok.android.utils.CountryUtil;
import ru.ok.android.utils.CountryUtil.Country;
import ru.ok.android.utils.LibverifyUtil;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper.FragmentLocation;
import ru.ok.android.utils.PermissionUtils;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.controls.nativeregistration.RegistrationControl;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;
import ru.ok.onelog.builtin.Outcome;
import ru.ok.onelog.registration.NumberGettingFromPhoneFactory;
import ru.ok.onelog.registration.PhoneValidationEventFactory;
import ru.ok.onelog.registration.RegistrationWorkflowSource;

public class RegistrationFragment extends PinFragment implements VerificationApi$PhoneCheckListener, VerificationApi$VerificationStateChangedListener, OnCountrySelectionListener {
    Button buttonText;
    private String countryCode;
    TextView countryCodeField;
    private Country currentCountry;
    private String existingUserPhoneNumber;
    private TextView feedbackButton;
    private boolean loading;
    FrameLayout mCreateUserBtn;
    EditText mPhoneNumberField;
    boolean permissionAlreadyAsked;
    private String phoneNumber;
    private PhoneNumberCheckSession phoneNumberCheckSession;
    PhoneNumberUtil phoneUtil;
    private View progress;
    private long smsRequestTime;
    Handler uiHandler;
    private TextView userAgreementTextView;

    /* renamed from: ru.ok.android.ui.nativeRegistration.RegistrationFragment.1 */
    class C11011 implements OnClickListener {
        C11011() {
        }

        public void onClick(View v) {
            new ActivityExecutor(RegistrationFragment.this.getActivity(), UserAgreementFragment.class).setFragmentLocation(FragmentLocation.center).setActionBarVisible(true).setNeedToolbar(false).setTallTitle(LocalizationManager.getString(RegistrationFragment.this.getActivity(), 2131166786)).execute();
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.RegistrationFragment.2 */
    class C11022 implements TextWatcher {
        C11022() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            String countryCode = String.valueOf(RegistrationFragment.this.currentCountry.getZip());
            if ((countryCode + RegistrationFragment.this.mPhoneNumberField.getText().toString()).equals(RegistrationFragment.this.existingUserPhoneNumber)) {
                RegistrationFragment.this.showError(2131166308);
            } else {
                RegistrationFragment.this.hideError();
            }
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.RegistrationFragment.3 */
    class C11033 implements OnClickListener {
        C11033() {
        }

        public void onClick(View view) {
            String[] permissionsToRequest = AuthorizationPreferences.filterNotGrantedPermissions(RegistrationFragment.this.getContext(), AuthorizationPreferences.getRequestSmsPermissionArrayBeforeUsage());
            if (RegistrationFragment.this.permissionAlreadyAsked || permissionsToRequest.length <= 0) {
                RegistrationFragment.this.checkPhoneIfValid();
                return;
            }
            RegistrationFragment.this.permissionAlreadyAsked = true;
            RegistrationFragment.this.requestPermissions(permissionsToRequest, 3);
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.RegistrationFragment.4 */
    class C11044 implements OnClickListener {
        C11044() {
        }

        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("code", RegistrationFragment.this.currentCountry);
            new ActivityExecutor(RegistrationFragment.this.getActivity(), CountryCodeListFragment.class).setTallTitle(LocalizationManager.getString(RegistrationFragment.this.getActivity(), 2131165647)).setArguments(bundle).setRequestCode(2).setResultFragment(RegistrationFragment.this).execute();
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.RegistrationFragment.5 */
    class C11065 implements PhoneAccountSearchListener {

        /* renamed from: ru.ok.android.ui.nativeRegistration.RegistrationFragment.5.1 */
        class C11051 implements Runnable {
            final /* synthetic */ List val$accounts;

            C11051(List list) {
                this.val$accounts = list;
            }

            public void run() {
                if (this.val$accounts.size() > 0) {
                    String phone = ((PhoneAccountSearchItem) this.val$accounts.get(0)).phone;
                    if (!phone.startsWith("+")) {
                        phone = "+" + phone;
                    }
                    try {
                        PhoneNumber numberProto = RegistrationFragment.this.phoneUtil.parse(phone, "");
                        int countryCode = numberProto.getCountryCode();
                        String nationalNumber = String.valueOf(numberProto.getNationalNumber());
                        RegistrationFragment.this.currentCountry = CountryUtil.getInstance().getCountryByZip(countryCode);
                        if (RegistrationFragment.this.currentCountry != null) {
                            RegistrationFragment.this.mPhoneNumberField.setText(nationalNumber);
                            RegistrationFragment.this.countryCodeField.setText("+" + String.valueOf(RegistrationFragment.this.currentCountry.getZip()));
                            OneLog.log(NumberGettingFromPhoneFactory.get(Outcome.successIf(true)));
                        }
                    } catch (NumberParseException e) {
                        OneLog.log(NumberGettingFromPhoneFactory.get(Outcome.successIf(false)));
                    }
                }
            }
        }

        C11065() {
        }

        public void onComplete(@NonNull List<PhoneAccountSearchItem> accounts) {
            RegistrationFragment.this.uiHandler.post(new C11051(accounts));
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.RegistrationFragment.6 */
    class C11076 implements Runnable {
        final /* synthetic */ String val$sessionId;
        final /* synthetic */ VerificationStateDescriptor val$state;

        C11076(String str, VerificationStateDescriptor verificationStateDescriptor) {
            this.val$sessionId = str;
            this.val$state = verificationStateDescriptor;
        }

        public void run() {
            if (!TextUtils.equals(this.val$sessionId, LibverifyUtil.getSessionId(RegistrationFragment.this.getContext()))) {
                return;
            }
            if (this.val$state == null) {
                LibverifyUtil.restartVerification(RegistrationFragment.this.getContext(), RegistrationFragment.this.phoneNumber);
                return;
            }
            switch (C11098.f116x7fc70e95[this.val$state.getState().ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    boolean isIvrAvailable = false;
                    long pinTimeout = 0;
                    if (this.val$state.getIvrInfo() != null) {
                        pinTimeout = (long) (this.val$state.getIvrInfo().ivrTimeoutSec * LocationStatusCodes.GEOFENCE_NOT_AVAILABLE);
                        isIvrAvailable = RegistrationFragment.this.isIvrAvailableForCurrentLanguage(this.val$state.getIvrInfo().supportedIvrLanguages);
                    }
                    RegistrationFragment.this.communicationInterface.goToCheckPhone(RegistrationFragment.this.countryCode, RegistrationFragment.this.phoneNumber, pinTimeout, isIvrAvailable, RegistrationFragment.this.smsRequestTime);
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    if (this.val$state.getReason() == FailReason.OK) {
                        RegistrationFragment.this.processRegistrationWithLibVerify(this.val$sessionId, this.val$state.getToken(), RegistrationFragment.this.getLogin());
                        return;
                    }
                    RegistrationFragment.this.hideSpinner();
                    RegistrationFragment.this.showError(this.val$state.getReason().getDescription());
                    LibverifyUtil.cancelVerification(RegistrationFragment.this.getContext());
                default:
            }
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.RegistrationFragment.7 */
    class C11087 implements Runnable {
        final /* synthetic */ PhoneCheckResult val$result;

        C11087(PhoneCheckResult phoneCheckResult) {
            this.val$result = phoneCheckResult;
        }

        public void run() {
            if (this.val$result.getReason() == FailReason.OK) {
                boolean isPhoneNumberValid = this.val$result.isValid() && this.val$result.getExtendedInfo().isMobile;
                if (isPhoneNumberValid) {
                    RegistrationFragment.this.checkPhone();
                    return;
                }
                StringBuilder builder = new StringBuilder();
                if (this.val$result.getPrintableText() != null) {
                    for (String str : this.val$result.getPrintableText()) {
                        if (builder.length() > 0) {
                            builder.append('\n');
                        }
                        builder.append(StringUtils.uppercaseFirst(str)).append('.');
                    }
                }
                if (builder.length() > 0) {
                    RegistrationFragment.this.showError(builder.toString());
                } else {
                    RegistrationFragment.this.showError(LocalizationManager.getString(RegistrationFragment.this.getContext(), 2131165847));
                }
                RegistrationFragment.this.hideSpinner();
                return;
            }
            RegistrationFragment.this.hideSpinner();
            RegistrationFragment.this.showError(this.val$result.getReason().toString());
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.RegistrationFragment.8 */
    static /* synthetic */ class C11098 {
        static final /* synthetic */ int[] f116x7fc70e95;

        static {
            f116x7fc70e95 = new int[VerificationState.values().length];
            try {
                f116x7fc70e95[VerificationState.WAITING_FOR_SMS_CODE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f116x7fc70e95[VerificationState.FINAL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public RegistrationFragment() {
        this.phoneUtil = PhoneNumberUtil.getInstance();
        this.uiHandler = new Handler(Looper.getMainLooper());
    }

    private boolean isNumberValid(String number, String code) {
        boolean isPhoneNumberValid = false;
        try {
            isPhoneNumberValid = this.phoneUtil.isValidNumber(this.phoneUtil.parse(code + number, this.phoneUtil.getRegionCodeForCountryCode(Integer.parseInt(code)).toUpperCase()));
        } catch (Throwable e) {
            Logger.m178e(e);
        }
        OneLog.log(PhoneValidationEventFactory.get(Outcome.successIf(isPhoneNumberValid)));
        return isPhoneNumberValid;
    }

    protected void showSpinner() {
        this.loading = true;
        disableButton();
        this.progress.setVisibility(0);
    }

    protected String getLogin() {
        return this.countryCode + this.phoneNumber;
    }

    protected void hideInputError() {
        Utils.setViewBackgroundWithoutResettingPadding(this.countryCodeField, 2130837851);
        Utils.setViewBackgroundWithoutResettingPadding(this.mPhoneNumberField, 2130838300);
    }

    protected void showInputError() {
        Utils.setViewBackgroundWithoutResettingPadding(this.countryCodeField, 2130837852);
        Utils.setViewBackgroundWithoutResettingPadding(this.mPhoneNumberField, 2130838301);
    }

    public void hideSpinner() {
        this.loading = false;
        enableButton();
        this.progress.setVisibility(8);
    }

    private void initListeners() {
        this.userAgreementTextView.setOnClickListener(new C11011());
        this.mPhoneNumberField.addTextChangedListener(new C11022());
        this.mCreateUserBtn.setOnClickListener(new C11033());
        setFeedbackButtonListener(this.feedbackButton);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 3) {
            checkPhoneIfValid();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void checkPhoneIfValid() {
        showSpinner();
        this.countryCode = String.valueOf(this.currentCountry.getZip());
        this.phoneNumber = this.mPhoneNumberField.getText().toString();
        if (AuthorizationPreferences.getLibVerifyPhoneValidationEnabled()) {
            this.phoneNumberCheckSession.checkPhoneNumber(LibverifyUtil.VERIFICATION_SERVICE, "+" + this.countryCode + this.phoneNumber, true, this);
        } else if (isNumberValid(this.phoneNumber, this.countryCode)) {
            checkPhone();
        } else {
            hideSpinner();
            showError(((BaseActivity) getActivity()).getStringLocalized(2131165847));
        }
    }

    private void checkPhone() {
        hideError();
        this.communicationInterface.storeCountry(this.currentCountry);
        this.smsRequestTime = SystemClock.elapsedRealtime();
        if (AuthorizationPreferences.getLibVerifyEnabled()) {
            LibverifyUtil.restartVerification(getContext(), getLogin());
            return;
        }
        this.registrationControl = new RegistrationControl();
        this.registrationControl.tryToRegisterUser(getLogin(), this);
    }

    public void saveLastUsedPhoneNumber() {
        this.existingUserPhoneNumber = getLogin();
        this.mPhoneNumberField.getText().clear();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.phoneNumberCheckSession = new PhoneNumberCheckSession(LibverifyUtil.getVerificationApi(getContext()));
        setCommunicationInterface((CommunicationInterface) getActivity());
        View view = LocalizationManager.inflate(getActivity(), 2130903417, container, false);
        this.mCreateUserBtn = (FrameLayout) view.findViewById(2131625284);
        this.buttonText = (Button) view.findViewById(2131625286);
        this.mPhoneNumberField = (EditText) view.findViewById(2131625283);
        this.countryCodeField = (TextView) view.findViewById(2131625282);
        this.countryCodeField.setOnClickListener(new C11044());
        setErrorTextView((TextView) view.findViewById(2131625281));
        this.loading = false;
        this.progress = view.findViewById(2131624680);
        this.feedbackButton = (TextView) view.findViewById(2131624812);
        this.userAgreementTextView = (TextView) view.findViewById(2131625288);
        if (savedInstanceState != null) {
            this.currentCountry = (Country) savedInstanceState.getParcelable("CURRENT_COUNTRY");
            this.existingUserPhoneNumber = savedInstanceState.getString("EXISTING_USER_PHONE");
        }
        setUserAgreementLinkText();
        initListeners();
        prepareLoginField();
        return view;
    }

    private void setUserAgreementLinkText() {
        SpannableString spannableString = new SpannableString(LocalizationManager.getString(getActivity(), 2131166786) + ".");
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(2131493005)), spannableString.length() - 1, spannableString.length(), 17);
        spannableString.setSpan(new BackgroundColorSpan(-1), spannableString.length() - 1, spannableString.length(), 17);
        this.userAgreementTextView.setText(spannableString);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode == -1) {
            this.currentCountry = (Country) data.getParcelableExtra("code");
            onCountrySelected(this.currentCountry);
        }
    }

    public void onStart() {
        super.onStart();
        if (!AuthorizationPreferences.getGoogleInfoThroughOAuth()) {
            getActivity().startService(new Intent(getActivity(), GoogleInfoService.class));
        }
    }

    private void enableButton() {
        if (!this.loading) {
            this.mCreateUserBtn.setAlpha(1.0f);
            this.mCreateUserBtn.setClickable(true);
        }
    }

    private void disableButton() {
        this.mCreateUserBtn.setAlpha(0.4f);
        this.mCreateUserBtn.setClickable(false);
    }

    private void prepareLoginField() {
        String login = null;
        TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService("phone");
        if (this.currentCountry == null) {
            String countryID = tMgr.getSimCountryIso().toUpperCase().trim();
            if (StringUtils.isEmpty(countryID)) {
                this.currentCountry = getCurrentCountryFromLocation();
            } else {
                this.currentCountry = CountryUtil.getInstance().getCountryByIso(countryID);
                if (PermissionUtils.checkAnySelfPermission(getActivity(), "android.permission.READ_SMS", "android.permission.READ_PHONE_STATE") == 0) {
                    login = tMgr.getLine1Number();
                    if (!(login == null || this.currentCountry == null)) {
                        String zip = String.valueOf(this.currentCountry.getZip());
                        int countryCodeLength = 0;
                        if (login.startsWith(zip)) {
                            countryCodeLength = zip.length();
                        } else if (login.startsWith("+" + zip)) {
                            countryCodeLength = zip.length() + 1;
                        }
                        login = login.substring(countryCodeLength);
                    }
                }
            }
            if (this.currentCountry == null) {
                this.currentCountry = CountryUtil.getInstance().getCountryByIso("ru");
            } else if (!StringUtils.isEmpty(login)) {
                this.mPhoneNumberField.setText(login);
                OneLog.log(NumberGettingFromPhoneFactory.get(Outcome.successIf(true)));
            }
            if (this.currentCountry == null || StringUtils.isEmpty(login)) {
                fillPhoneNumberFromAccount();
            }
        }
        this.countryCodeField.setText("+" + String.valueOf(this.currentCountry.getZip()));
    }

    private Country getCurrentCountryFromLocation() {
        Country country = null;
        Location location = LocationUtils.getLastLocationIfPermitted(getActivity());
        if (location != null) {
            try {
                List<Address> addresses = new Geocoder(getActivity(), Locale.getDefault()).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!(addresses == null || addresses.isEmpty())) {
                    country = CountryUtil.getInstance().getCountryByIso(((Address) addresses.get(0)).getCountryCode());
                }
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }
        return country;
    }

    private void fillPhoneNumberFromAccount() {
        if (PermissionUtils.checkSelfPermission(getContext(), "android.permission.GET_ACCOUNTS", "android.permission.READ_PHONE_STATE") == 0) {
            LibverifyUtil.getVerificationApi(getContext()).searchPhoneAccounts(new C11065(), true);
        } else {
            OneLog.log(NumberGettingFromPhoneFactory.get(Outcome.successIf(false)));
        }
    }

    public void onUserCreationSuccesfull(String userId, boolean isPhoneAlreadyLogin, boolean isAccountRecovery) {
        this.registrationControl = null;
        hideSpinner();
        if (isAccountRecovery) {
            this.communicationInterface.goToCheckPhoneForRecovery(this.countryCode, this.phoneNumber, this.smsRequestTime);
            return;
        }
        this.communicationInterface.goToCheckPhone(userId, this.phoneNumber, this.countryCode, isPhoneAlreadyLogin, this.smsRequestTime);
    }

    public void onCountrySelected(Country country) {
        this.currentCountry = country;
        this.countryCodeField.setText("+" + String.valueOf(this.currentCountry.getZip()));
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("CURRENT_COUNTRY", this.currentCountry);
        outState.putString("EXISTING_USER_PHONE", this.existingUserPhoneNumber);
    }

    protected RegistrationWorkflowSource getWorkflowSource() {
        if (AuthorizationPreferences.getLibVerifyEnabled()) {
            if (StringUtils.isEmpty(this.existingUserPhoneNumber)) {
                return RegistrationWorkflowSource.libv_enter_phone;
            }
            if (this.existingUserPhoneNumber.equals(getLogin())) {
                return RegistrationWorkflowSource.libv_enter_phone_same;
            }
            return RegistrationWorkflowSource.libv_enter_phone_diff;
        } else if (StringUtils.isEmpty(this.existingUserPhoneNumber)) {
            return RegistrationWorkflowSource.enter_phone;
        } else {
            if (this.existingUserPhoneNumber.equals(getLogin())) {
                return RegistrationWorkflowSource.enter_phone_same;
            }
            return RegistrationWorkflowSource.enter_phone_diff;
        }
    }

    private boolean isIvrAvailableForCurrentLanguage(Collection<String> supportedIvrLanguages) {
        String currentLang = Settings.getCurrentLocale(getContext());
        for (String lang : supportedIvrLanguages) {
            if (lang.startsWith(currentLang)) {
                return true;
            }
        }
        return false;
    }

    public void onStateChanged(@NonNull String sessionId, VerificationStateDescriptor state) {
        this.uiHandler.post(new C11076(sessionId, state));
    }

    @Subscribe(on = 2131623946, to = 2131624239)
    public void onRegisterWithLibVerifyResult(BusEvent event) {
        onRegisterWithLibVerifyResult(event, this.countryCode, this.phoneNumber);
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

    public void onCompleted(@NonNull String phoneNumber, @NonNull PhoneCheckResult result) {
        this.uiHandler.post(new C11087(result));
    }
}
