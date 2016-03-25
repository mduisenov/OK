package ru.ok.android.app.helper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.services.sync.SyncContactsAdapter;
import ru.ok.android.statistics.StatisticManager;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.ServiceStateHolder;
import ru.ok.model.UserInfo;

public final class AccountsHelper {

    /* renamed from: ru.ok.android.app.helper.AccountsHelper.1 */
    static class C02241 implements Runnable {
        final /* synthetic */ Account val$account;
        final /* synthetic */ Context val$context;

        C02241(Context context, Account account) {
            this.val$context = context;
            this.val$account = account;
        }

        public void run() {
            SyncContactsAdapter.removeAccountContacts(this.val$context, this.val$account);
        }
    }

    /* renamed from: ru.ok.android.app.helper.AccountsHelper.2 */
    static class C02252 implements Runnable {
        final /* synthetic */ Account val$account;
        final /* synthetic */ Context val$context;

        C02252(Context context, Account account) {
            this.val$context = context;
            this.val$account = account;
        }

        public void run() {
            SyncContactsAdapter.removeAccountContacts(this.val$context, this.val$account);
        }
    }

    private static Account getAccount(@NonNull Context context) {
        Account[] accounts = AccountManager.get(context).getAccountsByType(context.getString(2131165319));
        if (accounts != null && accounts.length > 0) {
            return accounts[0];
        }
        Logger.m172d("No accounts found");
        return null;
    }

    public static boolean hasAccountForCurrentUser(@NonNull Context context) {
        Account account = getAccount(context);
        if (account == null) {
            return false;
        }
        String uid = AccountManager.get(context).getUserData(account, "user_id");
        String currentUserId = OdnoklassnikiApplication.getCurrentUser().uid;
        if (uid == null || !TextUtils.equals(uid, currentUserId)) {
            return false;
        }
        return true;
    }

    @Nullable
    public static Account registerAccountForUser(@NonNull Context context, @NonNull UserInfo userInfo) {
        String userId = userInfo.uid;
        String userName = userInfo.getAnyName();
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(userName)) {
            Logger.m185w("User has empty id or name: %s", userInfo);
            return null;
        }
        Account account;
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(context.getString(2131165319));
        if (accounts != null) {
            Account[] arr$ = accounts;
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                account = arr$[i$];
                String uid = am.getUserData(account, "user_id");
                if (uid == null || !TextUtils.equals(uid, userId)) {
                    Logger.m185w("Remove account %s for another user uid: %s", account, uid);
                    removeAccount(am, account);
                    i$++;
                } else {
                    Logger.m185w("Account for user %s already exists", userInfo);
                    return account;
                }
            }
        }
        Logger.m173d("Create new account for user %s", userInfo);
        account = new Account(userName, context.getString(2131165319));
        Bundle userData = new Bundle();
        userData.putString("user_id", userId);
        userData.putString("user_pic_url", userInfo.getAnyPicUrl());
        try {
            am.addAccountExplicitly(account, null, userData);
            ContentResolver.setSyncAutomatically(account, "com.android.contacts", true);
            return account;
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to register account");
            StatisticManager.getInstance().reportError(AccountsHelper.class.getSimpleName(), "Failed to register account", e);
            return null;
        }
    }

    public static void deleteAccounts(@NonNull Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(context.getString(2131165319));
        if (accounts != null) {
            for (Account account : accounts) {
                Logger.m173d("Remove account: %s", account);
                removeAccount(am, account);
            }
        }
    }

    private static void removeAccount(AccountManager am, Account account) {
        am.removeAccount(account, null, null);
    }

    public static void applyNewSyncSettings(@NonNull Context context, boolean allowContactSyncing) {
        Account account = getAccount(context);
        if (account != null) {
            ContentResolver.setSyncAutomatically(account, "com.android.contacts", allowContactSyncing);
            if (allowContactSyncing) {
                Logger.m173d("Request sync for account %s", account);
                ContentResolver.requestSync(account, "com.android.contacts", new Bundle());
                return;
            }
            ThreadUtil.execute(new C02241(context, account));
        }
    }

    public static String extractUserIdFromContactUri(@NonNull Context context, @NonNull Intent intent) {
        String str = null;
        Uri uri = intent.getData();
        if (uri != null) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{"data1"}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        str = cursor.getString(0);
                    } else {
                        cursor.close();
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        return str;
    }

    public static void requestSyncIfNeeded(Context context) {
        Account account = getAccount(context);
        if (account != null) {
            long prevSync = Settings.getLongValue(context, "last-sync-request-time", 0);
            long time = System.currentTimeMillis();
            if (time - prevSync > 86400000) {
                boolean enabled = Settings.getBoolValueInvariable(context, context.getString(2131166672), false);
                ContentResolver.setSyncAutomatically(account, "com.android.contacts", enabled);
                if (enabled) {
                    ContentResolver.requestSync(account, "com.android.contacts", new Bundle());
                } else {
                    ThreadUtil.execute(new C02252(context, account));
                }
                Settings.storeLongValue(context, "last-sync-request-time", time);
            }
        }
    }

    public static void storeAuthenticationToken(Context context, @Nullable ServiceStateHolder stateHolder) {
        if (stateHolder == null) {
            Logger.m184w("Null state holder passed");
            return;
        }
        Account account = getAccount(context);
        if (account == null) {
            Logger.m184w("No account found");
            return;
        }
        AccountManager am = AccountManager.get(context);
        if (TextUtils.equals(am.getUserData(account, "user_id"), stateHolder.getUserId())) {
            am.setAuthToken(account, "authentication_token", stateHolder.getAuthenticationToken());
            return;
        }
        Logger.m185w("User id of state holder (%s) and account (%s) not equals", stateHolder.getUserId(), am.getUserData(account, "user_id"));
    }

    public static void updateUserInfo(Context context, UserInfo info) {
        Account account = getAccount(context);
        if (account == null) {
            Logger.m184w("No account found");
            return;
        }
        AccountManager am = AccountManager.get(context);
        if (TextUtils.equals(am.getUserData(account, "user_id"), info.uid)) {
            am.setUserData(account, "user_pic_url", info.getAnyPicUrl());
            return;
        }
        Logger.m185w("We has %s uid, %s passed", am.getUserData(account, "user_id"), info.uid);
    }
}
