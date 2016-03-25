package ru.ok.android.utils.localization.visitor;

import android.preference.PreferenceActivity;

public final class PreferenceActivityVisitableHolder extends BaseVisitableHolder<PreferenceActivity> {
    public PreferenceActivityVisitableHolder(PreferenceActivity view) {
        super(view);
    }

    public void visit(ViewVisitor visitor, int resourceId) {
        visitor.visitPreferencesActivity((PreferenceActivity) getView(), resourceId);
    }
}
