package ru.ok.android.ui.nativeRegistration;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.ScrollView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Name;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.fragments.registr.NotLoggedInWebFragment.Page;
import ru.ok.android.model.UpdateProfileFieldsFlags;
import ru.ok.android.model.UserWithLogin;
import ru.ok.android.onelog.registration.RegistrationWorkflowLogHelper;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.services.processors.registration.Location;
import ru.ok.android.ui.NotLoggedInWebActivity;
import ru.ok.android.ui.activity.BaseNoToolbarActivity;
import ru.ok.android.ui.activity.main.LinksActivity;
import ru.ok.android.ui.nativeRegistration.CheckPhoneFragment.GoToCheckPhoneBundleBuilder;
import ru.ok.android.utils.CountryUtil;
import ru.ok.android.utils.CountryUtil.Country;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.LibverifyUtil;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.controls.nativeregistration.RegistrationConstants.EnterPasswordReason;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.onelog.builtin.Outcome;

public class NativeLoginActivity extends BaseNoToolbarActivity implements ConnectionCallbacks, OnConnectionFailedListener, CommunicationInterface {
    private static String STACK_REGISTRATION_TAG;
    private final String KEY_COUNTRY;
    private final String KEY_LOCATIONS;
    private final String KEY_OPENED_FOR_AUTHORIZATION;
    private final String KEY_PIN;
    private final String KEY_RESOLVE_ON_FAIL;
    private final String UPDATE_PROFILE_FIELDS_FLAGS;
    private Country country;
    private GoogleApiClient googleApiClient;
    private boolean isOpenedForAuthorization;
    private boolean keyboardAppeared;
    private ArrayList<Location> locations;
    private boolean loginFromWebRegistration;
    private String pin;
    private boolean resolveOnfail;
    private ScrollView rootView;
    private int stackRegistrationId;
    private UpdateProfileFieldsFlags updateProfileFieldsFlags;

    /* renamed from: ru.ok.android.ui.nativeRegistration.NativeLoginActivity.1 */
    class C10961 implements OnGlobalLayoutListener {

        /* renamed from: ru.ok.android.ui.nativeRegistration.NativeLoginActivity.1.1 */
        class C10951 implements Runnable {
            final /* synthetic */ View val$focusedView;

            C10951(View view) {
                this.val$focusedView = view;
            }

            public void run() {
                NativeLoginActivity.this.rootView.scrollTo(0, NativeLoginActivity.this.getTopRelativeToRoot(this.val$focusedView));
                if (this.val$focusedView instanceof EditText) {
                    EditText editText = this.val$focusedView;
                    int selectionStart = editText.getSelectionStart();
                    int selectionEnd = editText.getSelectionEnd();
                    editText.setText(editText.getText());
                    editText.setSelection(selectionStart, selectionEnd);
                }
            }
        }

        C10961() {
        }

        public void onGlobalLayout() {
            int heightDiff = NativeLoginActivity.this.rootView.getRootView().getHeight() - NativeLoginActivity.this.rootView.getHeight();
            View divider = NativeLoginActivity.this.rootView.findViewById(2131624602);
            if (((float) heightDiff) > TypedValue.applyDimension(1, 100.0f, NativeLoginActivity.this.getResources().getDisplayMetrics())) {
                if (!NativeLoginActivity.this.keyboardAppeared) {
                    NativeLoginActivity.this.keyboardAppeared = true;
                    View focusedView = NativeLoginActivity.this.rootView.findFocus();
                    if (focusedView != null) {
                        NativeLoginActivity.this.rootView.post(new C10951(focusedView));
                    }
                    divider.setVisibility(8);
                }
            } else if (NativeLoginActivity.this.keyboardAppeared) {
                NativeLoginActivity.this.keyboardAppeared = false;
                divider.setVisibility(0);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.NativeLoginActivity.2 */
    class C10972 extends AsyncTask {
        C10972() {
        }

        protected Object doInBackground(Object[] params) {
            CountryUtil.getInstance();
            return null;
        }
    }

    public NativeLoginActivity() {
        this.KEY_LOCATIONS = "locations";
        this.UPDATE_PROFILE_FIELDS_FLAGS = "updateProfileFieldsFlags";
        this.KEY_PIN = "pin";
        this.KEY_RESOLVE_ON_FAIL = "resolveOnFail";
        this.KEY_OPENED_FOR_AUTHORIZATION = "openedForAuthorization";
        this.KEY_COUNTRY = "country";
    }

    static {
        STACK_REGISTRATION_TAG = "tag_registration";
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pin", this.pin);
        outState.putParcelableArrayList("locations", this.locations);
        outState.putParcelable("updateProfileFieldsFlags", this.updateProfileFieldsFlags);
        outState.putBoolean("resolveOnFail", this.resolveOnfail);
        outState.putBoolean("openedForAuthorization", this.isOpenedForAuthorization);
        outState.putParcelable("country", this.country);
        outState.putInt(STACK_REGISTRATION_TAG, this.stackRegistrationId);
    }

    protected boolean startLoginIfNeeded() {
        return false;
    }

    protected void onStop() {
        super.onStop();
        if (this.googleApiClient != null && this.googleApiClient.isConnected()) {
            this.googleApiClient.disconnect();
        }
        hideKeyboard();
    }

    private int getTopRelativeToRoot(View myView) {
        if (myView.getParent() == myView.getRootView()) {
            return myView.getTop();
        }
        return getTopRelativeToRoot((View) myView.getParent()) + myView.getTop();
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        if (savedInstanceState != null) {
            this.pin = savedInstanceState.getString("pin");
            this.resolveOnfail = savedInstanceState.getBoolean("resolveOnFail");
            this.locations = savedInstanceState.getParcelableArrayList("locations");
            this.isOpenedForAuthorization = savedInstanceState.getBoolean("openedForAuthorization");
            this.updateProfileFieldsFlags = (UpdateProfileFieldsFlags) savedInstanceState.getParcelable("updateProfileFieldsFlags");
            this.country = (Country) savedInstanceState.getParcelable("country");
            this.stackRegistrationId = savedInstanceState.getInt(STACK_REGISTRATION_TAG);
        }
        this.googleApiClient = new Builder(this).addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        setContentView(2130903342);
        this.rootView = (ScrollView) findViewById(2131624582);
        this.rootView.getViewTreeObserver().addOnGlobalLayoutListener(new C10961());
        initPhoneUtils();
        if (getSupportFragmentManager().getFragments() == null) {
            Intent data = getIntent();
            if (data == null || !data.getBooleanExtra("registration", false)) {
                Fragment fragment = null;
                if (data != null) {
                    this.loginFromWebRegistration = data.getBooleanExtra("login_from_web_registration", false);
                    if (this.loginFromWebRegistration) {
                        fragment = new NewLoginFragment();
                        Bundle args = new Bundle();
                        args.putAll(data.getExtras());
                        fragment.setArguments(args);
                    } else {
                        this.isOpenedForAuthorization = data.getBooleanExtra("authorization", false);
                        if (this.isOpenedForAuthorization) {
                            fragment = new NewLoginFragment();
                            getWindow().setSoftInputMode(20);
                        }
                    }
                }
                if (fragment == null) {
                    fragment = new LoginFragment();
                }
                getSupportFragmentManager().beginTransaction().add(2131624582, fragment).commit();
                if (savedInstanceState == null && !this.isOpenedForAuthorization && !this.loginFromWebRegistration) {
                    GlobalBus.send(2131623948, new BusEvent());
                    return;
                }
                return;
            }
            goToRegistration(false);
        }
    }

    private void initPhoneUtils() {
        new C10972().execute(new Object[0]);
    }

    private void hideKeyboard() {
        KeyBoardUtils.hideKeyBoard(this);
    }

    protected void onStart() {
        super.onStart();
        if (this.resolveOnfail && !this.googleApiClient.isConnected()) {
            this.googleApiClient.connect();
        }
        setHeight();
    }

    private void setHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        LayoutParams layoutParams = this.rootView.getLayoutParams();
        if (height <= width) {
            height = width;
        }
        layoutParams.height = height;
    }

    public void goToRegistration() {
        hideKeyboard();
        goToRegistration(true);
    }

    private void goToRegistration(boolean replaceFragment) {
        boolean chooseRegAlreadOpened = false;
        hideKeyboard();
        if (AuthorizationPreferences.getNativeRegistrationEnabled()) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null && fragments.size() > 0 && (fragments.get(getSupportFragmentManager().getBackStackEntryCount()) instanceof ChooseRegistrationFragment)) {
                chooseRegAlreadOpened = true;
            }
            BaseFragment fragment;
            if (VERSION.SDK_INT < 23 || !AuthorizationPreferences.getPermissionsRequestOnSeparateScreen() || AuthorizationPreferences.getNecessaryPermissions(this).length <= 0 || chooseRegAlreadOpened) {
                fragment = new RegistrationFragment();
                if (replaceFragment) {
                    this.stackRegistrationId = replaceFragment(fragment, true, STACK_REGISTRATION_TAG);
                    return;
                } else {
                    this.stackRegistrationId = getSupportFragmentManager().beginTransaction().add(2131624582, fragment, STACK_REGISTRATION_TAG).commit();
                    return;
                }
            }
            fragment = new ChooseRegistrationFragment();
            if (replaceFragment) {
                replaceFragment(fragment, true, null);
                return;
            } else {
                getSupportFragmentManager().beginTransaction().add(2131624582, fragment, null).commit();
                return;
            }
        }
        hideKeyboard();
        NavigationHelper.goToOldRegistration(this, 0);
    }

    private void goToCheckPhone(@NonNull GoToCheckPhoneBundleBuilder bundleBuilder) {
        CheckPhoneFragment checkPhoneFragment = new CheckPhoneFragment();
        checkPhoneFragment.setArguments(bundleBuilder.build());
        replaceFragment(checkPhoneFragment);
    }

    public void goToCheckPhoneForRecovery(String countryCode, String phoneNumber, long smsRequestTime) {
        GoToCheckPhoneBundleBuilder builder = new GoToCheckPhoneBundleBuilder();
        builder.setPhone(phoneNumber).setIsAccountRecovery(true).setCountryCode(countryCode).setSmsRequestTime(smsRequestTime);
        goToCheckPhone(builder);
    }

    public void goToCheckPhone(String countryCode, String phoneNumber, long pinTimeout, boolean isIvrAvailable, long smsRequestTime) {
        GoToCheckPhoneBundleBuilder builder = new GoToCheckPhoneBundleBuilder();
        builder.setPhone(phoneNumber).setCountryCode(countryCode).setSmsRequestTime(smsRequestTime).setPinTimeout(pinTimeout).setIsIvrAvailable(isIvrAvailable);
        goToCheckPhone(builder);
    }

    public void goToCheckPhone(String userId, String phoneNumber, String countryCode, boolean isPhoneAlreadyLogin, long smsRequestTime) {
        GoToCheckPhoneBundleBuilder builder = new GoToCheckPhoneBundleBuilder();
        builder.setPhone(phoneNumber).setUserId(userId).setCountryCode(countryCode).setSmsRequestTime(smsRequestTime).setIsPhoneAlreadyLogin(isPhoneAlreadyLogin);
        goToCheckPhone(builder);
    }

    public void goToUserList(String userId, String phone, String pin, ArrayList<UserWithLogin> userList, boolean isPhoneAlreadyLogin) {
        UserListFragment userListFragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putString("uid", userId);
        args.putString("pin", pin);
        args.putParcelableArrayList("user_list", userList);
        args.putBoolean("phone_already_login", isPhoneAlreadyLogin);
        userListFragment.setArguments(args);
        replaceFragment(userListFragment);
    }

    public void goToUpdateUserInfo(String pin, ArrayList<Location> locations, UserInfo userInfo, UpdateProfileFieldsFlags updateProfileFieldsFlags) {
        hideKeyboard();
        LibverifyUtil.completeVerification(getContext());
        logRegistrationWorkflowSuccess();
        Intent intent = new Intent(this, UpdateUserInfoActivity.class);
        intent.putExtra("country", getLocationByCode(locations, this.country.getCountryISO()));
        intent.putExtra("user_info", userInfo);
        if (!AuthorizationPreferences.getPasswordObligatoryBeforeProfile()) {
            intent.putExtra("password", pin);
        }
        intent.putExtra("update_profile_fields_flags", updateProfileFieldsFlags);
        intent.addFlags(268468224);
        startActivity(intent);
    }

    public Location getLocationByCode(List<Location> locations, String code) {
        for (Location location : locations) {
            String locationCode = location.getCode();
            if (locationCode != null && locationCode.toUpperCase().equals(code.toUpperCase())) {
                return location;
            }
        }
        return (Location) locations.get(0);
    }

    public void goToUpdateUserInfo(String pin, ArrayList<Location> locations, UpdateProfileFieldsFlags updateProfileFieldsFlags) {
        this.pin = pin;
        this.locations = locations;
        this.updateProfileFieldsFlags = updateProfileFieldsFlags;
        this.resolveOnfail = true;
        this.googleApiClient.connect();
    }

    public void goToUpdateUserInfo() {
        goToUpdateUserInfo(this.pin, this.locations, this.updateProfileFieldsFlags);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1) {
            super.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode != -1) {
            goToUpdateUserInfo(this.pin, this.locations, null, this.updateProfileFieldsFlags);
        } else {
            this.resolveOnfail = true;
            this.googleApiClient.connect();
        }
    }

    public void goToEnterNewLogin(String userId, String pin) {
        EnterNewLoginFragment enterNewLoginFragment = new EnterNewLoginFragment();
        Bundle args = new Bundle();
        args.putString("pin", pin);
        args.putString("uid", userId);
        enterNewLoginFragment.setArguments(args);
        replaceFragment(enterNewLoginFragment);
    }

    public void goToOdklActivity() {
        hideKeyboard();
        if (!isOdklActivityOrIntentStarted(true)) {
            setResult(-1);
            finish();
        }
    }

    private boolean isOdklActivityOrIntentStarted(boolean startOdklActivityWithoutIntent) {
        if (getCallingActivity() == null) {
            Intent intent = NavigationHelper.getIntentFromIntent(getIntent());
            if (intent != null) {
                intent.setExtrasClassLoader(NativeLoginActivity.class.getClassLoader());
            }
            if ((intent == null && startOdklActivityWithoutIntent) || this.loginFromWebRegistration) {
                intent = NavigationHelper.createIntentForOdklActivity(this);
                intent.setFlags(268468224);
                startActivity(intent);
                finish();
                return true;
            } else if (intent != null) {
                Intent newIntent;
                if (intent.getBooleanExtra("LinksActivity.EXTRA_SHORTLINK", false)) {
                    newIntent = new Intent(this, LinksActivity.class);
                    newIntent.setData(intent.getData());
                } else {
                    newIntent = (Intent) intent.clone();
                    if (intent.getExtras() != null) {
                        newIntent.replaceExtras(intent.getExtras());
                    }
                    newIntent.setFlags(268468224);
                }
                startActivity(newIntent);
                finish();
                return true;
            }
        }
        return false;
    }

    protected void onResume() {
        super.onResume();
        if (Settings.hasLoginData(this)) {
            isOdklActivityOrIntentStarted(false);
        }
    }

    protected void onCredentialsUserError() {
        Logger.m172d("no valid password");
    }

    public void goToExistingUser(UserWithLogin userInfo, String countryCode, String phone, String pin) {
        ExistingUserFragment existingUserFragment = new ExistingUserFragment();
        Bundle args = new Bundle();
        args.putParcelable("user_info", userInfo);
        args.putString("pin", pin);
        args.putString("code", countryCode);
        args.putString("phone", phone);
        existingUserFragment.setArguments(args);
        replaceFragment(existingUserFragment);
    }

    public void goToEnterPassword(UserWithLogin userWithLogin, String uid, String pin, EnterPasswordReason enterPasswordReason) {
        EnterPasswordFragment enterPasswordFragment = new EnterPasswordFragment();
        Bundle args = new Bundle();
        args.putParcelable("user_info", userWithLogin);
        args.putString("pin", pin);
        args.putString("uid", uid);
        args.putSerializable("enter_password_reason", enterPasswordReason);
        enterPasswordFragment.setArguments(args);
        replaceFragment(enterPasswordFragment);
    }

    public void goToRecoverPassword(UserWithLogin userInfo, String pin) {
        goToEnterPassword(userInfo, null, pin, EnterPasswordReason.RECOVER);
    }

    public void goToRegainUser(UserWithLogin userWithLogin, String uid, String pin) {
        goToEnterPassword(userWithLogin, uid, pin, EnterPasswordReason.REGAIN);
    }

    public void goToEnterPassword(String pin) {
        goToEnterPassword(null, null, pin, EnterPasswordReason.CHANGE_AFTER_REGISTRATION);
    }

    public void storeCountry(Country country) {
        this.country = country;
    }

    public void goToFeedback() {
        goToPage(Page.FeedBack);
    }

    public void goToFaq() {
        goToPage(Page.Faq);
    }

    private void goToPage(Page page) {
        hideKeyboard();
        Intent intent = new Intent(this, NotLoggedInWebActivity.class);
        intent.putExtra("page", page);
        startActivity(intent);
    }

    public void goBackToRegistration() {
        hideKeyboard();
        if (this.stackRegistrationId < 0) {
            getSupportFragmentManager().popBackStackImmediate(0, 1);
        } else {
            getSupportFragmentManager().popBackStackImmediate(this.stackRegistrationId, 0);
        }
        ((RegistrationFragment) getSupportFragmentManager().findFragmentByTag(STACK_REGISTRATION_TAG)).saveLastUsedPhoneNumber();
    }

    public void goToNewLogin() {
        replaceFragment(new NewLoginFragment(), true);
    }

    private int replaceFragment(BaseFragment fragment, boolean animated, String tag) {
        hideKeyboard();
        if (!(fragment instanceof NewLoginFragment)) {
            logRegistrationWorkflow(Outcome.success);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (animated) {
            fragmentTransaction.setCustomAnimations(2130968637, 2130968640, 2130968636, 2130968641);
        }
        return fragmentTransaction.replace(2131624582, fragment, tag).addToBackStack(null).commitAllowingStateLoss();
    }

    private int replaceFragment(BaseFragment fragment, boolean animated) {
        return replaceFragment(fragment, animated, null);
    }

    private int replaceFragment(BaseFragment fragment) {
        return replaceFragment(fragment, false, null);
    }

    public void onConnected(Bundle bundle) {
        UserInfo person = new UserInfo(Settings.getCurrentUser(this).getId());
        Person currentPerson = Plus.PeopleApi.getCurrentPerson(this.googleApiClient);
        if (currentPerson != null) {
            if (currentPerson.hasName()) {
                Name name = currentPerson.getName();
                if (name.hasGivenName()) {
                    person.firstName = name.getGivenName();
                }
                if (name.hasFamilyName()) {
                    person.lastName = name.getFamilyName();
                }
            }
            if (currentPerson.hasBirthday()) {
                try {
                    person.setBirthday(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(currentPerson.getBirthday()));
                } catch (Throwable e) {
                    Logger.m178e(e);
                    person.birthday = null;
                }
            }
            if (currentPerson.hasGender()) {
                if (currentPerson.getGender() == 0) {
                    person.genderType = UserGenderType.MALE;
                } else {
                    person.genderType = UserGenderType.FEMALE;
                }
            }
        }
        goToUpdateUserInfo(this.pin, this.locations, person, this.updateProfileFieldsFlags);
    }

    public void onConnectionSuspended(int i) {
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            if (this.resolveOnfail) {
                try {
                    this.resolveOnfail = false;
                    connectionResult.startResolutionForResult(this, 1);
                } catch (SendIntentException e) {
                    this.googleApiClient.connect();
                }
            }
            logRegistrationWorkflow(Outcome.failure);
            return;
        }
        goToUpdateUserInfo(this.pin, this.locations, null, this.updateProfileFieldsFlags);
    }

    private void logRegistrationWorkflowSuccess() {
        logRegistrationWorkflow(Outcome.success);
    }

    private void logRegistrationWorkflow(Outcome outcome) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            RegistrationWorkflowLogHelper.log(((BaseFragment) fragments.get(getSupportFragmentManager().getBackStackEntryCount())).getWorkflowSource(), outcome);
        }
    }
}
