package ru.ok.android.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.authorization.AuthorizationControl;
import ru.ok.android.utils.controls.authorization.OnLoginListener;
import ru.ok.android.utils.settings.Settings;

public final class LoginScreenUtils {
    private static final Map<String, Pair<String, String>> QUICK_LOGIN_NAMES;

    static {
        QUICK_LOGIN_NAMES = null;
    }

    public static boolean setupLoginAutocompletion(Context context, AutoCompleteTextView loginTextView, boolean displayOldLogin) {
        String oldLogin = null;
        if (displayOldLogin) {
            oldLogin = Settings.getUserName(context);
            loginTextView.setText(oldLogin);
        } else {
            loginTextView.setText("");
        }
        loginTextView.setThreshold(1);
        loginTextView.setDropDownHeight(-2);
        loginTextView.setAdapter(new ArrayAdapter(context, 17367050, getUserNameVariants(context)));
        if (TextUtils.isEmpty(oldLogin)) {
            return false;
        }
        return true;
    }

    public static List<String> getUserNameVariants(Context context) {
        List<String> result = new ArrayList();
        List<String> historyNames = Settings.getSuccessfulUsernames(context);
        List<String> emails = getAccountsEmails(context);
        historyNames.removeAll(emails);
        result.addAll(historyNames);
        result.addAll(emails);
        Collections.sort(result);
        return result;
    }

    public static List<String> getAccountsEmails(Context context) {
        Account[] accounts = ((AccountManager) context.getSystemService("account")).getAccounts();
        Set<String> names = new HashSet();
        for (Account acc : accounts) {
            boolean isEmail;
            if (TextUtils.isEmpty(acc.name) || !Patterns.EMAIL_ADDRESS.matcher(acc.name).matches()) {
                isEmail = false;
            } else {
                isEmail = true;
            }
            Logger.m173d("Account name: %s, type: %s, email: %s", acc.name, acc.type, Boolean.valueOf(isEmail));
            if (isEmail) {
                names.add(acc.name);
            }
        }
        return new ArrayList(names);
    }

    public static void addSuccessfulLoginName(Context context, String loginName) {
        Settings.addSuccessfulUsername(context, loginName);
    }

    public static void performLoginByToken(String token, OnLoginListener onLoginListener) {
        AuthorizationControl.getInstance().login(token, true, onLoginListener);
    }

    public static void performLoginByPassword(String login, String password, OnLoginListener onLoginListener) {
        AuthorizationControl.getInstance().login(login, password, onLoginListener);
    }

    public static void preprocessLoginPassword(EditText loginText, EditText passwordText) {
    }
}
