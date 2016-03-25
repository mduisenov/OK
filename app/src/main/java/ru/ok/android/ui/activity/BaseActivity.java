package ru.ok.android.ui.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager.BadTokenException;
import android.widget.Toast;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.fragments.web.hooks.WebLinksProcessor;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.custom.animations.OnlineAnimationManager;
import ru.ok.android.ui.dialogs.ErrorDialog;
import ru.ok.android.ui.dialogs.ErrorDialog.OnClickButtonListener;
import ru.ok.android.ui.dialogs.PasswordDialog.OnLogoutAllClickListener;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.controls.authorization.AuthorizationControl;
import ru.ok.android.utils.controls.authorization.OnLoginListener;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.android.utils.controls.events.EventsManager.OnEvents;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.localization.base.LocalizedActivity;
import ru.ok.android.utils.settings.Settings;
import ru.ok.model.events.OdnkEvent;

public class BaseActivity extends LocalizedActivity implements OnBackStackChangedListener, OnLogoutAllClickListener, OnLoginListener, OnEvents {
    protected static String mErrorMessage;
    private int backStackCount;
    private GoogleApiClient client;
    BroadcastReceiver errorUserBroadcastReceiver;
    private boolean isShowDialog;
    private KillReceiver mKillReceiver;
    private WebLinksProcessor webLinksProcessor;

    /* renamed from: ru.ok.android.ui.activity.BaseActivity.1 */
    class C05331 implements OnClickButtonListener {
        C05331() {
        }

        public void OnClick(boolean dismissDialog) {
            BaseActivity.mErrorMessage = "";
            BaseActivity.this.finish();
        }
    }

    /* renamed from: ru.ok.android.ui.activity.BaseActivity.2 */
    class C05342 implements OnClickButtonListener {
        C05342() {
        }

        public void OnClick(boolean dismissDialog) {
            Settings.clearLoginData(BaseActivity.this);
            BaseActivity.mErrorMessage = "";
            BaseActivity.this.restart();
        }
    }

    /* renamed from: ru.ok.android.ui.activity.BaseActivity.3 */
    class C05353 implements OnClickButtonListener {
        C05353() {
        }

        public void OnClick(boolean dismissDialog) {
            Settings.clearLoginData(BaseActivity.this);
            BaseActivity.mErrorMessage = "";
        }
    }

    /* renamed from: ru.ok.android.ui.activity.BaseActivity.4 */
    class C05364 implements OnCancelListener {
        C05364() {
        }

        public void onCancel(DialogInterface dialog) {
            if (!BaseActivity.this.isFinishing()) {
                AuthorizationControl.getInstance().logout(BaseActivity.this);
            }
            BaseActivity.this.isShowDialog = false;
        }
    }

    /* renamed from: ru.ok.android.ui.activity.BaseActivity.5 */
    class C05375 implements OnClickListener {
        C05375() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (!BaseActivity.this.isFinishing()) {
                AuthorizationControl.getInstance().logout(BaseActivity.this);
            }
            BaseActivity.this.isShowDialog = false;
        }
    }

    /* renamed from: ru.ok.android.ui.activity.BaseActivity.6 */
    static /* synthetic */ class C05386 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$ui$activity$BaseActivity$ErrorType;

        static {
            $SwitchMap$ru$ok$android$ui$activity$BaseActivity$ErrorType = new int[ErrorType.values().length];
            try {
                $SwitchMap$ru$ok$android$ui$activity$BaseActivity$ErrorType[ErrorType.BLOCKED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$activity$BaseActivity$ErrorType[ErrorType.INVALID_CREDENTIALS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$ui$activity$BaseActivity$ErrorType[ErrorType.LOGOUT_ALL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public enum ErrorType {
        BLOCKED,
        INVALID_CREDENTIALS,
        LOGOUT_ALL
    }

    public class ErrorUserReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (!BaseActivity.this.isFinishing() && bundle != null) {
                switch (C05386.$SwitchMap$ru$ok$android$ui$activity$BaseActivity$ErrorType[((ErrorType) bundle.getSerializable("type_extras")).ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        BaseActivity.this.onUserIsBlock();
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                        BaseActivity.this.onCredentialsUserError();
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                        BaseActivity.this.onLogoutAll();
                    default:
                }
            }
        }
    }

    public final class KillReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            BaseActivity.this.finish();
        }
    }

    public BaseActivity() {
        this.backStackCount = 0;
        this.errorUserBroadcastReceiver = new ErrorUserReceiver();
        this.isShowDialog = false;
    }

    static {
        mErrorMessage = "";
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        GlobalBus.register(this);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        this.backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        this.mKillReceiver = new KillReceiver();
        registerReceiver(this.mKillReceiver, IntentFilter.create("kill", "ru.ok.android/logout"));
        if (isIndexingActivity()) {
            this.client = new Builder(this).addApi(AppIndex.APP_INDEX_API).build();
        }
    }

    protected boolean isIndexingActivity() {
        return false;
    }

    protected void onStart() {
        super.onStart();
        if (this.client != null) {
            this.client.connect();
            onAppIndexingStart(this.client);
        }
    }

    protected void onStop() {
        super.onStop();
        if (this.client != null) {
            onAppIndexingStop(this.client);
            this.client.disconnect();
        }
    }

    protected void onAppIndexingStart(GoogleApiClient client) {
    }

    protected void onAppIndexingStop(GoogleApiClient client) {
    }

    protected void onResume() {
        super.onResume();
        EventsManager.getInstance().subscribe(this);
        OnlineAnimationManager.getInstance().onStartAnimation();
        LocalBroadcastManager.getInstance(this).registerReceiver(this.errorUserBroadcastReceiver, new IntentFilter("error_user_action"));
    }

    public void onLogoutAllClick(String password) {
        Bundle bundle = new Bundle();
        bundle.putString("password", password);
        GlobalBus.send(2131623985, new BusEvent(bundle));
    }

    @Subscribe(on = 2131623946, to = 2131624237)
    public void onLogoutAllResp(BusEvent event) {
        if (event.resultCode == -1) {
            String token = event.bundleOutput.getString("token");
            if (!TextUtils.isEmpty(token)) {
                Settings.storeToken(this, token);
                JsonSessionTransportProvider.getInstance().getStateHolder().setAuthenticationToken(token);
                Toast.makeText(this, 2131166057, 0).show();
                return;
            }
            return;
        }
        ru.ok.android.services.processors.base.CommandProcessor.ErrorType errorType = ru.ok.android.services.processors.base.CommandProcessor.ErrorType.from(event.bundleOutput);
        if (errorType != ru.ok.android.services.processors.base.CommandProcessor.ErrorType.GENERAL) {
            Toast.makeText(this, errorType.getDefaultErrorMessage(), 0).show();
        } else {
            Toast.makeText(this, 2131166056, 0).show();
        }
    }

    public void onBackStackChanged() {
        if (this.backStackCount >= getSupportFragmentManager().getBackStackEntryCount()) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (int i = fragments.size() - 1; i >= 0; i--) {
                    Fragment fragment = (Fragment) fragments.get(i);
                    if ((fragment instanceof BaseFragment) && fragment.isAdded() && !fragment.isHidden()) {
                        ((BaseFragment) fragment).updateActionBarState();
                        return;
                    }
                }
            }
        }
    }

    protected void onPause() {
        EventsManager.getInstance().unSubscribe(this);
        OnlineAnimationManager.getInstance().onStopAnimation();
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.errorUserBroadcastReceiver);
    }

    protected void onDestroy() {
        GlobalBus.unregister(this);
        if (this.mKillReceiver != null) {
            unregisterReceiver(this.mKillReceiver);
        }
        super.onDestroy();
    }

    @Subscribe(on = 2131623946, to = 2131624233)
    public final void onConnectionAvailable(Object event) {
        onInternetAvailable();
    }

    public void onBackPressed() {
        if (getIntent().getBooleanExtra("key_activity_from_menu", false)) {
            startActivity(NavigationHelper.createIntentForBackFromSlidingMenuOpenActivity(this));
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe(on = 2131623946, to = 2131624234)
    public final void onNewEvents(BusEvent busEvent) {
        onGetNewEvents(EventsManager.getEventsFromBusEvent(busEvent));
    }

    public void onGetNewEvents(ArrayList<OdnkEvent> returnList) {
        super.onGetNewEvents(returnList);
    }

    protected boolean startLoginIfNeeded() {
        if (Settings.hasLoginData(this)) {
            return false;
        }
        setProgressBarIndeterminateVisibility(true);
        if (Settings.getAuthorizedUserCount(this) == 0 || !AuthorizationPreferences.getMultipleLoginEnabled()) {
            NavigationHelper.firstEnter(this);
            return true;
        }
        NavigationHelper.login(this);
        return true;
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public boolean onOptionsItemSelectedExceptionHandle(MenuItem item) {
        try {
            return super.onOptionsItemSelected(item);
        } catch (Throwable e) {
            Logger.m178e(e);
            finish();
            return false;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                dispatchKeyEvent(new KeyEvent(0, 4));
                dispatchKeyEvent(new KeyEvent(1, 4));
                return true;
            case C0158R.id.settings /*2131625112*/:
                NavigationHelper.showSettings(this, false);
                return true;
            case 2131625471:
                NavigationHelper.showNotificationsPage(this, false);
                return true;
            default:
                return onOptionsItemSelectedExceptionHandle(item);
        }
    }

    protected void notifyTransportError(boolean noValidDate) {
        if (noValidDate) {
            try {
                showTransportErrorDialog(LocalizationManager.getString((Context) this, 2131166620));
                return;
            } catch (BadTokenException e) {
                finish();
                return;
            }
        }
        showTransportErrorDialog(LocalizationManager.getString((Context) this, 2131166735));
    }

    protected Dialog onCreateDialog(int id) {
        OnClickButtonListener clickListener = null;
        switch (id) {
            case LocationStatusCodes.GEOFENCE_NOT_AVAILABLE /*1000*/:
                clickListener = getClickErrorDialogListener();
                break;
            case LocationStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES /*1001*/:
                clickListener = new C05331();
                break;
            case LocationStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS /*1002*/:
                clickListener = getClickErrorMessageDialogListener();
                break;
            case 1003:
                return getDialogForTransportError();
        }
        if (clickListener == null) {
            return null;
        }
        ErrorDialog errorDialog = new ErrorDialog(this, 2131165791, 2131165595);
        errorDialog.setOnClickButtonListener(clickListener);
        return errorDialog.getDialog();
    }

    public OnClickButtonListener getClickErrorDialogListener() {
        return new C05342();
    }

    public OnClickButtonListener getClickErrorMessageDialogListener() {
        return new C05353();
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case LocationStatusCodes.GEOFENCE_NOT_AVAILABLE /*1000*/:
                ((MaterialDialog) dialog).setContent(mErrorMessage);
            case LocationStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES /*1001*/:
                ((MaterialDialog) dialog).setContent(mErrorMessage);
            case LocationStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS /*1002*/:
                ((MaterialDialog) dialog).setContent(mErrorMessage);
            case 1003:
                ((MaterialDialog) dialog).setContent(mErrorMessage);
            default:
        }
    }

    protected Dialog getDialogForTransportError() {
        return new ErrorDialog(this, 2131165791, 2131165595).getDialog();
    }

    protected void showLoginErrorDialog(String msg) {
        mErrorMessage = msg;
        showDialog(LocationStatusCodes.GEOFENCE_NOT_AVAILABLE);
    }

    public void showErrorDialog(String errorMessage) {
        mErrorMessage = errorMessage;
        try {
            showDialog(LocationStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES);
        } catch (Exception e) {
            Logger.m176e("error bad token");
        }
    }

    protected void showMessageDialog(String msg) {
        mErrorMessage = msg;
        if (!isFinishing()) {
            showDialog(LocationStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS);
        }
    }

    protected void showTransportErrorDialog(String msg) {
        mErrorMessage = msg;
        showDialog(1003);
    }

    public void restart() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void onLoginSuccessful(String url, String verificationUrl) {
        setProgressBarIndeterminateVisibility(false);
    }

    public void onLoginError(String message, int type, int errorCode) {
        boolean z = false;
        setProgressBarIndeterminateVisibility(false);
        if (type == 10) {
            if (errorCode == 401) {
                OdnoklassnikiApplication.setCurrentUser(null);
                try {
                    if (message.equals("AUTH_LOGIN : BLOCKED")) {
                        showMessageDialog(getStringLocalized(2131166784));
                        onUserIsBlock();
                        return;
                    } else if (message.equals("AUTH_LOGIN : LOGOUT_ALL")) {
                        showMessageDialog(getStringLocalized(2131165792));
                        onUserIsBlock();
                        return;
                    } else {
                        showLoginErrorDialog(getStringLocalized(2131166052));
                        return;
                    }
                } catch (BadTokenException e) {
                    return;
                }
            }
            showErrorDialog(message);
        } else if (type == 9) {
            if (errorCode == 555) {
                z = true;
            }
            notifyTransportError(z);
        } else {
            showErrorDialog(message);
        }
    }

    protected void onUserIsBlock() {
        Logger.m172d("On user block");
        logout(2131166784);
    }

    protected void onInternetAvailable() {
        Logger.m172d("Internet available");
    }

    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).onContextMenuClosed();
                }
            }
        }
    }

    public final WebLinksProcessor getWebLinksProcessor() {
        if (this.webLinksProcessor == null) {
            this.webLinksProcessor = new WebLinksProcessor(this, false);
        }
        return this.webLinksProcessor;
    }

    protected void onCredentialsUserError() {
        logout(2131166785);
    }

    protected void onLogoutAll() {
        logout(2131165792);
    }

    private void logout(int resId) {
        onShowErrorUserDialog(resId);
    }

    private void onShowErrorUserDialog(int resId) {
        if (!this.isShowDialog) {
            this.isShowDialog = true;
            new AlertDialogWrapper.Builder(this).setMessage(LocalizationManager.getString(getContext(), resId)).setPositiveButton(17039370, new C05375()).setOnCancelListener(new C05364()).show();
        }
    }

    public void setToolbarTitle(CharSequence title) {
        super.setToolbarTitle(title);
    }
}
