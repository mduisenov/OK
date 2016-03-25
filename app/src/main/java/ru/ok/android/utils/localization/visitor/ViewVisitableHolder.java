package ru.ok.android.utils.localization.visitor;

import android.view.View;

public final class ViewVisitableHolder extends BaseVisitableHolder<View> {
    public ViewVisitableHolder(View view) {
        super(view);
    }

    public void visit(ViewVisitor visitor, int viewId) {
        visitor.visitView((View) getView(), viewId);
    }
}
