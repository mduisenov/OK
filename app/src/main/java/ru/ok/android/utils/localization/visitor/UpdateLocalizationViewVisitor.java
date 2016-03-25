package ru.ok.android.utils.localization.visitor;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import ru.ok.android.utils.localization.LocalizationViewWalker;

public final class UpdateLocalizationViewVisitor implements ViewVisitor {
    public void visitView(View view, int resourceId) {
        if (view != null) {
            LocalizationViewWalker.walkThroughLayout(resourceId, view);
        }
    }

    public void visitFragment(Fragment fragment, int resourceId) {
        if (fragment != null) {
            LocalizationViewWalker.walkThroughLayout(resourceId, fragment);
        }
    }

    public void visitActivity(Activity activity, int resourceId) {
        if (activity != null && !activity.isFinishing()) {
            LocalizationViewWalker.walkThroughLayout(resourceId, activity);
        }
    }

    public void visitPreferencesActivity(PreferenceActivity activity, int resourceId) {
        if (activity != null) {
            LocalizationViewWalker.walkThroughPreferences(resourceId, activity);
        }
    }

    public void visitMenu(Context context, Menu menu, int resourceId) {
        if (menu != null) {
            LocalizationViewWalker.walkThroughMenu(context, resourceId, menu);
        }
    }

    public void visitMenu(Context context, ContextMenu menu, int resourceId) {
        if (menu != null) {
            LocalizationViewWalker.walkThroughMenu(context, resourceId, menu);
        }
    }
}
