package ru.ok.android.ui.nativeRegistration;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import ru.ok.android.model.UserWithLogin;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.ui.adapters.ScrollLoadBlocker;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.onelog.registration.RegistrationStartAgainEventFactory;

public class ExistingUserFragment extends PinFragment {
    private String countryCode;
    private TextView loginView;
    private String phone;
    private TextView phoneTxt;
    private View progressView;
    private View recoverBtn;
    private View registerBtn;
    private UserWithLogin user;

    /* renamed from: ru.ok.android.ui.nativeRegistration.ExistingUserFragment.1 */
    class C10831 implements OnClickListener {
        C10831() {
        }

        public void onClick(View v) {
            ExistingUserFragment.this.communicationInterface.goToRecoverPassword(ExistingUserFragment.this.user, ExistingUserFragment.this.getPin());
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.ExistingUserFragment.2 */
    class C10842 implements OnClickListener {
        C10842() {
        }

        public void onClick(View v) {
            ExistingUserFragment.this.communicationInterface.goBackToRegistration();
            OneLog.log(RegistrationStartAgainEventFactory.get());
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCommunicationInterface((CommunicationInterface) getActivity());
        View view = LocalizationManager.inflate(getActivity(), 2130903180, container, false);
        this.progressView = view.findViewById(2131624548);
        this.recoverBtn = view.findViewById(2131624804);
        this.loginView = (TextView) view.findViewById(2131624676);
        this.registerBtn = view.findViewById(2131624806);
        AvatarImageView avatar = (AvatarImageView) view.findViewById(2131624657);
        this.phoneTxt = (TextView) view.findViewById(2131624807);
        this.user = (UserWithLogin) getArguments().getParcelable("user_info");
        this.pin = getArguments().getString("pin");
        this.phone = getArguments().getString("phone");
        this.countryCode = getArguments().getString("code");
        setUserExistsInfoMessage();
        buildUserName();
        ImageViewManager.getInstance().displayImage(this.user.picUrl, avatar, this.user.genderType == UserGenderType.MALE, ScrollLoadBlocker.forIdleAndTouchIdle());
        setFeedbackButtonListener((TextView) view.findViewById(2131624812));
        initListeners();
        return view;
    }

    private void setUserExistsInfoMessage() {
        String numberString;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            numberString = phoneUtil.format(phoneUtil.parse(this.countryCode + this.phone, phoneUtil.getRegionCodeForCountryCode(Integer.parseInt(this.countryCode)).toUpperCase()), PhoneNumberFormat.INTERNATIONAL);
        } catch (Exception e) {
            numberString = this.countryCode + this.phone;
        }
        String userExistsString = String.format(LocalizationManager.getString(getActivity(), 2131166804), new Object[]{numberString});
        SpannableString spannableString = new SpannableString(userExistsString);
        int start = userExistsString.indexOf(numberString);
        int end = start + numberString.length();
        spannableString.setSpan(new StyleSpan(1), start, end, 0);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(2131492915)), start, end, 0);
        this.phoneTxt.setText(spannableString, BufferType.SPANNABLE);
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
        this.recoverBtn.setOnClickListener(new C10831());
        this.registerBtn.setOnClickListener(new C10842());
    }

    protected void hideSpinner() {
        this.registerBtn.setAlpha(1.0f);
        this.registerBtn.setClickable(true);
        this.recoverBtn.setAlpha(1.0f);
        this.recoverBtn.setClickable(true);
        this.progressView.setVisibility(8);
    }

    protected void showSpinner() {
        this.registerBtn.setAlpha(0.4f);
        this.registerBtn.setClickable(false);
        this.recoverBtn.setAlpha(0.4f);
        this.recoverBtn.setClickable(false);
        this.progressView.setVisibility(0);
    }

    protected String getLogin() {
        return this.user.login;
    }
}
