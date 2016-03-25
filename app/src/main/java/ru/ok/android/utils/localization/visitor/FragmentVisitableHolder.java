package ru.ok.android.utils.localization.visitor;

import android.support.v4.app.Fragment;

public final class FragmentVisitableHolder extends BaseVisitableHolder<Fragment> {
    public FragmentVisitableHolder(Fragment view) {
        super(view);
    }

    public void visit(ViewVisitor visitor, int resourceId) {
        visitor.visitFragment((Fragment) getView(), resourceId);
    }
}
