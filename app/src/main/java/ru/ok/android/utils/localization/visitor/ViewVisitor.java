package ru.ok.android.utils.localization.visitor;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;

public interface ViewVisitor {
    void visitActivity(Activity activity, int i);

    void visitFragment(Fragment fragment, int i);

    void visitMenu(Context context, ContextMenu contextMenu, int i);

    void visitMenu(Context context, Menu menu, int i);

    void visitPreferencesActivity(PreferenceActivity preferenceActivity, int i);

    void visitView(View view, int i);
}
