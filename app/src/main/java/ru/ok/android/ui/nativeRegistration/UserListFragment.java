package ru.ok.android.ui.nativeRegistration;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import ru.ok.android.model.UserWithLogin;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.ui.adapters.UserListPagerAdapter;
import ru.ok.android.ui.adapters.UserListPagerAdapter.UserAvatarClickListener;
import ru.ok.android.utils.LibverifyUtil;
import ru.ok.android.utils.controls.authorization.AuthorizationControl;
import ru.ok.android.utils.controls.authorization.OnLoginListener;
import ru.ok.android.utils.controls.nativeregistration.ConfirmationControl;
import ru.ok.android.utils.controls.nativeregistration.OnConfirmationListener;
import ru.ok.android.utils.controls.nativeregistration.RegainUserControl;
import ru.ok.android.utils.localization.LocalizationManager;

public class UserListFragment extends PinFragment implements UserAvatarClickListener, OnConfirmationListener {
    private final String KEY_CURRENT_ITEM;
    private UserWithLogin currentUser;
    private TextView feedbackButton;
    private boolean isLoginAfterConfirm;
    private boolean isPhoneAlreadyLogin;
    private View loginBtn;
    private TextView nameView;
    private boolean needViewPagerRedraw;
    private TextView phoneView;
    private ProgressBar progressBar;
    private View registrationBtn;
    private String uid;
    private UserAvatarViewPager userList;
    private UserListPagerAdapter userListPagerAdapter;
    private UserAvatarViewPagerContainer viewUserAvatarViewPagerContainer;

    /* renamed from: ru.ok.android.ui.nativeRegistration.UserListFragment.1 */
    class C11191 implements OnTouchListener {
        C11191() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            UserListFragment.this.viewUserAvatarViewPagerContainer.onTouchEvent(event);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.UserListFragment.2 */
    class C11202 implements OnClickListener {
        C11202() {
        }

        public void onClick(View view) {
            UserListFragment.this.hideError();
            if (AuthorizationPreferences.getPasswordObligatory()) {
                UserListFragment.this.currentUser = (UserWithLogin) UserListFragment.this.userListPagerAdapter.getUserAtPosition(UserListFragment.this.userList.getCurrentItem());
                UserListFragment.this.communicationInterface.goToRegainUser(UserListFragment.this.currentUser, UserListFragment.this.uid, UserListFragment.this.getPin());
                return;
            }
            UserListFragment.this.showSpinner();
            UserListFragment.this.isLoginAfterConfirm = false;
            if (!UserListFragment.this.isLoginStarted()) {
                UserListFragment.this.currentUser = (UserWithLogin) UserListFragment.this.userListPagerAdapter.getUserAtPosition(UserListFragment.this.userList.getCurrentItem());
                UserListFragment.this.regainUserControl = new RegainUserControl();
                UserListFragment.this.regainUserControl.tryToRegainUser(UserListFragment.this.uid, UserListFragment.this.currentUser.uid, UserListFragment.this.pin, null, UserListFragment.this);
            }
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.UserListFragment.3 */
    class C11213 implements OnClickListener {
        C11213() {
        }

        public void onClick(View view) {
            UserListFragment.this.hideError();
            if (UserListFragment.this.isPhoneAlreadyLogin) {
                UserListFragment.this.communicationInterface.goToEnterNewLogin(UserListFragment.this.uid, UserListFragment.this.pin);
            } else if (UserListFragment.this.isActivityGotLocations()) {
                UserListFragment.this.communicationInterface.goToUpdateUserInfo();
            } else {
                UserListFragment.this.showSpinner();
                UserListFragment.this.isLoginAfterConfirm = true;
                if (!UserListFragment.this.isLoginStarted()) {
                    UserListFragment.this.currentUser = (UserWithLogin) UserListFragment.this.userListPagerAdapter.getUserAtPosition(UserListFragment.this.userList.getCurrentItem());
                    UserListFragment.this.confirmationControl = new ConfirmationControl();
                    UserListFragment.this.confirmationControl.tryToConfirmUser(UserListFragment.this.uid, "", UserListFragment.this.getPin(), null, UserListFragment.this);
                }
            }
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.UserListFragment.4 */
    class C11224 implements OnPageChangeListener {
        C11224() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (UserListFragment.this.needViewPagerRedraw) {
                UserListFragment.this.viewUserAvatarViewPagerContainer.invalidate();
            }
        }

        public void onPageSelected(int position) {
            UserListFragment.this.userList.setCurrentItem(position);
            UserListFragment.this.changeUserInfo();
        }

        public void onPageScrollStateChanged(int state) {
            UserListFragment.this.needViewPagerRedraw = state != 0;
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.UserListFragment.5 */
    class C11235 implements OnHierarchyChangeListener {
        C11235() {
        }

        public void onChildViewAdded(View view, View view2) {
            UserListFragment.this.userList.selectCurrentView();
        }

        public void onChildViewRemoved(View view, View view2) {
            UserListFragment.this.userList.selectCurrentView();
        }
    }

    public UserListFragment() {
        this.KEY_CURRENT_ITEM = "userListFragment:currentItem";
    }

    protected void showSpinner() {
        this.progressBar.setVisibility(0);
        disableButtons();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.userList != null) {
            outState.putInt("userListFragment:currentItem", this.userList.getCurrentItem());
        }
    }

    protected String getLogin() {
        return this.currentUser.login;
    }

    protected void hideSpinner() {
        this.progressBar.setVisibility(8);
        enableButtons();
    }

    private void disableButtons() {
        this.registrationBtn.setClickable(false);
        this.registrationBtn.setAlpha(0.4f);
        this.loginBtn.setClickable(false);
        this.loginBtn.setAlpha(0.4f);
    }

    private void enableButtons() {
        this.registrationBtn.setClickable(true);
        this.registrationBtn.setAlpha(1.0f);
        this.loginBtn.setClickable(true);
        this.loginBtn.setAlpha(1.0f);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        setCommunicationInterface((CommunicationInterface) getActivity());
        View view = LocalizationManager.inflate(getActivity(), 2130903555, container, false);
        this.userList = (UserAvatarViewPager) view.findViewById(2131625126);
        this.phoneView = (TextView) view.findViewById(2131625129);
        this.nameView = (TextView) view.findViewById(2131625128);
        this.loginBtn = view.findViewById(2131625018);
        this.progressBar = (ProgressBar) view.findViewById(2131624680);
        this.registrationBtn = view.findViewById(2131624806);
        this.viewUserAvatarViewPagerContainer = (UserAvatarViewPagerContainer) view.findViewById(2131625127);
        this.feedbackButton = (TextView) view.findViewById(2131624812);
        setErrorTextView((TextView) view.findViewById(2131625281));
        this.userListPagerAdapter = new UserListPagerAdapter(getActivity(), this);
        this.userList.setOffscreenPageLimit(9);
        this.uid = (String) getArguments().get("uid");
        this.pin = (String) getArguments().get("pin");
        this.isPhoneAlreadyLogin = getArguments().getBoolean("phone_already_login");
        this.userListPagerAdapter.setData(getArguments().getParcelableArrayList("user_list"));
        this.userList.setAdapter(this.userListPagerAdapter);
        changeUserInfo();
        initListeners();
        view.setOnTouchListener(new C11191());
        return view;
    }

    public void onResume() {
        super.onResume();
        this.viewUserAvatarViewPagerContainer.setPagerWidth();
    }

    public void onPause() {
        super.onPause();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.viewUserAvatarViewPagerContainer.setPagerWidth();
    }

    public void onLoginSuccessful(String url, String verificationUrl) {
        boolean goToEnterPassword = AuthorizationPreferences.getPasswordObligatory() && AuthorizationPreferences.getPasswordBeforeProfile();
        if (!this.isLoginAfterConfirm) {
            hideSpinner();
            this.regainUserControl = null;
            storeUserName(getLogin(), true);
            LibverifyUtil.completeVerification(getContext());
            logWorkflowSuccess();
            this.communicationInterface.goToOdklActivity();
        } else if (goToEnterPassword) {
            storeUserName(getLogin(), true);
            this.communicationInterface.goToEnterPassword(getPin());
        } else {
            super.onLoginSuccessful(url, verificationUrl);
        }
    }

    private void initListeners() {
        this.loginBtn.setOnClickListener(new C11202());
        this.registrationBtn.setOnClickListener(new C11213());
        this.userList.setOnPageChangeListener(new C11224());
        this.userList.setOnHierarchyChangeListener(new C11235());
        setFeedbackButtonListener(this.feedbackButton);
    }

    private void changeUserInfo() {
        UserWithLogin user = (UserWithLogin) this.userListPagerAdapter.getUserAtPosition(this.userList.getCurrentItem());
        this.phoneView.setText(user.login);
        this.nameView.setText(user.getAnyName().trim());
    }

    public void onAvatarClick(Object object, int position) {
        if (position != this.userList.getCurrentItem()) {
            this.userList.setCurrentItem(position, true);
        }
    }

    public void onUserConfirmationSuccessfull(String authToken) {
        setToken(authToken);
        AuthorizationControl.getInstance().login(authToken, true, (OnLoginListener) this);
    }

    public void onUserConfirmationError(String error, @NonNull ErrorType errorType) {
        logWorkflowError();
        hideSpinner();
        this.confirmationControl = null;
        showError(errorType.getDefaultErrorMessage());
    }
}
