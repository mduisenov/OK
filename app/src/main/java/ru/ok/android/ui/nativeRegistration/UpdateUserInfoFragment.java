package ru.ok.android.ui.nativeRegistration;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.model.UpdateProfileFieldsFlags;
import ru.ok.android.model.image.ImageForUpload;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.onelog.registration.ProfileErrorBuilder;
import ru.ok.android.onelog.registration.RegistrationWorkflowLogHelper;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.services.processors.registration.Location;
import ru.ok.android.ui.custom.UploadAvatarRoundedImageView;
import ru.ok.android.ui.custom.text.PasswordEditText;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.users.fragments.profiles.statistics.UserProfileStatisticsManager;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.controls.nativeregistration.UpdateInfoListener;
import ru.ok.android.utils.controls.nativeregistration.UserInfoControl;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.onelog.builtin.Outcome;
import ru.ok.onelog.registration.AvatarUploadEventFactory;
import ru.ok.onelog.registration.AvatarUploadEventSource;
import ru.ok.onelog.registration.ProfileErrorEventFactory;
import ru.ok.onelog.registration.ProfileErrorSource;

public class UpdateUserInfoFragment extends BaseFragment implements OnClickListener, UpdateInfoListener {
    private UploadAvatarRoundedImageView avatarImage;
    private EditText birthday;
    private RelativeLayout birthdayAnchor;
    private View clearPassword;
    private Location currentCountry;
    private UserInfo currentUserInfo;
    private EditText firstNameTxt;
    private ImageView genderIcon;
    private Spinner genderSpinner;
    private UserGenderType genderType;
    private View goBtn;
    private boolean isChangePasswordEnabled;
    private EditText lastNameTxt;
    private ImageView nameIcon;
    private String oldPassword;
    private View passwordContainer;
    private PasswordEditText passwordTxt;
    private View progress;
    private SelectDateFragment selectDateFragment;
    private TextView updatePasswordInfoText;
    private UpdateProfileFieldsFlags updateProfileFieldsFlags;
    private ImageForUpload uploadedImage;
    private UserInfoControl userInfoControl;

    public interface OnUpdateUserInfoListener {
        void onInfoUpdated(UserInfo userInfo);
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.UpdateUserInfoFragment.1 */
    class C11131 implements TextWatcher {
        final /* synthetic */ EditText val$editText;

        C11131(EditText editText) {
            this.val$editText = editText;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            UpdateUserInfoFragment.this.hideEditTextError(this.val$editText);
        }

        public void afterTextChanged(Editable s) {
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.UpdateUserInfoFragment.2 */
    class C11142 implements OnFocusChangeListener {
        C11142() {
        }

        public void onFocusChange(View view, boolean b) {
            if (b) {
                UpdateUserInfoFragment.this.showBirthdayDialog();
            }
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.UpdateUserInfoFragment.3 */
    class C11153 implements OnItemSelectedListener {
        C11153() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            UpdateUserInfoFragment.this.genderType = UserGenderType.byInteger(position);
            UpdateUserInfoFragment.this.avatarImage.setGender(UpdateUserInfoFragment.this.genderType);
            int genderIconRes = UpdateUserInfoFragment.this.genderType == UserGenderType.MALE ? 2130837931 : 2130837932;
            UpdateUserInfoFragment.this.nameIcon.setImageResource(UpdateUserInfoFragment.this.genderType == UserGenderType.MALE ? 2130838399 : 2130838400);
            UpdateUserInfoFragment.this.genderIcon.setImageResource(genderIconRes);
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.UpdateUserInfoFragment.4 */
    class C11164 implements TextWatcher {
        C11164() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                UpdateUserInfoFragment.this.clearPassword.setVisibility(4);
            } else {
                UpdateUserInfoFragment.this.clearPassword.setVisibility(0);
            }
            UpdateUserInfoFragment.this.passwordTxt.validatePassword();
            UpdateUserInfoFragment.this.hideEditTextError(UpdateUserInfoFragment.this.passwordTxt.getEditText());
        }

        public void afterTextChanged(Editable s) {
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.UpdateUserInfoFragment.5 */
    class C11175 implements DialogInterface.OnClickListener {
        C11175() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    public static class SelectDateFragment extends DialogFragment implements OnDateSetListener {
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar cal = GregorianCalendar.getInstance();
            if (getArguments() != null) {
                Date birthday = (Date) getArguments().getSerializable("birthday");
                if (birthday != null) {
                    cal.setTime(birthday);
                }
            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this, cal.get(1), cal.get(2), cal.get(5));
            Calendar sixYears = GregorianCalendar.getInstance();
            sixYears.add(1, -6);
            datePickerDialog.getDatePicker().setMaxDate(sixYears.getTimeInMillis());
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            ((UpdateUserInfoFragment) getTargetFragment()).populateSetDate(yy, mm + 1, dd);
        }
    }

    public UpdateUserInfoFragment() {
        this.isChangePasswordEnabled = true;
    }

    private void prepareGenderSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(getContext(), 2130903440);
        spinnerAdapter.addAll(LocalizationManager.getStringArray(getContext(), 2131558416));
        this.genderSpinner.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(17367049);
    }

    private void setGenderValue(UserGenderType genderType) {
        prepareGenderSpinner();
        this.genderSpinner.setSelection(genderType.toInteger());
    }

    private void hideFields() {
        if (!this.updateProfileFieldsFlags.isAvatarVisible) {
            this.avatarImage.setVisibility(8);
        }
        if (AuthorizationPreferences.getPasswordObligatoryBeforeProfile() || !this.isChangePasswordEnabled) {
            this.passwordContainer.setVisibility(8);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.isChangePasswordEnabled = savedInstanceState.getBoolean("IS_CHANGE_PASSWORD_ENABLED", true);
            this.currentUserInfo = (UserInfo) savedInstanceState.getParcelable("CURRENT_USER_INFO");
            this.uploadedImage = (ImageForUpload) savedInstanceState.getParcelable("pic");
        }
        this.updateProfileFieldsFlags = (UpdateProfileFieldsFlags) getArguments().getParcelable("update_profile_fields_flags");
        this.oldPassword = getArguments().getString("password");
        this.currentCountry = (Location) getArguments().getParcelable("country");
        View view = LocalizationManager.inflate(getContext(), 2130903203, container, false);
        this.nameIcon = (ImageView) view.findViewById(2131624852);
        this.genderIcon = (ImageView) view.findViewById(2131624857);
        this.birthday = (EditText) view.findViewById(2131624856);
        this.firstNameTxt = (EditText) view.findViewById(2131624853);
        this.lastNameTxt = (EditText) view.findViewById(2131624854);
        this.passwordTxt = (PasswordEditText) view.findViewById(2131624773);
        this.genderSpinner = (Spinner) view.findViewById(2131624858);
        this.goBtn = view.findViewById(2131624862);
        this.updatePasswordInfoText = (TextView) view.findViewById(2131624861);
        this.passwordContainer = view.findViewById(2131624859);
        this.clearPassword = view.findViewById(2131624860);
        this.birthdayAnchor = (RelativeLayout) view.findViewById(2131624855);
        this.progress = view.findViewById(2131624680);
        this.avatarImage = (UploadAvatarRoundedImageView) view.findViewById(2131624657);
        hideFields();
        setPasswordInfoText();
        initListeners();
        preparePerson();
        return view;
    }

    private void setPasswordInfoText() {
        if (AuthorizationPreferences.getPasswordObligatory()) {
            this.updatePasswordInfoText.setText(LocalizationManager.getString(getContext(), 2131165789));
        } else {
            this.updatePasswordInfoText.setText(LocalizationManager.getString(getContext(), 2131165562));
        }
    }

    private void fillForm(UserInfo userInfo) {
        this.avatarImage.setGender(userInfo.genderType);
        if (this.uploadedImage != null) {
            this.avatarImage.setAvatar(this.uploadedImage.getUri().toString());
        }
        if (!AuthorizationPreferences.getPasswordObligatory()) {
            this.passwordTxt.setText(this.oldPassword);
        }
        if (AuthorizationPreferences.getPasswordValidationEnabled() && this.passwordContainer.getVisibility() == 0) {
            this.passwordTxt.setValidatePassword(true, true);
        }
        setGenderValue(userInfo.genderType);
        if (userInfo.birthday != null) {
            GregorianCalendar birth = new GregorianCalendar();
            birth.setTime(userInfo.birthday);
            populateSetDate(birth.get(1), birth.get(2) + 1, birth.get(5));
        } else {
            this.birthday.setText("");
        }
        this.firstNameTxt.setText(userInfo.firstName);
        this.lastNameTxt.setText(userInfo.lastName);
    }

    private void disableButton() {
        this.goBtn.setClickable(false);
        this.goBtn.setAlpha(0.4f);
    }

    private void enableButton() {
        this.goBtn.setClickable(true);
        this.goBtn.setAlpha(1.0f);
    }

    private boolean isUserInfoChanged(UserInfo newUserInfo) {
        return (this.currentUserInfo != null && newUserInfo.firstName.equals(this.currentUserInfo.firstName) && newUserInfo.lastName.equals(this.currentUserInfo.lastName) && newUserInfo.genderType.equals(this.currentUserInfo.genderType) && (newUserInfo.birthday == null || newUserInfo.birthday.equals(this.currentUserInfo.birthday))) ? false : true;
    }

    private void updateUserInfo() {
        String newPassword;
        showSpinner();
        if (this.passwordContainer.getVisibility() == 8) {
            newPassword = "";
        } else {
            newPassword = this.passwordTxt.getText();
        }
        UserInfo user = getUserInfoFromForm();
        if (!validateFields(user, newPassword)) {
            hideSpinner();
        } else if (isUserInfoChanged(user)) {
            this.userInfoControl = new UserInfoControl();
            this.userInfoControl.tryToUpdateUserInfo(this.oldPassword, newPassword, user, this);
        } else {
            hideSpinner();
            ((OnUpdateUserInfoListener) getActivity()).onInfoUpdated(user);
        }
    }

    private void onAvatarClick() {
        NavigationHelper.startPhotoUploadSequence(getActivity(), null, 1, 2);
    }

    private void onClearPasswordCLick() {
        this.passwordTxt.clearText();
    }

    private void preparePerson() {
        UserInfo userInfo = (UserInfo) getArguments().getParcelable("user_info");
        if (userInfo == null) {
            userInfo = new UserInfo("");
            userInfo.birthday = null;
            userInfo.genderType = UserGenderType.MALE;
        }
        if (userInfo.location == null) {
            userInfo.location = new UserInfo.Location("", "", "");
        }
        userInfo.name = Settings.getUserName(getContext());
        userInfo.uid = Settings.getCurrentUser(getContext()).uid;
        fillForm(userInfo);
    }

    private void showEditTextError(EditText editText) {
        Utils.setViewBackgroundWithoutResettingPadding(editText, 2130838301);
    }

    private void hideEditTextError(EditText editText) {
        Utils.setViewBackgroundWithoutResettingPadding(editText, 2130838300);
    }

    private void addTextWatcher(EditText editText) {
        editText.addTextChangedListener(new C11131(editText));
    }

    private void initListeners() {
        this.goBtn.setOnClickListener(this);
        this.avatarImage.setOnClickListener(this);
        this.clearPassword.setOnClickListener(this);
        this.birthday.setOnClickListener(this);
        this.birthdayAnchor.setOnFocusChangeListener(new C11142());
        this.genderSpinner.setOnItemSelectedListener(new C11153());
        addTextWatcher(this.firstNameTxt);
        addTextWatcher(this.lastNameTxt);
        addTextWatcher(this.birthday);
        this.passwordTxt.setTextChangedListener(new C11164());
    }

    private boolean validateFields(UserInfo user, String newPassword) {
        ProfileErrorBuilder profileErrorBuilder = new ProfileErrorBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        if (this.updateProfileFieldsFlags.isFirstNameLastNameRequired && TextUtils.isEmpty(user.firstName)) {
            appendError(stringBuilder, this.firstNameTxt, 2131165877);
            profileErrorBuilder.setFirstNameEmpty(true);
        }
        if (this.updateProfileFieldsFlags.isFirstNameLastNameRequired && TextUtils.isEmpty(user.lastName)) {
            appendError(stringBuilder, this.lastNameTxt, 2131166033);
            profileErrorBuilder.setLastNameEmpty(true);
        }
        if (this.updateProfileFieldsFlags.isBirthdayRequired && user.birthday == null) {
            appendError(stringBuilder, this.birthday, 2131165444);
            profileErrorBuilder.setBirthdayEmpty(true);
        }
        if (this.passwordContainer.getVisibility() == 0) {
            if (this.passwordTxt.validatePassword()) {
                if (newPassword.contains(" ")) {
                    showErrorDialog(LocalizationManager.getString(getContext(), 2131165838));
                    profileErrorBuilder.setPasswordInvalid(true);
                } else if (TextUtils.isEmpty(newPassword) && AuthorizationPreferences.getPasswordObligatory()) {
                    appendError(stringBuilder, this.passwordTxt.getEditText(), 2131166328);
                    profileErrorBuilder.setPasswordEmpty(true);
                }
            } else if (TextUtils.isEmpty(this.passwordTxt.getText())) {
                profileErrorBuilder.setPasswordEmpty(true);
            } else {
                profileErrorBuilder.setPasswordInvalid(true);
            }
        }
        if (stringBuilder.length() > 0) {
            showErrorDialog(stringBuilder.toString());
        }
        if (profileErrorBuilder.hasError()) {
            OneLog.log(ProfileErrorEventFactory.get(profileErrorBuilder.toString(), ProfileErrorSource.profile));
        }
        if (profileErrorBuilder.hasError()) {
            return false;
        }
        return true;
    }

    private void appendError(StringBuilder stringBuilder, EditText editText, int stringId) {
        showEditTextError(editText);
        String password = LocalizationManager.getString(getContext(), stringId).toLowerCase();
        if (stringBuilder.length() > 0) {
            stringBuilder.append(", ").append(password);
        } else {
            stringBuilder.append(LocalizationManager.getString(getContext(), 2131165875)).append(" ").append(password);
        }
    }

    private void showSpinner() {
        disableButton();
        this.progress.setVisibility(0);
    }

    private void hideSpinner() {
        enableButton();
        this.progress.setVisibility(8);
    }

    private UserInfo getUserInfoFromForm() {
        UserInfo user = new UserInfo(OdnoklassnikiApplication.getCurrentUser().getId());
        user.location = new UserInfo.Location(this.currentCountry.getId(), this.currentCountry.getName(), " ");
        user.firstName = this.firstNameTxt.getText().toString();
        user.lastName = this.lastNameTxt.getText().toString();
        user.genderType = this.genderType;
        try {
            user.birthday = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(this.birthday.getText().toString());
        } catch (Throwable e) {
            Logger.m178e(e);
        }
        return user;
    }

    public void populateSetDate(int year, int month, int day) {
        this.birthday.setText(String.format("%04d-%02d-%02d", new Object[]{Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day)}));
    }

    public void onUserInfoUpdateError(String error, @NonNull ErrorType errorType) {
        logRegistrationWorkflowFailure();
        hideSpinner();
        this.userInfoControl = null;
        showErrorDialog(LocalizationManager.getString(getContext(), errorType.getDefaultErrorMessage()));
    }

    public void showErrorDialog(String errorMessage) {
        Builder builder = new Builder(getContext());
        builder.setMessage((CharSequence) errorMessage).setNeutralButton(getStringLocalized(2131165595), new C11175());
        builder.create().show();
    }

    @Subscribe(on = 2131623946, to = 2131624225)
    public void onImageUploaded(BusEvent event) {
        if (event.resultCode == 1) {
            this.uploadedImage = (ImageForUpload) event.bundleOutput.getParcelable("img");
            if (this.uploadedImage == null || this.uploadedImage.getCurrentStatus() != 5) {
                this.avatarImage.showAvatarProgress();
                return;
            }
            this.avatarImage.hideAvatarProgress();
            if (this.uploadedImage.getUploadTarget() == 2) {
                OneLog.log(AvatarUploadEventFactory.get(AvatarUploadEventSource.profile_screen));
                this.avatarImage.setAvatar(this.uploadedImage.getUri().toString());
                UserProfileStatisticsManager.sendStatEventForCurrentUser(UserProfileStatisticsManager.ACTION_UPLOAD_AVATAR);
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("IS_CHANGE_PASSWORD_ENABLED", this.isChangePasswordEnabled);
        outState.putParcelable("CURRENT_USER_INFO", this.currentUserInfo);
        super.onSaveInstanceState(outState);
    }

    public void onUserInfoUpdateSuccessful(UserInfo userInfo) {
        this.currentUserInfo = userInfo;
        this.userInfoControl = null;
        this.isChangePasswordEnabled = false;
        hideSpinner();
        ((OnUpdateUserInfoListener) getActivity()).onInfoUpdated(userInfo);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case 2131624657:
                onAvatarClick();
            case 2131624856:
                showBirthdayDialog();
            case 2131624860:
                onClearPasswordCLick();
            case 2131624862:
                updateUserInfo();
            default:
        }
    }

    private void showBirthdayDialog() {
        if (this.selectDateFragment == null || !this.selectDateFragment.isAdded()) {
            this.selectDateFragment = new SelectDateFragment();
            Bundle args = new Bundle();
            try {
                args.putSerializable("birthday", new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(this.birthday.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.selectDateFragment.setArguments(args);
            this.selectDateFragment.setTargetFragment(this, 0);
            this.selectDateFragment.show(getFragmentManager(), null);
        }
    }

    private void logRegistrationWorkflowFailure() {
        RegistrationWorkflowLogHelper.log(getClass(), Outcome.failure);
    }

    protected int getLayoutId() {
        return 2130903203;
    }

    public void onStart() {
        super.onStart();
        if (!this.isChangePasswordEnabled) {
            this.passwordContainer.setVisibility(8);
        }
    }

    public static UpdateUserInfoFragment newInstance(Location location, UserInfo userInfo, UpdateProfileFieldsFlags updateProfileFieldsFlags, String oldPassword) {
        UpdateUserInfoFragment fragment = new UpdateUserInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("country", location);
        bundle.putParcelable("user_info", userInfo);
        bundle.putParcelable("update_profile_fields_flags", updateProfileFieldsFlags);
        bundle.putString("password", oldPassword);
        fragment.setArguments(bundle);
        return fragment;
    }
}
