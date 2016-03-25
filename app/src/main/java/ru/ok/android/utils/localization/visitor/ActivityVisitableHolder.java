package ru.ok.android.utils.localization.visitor;

import android.app.Activity;

public final class ActivityVisitableHolder extends BaseVisitableHolder<Activity> {
    public ActivityVisitableHolder(Activity view) {
        super(view);
    }

    public void visit(ViewVisitor visitor, int resourceId) {
        visitor.visitActivity((Activity) getView(), resourceId);
    }
}
