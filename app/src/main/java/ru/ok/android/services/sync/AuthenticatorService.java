package ru.ok.android.services.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.widget.Toast;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.helper.AccountsHelper;
import ru.ok.android.ui.nativeRegistration.NativeLoginActivity;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.settings.Settings;

public final class AuthenticatorService extends Service {
    private final Handler handler;
    private Authenticator mAuthenticator;

    private final class Authenticator extends AbstractAccountAuthenticator {
        private final Context context;

        /* renamed from: ru.ok.android.services.sync.AuthenticatorService.Authenticator.1 */
        class C05171 implements Runnable {
            final /* synthetic */ String val$errorMessage;

            C05171(String str) {
                this.val$errorMessage = str;
            }

            public void run() {
                Toast.makeText(Authenticator.this.context, this.val$errorMessage, 0).show();
            }
        }

        public Authenticator(Context context) {
            super(context);
            this.context = context;
        }

        @NonNull
        public Bundle getAccountRemovalAllowed(AccountAuthenticatorResponse response, Account account) throws NetworkErrorException {
            Logger.m172d("");
            return super.getAccountRemovalAllowed(response, account);
        }

        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
            Logger.m172d("");
            return null;
        }

        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
            Logger.m172d("");
            if (AccountsHelper.hasAccountForCurrentUser(this.context)) {
                Logger.m184w("Account already existed");
                String errorMessage = LocalizationManager.getString(this.context, 2131165318);
                Logger.m173d("AccountsHelper %s", errorMessage);
                AuthenticatorService.this.handler.post(new C05171(errorMessage));
                Bundle bundle = new Bundle();
                bundle.putInt("errorCode", 6);
                bundle.putString("errorMessage", errorMessage);
                return bundle;
            }
            if (Settings.hasLoginData(this.context)) {
                Logger.m172d("We do not have account, but have session, add account...");
                Account account = AccountsHelper.registerAccountForUser(this.context, OdnoklassnikiApplication.getCurrentUser());
                if (account != null) {
                    bundle = new Bundle();
                    bundle.putString("authAccount", account.name);
                    bundle.putString("accountType", account.type);
                    return bundle;
                }
            }
            Logger.m172d("Start LoginActivity");
            Intent intent = new Intent(this.context, NativeLoginActivity.class);
            intent.putExtra("accountAuthenticatorResponse", response);
            bundle = new Bundle();
            bundle.putParcelable("intent", intent);
            return bundle;
        }

        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
            Logger.m172d("");
            return null;
        }

        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
            Logger.m172d("");
            return null;
        }

        public String getAuthTokenLabel(String authTokenType) {
            Logger.m172d("");
            return null;
        }

        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
            Logger.m172d("");
            return null;
        }

        public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
            Logger.m172d("");
            return null;
        }
    }

    public AuthenticatorService() {
        this.handler = new Handler();
    }

    public void onCreate() {
        this.mAuthenticator = new Authenticator(this);
    }

    public IBinder onBind(Intent intent) {
        return this.mAuthenticator.getIBinder();
    }
}
