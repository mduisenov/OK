package ru.ok.android.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import com.google.android.gms.auth.GoogleAuthUtil;

public class AccountEmailFinder {
    public static String getGoogleEmail(Context context) {
        Account[] accounts = AccountManager.get(context).getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        if (accounts.length > 0) {
            return accounts[0].name;
        }
        return null;
    }
}
