package ru.ok.android.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.WeakHashMap;
import ru.ok.android.app.OdnoklassnikiApplication;

public class KeyBoardUtils {
    private static final WeakHashMap<OnKeyboardForceHiddenListener, Integer> onKeyboardHiddenListeners;

    public interface OnKeyboardForceHiddenListener {
        void onKeyboardForceHidden();
    }

    public static void showKeyBoard(EditText view) {
        ((InputMethodManager) OdnoklassnikiApplication.getContext().getSystemService("input_method")).showSoftInput(view, 2);
    }

    public static void showKeyBoard(Context context, EditText text) {
        text.requestFocus();
        ((InputMethodManager) context.getSystemService("input_method")).toggleSoftInput(2, 0);
    }

    public static void showKeyBoard(IBinder binder) {
        ((InputMethodManager) OdnoklassnikiApplication.getContext().getSystemService("input_method")).toggleSoftInputFromWindow(binder, 1, 0);
    }

    public static void hideKeyBoard(Context context, IBinder binder) {
        if (context != null) {
            ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(binder, 0);
            notifyOnKeyboardHidden();
        }
    }

    public static void hideKeyBoard(Activity activity) {
        if (activity != null) {
            hideKeyBoard(activity, activity.getWindow().getDecorView().getWindowToken());
        }
    }

    public static void addOnKeyboardHiddenListener(OnKeyboardForceHiddenListener listener) {
        synchronized (onKeyboardHiddenListeners) {
            onKeyboardHiddenListeners.put(listener, Integer.valueOf(1));
        }
    }

    public static void removeOnKeyboardHiddenListner(OnKeyboardForceHiddenListener listener) {
        synchronized (onKeyboardHiddenListeners) {
            onKeyboardHiddenListeners.remove(listener);
        }
    }

    private static void notifyOnKeyboardHidden() {
        synchronized (onKeyboardHiddenListeners) {
            Iterator i$ = new ArrayList(onKeyboardHiddenListeners.keySet()).iterator();
            while (i$.hasNext()) {
                OnKeyboardForceHiddenListener l = (OnKeyboardForceHiddenListener) i$.next();
                if (l != null) {
                    l.onKeyboardForceHidden();
                }
            }
        }
    }

    static {
        onKeyboardHiddenListeners = new WeakHashMap();
    }
}
