package ru.ok.android.ui.nativeRegistration;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import ru.ok.android.db.access.AuthorizedUsersStorageFacade;
import ru.ok.android.model.AuthorizedUser;
import ru.ok.android.model.UserWithLogin;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.ui.activity.LoginScreenUtils;
import ru.ok.android.ui.adapters.UserListPagerAdapter;
import ru.ok.android.ui.adapters.UserListPagerAdapter.UserAvatarCancelClickListener;
import ru.ok.android.ui.adapters.UserListPagerAdapter.UserAvatarClickListener;
import ru.ok.android.ui.custom.text.PasswordEditText;
import ru.ok.android.ui.nativeRegistration.DeleteUserDialog.DeleteUserClickListener;
import ru.ok.android.utils.KeyBoardUtils;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.UserInfo;
import ru.ok.onelog.registration.AuthorizationExperienceEvent;
import ru.ok.onelog.registration.AuthorizationExperienceFactory;

public class LoginFragment extends BaseLoginFragment implements OnClickListener, OnEditorActionListener, UserAvatarCancelClickListener, UserAvatarClickListener, DeleteUserClickListener {
    private int CODE_REQUEST_PERMISSIONS;
    private final String KEY_CURRENT_ITEM;
    private final String KEY_STATE;
    private int currentItem;
    private AuthorizedUser currentUser;
    private DeleteUserDialog deleteUserDialog;
    private Button goToRegistration;
    private Button loginButton;
    private ProgressBar loginProgress;
    private TextView loginView;
    private TextView nameView;
    private boolean needViewPagerRedraw;
    private boolean permissionsAlreadyAsked;
    private State state;
    private UserAvatarViewPager userList;
    private UserListPagerAdapter userListAdapter;
    private final LoaderCallbacks<ArrayList<AuthorizedUser>> userListLoader;
    private UserAvatarViewPagerContainer viewUserAvatarViewPagerContainer;

    /* renamed from: ru.ok.android.ui.nativeRegistration.LoginFragment.1 */
    class C10871 extends AsyncTask {
        final /* synthetic */ UserInfo val$user;

        C10871(UserInfo userInfo) {
            this.val$user = userInfo;
        }

        protected Object doInBackground(Object[] params) {
            AuthorizedUsersStorageFacade.deleteEntry(this.val$user.uid);
            return null;
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.LoginFragment.2 */
    class C10882 extends AsyncTask {
        C10882() {
        }

        protected Object doInBackground(Object[] params) {
            AuthorizedUsersStorageFacade.logOutCurrentUser(LoginFragment.this.currentUser.user.uid);
            return null;
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.LoginFragment.3 */
    class C10893 implements OnTouchListener {
        C10893() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            LoginFragment.this.viewUserAvatarViewPagerContainer.onTouchEvent(event);
            return true;
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.LoginFragment.4 */
    class C10904 implements OnPageChangeListener {
        C10904() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            LoginFragment.this.currentItem = LoginFragment.this.userList.getCurrentItem();
            if (LoginFragment.this.needViewPagerRedraw) {
                LoginFragment.this.viewUserAvatarViewPagerContainer.invalidate();
            }
            LoginFragment.this.hideKeyboard();
        }

        public void onPageSelected(int position) {
            if (position != LoginFragment.this.currentItem) {
                LoginFragment.this.clearInputFields();
            }
            LoginFragment.this.checkCurrentUser(position);
            LoginFragment.this.hideError();
        }

        public void onPageScrollStateChanged(int state) {
            LoginFragment.this.needViewPagerRedraw = state != 0;
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.LoginFragment.5 */
    class C10915 implements OnHierarchyChangeListener {
        C10915() {
        }

        public void onChildViewAdded(View view, View view2) {
            LoginFragment.this.userList.selectCurrentView();
        }

        public void onChildViewRemoved(View view, View view2) {
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.LoginFragment.6 */
    class C10926 implements OnClickListener {
        C10926() {
        }

        public void onClick(View view) {
            String[] permissionsToRequest = AuthorizationPreferences.getInitNecessaryPermissions(LoginFragment.this.getContext());
            if (LoginFragment.this.permissionsAlreadyAsked || AuthorizationPreferences.getPermissionsRequestOnSeparateScreen() || permissionsToRequest.length <= 0) {
                LoginFragment.this.communicationInterface.goToRegistration();
                return;
            }
            LoginFragment.this.permissionsAlreadyAsked = true;
            LoginFragment.this.requestPermissions(permissionsToRequest, LoginFragment.this.CODE_REQUEST_PERMISSIONS);
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.LoginFragment.7 */
    class C10937 implements LoaderCallbacks<ArrayList<AuthorizedUser>> {
        C10937() {
        }

        public Loader onCreateLoader(int id, Bundle args) {
            if (id == 1) {
                return new AuthorizedUsersLoader(LoginFragment.this.getActivity());
            }
            return null;
        }

        public void onLoadFinished(Loader<ArrayList<AuthorizedUser>> loader, ArrayList<AuthorizedUser> data) {
            if (data == null) {
                data = new ArrayList();
                data.add(new AuthorizedUser());
            } else if (data.size() == 0 || !((AuthorizedUser) data.get(data.size() - 1)).user.getId().equals("")) {
                data.add(new AuthorizedUser());
            }
            if (LoginFragment.this.userListAdapter.getCount() != data.size()) {
                LoginFragment.this.userListAdapter.setData(data);
                if (LoginFragment.this.userList.getChildCount() > 1) {
                    LoginFragment.this.userList.setCurrentItem(LoginFragment.this.userList.getChildCount() - 2);
                }
                if (((AuthorizedUser) LoginFragment.this.userListAdapter.getUserAtPosition(LoginFragment.this.userList.getCurrentItem())).user.uid.equals("")) {
                    LoginFragment.this.changeState(State.NEW_LOGIN_BUTTON);
                } else if (((AuthorizedUser) LoginFragment.this.userListAdapter.getUserAtPosition(LoginFragment.this.userList.getCurrentItem())).token.equals("")) {
                    LoginFragment.this.changeState(State.OLD_LOGIN_PASSWORD);
                } else {
                    LoginFragment.this.changeState(State.OLD_LOGIN_PASSWORD);
                }
            }
        }

        public void onLoaderReset(Loader loader) {
            LoginFragment.this.userListAdapter.setData(null);
        }
    }

    /* renamed from: ru.ok.android.ui.nativeRegistration.LoginFragment.8 */
    static /* synthetic */ class C10948 {
        static final /* synthetic */ int[] f115xe5d2f8c6;

        static {
            f115xe5d2f8c6 = new int[State.values().length];
            try {
                f115xe5d2f8c6[State.OLD_LOGIN_TOKEN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f115xe5d2f8c6[State.OLD_LOGIN_PASSWORD.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private enum State {
        NEW_LOGIN_BUTTON,
        OLD_LOGIN_TOKEN,
        OLD_LOGIN_PASSWORD
    }

    public LoginFragment() {
        this.KEY_CURRENT_ITEM = "loginFragment:currentItem";
        this.KEY_STATE = "loginFragment:state";
        this.state = State.NEW_LOGIN_BUTTON;
        this.CODE_REQUEST_PERMISSIONS = 4;
        this.userListLoader = new C10937();
    }

    public void onAvatarClick(Object object, int position) {
        if (position == this.userList.getCurrentItem()) {
            AuthorizedUser authorizedUser = (AuthorizedUser) object;
            if (TextUtils.isEmpty(authorizedUser.user.uid)) {
                if (false) {
                    NavigationHelper.showSettings(getActivity(), true);
                    return;
                }
                return;
            } else if (!authorizedUser.token.equals("")) {
                showSpinner();
                this.login = authorizedUser.user.login;
                LoginScreenUtils.performLoginByToken(authorizedUser.token, this);
                return;
            } else {
                return;
            }
        }
        this.userList.setCurrentItem(position, true);
        hideKeyboard();
    }

    public void onDeleteUserClicked(int item, UserInfo user) {
        new C10871(user).execute(new Object[0]);
        OneLog.log(AuthorizationExperienceFactory.get(AuthorizationExperienceEvent.user_removing_approve, Settings.getAuthorizedUserCount(getActivity())));
        Settings.removeAuthorizedUser(getActivity());
        this.userListAdapter.deleteItem(item);
        checkCurrentUser(item);
    }

    public void onAvatarCancelClick(UserInfo userInfo, int position) {
        if (this.deleteUserDialog == null || !this.deleteUserDialog.isVisible()) {
            OneLog.log(AuthorizationExperienceFactory.get(AuthorizationExperienceEvent.user_removing_btn_click, Settings.getAuthorizedUserCount(getActivity())));
            this.deleteUserDialog = DeleteUserDialog.newInstance(userInfo, position, this);
            this.deleteUserDialog.show(getFragmentManager(), null);
        }
    }

    private void changeState(State state) {
        hideError();
        this.state = state;
        stateChanged();
    }

    private void clearInputFields() {
        this.passwordText.clearText();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.userList != null) {
            outState.putInt("loginFragment:currentItem", this.userList.getCurrentItem());
        }
        outState.putSerializable("loginFragment:state", this.state);
    }

    public void onPause() {
        super.onPause();
        this.currentItem = this.userList.getCurrentItem();
    }

    private void stateChanged() {
        if (this.state == State.NEW_LOGIN_BUTTON) {
            initNewLoginButton();
            this.nameView.setVisibility(8);
            this.loginView.setVisibility(0);
        } else {
            UserWithLogin user = ((AuthorizedUser) this.userListAdapter.getUserAtPosition(this.userList.getCurrentItem())).user;
            this.loginView.setVisibility(8);
            this.goToRegistration.setVisibility(8);
            String userName = user.getAnyName().trim();
            if (StringUtils.isEmpty(userName)) {
                this.nameView.setText(user.login);
            } else {
                this.nameView.setText(userName);
            }
            this.nameView.setVisibility(0);
        }
        switch (C10948.f115xe5d2f8c6[this.state.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                initOldLoginToken();
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                initOldLoginPassword();
            default:
        }
    }

    private void initOldLoginPassword() {
        showPasswordTxt();
    }

    private void initNewLoginButton() {
        clearInputFields();
        hidePasswordTxt();
        this.goToRegistration.setVisibility(0);
    }

    private void initOldLoginToken() {
        hidePasswordTxt();
    }

    private void hidePasswordTxt() {
        this.passwordText.setVisibility(8);
    }

    private void showPasswordTxt() {
        this.passwordText.setVisibility(0);
    }

    protected void hideInputError() {
        this.passwordText.setEditTextBackground(2130838300);
    }

    protected void showInputError() {
        this.passwordText.setEditTextBackground(2130838301);
    }

    public void onLoginError(String message, int type, int errorCode) {
        hideSpinner();
        if (message == null) {
            showDefaultErrorMessage(message, type, errorCode);
        } else if ((message.equals("AUTH_LOGIN : INVALID_CREDENTIALS") || message.equals("AUTH_LOGIN : LOGOUT_ALL")) && this.state == State.OLD_LOGIN_TOKEN) {
            changeState(State.OLD_LOGIN_PASSWORD);
            showError(LocalizationManager.getString(getActivity(), 2131166053));
            clearInvalidToken();
        } else if (!message.equals("AUTH_LOGIN : BLOCKED") && errorCode == 401 && this.state == State.OLD_LOGIN_PASSWORD && type == 10) {
            showError(LocalizationManager.getString(getActivity(), 2131165846));
        } else {
            showDefaultErrorMessage(message, type, errorCode);
        }
    }

    private void clearInvalidToken() {
        this.currentUser.token = "";
        new C10882().execute(new Object[0]);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCommunicationInterface((CommunicationInterface) getActivity());
        View view = LocalizationManager.inflate(getActivity(), 2130903341, container, false);
        setErrorTextView((TextView) view.findViewById(2131625281));
        this.passwordText = (PasswordEditText) view.findViewById(2131624773);
        this.passwordText.getEditText().setOnEditorActionListener(this);
        this.userList = (UserAvatarViewPager) view.findViewById(2131625126);
        this.userListAdapter = new UserListPagerAdapter(getActivity(), this, this);
        this.userList.setAdapter(this.userListAdapter);
        this.userListAdapter.showEmptyAuthorizedUser();
        this.userList.setOffscreenPageLimit(10);
        this.loginButton = (Button) view.findViewById(2131625018);
        this.goToRegistration = (Button) view.findViewById(2131624806);
        this.loginProgress = (ProgressBar) view.findViewById(2131624680);
        this.loginView = (TextView) view.findViewById(2131625129);
        this.nameView = (TextView) view.findViewById(2131625128);
        this.viewUserAvatarViewPagerContainer = (UserAvatarViewPagerContainer) view.findViewById(2131625127);
        this.needHelpButton = view.findViewById(2131625131);
        initListeners();
        getActivity().getSupportLoaderManager().initLoader(1, null, this.userListLoader);
        if (savedInstanceState != null) {
            this.currentItem = savedInstanceState.getInt("loginFragment:currentItem", -1);
        }
        view.setOnTouchListener(new C10893());
        return view;
    }

    protected void showSpinner() {
        this.userList.setTouchEnabled(false);
        this.loginProgress.setVisibility(0);
        disableButtons();
    }

    private void disableButtons() {
        this.loginButton.setAlpha(0.4f);
        this.loginButton.setEnabled(false);
        this.goToRegistration.setEnabled(false);
        this.goToRegistration.setAlpha(0.4f);
    }

    protected void hideSpinner() {
        this.userList.setTouchEnabled(true);
        this.loginProgress.setVisibility(4);
        enableButtons();
    }

    private void enableButtons() {
        this.loginButton.setAlpha(1.0f);
        this.loginButton.setEnabled(true);
        this.goToRegistration.setEnabled(true);
        this.goToRegistration.setAlpha(1.0f);
    }

    private void checkCurrentUser(int position) {
        this.userList.setCurrentItem(position);
        if (position == this.userListAdapter.getCount() - 1) {
            clearInputFields();
            changeState(State.NEW_LOGIN_BUTTON);
        } else if (((AuthorizedUser) this.userListAdapter.getUserAtPosition(position)).token.equals("")) {
            changeState(State.OLD_LOGIN_PASSWORD);
        } else {
            changeState(State.OLD_LOGIN_PASSWORD);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.viewUserAvatarViewPagerContainer.setPagerWidth();
        this.userList.setCurrentItem(this.currentItem);
    }

    protected void initListeners() {
        super.initListeners();
        this.userList.setOnPageChangeListener(new C10904());
        this.userList.setOnHierarchyChangeListener(new C10915());
        this.loginButton.setOnClickListener(this);
        this.goToRegistration.setOnClickListener(new C10926());
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == this.CODE_REQUEST_PERMISSIONS) {
            this.communicationInterface.goToRegistration();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onResume() {
        super.onResume();
        this.viewUserAvatarViewPagerContainer.setPagerWidth();
    }

    private void hideKeyboard() {
        KeyBoardUtils.hideKeyBoard(getActivity());
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        onLoginClick();
        return true;
    }

    public void onClick(View v) {
        if (v.getId() == 2131625018) {
            onLoginClick();
        }
    }

    private void onLoginClick() {
        if (this.state == State.OLD_LOGIN_PASSWORD && this.passwordText.getText().length() == 0) {
            showError(LocalizationManager.getString(getActivity(), 2131165785));
        } else if (this.state == State.NEW_LOGIN_BUTTON) {
            OneLog.log(AuthorizationExperienceFactory.get(AuthorizationExperienceEvent.user_authorization, Settings.getAuthorizedUserCount(getActivity())));
            this.communicationInterface.goToNewLogin();
        } else {
            String password = this.passwordText.getText().trim();
            hideError();
            showSpinner();
            if (this.state == State.OLD_LOGIN_TOKEN || this.state == State.OLD_LOGIN_PASSWORD) {
                this.currentUser = (AuthorizedUser) this.userListAdapter.getUserAtPosition(this.userList.getCurrentItem());
                this.login = this.currentUser.user.login;
                if (this.currentUser.token.equals("") || this.state != State.OLD_LOGIN_TOKEN) {
                    performLoginByPassword(this.login, password);
                } else {
                    LoginScreenUtils.performLoginByToken(this.currentUser.token, this);
                }
            }
        }
    }
}
