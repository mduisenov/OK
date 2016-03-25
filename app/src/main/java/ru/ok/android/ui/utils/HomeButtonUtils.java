package ru.ok.android.ui.utils;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class HomeButtonUtils {
    public static void showHomeButton(Activity activity) {
        if (activity instanceof AppCompatActivity) {
            setHomeButtonCompatVisibility(((AppCompatActivity) activity).getSupportActionBar(), true);
        }
    }

    public static void hideHomeButton(Activity activity) {
        if (activity instanceof AppCompatActivity) {
            setHomeButtonCompatVisibility(((AppCompatActivity) activity).getSupportActionBar(), false);
        }
    }

    protected static void setHomeButtonCompatVisibility(ActionBar actionBar, boolean visible) {
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(visible);
            actionBar.setDisplayHomeAsUpEnabled(visible);
        }
    }
}
